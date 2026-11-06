package model;
import java.time.LocalDate;

public class Book {
    private String bookId, title, author, publisher;
    private int publicationYear, quantity;

    public Book(String bookId, String title, String author, String publisher, int publicationYear, int quantity) {
        setBookId(bookId);
        setTitle(title);
        setAuthor(author);
        setPublisher(publisher);
        setPublicationYear(publicationYear);
        setQuantity(quantity);
    }

    // getter và setter BookID
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sách (ID) không được để trống!");
        }
        this.bookId = bookId.trim().toUpperCase();
    }

    // getter và setter title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sách không được để trống!");
        }
        this.title = title.trim();
    }

    // getter và setter author
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        //xu li loi dau vao(de trong)
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên tác giả không được để trống!");
        }
        this.author = author.trim();
    }

    // getter và setter nhà xuất bản
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        if (publisher == null || publisher.trim().isEmpty()) {
            throw new IllegalArgumentException("Nhà xuất bản không được để trống!");
        }
        this.publisher = publisher.trim();
    }

    // getter và setter năm xuất bản
    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        int currentYear = LocalDate.now().getYear();
        if (publicationYear < 1500 || publicationYear > currentYear) {
            throw new IllegalArgumentException("Năm xuất bản phải từ 1500 đến " + currentYear + "!");
        }
        this.publicationYear = publicationYear;
    }

    // getter và setter số lượng
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        // mục 5.2 yêu cầu kiểm tra số lượng không được âm ngay trong setter
        if (quantity < 0) {
            throw new IllegalArgumentException("Số lượng sách không được là số âm");
        }
        this.quantity = quantity;
    }

    // Định dạng dòng hiển thị dạng bảng trên Console
    public String toTableRow() {
        return String.format("| %-8s | %-28s | %-20s | %-15s | %-6d | %-8d |",
                bookId,
                truncate(title, 28),
                truncate(author, 20),
                truncate(publisher, 15),
                publicationYear,
                quantity);
    }

    // Chuyển đối tượng thành 1 dòng CSV để lưu xuống file books.csv
    public String toCsvString() {
        return String.join(",", bookId, title, author, publisher,
                String.valueOf(publicationYear), String.valueOf(quantity));
    }

    //rut gon lai ten sach de hien thi o Console
    private String truncate(String text, int maxLength) {
        if (text.length() <= maxLength)
            return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}
