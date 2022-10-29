### 1.搭建你的第一个maven私人服务器

简介:搭建属于自己的maven私人服务器

* 背景
  * 回顾下maven的构建流程，如果没有私服，我们所需的所有jar包都需要通过maven的中央仓库或者第三方的maven仓库下载到本地，当一个公司或者一个团队所有人都重复的从maven仓库下载jar包，这样就加大了中央仓库的负载和浪费了外网的带宽，如果网速慢的话还会影响项目的进程。
* 简介
  * 私服是在局域网的一种特殊的远程仓库，目的是代理远程仓库及部署第三方构件。有了私服之后，当Maven需要下载jar包时，先请求私服，私服上如果存在则下载到本地仓库。否则，私服直接请求外部的远程仓库，将jar包下载到私服，再提供给本地仓库下载。
* 安装
  * 我们可以使用专门的Maven仓库管理软件来搭建私服，这里我们使用Nexus
  * 下载地址: https://help.sonatype.com/repomanager2/download
  * Nexus专业版是需要付费的，下载开源版Nexus OSS，最新的是OSS3.x，选择稳定的版本2.X。
* 启动
  * 以管理员身份打开cmd，进入到bin目录，先执行nexus install命令，再执行nexus start。
  * 打开浏览器，访问http://localhost:8081/nexus
  * 点击右上角Log in，使用用户名: admin，密码: admin123登录

点击左边列表Views/Repositories->Repositories确认私服搭建完成

### 2.Nexus私服的秘密花园

简介:介绍nexus服务器预置的仓库

* 类型介绍
  *  hosted:是本地仓库，用户可以把自己的一些jar包，发布到hosted中，比如公司的第二方库
  * proxy，代理仓库，它们被用来代理远程的公共仓库，如maven中央仓库。不允许用户自己上传jar包，只能从中央仓库下载
  *  group，仓库组，用来合并多个hosted/proxy仓库，当你的项目希望在多个repository使用资源时就不需要多次引用了，只需要引用一个group即可
  * virtual,虚拟仓库基本废弃了。
* 预置仓库
  * Central:该仓库代理Maven中央仓库，其策略为Release，因此只会下载和缓存中央仓库中的发布版本构件。
  *  Releases:这是一个策略为Release的宿主类型仓库，用来部署正式发布版本构件
  *  Snapshots:这是一个策略为Snapshot的宿主类型仓库，用来部署开发版本构件。
  * 3rd party:这是一个策略为Release的宿主类型仓库，用来部署无法从maven中央仓库获得的第三方发布版本构件，比如IBM或者oracle的一些jar包（比如classe12.jar)，由于受到商业版权的限制，不允许在中央仓库出现,如果想让这些包在私服上进行管理，就需要第三方的仓库。
  *  Public Repositories:一个组合仓库，点击后选择Configuration可以进行组合

### 3.在nexus建立你的第一个仓库

简介:建立专属于你的第一个仓库

* 建库，上方选择Add-- >Hosted Repository
* 下方填写仓库信息
  *  Respository ID仓库编号
  * Repository NAME仓库名称
  * Repository Type仓库类型【hosted】
  * Repository Policy仓库策略【Release】
  *  Default Local Storage Location仓库路径【C:\Users\hakuou\Downloads\nexus-2.14.21-02-bundle\sonatype-work\nexus\storage】
  * Deployment Policy发布策略【Allow Redeploy】
* 然后选择Public Repositories,打开configuration选项卡，将自己创建的仓库添加到group



### 4.如何将项目发布到maven私服

简介:将项目发布到maven私服

在.m2/settings.xml中添加

```
    <server>
		<id>rantherclass</id>
		<username>admin</username>
		<password>admin123</password>
	</server>
```
```
 	<mirror>
    	<id>nexusMirror</id>
    	<mirrorOf>nexus,central</mirrorOf>
    	<name>local nexus</name>
    	<url>http://localhost:8081/nexus/content/groups/public/</url>
    </mirror>
```

```
	<profile>
    	<id>ranther</id>
      		<repositories>
        		<repository>
					<id>local-nexus</id>
					<url>http://localhost:8081/nexus/content/groups/public/</url>
					<snapshots>
						<enabled>false</enabled>
					</snapshots>
					<releases>
						<enabled>true</enabled>
					</releases>
			</repository>
    	</repositories>
    </profile>
```

```
	<activeProfiles>
		<!--激活了才生效-->
		<activeProfile>ranther</activeProfile>
	</activeProfiles>
```

在idea中的pom文件build下面，project里面添加(可以在nexus自己添加的仓库中点击下方Summary获取)

```
<distributionManagement>
  <repository>
    <id>rantherclass</id>
    <url>http://localhost:8081/nexus/content/repositories/rantherclass</url>
  </repository>
</distributionManagement>
```

