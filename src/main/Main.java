package main;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import manager.DocumentManager;
import manager.TransactionManager;
import manager.UserManager;
import model.Admin;
import model.Book;
import model.EBook;
import model.Magazine;
import model.Member;
import model.Thesis;
import view.LoginFrame;

/**
 * Điểm khởi chạy ứng dụng Hệ thống Quản lý Thư viện - phiên bản Desktop GUI (SRS FR-01, FR-02, FR-07).
 * Luồng khởi động: Áp dụng Nimbus Look & Feel -> Nạp dữ liệu từ File vào Memory -> Mở LoginFrame.
 */
public class Main {

    public static void main(String[] args) {
        applyNimbusLookAndFeel();

        // FR-07: Tự động tải dữ liệu từ File vào Collection khi ứng dụng khởi chạy
        UserManager userManager = new UserManager(); // constructor tự gọi loadData()
        DocumentManager documentManager = new DocumentManager();
        documentManager.loadData();
        TransactionManager transactionManager = new TransactionManager();
        transactionManager.loadData();

        seedSampleData(userManager, documentManager);

        SwingUtilities.invokeLater(() ->
                new LoginFrame(userManager, documentManager, transactionManager).setVisible(true));
    }

    /** SRS Mục 7.1: Áp dụng Nimbus Look and Feel cho giao diện hiện đại. */
    private static void applyNimbusLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("Không thể áp dụng Nimbus Look & Feel: " + e.getMessage());
        }
    }

    /**
     * Khởi tạo dữ liệu mẫu khi chạy ứng dụng lần đầu (file dữ liệu còn trống):
     * - Tài khoản Admin mặc định: admin / admin123
     * - Tài khoản Member mẫu:     member / 123456
     * - Danh mục 4 loại tài liệu minh họa tính Đa hình (FR-14).
     */
    private static void seedSampleData(UserManager userManager, DocumentManager documentManager) {
        if (userManager.getUsers().isEmpty()) {
            userManager.addUser(new Admin("AD001", "admin", "admin123", "Quản Trị Viên",
                    "admin@library.edu.vn", "0901234567"));
            userManager.addUser(new Member("MEM001", "member", "123456", "Nguyễn Văn Độc Giả",
                    "docgia@gmail.com", "0912345678"));
            System.out.println("Đã khởi tạo tài khoản mặc định: admin/admin123 và member/123456");
        }

        if (documentManager.getDocuments().isEmpty()) {
            documentManager.addDocument(new Book("B001", "Lập Trình Hướng Đối Tượng Java", 2024,
                    "Nguyễn Văn A", "NXB Giáo Dục", 5));
            documentManager.addDocument(new Book("B002", "Clean Code", 2020,
                    "Robert C. Martin", "Prentice Hall", 12));
            documentManager.addDocument(new Book("B003", "Design Patterns", 2022,
                    "Erich Gamma", "Addison-Wesley", 0));
            documentManager.addDocument(new Magazine("M001", "Tạp Chí Tin Học & Đời Sống", 2025, 15, 10, 8));
            documentManager.addDocument(new Thesis("T001", "Ứng Dụng MVC Trong Java Swing", 2024,
                    "ĐH Bách Khoa", "TS. Lê Hoàng", "CNTT"));
            documentManager.addDocument(new EBook("E001", "Effective Java Digital Edition", 2023,
                    "https://library.edu.vn/effective-java.pdf", 8.5, "PDF"));
            documentManager.saveData();
            System.out.println("Đã khởi tạo danh mục tài liệu mẫu (data/documents.csv)");
        }
    }
}
