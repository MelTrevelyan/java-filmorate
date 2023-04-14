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

INSERT INTO `EVENT_TYPES` (EVENT_TYPE_ID, NAME)
                  VALUES (1, 'LIKE'),
                         (2, 'REVIEW'),
                         (3, 'FRIEND');

INSERT INTO `EVENT_OPERATIONS` (EVENT_OPERATION_ID, NAME)
                  VALUES (1, 'ADD'),
                         (2, 'UPDATE'),
                         (3, 'REMOVE');