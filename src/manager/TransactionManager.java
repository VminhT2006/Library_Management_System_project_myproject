package manager;

import interfaces.IFileService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import model.Transaction;
import util.FileUtil;
import util.Validator;

/**
 * Lớp quản lý Giao dịch mượn / trả tài liệu (SRS Mục 5.1 - FR-06, FR-09, FR-10, FR-13).
 * Bao gồm: CRUD giao dịch, lọc theo trạng thái, thống kê bằng Java Stream API,
 * và quản lý danh sách "giữ sách" cho độc giả đứng đầu hàng đợi đặt trước (UC-05, UC-07).
 */
public class TransactionManager implements IFileService {
    private static final String DATA_FILE = "data/transactions-v2.csv";
    private static final String HOLD_FILE = "data/holds-v2.csv";
    private static final String HEADER =
            "transactionID,memberID,documentID,borrowDate,dueDate,returnDate,status,fineAmount,isRenewed";

    public static final String FILTER_ALL = "Tất cả";
    public static final String FILTER_BORROWED = "Đang mượn";
    public static final String FILTER_RETURNED = "Đã trả";
    public static final String FILTER_OVERDUE = "Quá hạn";

    private final List<Transaction> transactions;
    /** Bản đồ giữ sách: Mã tài liệu -> Mã độc giả được ưu tiên giữ sách (sau khi poll hàng đợi). */
    private final Map<String, String> holds;

    public TransactionManager() {
        this.transactions = new ArrayList<>();
        this.holds = new LinkedHashMap<>();
    }

