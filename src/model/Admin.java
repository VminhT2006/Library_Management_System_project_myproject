package model;

public class Admin extends User{
    public Admin(String userID, String userName, String password, String fullName, String role){
        super(userID, userName, password, fullName, "ADMIN");
    }
    @Override
    public void showMenu(){
        System.out.println("\n================ MENU QUẢN TRỊ VIÊN (ADMIN) ================");
        System.out.println("Xin chào Thủ thư: " + getFullName() + " (" + getUserID() + ")");
        System.out.println("1. Thêm sách mới vào kho");
        System.out.println("2. Cập nhật / Xóa sách");
        System.out.println("3. Tìm kiếm & Xem danh sách sách");
        System.out.println("4. Ghi nhận trả sách & Tính phí phạt");
        System.out.println("5. Xem toàn bộ lịch sử giao dịch mượn/trả");
        System.out.println("0. Đăng xuất");
        System.out.println("============================================================");
    }
}
