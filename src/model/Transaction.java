package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import util.Validator;

/**
 * Lớp Transaction đại diện cho Giao dịch mượn / trả tài liệu (SRS Mục 5.5).
 * Hỗ trợ kiểm tra quá hạn, tính phí phạt (5.000 VNĐ/ngày) và gia hạn mượn (+7 ngày, tối đa 1 lần).
 */
public class Transaction {
    public static final int DEFAULT_BORROW_DAYS = 14;
    public static final int RENEW_DAYS = 7;
    public static final double FINE_PER_DAY = 5000.0;

    private String transactionID;
    private String memberID;
    private String documentID;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status; // "BORROWED" hoặc "RETURNED"
    private double fineAmount;
    private boolean isRenewed;

    public Transaction() {
    }

    public Transaction(String transactionID, String memberID, String documentID) {
        setTransactionID(transactionID);
        setMemberID(memberID);
        setDocumentID(documentID);
        setBorrowDate(LocalDate.now());
        setDueDate(this.borrowDate.plusDays(DEFAULT_BORROW_DAYS));
        setStatus("BORROWED");
        setFineAmount(0.0);
        this.isRenewed = false;
    }

    public Transaction(String transactionID, String memberID, String documentID,
                       LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate,
                       String status, double fineAmount, boolean isRenewed) {
        setTransactionID(transactionID);
        setMemberID(memberID);
        setDocumentID(documentID);
        setBorrowDate(borrowDate);
        setDueDate(dueDate);
        setReturnDate(returnDate);
        setStatus(status);
        setFineAmount(fineAmount);
        this.isRenewed = isRenewed;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = Validator.requireText(transactionID, "Mã giao dịch (transactionID)").toUpperCase();
    }

    public String getMemberID() {
        return memberID;
    }

    public void setMemberID(String memberID) {
        this.memberID = Validator.requireText(memberID, "Mã độc giả (memberID)").toUpperCase();
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = Validator.requireText(documentID, "Mã tài liệu (documentID)").toUpperCase();
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = Validator.requireNonNull(borrowDate, "Ngày mượn");
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        Validator.requireNonNull(dueDate, "Ngày hẹn trả");
        if (this.borrowDate != null && dueDate.isBefore(this.borrowDate)) {
            throw new IllegalArgumentException("Ngày hẹn trả không được trước ngày mượn!");
        }
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        if (returnDate != null && this.borrowDate != null && returnDate.isBefore(this.borrowDate)) {
            throw new IllegalArgumentException("Ngày trả thực tế không được trước ngày mượn!");
        }
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = Validator.requireText(status, "Trạng thái giao dịch").toUpperCase();
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = Validator.requireNonNegative(fineAmount, "Số tiền phạt");
    }

    public boolean isRenewed() {
        return isRenewed;
    }

    public void setRenewed(boolean renewed) {
        isRenewed = renewed;
    }

    /**
     * Kiểm tra giao dịch hiện có bị quá hạn hay không.
     */
    public boolean isOverdue() {
        if ("RETURNED".equalsIgnoreCase(this.status)) {
            return false;
        }
        return this.dueDate != null && LocalDate.now().isAfter(this.dueDate);
    }

    /**
     * Gia hạn phiếu mượn thêm 7 ngày (UC-06: tối đa 01 lần và chưa bị quá hạn).
     */
    public boolean renewLoan() {
        if (this.isRenewed || isOverdue() || !"BORROWED".equalsIgnoreCase(this.status)) {
            return false;
        }
        this.dueDate = this.dueDate.plusDays(RENEW_DAYS);
        this.isRenewed = true;
        return true;
    }

    /**
     * Tính toán số tiền phạt trễ hạn (5.000 VNĐ / ngày trễ).
     */
    public double calculateFine() {
        if (this.dueDate == null) {
            return 0.0;
        }
        LocalDate checkDate = (this.returnDate != null) ? this.returnDate : LocalDate.now();
        if (checkDate.isAfter(this.dueDate)) {
            long overdueDays = ChronoUnit.DAYS.between(this.dueDate, checkDate);
            this.fineAmount = overdueDays * FINE_PER_DAY;
        } else {
            this.fineAmount = 0.0;
        }
        return this.fineAmount;
    }
}
