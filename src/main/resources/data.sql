 INSERT INTO PUBLIC.RATING(RATING_NAME) VALUES ('G'),
    	('PG'),
	    ('PG-13'),
	    ('R'),
	    ('NC-17');

INSERT INTO PUBLIC.GENRE
	(GENRE_NAME) VALUES ('Комедия'),
                  ('Драма'),
                  ('Мультфильм'),
                  ('Триллер'),
                  ('Документальный'),
                  ('Боевик');

INSERT INTO PUBLIC.DIRECTOR (NAME) VALUES
	 ('director1'),
	 ('director2');

--INSERT INTO PUBLIC.FILM (RELEASE_DATE,DURATION,RATING_ID,NAME,DESCRIPTION,DIRECTOR) VALUES
--	 ('1899-12-09',45, 1, 'name','Description', 1),
--	 ('2014-04-05',112, 1,'1+1','Пострадав в результате несчастного случая, богатый аристократ Филипп нанимает в помощники человека, который менее всего подходит для этой работы,молодого жителя предместья Дрисса', 2),
--	 ('2022-07-03',113, 2,'Джентльмены','Наркобарон хочет уйти на покой, но криминальный мир не отпускает. Успешное возвращение Гая Ричи к корням', 2);
--
--INSERT INTO PUBLIC."USER" (BIRTHDAY,EMAIL,LOGIN,NAME) VALUES
--('1976-09-20','mail@yandex.ru','doloreUpdate','est adipisicing'),
--	 ('2004-04-08','anna@mail.ru','anna13','Anna'),
--	 ('2008-09-06','alex@yandex.ru','alex45','alex'),
--	 ('1988-02-10','elena@yandex.ru','elena4','elena');
--
--INSERT INTO PUBLIC.FILM_LIKE (USER_ID,FILM_ID) VALUES
--	 (1,1),
--	 (1,2),
--	 (2,2),
--	 (3,2);
----INSERT INTO PUBLIC.FRIENDS (USER_ID,FRIEND_ID,CONFIRMATION_STATUS) VALUES
----	 (1,2,true),
----	 (2,3,true),
----	 (4,2,false);
--
--INSERT INTO PUBLIC.FILM_GENRE (FILM_ID,GENRE_ID) VALUES
--	 (2,1),
--	 (2,2);

