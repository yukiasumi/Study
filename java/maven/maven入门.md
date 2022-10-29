### 1.配置maven的环境变量

1. 下载maven

2. 配置环境变量
   MAVEN_HOME
   I:\CS\apache-maven-3.8.4-bin\apache-maven-3.8.4

   Path

   %MAVEN_HOME%\bin

3. 检测是否安装成功（cmd命令）
   mvn --version

## 2.maven仓库

Maven仓库的分类
本地仓库:Maven在本地存储的地方
远程仓库：

- 中央仓库

- 私服

- 其他公共库
  		

  ##### 本地仓库:	

  默认的本地仓库是.m2/repository，将conf目录下找到settings.xml的文件复制到.m2/下，以后修改配置都在这修改
  C:\Users\hakuou\.m2\repository
  1.在Maven目下找到conf目录，在conf目录下找到settings.xml的文件
  2.编辑该文件，在该文件中找到<localRepository>本地仓库路径</localRepository>，在其中间添加本地仓库路径
    <localRepository>I:\CS\apache-maven-3.8.4-bin\MavenRepository</localRepository>
  maven的本地仓库，在安装maven后不会被创建，在第一次执行maven命令的时候被创建

  ##### 远程仓库-中央仓库

  ​	最核心的中央仓库开始，中央仓库是默认的远程仓库，maven在安装的时候，自带的就是中央仓库的配置，可以通过修改setting.xml文件来修改默认的中央仓库地址
  ​	中央仓库包含了绝大多数流行的开源Java构件，以及源码、作者信息、SCM、信息、许可证信息等。一般来说，简单的Java项目依赖的构建都可以在这里下载到

  ##### 远程仓库-私服

  ​	公司内部项目组使用的

  ##### 远程仓库-其他公共库

  ​	中央仓库下不到的，可以去公共库下载

### 3.使用IDEA建立一个Maven项目

1. IDEA创建一个project，侧边栏点击Maven，顶部选择SDK，勾选Create from archetype[通过模板创建]，下方有很多模板比如选择 maven-archetype-quickstart，点击next

2. GroupId组织的ID[一个银行组织(包含招商银行，建设银行····)]，ArtifactId项目ID[银行]

   - GroupId ---com.xdclass

   - ArtifactId ---demo

3. 点击next

4. 会显示本地仓库配置文件settings.xml和默认的本地仓库.m2/repository

5. 点击next

6. 点击finish

### 4.maven项目的标准目录结构

- src
  - main		

    - java -java源代码文件
- resources -资源库(maven文件，springboot，框架文件)
    - webapp

      - WEB-INF
    - index.jsp
      - Css、js
    - Bin -脚本库
    - config -配置文件
- Filters - 资源过滤库
  
- test[测试功能相关]
  - java -java测试源代码
  - resources - 测试资源库
  
  - filters -测试资源过滤库
  
- target -存放项目构建后的文件和目录，比如jar包，war包，编译的class文件等

IDEA右侧的maven栏目双击执行demo下的Lifecycle下的install

### 5.maven核心pom文件

简介:介绍maven的pom文件，分析它重要的组成部分

* 什么是pom
  * pom代表项目对象模型，它是Maven中工作的基本组成单位。它是一个XML文件，始终保存在项目的基本目录中的pom.xml文件中。pom包含的对象是使用maven来构建的，pom.xml文件包含了项目的各种配置信息，需要特别注意，每个项目都只有一个pom.xml文件。
* 项目配置信息
  * project:工程的根标签
  *  modelVersion: pom模型版本，maven2和3只能为4.0.0
  * groupId:这是工程组的标识。它在一个组织或者项目中通常是唯一的。例如，一个银行组织com.companyname.project-group拥有所有的和银行相关的项目。
  * artifactld:这是工程的标识。它通常是工程的名称。例如，消费者银行。groupld和artifactId一起定义了artifact在仓库中的位置
  * version:这是工程的版本号。在 artifact的仓库中，它用来区分不同的版本
  *  packaging:定义Maven项目的打包方式，有JAR、WAR和EAR三种格式
* 最小pom

```
<project xmlns="http://maven.apache.org/POM/4.0.0"
xmlns:xsi="http: // www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<groupId>com .xdclass</groupId>
	<artifactId>demo</artifactId>
	<version>1.0-SNAPSHOT</version>
</project>
```



* super pom

  * 父(Super)POM是 Maven默认的POM。所有的POM都继承自一个父POM(不显示)。父POM包含了一些可以被继承的默认设置(类似于Java的object类)。因此，当Maven发现需要下载POM中的依赖时，它会到super POM中配置的默认仓库。

  * 使用以下命令来查看Super POM默认配置:

    ​	1. 右键pom.xml文件

    ​	2. 选择Open in Terminal打开终端

     	3. 执行mvn help:effective-pom命令

