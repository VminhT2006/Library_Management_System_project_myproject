package util;

import java.util.regex.Pattern;

/**
 * Lớp tiện ích kiểm tra hợp lệ dữ liệu đầu vào cho cả tầng Model (setter) và tầng View (Swing Form).
 */
public final class Validator {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^0\\d{9,10}$");

    private Validator() {
    }

    // =========================================================================
    // 1. KIỂM TRA CHUỖI & ĐỐI TƯỢNG (Dùng trực tiếp trong Setter của Model)
    // =========================================================================

    /**
     * Kiểm tra chuỗi bắt buộc không được rỗng hoặc null, trả về chuỗi đã trim().
     */
    public static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " không được để trống!");
        }
        return value.trim();
    }

    /**
     * Kiểm tra đối tượng không được null.
     */
    public static <T> T requireNonNull(T value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " không được để trống (null)!");
        }
        return value;
    }

    /**
     * Kiểm tra và trả về Email hợp lệ.
     */
    public static String requireEmail(String email) {
        String cleaned = requireText(email, "Email");
        if (!isValidEmail(cleaned)) {
            throw new IllegalArgumentException("Email không đúng định dạng (ví dụ: ten@gmail.com)!");
        }
        return cleaned;
    }

    /**
     * Kiểm tra và trả về Số điện thoại hợp lệ (10-11 chữ số, bắt đầu bằng số 0).
     */
    public static String requirePhone(String phone) {
        String cleaned = requireText(phone, "Số điện thoại");
        if (!isValidPhone(cleaned)) {
            throw new IllegalArgumentException("Số điện thoại phải gồm 10-11 chữ số và bắt đầu bằng số 0!");
        }
        return cleaned;
    }

    /**
     * Kiểm tra và trả về đường dẫn URL hợp lệ (http:// hoặc https://).
     */
    public static String requireUrl(String url, String fieldName) {
        String cleaned = requireText(url, fieldName);
        if (!isValidUrl(cleaned)) {
            throw new IllegalArgumentException(fieldName + " phải bắt đầu bằng http:// hoặc https://!");
        }
        return cleaned;
    }

    // =========================================================================
    // 2. KIỂM TRA GIÁ TRỊ SỐ (Dùng trong Setter của Model)
    // =========================================================================

    /**
     * Kiểm tra số nguyên dương (> 0).
     */
    public static int requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " phải lớn hơn 0!");
        }
        return value;
    }

    /**
     * Kiểm tra số nguyên không âm (>= 0).
     */
    public static int requireNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " không được âm!");
        }
        return value;
    }

    /**
     * Kiểm tra số nguyên nằm trong khoảng [min, max].
     */
    public static int requireRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(fieldName + " phải nằm trong khoảng từ " + min + " đến " + max + "!");
        }
        return value;
    }

    /**
     * Kiểm tra số thực dương (> 0).
     */
    public static double requirePositive(double value, String fieldName) {
        if (value <= 0 || Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException(fieldName + " phải lớn hơn 0!");
        }
        return value;
    }

    /**
     * Kiểm tra số thực không âm (>= 0).
     */
    public static double requireNonNegative(double value, String fieldName) {
        if (value < 0 || Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException(fieldName + " không được âm!");
        }
        return value;
    }

    // =========================================================================
    // 3. ÉP KIỂU TỪ CHUỖI NHẬP TRÊN GIAO DIỆN SWING (JTextField)
    // =========================================================================

    public static int parseInt(String text, String fieldName) {
        String cleaned = requireText(text, fieldName);
        try {
            return Integer.parseInt(cleaned);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " phải là một số nguyên hợp lệ!");
        }
    }

    public static int parsePositiveInt(String text, String fieldName) {
        return requirePositive(parseInt(text, fieldName), fieldName);
    }

    public static int parseNonNegativeInt(String text, String fieldName) {
        return requireNonNegative(parseInt(text, fieldName), fieldName);
    }

    public static double parseDouble(String text, String fieldName) {
        String cleaned = requireText(text, fieldName).replace(',', '.');
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " phải là một số hợp lệ!");
        }
    }

    public static double parsePositiveDouble(String text, String fieldName) {
        return requirePositive(parseDouble(text, fieldName), fieldName);
    }

    // =========================================================================
    // 4. KIỂM TRA ĐỊNHNH DẠNG BOOLEAN
    // =========================================================================

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidUrl(String url) {
        if (url == null) {
            return false;
        }
        String trimmed = url.trim().toLowerCase();
        return trimmed.startsWith("http://") || trimmed.startsWith("https://");
    }
}
