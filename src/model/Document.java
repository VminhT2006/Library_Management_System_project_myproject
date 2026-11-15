package model;

import java.time.LocalDate;
import util.Validator;

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
        this.id = Validator.requireText(id, "Mã tài liệu (ID)").toUpperCase();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = Validator.requireText(title, "Tiêu đề tài liệu");
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        int currentYear = LocalDate.now().getYear();
        this.publicationYear = Validator.requireRange(publicationYear, 1, currentYear, "Năm xuất bản");
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