* 依赖配置信息

  1. dependencies

     ```
     <dependencies>
     	<dependency>
     		<groupId>junit</groupId>
  			<artifactId>junit</artifactId>
     		<version>4.12</version>
     	</dependency>
     </dependencies>
     ```
     
     打开https://mvnrepository.com/
     
     例如搜索junit
     
     选择版本后会出现maven的dependency代码复制到pom文件中的dependencies下即可使用
     
  2. parent

     ```
     <parent>
     	<groupId>xd.class<groupId>
     	<artifactId>demo-parent</artifactId>
     	<relativePath>/</relativePath>
     	<version>1.0</version>
     </parent>
     ```
     
     - groupId:父项目的组Id标识符
     
     - artifactId:父项目的唯一标识符
     
     - relativePath: Maven首先在当前项目中找父项目的pom，然后在文件系统的这个位
     
       (relativePath)，然后在本地仓库，再在远程仓库找。
     
     - version:父项目的版本
     
  3. modules

     - 有些maven项目会做成多模块的，这个标签用于指定当前项目所包含的所有模块。之后对这个项目进行的maven操作，会让所有子模块也进行相同操作。

       ```
       <modules>
       	<module>com-a</module>
       	<module>com-b</module>
       	<module>com-c</module>
       </modules>
       ```
     
  4. properties

     - 用于定义pom常量

     ```
     <properties>
     	<java.version>1.7</java.version>
     </properties>
     ```

     上面这个常量可以在pom文件的任意地方通过${Java.version}来引用

     Java.version是定义的常量名字

  5. dependencyManagement

     - 应用场景

       - 当我们的项目模块很多的时候，我们依赖包的管理就会出现很多问题，为了项目的正确运行，必须让所有的子项目使用依赖项的同一版本，确保应用的各个项目的依赖项和版本一致，才能保证测试的和发布的是相同的结果。

     - 使用的好处

       - 在父模块中定义后，子模块不会直接使用对应依赖，但是在使用相同依赖的时候可以不加版本号，这样的好处是，可以避免在每个使用的子项目中都声明一个版本号，这样想升级或者切换到另一个版本时，只需要在父类容器里更新，不需要任何一个子项目的修改

    ```
    	父项目:
         <dependencyManagement>
         	<dependencies>
         		<dependency>
         			<groupId>junit</groupId
  				<artifactId>junit</ artifactId>						
  				<version>4.2.0</version>
         			<scope>test</ scope>
         		</dependency>
         	</dependencies>
         </dependencyManagement>
         子项目1:
         <dependency>
         		<groupId>junit</groupId>
         		<artifactId>junit</artifactId>
         </dependency>
         子项目2:
         <dependency>
         		<groupId>junit</groupId>
         		<artifactId>junit</artifactId>
         </dependency>
         子项目3:
         <dependency>
         		<groupId>junit</groupId>
         		<artifactId>junit</ artifactId>
         		<version>5.0</version>可自定义版本号
         </dependency>
    ```

  - 和dependencies的区别
    - dependencies即使在子项目中不写该依赖项，那么子项目仍然会从父项目中继承该依赖项（全部继承)
  - dependencyManagement里只是声明依赖，并不实现引入，因此子项目需要显示的声明需要用的依赖。如果不在子项目中声明依赖，是不会从父项目中继承下来的;只有在子项目中写了该依赖项，并且没有指定具体版本，才会从父项目中继承该项，并且version和scope都读取自父pom;另外如果子项目中指定了版本号，那么会使用子项目中指定的jar版本。

  ### 6.maven的生命周期

  简介:介绍和分析maven的整个生命周期

  IDEA右边的maven栏目已列出所有项目

  * 什么是生命周期
    * Maven的生命周期就是对所有的构建过程进行抽象和统一。包含了项目的清理、初始化、编译、测试、打包、集成测试、验证、部署和站点生成等几乎所有的构建步骤。
  * maven的三个构建生命周期
    * clean
      * pre-clean执行一些清理前需要完成的工作
      * clean清理上一次构建生成的文件
      * post-clean执行一些清理后需要完成的工作
    * default
      * validate:验证工程是否正确
      * compile:编译项目的源代码
      * test:使用合适的单元测试框架来测试已编译的源代码。
      * package:把已编译的代码打包成可以发布的格式，比如jar或者war
      * verify:运行所有检查，验证包是否有效
      * install:安装到maven本地仓库
      * deploy:部署到远程的仓库，使得其他开发者或者工程可以共享
    * site[很少用，生成项目信息，生成maven状态]

  ### 7.常用的maven基本命令

  简介:介绍maven常用的基本命令,并对个别命令重点剖析

  - 常用命令
    - mvn validate验证项目是否正确
    - mvn package maven打包
    -  mvn generate-sources生成源代码
    - mvn compile编译
    -  mvn test-compile编译测试代码
    - mvn test运行测试
    - mvn verify运行检查
    - mvn clean清理项目(清除target目录)
    -  mvn install 安装项目到本地仓库(创建target项目，打包后放到本地仓库)
    - mvn deploy 发布项目到远程仓库
    - mvn dependency:tree 显示Maven依赖树(显示依赖包，树类型)直观，一般用这个
    - mvn dependency:list 显示Maven依赖列表(显示依赖包，列表类型)
  - 常用参数
    -  -D指定参数，如-Dmaven.test.skip=true跳过单元测试;
    - -P指定Profile配置，可以用于区分环境;
  - web相关命令
    - mvn tomcat:run启动tomcat
    - mvn jetty:run启动jetty
    - mvn tomcat:deploy运行打包部署