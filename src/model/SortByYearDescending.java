package model;

import java.util.Comparator;
import java.util.List;

public class SortByYearDescending implements IBookSorter {
    @Override
    public void sort(List<Book> books) {
        books.sort(Comparator.comparingInt(Book::getPublicationYear).reversed());
    }
}
