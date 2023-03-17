# java-filmorate
## ER-диаграмма
![filmorate](https://user-images.githubusercontent.com/114815793/225908393-05f9f05c-c9ff-4cf7-8e56-a52bc14bda50.png)

Пример запроса:
Получение топ-10 фильмов по количеству лайков:
```sql
SELECT name
FROM film
WHERE film_id IN (SELECT film_id
                  FROM like
                  GROUP BY film_id
                  ORDER BY COUNT(user_id) desc
                   LIMIT 10);
```

