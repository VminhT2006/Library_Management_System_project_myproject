package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp tiện ích xử lý đọc và ghi tập tin văn bản/CSV chuẩn UTF-8 (FR-07, Mục 7.3 SRS).
 */
public final class FileUtil {

    private FileUtil() {
        // Không cho phép khởi tạo đối tượng từ lớp tiện ích
    }

    /**
     * Đọc toàn bộ các dòng có nội dung từ file văn bản theo bảng mã UTF-8.
     * Nếu file chưa tồn tại, trả về danh sách rỗng thay vì ném lỗi.
     */
    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        if (filePath == null || filePath.trim().isEmpty()) {
            return lines;
        }

        File file = new File(filePath.trim());
        if (!file.exists()) {
            return lines;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file [" + filePath + "]: " + e.getMessage());
        }
        return lines;
    }

    // =========================================================================
    // ĐỊNH VỊ FILE DỮ LIỆU LINH HOẠT THEO THƯ MỤC LÀM VIỆC (IDE / dòng lệnh)
    // =========================================================================

    /** Các tiền tố thư mục thử lần lượt để tìm file dữ liệu dù chạy từ vị trí nào. */
    private static final String[] BASE_PREFIXES = {
            "",
            "src/",
            "src/bai_tap_lon/Library_Management_System_project_myproject/src/"
    };

    /**
     * Trả về đường dẫn thực tế của file dữ liệu để ĐỌC (file đầu tiên tồn tại).
     * Nếu không tìm thấy ở đâu, trả về chính relativePath (sẽ đọc ra danh sách rỗng).
     */
    public static String resolveForRead(String relativePath) {
        for (String prefix : BASE_PREFIXES) {
            File file = new File(prefix + relativePath);
            if (file.exists()) {
                return prefix + relativePath;
            }
        }
        return relativePath;
    }

    /**
     * Trả về đường dẫn để GHI: ưu tiên đúng file đang tồn tại (ghi đè tại chỗ),
     * nếu chưa có thì chọn thư mục cha đã tồn tại đầu tiên, cuối cùng dùng relativePath.
     */
    public static String resolveForWrite(String relativePath) {
        for (String prefix : BASE_PREFIXES) {
            if (new File(prefix + relativePath).exists()) {
                return prefix + relativePath;
            }
        }
        for (String prefix : BASE_PREFIXES) {
            File parent = new File(prefix + relativePath).getParentFile();
            if (parent != null && parent.exists()) {
                return prefix + relativePath;
            }
        }
        return relativePath;
    }

    // =========================================================================
    // TIỆN ÍCH CSV CHUẨN (dấu phẩy, hỗ trợ trường có dấu nháy kép)
    // =========================================================================

    /** Tách một dòng CSV phân cách bằng dấu phẩy, hỗ trợ trường bọc trong dấu "..." . */
    public static List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    field.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(field.toString());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        result.add(field.toString());
        return result;
    }

    /** Ghép các trường thành một dòng CSV, tự bọc dấu nháy khi gặp dấu phẩy/nháy/xuống dòng. */
    public static String toCsvLine(String... fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            String value = fields[i] == null ? "" : fields[i];
            if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
                sb.append('"').append(value.replace("\"", "\"\"")).append('"');
            } else {
                sb.append(value);
            }
        }
        return sb.toString();
    }

    /**
     * Ghi danh sách các dòng văn bản xuống file theo bảng mã UTF-8.
     * Tự động tạo thư mục cha (ví dụ: thư mục data/) nếu chưa tồn tại.
     */
    public static boolean writeLines(String filePath, List<String> lines) {
        if (filePath == null || filePath.trim().isEmpty() || lines == null) {
            return false;
        }

        File file = new File(filePath.trim());
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            for (String line : lines) {
                if (line != null) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi file [" + filePath + "]: " + e.getMessage());
            return false;
        }
    }
}
