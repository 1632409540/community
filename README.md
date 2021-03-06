## 校园博客系统

##部署  
- Git
- JDK
- Maven  
- MySQL  

##步骤  
- yum update
- yum install git
- mkdir App
- cd App
- git clone xxxxxx
- yum install maven
- mvn -v
- mvn  compile package
- more src/main/resource/application.properties
- cp src/main/resource/application.properties src/main/resource/application-production.properties
- vi src/main/resource/application-production.properties
- mvn package -Dmaven.test.skip=true
- java -jar -Dspring.profiles.active=production target/community-0.0.1-SNAPSHOT.jar  


## 资料
[Github community](https://github.com/1632409540/community)  
[Spring 文档](https://spring.io/guides)  
[Spring Web 文档](https://spring.io/guides/gs/serving-web-content/)  
[es](https://elasticsearch.cn/explore)  
[Bootstrap](https://www.bootcss.com/)
[Github OAuth](https://developer.github.com/apps/building-oauth-apps/creating-an-oauth-app/)  
[thymeleaf](https://www.thymeleaf.org/)  
[H2](http://www.h2database.com/html/main.html)  
[MyBatis-Plus](https://mp.baomidou.com/)  
[MarkDown 使用](http://editor.md.ipandao.com/#download)

## 工具
[Git](https://git-scm.com/)  
[Visual Paradigm](https://www.visual-paradigm.com)  
[flyway](https://flywaydb.org/)  
[lombok](https://projectlombok.org/)  
[JSON Editor Online](http://jsoneditoronline.org/#left=local.vusika)  
[postman 及教程](https://www.jianshu.com/p/97ba64888894)



## 脚本
```sql
 create table USER
    (
      ID           BIGINT AUTO_INCREMENT PRIMARY KEY,
      ACCOUNT_ID   VARCHAR(100),
      NAME         VARCHAR(50),
      TOKEN        CHAR(36),
      GMT_CREATE   BIGINT,
      GMT_MODIFIED BIGINT
    );
```
```bash
mvn flyway:migrate
mvn -Dmybatis.generator.overwrite=true mybatis-generator:generate
```

