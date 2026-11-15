package interfaces;

/**
 * Interface định nghĩa chuẩn đọc và ghi dữ liệu bền vững xuống tập tin (FR-07 SRS).
 * Các lớp Manager (DocumentManager, UserManager, TransactionManager) sẽ cài đặt interface này.
 */
public interface IFileService {
    /**
     * Đọc dữ liệu từ tập tin vào bộ nhớ (Collection).
     */
    void loadData();

    /**
     * Lưu dữ liệu từ bộ nhớ xuống tập tin.
     */
    boolean saveData();
}
