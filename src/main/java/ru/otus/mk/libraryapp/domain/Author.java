package ru.otus.mk.libraryapp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class Author {
    private  long id;
    private final String lastName;
    private final String firstName;
    private String middleName;

    public Author(String lastName, String firstName, String middleName) {
        this(lastName, firstName);
        this.middleName = middleName;
    }
}
