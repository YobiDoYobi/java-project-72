DROP TABLE IF EXISTS urls;
DROP TABLE IF EXISTS url_checks;

CREATE TABLE urls
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP
);


CREATE TABLE url_checks
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    urlId       INT,
    statusCode  INT,
    title       VARCHAR(255),
    h1          VARCHAR(255),
    description VARCHAR(1024),
    created_at  TIMESTAMP
);