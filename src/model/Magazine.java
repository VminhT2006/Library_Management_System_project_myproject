package model;

import util.Validator;

/**
 * Lớp Magazine đại diện cho Tạp chí định kỳ, kế thừa từ Document (SRS Mục 5.2).
 */
public class Magazine extends Document {
    public static final double LATE_FEE_PER_DAY = 5000.0;

    private int issueNumber;
    private int publishMonth;
    private int quantity;

    public Magazine() {
        super();
    }

    public Magazine(String id, String title, int publicationYear, int issueNumber, int publishMonth, int quantity) {
        super(id, title, publicationYear);
        setIssueNumber(issueNumber);
        setPublishMonth(publishMonth);
        setQuantity(quantity);
    }

    public int getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(int issueNumber) {
        this.issueNumber = Validator.requirePositive(issueNumber, "Số phát hành tạp chí");
    }

    public int getPublishMonth() {
        return publishMonth;
    }

    public void setPublishMonth(int publishMonth) {
        this.publishMonth = Validator.requireRange(publishMonth, 1, 12, "Tháng xuất bản");
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Validator.requireNonNegative(quantity, "Số lượng tạp chí");
    }

    @Override
    public boolean canBorrow() {
        return this.quantity > 0;
    }

    @Override
    public double calculateLateFee(int days) {
        return days > 0 ? days * LATE_FEE_PER_DAY : 0.0;
    }

    @Override
    public String getTypeName() {
        return "Tạp chí";
    }
}
