### 1.如何添加项目所需要的jar包

简介:教你如何在maven使用你项目当中需要的依赖包

（若无法执行Java文件，在src/main/java/com/xdclass右键执行Mark Directory As选择Sources Root

在src/test/java/com/xdclass右键执行Mark Directory As选择Test Sources Root，未解决）

- 原理
  - 在本地，指定一个文件夹，便是maven的仓库，maven会从远程的中央仓库中下载你需要的jar资源到你本地，然后通过maven关联，将jar包依赖到你的项目中，避免了你需要将jar包拷贝到lib中，并通过classpath引入这些jar包的工作。
- 步骤
  - 打开仓库网站
  - 选择你要jar包的信息和版本
  - 填写依赖信息到pom文件
  - 下载到本地仓库
  - 项目使用
- 实战

### 2.如何使用maven运行单元测试

简介:用maven运行一个单元测试

先在Test包下写一个

```
public class AppTest 
{
    @Test
    public void test(){
        System.out.println("welcome to world");
    }
}
```

然后点击compie编译，再点击package打包，最后运行

### 3.如何建立一个web应用

简介:用idea建立一个web项目

左上角new project，选择模板webapp，next，next，finish

左上角File选择Project Structure可新建残缺的文件(需要选择文件类型)

- src
  - main		

    - java 
    - resources 
    - webapp
      - WEB-INF
      - index.jsp
  - test
    - java
    - resources

### 4.如何导入第三方jar包到本地仓库

简介:导入第三方jar包到本地仓库

* 进入cmd命令界面

* 输入指令如下: mvn install:install-file -Dfile=xxXXX -DgroupId=com.alibaba -DartifactId=fastjson -Dversion=xxXXX -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true

* 参数说明
  * Dfile为jar包文件路径
  * Dgroupld一般为jar开发组织的名称，也是坐标groupId
  * DartifactId一般为jar名称，也是坐标artifactId
  * Dversion是版本号
  * Dpackaging是打包类型
  
* 实际命令

  mvn install:install-file -Dfile=C:\Users\hakuou\Downloads\fastjson-1.2.75.jar -DgroupId=com.alibaba -DartifactId=fastjson -Dversion=1.2.75 -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true
  

### 5.常用的maven插件

简介︰告诉你常用的maven插件，并且介绍怎么去更好的去用maven插件

* maven官方插件列表
  * groupId为org.apache.maven.plugins
  *  http://maven.apache.org/plugins/index.html
* 两种方式调用maven插件
  * 将插件目标与生命周期阶段绑定，例如maven默认将maven-compiler-plugin的compile与maven生命周期的compile阶段绑定。
  * 直接在命令行显示指定要执行的插件目标，例如mvn archetype:generate就表示调用maven-archetype-plugin的generate目标。
* 常用的maven插件
  * maven-antrun-plugin
    * maven-antrun-plugin能让用户在Maven项目中运行Ant任务。用户可以直接在该插件的配置以Ant的方式编写Target，然后交给该插件的run目标去执行。
  * maven-archetype-plugin
    * Archtype指项目的骨架，Maven初学者最开始执行的Maven命令可能就是mvn archetype:generate，这实际上就是让maven-archetype-plugin生成一个很简单的项目骨架，帮助开发者快速上手。
  * maven-assembly-plugin
    *  maven-assembly-plugin的用途是制作项目分发包(tar,zip,jar各种包)，该分发包可能包含了项目的可执行文件、源代码、readme、平台脚本等等。
  * maven-dependency-plugin
    * maven-dependency-plugin最大的用途是帮助分析项目依赖 
    * dependency:list能够列出项目最终解析到的依赖列表
    * dependency:tree能进一步的描绘项目依赖树
  * maven-enforcer-plugin
    * maven-enforcer-plugin能够允许你创建一系列规则强制大家遵守，包括设定Java版本、设定Maven版本、禁止某些依赖、禁止SNAPSHOT依赖。
  * maven-help-plugin
    * maven-help-plugin是一个小巧的辅助工具。
    * 最简单的help:system可以打印所有可用的环境变量和Java系统属性。
  * maven-release-plugin
    * maven-release-plugin的用途是帮助自动化项目版本发布，它依赖于
      POM中的SCM信息