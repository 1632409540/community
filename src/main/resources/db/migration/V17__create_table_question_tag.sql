create table `question_tag` (
  `id` bigint(20) not null auto_increment,
  `question_id` bigint(20) not null comment '问题id',
  `tag_id` bigint(20) not null comment '标签id',
  `create_date` timestamp null default current_timestamp  comment '创建时间',
  `last_modified` timestamp null default current_timestamp on update current_timestamp comment '最后更新时间',
  primary key (`id`)
) engine=innodb default charset=utf8mb4;