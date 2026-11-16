package controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import manager.DocumentManager;
import manager.TransactionManager;
import manager.UserManager;
import model.Book;
import model.Document;
import model.Magazine;
import model.Member;
import model.Thesis;
import model.Transaction;
import model.User;
import util.Validator;

/**
 * Controller xử lý Mượn - Trả - Gia hạn - Đặt trước tài liệu
 * (FR-05, FR-06, FR-10, FR-12 - UC-04, UC-05, UC-06, UC-07, Sơ đồ tuần tự SRS Mục 5.4).
 */
public class TransactionController {
    private final DocumentManager documentManager;
    private final UserManager userManager;
    private final TransactionManager transactionManager;

    public TransactionController(DocumentManager documentManager,
                                 UserManager userManager,
                                 TransactionManager transactionManager) {
        this.documentManager = Validator.requireNonNull(documentManager, "DocumentManager");
        this.userManager = Validator.requireNonNull(userManager, "UserManager");
        this.transactionManager = Validator.requireNonNull(transactionManager, "TransactionManager");
    }

    /** Ngoại lệ báo hiệu sách đã hết hàng để View hỏi độc giả tham gia hàng đợi (UC-04 -> UC-07). */
    public static class OutOfStockException extends RuntimeException {
        public OutOfStockException(String message) {
            super(message);
        }
    }

    /** Kết quả trả tài liệu để View hiển thị popup chi tiết (UC-05). */
    public static class ReturnResult {
        public final long overdueDays;
        public final double fine;
        public final boolean paidFullyFromWallet;
        public final String heldForMemberId;

        public ReturnResult(long overdueDays, double fine, boolean paidFullyFromWallet, String heldForMemberId) {
            this.overdueDays = overdueDays;
            this.fine = fine;
            this.paidFullyFromWallet = paidFullyFromWallet;
            this.heldForMemberId = heldForMemberId;
        }
    }

    // =========================================================================
    // 1. MƯỢN TÀI LIỆU MỘT CHẠM (FR-05, UC-04)
    // =========================================================================

    public Transaction borrow(Member member, String documentId) {
        Validator.requireNonNull(member, "Độc giả");

        if (member.isBanned()) {
            throw new IllegalStateException("Tài khoản của bạn đang bị khóa do nợ phạt quá hạn! "
                    + "Vui lòng nạp tiền vào ví để mở khóa trước khi mượn sách.");
        }

        Document doc = documentManager.findById(documentId);
        if (doc == null) {
            throw new IllegalArgumentException("Không tìm thấy tài liệu với mã: " + documentId);
        }

        if (transactionManager.countActiveBorrows(member.getUserID()) >= member.getMaxBorrowBook()) {
            throw new IllegalStateException("Bạn đã mượn tối đa " + member.getMaxBorrowBook()
                    + " tài liệu! Vui lòng trả bớt trước khi mượn thêm.");
        }
        if (transactionManager.isBorrowingDocument(member.getUserID(), doc.getId())) {
            throw new IllegalStateException("Bạn đang mượn tài liệu này rồi, không thể mượn thêm bản sao!");
        }

        // Kiểm tra Đa hình canBorrow() (SRS Mục 5.2)
        if (doc instanceof Thesis) {
            throw new IllegalStateException("Luận văn chỉ được phép đọc tại chỗ, không cho mượn về!");
        }

        boolean consumeHold = false;
        if (doc instanceof Book) {
            Book book = (Book) doc;
            if (transactionManager.isHeldFor(book.getId(), member.getUserID())) {
                consumeHold = true; // Sách đang được giữ riêng cho chính độc giả này
            } else if (!book.canBorrow()) {
                throw new OutOfStockException("Cuốn sách này hiện đã hết bản sao. "
                        + "Bạn có muốn tham gia hàng đợi đặt trước không?");
            }
        } else if (doc instanceof Magazine) {
            if (!doc.canBorrow()) {
                throw new IllegalStateException("Tạp chí này hiện đã hết bản sao, vui lòng quay lại sau!");
            }
        }
        // EBook: canBorrow() luôn true, không giới hạn bản sao vật lý

        Transaction tx = new Transaction(
                transactionManager.generateTransactionId(), member.getUserID(), doc.getId());
        transactionManager.addTransaction(tx);

        if (consumeHold) {
            transactionManager.clearHold(doc.getId());
        } else if (doc instanceof Book) {
            Book book = (Book) doc;
            book.setQuantity(book.getQuantity() - 1);
        } else if (doc instanceof Magazine) {
            Magazine magazine = (Magazine) doc;
            magazine.setQuantity(magazine.getQuantity() - 1);
        }

        documentManager.saveData();
        transactionManager.saveData();
        return tx;
    }

    // =========================================================================
    // 2. HÀNG ĐỢI ĐẶT TRƯỚC - FIFO (FR-12, UC-07)
    // =========================================================================

