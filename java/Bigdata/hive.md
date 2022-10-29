## 安装Hive

```
tar -zxvf /opt/software/apache-hive-3.1.2-bin.tar.gz -C /opt/module/ 
mv /opt/module/apache-hive-3.1.2-bin/ /opt/module/hive 
sudo vim /etc/profile.d/my_env.sh 
添加以下内容
#HIVE_HOME 
export HIVE_HOME=/opt/module/hive 
export PATH=$PATH:$HIVE_HOME/bin 

source /etc/profile
mv $HIVE_HOME/lib/log4j-slf4j-impl-2.10.0.jar $HIVE_HOME/lib/log4j-slf4j-impl-2.10.0.bak 
```

### 安装Mysql

```
rpm -qa|grep mariadb
sudo rpm -e --nodeps  mariadb-libs
tar -xf mysql-5.7.28-1.el7.x86_64.rpm-bundle.tar
sudo rpm -ivh mysql-community-common-5.7.28-1.el7.x86_64.rpm 
sudo rpm -ivh mysql-community-libs-5.7.28-1.el7.x86_64.rpm 
sudo rpm -ivh mysql-community-libs-compat-5.7.28-1.el7.x86_64.rpm 
sudo rpm -ivh mysql-community-client-5.7.28-1.el7.x86_64.rpm 
sudo rpm -ivh mysql-community-server-5.7.28-1.el7.x86_64.rpm

cd /var/lib/mysql
sudo rm -rf ./*
sudo mysqld --initialize --user=mysql
查看密码
sudo cat /var/log/mysqld.log|grep temporary

sudo systemctl start mysqld
mysql -uroot -p 
set password = password("123456");
update mysql.user set host='%' where user='root';
flush privileges; 
```

#### 配置Hive

```
cp /opt/software/mysql-connector-java-5.1.37.jar $HIVE_HOME/lib 
vim $HIVE_HOME/conf/hive-site.xml
```

```xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
    <!-- jdbc连接的URL -->
    <property>
        <name>javax.jdo.option.ConnectionURL</name>
        <value>jdbc:mysql://hadoop102:3306/metastore?useSSL=false</value>
    </property>

    <!-- jdbc连接的Driver-->
    <property>
        <name>javax.jdo.option.ConnectionDriverName</name>
        <value>com.mysql.jdbc.Driver</value>
    </property>

   <!-- jdbc连接的username-->
    <property>
        <name>javax.jdo.option.ConnectionUserName</name>
        <value>root</value>
    </property>

    <!-- jdbc连接的password -->
    <property>
        <name>javax.jdo.option.ConnectionPassword</name>
        <value>123456</value>
    </property>

    <!-- Hive元数据存储版本的验证 -->
    <property>
        <name>hive.metastore.schema.verification</name>
        <value>false</value>
    </property>

    <!--元数据存储授权-->
    <property>
        <name>hive.metastore.event.db.notification.api.auth</name>
        <value>false</value>
    </property>

    <!-- Hive默认在HDFS的工作目录 -->
    <property>
        <name>hive.metastore.warehouse.dir</name>
        <value>/user/hive/warehouse</value>
    </property>
    <!-- 指定存储元数据要连接的地址 --> 
    <property> 
        <name>hive.metastore.uris</name> 
        <value>thrift://hadoop102:9083</value> 
    </property> 
    
    <!-- 指定hiveserver2连接的host --> 
    <property> 
        <name>hive.server2.thrift.bind.host</name>
        <value>hadoop102</value> 
    </property> 
 
    <!-- 指定hiveserver2连接的端口号 --> 
    <property> 
        <name>hive.server2.thrift.port</name> 
        <value>10000</value> 
    </property> 
    
   <!-- 模拟连接的用户，默认为 true -->
    <property> 
        <name>hive.server2.enable.doAs</name> 
        <value>true</value> 
    </property> 
    
    <!-- 打印当前库和表头 -->
    <property> 
        <name>hive.cli.print.header</name> 
        <value>true</value> 
    </property> 
 
    <property> 
        <name>hive.cli.print.current.db</name> 
        <value>true</value> 
    </property> 
</configuration>

```

```xml
vim $HADOOP_HOME/etc/hadoop/core-site.xml
添加以下内容
		<property>
                <name>hadoop.proxyuser.atguigu.hosts</name>
                <value>*</value>
        </property>
        <property>
                <name>hadoop.proxyuser.atguigu.groups</name>
                <value>*</value>
        </property>

重启Hadoop集群
关闭hdfs安全模式
hdfs dfsadmin -safemode leave
```

==hadoop core-site.xml hdfs地址应改为 localhost/0.0.0.0 才可以访问==

#### 进入hive数据库

```
mysql -uroot -p123456
create database metastore;
schematool -initSchema -dbType mysql verbose

hive --service metastore
hive --service hiveserver2

/tmp/atguigu/hive.log可查看日志

进入beeline客户端
beeline -u jdbc:hive2://hadoop102:10000 -n atguigu 
或
beeline
! connect jdbc:hive2://hadoop102:10000
```

**Hive** 的 **log** 默认存放在**/tmp/atguigu/hive.log** 目录下（当前用户名下）

修改 **hive** 的 **log** 存放日志到**/opt/module/hive/logs** 

```shell
pwd 
/opt/module/hive/conf 
mv hive-log4j2.properties.template hivelog4j2.properties
在 hive-log4j2.properties 文件中修改 log 存放位置 
hive.log.dir=/opt/module/hive/logs 
```

## 报错

### Hive Execution Error, return code 2 from org.apache.hadoop.hive.ql.exec.mr.MapRedTask

#### 1.没有开启hive的metastore服务
```shell
# 这是在hive shell客户端之外执行的，注意注意
hive --service metastore
```
#### 2.集群中各个节点时间不同步导致
```shell
连接各个节点，输入date查看时间信息
date

安装日期同步工具
yum -y install ntp ntpdate

设置日期同步指令
ntpdate cn.pool.ntp.org

或者使用这一条指令
ntpdate -u ntp.api.bz

将系统时间写入硬件时间（主板中会有电池，这个电池耗尽之前，主板时间都会相对精准）
hwclock –w

设置后查看硬件时间
hwclock -show
```

#### 3.执行hive shell指令的集群节点内存耗尽
```
主要查看剩余可用的内存，如果只有200到300Mb，很多时候就会报这个错误。
free -h

解决办法：

可以重新启动hive服务或者重新启动节点机器，这样内存可以被释放出来，
使用linux的内存释放方法
加内存条
使用yarn模式运行，不要使用local模式运行，这样单节点的内存消耗会慢很多
```
#### 由于hadoop 3.2.1和hive 3.1.2不兼容
```
将hadoop 3.2.1适配的hive 3.1.1一起安装，不要安装hive 3.1.2.强行安装在一起会导致内存不回收的问题，本地模式执行一段时间后，内存就会被耗尽。最后导致hive转换的mapreduce程序因为内存耗尽而报错
```

### Error: Error while compiling statement: FAILED: SemanticException Unable to determine if hdfs://localhost:9000/user/hive/warehouse/itcast.db/t_archer is encrypted: org.apache.hadoop.hive.ql.metadata.HiveException: java.net.ConnectException: Call From Neptune/192.168.10.130 to localhost:9000 failed on connection exception: java.net.ConnectException: 拒绝连接; For more details see:  http://wiki.apache.org/hadoop/ConnectionRefused (state=42000,code=40000)

```perl
spark-sql中 Call From DESKTOP-UA4DJL8/192.168.1.7 to 0.0.0.0:9000 failed on connection exception

原因 ：0.0.0.0:9000 为metastore中记录的数据，初始化时记录，后续core-site.xml，hdfs-site.xml有变化
解决方案： 修改metastore的数据，修改MySQL中
hive.CTLGS.LOCATION_URI，
hive.DBS.DB_LOCATION_URI，
hive.SDS.LOCATION字段的hdfs路径，修改为与core-site.xml，hdfs-site.xml一致

```



## DDL

#### 查询/创建/删除数据库

```sql
show databases;
show databases like 'db_hive*';
desc database db_hive; --显示数据库信息
desc database extended db_hive; --显示数据库详细信息

use db_hive; --切换数据库
drop database db_hive2; --删除空库
drop database if exists db_hive2; --判断是否存在
drop database db_hive cascade; --强制删除
```

```sql
--创建数据库并切换使用
create database if not exists itcast;
use itcast;

drop table t_archer;
--ddl create table
create table t_archer(
      id int comment "ID",
      name string comment "英雄名称",
      hp_max int comment "最大生命",
      mp_max int comment "最大法力",
      attack_max int comment "最高物攻",
      defense_max int comment "最大物防",
      attack_range string comment "攻击范围",
      role_main string comment "主要定位",
      role_assist string comment "次要定位"
) comment "王者荣耀射手信息"
row format delimited
fields terminated by "\t";

select *
from t_archer;

desc formatted t_archer;


create table t_hot_hero_skin_price(
      id int,
      name string,
      win_rate int,
      skin_price map<string,int>
)
row format delimited
fields terminated by ',' --字段之间分隔符
collection items terminated by '-'  --集合元素之间分隔符
map keys terminated by ':'; --集合元素kv之间分隔符;


select *
from t_hot_hero_skin_price;

create table t_team_ace_player(
   id int,
   team_name string,
   ace_player_name string
); --没有指定row format语句 此时采用的是默认的\001作为字段的分隔符

select * from t_team_ace_player;



create table t_team_ace_player_location(
 id int,
 team_name string,
 ace_player_name string)
 location '/data'; --使用location关键字指定本张表数据在hdfs上的存储路径

select * from t_team_ace_player_location;

------------------------------------
```
#### 创建表

##### 内部表

