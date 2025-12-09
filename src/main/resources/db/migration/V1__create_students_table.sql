CREATE TABLE students
(
   id               BIGSERIAL PRIMARY KEY,
   first_name       VARCHAR(255) NOT NULL,
   last_name        VARCHAR(255) NOT NULL,
   email            VARCHAR(255) NOT NULL UNIQUE,
   age              INTEGER NOT NULL
);