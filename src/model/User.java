package model;

import util.Validator;

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
        this.userID = Validator.requireText(userID, "Mã người dùng (ID)").toUpperCase();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = Validator.requireText(userName, "Tên đăng nhập");
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = Validator.requireText(password, "Mật khẩu");
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = Validator.requireText(role, "Vai trò (role)").toUpperCase();
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
