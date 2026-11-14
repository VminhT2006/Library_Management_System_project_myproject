package model;

/**
 * Lớp cơ sở Person đại diện cho thông tin con người trong hệ thống.
 */
public class Person {
    protected String fullName;
    protected String email;
    protected String phone;

    public Person() {
    }

    public Person(String fullName) {
        setFullName(fullName);
    }

    public Person(String fullName, String email, String phone) {
        setFullName(fullName);
        setEmail(email);
        setPhone(phone);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ và tên không được để trống!");
        }
        this.fullName = fullName.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống!");
        }
        this.email = email.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống!");
        }
        this.phone = phone.trim();
    }
}
