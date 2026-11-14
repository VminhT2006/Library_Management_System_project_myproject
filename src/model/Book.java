package model;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Lớp Book đại diện cho Sách truyền thống, kế thừa từ Document (SRS Mục 5.2).
 * Tích hợp Hàng đợi đặt trước FIFO (Reservation Queue - FR-12, UC-07).
 */
public class Book extends Document {
    public static final double LATE_FEE_PER_DAY = 5000.0;

    private String author;
    private String publisher;
    private int quantity;
    private Queue<String> waitingList;

    public Book() {
        super();
        this.waitingList = new LinkedList<>();
    }

    public Book(String id, String title, int publicationYear, String author, String publisher, int quantity) {
        super(id, title, publicationYear);
        setAuthor(author);
        setPublisher(publisher);
        setQuantity(quantity);
        this.waitingList = new LinkedList<>();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên tác giả không được để trống!");
        }
        this.author = author.trim();
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        if (publisher == null || publisher.trim().isEmpty()) {
            throw new IllegalArgumentException("Nhà xuất bản không được để trống!");
        }
        this.publisher = publisher.trim();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Số lượng sách không được âm!");
        }
        this.quantity = quantity;
    }

    public Queue<String> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(Queue<String> waitingList) {
        if (waitingList == null) {
            throw new IllegalArgumentException("Hàng đợi đặt trước không được để rỗng (null)!");
        }
        this.waitingList = waitingList;
    }

    @Override
    public boolean canBorrow() {
        return this.quantity > 0;
    }

    @Override
    public double calculateLateFee(int days) {
        return days > 0 ? days * LATE_FEE_PER_DAY : 0.0;
    }

    @Override
    public String getTypeName() {
        return "Sách";
    }
}
