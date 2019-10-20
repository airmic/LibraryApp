package ru.otus.mk.libraryapp.dao.book;

import ru.otus.mk.libraryapp.domain.Book;
import ru.otus.mk.libraryapp.domain.Genre;

import java.util.List;

public interface BookGenreRelations {

    void addGenreToBook(Book book, Genre genre);
    void deleteGenreFromBook(Book book, Genre genre);
    void clearGenresfromBook(Book book);
    List<Genre> getGenres(Book book);
    List<Genre> getGenresFromDB(Book book);
    void reloadRelations();

}
