### 1.实战进阶之使用idea建立Spring Boot项目

简介:怎么建立一个spring boot项目

* 下载企业版idea
* 选择Spring initializr，在Custom里输入https://start.aliyun.com/，点击Next
* 填写项目信息，点击next
* 选择依赖信息
  * 点击Web，勾选SpringWeb Starter
  * 选择SQL，勾选Mysql Driver，JDBC API，MyBatis Framework
  * 点击next
* 填写项目名点击finish

### 2.实战进阶之使用idea导入maven项目

简介:怎么导入一个maven项目，包括本地的项目和git上项目的导入

* 本地maven项目导入
  * import project
  * 选择完项目以后勾选Maven
  * 一直点next，最后finish
  
* 导入git的maven项目

  * 左上角new
  * 选择Project from Version Control
  * 选择Git
  * 复制URL【https://github.com/gatling/gatling-maven-plugin-demo.git】
  * 点击test测试一下【网络问题】

  

### 3.Spring Boot整合Msyql

简介:整合maven+spring boot+mysql

### 4. Spring Boot整合Mybatis和Mysql

简介:整合maven+spring boot+mybatis+mysql

### 5.Spring Boot+Mybatis+Mysql的增删改查

简介:结合例子，通过表的增删改查，熟悉maven