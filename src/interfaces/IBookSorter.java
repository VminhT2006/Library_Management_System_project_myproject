package interfaces;

import java.util.List;
import model.Book;

/**
 * Strategy Interface cho chức năng sắp xếp danh mục Sách (SRS Mục 5.3 - FR-11).
 */
public interface IBookSorter {
    void sort(List<Book> books);
}
