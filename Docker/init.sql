CREATE DATABASE testdb;
use testdb;

CREATE TABLE tatbestand (
    id MEDIUMINT NOT NULL AUTO_INCREMENT,
    tattag DATE,
    tatzeit TIME,
    tatort VARCHAR(100),
    tatort2 VARCHAR(100),
    tatbestand INT,
    betrag DOUBLE,
    PRIMARY KEY(id)
);