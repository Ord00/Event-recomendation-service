INSERT INTO venue (address, location)
VALUES 
    ('Москва, Красная площадь', ST_GeogFromText('SRID=4326;POINT(37.617494 55.755826)')),
    ('Москва, Большой театр', ST_GeogFromText('SRID=4326;POINT(37.618721 55.760321)')),
    ('Москва, ВДНХ', ST_GeogFromText('SRID=4326;POINT(37.632569 55.823629)')),
    ('Санкт-Петербург, Дворцовая площадь', ST_GeogFromText('SRID=4326;POINT(30.314186 59.938806)')),
    ('Санкт-Петербург, Мариинский театр', ST_GeogFromText('SRID=4326;POINT(30.295887 59.925312)'));

INSERT INTO category (category_name)
VALUES 
    ('Концерт'),
    ('Спектакль'),
    ('Выставка'),
    ('Фестиваль'),
    ('Рок'),
    ('Классика'),
    ('Поп'),
    ('Шансон');