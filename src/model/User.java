package model;

/**
 * Lớp trừu tượng User đại diện cho tài khoản người dùng trong hệ thống (SRS Mục 5.5).
 * Kế thừa từ lớp Person.
 */
public abstract class User extends Person {
    protected String userID;
    protected String userName;
    protected String password;
    protected String role;

    public User() {
        super();
    }

    public User(String userID, String userName, String password, String fullName, String role) {
        super(fullName);
        setUserID(userID);
        setUserName(userName);
        setPassword(password);
        setRole(role);
    }

    public User(String userID, String userName, String password, String fullName, String email, String phone, String role) {
        super(fullName, email, phone);
        setUserID(userID);
        setUserName(userName);
        setPassword(password);
        setRole(role);
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        if (userID == null || userID.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã người dùng (ID) không được để trống!");
        }
        this.userID = userID.trim().toUpperCase();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống!");
        }
        this.userName = userName.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống!");
        }
        this.password = password.trim();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Vai trò (role) không được để trống!");
        }
        this.role = role.trim().toUpperCase();
    }

    /**
     * Kiểm tra mật khẩu đăng nhập.
     */
    public boolean checkPassword(String pwd) {
        if (pwd == null || pwd.trim().isEmpty()) {
            return false;
        }
        return this.password != null && this.password.equals(pwd.trim());
    }
}
