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
