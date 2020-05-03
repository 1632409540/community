## 丽院论坛

## 资料
[Github community](https://github.com/1632409540/community)  
[Spring 文档](https://spring.io/guides)  
[Spring Web 文档](https://spring.io/guides/gs/serving-web-content/)  
[es](https://elasticsearch.cn/explore)  
[Github OAuth](https://developer.github.com/apps/building-oauth-apps/creating-an-oauth-app/)  
[flyway](https://flywaydb.org/)
## 工具
[Git](https://git-scm.com/)  
[Visual Paradigm](https://www.visual-paradigm.com)  


## 脚本
```sql
    create table USER
    (
      ID           INTEGER default NEXT VALUE FOR "PUBLIC"."SYSTEM_SEQUENCE_7E96F4B6_5A61_411E_80A2_B1BCEBB73D5D"
        primary key,
      ACCOUNT_ID   VARCHAR(100),
      NAME         VARCHAR(50),
      TOKEN        CHAR(36),
      GMT_CREATE   BIGINT,
      GMT_MODIFIED BIGINT
    );
    ALTER TABLE USER ADD bio varchar(256) NULL;
```

## 异常处理

[h2:Wrong user name or password](https://blog.csdn.net/tripleDemo/article/details/98888281)
