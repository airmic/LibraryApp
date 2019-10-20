package ru.otus.mk.libraryapp.dao.genre;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.mk.libraryapp.domain.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GenreDaoImpl")
@JdbcTest
@Import(GenreDaoImpl.class)
class GenreDaoImplTest {

    @Autowired
    GenreDaoImpl genreDao;

    @DisplayName("Добавление нового жанра")
    @Test
    void insert() {
        Genre genre = new Genre("Фантастика");
        genreDao.insert( genre  );
        assertAll("Проверка на добавление нового жанра"
            , () -> assertNotNull( genre, () -> "Объект не должен быть равен НУЛУ" )
            , () -> assertNotNull( genre.getId(), () -> "Идентификатор объекта не должен быть равен НУЛУ" )
        );
    }


    @Test
    void getAuthorList() {
        List<Genre> genres = genreDao.getAllGenreList();
        assertAll("Проверка на количество жанров"
                , () -> assertNotNull( genres, () -> "Объект не должен быть равен НУЛУ" )
                , () -> assertEquals( genres.size(), 3, () -> "Количество жанров должно быть 3" )
        );
    }

    @Test
    void getByName() {
        Genre genre = genreDao.getByName("Драма");
        assertAll("Проверка на поиск по имени"
                , () -> assertNotNull( genre, () -> "Объект не должен быть равен НУЛУ" )
                , () -> assertEquals( genre.getId(), 1, () -> "Идентификатор жанра должен быть равен 1" )
        );
    }

    @Test
    void getByID() {
        Genre genre = genreDao.getByID(1);
        assertAll("Проверка на поиск по имени"
                , () -> assertNotNull( genre, () -> "Объект не должен быть равен НУЛУ" )
                , () -> assertEquals( genre.getGenreName(), "Драма", () -> "Название жанра должено быть \"Драма\'" )
        );
    }
}