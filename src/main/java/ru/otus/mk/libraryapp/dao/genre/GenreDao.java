package ru.otus.mk.libraryapp.dao.genre;

import ru.otus.mk.libraryapp.domain.Genre;

import java.util.List;
import java.util.Map;

public interface GenreDao {
    void insert(final Genre genre);
    List<Genre> getAllGenreList();
    Genre getByName(final String name);
    Genre getByID(final long id);
    Map<Long,Genre> getLinkedGenreMap();
}
