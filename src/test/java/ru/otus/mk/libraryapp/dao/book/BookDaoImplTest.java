package ru.otus.mk.libraryapp.dao.book;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.mk.libraryapp.dao.author.AuthorDaoImpl;
import ru.otus.mk.libraryapp.dao.genre.GenreDaoImpl;
import ru.otus.mk.libraryapp.domain.Author;
import ru.otus.mk.libraryapp.domain.Book;
import ru.otus.mk.libraryapp.domain.Genre;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BookDaoImp")
@JdbcTest
@Import({BookDaoImpl.class, AuthorDaoImpl.class, GenreDaoImpl.class})
class BookDaoImplTest {

    @Autowired
    private BookDaoImpl bookDao;

    @DisplayName("Добавление новой книги")
    @Test
    void insert() {
        Book book = new Book("1984", 2001);
        List<Author> authors = new ArrayList<>(1);
        authors.add(new Author("Оруэлл", "Джордж"));
        book.addAllAuthors(authors);
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre("Утопия"));
        genres.add(new Genre("Фантастика"));
        book.addAllGenres(genres);
        bookDao.insert(book);

        assertNotNull(book.getId(), () -> "После вставки у книги д.б. непустой ИД");

        Book book2 = bookDao.getByID(book.getId());
        assertEquals(book, book2, "Книги д.б. эквивалентны");
    }

    @DisplayName("Обновление книги")
    @Test
    void update() {

        Book book = bookDao.getByID(1);

        Genre newGenre = new Genre("Фантастика");
        Genre oldGenre = new Genre("Комедия");
        book.clearGenreList();
        book.linkToGenre(newGenre);

        bookDao.update(book);

        Book book2 = bookDao.getByID(1);

        assertAll("Обновление путем очиски списка жанров и добавления нового"
                , () -> assertEquals(book2.getGenres().size(), 1,"Размер списка жанров д.б. = 1")
                , () -> assertEquals(newGenre.getGenreName(),book2.getGenres().get(0).getGenreName(),"Должна быть \"Фантастика\"")
        );

        book.deleteGenre(newGenre);
        book.linkToGenre(oldGenre);
        bookDao.update(book);
        Book book3 = bookDao.getByID(1);

        assertAll("Обновление путем удаления старого жанра и добавления нового"
                , () -> assertEquals(book3.getGenres().size(), 1,"Размер списка жанров д.б. = 1")
                , () -> assertEquals(oldGenre.getGenreName(),book3.getGenres().get(0).getGenreName(),"Должна быть \"Фантастика\"")
        );

    }

    @DisplayName("Список книг")
    @Test
    void getBookList() {
        List<Book> books = bookDao.getBookList();
        assertAll(
                () -> assertNotNull(books, () -> "Вернулся NULL вместо списка книг")
                , () -> assertEquals(books.size(), 5, () -> "Должно вернуться 5 строк")
        );
    }

    @DisplayName("Поиск книги по названию")
    @Test
    void getByName() {
        List<Book> books = bookDao.getByNameAndYear("Вишневый сад", 1958);
        assertAll("книга \"Вишневый сад\""
                , () -> assertNotNull(books, () -> "Вернулся NULL вместо списка книг")
                , () -> assertEquals( books.size(), 1, () -> "Должна вернуться 1 запись")
                , () -> assertEquals( books.get(0).getAuthors().size(), 1)
                , () -> assertEquals( books.get(0).getGenres().size(), 1)
        );
    }

    @DisplayName("Поиск книги по идентификатору")
    @Test
    void getByID() {
        Book book = bookDao.getByID(1);
        assertAll("книга \"Горе от ума\""
                , () -> assertNotNull(book, () -> "Вернулся NULL вместо списка книг")
                , () -> assertEquals( book.getBookName().trim().toUpperCase(), "Горе от ума".trim().toUpperCase(), () -> "Должна вернуться Название \"Горе от ума\"")
                , () -> assertEquals( book.getAuthors().size(), 1, () -> "У книги должен быть только 1 автор")
                , () -> assertEquals( book.getAuthors().get(0).getLastName(), "Грибоедов", () -> "Фамилиия не совпадает")
                , () -> assertEquals( book.getAuthors().get(0).getFirstName(), "Александр", () -> "Имя не совпадает")
                , () -> assertEquals( book.getAuthors().get(0).getMiddleName(), "Сергеевич", () -> "Отчетство не совпадает")
                , () -> assertEquals( book.getGenres().size(), 1, () -> "Книга дожна относится к 1 жанру")
                , () -> assertEquals( book.getGenres().get(0).getGenreName(), "Комедия", () -> "Книга доджна относится жанру \"Комедия\"")
        );
    }
}