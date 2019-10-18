insert into GENRES(GENRE_ID, GENRE_NAME) values (default, 'Драма');
insert into GENRES(GENRE_ID, GENRE_NAME) values (default, 'Трагедия');
insert into GENRES(GENRE_NAME) values ('Комедия');

insert into BOOKS(BOOK_NAME,ISSUE_YEAR) values ('Горе от ума', 1957);
insert into BOOKS(BOOK_NAME,ISSUE_YEAR) values ('Вишневый сад', 1958);
insert into BOOKS(BOOK_NAME,ISSUE_YEAR) values ('Недоросль', 1959);
insert into BOOKS(BOOK_NAME,ISSUE_YEAR) values ('Преступление и наказание', 1955);
insert into BOOKS(BOOK_NAME,ISSUE_YEAR) values ('Гамлет', 1954);

insert into AUTHORS(FIRST_NAME, LAST_NAME, MIDDLE_NAME) values ('Александр','Грибоедов','Сергеевич');
insert into AUTHORS(FIRST_NAME, LAST_NAME, MIDDLE_NAME) values ('Антон','Чехов','Павлович');
insert into AUTHORS(FIRST_NAME, LAST_NAME, MIDDLE_NAME) values ('Денис','Фонвизин','Иванович');
insert into AUTHORS(FIRST_NAME, LAST_NAME, MIDDLE_NAME) values ('Фёдор','Достоевскмй','Михайлович');
insert into AUTHORS(FIRST_NAME, LAST_NAME, MIDDLE_NAME) values ('Вильям','Шекспир','');

insert into GENRE_BOOK(GENRE_ID, BOOK_ID) values ((select GENRE_ID from GENRES where lower(GENRE_NAME) like 'драма')
                                                 ,(select BOOK_ID from BOOKS where lower(BOOK_NAME) like 'преступление%'));
insert into GENRE_BOOK(GENRE_ID, BOOK_ID) values ((select GENRE_ID from GENRES where lower(GENRE_NAME) like 'трагед%')
                                                 ,(select BOOK_ID from BOOKS where lower(BOOK_NAME) like 'гамле%'));
insert into GENRE_BOOK(GENRE_ID, BOOK_ID) values ((select GENRE_ID from GENRES where lower(GENRE_NAME) like 'комедия')
                                                 ,(select BOOK_ID from BOOKS where lower(BOOK_NAME) like 'недорос%'));
insert into GENRE_BOOK(GENRE_ID, BOOK_ID) values ((select GENRE_ID from GENRES where lower(GENRE_NAME) like 'комедия')
                                                 ,(select BOOK_ID from BOOKS where lower(BOOK_NAME) like 'горе%'));
insert into GENRE_BOOK(GENRE_ID, BOOK_ID) values ((select GENRE_ID from GENRES where lower(GENRE_NAME) like 'комедия')
                                                 ,(select BOOK_ID from BOOKS where lower(BOOK_NAME) like 'вишневы%'));

insert into AUTHOR_BOOK(AUTHOR_ID, BOOK_ID) values ((select AUTHOR_ID from AUTHORS where lower(LAST_NAME) like 'фонвизи%')
                                                   ,(select BOOK_ID from BOOKS where lower(BOOK_NAME) like 'недорос%'));
insert into AUTHOR_BOOK(AUTHOR_ID, BOOK_ID) values ((select AUTHOR_ID from AUTHORS where lower(LAST_NAME) like 'достоевс%')
                                                   ,(select BOOK_ID from BOOKS where lower(BOOK_NAME) like 'преступл%'));
insert into AUTHOR_BOOK(AUTHOR_ID, BOOK_ID) values ((select AUTHOR_ID from AUTHORS where lower(LAST_NAME) like 'шексп%')
                                                   ,(select BOOK_ID from BOOKS where lower(BOOK_NAME) like 'гамле%'));
insert into AUTHOR_BOOK(AUTHOR_ID, BOOK_ID) values ((select AUTHOR_ID from AUTHORS where lower(LAST_NAME) like 'чехов%')
                                                   ,(select BOOK_ID from BOOKS where lower(BOOK_NAME) like 'вишневы%'));
insert into AUTHOR_BOOK(AUTHOR_ID, BOOK_ID) values ((select AUTHOR_ID from AUTHORS where lower(LAST_NAME) like 'грибоедо%')
                                                   ,(select BOOK_ID from BOOKS where lower(BOOK_NAME) like 'горе%'));
