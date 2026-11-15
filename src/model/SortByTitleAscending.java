package model;

import java.util.Comparator;
import java.util.List;

public class SortByTitleAscending implements IBookSorter {
    @Override
    public void sort(List<Book> books) {
        books.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
    }
}
