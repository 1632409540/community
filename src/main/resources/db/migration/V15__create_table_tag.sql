create table `tag_type` (
  `id` bigint(20) not null auto_increment,
  `name` varchar(50) not null comment '标签类别名称',
  `create_date` timestamp null default null on update current_timestamp comment '创建时间',
  `last_modified` timestamp null default current_timestamp on update current_timestamp comment '最后更新时间',
  primary key (`id`)
) engine=innodb default charset=utf8mb4;

create table `tag` (
  `id` bigint(20) not null auto_increment,
  `tag_type_id` bigint(20) not null comment '标签类别id',
  `name` varchar(50) not null comment '标签名称',
  `create_date` timestamp null default null on update current_timestamp comment '创建时间',
  `last_modified` timestamp null default current_timestamp on update current_timestamp comment '最后更新时间',
  primary key (`id`)
) engine=innodb default charset=utf8mb4;