```sql

--默认情况下 创建的表就是内部表
create table student(
     num int,
     name string,
     sex string,
     age int,
     dept string)
row format delimited
fields terminated by ',';

--可以使用DESCRIBE FORMATTED itcast.student
-- 来获取表的描述信息，从中可以看出表的类型。
describe formatted itcast.student;
```
##### 外部表
```sql
--创建外部表 需要关键字 external
--外部表数据存储路径不指定 默认规则和内部表一致
--也可以使用location关键字指定HDFS任意路径
create external table student_ext(
   num int,
   name string,
   sex string,
   age int,
   dept string)
row format delimited
fields terminated by ','
location '/stu';

--创建外部表 不指定location
create external table student_ext_nolocation(
                                     num int,
                                     name string,
                                     sex string,
                                     age int,
                                     dept string)
    row format delimited
        fields terminated by ',';

drop table  student_ext_nolocation;

describe formatted itcast.student_ext;


show tables;

select *
from student_ext;

select *
from student;


drop table student;
drop table student_ext;
show tables;


create table t_all_hero(
   id int,
   name string,
   hp_max int,
   mp_max int,
   attack_max int,
   defense_max int,
   attack_range string,
   role_main string,
   role_assist string
)
row format delimited
fields terminated by "\t";

--hadoop fs -put archer.txt assassin.txt mage.txt support.txt tank.txt warrior.txt /user/hive/warehouse/itcast.db/t_all_hero

select * from t_all_hero;


--查询role_main主要定位是射手并且hp_max最大生命大于6000的有几个
select count(*) from t_all_hero where role_main="archer" and hp_max >6000;

```
分区表
```sql

--注意分区表创建语法规则
--分区表建表
create table t_all_hero_part(
   id int,
   name string,
   hp_max int,
   mp_max int,
   attack_max int,
   defense_max int,
   attack_range string,
   role_main string,
   role_assist string
) partitioned by (role string)--注意哦 这里是分区字段
row format delimited
fields terminated by "\t";


select * from t_all_hero_part;

--静态加载分区表数据
load data local inpath '/root/hivedata/archer.txt' into table t_all_hero_part partition(role='sheshou');
load data local inpath '/root/hivedata/assassin.txt' into table t_all_hero_part partition(role='cike');
load data local inpath '/root/hivedata/mage.txt' into table t_all_hero_part partition(role='fashi');
load data local inpath '/root/hivedata/support.txt' into table t_all_hero_part partition(role='fuzhu');
load data local inpath '/root/hivedata/tank.txt' into table t_all_hero_part partition(role='tanke');
load data local inpath '/root/hivedata/warrior.txt' into table t_all_hero_part partition(role='zhanshi');


--非分区表 全表扫描过滤查询
select count(*) from t_all_hero where role_main="archer" and hp_max >6000;
--分区表 先基于分区过滤 再查询
select count(*) from t_all_hero_part where role="sheshou" and hp_max >6000;

-----多重分区表
--单分区表，按省份分区
create table t_user_province (id int, name string,age int) partitioned by (province string);
--双分区表，按省份和市分区
--分区字段之间是一种递进的关系 因此要注意分区字段的顺序 谁在前在后
create table t_user_province_city (id int, name string,age int) partitioned by (province string, city string);

--双分区表的数据加载 静态分区加载数据
load data local inpath '/root/hivedata/user.txt' into table t_user_province_city
    partition(province='zhejiang',city='hangzhou');
load data local inpath '/root/hivedata/user.txt' into table t_user_province_city
    partition(province='zhejiang',city='ningbo');
load data local inpath '/root/hivedata/user.txt' into table t_user_province_city
    partition(province='shanghai',city='pudong');

--双分区表的使用  使用分区进行过滤 减少全表扫描 提高查询效率
select * from t_user_province_city where  province= "zhejiang" and city ="hangzhou";

--动态分区
set hive.exec.dynamic.partition=true;
set hive.exec.dynamic.partition.mode=nonstrict;

--创建一张新的分区表 t_all_hero_part_dynamic
create table t_all_hero_part_dynamic(
    id int,
    name string,
    hp_max int,
    mp_max int,
    attack_max int,
    defense_max int,
    attack_range string,
    role_main string,
    role_assist string
) partitioned by (role string)
row format delimited
fields terminated by "\t";


select * from t_all_hero;

--执行动态分区插入
insert into table t_all_hero_part_dynamic partition(role) --注意这里 分区值并没有手动写死指定
select tmp.*,tmp.role_main from t_all_hero tmp;

select * from t_all_hero_part_dynamic;


--单分区表，按省份分区
create table t_user_province (id int, name string,age int) partitioned by (province string);

--双分区表，按省份和市分区
create table t_user_province_city (id int, name string,age int) partitioned by (province string, city string);

--三分区表，按省份、市、县分区
create table t_user_province_city_county (id int, name string,age int) partitioned by (province string, city string,county string);

--多分区表的数据插入和查询使用
load data local inpath '/root/hivedata/user.txt' into table t_user_province partition(province='shanghai');

load data local inpath '/root/hivedata/user.txt' into table t_user_province_city_county partition(province='zhejiang',city='hangzhou',county='xiaoshan');

select * from t_user_province_city_county where province='zhejiang' and city='hangzhou';
```
##### 分桶表
```sql
--分桶表建表语句
CREATE [EXTERNAL] TABLE [db_name.]table_name
[(col_name data_type, ...)]
CLUSTERED BY (col_name)
INTO N BUCKETS;


CREATE TABLE itcast.t_usa_covid19_bucket(
      count_date string,
      county string,
      state string,
      fips int,
      cases int,
      deaths int)
CLUSTERED BY(state) INTO 5 BUCKETS; --分桶的字段一定要是表中已经存在的字段


--根据state州分为5桶 每个桶内根据cases确诊病例数倒序排序
CREATE TABLE itcast.t_usa_covid19_bucket_sort(
     count_date string,
     county string,
     state string,
     fips int,
     cases int,
     deaths int)
CLUSTERED BY(state)
sorted by (cases desc) INTO 5 BUCKETS;--指定每个分桶内部根据 cases倒序排序


--step1:开启分桶的功能 从Hive2.0开始不再需要设置
set hive.enforce.bucketing=true;

--step2:把源数据加载到普通hive表中
drop table if exists t_usa_covid19;
CREATE TABLE itcast.t_usa_covid19(
       count_date string,
       county string,
       state string,
       fips int,
       cases int,
       deaths int)
row format delimited fields terminated by ",";

--将源数据上传到HDFS，t_usa_covid19表对应的路径下
hadoop fs -put us-covid19-counties.dat /user/hive/warehouse/itcast.db/t_usa_covid19

--step3:使用insert+select语法将数据加载到分桶表中
insert into t_usa_covid19_bucket select * from t_usa_covid19;

select * from t_usa_covid19_bucket;

--基于分桶字段state查询来自于New York州的数据
--不再需要进行全表扫描过滤
--根据分桶的规则hash_function(New York) mod 5计算出分桶编号
--查询指定分桶里面的数据 就可以找出结果  此时是分桶扫描而不是全表扫描
select *
from t_usa_covid19_bucket where state="New York";

select *
from student;
```
##### 事务表
```sql
---Hive事务表
--Step1：创建普通的表
drop table if exists itcast.student;
create table student(
    num int,
    name string,
    sex string,
    age int,
    dept string)
row format delimited
fields terminated by ',';
--Step2：加载数据到普通表中
load data local inpath '/root/hivedata/students.txt' into table itcast.student;
select * from itcast.student;

--Step3：执行更新操作
update student
set age = 66
where num = 95001;


--Hive中事务表的创建使用
--1、开启事务配置（可以使用set设置当前session生效 也可以配置在hive-site.xml中）
set hive.support.concurrency = true; --Hive是否支持并发
set hive.enforce.bucketing = true; --从Hive2.0开始不再需要  是否开启分桶功能
set hive.exec.dynamic.partition.mode = nonstrict; --动态分区模式  非严格
set hive.txn.manager = org.apache.hadoop.hive.ql.lockmgr.DbTxnManager; --
set hive.compactor.initiator.on = true; --是否在Metastore实例上运行启动线程和清理线程
set hive.compactor.worker.threads = 1; --在此metastore实例上运行多少个压缩程序工作线程。

--2、创建Hive事务表
drop table if exists trans_student;
create table trans_student(
   id int,
   name String,
   age int
)clustered by (id) into 2 buckets stored as orc TBLPROPERTIES('transactional'='true');
--注意 事务表创建几个要素：开启参数、分桶表、存储格式orc、表属性

--3、针对事务表进行insert update delete操作
insert into trans_student values(1,"allen",18);

update trans_student
set age = 20
where id = 1;

delete from trans_student where id =1;

select *
from trans_student;
```
##### 视图

```sql
---Hive View视图相关语法
--hive中有一张真实的基础表t_usa_covid19
select *
from itcast.t_usa_covid19;

--1、创建视图
create view v_usa_covid19 as select count_date, county,state,deaths from t_usa_covid19 limit 5;

--能否从已有的视图中创建视图呢  可以的
create view v_usa_covid19_from_view as select * from v_usa_covid19 limit 2;

--2、显示当前已有的视图
show tables;
show views;--hive v2.2.0之后支持

--3、视图的查询使用
select *
from v_usa_covid19;

--能否插入数据到视图中呢？
--不行 报错  SemanticException:A view cannot be used as target table for LOAD or INSERT
insert into v_usa_covid19 select count_date,county,state,deaths from t_usa_covid19;

--4、查看视图定义
show create table v_usa_covid19;

--5、删除视图
drop view v_usa_covid19_from_view;
--6、更改视图属性
alter view v_usa_covid19 set TBLPROPERTIES ('comment' = 'This is a view');
--7、更改视图定义
alter view v_usa_covid19 as  select county,deaths from t_usa_covid19 limit 2;



--通过视图来限制数据访问可以用来保护信息不被随意查询:
create table userinfo(firstname string, lastname string, ssn string, password string);

create view safer_user_info as select firstname, lastname from userinfo;

--可以通过where子句限制数据访问，比如，提供一个员工表视图，只暴露来自特定部门的员工信息:
create table employee(firstname string, lastname string, ssn string, password string, department string);

create view techops_employee as select firstname, lastname, ssn from userinfo where department = 'java';


--使用视图优化嵌套查询
from (
         select * from people join cart
                                   on(cart.pepople_id = people.id) where firstname = 'join'
     )a select a.lastname where a.id = 3;

--把嵌套子查询变成一个视图
create view shorter_join as
select * from people join cart
                          on (cart.pepople_id = people.id) where firstname = 'join';
--基于视图查询
select lastname from shorter_join where id = 3;

```
##### 物化视图

