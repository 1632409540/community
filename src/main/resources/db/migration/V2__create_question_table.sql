CREATE TABLE question
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title varchar(50),
    description text,
    gmt_create BIGINT,
    gmt_modified BIGINT,
    creator int,
    comunt_count int DEFAULT 0,
    view_count int DEFAULT 0,
    like_count int DEFAULT 0,
    tag varchar(256)
);