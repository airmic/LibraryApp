package ru.otus.mk.libraryapp.dao.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.mk.libraryapp.dao.author.AuthorDao;
import ru.otus.mk.libraryapp.dao.genre.GenreDao;
import ru.otus.mk.libraryapp.domain.Author;
import ru.otus.mk.libraryapp.domain.Book;
import ru.otus.mk.libraryapp.domain.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class BookDaoImpl implements BookDao {
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;
    private final BookGenreRelations bookGenreRelations;
    private final BookAuthorRelations bookAuthorRelations;


    @Autowired
    public BookDaoImpl(NamedParameterJdbcOperations namedParameterJdbcOperations
            , AuthorDao authorDao, GenreDao genreDao
            , BookGenreRelations bookGenreRelations
            , BookAuthorRelations bookAuthorRelations) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.authorDao = authorDao;
        this.genreDao = genreDao;
        this.bookGenreRelations = bookGenreRelations;
        this.bookAuthorRelations = bookAuthorRelations;
    }

    private boolean isAuthorLinkExist(Book book, Author author) {
        List<Author> bookAuthor = bookAuthorRelations.getAuthors(book);
        if( bookAuthor == null)
            return false;
        return bookAuthor.contains(author);
    }


    private void linkBookToAurhorInDB(Book book) {
        checkBookIdentificator(book);

        final int[] idx = {0};
        book.getAuthors().forEach(author -> {
            if( author.getId() == 0) {
                authorDao.insert(author);
            }

            if( !isAuthorLinkExist(book, author) ) {
                bookAuthorRelations.addAuthorToBook(book, author, idx[0]++ );
            }
        });
    }

    private boolean isGenreLinkExist(Book book, Genre genre) {
        List<Genre> bookGenres = bookGenreRelations.getGenres(book);
        if( bookGenres == null)
            return false;
        return bookGenres.contains(genre);
    }

    private void linkBookToGenreInDB(Book book) {
        checkBookIdentificator(book);
        book.getGenres().forEach(genre -> {
            if( genre.getId() == 0) {
                Genre findedGenre = genreDao.getByName(genre.getGenreName());
                if( findedGenre != null )
                    genre.setId(findedGenre.getId());
                else
                    genreDao.insert(genre);
            }

            if( !isGenreLinkExist(book, genre) ) {
                bookGenreRelations.addGenreToBook(book,genre);
            }
        });
    }

    private void checkBookIdentificator( Book book) {
        if( book == null || book.getId() == 0) {
            throw new RuntimeException("Не найден идентификатор у книги");
        }
    }





    @Override
    public void insert(final Book book) {

        final String sql = "insert into books(book_name,issue_year) values(:book_name, :issue_year)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("book_name", book.getBookName());
        params.addValue("issue_year", book.getIssueYear());

        KeyHolder kh = new GeneratedKeyHolder();

        namedParameterJdbcOperations.update(sql, params, kh);
        book.setId( kh.getKey().longValue());

        linkBookToAurhorInDB(book);
        linkBookToGenreInDB(book);

    }

    private void deleteAllBookAuthorLinks(Book book) {
        bookAuthorRelations.clearAuthorsfromBook(book);
    }

    private void deleteAllBookGenreLinks(Book book) {
        bookGenreRelations.clearGenresfromBook(book);
    }

    @Override
    public void update(final Book book) {
        if( book == null || book.getId() == 0)
            throw new RuntimeException("Не найден идентификатор у книги");
        final String sql = "update books set book_name=:book_name, issue_year=:issue_year where book_id=:id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("book_name", book.getBookName());
        params.addValue("issue_year", book.getIssueYear());
        params.addValue("id", book.getId());

        namedParameterJdbcOperations.update(sql, params);

        deleteAllBookAuthorLinks(book);
        linkBookToAurhorInDB(book);

        deleteAllBookGenreLinks(book);
        linkBookToGenreInDB(book);
    }

    @Override
    public List<Book> getBookList() {
        final String sql = "select * from books ";
        return   namedParameterJdbcOperations.query(sql, rs -> {
            List<Book> bookList = new ArrayList<>();
            Map<Long, List<Genre>> bookGenresRelation = bookGenreRelations.getBookGenreRelations();
            Map<Long, List<Author>> bookAuthorsRelation = bookAuthorRelations.getBookAuthorRelations();
            while(rs.next()) {
                Book book = new Book(rs.getString("book_name"), rs.getInt("issue_year"));
                book.setId(rs.getLong("book_id"));
                book.addAllGenres(bookGenresRelation.get(book.getId()));
                book.addAllAuthors(bookAuthorsRelation.get(book.getId()));

                bookList.add( book );
            }
            return bookList;
        });
    }

    @Override
    public List<Book> getByNameAndYear(final String name, final Integer issueYear) {
        String sql = "select * from books where upper(trim(book_name))=:name and issue_year=:issue_year";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name.trim().toUpperCase());
        params.addValue("issue_year", issueYear);
        return namedParameterJdbcOperations.query(sql, params, new BookRowMapper() );
    }

    @Override
    public Book getByID(final long id) {
        final String sql = "select * from books where book_id=:id";
        Map<String, Object> params = Collections.singletonMap("id",id);
        Book book =  namedParameterJdbcOperations.queryForObject(sql, params, new BookRowMapper());
        return book;
    }



    private class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Book book = new Book(rs.getString("book_name"), rs.getInt("issue_year"));
            book.setId(rs.getLong("book_id"));
            book.addAllGenres(bookGenreRelations.getGenres(book));
            book.addAllAuthors(bookAuthorRelations.getAuthors(book));
            return book;
        }
    }
}
