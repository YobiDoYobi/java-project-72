DROP TABLE IF EXISTS url_checks;
DROP TABLE IF EXISTS urls;


CREATE TABLE urls
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY unique NOT NULL,
    name       VARCHAR(255)                                   NOT NULL,
    created_at TIMESTAMP
);


CREATE TABLE url_checks
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY unique NOT NULL,
    url_id      BIGINT REFERENCES urls (id)                    NOT NULL,
    status_code INT,
    title       VARCHAR(255),
    h1          VARCHAR(255),
    description VARCHAR(1024),
    created_at  TIMESTAMP
);
