package ru.otus.mk.libraryapp;

import org.h2.tools.Console;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
public class LibraryAppApplication {

    public static void main(String[] args) throws SQLException {
        SpringApplication.run(LibraryAppApplication.class, args);
        Console.main(args);
    }

}
