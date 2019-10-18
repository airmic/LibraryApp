package ru.otus.mk.libraryapp.dao.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.mk.libraryapp.dao.genre.GenreDao;
import ru.otus.mk.libraryapp.domain.Book;
import ru.otus.mk.libraryapp.domain.Genre;

import java.util.*;

@Repository
public class BookGenreRelationsImpl implements BookGenreRelations {

    private final Map<Long, List<Genre>> bookGenresMap;
    private final GenreDao genreDao;
    private final NamedParameterJdbcOperations op;

    @Autowired
    public BookGenreRelationsImpl(NamedParameterJdbcOperations op, GenreDao genreDao) {
        this.op = op;
        this.genreDao = genreDao;
        bookGenresMap = getBookGenreRelations();
    }

    private Map<Long, List<Genre>> getBookGenreRelations() {
        final String sql = "select * from genre_book";
        return op.query(sql, rs -> {
            return new HashMap<Long, List<Genre>>() {{
                while ( rs.next() ) {
                    List<Genre> bookGenreList = get(rs.getLong("book_id"));
                    if( bookGenreList == null ) {
                        bookGenreList = new ArrayList<>();
                        put(rs.getLong("book_id"), bookGenreList);
                    }
                    bookGenreList.add(genreDao.getByID(rs.getLong("genre_id")));


                }
            }};
        } );
    }

    @Override
    public void addGenreToBook(Book book, Genre genre) {
        List<Genre> bgl = bookGenresMap.get(book.getId());
        if( bgl != null && bgl.contains(genre) )
            throw new RuntimeException("Для данной книги указанный жанр уже добален");

        final String sql = "insert into genre_book (book_id, genre_id) values (:book_id, :genre_id)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("book_id", book.getId());
        params.addValue("genre_id", genre.getId());
        op.update(sql, params);

        if( bgl == null ) {
            bgl = new ArrayList<>();
            bookGenresMap.put(book.getId(), bgl);
        }
        bgl.add(genre);

    }

    @Override
    public void deleteGenreFromBook(Book book, Genre genre) {
        List<Genre> bgl = bookGenresMap.get(book.getId());
        if( bgl != null && !bgl.contains(genre) )
            throw new RuntimeException("Для данной книги указанный жанр не привязан");

        final String sql = "delete from genre_book where book_id = :book_id and genre_id = :genre_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("book_id", book.getId());
        params.addValue("genre_id", genre.getId());
        op.update(sql, params);

    }

    @Override
    public void clearGenresfromBook(Book book) {
        bookGenresMap.get(book.getId()).forEach( genre -> deleteGenreFromBook(book, genre) );
        bookGenresMap.get(book.getId()).clear();
    }

    @Override
    public List<Genre> getGenres(Book book) {
        return bookGenresMap.get(book.getId());
    }
}