    /** Thêm độc giả vào hàng đợi đặt trước, trả về vị trí trong hàng đợi (bắt đầu từ 1). */
    public int joinWaitingList(Member member, String documentId) {
        Validator.requireNonNull(member, "Độc giả");
        Document doc = documentManager.findById(documentId);
        if (!(doc instanceof Book)) {
            throw new IllegalArgumentException("Chỉ hỗ trợ đặt trước đối với tài liệu loại Sách!");
        }
        Book book = (Book) doc;
        if (book.getWaitingList().contains(member.getUserID())) {
            throw new IllegalStateException("Bạn đã đăng ký đặt trước tài liệu này trước đó rồi!");
        }
        book.getWaitingList().offer(member.getUserID());
        documentManager.saveData();
        return book.getWaitingList().size();
    }

    // =========================================================================
    // 3. TRẢ TÀI LIỆU & XỬ LÝ PHẠT (FR-06, UC-05)
    // =========================================================================

    /** Tính trước số tiền phạt nếu trả hôm nay (để View hiển thị popup xác nhận). */
    public double previewFine(String transactionId) {
        Transaction tx = requireBorrowedTransaction(transactionId);
        if (!tx.isOverdue()) {
            return 0.0;
        }
        long days = ChronoUnit.DAYS.between(tx.getDueDate(), LocalDate.now());
        return days * Transaction.FINE_PER_DAY;
    }

    public long previewOverdueDays(String transactionId) {
        Transaction tx = requireBorrowedTransaction(transactionId);
        if (!tx.isOverdue()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(tx.getDueDate(), LocalDate.now());
    }

    /**
     * Trả tài liệu: tính phạt, trừ ví (nếu được xác nhận), xử lý hàng đợi đặt trước.
     * payFromWallet = true -> trừ trực tiếp từ ví; false -> ghi nhận vào nợ tích lũy.
     */
    public ReturnResult returnDocument(String transactionId, boolean payFromWallet) {
        Transaction tx = requireBorrowedTransaction(transactionId);

        tx.setReturnDate(LocalDate.now());
        double fine = tx.calculateFine();
        long overdueDays = fine > 0
                ? ChronoUnit.DAYS.between(tx.getDueDate(), tx.getReturnDate())
                : 0;
        tx.setStatus("RETURNED");

        // Xử lý tiền phạt trên ví độc giả (Fine Wallet - FR-15)
        boolean paidFully = fine <= 0;
        User user = userManager.findById(tx.getMemberID());
        if (fine > 0 && user instanceof Member) {
            Member member = (Member) user;
            if (payFromWallet) {
                paidFully = member.deductFine(fine);
            } else {
                member.setUnpaidFine(member.getUnpaidFine() + fine);
            }
            userManager.updateUser(member);
        }

        // Kiểm tra hàng đợi đặt trước (Sơ đồ tuần tự SRS Mục 5.4)
        String heldFor = null;
        Document doc = documentManager.findById(tx.getDocumentID());
        if (doc instanceof Book) {
            Book book = (Book) doc;
            String nextMember = book.getWaitingList().poll();
            if (nextMember != null) {
                heldFor = nextMember;
                transactionManager.setHold(book.getId(), nextMember);
                // Giữ sách riêng cho độc giả kế tiếp: KHÔNG tăng số lượng vào kho công cộng
            } else {
                book.setQuantity(book.getQuantity() + 1);
            }
        } else if (doc instanceof Magazine) {
            Magazine magazine = (Magazine) doc;
            magazine.setQuantity(magazine.getQuantity() + 1);
        }

        documentManager.saveData();
        transactionManager.saveData();
        return new ReturnResult(overdueDays, fine, paidFully, heldFor);
    }

    // =========================================================================
    // 4. GIA HẠN MƯỢN TÀI LIỆU (FR-10, UC-06)
    // =========================================================================

    /** Gia hạn thêm 7 ngày; trả về hạn trả mới nếu thành công. */
    public LocalDate renew(String transactionId) {
        Transaction tx = requireBorrowedTransaction(transactionId);

        if (tx.isRenewed()) {
            throw new IllegalStateException("Mỗi phiếu mượn chỉ được phép gia hạn tối đa 01 lần!");
        }
        if (tx.isOverdue()) {
            throw new IllegalStateException("Tài liệu đã quá hạn, không thể gia hạn. "
                    + "Vui lòng mang sách trả và đóng phạt!");
        }
        Document doc = documentManager.findById(tx.getDocumentID());
        if (doc instanceof Book && !((Book) doc).getWaitingList().isEmpty()) {
            throw new IllegalStateException("Cuốn sách đang có độc giả khác đặt trước, không thể gia hạn thêm!");
        }

        tx.renewLoan();
        transactionManager.saveData();
        return tx.getDueDate();
    }

    // =========================================================================
    // HỖ TRỢ NỘI BỘ
    // =========================================================================

    private Transaction requireBorrowedTransaction(String transactionId) {
        Transaction tx = transactionManager.findById(transactionId);
        if (tx == null) {
            throw new IllegalArgumentException("Không tìm thấy giao dịch với mã: " + transactionId);
        }
        if (!"BORROWED".equalsIgnoreCase(tx.getStatus())) {
            throw new IllegalStateException("Giao dịch này đã được trả trước đó!");
        }
        return tx;
    }
}
