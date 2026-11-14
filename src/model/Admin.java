package model;

/**
 * Lớp Admin đại diện cho Quản trị viên / Thủ thư (SRS Mục 5.5).
 * Kế thừa từ lớp trừu tượng User.
 */
public class Admin extends User {

    public Admin() {
        super();
        this.role = "ADMIN";
    }

    public Admin(String userID, String userName, String password, String fullName) {
        super(userID, userName, password, fullName, "ADMIN");
    }

    public Admin(String userID, String userName, String password, String fullName, String email, String phone) {
        super(userID, userName, password, fullName, email, phone, "ADMIN");
    }

    public void manageUsers() {
        // Khung phương thức quản lý người dùng
    }

    public void viewReports() {
        // Khung phương thức xem báo cáo thống kê
    }
}