```sql
---Hive 物化视图------------------------------
-- Drops a materialized view
DROP MATERIALIZED VIEW [db_name.]materialized_view_name;
-- Shows materialized views (with optional filters)
SHOW MATERIALIZED VIEWS [IN database_name];
-- Shows information about a specific materialized view
DESCRIBE [EXTENDED | FORMATTED] [db_name.]materialized_view_name;

ALTER MATERIALIZED VIEW [db_name.]materialized_view_name REBUILD;
ALTER MATERIALIZED VIEW [db_name.]materialized_view_name ENABLE|DISABLE REWRITE;


--1、新建一张事务表 student_trans
set hive.support.concurrency = true; --Hive是否支持并发
set hive.enforce.bucketing = true; --从Hive2.0开始不再需要  是否开启分桶功能
set hive.exec.dynamic.partition.mode = nonstrict; --动态分区模式  非严格
set hive.txn.manager = org.apache.hadoop.hive.ql.lockmgr.DbTxnManager; --
set hive.compactor.initiator.on = true; --是否在Metastore实例上运行启动线程和清理线程
set hive.compactor.worker.threads = 1; --在此metastore实例上运行多少个压缩程序工作线程。

drop table if exists  student_trans;

CREATE TABLE student_trans (
      sno int,
      sname string,
      sdept string)
clustered by (sno) into 2 buckets stored as orc TBLPROPERTIES('transactional'='true');

--2、导入数据到student_trans中
insert overwrite table student_trans
select num,name,dept
from student;

select *
from student_trans;

--3、对student_trans建立聚合物化视图
CREATE MATERIALIZED VIEW student_trans_agg
AS SELECT sdept, count(*) as sdept_cnt from student_trans group by sdept;

--注意 这里当执行CREATE MATERIALIZED VIEW，会启动一个MR对物化视图进行构建
--可以发现当下的数据库中有了一个物化视图
show tables;
show materialized views;

--4、对原始表student_trans查询
--由于会命中物化视图，重写query查询物化视图，查询速度会加快（没有启动MR，只是普通的table scan）
SELECT sdept, count(*) as sdept_cnt from student_trans group by sdept;

--5、查询执行计划可以发现 查询被自动重写为TableScan alias: itcast.student_trans_agg
--转换成了对物化视图的查询  提高了查询效率
explain SELECT sdept, count(*) as sdept_cnt from student_trans group by sdept;


--验证禁用物化视图自动重写
ALTER MATERIALIZED VIEW student_trans_agg DISABLE REWRITE;

--删除物化视图
drop materialized view student_trans_agg;
```
### 总结
```sql

-------------------Database 数据库 DDL操作---------------------------------------
--创建数据库
create database if not exists itcast
comment "this is my first db"
with dbproperties ('createdBy'='Allen');

--描述数据库信息
describe database itcast;
describe database extended itcast;
desc database extended itcast;

--切换数据库
use default;
use itcast;
create table t_1(id int);

--删除数据库
--注意 CASCADE关键字慎重使用
DROP (DATABASE|SCHEMA) [IF EXISTS] database_name [RESTRICT|CASCADE];
drop database itcast cascade ;


--更改数据库属性
ALTER (DATABASE|SCHEMA) database_name SET DBPROPERTIES (property_name=property_value, ...);
--更改数据库所有者
ALTER (DATABASE|SCHEMA) database_name SET OWNER [USER|ROLE] user_or_role;
--更改数据库位置
ALTER (DATABASE|SCHEMA) database_name SET LOCATION hdfs_path;


-------------------Table 表 DDL操作---------------------------------------

--查询指定表的元数据信息
describe formatted itcast.student_partition;

--1、更改表名
ALTER TABLE table_name RENAME TO new_table_name;
--2、更改表属性
ALTER TABLE table_name SET TBLPROPERTIES (property_name = property_value, ... );
--更改表注释
ALTER TABLE student SET TBLPROPERTIES ('comment' = "new comment for student table");
--3、更改SerDe属性
ALTER TABLE table_name SET SERDE serde_class_name [WITH SERDEPROPERTIES (property_name = property_value, ... )];
ALTER TABLE table_name [PARTITION partition_spec] SET SERDEPROPERTIES serde_properties;
ALTER TABLE table_name SET SERDEPROPERTIES ('field.delim' = ',');
--移除SerDe属性
ALTER TABLE table_name [PARTITION partition_spec] UNSET SERDEPROPERTIES (property_name, ... );

--4、更改表的文件存储格式 该操作仅更改表元数据。现有数据的任何转换都必须在Hive之外进行。
ALTER TABLE table_name  SET FILEFORMAT file_format;
--5、更改表的存储位置路径
ALTER TABLE table_name SET LOCATION "new location";

--6、更改列名称/类型/位置/注释
CREATE TABLE test_change (a int, b int, c int);
// First change column a's name to a1.
ALTER TABLE test_change CHANGE a a1 INT;
// Next change column a1's name to a2, its data type to string, and put it after column b.
ALTER TABLE test_change CHANGE a1 a2 STRING AFTER b;
// The new table's structure is:  b int, a2 string, c int.
// Then change column c's name to c1, and put it as the first column.
ALTER TABLE test_change CHANGE c c1 INT FIRST;
// The new table's structure is:  c1 int, b int, a2 string.
// Add a comment to column a1
ALTER TABLE test_change CHANGE a1 a1 INT COMMENT 'this is column a1';

--7、添加/替换列
--使用ADD COLUMNS，您可以将新列添加到现有列的末尾但在分区列之前。
--REPLACE COLUMNS 将删除所有现有列，并添加新的列集。
ALTER TABLE table_name ADD|REPLACE COLUMNS (col_name data_type,...);


-------------------Partition分区 DDL操作---------------------------------------
--1、增加分区
--step1: 创建表 手动加载分区数据
drop table if exists t_user_province;
create table t_user_province (
    num int,
    name string,
    sex string,
    age int,
    dept string) partitioned by (province string);

load data local inpath '/root/hivedata/students.txt' into table t_user_province partition(province ="SH");

--step2：添加一个分区
ALTER TABLE t_user_province ADD PARTITION (province='BJ') location
    '/user/hive/warehouse/itcast.db/t_user_province/province=BJ';

--step3:必须自己把数据加载到增加的分区中 hive不会帮你添加


----此外还支持一次添加多个分区
ALTER TABLE table_name ADD PARTITION (dt='2008-08-08', country='us') location '/path/to/us/part080808'
    PARTITION (dt='2008-08-09', country='us') location '/path/to/us/part080809';


--2、重命名分区
ALTER TABLE t_user_province PARTITION (province ="SH") RENAME TO PARTITION (province ="Shanghai");

--3、删除分区
ALTER TABLE table_name DROP [IF EXISTS] PARTITION (dt='2008-08-08', country='us');
ALTER TABLE table_name DROP [IF EXISTS] PARTITION (dt='2008-08-08', country='us') PURGE; --直接删除数据 不进垃圾桶

--4、修复分区
MSCK [REPAIR] TABLE table_name [ADD/DROP/SYNC PARTITIONS];


--5、修改分区
--更改分区文件存储格式
ALTER TABLE table_name PARTITION (dt='2008-08-09') SET FILEFORMAT file_format;
--更改分区位置
ALTER TABLE table_name PARTITION (dt='2008-08-09') SET LOCATION "new location";


-----MSCK 修复分区---------------
--Step1：创建分区表
create table t_all_hero_part_msck(
                                     id int,
                                     name string,
                                     hp_max int,
                                     mp_max int,
                                     attack_max int,
                                     defense_max int,
                                     attack_range string,
                                     role_main string,
                                     role_assist string
) partitioned by (role string)
    row format delimited
        fields terminated by "\t";

--Step2：在linux上，使用HDFS命令创建分区文件夹
hadoop fs -mkdir -p /user/hive/warehouse/itcast.db/t_all_hero_part_msck/role=sheshou
hadoop fs -mkdir -p /user/hive/warehouse/itcast.db/t_all_hero_part_msck/role=tanke

--Step3：把数据文件上传到对应的分区文件夹下
hadoop fs -put archer.txt /user/hive/warehouse/itcast.db/t_all_hero_part_msck/role=sheshou
hadoop fs -put tank.txt /user/hive/warehouse/itcast.db/t_all_hero_part_msck/role=tanke

--Step4：查询表 可以发现没有数据
select * from t_all_hero_part_msck;
--Step5：使用MSCK命令进行修复
--add partitions可以不写 因为默认就是增加分区
MSCK repair table t_all_hero_part_msck add partitions;


--Step1：直接使用HDFS命令删除分区表的某一个分区文件夹
hadoop fs -rm -r /user/hive/warehouse/itcast.db/t_all_hero_part_msck/role=sheshou

--Step2：查询发现还有分区信息
--因为元数据信息没有删除
show partitions t_all_hero_part_msck;

--Step3：使用MSCK命令进行修复
MSCK repair table t_all_hero_part_msck drop partitions;





--1、显示所有数据库 SCHEMAS和DATABASES的用法 功能一样
show databases;
show schemas;

--2、显示当前数据库所有表/视图/物化视图/分区/索引
show tables;
SHOW TABLES [IN database_name]; --指定某个数据库

--3、显示当前数据库下所有视图
Show Views;
SHOW VIEWS 'test_*'; -- show all views that start with "test_"
SHOW VIEWS FROM test1; -- show views from database test1
SHOW VIEWS [IN/FROM database_name];

--4、显示当前数据库下所有物化视图
SHOW MATERIALIZED VIEWS [IN/FROM database_name];

--5、显示表分区信息，分区按字母顺序列出，不是分区表执行该语句会报错
show partitions table_name;
show partitions itcast.student_partition;

--6、显示表/分区的扩展信息
SHOW TABLE EXTENDED [IN|FROM database_name] LIKE table_name;
show table extended like student;
describe formatted itcast.student;

--7、显示表的属性信息
SHOW TBLPROPERTIES table_name;
show tblproperties student;

--8、显示表、视图的创建语句
SHOW CREATE TABLE ([db_name.]table_name|view_name);
show create table student;

--9、显示表中的所有列，包括分区列。
SHOW COLUMNS (FROM|IN) table_name [(FROM|IN) db_name];
show columns  in student;

--10、显示当前支持的所有自定义和内置的函数
show functions;

--11、Describe desc
--查看表信息
desc extended table_name;
--查看表信息（格式化美观）
desc formatted table_name;
--查看数据库相关信息
describe database database_name;

```

## DML

