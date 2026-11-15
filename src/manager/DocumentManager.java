package manager;

import interfaces.IBookSorter;
import interfaces.IFileService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import model.Book;
import model.Document;
import model.EBook;
import model.Magazine;
import model.Thesis;
import strategy.SortByTitleAscending;
import util.FileUtil;
import util.TextUtil;
import util.Validator;

/**
 * Lớp quản lý danh mục Tài liệu (SRS Mục 5.1 & 5.3 - FR-03, FR-04, FR-07, FR-11).
 * Kết hợp Strategy Pattern (IBookSorter) để sắp xếp sách và hỗ trợ CRUD, Tìm kiếm tức thì, Đọc/Ghi File.
 */
public class DocumentManager implements IFileService {
    private static final String DATA_FILE = "data/documents.csv";

    private final List<Document> documents;
    private IBookSorter sorter;

    public DocumentManager() {
        this.documents = new ArrayList<>();
        this.sorter = new SortByTitleAscending();
    }

    // =========================================================================
    // 1. SẮP XẾP VỚI STRATEGY PATTERN (Khớp Sơ đồ lớp Mục 5.3)
    // =========================================================================

    public IBookSorter getSorter() {
        return sorter;
    }

    public void setSorter(IBookSorter sorter) {
        this.sorter = Validator.requireNonNull(sorter, "Chiến lược sắp xếp");
    }

    public void executeSort(List<Book> books) {
        Validator.requireNonNull(books, "Danh sách sách");
        this.sorter.sort(books);
    }

    // =========================================================================
    // 2. QUẢN LÝ DANH MỤC TÀI LIỆU - CRUD (FR-03, UC-02)
    // =========================================================================

    public List<Document> getDocuments() {
        return documents;
    }

    public List<Book> getAllBooks() {
        return documents.stream()
                .filter(doc -> doc instanceof Book)
                .map(doc -> (Book) doc)
                .collect(Collectors.toList());
    }

