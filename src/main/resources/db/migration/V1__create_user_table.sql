 create table user
    (
      id           bigint auto_increment primary key,
      account_id   varchar(100) default null comment 'GitHub账号id',
      name         varchar(50) default null comment '用户名',
      token        char(36) default null comment '第三方登录token',
      gmt_create   bigint,
      gmt_modified bigint
    );