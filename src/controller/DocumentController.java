package controller;

import interfaces.IBookSorter;
import java.util.List;
import manager.DocumentManager;
import model.Book;
import model.Document;
import util.Validator;

/**
 * Controller quản lý danh mục Tài liệu (FR-03, FR-04, FR-11 - UC-02, UC-03).
 * Mọi thao tác thêm/sửa/xóa đều tự động đồng bộ ngầm xuống File (FR-07).
 */
public class DocumentController {
    private final DocumentManager documentManager;

    public DocumentController(DocumentManager documentManager) {
        this.documentManager = Validator.requireNonNull(documentManager, "DocumentManager");
    }

    public List<Document> getAllDocuments() {
        return documentManager.getDocuments();
    }

    public Document findById(String id) {
        return documentManager.findById(id);
    }

    /** Thêm tài liệu mới (UC-02); ném IllegalArgumentException nếu trùng mã ID. */
    public void addDocument(Document doc) {
        documentManager.addDocument(doc);
        documentManager.saveData();
    }

    /** Cập nhật tài liệu theo mã ID (UC-02). */
    public void updateDocument(Document doc) {
        if (!documentManager.updateDocument(doc)) {
            throw new IllegalArgumentException("Không tìm thấy tài liệu cần cập nhật: " + doc.getId());
        }
        documentManager.saveData();
    }

    /** Xóa tài liệu theo mã ID (UC-02). */
    public void deleteDocument(String id) {
        if (!documentManager.removeDocument(id)) {
            throw new IllegalArgumentException("Không tìm thấy tài liệu cần xóa: " + id);
        }
        documentManager.saveData();
    }

    /** Tìm kiếm tức thì - Live Search (UC-03). */
    public List<Document> search(String keyword, String criteria) {
        return documentManager.searchDocuments(keyword, criteria);
    }

    /** Áp dụng Strategy Pattern sắp xếp danh sách Sách (FR-11, SRS Mục 5.3). */
    public void sortBooks(IBookSorter strategy, List<Book> books) {
        documentManager.setSorter(strategy);
        documentManager.executeSort(books);
    }

    /** Lưu thủ công (ví dụ sau khi thay đổi hàng đợi đặt trước). */
    public void save() {
        documentManager.saveData();
    }
}
