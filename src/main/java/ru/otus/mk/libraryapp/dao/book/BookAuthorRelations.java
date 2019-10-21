package ru.otus.mk.libraryapp.dao.book;

import ru.otus.mk.libraryapp.domain.Author;
import ru.otus.mk.libraryapp.domain.Book;

import java.util.List;
import java.util.Map;

public interface BookAuthorRelations {

    Map<Long, List<Author>> getBookAuthorRelations() ;
    void addAuthorToBook(Book book, Author author, int order_num);
    void deleteAuthorFromBook(Book book, Author author);
    void clearAuthorsfromBook(Book book);
    List<Author> getAuthors(Book book);

}
