DROP TABLE IF EXISTS PUBLIC."USER" CASCADE;
DROP TABLE IF EXISTS PUBLIC.FRIENDSHIP CASCADE;
DROP TABLE IF EXISTS PUBLIC.RATING CASCADE;
DROP TABLE IF EXISTS PUBLIC.FILM CASCADE;
DROP TABLE IF EXISTS PUBLIC.FILM_LIKE CASCADE;
DROP TABLE IF EXISTS PUBLIC.GENRE CASCADE;
DROP TABLE IF EXISTS PUBLIC.FILM_GENRE CASCADE;
DROP TABLE IF EXISTS PUBLIC.DIRECTOR CASCADE;
DROP TABLE IF EXISTS PUBLIC.REVIEW CASCADE;
DROP TABLE IF EXISTS PUBLIC.REVIEW_LIKE CASCADE;
DROP TABLE IF EXISTS PUBLIC.REVIEW_DISLIKE CASCADE;
DROP TABLE IF EXISTS PUBLIC.DIRECTOR CASCADE;
DROP TABLE IF EXISTS PUBLIC.FILM_DIRECTOR CASCADE;
DROP TABLE IF EXISTS PUBLIC.EVENT_TYPE CASCADE;
DROP TABLE IF EXISTS PUBLIC.EVENT_OPERATION  CASCADE;
DROP TABLE IF EXISTS PUBLIC.USER_EVENT CASCADE;


