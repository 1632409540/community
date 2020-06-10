CREATE TABLE notification
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nitifier BIGINT NOT NULL,
    receiver BIGINT NOT NULL,
    outer_id BIGINT NOT NULL,
    type int NOT NULL,
    gmt_create BIGINT,
    status int DEFAULT 0
);