create table notification
(
    id bigint auto_increment primary key,
    nitifier bigint not null,
    receiver bigint not null,
    outer_id bigint not null,
    type int not null,
    gmt_create bigint,
    status int default 0
);