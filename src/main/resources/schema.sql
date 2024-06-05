DROP TABLE users IF EXISTS CASCADE ;
DROP TABLE items IF EXISTS CASCADE ;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR (255) NOT NULL,
    email VARCHAR (512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR (255) NOT NULL,
    description VARCHAR (512) NOT NULL,
    is_available BOOLEAN NOT NULL,
    owner_id BIGINT,
    request_id BIGINT,
    CONSTRAINT pk_item PRIMARY KEY (id)
);