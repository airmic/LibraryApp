package ru.otus.mk.libraryapp.dao.genre;

import ru.otus.mk.libraryapp.domain.Genre;

import java.util.List;

public interface GenreDao {
    void insert(final Genre genre);
    List<Genre> getAllGenreList();
    Genre getByName(final String name);
    Genre getByID(final long id);
    Genre getByCachedID(final long id);
    void reloadGenres();
    List<Genre> getLinkedGenreList();
}
