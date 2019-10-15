package ru.otus.mk.libraryapp.dao.author;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.mk.libraryapp.domain.Author;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuthorDaoImp")
@JdbcTest
@Import(AuthorDaoImpl.class)
class AuthorDaoImplTest {

    @Autowired
    AuthorDaoImpl authorDao;

    @DisplayName("Добавление нового автора")
    @Test
    void insert() {
        Author author = new Author("Иванов","Сергей", "Петрович");
        authorDao.insert(author);
        assertNotNull(author.getId(), () -> "Идентификатор новой записи не должен быть НУЛОМ");
    }

    @DisplayName("Список авторов")
    @Test
    void getAuthorList() {
        List<Author> authors = authorDao.getAuthorList();
        assertEquals(authors.size(), 5, () -> "Количество авторов должно быть 5");
    }

    @DisplayName("Поиск автора по фамилии")
    @Test
    void getByFIO() {
        List<Author> authors = authorDao.getByFIO("Шекспир", "Вильям", "");
        assertEquals(authors.size(), 1, () -> "Должен был найден Шекспир");
    }

    @DisplayName("Поиск автора по ИД")
    @Test
    void getByID() {
        Author author = authorDao.getByID(1);
        assertEquals(author.getLastName(),"Грибоедов", () -> "Должен найтись Грибоедов А.С.");
    }
}