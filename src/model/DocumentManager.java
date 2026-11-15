package model;

import java.util.List;
import java.util.Objects;

public class DocumentManager {
    private IBookSorter sorter;

    public DocumentManager() {
        this.sorter = new SortByTitleAscending();
    }

    public void setSorter(IBookSorter sorter) {
        this.sorter = Objects.requireNonNull(sorter, "Chiến lược sắp xếp không được null!");
    }

    public void executeSort(List<Book> books) {
        Objects.requireNonNull(books, "Danh sách sách không được null!");
        sorter.sort(books);
    }
}