### load
```sql
show databases ;

------load语法规则----
LOAD DATA [LOCAL] INPATH 'filepath' [OVERWRITE] INTO TABLE tablename [PARTITION (partcol1=val1, partcol2=val2 ...)]

LOAD DATA [LOCAL] INPATH 'filepath' [OVERWRITE] INTO TABLE tablename [PARTITION (partcol1=val1, partcol2=val2 ...)] [INPUTFORMAT 'inputformat' SERDE 'serde'] (3.0 or later)


--------练习:Load Data From Local FS or HDFS------
--step1:建表
--建表student_local 用于演示从本地加载数据
create table student_local(num int,name string,sex string,age int,dept string) row format delimited fields terminated by ',';
--建表student_HDFS  用于演示从HDFS加载数据
create external table student_HDFS(num int,name string,sex string,age int,dept string) row format delimited fields terminated by ',';
--建表student_HDFS_p 用于演示从HDFS加载数据到分区表
create table student_HDFS_p(num int,name string,sex string,age int,dept string) partitioned by(country string) row format delimited fields terminated by ',';

--建议使用beeline客户端 可以显示出加载过程日志信息
--step2:加载数据
-- 从本地加载数据  数据位于HS2（node1）本地文件系统  本质是hadoop fs -put上传操作
LOAD DATA LOCAL INPATH '/root/hivedata/students.txt' INTO TABLE student_local;

--从HDFS加载数据  数据位于HDFS文件系统根目录下  本质是hadoop fs -mv 移动操作
--先把数据上传到HDFS上  hadoop fs -put /root/hivedata/students.txt /
LOAD DATA INPATH '/students.txt' INTO TABLE student_HDFS;

----从HDFS加载数据到分区表中并制定分区  数据位于HDFS文件系统根目录下
--先把数据上传到HDFS上 hadoop fs -put /root/hivedata/students.txt /
LOAD DATA INPATH '/students.txt' INTO TABLE student_HDFS_p partition(country ="China");


-------hive 3.0 load命令新特性------------------
CREATE TABLE if not exists tab1 (col1 int, col2 int)
PARTITIONED BY (col3 int)
row format delimited fields terminated by ',';

--正常情况下  数据格式如下
11,22
33,44
LOAD DATA LOCAL INPATH '/root/hivedata/xxx.txt' INTO TABLE tab1 partition(col3="1");

--在hive3.0之后 新特性可以帮助我们把load改写为insert as select
LOAD DATA LOCAL INPATH '/root/hivedata/tab1.txt' INTO TABLE tab1;

--tab1.txt内容如下
11,22,1
33,44,2

select * from tab1;

```
### insert
```sql
INSERT INTO table_name ( field1, field2,...fieldN )
VALUES
( value1, value2,...valueN );

---------hive中insert+values----------------
create table t_test_insert(id int,name string,age int);

insert into table t_test_insert values(1,"allen",18);

select * from t_test_insert;

----------hive中insert+select-----------------
--语法规则
INSERT OVERWRITE TABLE tablename1 [PARTITION (partcol1=val1, partcol2=val2 ...) [IF NOT EXISTS]] select_statement1 FROM from_statement;

INSERT INTO TABLE tablename1 [PARTITION (partcol1=val1, partcol2=val2 ...)] select_statement1 FROM from_statement;


--step1:创建一张源表student
drop table if exists student;
create table student(num int,name string,sex string,age int,dept string)
row format delimited
fields terminated by ',';
--加载数据
load data local inpath '/root/hivedata/students.txt' into table student;

select * from student;

--step2：创建一张目标表  只有两个字段
create table student_from_insert(sno int,sname string);
--使用insert+select插入数据到新表中
insert into table student_from_insert
select num,name from student;

select *
from student_from_insert;


------------multiple inserts----------------------
--当前库下已有一张表student
select * from student;
--创建两张新表
create table student_insert1(sno int);
create table student_insert2(sname string);

--正常思路来讲
insert into student_insert1
select num  from student;

insert into student_insert2
select name  from student;




--多重插入  一次扫描 多次插入
from student
insert overwrite table student_insert1
select num
insert overwrite table student_insert2
select name;


---------------动态分区插入--------------------
--背景：静态分区
drop table if exists student_HDFS_p;
create table student_HDFS_p(Sno int,Sname string,Sex string,Sage int,Sdept string) partitioned by(country string) row format delimited fields terminated by ',';
--注意 分区字段country的值是在导入数据的时候手动指定的 China
LOAD DATA INPATH '/students.txt' INTO TABLE student_HDFS_p partition(country ="China");


FROM page_view_stg pvs
INSERT OVERWRITE TABLE page_view PARTITION(dt='2008-06-08', country)
SELECT pvs.viewTime, pvs.userid, pvs.page_url, pvs.referrer_url, null, null, pvs.ip, pvs.cnt
--在这里，country分区将由SELECT子句（即pvs.cnt）的最后一列动态创建。
--而dt分区是手动指定写死的。
--如果是nonstrict模式下，dt分区也可以动态创建。

-----------案例：动态分区插入-----------
--1、首先设置动态分区模式为非严格模式 默认已经开启了动态分区功能
set hive.exec.dynamic.partition = true;
set hive.exec.dynamic.partition.mode = nonstrict;

--2、当前库下已有一张表student
select * from student;

--3、创建分区表 以sdept作为分区字段
create table student_partition(Sno int,Sname string,Sex string,Sage int) partitioned by(Sdept string);

--4、执行动态分区插入操作
insert into table student_partition partition(Sdept)
select num,name,sex,age,dept from student;
--其中，num,name,sex,age作为表的字段内容插入表中
--dept作为分区字段值

select *
from student_partition;

show partitions student_partition;
```

### unload导出数据
```sql
-----------------导出数据-------------------
--标准语法:
INSERT OVERWRITE [LOCAL] DIRECTORY directory1
    [ROW FORMAT row_format] [STORED AS file_format] (Note: Only available starting with Hive 0.11.0)
SELECT ... FROM ...

--Hive extension (multiple inserts):
FROM from_statement
INSERT OVERWRITE [LOCAL] DIRECTORY directory1 select_statement1
[INSERT OVERWRITE [LOCAL] DIRECTORY directory2 select_statement2] ...

--row_format
: DELIMITED [FIELDS TERMINATED BY char [ESCAPED BY char]] [COLLECTION ITEMS TERMINATED BY char]
[MAP KEYS TERMINATED BY char] [LINES TERMINATED BY char]


--导出操作演示
--当前库下已有一张表student
select * from student;

--1、导出查询结果到HDFS指定目录下
insert overwrite directory '/tmp/hive_export/e1' select num,name,age from student limit 2;

--2、导出时指定分隔符和文件存储格式
insert overwrite directory '/tmp/hive_export/e2' row format delimited fields terminated by ','
stored as orc
select * from student;

--3、导出数据到本地文件系统指定目录下
insert overwrite local directory '/root/hive_export/e1' select * from student;

```
### 事务表
```sql
--Hive中事务表的创建使用
--1、开启事务配置（可以使用set设置当前session生效 也可以配置在hive-site.xml中）
set hive.support.concurrency = true; --Hive是否支持并发
set hive.enforce.bucketing = true; --从Hive2.0开始不再需要  是否开启分桶功能
set hive.exec.dynamic.partition.mode = nonstrict; --动态分区模式  非严格
set hive.txn.manager = org.apache.hadoop.hive.ql.lockmgr.DbTxnManager; --
set hive.compactor.initiator.on = true; --是否在Metastore实例上运行启动压缩合并
set hive.compactor.worker.threads = 1; --在此metastore实例上运行多少个压缩程序工作线程。


--事务表的创建
CREATE TABLE emp (id int, name string, salary int)
STORED AS ORC TBLPROPERTIES ('transactional' = 'true');

--事务表 insert  -->delta文件
INSERT INTO emp VALUES
(1, 'Jerry', 5000),
(2, 'Tom',   8000),
(3, 'Kate',  6000);

select * from emp;

--再次insert  --->delta文件
INSERT INTO emp VALUES(4, 'Allen', 8000);

--执行delete --> delete-delta文件
delete from emp where id =2;

--显示有关当前运行的压缩和最近的压缩历史
Show Compactions;




--2、创建Hive事务表
create table trans_student(
                              id int,
                              name String,
                              age int
)stored as orc TBLPROPERTIES('transactional'='true');

describe formatted trans_student;

--3、针对事务表进行insert update delete操作
insert into trans_student (id, name, age)
values (1,"allen",18);

select *
from trans_student;

describe formatted trans_student;

update trans_student
set age = 20
where id = 1;

delete from trans_student where id =1;

select *
from trans_student;


show tables;

select * from student_local;

update student_local
set  age= 35
where num =95001;
```

```sql

---------select语法树------------
[WITH CommonTableExpression (, CommonTableExpression)*]
SELECT [ALL | DISTINCT] select_expr, select_expr, ...
  FROM table_reference
  [WHERE where_condition]
  [GROUP BY col_list]
  [ORDER BY col_list]
  [CLUSTER BY col_list
    | [DISTRIBUTE BY col_list] [SORT BY col_list]
  ]
 [LIMIT [offset,] rows];


------------案例：美国Covid-19新冠数据之select查询---------------
--step1:创建普通表t_usa_covid19
drop table if exists t_usa_covid19;
CREATE TABLE t_usa_covid19(
       count_date string,
       county string,
       state string,
       fips int,
       cases int,
       deaths int)
row format delimited fields terminated by ",";
--将源数据load加载到t_usa_covid19表对应的路径下
load data local inpath '/root/hivedata/us-covid19-counties.dat' into table t_usa_covid19;

select * from t_usa_covid19;

--step2:创建一张分区表 基于count_date日期,state州进行分区
CREATE TABLE if not exists t_usa_covid19_p(
     county string,
     fips int,
     cases int,
     deaths int)
partitioned by(count_date string,state string)
row format delimited fields terminated by ",";

--step3:使用动态分区插入将数据导入t_usa_covid19_p中
set hive.exec.dynamic.partition.mode = nonstrict;

insert into table t_usa_covid19_p partition (count_date,state)
select county,fips,cases,deaths,count_date,state from t_usa_covid19;
```

