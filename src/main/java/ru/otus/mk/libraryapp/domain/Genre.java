package ru.otus.mk.libraryapp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class Genre {
    private long id;
    private final String genreName;
}
