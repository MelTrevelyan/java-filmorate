# java-filmorate
## ER-диаграмма
![filmorate](https://user-images.githubusercontent.com/114815793/225908393-05f9f05c-c9ff-4cf7-8e56-a52bc14bda50.png)

Пример запроса:
Получение топ-10 названий фильмов по количеству лайков:
```sql
SELECT name
FROM film
WHERE film_id IN (SELECT film_id
                  FROM like
                  GROUP BY film_id
                  ORDER BY COUNT(user_id) desc
                   LIMIT 10);
```
Получение id и логинов друзей по id пользователя = 1:
```sql 
SELECT u.login,
       u.user_id
FROM user AS u
WHERE u.user_id IN (SELECT f.user_second_id
                    FROM friendship AS f
                    WHERE f.user_first_id = 1);

Пир-ревью:
Отличная работа, молодец! 
Диаграмма выполнена в соответствии с поставленным ТЗ,
с учетом code style. 
Для наглядности можно было бы таблицу "like" назвать "film_like", 
что подчеркнуло бы принадлежность Лайка фильму. 
