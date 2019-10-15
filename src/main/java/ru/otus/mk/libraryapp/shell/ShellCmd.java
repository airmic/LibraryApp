package ru.otus.mk.libraryapp.shell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.stereotype.Service;
import ru.otus.mk.libraryapp.domain.Author;
import ru.otus.mk.libraryapp.domain.Book;
import ru.otus.mk.libraryapp.domain.Genre;
import ru.otus.mk.libraryapp.service.BookService;
import ru.otus.mk.libraryapp.service.ConsoleService;

import java.util.List;

@ShellComponent
public class ShellCmd {

    private boolean isAuth=false;
    private final BookService bookService;
    private final ConsoleService consoleService;

    @Autowired
    public ShellCmd(BookService bookService, ConsoleService consoleService) {
        this.bookService = bookService;
        this.consoleService = consoleService;
    }

    @ShellMethod(value="help", key = {"h","hlp"})
    public void help() {
        consoleService.printHelp();
    }

    @ShellMethod(value="login", key = {"l","lgn"})
    public void login(@ShellOption String login, @ShellOption String password) {
        if( login!=null && !login.isEmpty() && password.equals("123"))
            isAuth = true;
    }

    @ShellMethod(value="add genre", key = {"ag"})
    @ShellMethodAvailability("isAuthFun")
    public void getGenre(@ShellOption String genreName) {
        Genre genre = bookService.getFindedOrCreatedGenreByName(genreName);
        consoleService.printGenre(genre);
    }

    @ShellMethod(value="print genres", key = {"pg"})
    @ShellMethodAvailability("isAuthFun")
    public void printGenres() {
        List<Genre> genres = bookService.getAllGenres();
        consoleService.printGenreList(genres);
    }

    @ShellMethod(value="add author", key = {"aa"})
    @ShellMethodAvailability("isAuthFun")
    public void getAuthorsList(@ShellOption String lastName, @ShellOption String firstName, @ShellOption(defaultValue = "") String middleName) {
        List<Author> authors = bookService.getFindedOrCreatedAuthorsByName(lastName, firstName, middleName);
        consoleService.printAuthorList(authors);
    }

    @ShellMethod(value="print all authors", key = {"pa"})
    @ShellMethodAvailability("isAuthFun")
    public void printAuthors() {
        List<Author> authors = bookService.getAllAuthors();
        consoleService.printAuthorList(authors);
    }

    @ShellMethod(value="add new book", key = {"anb"})
    @ShellMethodAvailability("isAuthFun")
    public void addBook(@ShellOption String bookName, @ShellOption Integer issueYear, @ShellOption long authorId, @ShellOption long genreId, @ShellOption int cnt) {
        Book book = bookService.createNewBookIsSameNotExist(bookName, issueYear, authorId, genreId, cnt);
        consoleService.printBookInfo(book);
    }

    @ShellMethod(value="print all books", key = {"pab"})
    @ShellMethodAvailability("isAuthFun")
    public void printAllBooks() {
        List<Book> books = bookService.getAllBooks();
        consoleService.printAllBooks(books);
    }

    private Availability isAuthFun() {
        return isAuth ? Availability.available() : Availability.unavailable("Необходимо залогиниться");
    }
}
