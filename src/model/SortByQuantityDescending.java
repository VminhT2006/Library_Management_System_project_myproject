package model;

import java.util.Comparator;
import java.util.List;

public class SortByQuantityDescending implements IBookSorter {
    @Override
    public void sort(List<Book> books) {
        books.sort(Comparator.comparingInt(Book::getQuantity).reversed());
    }
}