### select
#### select_expr
```sql
---------------Hive SQL select查询基础语法------------------

--1、select_expr
--查询所有字段或者指定字段
select * from t_usa_covid19_p;
select county, cases, deaths from t_usa_covid19_p;
--查询匹配正则表达式的所有字段
SET hive.support.quoted.identifiers = none; --反引号不在解释为其他含义，被解释为正则表达式
select `^c.*` from t_usa_covid19_p;
--查询当前数据库
select current_database(); --省去from关键字
--查询使用函数
select count(county) from t_usa_covid19_p;
```
#### ALL DISTINCT
```sql
--2、ALL DISTINCT
--返回所有匹配的行
select state from t_usa_covid19_p;
--相当于
select all state from t_usa_covid19_p;
--返回所有匹配的行 去除重复的结果
select distinct state from t_usa_covid19_p;
--多个字段distinct 整体去重
select  county,state from t_usa_covid19_p;
select distinct county,state from t_usa_covid19_p;
select distinct sex from student;
```
#### WHERE CAUSE
```sql
--3、WHERE CAUSE
select * from t_usa_covid19_p where 1 > 2;  -- 1 > 2 返回false
select * from t_usa_covid19_p where 1 = 1;  -- 1 = 1 返回true
--where条件中使用函数 找出州名字母长度超过10位的有哪些
select * from t_usa_covid19_p where length(state) >10 ;
--where子句支持子查询
SELECT *
FROM A
WHERE A.a IN (SELECT foo FROM B);

--注意：where条件中不能使用聚合函数
--报错 SemanticException:Not yet supported place for UDAF 'count'
--聚合函数要使用它的前提是结果集已经确定。
--而where子句还处于“确定”结果集的过程中，因而不能使用聚合函数。
select state,count(deaths)
from t_usa_covid19_p where count(deaths) >100 group by state;

--可以使用Having实现
select state,count(deaths)
from t_usa_covid19_p  group by state
having count(deaths) > 100;
```
#### 分区查询、分区裁剪
```sql
--4、分区查询、分区裁剪
--找出来自加州，累计死亡人数大于1000的县 state字段就是分区字段 进行分区裁剪 避免全表扫描
select * from t_usa_covid19_p where state ="California" and deaths > 1000;
--多分区裁剪
select * from t_usa_covid19_p where count_date = "2021-01-28" and state ="California" and deaths > 1000;
```
#### GROUP BY
```
--5、GROUP BY
--根据state州进行分组
--SemanticException:Expression not in GROUP BY key 'deaths'
--deaths不是分组字段 报错
--state是分组字段 可以直接出现在select_expr中
select state,deaths
from t_usa_covid19_p where count_date = "2021-01-28" group by state;

--被聚合函数应用
select state,sum(deaths)
from t_usa_covid19_p where count_date = "2021-01-28" group by state;

```
#### having
```sql
--6、having
--统计死亡病例数大于10000的州
--where语句中不能使用聚合函数 语法报错
select state,sum(deaths)
from t_usa_covid19_p where count_date = "2021-01-28" and sum(deaths) >10000 group by state;

--先where分组前过滤（此处是分区裁剪），再进行group by分组， 分组后每个分组结果集确定 再使用having过滤
select state,sum(deaths)
from t_usa_covid19_p
where count_date = "2021-01-28"
group by state
having sum(deaths) > 10000;

--这样写更好 即在group by的时候聚合函数已经作用得出结果 having直接引用结果过滤 不需要再单独计算一次了
select state,sum(deaths) as cnts
from t_usa_covid19_p
where count_date = "2021-01-28"
group by state
having cnts> 10000;
```
#### limit
```sql
--7、limit
--没有限制返回2021.1.28 加州的所有记录
select * from t_usa_covid19_p
where count_date = "2021-01-28"
and state ="California";

--返回结果集的前5条
select * from t_usa_covid19_p
where count_date = "2021-01-28"
  and state ="California"
limit 5;

--返回结果集从第1行开始 共3行
select * from t_usa_covid19_p
where count_date = "2021-01-28"
and state ="California"
limit 2,3; --注意 第一个参数偏移量是从0开始的
```
### select高阶
#### order by
```sql
---------------Hive SQL select查询高阶语法------------------
---1、order by
--根据字段进行排序
select * from t_usa_covid19_p
where count_date = "2021-01-28"
and state ="California"
order by deaths ; --默认asc, nulls first 也可以手动指定nulls last

select * from t_usa_covid19_p
where count_date = "2021-01-28"
and state ="California"
order by deaths desc; --指定desc nulls last

--强烈建议将LIMIT与ORDER BY一起使用。避免数据集行数过大
--当hive.mapred.mode设置为strict严格模式时，使用不带LIMIT的ORDER BY时会引发异常。
select * from t_usa_covid19_p
where count_date = "2021-01-28"
  and state ="California"
order by deaths desc
limit 3;
```
#### cluster by
```sql
--2、cluster by
select * from student;
--不指定reduce task个数
--日志显示：Number of reduce tasks not specified. Estimated from input data size: 1
select * from student cluster by num;

--手动设置reduce task个数
set mapreduce.job.reduces =2;
select * from student cluster by num;
```
#### distribute by +sort by
```sql
--3、distribute by +sort by
--案例：把学生表数据根据性别分为两个部分，每个分组内根据年龄的倒序排序。
--错误
select * from student cluster by sex order by age desc;
select * from student cluster by sex sort by age desc;

--正确
select * from student distribute by sex sort by age desc;

--下面两个语句执行结果一样
select * from student distribute by num sort by num;
select * from student cluster by num;
```
#### Union联合查询
```sql

---------------Union联合查询----------------------------
--语法规则
select_statement UNION [ALL | DISTINCT] select_statement UNION [ALL | DISTINCT] select_statement ...;

--使用DISTINCT关键字与使用UNION默认值效果一样，都会删除重复行。
select num,name from student_local
UNION
select num,name from student_hdfs;
--和上面一样
select num,name from student_local
UNION DISTINCT
select num,name from student_hdfs;

--使用ALL关键字会保留重复行。
select num,name from student_local
UNION ALL
select num,name from student_hdfs limit 2;

--如果要将ORDER BY，SORT BY，CLUSTER BY，DISTRIBUTE BY或LIMIT应用于单个SELECT
--请将子句放在括住SELECT的括号内
SELECT num,name FROM (select num,name from student_local LIMIT 2)  subq1
UNION
SELECT num,name FROM (select num,name from student_hdfs LIMIT 3) subq2;

--如果要将ORDER BY，SORT BY，CLUSTER BY，DISTRIBUTE BY或LIMIT子句应用于整个UNION结果
--请将ORDER BY，SORT BY，CLUSTER BY，DISTRIBUTE BY或LIMIT放在最后一个之后。
select num,name from student_local
UNION
select num,name from student_hdfs
order by num desc;
```
#### 子查询Subqueries
```sql
------------子查询Subqueries--------------

--from子句中子查询（Subqueries）
--子查询
SELECT num
FROM (
         select num,name from student_local
     ) tmp;

--包含UNION ALL的子查询的示例
SELECT t3.name
FROM (
         select num,name from student_local
         UNION distinct
         select num,name from student_hdfs
     ) t3;


--where子句中子查询（Subqueries）
--不相关子查询，相当于IN、NOT IN,子查询只能选择一个列。
--（1）执行子查询，其结果不被显示，而是传递给外部查询，作为外部查询的条件使用。
--（2）执行外部查询，并显示整个结果。　　
SELECT *
FROM student_hdfs
WHERE student_hdfs.num IN (select num from student_local limit 2);

--相关子查询，指EXISTS和NOT EXISTS子查询
--子查询的WHERE子句中支持对父查询的引用
SELECT A
FROM T1
WHERE EXISTS (SELECT B FROM T2 WHERE T1.X = T2.Y);

```
#### Common Table Expressions（CTE）
```sql
-----------------Common Table Expressions（CTE）-----------------------------------
--select语句中的CTE
with q1 as (select num,name,age from student where num = 95002)
select *
from q1;

-- from风格
with q1 as (select num,name,age from student where num = 95002)
from q1
select *;

-- chaining CTEs 链式
with q1 as ( select * from student where num = 95002),
     q2 as ( select num,name,age from q1)
select * from (select num from q2) a;


-- union
with q1 as (select * from student where num = 95002),
     q2 as (select * from student where num = 95004)
select * from q1 union all select * from q2;

--视图，CTAS和插入语句中的CTE
-- insert
create table s1 like student;

with q1 as ( select * from student where num = 95002)
from q1
insert overwrite table s1
select *;

select * from s1;

-- ctas
create table s2 as
with q1 as ( select * from student where num = 95002)
select * from q1;

-- view
create view v1 as
with q1 as ( select * from student where num = 95002)
select * from q1;

select * from v1;

```
### join
```sql
-------------------Hive join语法规则树------------------------------
join_table:
    table_reference [INNER] JOIN table_factor [join_condition]
  | table_reference {LEFT|RIGHT|FULL} [OUTER] JOIN table_reference join_condition
  | table_reference LEFT SEMI JOIN table_reference join_condition
  | table_reference CROSS JOIN table_reference [join_condition] (as of Hive 0.10)

join_condition:
    ON expression
-------------------
--隐式联接表示法
SELECT *
FROM table1 t1, table2 t2, table3 t3
WHERE t1.id = t2.id AND t2.id = t3.id AND t1.zipcode = '02535';

--支持非等值连接
SELECT a.* FROM a JOIN b ON (a.id = b.id)
SELECT a.* FROM a JOIN b ON (a.id = b.id AND a.department = b.department)
SELECT a.* FROM a LEFT OUTER JOIN b ON (a.id <> b.id)
-------------------


--Join语法练习 建表
drop table if exists employee_address;
drop table if exists employee_connection;
drop table if exists employee;

--table1: 员工表
CREATE TABLE employee(
   id int,
   name string,
   deg string,
   salary int,
   dept string
 ) row format delimited
fields terminated by ',';

--table2:员工家庭住址信息表
CREATE TABLE employee_address (
    id int,
    hno string,
    street string,
    city string
) row format delimited
fields terminated by ',';

--table3:员工联系方式信息表
CREATE TABLE employee_connection (
    id int,
    phno string,
    email string
) row format delimited
fields terminated by ',';

--加载数据到表中
load data local inpath '/root/hivedata/employee.txt' into table employee;
load data local inpath '/root/hivedata/employee_address.txt' into table employee_address;
load data local inpath '/root/hivedata/employee_connection.txt' into table employee_connection;

select *
from employee;

select *
from employee_address;

select *
from employee_connection;
```
#### inner join
```sql
----------Hive join----------

--1、inner join
select e.id,e.name,e_a.city,e_a.street
from employee e inner join employee_address e_a
on e.id =e_a.id;
--等价于 inner join=join
select e.id,e.name,e_a.city,e_a.street
from employee e join employee_address e_a
on e.id =e_a.id;

--等价于 隐式连接表示法
select e.id,e.name,e_a.city,e_a.street
from employee e , employee_address e_a
where e.id =e_a.id;
```
#### left join
```sql
--2、left join
select e.id,e.name,e_conn.phno,e_conn.email
from employee e left join employee_connection e_conn
on e.id =e_conn.id;

--等价于 left outer join
select e.id,e.name,e_conn.phno,e_conn.email
from employee e left outer join  employee_connection e_conn
on e.id =e_conn.id;
```
#### right join
```sql
--3、right join
select e.id,e.name,e_conn.phno,e_conn.email
from employee e right join employee_connection e_conn
on e.id =e_conn.id;

--等价于 right outer join
select e.id,e.name,e_conn.phno,e_conn.email
from employee e right outer join employee_connection e_conn
on e.id =e_conn.id;
```
#### full outer join
```sql
--4、full outer join
select e.id,e.name,e_a.city,e_a.street
from employee e full outer join employee_address e_a
on e.id =e_a.id;
--等价于
select e.id,e.name,e_a.city,e_a.street
from employee e full  join employee_address e_a
on e.id =e_a.id;


select * from employee;
select * from employee_address;
```
#### left semi join
```sql
--5、left semi join
select *
from employee e left semi join employee_address e_addr
on e.id =e_addr.id;

--相当于 inner join,但是只返回左表全部数据， 只不过效率高一些
select e.*
from employee e inner join employee_address e_addr
on e.id =e_addr.id;
```
#### cross join
```sql
--6、cross join
--下列A、B、C 执行结果相同，但是效率不一样：
--A:
select a.*,b.* from employee a,employee_address b where a.id=b.id;
--B:
select * from employee a cross join employee_address b on a.id=b.id;
select * from employee a cross join employee_address b where a.id=b.id;
--C:
select * from employee a inner join employee_address b on a.id=b.id;

--一般不建议使用方法A和B，因为如果有WHERE子句的话，往往会先生成两个表行数乘积的行的数据表然后才根据WHERE条件从中选择。
--因此，如果两个需要求交集的表太大，将会非常非常慢，不建议使用。


explain  select a.*,b.* from employee a,employee_address b where a.id=b.id;
--B:
explain select * from employee a cross join employee_address b on a.id=b.id;
--C:
explain select * from employee a inner join employee_address b on a.id=b.id;

------------------------------
SELECT a.* FROM a JOIN b ON (a.id = b.id)
SELECT a.* FROM a JOIN b ON (a.id = b.id AND a.department = b.department)
SELECT a.* FROM a LEFT OUTER JOIN b ON (a.id <> b.id)


SELECT a.val, b.val, c.val FROM a JOIN b ON (a.key = b.key1) JOIN c ON (c.key = b.key2)


SELECT a.val, b.val, c.val FROM a JOIN b ON (a.key = b.key1) JOIN c ON (c.key = b.key1)
--由于联接中仅涉及b的key1列，因此被转换为1个MR作业来执行
SELECT a.val, b.val, c.val FROM a JOIN b ON (a.key = b.key1) JOIN c ON (c.key = b.key2)
--会转换为两个MR作业，因为在第一个连接条件中使用了b中的key1列，而在第二个连接条件中使用了b中的key2列。
-- 第一个map / reduce作业将a与b联接在一起，然后将结果与c联接到第二个map / reduce作业中。



SELECT a.val, b.val, c.val FROM a JOIN b ON (a.key = b.key1) JOIN c ON (c.key = b.key1)
--由于联接中仅涉及b的key1列，因此被转换为1个MR作业来执行，并且表a和b的键的特定值的值被缓冲在reducer的内存中。然后，对于从c中检索的每一行，将使用缓冲的行来计算联接。
SELECT a.val, b.val, c.val FROM a JOIN b ON (a.key = b.key1) JOIN c ON (c.key = b.key2)
--计算涉及两个MR作业。其中的第一个将a与b连接起来，并缓冲a的值，同时在reducer中流式传输b的值。
-- 在第二个MR作业中，将缓冲第一个连接的结果，同时将c的值通过reducer流式传输。


SELECT /*+ STREAMTABLE(a) */ a.val, b.val, c.val FROM a JOIN b ON (a.key = b.key1) JOIN c ON (c.key = b.key1)
--a,b,c三个表都在一个MR作业中联接，并且表b和c的键的特定值的值被缓冲在reducer的内存中。
-- 然后，对于从a中检索到的每一行，将使用缓冲的行来计算联接。如果省略STREAMTABLE提示，则Hive将流式传输最右边的表。

SELECT /*+ MAPJOIN(b) */ a.key, a.value FROM a JOIN b ON a.key = b.key
--不需要reducer。对于A的每个Mapper，B都会被完全读取。限制是不能执行FULL / RIGHT OUTER JOIN b。

```

