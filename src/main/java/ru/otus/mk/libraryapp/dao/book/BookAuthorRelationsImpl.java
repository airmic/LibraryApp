package ru.otus.mk.libraryapp.dao.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.mk.libraryapp.dao.author.AuthorDao;
import ru.otus.mk.libraryapp.domain.Book;
import ru.otus.mk.libraryapp.domain.Author;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BookAuthorRelationsImpl implements BookAuthorRelations {

    private final Map<Long, List<Author>> bookAuthorsMap;
    private final AuthorDao authorDao;
    private final NamedParameterJdbcOperations op;

    @Autowired
    public BookAuthorRelationsImpl(NamedParameterJdbcOperations op, AuthorDao authorDao) {
        this.op = op;
        this.authorDao = authorDao;
        bookAuthorsMap = new HashMap<>();
        reloadRelations();
    }

    private Map<Long, List<Author>> getBookAuthorRelations() {
        final String sql = "select * from author_book";
        authorDao.reloadAuthors();
        return op.query(sql, rs -> {
            return new HashMap<Long, List<Author>>() {{
                while ( rs.next() ) {
                    List<Author> bookAuthorList = get(rs.getLong("book_id"));
                    if( bookAuthorList == null ) {
                        bookAuthorList = new ArrayList<>();
                        put(rs.getLong("book_id"), bookAuthorList);
                    }
                    bookAuthorList.add(authorDao.getByCachedID(rs.getLong("author_id")));


                }
            }};
        } );
    }

    @Override
    public void addAuthorToBook(Book book, Author Author, int order_num) {
        List<Author> bgl = bookAuthorsMap.get(book.getId());
        if( bgl != null && bgl.contains(Author) )
            throw new RuntimeException("Для данной книги указанный жанр уже добален");

        final String sql = "insert into author_book (book_id, author_id, order_num) values (:book_id, :author_id, :order_num)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("book_id", book.getId());
        params.addValue("author_id", Author.getId());
        params.addValue("order_num", order_num);
        op.update(sql, params);

        if( bgl == null ) {
            bgl = new ArrayList<>();
            bookAuthorsMap.put(book.getId(), bgl);
        }
        bgl.add(Author);

    }

    @Override
    public void deleteAuthorFromBook(Book book, Author Author) {
        List<Author> bgl = bookAuthorsMap.get(book.getId());
        if( bgl != null && !bgl.contains(Author) )
            throw new RuntimeException("Для данной книги указанный жанр не привязан");

        final String sql = "delete from author_book where book_id = :book_id and author_id = :author_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("book_id", book.getId());
        params.addValue("author_id", Author.getId());
        op.update(sql, params);

    }

    @Override
    public void clearAuthorsfromBook(Book book) {
        bookAuthorsMap.get(book.getId()).forEach( Author -> deleteAuthorFromBook(book, Author) );
        bookAuthorsMap.get(book.getId()).clear();
    }

    @Override
    public List<Author> getAuthors(Book book) {
        return bookAuthorsMap.get(book.getId());
    }

    @Override
    public List<Author> getAuthorsFromDB(Book book) {
        final String sql = "select author_id from author_book where book_id = :book_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("book_id", book.getId());
        return op.query(sql, params, (rs, rowNum) -> authorDao.getByID(rs.getLong("author_id")));
    }

    @Override
    public void reloadRelations() {
        bookAuthorsMap.clear();
        bookAuthorsMap.putAll(getBookAuthorRelations());
    }
}
