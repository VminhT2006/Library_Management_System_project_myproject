package main;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import manager.DocumentManager;
import model.Admin;
import model.Book;
import model.Document;
import model.EBook;
import model.Magazine;
import model.Member;
import model.Thesis;
import model.Transaction;
import strategy.SortByQuantityDescending;
import strategy.SortByTitleAscending;
import strategy.SortByYearDescending;
import util.DateUtil;
import util.FormatUtil;

/**
 * Lớp Main chạy kiểm thử toàn bộ các tính năng đã xây dựng (Model, Util, Strategy, DocumentManager).
 */
public class Main {

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.out.println("=================================================================");
        System.out.println("   KIỂM THỬ HỆ THỐNG QUẢN LÝ THƯ VIỆN (MODEL - STRATEGY - UTIL)");
        System.out.println("=================================================================\n");

        // 1. Khởi tạo DocumentManager và thêm 4 loại tài liệu (Đa hình)
        DocumentManager docManager = new DocumentManager();
        docManager.addDocument(new Book("B001", "Lập Trình Hướng Đối Tượng Java", 2024, "Nguyễn Văn A", "NXB Giáo Dục", 5));
        docManager.addDocument(new Book("B002", "Clean Code", 2020, "Robert C. Martin", "Prentice Hall", 12));
        docManager.addDocument(new Book("B003", "Design Patterns", 2022, "Erich Gamma", "Addison-Wesley", 0));
        docManager.addDocument(new Magazine("M001", "Tạp Chí Tin Học & Đời Sống", 2025, 15, 10, 8));
        docManager.addDocument(new Thesis("T001", "Ứng Dụng MVC Trong Java Swing", 2024, "ĐH Bách Khoa", "TS. Lê Hoàng", "CNTT"));
        docManager.addDocument(new EBook("E001", "Effective Java Digital Edition", 2023, "https://library.edu.vn/effective-java.pdf", 8.5, "PDF"));

        System.out.println("--- [1] KIỂM THỬ ĐA HÌNH TRÊN 4 LOẠI TÀI LIỆU (trễ 3 ngày) ---");
        for (Document doc : docManager.getDocuments()) {
            System.out.printf("• [%s] %-10s | %-34s | Mượn về: %-5s | Phí trễ 3 ngày: %s%n",
                    doc.getId(),
                    doc.getTypeName(),
                    doc.getTitle(),
                    doc.canBorrow() ? "Có" : "Không",
                    FormatUtil.formatVND(doc.calculateLateFee(3)));
        }

        // 2. Kiểm thử Strategy Pattern sắp xếp Sách
        System.out.println("\n--- [2] KIỂM THỬ STRATEGY PATTERN SẮP XẾP SÁCH ---");
        List<Book> books = docManager.getAllBooks();

        docManager.setSorter(new SortByTitleAscending());
        docManager.executeSort(books);
        System.out.println("* Sắp xếp theo Tên sách (A -> Z):");
        printBooks(books);

        docManager.setSorter(new SortByYearDescending());
        docManager.executeSort(books);
        System.out.println("* Sắp xếp theo Năm xuất bản (Mới nhất -> Cũ nhất):");
        printBooks(books);

        docManager.setSorter(new SortByQuantityDescending());
        docManager.executeSort(books);
        System.out.println("* Sắp xếp theo Số lượng tồn kho (Nhiều nhất -> Ít nhất):");
        printBooks(books);

        // 3. Kiểm thử Tìm kiếm tức thì (Live Search không dấu bằng TextUtil)
        System.out.println("\n--- [3] KIỂM THỬ TÌM KIẾM KHÔNG DẤU (Từ khóa: 'lap trinh') ---");
        List<Document> found = docManager.searchDocuments("lap trinh", "Tất cả");
        for (Document doc : found) {
            System.out.println("  -> Tìm thấy: [" + doc.getId() + "] " + doc.getTitle());
        }

