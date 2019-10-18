package ru.otus.mk.libraryapp.dao.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.mk.libraryapp.dao.author.AuthorDao;
import ru.otus.mk.libraryapp.dao.author.AuthorDaoImpl;
import ru.otus.mk.libraryapp.dao.genre.GenreDao;
import ru.otus.mk.libraryapp.dao.genre.GenreDaoImpl;
import ru.otus.mk.libraryapp.domain.Author;
import ru.otus.mk.libraryapp.domain.Book;
import ru.otus.mk.libraryapp.domain.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class BookDaoImpl implements BookDao {
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;

    @Autowired
    public BookDaoImpl(NamedParameterJdbcOperations namedParameterJdbcOperations, AuthorDao authorDao, GenreDao genreDao) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.authorDao = authorDao;
        this.genreDao = genreDao;
    }

    private void createBookAuthorLink(long book_id, long author_id, int order_num) {
        final String sql = "insert into author_book(book_id,author_id, order_num) values(:book_id, :author_id, :order_num)";


        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("book_id", book_id);
        params.addValue("author_id", author_id);
        params.addValue("order_num", order_num);

        namedParameterJdbcOperations.update(sql, params);
    }

    private boolean isAuthorLinkExist(long book_id, long author_id) {
        final String sql = "select 1 from author_book where book_id=:book_id and author_id=:author_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("book_id", book_id);
        params.addValue("author_id", author_id);
        try {
            namedParameterJdbcOperations.queryForObject(sql, params, Byte.class);
        } catch (IncorrectResultSizeDataAccessException e) {
            return false;
        }
        return true;
    }


    private void linkBookToAurhorInDB(Book book) {
        checkBookIdentificator(book);

        final int[] idx = {0};
        book.getAuthors().forEach(author -> {
            if( author.getId() == 0) {
                authorDao.insert(author);
            }

            if( !isAuthorLinkExist(book.getId(), author.getId()) ) {
                createBookAuthorLink( book.getId(), author.getId(), idx[0]++ );
            }
        });
    }

    private void checkBookIdentificator( Book book) {
        if( book == null || book.getId() == 0) {
            throw new RuntimeException("Не найден идентификатор у книги");
        }
    }

    private void createBookGenreLink(long book_id, long genre_id) {
        final String sql = "insert into genre_book(book_id,genre_id) values(:book_id, :genre_id)";


        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("book_id", book_id);
        params.addValue("genre_id", genre_id);

        namedParameterJdbcOperations.update(sql, params);
    }

    private boolean isGenreLinkExist(long book_id, long genre_id) {
        final String sql = "select 1 from genre_book where book_id=:book_id and genre_id=:genre_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("book_id", book_id);
        params.addValue("genre_id", genre_id);
        try {
            namedParameterJdbcOperations.queryForObject(sql, params, Byte.class);
        } catch (IncorrectResultSizeDataAccessException e) {
            return false;
        }
        return true;
    }

    private void linkBookToGenreInDB(Book book) {
        checkBookIdentificator(book);
        book.getGenres().forEach((genre -> {
            if( genre.getId() == 0) {
                Genre findedGenre = genreDao.getByName(genre.getGenreName());
                if( findedGenre != null )
                    genre.setId(findedGenre.getId());
                else
                    genreDao.insert(genre);
            }

            if( !isGenreLinkExist(book.getId(), genre.getId()) ) {
                createBookGenreLink( book.getId(), genre.getId());
            }
        }));
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
        final String sql = "delete from author_book where book_id=:id";
        namedParameterJdbcOperations.update(sql, Collections.singletonMap("id",book.getId()));
    }

    private void deleteAllBookGenerLinks(Book book) {
        final String sql = "delete from genre_book where book_id=:id";
        namedParameterJdbcOperations.update(sql, Collections.singletonMap("id",book.getId()));
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
        deleteAllBookGenerLinks(book);
        linkBookToGenreInDB(book);
    }

    @Override
    public List<Book> getBookList() {
        final String sql = "select * from books ";
        return namedParameterJdbcOperations.query(sql, new BookRowMapper() );
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

    private List<Genre> getBookGenres(Book book) {
        final String sql = "select g.genre_id, g.genre_name\n" +
                "  from books b \n" +
                "  natural join genre_book gb\n" +
                "  natural join genres g\n" +
                " where b.book_id=:id";

        return namedParameterJdbcOperations.query(sql, Collections.singletonMap("id",book.getId()), new GenreDaoImpl.GenreRowMapper());
    }

    private List<Author> getBookAuthor(Book book) {
        final String sql = "select a.author_id, a.*\n" +
                "  from books b \n" +
                "  natural join author_book ab\n" +
                "  natural join authors a\n" +
                " where b.book_id=:id\n" +
                " order by ab.order_num nulls first";

        return namedParameterJdbcOperations.query(sql, Collections.singletonMap("id",book.getId()), new AuthorDaoImpl.AuthorRowMapper());
    }

    private class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Book book = new Book(rs.getString("book_name"), rs.getInt("issue_year"));
            book.setId(rs.getLong("book_id"));
            book.addAllGenres(getBookGenres(book));
            book.addAllAuthors(getBookAuthor(book));
            return book;
        }
    }
}
