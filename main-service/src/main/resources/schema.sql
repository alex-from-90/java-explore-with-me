DROP TABLE IF EXISTS REQUESTS CASCADE;
DROP TABLE IF EXISTS CATEGORIES CASCADE;
DROP TABLE IF EXISTS COMPILATIONS CASCADE;
DROP TABLE IF EXISTS EVENTS CASCADE;
DROP TABLE IF EXISTS LOCATIONS CASCADE;
DROP TABLE IF EXISTS USERS CASCADE;
DROP TABLE IF EXISTS COMPILATION_EVENTS CASCADE;

CREATE TABLE IF NOT EXISTS USERS
(
    USER_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    NAME    CHARACTER VARYING                       NOT NULL,
    EMAIL   CHARACTER VARYING                       NOT NULL,
    CONSTRAINT UN_EMAIL UNIQUE (EMAIL),
    CONSTRAINT USERS_PK PRIMARY KEY (USER_ID)
);

CREATE TABLE IF NOT EXISTS CATEGORIES
(
    CATEGORY_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    NAME        CHARACTER VARYING                       NOT NULL,
    CONSTRAINT UN_NAME UNIQUE (NAME),
    CONSTRAINT CATEGORIES_PK PRIMARY KEY (CATEGORY_ID)
);
CREATE TABLE IF NOT EXISTS LOCATIONS
(
    LOCATION_ID BIGINT GENERATED BY DEFAULT AS IDENTITY,
    LAT         FLOAT NOT NULL,
    LON         FLOAT NOT NULL,
    CONSTRAINT LOCATIONS_PK PRIMARY KEY (LOCATION_ID)
);

CREATE TABLE IF NOT EXISTS COMPILATIONS
(
    COMPILATION_ID BIGINT GENERATED BY DEFAULT AS IDENTITY,
    PINNED         BOOLEAN,
    TITLE          VARCHAR,
    CONSTRAINT COMPILATIONS_PK PRIMARY KEY (COMPILATION_ID)
);

CREATE TABLE IF NOT EXISTS EVENTS
(
    EVENT_ID           BIGINT GENERATED BY DEFAULT AS IDENTITY,
    ANNOTATION         VARCHAR                     NOT NULL,
    CATEGORY_ID        BIGINT                      NOT NULL,
    CREATE_DATE        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    DESCRIPTION        VARCHAR                     NOT NULL,
    EVENT_DATE         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    INITIATOR_ID       BIGINT                      NOT NULL,
    PAID               BOOLEAN                     NOT NULL,
    PARTICIPANT_LIMIT  BIGINT                      NOT NULL,
    PUBLISHED_DATE     TIMESTAMP WITHOUT TIME ZONE,
    REQUEST_MODERATION BOOLEAN                     NOT NULL,
    STATE              VARCHAR                     NOT NULL,
    TITLE              VARCHAR                     NOT NULL,
    LOCATION_ID        BIGINT,
    COMPILATION_ID     BIGINT                      REFERENCES COMPILATIONS (COMPILATION_ID) ON DELETE SET NULL,
    CONSTRAINT EVENTS_PK PRIMARY KEY (EVENT_ID),
    CONSTRAINT EVENTS_LOCATION_FK FOREIGN KEY (LOCATION_ID) REFERENCES LOCATIONS,
    CONSTRAINT EVENTS_CATEGORIES_FK FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORIES,
    CONSTRAINT EVENTS_INITIATOR_FK FOREIGN KEY (INITIATOR_ID) REFERENCES USERS
);

CREATE TABLE IF NOT EXISTS REQUESTS
(
    REQUEST_ID   BIGINT GENERATED BY DEFAULT AS IDENTITY,
    CREATED_DATE TIMESTAMP WITHOUT TIME ZONE,
    EVENT_ID     BIGINT,
    REQUESTER_ID BIGINT NOT NULL,
    STATUS       VARCHAR,
    CONSTRAINT REQUESTS_PK PRIMARY KEY (REQUEST_ID),
    CONSTRAINT REQUESTS_EVENTS_FK FOREIGN KEY (EVENT_ID) REFERENCES EVENTS,
    CONSTRAINT REQUESTS_USERS_FK FOREIGN KEY (REQUESTER_ID) REFERENCES USERS
);

CREATE TABLE IF NOT EXISTS COMPILATION_EVENTS
(
    EVENT_ID       BIGINT REFERENCES EVENTS (EVENT_ID),
    COMPILATION_ID BIGINT REFERENCES COMPILATIONS (COMPILATION_ID),
    CONSTRAINT COMPILATION_EVENT_PK PRIMARY KEY (EVENT_ID, COMPILATION_ID)
);