## 参数配置与函数运算符

### 参数配置

```shell
#-------Batch Mode 批处理模式-----------
#-e
$HIVE_HOME/bin/hive -e 'show databases'

#-f
cd ~
#编辑一个sql文件 里面写上合法正确的sql语句
vim hive.sql
show databases;
#执行 从客户端所在机器的本地磁盘加载文件
$HIVE_HOME/bin/hive -f /root/hive.sql
#也可以从其他文件系统加载sql文件执行
$HIVE_HOME/bin/hive -f hdfs://<namenode>:<port>/hive-script.sql
$HIVE_HOME/bin/hive -f s3://mys3bucket/s3-script.sql

#-i 进入交互模式之前运行初始化脚本
$HIVE_HOME/bin/hive -i /home/my/hive-init.sql

#使用静默模式将数据从查询中转储到文件中
$HIVE_HOME/bin/hive -S -e 'select * from itheima.student' > a.txt

#----------启动服务-------------------
#--hiveconf
$HIVE_HOME/bin/hive --hiveconf hive.root.logger=DEBUG,console

#--service
$HIVE_HOME/bin/hive --service metastore
$HIVE_HOME/bin/hive --service hiveserver2


#---------交互式模式运行
/export/server/hive/bin/hive
hive> show databases;
OK
default
itcast
itheima
Time taken: 0.028 seconds, Fetched: 3 row(s)
hive> use itcast;
OK
Time taken: 0.027 seconds
hive> exit;


#启用hive动态分区，需要在hive会话中设置两个参数：
set hive.exec.dynamic.partition=true;
set hive.exec.dynamic.partition.mode=nonstrict;

```
### 运算符
```sql
--显示所有的函数和运算符
show functions;
--查看运算符或者函数的使用说明
describe function +;
--使用extended 可以查看更加详细的使用说明
describe function extended count;

--1、创建表dual
create table dual(id string);
--2、加载一个文件dual.txt到dual表中
--dual.txt只有一行内容：内容为一个空格
load data local inpath '/root/hivedata/dual.txt' into table dual;
--3、在select查询语句中使用dual表完成运算符、函数功能测试
select 1+1 from dual;

select 1+1;
```

#### 关系运算符
```sql
----------------Hive中关系运算符--------------------------
--is null空值判断
select 1 from dual where 'itcast' is null;

--is not null 非空值判断
select 1 from dual where 'itcast' is not null;

--like比较： _表示任意单个字符 %表示任意数量字符
--否定比较： NOT A like B
select 1 from dual where 'itcast' like 'it_';
select 1 from dual where 'itcast' like 'it%';
select 1 from dual where  'itcast' not like 'hadoo_';
select 1 from dual where  not 'itcast' like 'hadoo_';

--rlike：确定字符串是否匹配正则表达式，是REGEXP_LIKE()的同义词。
select 1 from dual where 'itcast' rlike '^i.*t$';
select 1 from dual where '123456' rlike '^\\d+$';  --判断是否全为数字
select 1 from dual where '123456aa' rlike '^\\d+$';

--regexp：功能与rlike相同 用于判断字符串是否匹配正则表达式
select 1 from dual where 'itcast' regexp '^i.*t$';
```

#### 算术运算符
```sql

-------------------Hive中算术运算符---------------------------------
--取整操作: div  给出将A除以B所得的整数部分。例如17 div 3得出5。
select 17 div 3;

--取余操作: %  也叫做取模mod  A除以B所得的余数部分
select 17 % 3;

--位与操作: &  A和B按位进行与操作的结果。 与表示两个都为1则结果为1
select 4 & 8 from dual;  --4转换二进制：0100 8转换二进制：1000
select 6 & 4 from dual;  --4转换二进制：0100 6转换二进制：0110

--位或操作: |  A和B按位进行或操作的结果  或表示有一个为1则结果为1
select 4 | 8 from dual;
select 6 | 4 from dual;

--位异或操作: ^ A和B按位进行异或操作的结果 异或表示两者的值不同,则结果为1
select 4 ^ 8 from dual;
select 6 ^ 4 from dual;
```
#### 逻辑运算符
```sql
--3、Hive逻辑运算符
--与操作: A AND B   如果A和B均为TRUE，则为TRUE，否则为FALSE。如果A或B为NULL，则为NULL。
select 1 from dual where 3>1 and 2>1;
--或操作: A OR B   如果A或B或两者均为TRUE，则为TRUE，否则为FALSE。
select 1 from dual where 3>1 or 2!=2;
--非操作: NOT A 、!A   如果A为FALSE，则为TRUE；如果A为NULL，则为NULL。否则为FALSE。
select 1 from dual where not 2>1;
select 1 from dual where !2=1;
--在:A IN (val1, val2, ...)  如果A等于任何值，则为TRUE。
select 1 from dual where 11  in(11,22,33);
--不在:A NOT IN (val1, val2, ...) 如果A不等于任何值，则为TRUE
select 1 from dual where 11 not in(22,33,44);
--逻辑是否存在: [NOT] EXISTS (subquery)
--将主查询的数据，放到子查询中做条件验证，根据验证结果（TRUE 或 FALSE）来决定主查询的数据结果是否得以保留。
select A.* from A
where exists (select B.id from B where A.id = B.id);
```
#### 其他运算符
```sql
--其他运算符
select 'it' || 'cast';
select concat()

select `array`(11,22,33)
from dual;
```
### 函数
#### 字符串函数
```sql
describe function extended get_json_object;

------------String Functions 字符串函数------------
select concat("angela","baby");
--带分隔符字符串连接函数：concat_ws(separator, [string | array(string)]+)
select concat_ws('.', 'www', array('itcast', 'cn'));
--字符串截取函数：substr(str, pos[, len]) 或者  substring(str, pos[, len])
select substr("angelababy",-2); --pos是从1开始的索引，如果为负数则倒着数
select substr("angelababy",2,2);
--正则表达式替换函数：regexp_replace(str, regexp, rep)
select regexp_replace('100-200', '(\\d+)', 'num');
--正则表达式解析函数：regexp_extract(str, regexp[, idx]) 提取正则匹配到的指定组内容
select regexp_extract('100-200', '(\\d+)-(\\d+)', 2);
--URL解析函数：parse_url 注意要想一次解析出多个 可以使用parse_url_tuple这个UDTF函数
select parse_url('http://www.itcast.cn/path/p1.php?query=1', 'HOST');
--分割字符串函数: split(str, regex)
select split('apache hive', '\\s+');
--json解析函数：get_json_object(json_txt, path)
--$表示json对象
select get_json_object('[{"website":"www.itcast.cn","name":"allenwoon"}, {"website":"cloud.itcast.com","name":"carbondata 中文文档"}]', '$.[1].website');



--字符串长度函数：length(str | binary)
select length("angelababy");
--字符串反转函数：reverse
select reverse("angelababy");
--字符串连接函数：concat(str1, str2, ... strN)
--字符串转大写函数：upper,ucase
select upper("angelababy");
select ucase("angelababy");
--字符串转小写函数：lower,lcase
select lower("ANGELABABY");
select lcase("ANGELABABY");
--去空格函数：trim 去除左右两边的空格
select trim(" angelababy ");
--左边去空格函数：ltrim
select ltrim(" angelababy ");
--右边去空格函数：rtrim
select rtrim(" angelababy ");
--空格字符串函数：space(n) 返回指定个数空格
select space(4);
--重复字符串函数：repeat(str, n) 重复str字符串n次
select repeat("angela",2);
--首字符ascii函数：ascii
select ascii("angela");  --a对应ASCII 97
--左补足函数：lpad
select lpad('hi', 5, '??');  --???hi
select lpad('hi', 1, '??');  --h
--右补足函数：rpad
select rpad('hi', 5, '??');
--集合查找函数: find_in_set(str,str_array)
select find_in_set('a','abc,b,ab,c,def');

```
#### 日期函数
```sql
----------- Date Functions 日期函数 -----------------
--获取当前日期: current_date
select current_date();
--获取当前时间戳: current_timestamp
--同一查询中对current_timestamp的所有调用均返回相同的值。
select current_timestamp();
--获取当前UNIX时间戳函数: unix_timestamp
select unix_timestamp();
--日期转UNIX时间戳函数: unix_timestamp
select unix_timestamp("2011-12-07 13:01:03");
--指定格式日期转UNIX时间戳函数: unix_timestamp
select unix_timestamp('20111207 13:01:03','yyyyMMdd HH:mm:ss');
--UNIX时间戳转日期函数: from_unixtime
select from_unixtime(1618238391);
select from_unixtime(0, 'yyyy-MM-dd HH:mm:ss');
--日期比较函数: datediff  日期格式要求'yyyy-MM-dd HH:mm:ss' or 'yyyy-MM-dd'
select datediff('2012-12-08','2012-05-09');
--日期增加函数: date_add
select date_add('2012-02-28',10);
--日期减少函数: date_sub
select date_sub('2012-01-1',10);


--抽取日期函数: to_date
select to_date('2009-07-30 04:17:52');
--日期转年函数: year
select year('2009-07-30 04:17:52');
--日期转月函数: month
select month('2009-07-30 04:17:52');
--日期转天函数: day
select day('2009-07-30 04:17:52');
--日期转小时函数: hour
select hour('2009-07-30 04:17:52');
--日期转分钟函数: minute
select minute('2009-07-30 04:17:52');
--日期转秒函数: second
select second('2009-07-30 04:17:52');
--日期转周函数: weekofyear 返回指定日期所示年份第几周
select weekofyear('2009-07-30 04:17:52');

```
#### 数学函数
```sql
----Mathematical Functions 数学函数-------------
--取整函数: round  返回double类型的整数值部分 （遵循四舍五入）
select round(3.1415926);
--指定精度取整函数: round(double a, int d) 返回指定精度d的double类型
select round(3.1415926,4);
--向下取整函数: floor
select floor(3.1415926);
select floor(-3.1415926);
--向上取整函数: ceil
select ceil(3.1415926);
select ceil(-3.1415926);
--取随机数函数: rand 每次执行都不一样 返回一个0到1范围内的随机数
select rand();
--指定种子取随机数函数: rand(int seed) 得到一个稳定的随机数序列
select rand(3);



--二进制函数:  bin(BIGINT a)
select bin(18);
--进制转换函数: conv(BIGINT num, int from_base, int to_base)
select conv(17,10,16);
--绝对值函数: abs
select abs(-3.9);
```
#### 集合函数
```sql
-------Collection Functions 集合函数--------------
--集合元素size函数: size(Map<K.V>) size(Array<T>)
select size(`array`(11,22,33));
select size(`map`("id",10086,"name","zhangsan","age",18));

--取map集合keys函数: map_keys(Map<K.V>)
select map_keys(`map`("id",10086,"name","zhangsan","age",18));

--取map集合values函数: map_values(Map<K.V>)
select map_values(`map`("id",10086,"name","zhangsan","age",18));

--判断数组是否包含指定元素: array_contains(Array<T>, value)
select array_contains(`array`(11,22,33),11);
select array_contains(`array`(11,22,33),66);

--数组排序函数:sort_array(Array<T>)
select sort_array(`array`(12,2,32));

```
#### 条件函数
```sql
-----Conditional Functions 条件函数------------------
--使用之前课程创建好的student表数据
select * from student limit 3;

describe function extended isnull;

--if条件判断: if(boolean testCondition, T valueTrue, T valueFalseOrNull)
select if(1=2,100,200);
select if(sex ='男','M','W') from student limit 3;

--空判断函数: isnull( a )
select isnull("allen");
select isnull(null);

--非空判断函数: isnotnull ( a )
select isnotnull("allen");
select isnotnull(null);

--空值转换函数: nvl(T value, T default_value)
select nvl("allen","itcast");
select nvl(null,"itcast");

--非空查找函数: COALESCE(T v1, T v2, ...)
--返回参数中的第一个非空值；如果所有值都为NULL，那么返回NULL
select COALESCE(null,11,22,33);
select COALESCE(null,null,null,33);
select COALESCE(null,null,null);

--条件转换函数: CASE a WHEN b THEN c [WHEN d THEN e]* [ELSE f] END
select case 100 when 50 then 'tom' when 100 then 'mary' else 'tim' end;
select case sex when '男' then 'male' else 'female' end from student limit 3;

--nullif( a, b ):
-- 如果a = b，则返回NULL，否则返回一个
select nullif(11,11);
select nullif(11,12);

--assert_true(condition)
--如果'condition'不为真，则引发异常，否则返回null
SELECT assert_true(11 >= 0);
SELECT assert_true(-1 >= 0);

```
#### 类型转换函数
```sql
----Type Conversion Functions 类型转换函数-----------------
--任意数据类型之间转换:cast
select cast(12.14 as bigint);
select cast(12.14 as string);
select cast("hello" as int);

```
#### 数据脱敏函数
```sql

----Data Masking Functions 数据脱敏函数------------
--mask
--将查询回的数据，大写字母转换为X，小写字母转换为x，数字转换为n。
select mask("abc123DEF");
select mask("abc123DEF",'-','.','^'); --自定义替换的字母

--mask_first_n(string str[, int n]
--对前n个进行脱敏替换
select mask_first_n("abc123DEF",4);

--mask_last_n(string str[, int n])
select mask_last_n("abc123DEF",4);

--mask_show_first_n(string str[, int n])
--除了前n个字符，其余进行掩码处理
select mask_show_first_n("abc123DEF",4);

--mask_show_last_n(string str[, int n])
select mask_show_last_n("abc123DEF",4);

--mask_hash(string|char|varchar str)
--返回字符串的hash编码。
select mask_hash("abc123DEF");
```
#### 其他杂项函数
```sql
----- Misc. Functions 其他杂项函数---------------

--如果你要调用的java方法所在的jar包不是hive自带的 可以使用add jar添加进来
--hive调用java方法: java_method(class, method[, arg1[, arg2..]])
select java_method("java.lang.Math","max",11,22);

--反射函数: reflect(class, method[, arg1[, arg2..]])
select reflect("java.lang.Math","max",11,22);

--取哈希值函数:hash
select hash("allen");

--current_user()、logged_in_user()、current_database()、version()

--SHA-1加密: sha1(string/binary)
select sha1("allen");

--SHA-2家族算法加密：sha2(string/binary, int)  (SHA-224, SHA-256, SHA-384, SHA-512)
select sha2("allen",224);
select sha2("allen",512);

--crc32加密:
select crc32("allen");

--MD5加密: md5(string/binary)
select md5("allen");

```
### 横向视图