    // =========================================================================
    // 1. CRUD GIAO DỊCH
    // =========================================================================

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public Transaction findById(String transactionId) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            return null;
        }
        String target = transactionId.trim();
        return transactions.stream()
                .filter(tx -> tx.getTransactionID().equalsIgnoreCase(target))
                .findFirst()
                .orElse(null);
    }

    public void addTransaction(Transaction transaction) {
        Validator.requireNonNull(transaction, "Giao dịch");
        if (findById(transaction.getTransactionID()) != null) {
            throw new IllegalArgumentException("Mã giao dịch đã tồn tại: " + transaction.getTransactionID());
        }
        transactions.add(transaction);
    }

    /** Sinh mã giao dịch mới duy nhất dạng TX0001, TX0002, ... */
    public String generateTransactionId() {
        int counter = transactions.size() + 1;
        while (true) {
            String id = String.format("TX%04d", counter++);
            if (findById(id) == null) {
                return id;
            }
        }
    }

    // =========================================================================
    // 2. TRUY VẤN NGHIỆP VỤ (UC-04, UC-06, FR-09)
    // =========================================================================

    /** Đếm số tài liệu độc giả đang mượn (chưa trả) để kiểm tra hạn mức 3 cuốn. */
    public long countActiveBorrows(String memberId) {
        return transactions.stream()
                .filter(tx -> tx.getMemberID().equalsIgnoreCase(memberId))
                .filter(tx -> "BORROWED".equalsIgnoreCase(tx.getStatus()))
                .count();
    }

    /** Kiểm tra độc giả có đang mượn (chưa trả) đúng tài liệu này hay không. */
    public boolean isBorrowingDocument(String memberId, String documentId) {
        return transactions.stream()
                .anyMatch(tx -> tx.getMemberID().equalsIgnoreCase(memberId)
                        && tx.getDocumentID().equalsIgnoreCase(documentId)
                        && "BORROWED".equalsIgnoreCase(tx.getStatus()));
    }

    /** Toàn bộ lịch sử giao dịch của một độc giả. */
    public List<Transaction> getByMember(String memberId) {
        return transactions.stream()
                .filter(tx -> tx.getMemberID().equalsIgnoreCase(memberId))
                .collect(Collectors.toList());
    }

    /**
     * Lọc giao dịch theo trạng thái hiển thị trên ComboBox (FR-09).
     * memberId = null nghĩa là lọc trên toàn bộ hệ thống (dành cho Admin).
     */
    public List<Transaction> filterByStatus(String uiStatus, String memberId) {
        List<Transaction> source = (memberId == null) ? transactions : getByMember(memberId);
        if (uiStatus == null || FILTER_ALL.equalsIgnoreCase(uiStatus)) {
            return new ArrayList<>(source);
        }
        return source.stream()
                .filter(tx -> uiStatus.equalsIgnoreCase(getDisplayStatus(tx)))
                .collect(Collectors.toList());
    }

    /** Chuyển trạng thái nội bộ (BORROWED/RETURNED) sang nhãn tiếng Việt hiển thị trên bảng. */
    public static String getDisplayStatus(Transaction tx) {
        if ("RETURNED".equalsIgnoreCase(tx.getStatus())) {
            return FILTER_RETURNED;
        }
        return tx.isOverdue() ? FILTER_OVERDUE : FILTER_BORROWED;
    }

    // =========================================================================
    // 3. GIỮ SÁCH CHO NGƯỜI ĐỨNG ĐẦU HÀNG ĐỢI (UC-05 bước 4, UC-07)
    // =========================================================================

    public void setHold(String documentId, String memberId) {
        holds.put(documentId.toUpperCase(), memberId.toUpperCase());
    }

    public String getHoldFor(String documentId) {
        return documentId == null ? null : holds.get(documentId.toUpperCase());
    }

    public boolean isHeldFor(String documentId, String memberId) {
        String holder = getHoldFor(documentId);
        return holder != null && holder.equalsIgnoreCase(memberId);
    }

    public void clearHold(String documentId) {
        if (documentId != null) {
            holds.remove(documentId.toUpperCase());
        }
    }

    // =========================================================================
    // 4. THỐNG KÊ BẰNG JAVA STREAM API (FR-13, UC-08)
    // =========================================================================

    /** Tổng số lượt mượn đang hoạt động (status = BORROWED). */
    public long countBorrowed() {
        return transactions.stream()
                .filter(tx -> "BORROWED".equalsIgnoreCase(tx.getStatus()))
                .count();
    }

    /** Tổng số lượt mượn đang bị quá hạn. */
    public long countOverdue() {
        return transactions.stream()
                .filter(Transaction::isOverdue)
                .count();
    }

    /** Tổng doanh thu tiền phạt đã ghi nhận trên các giao dịch đã trả. */
    public double totalCollectedFine() {
        return transactions.stream()
                .filter(tx -> "RETURNED".equalsIgnoreCase(tx.getStatus()))
                .mapToDouble(Transaction::getFineAmount)
                .sum();
    }

    /** Top N tài liệu được mượn nhiều nhất: Mã tài liệu -> Số lượt mượn (giảm dần). */
    public Map<String, Long> topBorrowedDocuments(int limit) {
        return transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getDocumentID, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    /** Danh sách mã độc giả đang có ít nhất một giao dịch quá hạn. */
    public Set<String> overdueMemberIds() {
        return transactions.stream()
                .filter(Transaction::isOverdue)
                .map(Transaction::getMemberID)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /** Đếm số giao dịch quá hạn của một độc giả cụ thể. */
    public long countOverdueOfMember(String memberId) {
        return transactions.stream()
                .filter(tx -> tx.getMemberID().equalsIgnoreCase(memberId))
                .filter(Transaction::isOverdue)
                .count();
    }

    // =========================================================================
    // 5. ĐỒNG BỘ DỮ LIỆU FILE CSV (FR-07)
    // =========================================================================

    @Override
    public void loadData() {
        transactions.clear();
        for (String line : FileUtil.readLines(FileUtil.resolveForRead(DATA_FILE))) {
            if (line.equals(HEADER)) {
                continue; // bỏ qua dòng tiêu đề
            }
            List<String> p = FileUtil.parseCsvLine(line);
            if (p.size() < 9) {
                continue;
            }
            try {
                // Chuẩn hóa: trạng thái "OVERDUE" trong file là trạng thái dẫn xuất;
                // trong mô hình, quá hạn được tính động từ dueDate nên lưu là BORROWED.
                String rawStatus = p.get(6).trim().toUpperCase();
                String status = "OVERDUE".equals(rawStatus) ? "BORROWED" : rawStatus;
                Transaction tx = new Transaction(
                        p.get(0).trim(), p.get(1).trim(), p.get(2).trim(),
                        LocalDate.parse(p.get(3).trim()),
                        LocalDate.parse(p.get(4).trim()),
                        p.get(5).trim().isEmpty() ? null : LocalDate.parse(p.get(5).trim()),
                        status,
                        Double.parseDouble(p.get(7).trim()),
                        Boolean.parseBoolean(p.get(8).trim()));
                transactions.add(tx);
            } catch (RuntimeException e) {
                System.err.println("Bỏ qua dòng giao dịch lỗi: " + line + " (" + e.getMessage() + ")");
            }
        }

        holds.clear();
        for (String line : FileUtil.readLines(FileUtil.resolveForRead(HOLD_FILE))) {
            List<String> p = FileUtil.parseCsvLine(line);
            if (p.size() >= 2 && !p.get(0).trim().isEmpty() && !p.get(1).trim().isEmpty()) {
                holds.put(p.get(0).trim().toUpperCase(), p.get(1).trim().toUpperCase());
            }
        }
    }

    @Override
    public boolean saveData() {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Transaction tx : transactions) {
            // Ghi lại trạng thái hiển thị: nếu đang mượn mà quá hạn thì ghi "OVERDUE".
            String status = tx.getStatus();
            if ("BORROWED".equalsIgnoreCase(status) && tx.isOverdue()) {
                status = "OVERDUE";
            }
            lines.add(FileUtil.toCsvLine(
                    tx.getTransactionID(), tx.getMemberID(), tx.getDocumentID(),
                    tx.getBorrowDate().toString(),
                    tx.getDueDate().toString(),
                    tx.getReturnDate() == null ? "" : tx.getReturnDate().toString(),
                    status,
                    String.valueOf(tx.getFineAmount()),
                    String.valueOf(tx.isRenewed())));
        }
        boolean okTransactions = FileUtil.writeLines(FileUtil.resolveForWrite(DATA_FILE), lines);

        List<String> holdLines = new ArrayList<>();
        for (Map.Entry<String, String> entry : holds.entrySet()) {
            holdLines.add(FileUtil.toCsvLine(entry.getKey(), entry.getValue()));
        }
        boolean okHolds = FileUtil.writeLines(FileUtil.resolveForWrite(HOLD_FILE), holdLines);
        return okTransactions && okHolds;
    }
}
