package model;

/**
 * Lớp Member đại diện cho Độc giả thư viện (SRS Mục 5.5).
 * Kế thừa từ lớp trừu tượng User, tích hợp Ví tiền & Phí phạt (Fine Wallet - FR-15, UC-09).
 */
public class Member extends User {
    public static final double BAN_FINE_THRESHOLD = 50000.0;

    private final int MAX_BORROW_BOOK = 3;
    private String tier;
    private boolean banned;
    private double balance;
    private double unpaidFine;

    public Member() {
        super();
        this.role = "MEMBER";
        this.tier = "Tiêu chuẩn";
        this.banned = false;
        this.balance = 0.0;
        this.unpaidFine = 0.0;
    }

    public Member(String userID, String userName, String password, String fullName) {
        super(userID, userName, password, fullName, "MEMBER");
        setTier("Tiêu chuẩn");
        this.banned = false;
        this.balance = 0.0;
        this.unpaidFine = 0.0;
    }

    public Member(String userID, String userName, String password, String fullName, String email, String phone) {
        super(userID, userName, password, fullName, email, phone, "MEMBER");
        setTier("Tiêu chuẩn");
        this.banned = false;
        this.balance = 0.0;
        this.unpaidFine = 0.0;
    }

    public int getMaxBorrowBook() {
        return MAX_BORROW_BOOK;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        if (tier == null || tier.trim().isEmpty()) {
            throw new IllegalArgumentException("Hạng thành viên không được để trống!");
        }
        this.tier = tier.trim();
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Số dư ví không được âm!");
        }
        this.balance = balance;
    }

    public double getUnpaidFine() {
        return unpaidFine;
    }

    public void setUnpaidFine(double unpaidFine) {
        if (unpaidFine < 0) {
            throw new IllegalArgumentException("Số tiền nợ phạt không được âm!");
        }
        this.unpaidFine = unpaidFine;
        if (this.unpaidFine > BAN_FINE_THRESHOLD) {
            this.banned = true;
        }
    }

    /**
     * Nạp tiền vào ví cá nhân (UC-09).
     * Nếu trước đó tài khoản bị khóa và số dư mới đủ thanh toán nợ phạt, hệ thống tự động trừ nợ và mở khóa.
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền nạp phải lớn hơn 0!");
        }
        this.balance += amount;
        if (this.unpaidFine > 0 && this.balance >= this.unpaidFine) {
            this.balance -= this.unpaidFine;
            this.unpaidFine = 0.0;
            this.banned = false;
        }
    }

    /**
     * Khấu trừ tiền phạt từ ví (UC-05).
     * Trả về true nếu ví đủ số dư để trừ hết phạt; false nếu không đủ (phần thiếu được cộng vào unpaidFine).
     */
    public boolean deductFine(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Số tiền phạt cần trừ không được âm!");
        }
        if (amount == 0) {
            return true;
        }
        if (this.balance >= amount) {
            this.balance -= amount;
            return true;
        } else {
            double remainingDebt = amount - this.balance;
            this.balance = 0.0;
            this.unpaidFine += remainingDebt;
            if (this.unpaidFine > BAN_FINE_THRESHOLD) {
                this.banned = true;
            }
            return false;
        }
    }
}
