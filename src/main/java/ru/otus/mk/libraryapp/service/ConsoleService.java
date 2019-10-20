package ru.otus.mk.libraryapp.service;

import org.springframework.stereotype.Service;
import ru.otus.mk.libraryapp.domain.Author;
import ru.otus.mk.libraryapp.domain.Book;
import ru.otus.mk.libraryapp.domain.Genre;

import java.util.List;

import static java.util.Optional.ofNullable;

@Service
public class ConsoleService {
    public void printHelp() {
        System.out.println("Для работы необходимо залогиниться (команда l  или login)");
        System.out.println("Формат: l username password");
        System.out.println("Добавление или поиск жанра: ag <название жанра>");
        System.out.println("Информация по всем жанрам: pg");
        System.out.println("Добавление или поиск автора: аа <Фамилия> <Имя> <Отчетво>");
        System.out.println("Информация по все авторам: pa");
        System.out.println("Добавление новой книги: anb <Название> <год издания> <ИД автора> <ИД жанра>");
        System.out.println("Информация по всем книгам: pab");
    }

    public void printGenreList(List<Genre> genres) {
        System.out.println("Жанры доступные на данный момент\n");
        for(Genre genre : genres) {
            System.out.println(String.format("%d\t\t - \t\t%s", genre.getId(), genre.getGenreName()));
        }
    }

    public void printGenre(Genre genre) {
        System.out.println(String.format("Информация по указанному жанру:\n\t %d \t - \t %s\n",genre.getId(), genre.getGenreName()));
    }

    public void printAuthorList(List<Author> authors) {
        System.out.println("Жанры доступные на данный момент\n");
        for(Author author : authors) {
            System.out.println(String.format("%d\t\t - \t\t%s %s %s", author.getId(), author.getLastName(), author.getFirstName(), ofNullable(author.getMiddleName()).orElse("")));
        }
    }

    public void printBookInfo(Book book) {
        System.out.println("Информация по введенной книге\n");
        System.out.println(String.format("ID - %d\nНазвание - %s\nГод издания - %d\n", book.getId(), book.getBookName(), book.getIssueYear()));
        System.out.println("Относится к жанрам:");
        for(Genre genre : book.getGenres()) {
            System.out.println(genre.getGenreName());
        }
        System.out.println("Авторы:");
        for(Author author : book.getAuthors()) {
            System.out.println(String.format("\t %s %s %s", author.getLastName(), author.getFirstName(), ofNullable(author.getMiddleName()).orElse("")));
        }
    }

    public void printAllBooks(List<Book> books) {
        System.out.println(" Информация по имеющимся книгам");
        for(Book book : books)
            printBookInfo(book);
    }
}
