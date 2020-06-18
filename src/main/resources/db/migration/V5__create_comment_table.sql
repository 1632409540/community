 create table comment
    (
      id           bigint auto_increment primary key,
      parent_id    bigint not null,
      type         integer not null,
      commentator   integer not null,
      gmt_create   bigint,
      gmt_modified bigint,
      like_count   integer default 0,
      content      varchar(10244)
    );