        // 4. Kiểm thử Hàng đợi đặt trước FIFO & Lưu/Đọc File CSV
        System.out.println("\n--- [4] KIỂM THỬ HÀNG ĐỢI ĐẶT TRƯỚC (FIFO) & LƯU FILE CSV ---");
        Book outOfStockBook = (Book) docManager.findById("B003");
        outOfStockBook.getWaitingList().offer("MEM01");
        outOfStockBook.getWaitingList().offer("MEM02");
        System.out.println("Hàng đợi của sách [" + outOfStockBook.getTitle() + "]: " + outOfStockBook.getWaitingList());

        boolean saved = docManager.saveData();
        System.out.println("Đã lưu xuống file data/documents.csv: " + saved);
        docManager.loadData();
        System.out.println("Số tài liệu đọc lại từ file CSV: " + docManager.getDocuments().size());

        // 5. Kiểm thử Member (Ví tiền & Khóa tài khoản) và Transaction (Gia hạn & Tính phạt)
        System.out.println("\n--- [5] KIỂM THỬ VÍ TIỀN ĐỘC GIẢ & GIAO DỊCH MƯỢN/GIA HẠN ---");
        Admin admin = new Admin("AD01", "admin", "admin123", "Quản Trị Viên", "admin@lib.edu.vn", "0901234567");
        Member member = new Member("MEM01", "nguyenvanb", "123456", "Nguyễn Văn B", "vanb@gmail.com", "0912345678");
        System.out.println("Khởi tạo Admin: " + admin.getFullName() + " (" + admin.getRole() + ")");

        member.deposit(20000);
        System.out.println("Số dư ví sau khi nạp 20.000đ: " + FormatUtil.formatVND(member.getBalance()));

        // Trừ phạt 80.000đ -> thiếu 60.000đ (> 50.000đ) -> tự động khóa tài khoản
        member.deductFine(80000);
        System.out.println("Sau khi phát sinh phạt 80.000đ -> Nợ phạt: "
                + FormatUtil.formatVND(member.getUnpaidFine()) + " | Bị khóa (banned): " + member.isBanned());

        // Nạp thêm 100.000đ -> tự động trừ hết nợ 60.000đ và mở khóa
        member.deposit(100000);
        System.out.println("Sau khi nạp thêm 100.000đ -> Số dư còn: "
                + FormatUtil.formatVND(member.getBalance()) + " | Nợ phạt: "
                + FormatUtil.formatVND(member.getUnpaidFine()) + " | Bị khóa (banned): " + member.isBanned());

        // Giao dịch mượn & Gia hạn (+7 ngày)
        Transaction tx = new Transaction("TX001", member.getUserID(), "B001");
        System.out.println("Phiếu mượn " + tx.getTransactionID()
                + " | Ngày mượn: " + DateUtil.format(tx.getBorrowDate())
                + " | Hạn trả ban đầu: " + DateUtil.format(tx.getDueDate()));
        tx.renewLoan();
        System.out.println("Sau khi gia hạn lần 1 (+7 ngày) -> Hạn trả mới: "
                + DateUtil.format(tx.getDueDate()) + " | Gia hạn lần 2 được không? " + tx.renewLoan());

        // Giả lập trả trễ 4 ngày
        tx.setReturnDate(tx.getDueDate().plusDays(4));
        System.out.println("Giả lập trả trễ 4 ngày -> Tiền phạt: " + FormatUtil.formatVND(tx.calculateFine()));

        // 6. Kiểm thử Validator bẫy lỗi đầu vào rỗng
        System.out.println("\n--- [6] KIỂM THỬ VALIDATOR BẪY LỖI TRONG SETTER ---");
        try {
            member.setEmail("email_sai_dinh_dang");
        } catch (IllegalArgumentException e) {
            System.out.println("Bắt lỗi thành công khi nhập sai Email: " + e.getMessage());
        }

        try {
            outOfStockBook.setQuantity(-5);
        } catch (IllegalArgumentException e) {
            System.out.println("Bắt lỗi thành công khi nhập số lượng âm: " + e.getMessage());
        }
    }

    private static void printBooks(List<Book> books) {
        for (Book b : books) {
            System.out.printf("   - [%s] %-34s | Năm XB: %d | Tồn kho: %d%n",
                    b.getId(), b.getTitle(), b.getPublicationYear(), b.getQuantity());
        }
    }
}