    public Document findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        String target = id.trim();
        return documents.stream()
                .filter(doc -> doc.getId().equalsIgnoreCase(target))
                .findFirst()
                .orElse(null);
    }

    public boolean addDocument(Document doc) {
        Validator.requireNonNull(doc, "Tài liệu");
        if (findById(doc.getId()) != null) {
            throw new IllegalArgumentException("Mã tài liệu đã tồn tại trong hệ thống: " + doc.getId());
        }
        documents.add(doc);
        return true;
    }

    public boolean updateDocument(Document updatedDoc) {
        Validator.requireNonNull(updatedDoc, "Tài liệu cập nhật");
        for (int i = 0; i < documents.size(); i++) {
            if (documents.get(i).getId().equalsIgnoreCase(updatedDoc.getId())) {
                documents.set(i, updatedDoc);
                return true;
            }
        }
        return false;
    }

    public boolean removeDocument(String id) {
        String cleanId = Validator.requireText(id, "Mã tài liệu cần xóa");
        return documents.removeIf(doc -> doc.getId().equalsIgnoreCase(cleanId));
    }

    // =========================================================================
    // 3. TÌM KIẾM TỨC THÌ - LIVE SEARCH (FR-04, UC-03)
    // =========================================================================

    public List<Document> searchDocuments(String keyword, String criteria) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(documents);
        }
        return documents.stream()
                .filter(doc -> matchesCriteria(doc, keyword, criteria))
                .collect(Collectors.toList());
    }

    private boolean matchesCriteria(Document doc, String keyword, String criteria) {
        if ("Mã ID".equalsIgnoreCase(criteria)) {
            return TextUtil.containsKeyword(doc.getId(), keyword);
        } else if ("Tiêu đề".equalsIgnoreCase(criteria)) {
            return TextUtil.containsKeyword(doc.getTitle(), keyword);
        } else if ("Tác giả".equalsIgnoreCase(criteria)) {
            if (doc instanceof Book) {
                return TextUtil.containsKeyword(((Book) doc).getAuthor(), keyword);
            } else if (doc instanceof Thesis) {
                return TextUtil.containsKeyword(((Thesis) doc).getAdvisor(), keyword);
            }
            return false;
        } else {
            // Mặc định: Tìm theo Tất cả (Mã ID, Tiêu đề, Tác giả/GVHD)
            if (TextUtil.containsKeyword(doc.getId(), keyword)
                    || TextUtil.containsKeyword(doc.getTitle(), keyword)) {
                return true;
            }
            if (doc instanceof Book) {
                return TextUtil.containsKeyword(((Book) doc).getAuthor(), keyword);
            }
            if (doc instanceof Thesis) {
                return TextUtil.containsKeyword(((Thesis) doc).getAdvisor(), keyword);
            }
            return false;
        }
    }

    // =========================================================================
    // 4. ĐỒNG BỘ DỮ LIỆU FILE CSV (FR-07)
    // =========================================================================

    @Override
    public void loadData() {
        documents.clear();
        List<String> lines = FileUtil.readLines(DATA_FILE);
        for (String line : lines) {
            String[] parts = line.split(";", -1);
            if (parts.length < 4) {
                continue;
            }
            try {
                String type = parts[0].trim().toUpperCase();
                String id = parts[1].trim();
                String title = parts[2].trim();
                int year = Integer.parseInt(parts[3].trim());

                switch (type) {
                    case "BOOK":
                        if (parts.length >= 7) {
                            Book book = new Book(
                                    id, title, year,
                                    parts[4].trim(),
                                    parts[5].trim(),
                                    Integer.parseInt(parts[6].trim())
                            );
                            if (parts.length >= 8 && !parts[7].trim().isEmpty()) {
                                for (String memberId : parts[7].split(",")) {
                                    if (!memberId.trim().isEmpty()) {
                                        book.getWaitingList().offer(memberId.trim().toUpperCase());
                                    }
                                }
                            }
                            documents.add(book);
                        }
                        break;
                    case "MAGAZINE":
                        if (parts.length >= 7) {
                            documents.add(new Magazine(
                                    id, title, year,
                                    Integer.parseInt(parts[4].trim()),
                                    Integer.parseInt(parts[5].trim()),
                                    Integer.parseInt(parts[6].trim())
                            ));
                        }
                        break;
                    case "THESIS":
                        if (parts.length >= 7) {
                            documents.add(new Thesis(
                                    id, title, year,
                                    parts[4].trim(),
                                    parts[5].trim(),
                                    parts[6].trim()
                            ));
                        }
                        break;
                    case "EBOOK":
                        if (parts.length >= 7) {
                            documents.add(new EBook(
                                    id, title, year,
                                    parts[4].trim(),
                                    Double.parseDouble(parts[5].trim()),
                                    parts[6].trim()
                            ));
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                System.err.println("Bỏ qua dòng dữ liệu tài liệu lỗi: " + line + " (" + e.getMessage() + ")");
            }
        }
    }

    @Override
    public boolean saveData() {
        List<String> lines = new ArrayList<>();
        for (Document doc : documents) {
            if (doc instanceof Book) {
                Book b = (Book) doc;
                String queueStr = String.join(",", b.getWaitingList());
                lines.add(String.join(";",
                        "BOOK", b.getId(), b.getTitle(), String.valueOf(b.getPublicationYear()),
                        b.getAuthor(), b.getPublisher(), String.valueOf(b.getQuantity()), queueStr));
            } else if (doc instanceof Magazine) {
                Magazine m = (Magazine) doc;
                lines.add(String.join(";",
                        "MAGAZINE", m.getId(), m.getTitle(), String.valueOf(m.getPublicationYear()),
                        String.valueOf(m.getIssueNumber()), String.valueOf(m.getPublishMonth()),
                        String.valueOf(m.getQuantity())));
            } else if (doc instanceof Thesis) {
                Thesis t = (Thesis) doc;
                lines.add(String.join(";",
                        "THESIS", t.getId(), t.getTitle(), String.valueOf(t.getPublicationYear()),
                        t.getUniversity(), t.getAdvisor(), t.getMajor()));
            } else if (doc instanceof EBook) {
                EBook e = (EBook) doc;
                lines.add(String.join(";",
                        "EBOOK", e.getId(), e.getTitle(), String.valueOf(e.getPublicationYear()),
                        e.getDownloadUrl(), String.valueOf(e.getFileSizeMb()), e.getFormat()));
            }
        }
        return FileUtil.writeLines(DATA_FILE, lines);
    }
}
