INSERT INTO venue (address, location)
VALUES ('Москва, Красная площадь', ST_GeogFromText('POINT(37.617494 55.755826)')),
       ('Москва, Большой театр', ST_GeogFromText('POINT(37.618721 55.760321)')),
       ('Москва, ВДНХ', ST_GeogFromText('POINT(37.632569 55.823629)'));

INSERT INTO venue (address, location)
VALUES ('Санкт-Петербург, Дворцовая площадь', ST_GeogFromText('POINT(30.314186 59.938806)')),
       ('Санкт-Петербург, Мариинский театр', ST_GeogFromText('POINT(30.295887 59.925312)'));

INSERT INTO category (category_name)
VALUES ('Концерт'),
       ('Спектакль'),
       ('Выставка'),
       ('Фестиваль');

INSERT INTO category (category_name)
VALUES ('Рок'),
       ('Классика'),
       ('Поп'),
       ('Шансон');