```sql
--step1:建表
create table the_nba_championship(
           team_name string,
           champion_year array<string>
) row format delimited
fields terminated by ','
collection items terminated by '|';

--step2:加载数据文件到表中
load data local inpath '/root/hivedata/The_NBA_Championship.txt' into table the_nba_championship;

--step3:验证
select *
from the_nba_championship;

--step4:使用explode函数对champion_year进行拆分 俗称炸开
select explode(champion_year) from the_nba_championship;

--想法是正确的 sql执行确实错误的
select team_name,explode(champion_year) from the_nba_championship;


--step5: lateral view +explode
select a.team_name,b.year
from the_nba_championship a lateral view explode(champion_year) b as year
order by b.year desc;
```
#### view侧视图
```sql
--lateral view侧视图基本语法如下
select …… from tabelA lateral view UDTF(xxx) 别名 as col1,col2,col3……;


select a.team_name ,b.year
from the_nba_championship a lateral view explode(champion_year) b as year;

--根据年份倒序排序
select a.team_name ,b.year
from the_nba_championship a lateral view explode(champion_year) b as year
order by b.year desc;

--统计每个球队获取总冠军的次数 并且根据倒序排序
select a.team_name ,count(*) as nums
from the_nba_championship a lateral view explode(champion_year) b as year
group by a.team_name
order by nums desc;




describe function extended explode;

select explode(`array`(11,22,33,44,55));

select explode(`map`("id",10086,"name","allen","age",18));
```


