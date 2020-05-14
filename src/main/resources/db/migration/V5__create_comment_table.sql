 create table COMMENT
    (
      ID           BIGINT AUTO_INCREMENT PRIMARY KEY,
      PARENT_ID    BIGINT NOT NULL,
      TYPE         INTEGER NOT NULL,
      COMMENTATOR   INTEGER NOT NULL,
      GMT_CREATE   BIGINT,
      GMT_MODIFIED BIGINT,
      LIKE_COUNT   integer DEFAULT 0,
      content      varchar(10244)
    );