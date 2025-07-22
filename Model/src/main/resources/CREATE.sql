CREATE EXTENSION IF NOT EXISTS postgis;

-- Таблица пользователей (основная)
CREATE TABLE "user" (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Таблица администраторов
CREATE TABLE admin (
     id INTEGER PRIMARY KEY REFERENCES "user"(id),
     full_name VARCHAR(100) NOT NULL
);

-- Таблица обычных пользователей
CREATE TABLE common_user (
   id INTEGER PRIMARY KEY REFERENCES "user"(id),
   full_name VARCHAR(100) NOT NULL,
   phone_number VARCHAR(20) NOT NULL
);

-- Таблица организаторов
CREATE TABLE organizer (
     id INTEGER PRIMARY KEY REFERENCES "user"(id),
     organizer_name VARCHAR(100) NOT NULL
);

-- Таблица категорий
CREATE TABLE category (
   id BIGSERIAL PRIMARY KEY,
   category_name VARCHAR(50) NOT NULL UNIQUE
);

-- Таблица площадок
CREATE TABLE venue (
   id BIGSERIAL PRIMARY KEY,
   address TEXT NOT NULL,
   location GEOGRAPHY(POINT, 4326) NOT NULL
);

-- Таблица событий
CREATE TABLE event (
   id BIGSERIAL PRIMARY KEY,
   title VARCHAR(100) NOT NULL,
   description TEXT,
   start_time TIMESTAMPTZ NOT NULL,
   end_time TIMESTAMPTZ NOT NULL,
   recurrence VARCHAR(20),
   status VARCHAR(20) NOT NULL CHECK (status IN ('DRAFT', 'PUBLISHED', 'CANCELED', 'COMPLETED')),
   view_count INTEGER DEFAULT 0,
   id_user INTEGER NOT NULL REFERENCES "user"(id),
   id_venue INTEGER NOT NULL REFERENCES venue(id)
);

-- Связь событий с категориями (многие-ко-многим)
CREATE TABLE category_event (
    id BIGSERIAL PRIMARY KEY,
    id_category INTEGER NOT NULL REFERENCES category(id),
    id_event INTEGER NOT NULL REFERENCES event(id),
    UNIQUE (id_category, id_event)
);

-- Таблица подписок
CREATE TABLE event_subscription (
    id BIGSERIAL PRIMARY KEY,
    id_user INTEGER NOT NULL REFERENCES "user"(id),
    id_event INTEGER NOT NULL REFERENCES event(id),
    notify_time INTERVAL NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'COMPLETED')),
    UNIQUE (id_user, id_event)
);

-- Таблица уведомлений
CREATE TABLE notification_log (
    id BIGSERIAL PRIMARY KEY,
    id_user INTEGER NOT NULL REFERENCES "user"(id),
    id_event INTEGER NOT NULL REFERENCES event(id),
    channel VARCHAR(10) NOT NULL CHECK (channel IN ('EMAIL', 'SMS', 'PUSH')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'SENT', 'FAILED')),
    sent_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Индексы для производительности
CREATE INDEX idx_events_user ON event(id_user);
CREATE INDEX idx_events_venue ON event(id_venue);
CREATE INDEX idx_events_status ON event(status);
CREATE INDEX idx_events_time ON event(start_time, end_time);
CREATE INDEX idx_venue_location ON venue USING GIST(location);
CREATE INDEX idx_subscriptions_user ON event_subscription(id_user);
CREATE INDEX idx_subscriptions_event ON event_subscription(id_event);
CREATE INDEX idx_notifications_user_event ON notification_log(id_user, id_event);
CREATE INDEX idx_notifications_sent_at ON notification_log(sent_at);

-- Триггер для автоматического обновления счетчика просмотров
CREATE OR REPLACE FUNCTION update_view_count()
RETURNS TRIGGER AS $$
BEGIN
UPDATE event
SET view_count = view_count + 1
WHERE id = NEW.id_event;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_increment_view_count
AFTER INSERT ON event_subscription
FOR EACH ROW
EXECUTE FUNCTION update_view_count();

-- Представление для популярных событий
CREATE VIEW popular_event AS
SELECT e.id,
       e.title,
       e.description,
       e.start_time,
       e.end_time,
       e.status,
       e.id_user,
       e.id_venue,
       e.view_count,
       COUNT(es.id) AS subscribers_count
FROM event e LEFT JOIN event_subscription es ON e.id = es.id_event
GROUP BY e.id,
         e.title,
         e.description,
         e.start_time,
         e.end_time,
         e.status,
         e.id_user,
         e.id_venue,
         e.view_count
ORDER BY e.view_count DESC,
         subscribers_count DESC
    LIMIT 100;

CREATE OR REPLACE FUNCTION get_user_role(user_id INTEGER)
    RETURNS VARCHAR AS $$
DECLARE
    user_role VARCHAR;
BEGIN
    IF EXISTS (SELECT 1 FROM admin WHERE id = user_id) THEN
        user_role := 'ADMIN';
    ELSIF EXISTS (SELECT 1 FROM organizer WHERE id = user_id) THEN
        user_role := 'ORGANIZER';
    ELSIF EXISTS (SELECT 1 FROM common_user WHERE id = user_id) THEN
        user_role := 'USER';
    ELSE
        user_role := NULL;
    END IF;

    RETURN user_role;
END;
$$ LANGUAGE plpgsql;