#### 基础聚合函数
```sql
--------------基础聚合函数-------------------
--1、测试数据准备
drop table if exists student;
create table student(
    num int,
    name string,
    sex string,
    age int,
    dept string)
row format delimited
fields terminated by ',';
--加载数据
load data local inpath '/root/hivedata/students.txt' into table student;
--验证
select * from student;


--场景1：没有group by子句的聚合操作
    --count(*)：所有行进行统计，包括NULL行
    --count(1)：所有行进行统计，包括NULL行
    --count(column)：对column中非Null进行统计
select count(*) as cnt1,count(1) as cnt2 from student;
select count(sex) as cnt3 from student;

--场景2：带有group by子句的聚合操作 注意group by语法限制
select sex,count(*) as cnt from student group by sex;

--场景3：select时多个聚合函数一起使用
select count(*) as cnt1,avg(age) as cnt2 from student;

--场景4：聚合函数和case when条件转换函数、coalesce函数、if函数使用
select
    sum(CASE WHEN sex = '男'THEN 1 ELSE 0 END)
from student;

select
    sum(if(sex = '男',1,0))
from student;

--场景5：聚合参数不支持嵌套聚合函数
select avg(count(*))  from student;

--场景6：聚合操作时针对null的处理
CREATE TABLE tmp_1 (val1 int, val2 int);
INSERT INTO TABLE tmp_1 VALUES (1, 2),(null,2),(2,3);
select * from tmp_1;
--第二行数据(NULL, 2) 在进行sum(val1 + val2)的时候会被忽略
select sum(val1), sum(val1 + val2) from tmp_1;
--可以使用coalesce函数解决
select
    sum(coalesce(val1,0)),
    sum(coalesce(val1,0) + val2)
from tmp_1;

--场景7：配合distinct关键字去重聚合
--此场景下，会编译期间会自动设置只启动一个reduce task处理数据  可能造成数据拥堵
select count(distinct sex) as cnt1 from student;
--可以先去重 在聚合 通过子查询完成
--因为先执行distinct的时候 可以使用多个reducetask来跑数据
select count(*) as gender_uni_cnt
from (select distinct sex from student) a;

--案例需求：找出student中男女学生年龄最大的及其名字
--这里使用了struct来构造数据 然后针对struct应用max找出最大元素 然后取值
select sex,
max(struct(age, name)).col1 as age,
max(struct(age, name)).col2 as name
from student
group by sex;

select struct(age, name) from student;
select struct(age, name).col1 from student;
select max(struct(age, name)) from student;
```
#### 增强聚合
```sql

-------------------------增强聚合--------------
--表创建并且加载数据
CREATE TABLE cookie_info(
   month STRING,
   day STRING,
   cookieid STRING
) ROW FORMAT DELIMITED
FIELDS TERMINATED BY ',';

load data local inpath '/root/hivedata/cookie_info.txt' into table cookie_info;

select * from cookie_info;

---group sets---------
SELECT
    month,
    day,
    COUNT(DISTINCT cookieid) AS nums,
    GROUPING__ID
FROM cookie_info
GROUP BY month,day
GROUPING SETS (month,day) --这里是关键
ORDER BY GROUPING__ID;

--grouping_id表示这一组结果属于哪个分组集合，
--根据grouping sets中的分组条件month，day，1是代表month，2是代表day

--等价于
SELECT month,NULL,COUNT(DISTINCT cookieid) AS nums,1 AS GROUPING__ID FROM cookie_info GROUP BY month
UNION ALL
SELECT NULL as month,day,COUNT(DISTINCT cookieid) AS nums,2 AS GROUPING__ID FROM cookie_info GROUP BY day;

--再比如
SELECT
    month,
    day,
    COUNT(DISTINCT cookieid) AS nums,
    GROUPING__ID
FROM cookie_info
GROUP BY month,day
GROUPING SETS (month,day,(month,day))   --1 month   2 day    3 (month,day)
ORDER BY GROUPING__ID;

--等价于
SELECT month,NULL,COUNT(DISTINCT cookieid) AS nums,1 AS GROUPING__ID FROM cookie_info GROUP BY month
UNION ALL
SELECT NULL,day,COUNT(DISTINCT cookieid) AS nums,2 AS GROUPING__ID FROM cookie_info GROUP BY day
UNION ALL
SELECT month,day,COUNT(DISTINCT cookieid) AS nums,3 AS GROUPING__ID FROM cookie_info GROUP BY month,day;

------cube---------------
SELECT
    month,
    day,
    COUNT(DISTINCT cookieid) AS nums,
    GROUPING__ID
FROM cookie_info
GROUP BY month,day
WITH CUBE
ORDER BY GROUPING__ID;

--等价于
SELECT NULL,NULL,COUNT(DISTINCT cookieid) AS nums,0 AS GROUPING__ID FROM cookie_info
UNION ALL
SELECT month,NULL,COUNT(DISTINCT cookieid) AS nums,1 AS GROUPING__ID FROM cookie_info GROUP BY month
UNION ALL
SELECT NULL,day,COUNT(DISTINCT cookieid) AS nums,2 AS GROUPING__ID FROM cookie_info GROUP BY day
UNION ALL
SELECT month,day,COUNT(DISTINCT cookieid) AS nums,3 AS GROUPING__ID FROM cookie_info GROUP BY month,day;

--rollup-------------
--比如，以month维度进行层级聚合：
SELECT
    month,
    day,
    COUNT(DISTINCT cookieid) AS nums,
    GROUPING__ID
FROM cookie_info
GROUP BY month,day
WITH ROLLUP
ORDER BY GROUPING__ID;

--把month和day调换顺序，则以day维度进行层级聚合：
SELECT
    day,
    month,
    COUNT(DISTINCT cookieid) AS uv,
    GROUPING__ID
FROM cookie_info
GROUP BY day,month
WITH ROLLUP
ORDER BY GROUPING__ID;





-------------------------------------------------------------------
--验证测试count(*),count(1),count(字段)
select * from t_all_hero_part_dynamic where role ="archer";
select count(*),count(1),count(role_assist) from t_all_hero_part_dynamic where role ="archer";


```
### 窗口函数
```sql
--建表加载数据
CREATE TABLE employee(
       id int,
       name string,
       deg string,
       salary int,
       dept string
) row format delimited
    fields terminated by ',';

load data local inpath '/root/hivedata/employee.txt' into table employee;

select * from employee;

----sum+group by普通常规聚合操作------------
select id,dept,sum(salary) as total from employee group by dept;

----sum+窗口函数聚合操作------------
select id,name,deg,salary,dept,sum(salary) over(partition by dept) as total from employee;
```
#### 窗口函数语法树
```sql
-------窗口函数语法树
Function(arg1,..., argn) OVER ([PARTITION BY <...>] [ORDER BY <....>] [<window_expression>])

--其中Function(arg1,..., argn) 可以是下面分类中的任意一个
    --聚合函数：比如sum max avg等
    --排序函数：比如rank row_number等
    --分析函数：比如lead lag first_value等

--OVER [PARTITION BY <...>] 类似于group by 用于指定分组  每个分组你可以把它叫做窗口
--如果没有PARTITION BY 那么整张表的所有行就是一组

--[ORDER BY <....>]  用于指定每个分组内的数据排序规则 支持ASC、DESC

--[<window_expression>] 用于指定每个窗口中 操作的数据范围 默认是窗口中所有行



-------------------
---建表并且加载数据
create table website_pv_info(
   cookieid string,
   createtime string,   --day
   pv int
) row format delimited
fields terminated by ',';

create table website_url_info (
    cookieid string,
    createtime string,  --访问时间
    url string       --访问页面
) row format delimited
fields terminated by ',';


load data local inpath '/root/hivedata/website_pv_info.txt' into table website_pv_info;
load data local inpath '/root/hivedata/website_url_info.txt' into table website_url_info;

select * from website_pv_info;
select * from website_url_info;
```
#### 窗口函数的使用
```sql

-----窗口聚合函数的使用-----------
--1、求出每个用户总pv数  sum+group by普通常规聚合操作
select cookieid,sum(pv) as total_pv from website_pv_info group by cookieid;

--2、sum+窗口函数 总共有四种用法 注意是整体聚合 还是累积聚合
--sum(...) over( )对表所有行求和
--sum(...) over( order by ... ) 连续累积求和
--sum(...) over( partition by... ) 同组内所行求和
--sum(...) over( partition by... order by ... ) 在每个分组内，连续累积求和

--需求：求出网站总的pv数 所有用户所有访问加起来
--sum(...) over( )对表所有行求和
select cookieid,createtime,pv,
       sum(pv) over() as total_pv  --注意这里窗口函数是没有partition by 也就是没有分组  全表所有行
from website_pv_info;

--需求：求出每个用户总pv数
--sum(...) over( partition by... )，同组内所行求和
select cookieid,createtime,pv,
       sum(pv) over(partition by cookieid) as total_pv
from website_pv_info;

--需求：求出每个用户截止到当天，累积的总pv数
--sum(...) over( partition by... order by ... )，在每个分组内，连续累积求和
select cookieid,createtime,pv,
       sum(pv) over(partition by cookieid order by createtime) as current_total_pv
from website_pv_info;

```



#### 窗口表达式
```sql

---窗口表达式
select cookieid,createtime,pv,
       sum(pv) over(partition by cookieid order by createtime) as pv1  --默认从第一行到当前行
from website_pv_info;
--第一行到当前行
select cookieid,createtime,pv,
       sum(pv) over(partition by cookieid order by createtime rows between unbounded preceding and current row) as pv2
from website_pv_info;

--向前3行至当前行
select cookieid,createtime,pv,
       sum(pv) over(partition by cookieid order by createtime rows between 3 preceding and current row) as pv4
from website_pv_info;

--向前3行 向后1行
select cookieid,createtime,pv,
       sum(pv) over(partition by cookieid order by createtime rows between 3 preceding and 1 following) as pv5
from website_pv_info;

--当前行至最后一行
select cookieid,createtime,pv,
       sum(pv) over(partition by cookieid order by createtime rows between current row and unbounded following) as pv6
from website_pv_info;

--第一行到最后一行 也就是分组内的所有行
select cookieid,createtime,pv,
       sum(pv) over(partition by cookieid order by createtime rows between unbounded preceding  and unbounded following) as pv6
from website_pv_info;
```
#### 窗口排序函数
```sql
-----窗口排序函数
SELECT
    cookieid,
    createtime,
    pv,
    RANK() OVER(PARTITION BY cookieid ORDER BY pv desc) AS rn1,
    DENSE_RANK() OVER(PARTITION BY cookieid ORDER BY pv desc) AS rn2,
    ROW_NUMBER() OVER(PARTITION BY cookieid ORDER BY pv DESC) AS rn3
FROM website_pv_info
WHERE cookieid = 'cookie1';

--需求：找出每个用户访问pv最多的Top3 重复并列的不考虑
SELECT * from
(SELECT
    cookieid,
    createtime,
    pv,
    ROW_NUMBER() OVER(PARTITION BY cookieid ORDER BY pv DESC) AS seq
FROM website_pv_info) tmp where tmp.seq <4;

--把每个分组内的数据分为3桶
SELECT
    cookieid,
    createtime,
    pv,
    NTILE(3) OVER(PARTITION BY cookieid ORDER BY createtime) AS rn2
FROM website_pv_info
ORDER BY cookieid,createtime;

--需求：统计每个用户pv数最多的前3分之1天。
--理解：将数据根据cookieid分 根据pv倒序排序 排序之后分为3个部分 取第一部分
SELECT * from
(SELECT
     cookieid,
     createtime,
     pv,
     NTILE(3) OVER(PARTITION BY cookieid ORDER BY pv DESC) AS rn
 FROM website_pv_info) tmp where rn =1;
```

#### 窗口分析函数
```sql
select * from website_url_info;

-----------窗口分析函数----------
--LAG
SELECT cookieid,
       createtime,
       url,
       ROW_NUMBER() OVER(PARTITION BY cookieid ORDER BY createtime) AS rn,
       LAG(createtime,1,'1970-01-01 00:00:00') OVER(PARTITION BY cookieid ORDER BY createtime) AS last_1_time,
       LAG(createtime,2) OVER(PARTITION BY cookieid ORDER BY createtime) AS last_2_time
FROM website_url_info;


--LEAD
SELECT cookieid,
       createtime,
       url,
       ROW_NUMBER() OVER(PARTITION BY cookieid ORDER BY createtime) AS rn,
       LEAD(createtime,1,'1970-01-01 00:00:00') OVER(PARTITION BY cookieid ORDER BY createtime) AS next_1_time,
       LEAD(createtime,2) OVER(PARTITION BY cookieid ORDER BY createtime) AS next_2_time
FROM website_url_info;

--FIRST_VALUE
SELECT cookieid,
       createtime,
       url,
       ROW_NUMBER() OVER(PARTITION BY cookieid ORDER BY createtime) AS rn,
       FIRST_VALUE(url) OVER(PARTITION BY cookieid ORDER BY createtime) AS first1
FROM website_url_info;

--LAST_VALUE
SELECT cookieid,
       createtime,
       url,
       ROW_NUMBER() OVER(PARTITION BY cookieid ORDER BY createtime) AS rn,
       LAST_VALUE(url) OVER(PARTITION BY cookieid ORDER BY createtime) AS last1
FROM website_url_info;

```
### 抽样函数
```sql
--数据表
select * from student;

--需求：随机抽取2个学生的情况进行查看
SELECT * FROM student
DISTRIBUTE BY rand() SORT BY rand() LIMIT 2;

--使用order by+rand也可以实现同样的效果 但是效率不高
SELECT * FROM student
    ORDER BY rand() LIMIT 2;


---block抽样
--根据行数抽样
SELECT * FROM student TABLESAMPLE(1 ROWS);

--根据数据大小百分比抽样
SELECT * FROM student TABLESAMPLE(50 PERCENT);

--根据数据大小抽样
--支持数据单位 b/B, k/K, m/M, g/G
SELECT * FROM student TABLESAMPLE(1k);


---bucket table抽样
--根据整行数据进行抽样
SELECT * FROM t_usa_covid19_bucket TABLESAMPLE(BUCKET 1 OUT OF 5 ON rand());

--根据分桶字段进行抽样 效率更高
describe formatted t_usa_covid19_bucket;
SELECT * FROM t_usa_covid19_bucket TABLESAMPLE(BUCKET 1 OUT OF 5 ON state);


--TABLESAMPLE (BUCKET x OUT OF y [ON colname])

--1、y必须是table总bucket数的倍数或者因子。hive根据y的大小，决定抽样的比例。
    --例如，table总共分了4份（4个bucket），当y=2时，抽取(4/2=)2个bucket的数据，当y=8时，抽取(4/8=)1/2个bucket的数据。
--2、x表示从哪个bucket开始抽取。
    --例如，table总bucket数为4，tablesample(bucket 4 out of 4)，表示总共抽取（4/4=）1个bucket的数据，抽取第4个bucket的数据。
    --注意：x的值必须小于等于y的值，否则FAILED:Numerator should not be bigger than denominator in sample clause for table stu_buck
--3、ON colname表示基于什么抽
    --ON rand()表示随机抽
    --ON 分桶字段 表示基于分桶字段抽样 效率更高 推荐
```