执行package打包后，再执行deploy部署即可发布到私服

### 5.maven私服发布和使用之实战

简介:使用maven私服jar包

在私服上找到jar包点击可以查看到dependency的XML，选择复制后，复制到pom文件的dependency处就可以使用了

### 6.maven snapshot的秘密

简介:结合案例介绍和使用snapshot

* 产生背景
  * 假设一个团队工作，其中有个项目叫做data-use，同时他们使用数据服务工程(data-service.jar:1.0)。
    现在负责数据服务的团队可能正在进行修bug或者更新迭代，每次发布都会发布工程到远程仓库中。
    现在如果数据服务团队每天上传新的版本，那么就会有下面的问题:
    * 每次数据服务团队发布了一版更新的代码时，都要告诉应用接口团队。
    * 应用接口团队需要定期更新他们的pom.xml来得到更新的版本
* 什么是快照
  * 快照是一个特殊的版本，它表示当前开发的一个副本。与常规版本不同，
    Maven为每一次构建从远程仓库中检出一份新的快照版本。
* 快照VS版本
  * 对于版本，Maven一旦下载了指定的版本（例如data-service:1.0)，它将不会尝试从仓库里再次下载一个新的1.0版本。想要下载新的代码，数据服务版本需要被升级到1.1。【手动更新】
  * 对于快照，每次用户接口团队构建他们的项目时，Maven将自动获取最新的快照(data-service:1.O-SNAPSHOT) 。【自动更新】
* maven快照延伸
  *  updatePolicy
    * always每次都去远程仓库查看是否有更新
    * daily每天第一次的时候查看是否有更新
    * interval允许设置一个分钟为单位的间隔时间，在这个间隔时间内只会去远程仓库中查找一次
    * never从不会

### 7.maven的依赖管理

简介:依赖是maven最为用户熟知的特性之一，单个项目的依赖管理并不难，但是要管理几个或者几十个模块的时，那这个依赖应该怎么管理

* 依赖的传递性
  
  * 传递性依赖是在maven2中添加的新特征，这个特征的作用就是你不需要考虑你依赖的库文件所需要依赖的库文件，能够将依赖模块的依赖自动的引入。【B依赖A，C依赖B（间接依赖A）】
  
* 依赖的作用范围

  *  compile【常用】

    * 这是默认范围，编译依赖对项目所有的classpath都可用。此外，编译依赖会传递到依赖的项目
  
* provided
  
  * 表示该依赖项将由JDK或者运行容器在运行时提供，也就是说由Maven提供的该依赖项我们只有在编译和测试时才会用到，而在运行时将由JDK或者运行容器提供。
    
  *  runtime

    * 表明编译时不需要依赖,而只在运行时依赖

  *  test【常用】

    * 只在编译测试代码和运行测试的时候需要，应用的正常运行不需要此类依赖。(依赖代码里的作用域scope)
  
  * system【常用】
  
  * 系统范围与provided类似，不过你必须显式指定一个本地系统路径的JAR，此类依赖应该一直有效，Maven也不会去仓库中寻找它。【maven不会从仓库找，直接从指定的本地路径找】
  
    ```
      <project>
    	<dependencies>
      		<dependency>
      			<groupId>sun.jdk</groupId>
      			<artifactId>tools</artifactId>
      			<version>1.5.0</version>
      			<scope>system</scope>
      	<systemPath>${java.home}/ ../lib/tools.jar</systemPath>
      		</dependency>
      	</dependencies>...
      </project>
    ```
  
  * import【常用】
  
  * 范围只适用于<dependencyManagement>部分。表明指定的POM必须使用<dependencyManagement>部分的依赖。因为依赖已经被替换，所以使用import范围的依赖并不影响依赖传递。
  
* 依赖的两大原则

  * 路径近者优先
    A > B > C-1.0

    A > C-2.0

  * 第一声明优先
    A > B > D-1.0

    A > C > D-2.0

* 依赖的管理

  * 依赖排除
    * 任何可传递的依赖都可以通过"exclusion"元素被排除在外。举例说
      明，A依赖B，B依赖C，因此A可以标记C为"被排除的"
      
      在dependency标签下添加
```      
	<exclusions>
		<exclusion>
			<gropuId>xxx</groupId>
			<artifactId>xxx</artifactId>
		</exclusion>
	</exclusions>
```
  * 依赖可选
    * 任何可传递的依赖可以被标记为可选的，通过使用"optional"元素。
      例如:A依赖B，B依赖C。因此，B可以标记C为可选的，这样A就可以不再使用C。
      
      在dependency标签下添加<optional>true</option>

### 8.如何解决jar包冲突

简介:当出现jar包冲突时，如何快速定位和处理jar包冲突间题

* 命令: mvn dependency:tree -Dverbose，可显示jar包依赖关系

