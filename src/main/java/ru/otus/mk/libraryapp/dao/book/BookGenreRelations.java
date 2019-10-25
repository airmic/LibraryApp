package ru.otus.mk.libraryapp.dao.book;

import ru.otus.mk.libraryapp.domain.Book;
import ru.otus.mk.libraryapp.domain.Genre;

import java.util.List;
import java.util.Map;

public interface BookGenreRelations {

    Map<Long, List<Genre>> getBookGenreRelations();
    void addGenreToBook(Book book, Genre genre);
    void deleteGenreFromBook(Book book, Genre genre);
    void clearGenresfromBook(Book book);
    List<Genre> getGenres(Book book);

}
