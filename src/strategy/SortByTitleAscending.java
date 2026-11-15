package strategy;

import interfaces.IBookSorter;
import java.util.Comparator;
import java.util.List;
import model.Book;
import util.Validator;

/**
 * Chiến lược sắp xếp danh sách Sách theo Tiêu đề tăng dần A -> Z (SRS Mục 5.3).
 */
public class SortByTitleAscending implements IBookSorter {
    @Override
    public void sort(List<Book> books) {
        Validator.requireNonNull(books, "Danh sách sách");
        books.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
    }
}
