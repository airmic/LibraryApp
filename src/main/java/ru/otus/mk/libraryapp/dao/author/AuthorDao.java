package ru.otus.mk.libraryapp.dao.author;


import ru.otus.mk.libraryapp.domain.Author;

import java.util.List;

public interface AuthorDao {
    void insert(final Author author);
    List<Author> getAuthorList();
    List<Author> getByFIO(final String lastName, final String firstName, final String middleName);
    Author getByID(final long id);
}
