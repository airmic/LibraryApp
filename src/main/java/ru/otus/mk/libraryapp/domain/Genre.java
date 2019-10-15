package ru.otus.mk.libraryapp.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Genre {
    private long id;
    private final String genreName;
}
