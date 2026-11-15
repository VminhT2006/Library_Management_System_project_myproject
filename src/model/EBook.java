package model;

import util.Validator;

/**
 * Lớp EBook đại diện cho Sách điện tử, kế thừa từ Document (SRS Mục 5.2).
 * Quy tắc nghiệp vụ: Không giới hạn bản sao (canBorrow() luôn trả về true) và không tính phí phạt trễ hạn (0 VNĐ).
 */
public class EBook extends Document {
    private String downloadUrl;
    private double fileSizeMb;
    private String format;

    public EBook() {
        super();
    }

    public EBook(String id, String title, int publicationYear, String downloadUrl, double fileSizeMb, String format) {
        super(id, title, publicationYear);
        setDownloadUrl(downloadUrl);
        setFileSizeMb(fileSizeMb);
        setFormat(format);
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = Validator.requireUrl(downloadUrl, "Đường dẫn tải (URL)");
    }

    public double getFileSizeMb() {
        return fileSizeMb;
    }

    public void setFileSizeMb(double fileSizeMb) {
        this.fileSizeMb = Validator.requirePositive(fileSizeMb, "Dung lượng file (MB)");
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = Validator.requireText(format, "Định dạng file (format)").toUpperCase();
    }

    @Override
    public boolean canBorrow() {
        return true;
    }

    @Override
    public double calculateLateFee(int days) {
        return 0.0;
    }

    @Override
    public String getTypeName() {
        return "E-Book";
    }
}
