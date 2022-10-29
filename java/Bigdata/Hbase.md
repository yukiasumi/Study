## HBase 安装部署 
### Zookeeper 正常部署 
首先保证 Zookeeper 集群的正常部署，并启动之。 

```shell
bin/zkServer.sh start 
```
### Hadoop 正常部署 
Hadoop 集群的正常部署并启动。 

```shell
sbin/start-dfs.sh 
sbin/start-yarn.sh 
```
### HBase 的解压 
1）解压 Hbase 到指定目录 

```shell
tar -zxvf hbase-2.4.11-bin.tar.gz -C /opt/module/ 
mv /opt/module/hbase-2.4.11 /opt/module/hbase
```
2）配置环境变量 

```shell
sudo vim /etc/profile.d/my_env.sh 
添加 
#HBASE_HOME 
export HBASE_HOME=/opt/module/hbase 
export PATH=$PATH:$HBASE_HOME/bin 
```
3）使用 source 让配置的环境变量生效 
```shell
source /etc/profile.d/my_env.sh 
```
#### HBase 的配置文件 (stand alone)

1）hbase-env.sh 

```shell
export HBASE_PID_DIR=/opt/hbase/pids
export JAVA_HOME=/usr/java/jdk1.8.0_271
export HBASE_MANAGES_ZK=false

```

2)   hbase-site.xml

==切记此处应与Hadoop配置文件的core-site.xml保持一致hdfs://localhost:9000/hbase，否则无法启动Hmaster==

```xml
<configuration>
  <!-- hbase存放数据目录 -->
  <property> 
    <name>hbase.rootdir</name> 
    <value>hdfs://localhost:9000/hbase</value> 
    <description>The directory shared by RegionServers.</description> 
  </property>
  
  <property>
    <name>hbase.cluster.distributed</name>
    <value>true</value> 
</property>
  
     <!-- ZooKeeper host-->
    <property> 
    <name>hbase.zookeeper.quorum</name> 
    <value>Neptune</value>     
	<description>The directory shared by RegionServers</description> 
  </property>

  <!-- ZooKeeper数据文件路径 -->
  <property>
    <name>hbase.zookeeper.property.dataDir</name>
    <value>/data/kafka/zookeeper/data/dataLogDir</value>
  </property>

  <property>
    <name>hbase.unsafe.stream.capability.enforce</name>
    <value>false</value>
    <description>
Controls whether HBase will check for stream capabilities(hflush/hsync).
Disable this if you intend to run on LocalFileSystem, denoted by a rootdir
with the 'file://' scheme, but be mindful of the NOTE below.
WARNING: Setting this to false blinds you to potential data loss and
inconsistent system state in the event of process and/or node failures. If HBase is complaining of an inability to use hsync or hflush it's most likely not a false positive.
    </description>
  </property>
</configuration>

```
3）regionservers 

```shell
Neptune
```
4)	解决jar包冲突问题

```shell
mv $HBASE_HOME/lib/client-facingthirdparty/slf4j-reload4j-1.7.33.jar $HBASE_HOME/lib/clientfacing-thirdparty/slf4j-reload4j-1.7.33.jar.bak 
```
### 启动hbase

```shell
bin/start-hbase.sh
```
==浏览器访问Neptune:16010可打开hbase管理页面==

### 关闭hbase

```shell
bin/stop-hbase.sh
```



## HBase Shell操作

### 基本操作
#### 进入 HBase 客户端命令行 
```shell
 bin/hbase shell
```
#### 查看帮助命令 
能够展示 HBase 中所有能使用的命令，主要使用的命令有 namespace 命令空间相关，
DDL 创建修改表格，DML 写入读取数据。

```
hbase:001:0> help
```
### namespace
#### 创建命名空间 
使用特定的 help 语法能够查看命令如何使用。

```
hbase:002:0> help 'create_namespace'
```

#### 创建命名空间 bigdata 
```sql
hbase:003:0> create_namespace 'bigdata' --查看所有的命名空间 
hbase:004:0> list_namespace
```
### DDL
#### 创建表 

在 bigdata 命名空间中创建表格 student，两个列族。info 列族数据维护的版本数为 5 个，如果不写默认版本数为 1。

```sql
hbase:005:0> create 'bigdata:student', {NAME => 'info', VERSIONS => 5},{NAME => 'msg'}
```
如果创建表格只有一个列族，没有列族属性，可以简写。
如果不写命名空间，使用默认的命名空间 default。

```sql
hbase:009:0> create 'student1','info'
```
#### 查看表 
查看表有两个命令：list 和 describe
list：查看所有的表名

```sql
hbase:013:0> list
```
describe：查看一个表的详情
```sql
hbase:014:0> describe 'student1'
```
#### 修改表 
表名创建时写的所有和列族相关的信息，都可以后续通过 alter 修改，包括增加删除列
族。

（1）增加列族和修改信息都使用覆盖的方法

```sql
hbase:015:0> alter 'student1', {NAME => 'f1', VERSIONS => 3}
```
（2）删除信息使用特殊的语法
```sql
hbase:015:0> alter 'student1', NAME => 'f1', METHOD => 'delete'
hbase:016:0> alter 'student1', 'delete' => 'f1'
```
#### 删除表 
shell 中删除表格,需要先将表格状态设置为不可用。

```sql
hbase:017:0> disable 'student1'
hbase:018:0> drop 'student1'
```
### DML
#### 写入数据
在 HBase 中如果想要写入数据，只能添加结构中最底层的 cell。可以手动写入时间戳指定 cell 的版本，推荐不写默认使用当前的系统时间。

```sql
hbase:019:0> put 'bigdata:student','1001','info:name','zhangsan'
hbase:020:0> put 'bigdata:student','1001','info:name','lisi'
hbase:021:0> put 'bigdata:student','1001','info:age','18'
```
如果重复写入相同 rowKey，相同列的数据，会写入多个版本进行覆盖。

#### 读取数据 
读取数据的方法有两个：get 和 scan。
get 最大范围是一行数据，也可以进行列的过滤，读取数据的结果为多行 cell。
```sql
hbase:022:0> get 'bigdata:student','1001'
hbase:023:0> get 'bigdata:student','1001' , {COLUMN => 'info:name'}
```
也可以修改读取 cell 的版本数，默认读取一个。最多能够读取当前列族设置的维护版本
数。
```sql
hbase:024:0>get 'bigdata:student','1001' , {COLUMN => 'info:name', VERSIONS => 6}
```
scan 是扫描数据，能够读取多行数据，不建议扫描过多的数据，推荐使用 startRow 和
stopRow 来控制读取的数据，默认范围左闭右开。
```sql
hbase:025:0> scan 'bigdata:student',{STARTROW => '1001',STOPROW => '1002'}
```
实际开发中使用 shell 的机会不多，所有丰富的使用方法到 API 中介绍。
#### 删除数据 
删除数据的方法有两个：delete 和 deleteall。
delete 表示删除一个版本的数据，即为 1 个 cell，不填写版本默认删除最新的一个版本。
```sql
hbase:026:0> delete 'bigdata:student','1001','info:name'
```
deleteall 表示删除所有版本的数据，即为当前行当前列的多个 cell。（执行命令会标记
数据为要删除，不会直接将数据彻底删除，删除数据只在特定时期清理磁盘时进行）
```sql
hbase:027:0> deleteall 'bigdata:student','1001','info:name'
```