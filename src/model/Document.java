package model;

import java.time.LocalDate;

/**
 * Lớp trừu tượng Document làm gốc cho mọi loại ấn phẩm trong thư viện (SRS Mục 5.2).
 * Thể hiện tính Trừu tượng (Abstraction) và Đa hình (Polymorphism).
 */
public abstract class Document {
    protected String id;
    protected String title;
    protected int publicationYear;

    public Document() {
    }

    public Document(String id, String title, int publicationYear) {
        setId(id);
        setTitle(title);
        setPublicationYear(publicationYear);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã tài liệu (ID) không được để trống!");
        }
        this.id = id.trim().toUpperCase();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Tiêu đề tài liệu không được để trống!");
        }
        this.title = title.trim();
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        int currentYear = LocalDate.now().getYear();
        if (publicationYear <= 0 || publicationYear > currentYear) {
            throw new IllegalArgumentException("Năm xuất bản không hợp lệ (phải từ 1 đến " + currentYear + ")!");
        }
        this.publicationYear = publicationYear;
    }

    /**
     * Kiểm tra tài liệu có được phép mượn về hay không.
     */
    public abstract boolean canBorrow();

    /**
     * Tính phí phạt trễ hạn dựa trên số ngày quá hạn.
     */
    public abstract double calculateLateFee(int days);

    /**
     * Trả về tên loại tài liệu để hiển thị trên giao diện.
     */
    public abstract String getTypeName();
}
