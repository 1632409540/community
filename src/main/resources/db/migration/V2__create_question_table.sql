create table question
(
    id bigint auto_increment primary key,
    title varchar(50) default null comment '问题标题',
    description text default null comment '问题描述',
    gmt_create bigint,
    gmt_modified bigint,
    creator int comment '创建人',
    comunt_count int default 0 ,
    view_count int default 0 ,
    like_count int default 0,
    tag varchar(256)
);