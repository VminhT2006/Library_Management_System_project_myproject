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
    private static final String DATA_FILE = "data/documents-v2.csv";
    private static final String HEADER =
            "id,type,title,publicationYear,author,publisher,quantity,waitingList,"
            + "issueNumber,publishMonth,university,advisor,major,downloadUrl,fileSizeMb,format";

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
        List<String> lines = FileUtil.readLines(FileUtil.resolveForRead(DATA_FILE));
        for (String line : lines) {
            if (line.equals(HEADER)) {
                continue; // bỏ qua dòng tiêu đề
            }
            List<String> p = FileUtil.parseCsvLine(line);
            if (p.size() < 8) {
                continue;
            }
            try {
                String id = p.get(0).trim();
                String type = p.get(1).trim().toUpperCase();
                String title = p.get(2).trim();
                int year = Integer.parseInt(p.get(3).trim());

                switch (type) {
                    case "BOOK": {
                        Book book = new Book(id, title, year,
                                p.get(4).trim(), p.get(5).trim(),
                                Integer.parseInt(p.get(6).trim()));
                        String waiting = p.get(7).trim();
                        if (!waiting.isEmpty()) {
                            for (String memberId : waiting.split("\\|")) {
                                if (!memberId.trim().isEmpty()) {
                                    book.getWaitingList().offer(memberId.trim().toUpperCase());
                                }
                            }
                        }
                        documents.add(book);
                        break;
                    }
                    case "MAGAZINE":
                        documents.add(new Magazine(id, title, year,
                                Integer.parseInt(p.get(8).trim()),
                                Integer.parseInt(p.get(9).trim()),
                                Integer.parseInt(p.get(6).trim())));
                        break;
                    case "THESIS":
                        documents.add(new Thesis(id, title, year,
                                p.get(10).trim(), p.get(11).trim(), p.get(12).trim()));
                        break;
                    case "EBOOK":
                        documents.add(new EBook(id, title, year,
                                p.get(13).trim(),
                                Double.parseDouble(p.get(14).trim()),
                                p.get(15).trim()));
                        break;
                    default:
                        break;
                }
            } catch (RuntimeException e) {
                System.err.println("Bỏ qua dòng dữ liệu tài liệu lỗi: " + line + " (" + e.getMessage() + ")");
            }
        }
    }

    @Override
    public boolean saveData() {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Document doc : documents) {
            if (doc instanceof Book) {
                Book b = (Book) doc;
                String queueStr = String.join("|", b.getWaitingList());
                lines.add(FileUtil.toCsvLine(b.getId(), "Book", b.getTitle(),
                        String.valueOf(b.getPublicationYear()), b.getAuthor(), b.getPublisher(),
                        String.valueOf(b.getQuantity()), queueStr, "", "", "", "", "", "", "", ""));
            } else if (doc instanceof Magazine) {
                Magazine m = (Magazine) doc;
                lines.add(FileUtil.toCsvLine(m.getId(), "Magazine", m.getTitle(),
                        String.valueOf(m.getPublicationYear()), "", "",
                        String.valueOf(m.getQuantity()), "",
                        String.valueOf(m.getIssueNumber()), String.valueOf(m.getPublishMonth()),
                        "", "", "", "", "", ""));
            } else if (doc instanceof Thesis) {
                Thesis t = (Thesis) doc;
                lines.add(FileUtil.toCsvLine(t.getId(), "Thesis", t.getTitle(),
                        String.valueOf(t.getPublicationYear()), "", "", "1", "", "", "",
                        t.getUniversity(), t.getAdvisor(), t.getMajor(), "", "", ""));
            } else if (doc instanceof EBook) {
                EBook e = (EBook) doc;
                lines.add(FileUtil.toCsvLine(e.getId(), "EBook", e.getTitle(),
                        String.valueOf(e.getPublicationYear()), "", "", "999", "", "", "", "", "", "",
                        e.getDownloadUrl(), String.valueOf(e.getFileSizeMb()), e.getFormat()));
            }
        }
        return FileUtil.writeLines(FileUtil.resolveForWrite(DATA_FILE), lines);
    }
}
