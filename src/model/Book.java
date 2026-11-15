package model;

import java.util.LinkedList;
import java.util.Queue;
import util.Validator;

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
        this.author = Validator.requireText(author, "Tên tác giả");
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = Validator.requireText(publisher, "Nhà xuất bản");
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Validator.requireNonNegative(quantity, "Số lượng sách");
    }

    public Queue<String> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(Queue<String> waitingList) {
        this.waitingList = Validator.requireNonNull(waitingList, "Hàng đợi đặt trước");
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
