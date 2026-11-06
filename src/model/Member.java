package model;
public class Member extends User {
    private final int MAX_BORROW_BOOK = 3; //số lượng sách tối đa có thể mượn là 3
    private boolean banned; //cấm mượn sách true = banned, false = no banned
    public Member(String userID, String userName, String password, String fullName, String role) {
        super(userID, userName, password, fullName, "MEMBER");
        this.banned = false;
    }

    public boolean isBanned(){
        return this.banned;
    }
    public void setBanned(boolean banned){
        this.banned = banned;
    }
    //menu của member
    @Override
    public void showMenu() {
        System.out.println("\n=================== MENU ĐỘC GIẢ (MEMBER) ===================");
        System.out.println("Xin chào Độc giả: " + getFullName() + " (" + getUserID() + ")");
        System.out.println("1. Xem danh sách sách & Tìm kiếm ");
        System.out.println("2. Mượn sách ");
        System.out.println("3. Trả sách & Kiểm tra tiền phạt");
        System.out.println("4. Xem lịch sử mượn/trả & Thời hạn trả của bản thân");
        System.out.println("0. Đăng xuất");
        System.out.println("=============================================================");
    }
}
