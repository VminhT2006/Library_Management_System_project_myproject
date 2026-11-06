package model;

abstract class User {
    private String userID, userName, password, role, fullName;

    protected User(String userID, String userName, String password, String fullName, String role) {
        setFullName(fullName);
        setUserName(userName);
        setPassword(password);
        setUserID(userID);
        this.role = role;
    }

    // get vaf set fullName
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            // xử lí trường hợp xâu vào không hợp lệ
            throw new IllegalArgumentException("Tên đầy đủ không hợp lệ");
        } else
            this.fullName = fullName.trim();
    }

    // get role
    public String getRole() {
        return role;
    }

    // get va set userID
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        if (userID == null || userID.trim().isEmpty()) {
            // xử lí trường hợp xâu vào không hợp lệ
            throw new IllegalArgumentException("User ID không hợp lệ");
        } else
            this.userID = userID.trim().toUpperCase();
    }

    // get va set userName
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        if (userName == null || userName.matches("^[a-zA-Z0-9_]$")) {
            // xử lí trường hợp xâu vào không hợp lệ
            throw new IllegalArgumentException("UserName không hợp lệ");
        } else
            this.userName = userName.trim();
    }

    // mặt khẩu phải có độ dài >= 8, chấp nhận a-z, A-Z, 0-9
    public boolean checkPasswordValid(String password) {
        if (password.length() < 8 && password.matches("^[a-zA-Z0-9_]"))
            return true;
        else
            return false;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String passwrord) {
        if (password == null || password.trim().isEmpty() || !checkPasswordValid(password)) {
            // xử lí trường hợp xâu vào không hợp lệ
            throw new IllegalArgumentException("Mật khẩu không hợp lệ");
        } else
            this.password = password.trim();
    }

    public abstract void showMenu();

    public String toCsvString() {
        return String.join(",", userID, userName, fullName, password, role);
    }
}
