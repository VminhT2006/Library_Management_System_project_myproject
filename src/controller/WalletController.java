package controller;

import manager.UserManager;
import model.Member;
import util.Validator;

/**
 * Controller quản lý Ví tiền độc giả - Fine Wallet (FR-15, UC-09).
 */
public class WalletController {
    private final UserManager userManager;

    public WalletController(UserManager userManager) {
        this.userManager = Validator.requireNonNull(userManager, "UserManager");
    }

    /**
     * Nạp tiền vào ví độc giả (UC-09).
     * Nếu trước đó tài khoản bị khóa và số dư mới đủ trả nợ, Member tự động trừ nợ và mở khóa.
     * Trả về true nếu thao tác nạp giúp tài khoản được mở khóa.
     */
    public boolean deposit(Member member, double amount) {
        Validator.requireNonNull(member, "Độc giả");
        Validator.requirePositive(amount, "Số tiền nạp");
        boolean wasBanned = member.isBanned();
        member.deposit(amount);
        userManager.updateUser(member);
        return wasBanned && !member.isBanned();
    }

    /**
     * Thu toàn bộ nợ phạt tích lũy từ ví độc giả (nút "Thu phí phạt" của Admin - FR-06).
     */
    public double collectUnpaidFine(Member member) {
        Validator.requireNonNull(member, "Độc giả");
        double debt = member.getUnpaidFine();
        if (debt <= 0) {
            throw new IllegalStateException("Độc giả " + member.getFullName() + " không có nợ phạt!");
        }
        if (member.getBalance() < debt) {
            throw new IllegalStateException("Số dư ví của độc giả không đủ để thu phạt! "
                    + "Vui lòng yêu cầu độc giả nạp thêm tiền vào ví.");
        }
        member.setBalance(member.getBalance() - debt);
        member.setUnpaidFine(0.0);
        member.setBanned(false);
        userManager.updateUser(member);
        return debt;
    }
}
