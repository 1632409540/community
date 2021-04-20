create table `system_set` (
  `id` bigint(20) not null auto_increment,
  `title` varchar(255) DEFAULT NULL COMMENT '系统标题',
  `system_logo` varchar(255) DEFAULT NULL COMMENT '系统图标',
  `background` varchar(255) DEFAULT NULL COMMENT '背景图片',
  `public_title` varchar(255) DEFAULT NULL COMMENT '公众号标题',
  `public_wechat` varchar(255) DEFAULT NULL COMMENT '微信公众号二维码地址',
  `public_microblog` varchar(255) DEFAULT NULL COMMENT '微博公众号二维码地址',
  `public_qq` varchar(255) DEFAULT NULL COMMENT 'QQ公众号二维码地址',
  `create_date` timestamp null default current_timestamp  comment '创建时间',
  `last_modified` timestamp null default current_timestamp on update current_timestamp comment '最后更新时间',
  primary key (`id`)
) engine=innodb default charset=utf8mb4;