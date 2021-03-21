alter table user  change gmt_create create_date timestamp   comment '创建时间';
alter table user  change gmt_modified last_modified timestamp  default current_timestamp comment '最后更新时间';
alter table question  change gmt_create create_date timestamp   COMMENT '创建时间';
alter table question  change gmt_modified last_modified timestamp  default current_timestamp comment '最后更新时间';
alter table notification  change gmt_create create_date timestamp   comment '创建时间';
alter table notification  add  last_modified timestamp  default current_timestamp comment '最后更新时间';
alter table comment  change gmt_create create_date timestamp   comment '创建时间';
alter table comment  change gmt_modified last_modified timestamp  default current_timestamp comment '最后更新时间';
