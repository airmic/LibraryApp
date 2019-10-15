package ru.otus.mk.libraryapp.dao.book;

import ru.otus.mk.libraryapp.domain.Book;
import ru.otus.mk.libraryapp.domain.Genre;

import java.util.List;

public interface BookDao {
    void insert(final Book book);
    void update(final Book book);
    List<Book> getBookList();
    List<Book> getByName(final String name, final Integer issue_year);
    Book getByID(final long id);
}
