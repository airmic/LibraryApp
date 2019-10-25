drop table if exists genre_book;
drop table if exists author_book;
drop table if exists  genres;
drop table if exists authors;
drop table if exists books;


create table genres(genre_id number auto_increment primary key
                   , genre_name varchar2(50) not null
                   , unique key genres_unq1 (genre_name));
create table books(book_id number auto_increment primary key
                  , book_name varchar2(50) not null
                  , issue_year number not null);
create table genre_book(genre_id number not null
                       , book_id number not null
                       , foreign key (genre_id) references genres(genre_id)
                       , foreign key (book_id) references books(book_id));

create table authors(author_id number auto_increment primary key
                    , first_name varchar2(100) not null
                    , last_name varchar2(100) not null
                    , middle_name varchar2(100));
create table author_book(author_id number not null
                        , book_id number not null
                        , order_num number
                        , foreign key (author_id) references authors(author_id)
                        , foreign key (book_id) references books(book_id));