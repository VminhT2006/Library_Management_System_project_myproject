package controller;

import manager.UserManager;
import model.Member;
import model.User;
import util.Validator;

/**
 * Controller xử lý Đăng nhập (FR-02, UC-01) và Đăng ký tài khoản độc giả (FR-08).
 * Nhận sự kiện từ LoginFrame / RegisterDialog và gọi nghiệp vụ trong UserManager.
 */
public class LoginController {
    private final UserManager userManager;

    public LoginController(UserManager userManager) {
        this.userManager = Validator.requireNonNull(userManager, "UserManager");
    }

    /**
     * Xác thực đăng nhập. Trả về User (Admin/Member) nếu thành công,
     * ném IllegalArgumentException kèm thông báo tiếng Việt nếu thất bại.
     */
    public User login(String userName, String password) {
        if (userName == null || userName.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ Tên đăng nhập và Mật khẩu!");
        }
        User user = userManager.authenticate(userName, password);
        if (user == null) {
            throw new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu không chính xác!");
        }
        return user;
    }

    /**
     * Đăng ký tài khoản độc giả mới qua RegisterDialog (UC-01 luồng Guest, FR-08).
     * Kiểm tra định dạng và tính duy nhất trước khi lưu xuống File.
     */
    public Member register(String fullName, String userName, String password, String confirmPassword) {
        Validator.requireText(fullName, "Họ và tên");
        String cleanUserName = Validator.requireText(userName, "Tên đăng nhập");
        String cleanPassword = Validator.requireText(password, "Mật khẩu");

        if (cleanUserName.contains(" ")) {
            throw new IllegalArgumentException("Tên đăng nhập không được chứa khoảng trắng!");
        }
        if (cleanUserName.length() < 3) {
            throw new IllegalArgumentException("Tên đăng nhập phải có ít nhất 3 ký tự!");
        }
        if (cleanPassword.length() < 3) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 3 ký tự!");
        }
        if (!cleanPassword.equals(confirmPassword == null ? "" : confirmPassword.trim())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không trùng khớp!");
        }
        if (userManager.findByUserName(cleanUserName) != null) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại: " + cleanUserName);
        }
        return userManager.registerMember(fullName.trim(), cleanUserName, cleanPassword);
    }
}
