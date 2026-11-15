package util;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Lớp tiện ích xử lý và chuẩn hóa chuỗi phục vụ Tìm kiếm tức thì - Live Search (FR-04, UC-03).
 */
public final class TextUtil {

    private TextUtil() {
    }

    /**
     * Chuẩn hóa chuỗi về chữ thường và loại bỏ dấu tiếng Việt.
     * Ví dụ: "Lập Trình Hướng Đối Tượng" -> "lap trinh huong doi tuong".
     */
    public static String normalize(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        String decomposed = Normalizer.normalize(input.trim(), Normalizer.Form.NFD);
        String withoutAccents = decomposed.replaceAll("\\p{M}+", "");
        return withoutAccents
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase(Locale.ROOT);
    }

    /**
     * Kiểm tra chuỗi nguồn có chứa từ khóa tìm kiếm hay không (không phân biệt hoa thường & dấu).
     */
    public static boolean containsKeyword(String source, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }
        if (source == null || source.trim().isEmpty()) {
            return false;
        }
        return normalize(source).contains(normalize(keyword));
    }
}
