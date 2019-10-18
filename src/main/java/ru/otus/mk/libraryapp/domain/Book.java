package ru.otus.mk.libraryapp.domain;


import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Data
public class Book {
    private long id;
    private final String bookName;
    private final int issueYear;
    private final List<Author> authors = new ArrayList<>();
    private final List<Genre> genres = new ArrayList<>();

    public void linkToGenre(Genre genre) {
        if( !genres.contains(genre) ) {
            genres.add(genre);
        }
    }

    public void linkToAuthor(Author author) {
        if( !authors.contains(author) ) {
            authors.add(author);
        }
    }

    public void deleteGenre(Genre genre) {
        genres.removeIf( (g) -> {
            if( g.getId() == genre.getId() )
                return true;
            else if( g.getGenreName().trim().toUpperCase().equals(genre.getGenreName().trim().toUpperCase()) )
                return true;
            return false;
        });
    }

    public void deleteAuthor(Author author) {
        for(Author a: authors) {
            if( a.getId() == author.getId() )
                authors.remove(a);
            else if( a.getLastName().trim().toUpperCase().equals(author.getLastName().trim().toUpperCase())
                    && a.getFirstName().trim().toUpperCase().equals(author.getFirstName().trim().toUpperCase())
                    && (a.getMiddleName() == null || a.getMiddleName().isEmpty() || a.getMiddleName().trim().toUpperCase().equals(author.getMiddleName().trim().toUpperCase()))
            )
                authors.remove(a);
        }
    }

    public void clearGenreList() {
        genres.clear();
    }

    public void clearAuthorList() {
        authors.clear();
    }

    public void addAllGenres(List<Genre> genres) {
        if( genres != null && genres.size()!=0 )
            this.genres.addAll(genres);
    }

    public void addAllAuthors(List<Author> authors) {
        if( authors != null && authors.size()!=0 )
            this.authors.addAll(authors);
    }
}
