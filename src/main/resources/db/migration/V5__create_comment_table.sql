 create table comment
    (
      id           bigint auto_increment primary key,
      parent_id    bigint not null  comment '回复的问题或评论',
      type         integer not null default 1 comment '回复类型',
      commentator   integer not null  comment '创建人',
      gmt_create   bigint,
      gmt_modified bigint,
      like_count   integer default 0 comment '点赞数',
      content      varchar(10244)
    );