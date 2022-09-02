create table if not exists GENRE
(
    ID   INTEGER auto_increment,
    NAME CHARACTER VARYING(100) not null,
    constraint GENRE_PK
        primary key (ID)
);

create unique index if not exists GENRE_GENRE_NAME_UINDEX
    on GENRE (NAME);

create table if not exists MPA
(
    ID   INTEGER auto_increment,
    NAME CHARACTER VARYING(5) not null,
    constraint MPA_PK
        primary key (ID)
);

create unique index if not exists MPA_MPA_NAME_UINDEX
    on MPA (NAME);

create table if not exists FILMS
(
    FILM_ID          INTEGER auto_increment,
    FILM_NAME        CHARACTER VARYING(100) not null,
    FILM_DESCRIPTION CHARACTER VARYING(200) not null,
    RELEASE_DATE     DATE,
    DURATION         INTEGER,
    MPA_ID           INTEGER,
    constraint FILMS_PK
        primary key (FILM_ID),
    constraint FILMS_MPA_MPA_ID_FK
        foreign key (MPA_ID) references MPA
);

create table if not exists FILM_GENRES
(
    FILM_ID  INTEGER,
    GENRE_ID INTEGER
);

create table if not exists USERS
(
    USER_ID   INTEGER auto_increment,
    USER_NAME CHARACTER VARYING(100) not null,
    LOGIN     CHARACTER VARYING(50)  not null,
    EMAIL     CHARACTER VARYING(100) not null,
    BIRTHDAY  DATE                   not null,
    constraint USERS_PK
        primary key (USER_ID)
);

create unique index if not exists USERS_EMAIL_UINDEX
    on USERS (EMAIL);

create unique index if not exists USERS_LOGIN_UINDEX
    on USERS (LOGIN);

create table if not exists LIKES
(
    FILM_ID INTEGER,
    USER_ID INTEGER
);

create table if not exists USER_FRIENDS
(
    USER_ID   INTEGER,
    FRIEND_ID INTEGER
);

create table if not exists DIRECTORS
(
    ID   INTEGER auto_increment,
    NAME CHARACTER VARYING not null,
    constraint DIRECTORS_PK
        primary key (ID)
);

create table if not exists FILM_DIRECTORS
(
    FILM_ID     INTEGER,
    DIRECTOR_ID INTEGER
);