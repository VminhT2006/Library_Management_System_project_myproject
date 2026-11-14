package model;

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
        if (downloadUrl == null || downloadUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Đường dẫn tải (URL) không được để trống!");
        }
        this.downloadUrl = downloadUrl.trim();
    }

    public double getFileSizeMb() {
        return fileSizeMb;
    }

    public void setFileSizeMb(double fileSizeMb) {
        if (fileSizeMb <= 0) {
            throw new IllegalArgumentException("Dung lượng file phải lớn hơn 0 MB!");
        }
        this.fileSizeMb = fileSizeMb;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        if (format == null || format.trim().isEmpty()) {
            throw new IllegalArgumentException("Định dạng file (format) không được để trống!");
        }
        this.format = format.trim().toUpperCase();
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