CREATE TABLE IF NOT EXISTS PUBLIC."USER" (
	USER_ID INTEGER NOT NULL AUTO_INCREMENT,
	EMAIL CHARACTER VARYING(50) NOT NULL,
	LOGIN CHARACTER VARYING(30) NOT NULL,
	BIRTHDAY DATE NOT NULL,
	NAME CHARACTER VARYING(30) NOT NULL,
	CONSTRAINT USER_PK PRIMARY KEY (USER_ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.FRIENDSHIP (
	USER_FIRST_ID INTEGER NOT NULL,
	USER_SECOND_ID INTEGER NOT NULL,
	CONSTRAINT FRIENDSHIP_FK FOREIGN KEY (USER_FIRST_ID) REFERENCES PUBLIC."USER"(USER_ID)
	ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT FRIENDSHIP_FK_1 FOREIGN KEY (USER_SECOND_ID) REFERENCES PUBLIC."USER"(USER_ID)
	ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.GENRE (
	GENRE_ID INTEGER NOT NULL AUTO_INCREMENT,
	GENRE_NAME CHARACTER VARYING(20) NOT NULL,
	CONSTRAINT GENRE_PK PRIMARY KEY (GENRE_ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.RATING (
	RATING_ID INTEGER NOT NULL AUTO_INCREMENT,
	RATING_NAME CHARACTER VARYING(10) NOT NULL,
	CONSTRAINT RATING_PK PRIMARY KEY (RATING_ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILM (
	FILM_ID INTEGER NOT NULL AUTO_INCREMENT,
	NAME CHARACTER VARYING(30) NOT NULL,
	DESCRIPTION CHARACTER VARYING(200) NOT NULL,
	RELEASE_DATE DATE NOT NULL,
	DURATION INTEGER NOT NULL,
	RATING_ID INTEGER NOT NULL,
	CONSTRAINT FILM_PK PRIMARY KEY (FILM_ID),
	CONSTRAINT FILM_FK FOREIGN KEY (RATING_ID) REFERENCES PUBLIC.RATING(RATING_ID)
	ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILM_LIKE (
	FILM_ID INTEGER NOT NULL,
	USER_ID INTEGER NOT NULL,
	CONSTRAINT FILM_LIKE_PK PRIMARY KEY (USER_ID,FILM_ID),
	CONSTRAINT FILM_LIKE_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILM(FILM_ID)
	ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT FILM_LIKE_FK_1 FOREIGN KEY (USER_ID) REFERENCES PUBLIC."USER"(USER_ID)
	ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILM_GENRE (
	FILM_ID INTEGER NOT NULL,
	GENRE_ID INTEGER NOT NULL,
	CONSTRAINT FILM_GENRE_PK PRIMARY KEY (FILM_ID, GENRE_ID),
	CONSTRAINT FILM_GENRE_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILM(FILM_ID)
	ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT FILM_GENRE_FK_1 FOREIGN KEY (GENRE_ID) REFERENCES PUBLIC.GENRE(GENRE_ID)
	ON DELETE CASCADE ON UPDATE CASCADE
);


	CREATE TABLE IF NOT EXISTS PUBLIC.REVIEW (
	REVIEW_ID INTEGER NOT NULL AUTO_INCREMENT,
	CONTENT CHARACTER VARYING(200) NOT NULL,
	IS_POSITIVE BOOLEAN NOT NULL,
	USER_ID INTEGER NOT NULL,
	FILM_ID INTEGER NOT NULL,
	USEFUL INTEGER NOT NULL DEFAULT 0,
	CONSTRAINT REVIEW_PK PRIMARY KEY (REVIEW_ID),
	CONSTRAINT REVIEW_FK_1 FOREIGN KEY (USER_ID) REFERENCES PUBLIC."USER"(USER_ID)
	ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT REVIEW_FK_2 FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILM(FILM_ID)
	ON DELETE CASCADE ON UPDATE CASCADE
);

    CREATE TABLE IF NOT EXISTS PUBLIC.REVIEW_LIKE (
    REVIEW_ID INTEGER NOT NULL,
    USER_ID INTEGER NOT NULL,
    CONSTRAINT REVIEW_LIKE_PK PRIMARY KEY (REVIEW_ID, USER_ID),
    CONSTRAINT REVIEW_LIKE_FK_1 FOREIGN KEY (USER_ID) REFERENCES PUBLIC."USER"(USER_ID)
	ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT REVIEW_LIKE_FK_2 FOREIGN KEY (REVIEW_ID) REFERENCES PUBLIC.REVIEW(REVIEW_ID)
	ON DELETE CASCADE ON UPDATE CASCADE
);

    CREATE TABLE IF NOT EXISTS PUBLIC.REVIEW_DISLIKE (
    REVIEW_ID INTEGER NOT NULL,
    USER_ID INTEGER NOT NULL,
    CONSTRAINT REVIEW_DISLIKE_PK PRIMARY KEY (REVIEW_ID, USER_ID),
    CONSTRAINT REVIEW_DISLIKE_FK_1 FOREIGN KEY (USER_ID) REFERENCES PUBLIC."USER"(USER_ID)
	ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT REVIEW_DISLIKE_FK_2 FOREIGN KEY (REVIEW_ID) REFERENCES PUBLIC.REVIEW(REVIEW_ID)
	ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE IF NOT EXISTS PUBLIC.DIRECTOR (
	DIRECTOR_ID INTEGER NOT NULL,
	DIRECTOR_NAME CHARACTER VARYING(128) NOT NULL,
	CONSTRAINT DIRECTOR_PK PRIMARY KEY (DIRECTOR_ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILM_DIRECTOR (
	FILM_ID INTEGER NOT NULL,
	DIRECTOR_ID INTEGER NOT NULL,
	CONSTRAINT FILM_DIRECTOR_PK PRIMARY KEY (FILM_ID, DIRECTOR_ID),
	CONSTRAINT FILM_ID_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILM(FILM_ID) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT DIRECTOR_ID_FK FOREIGN KEY (DIRECTOR_ID) REFERENCES PUBLIC.DIRECTOR(DIRECTOR_ID)
  ON DELETE CASCADE ON UPDATE CASCADE
  );

  CREATE TABLE IF NOT EXISTS PUBLIC.EVENT_TYPE (
      EVENT_TYPE_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
      NAME VARCHAR(10)
  );

  CREATE TABLE IF NOT EXISTS PUBLIC.EVENT_OPERATION (
      EVENT_OPERATION_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
      NAME VARCHAR(10)
  );

  CREATE TABLE IF NOT EXISTS PUBLIC.USER_EVENT (
      EVENT_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
      TIME_ADD BIGINT,
      USER_ID INTEGER REFERENCES `USER`(USER_ID) ON DELETE NO ACTION,
      EVENT_TYPE_ID INTEGER REFERENCES `EVENT_TYPE`(EVENT_TYPE_ID) ON DELETE NO ACTION,
      EVENT_OPERATION_ID INTEGER REFERENCES `EVENT_OPERATION`(EVENT_OPERATION_ID) ON DELETE NO ACTION,
      ENTITY_ID INTEGER
  );

