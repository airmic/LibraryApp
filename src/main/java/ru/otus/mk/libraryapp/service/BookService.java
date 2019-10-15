package ru.otus.mk.libraryapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.mk.libraryapp.dao.author.AuthorDao;
import ru.otus.mk.libraryapp.dao.book.BookDao;
import ru.otus.mk.libraryapp.dao.genre.GenreDao;
import ru.otus.mk.libraryapp.domain.Author;
import ru.otus.mk.libraryapp.domain.Book;
import ru.otus.mk.libraryapp.domain.Genre;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {
    private GenreDao genreDao;
    private AuthorDao authorDao;
    private BookDao bookDao;

    @Autowired
    public BookService(GenreDao genreDao, AuthorDao authorDao, BookDao bookDao) {
        this.authorDao = authorDao;
        this.genreDao = genreDao;
        this.bookDao = bookDao;
    }

    public Genre getFindedOrCreatedGenreByName(final String genre) {
        Genre ret = genreDao.getByName(genre);
        if( ret == null ) {
            ret = new Genre(genre);
            genreDao.insert(ret);
        }
        return ret;
    }

    public List<Author> getFindedOrCreatedAuthorsByName(final String lastName, final String firstName, final String middleName) {
        List<Author> authors = authorDao.getByFIO(lastName,firstName, middleName);
        if( authors.isEmpty() ) {
            Author author = new Author(lastName,firstName, middleName);
            authorDao.insert(author);
            return new ArrayList<Author>(){{ add(author); }};
        }
        return  authors;
    }

    public List<Genre> getAllGenres() {
        return genreDao.getGenreList();
    }

    public List<Author> getAllAuthors() {
        return authorDao.getAuthorList();
    }

    public Book  createNewBookIsSameNotExist(final String bookName, final Integer issueYear, final long authorId, final long genreId, final int cnt) {

        if( bookDao.getByName(bookName, issueYear).isEmpty() ) {
            Book book = new Book(bookName,issueYear);
            book.setCnt(cnt);
            book.linkToGenre(genreDao.getByID(genreId));
            book.linkToAuthor(authorDao.getByID(authorId));
            bookDao.insert(book);
            return book;
        } else
            throw new RuntimeException("Такая книга уже есть");
    }

    public List<Book> getAllBooks() {
        return bookDao.getBookList();
    }
}
