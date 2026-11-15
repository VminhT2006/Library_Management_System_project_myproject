package model;

import util.Validator;

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
        this.fullName = Validator.requireText(fullName, "Họ và tên");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = Validator.requireEmail(email);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = Validator.requirePhone(phone);
    }
}
