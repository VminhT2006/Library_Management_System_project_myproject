package util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Lớp tiện ích định dạng tiền tệ hiển thị trên giao diện (FR-13, FR-15, UC-05, UC-09).
 */
public final class FormatUtil {
    private static final DecimalFormat VND_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        VND_FORMAT = new DecimalFormat("#,##0", symbols);
    }

    private FormatUtil() {
    }

    /**
     * Định dạng số tiền sang chuỗi VNĐ (ví dụ: 50000 -> "50.000 VNĐ").
     */
    public static synchronized String formatVND(double amount) {
        return VND_FORMAT.format(amount) + " VNĐ";
    }
}
