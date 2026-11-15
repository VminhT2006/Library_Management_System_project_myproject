package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Lớp tiện ích định dạng và chuyển đổi ngày tháng LocalDate (FR-06, UC-04, UC-05, UC-06).
 */
public final class DateUtil {
    public static final String PATTERN = "dd/MM/yyyy";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    private DateUtil() {
    }

    /**
     * Chuyển đối tượng LocalDate sang chuỗi định dạng "dd/MM/yyyy".
     * Trả về chuỗi rỗng nếu date là null.
     */
    public static String format(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * Chuyển chuỗi ngày tháng ("dd/MM/yyyy" hoặc ISO "yyyy-MM-dd") về đối tượng LocalDate.
     * Trả về null nếu chuỗi rỗng, hoặc ném IllegalArgumentException nếu sai định dạng.
     */
    public static LocalDate parse(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        String trimmed = dateStr.trim();
        try {
            return LocalDate.parse(trimmed, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(trimmed);
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException(
                        "Ngày tháng không đúng định dạng (" + PATTERN + "): " + trimmed);
            }
        }
    }
}
