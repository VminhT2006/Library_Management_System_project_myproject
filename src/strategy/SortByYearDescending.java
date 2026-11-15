package strategy;

import interfaces.IBookSorter;
import java.util.Comparator;
import java.util.List;
import model.Book;
import util.Validator;

/**
 * Chiến lược sắp xếp danh sách Sách theo Năm xuất bản giảm dần - Mới nhất trước (SRS Mục 5.3).
 */
public class SortByYearDescending implements IBookSorter {
    @Override
    public void sort(List<Book> books) {
        Validator.requireNonNull(books, "Danh sách sách");
        books.sort(Comparator.comparingInt(Book::getPublicationYear).reversed());
    }
}
