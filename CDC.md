#### zookeeper
```shell
wget https://archive.apache.org/dist/zookeeper/zookeeper-3.4.11/zookeeper-3.4.11.tar.gz 
tar -zxvf zookeeper-3.4.11.tar.gz		//解压
mv zookeeper-3.4.11 /usr/local/zookeeper   //移动到指定位置并改名为zookeeper
cd /usr/local/zookeeper/conf 		//切换到该目录
cp zoo_sample.cfg zoo.cfg			//复制一份取名为zoo.cfg
vi /etc/init.d/zookeeper
```
输入以下内容
```shell
#!/bin/bash
#chkconfig: 2345 80 90
#description:auto_run
ZK_PATH=/usr/local/zookeeper
export JAVA_HOME=/usr/local/java/jdk1.8.0_171
case $1 in
         start) sh  $ZK_PATH/bin/zkServer.sh start;;
         stop)  sh  $ZK_PATH/bin/zkServer.sh stop;;
         status) sh  $ZK_PATH/bin/zkServer.sh status;;
         restart) sh $ZK_PATH/bin/zkServer.sh restart;;
         *)  echo "require start|stop|status|restart"  ;;
esac
```
```shell
chmod +x /etc/init.d/zookeeper
```
启动zookeeper
```shell
service zookeeper start （开启）
service zookeeper stop （关闭）
service zookeeper status （查看状态）
service zookeeper restart （重启）
```

#### kafka

下载kafka、解压kafka、目录在/opt/kafka

在config目录下输入命令：vi server.properties
```properties
server.properties修改 内容如下：可直接复制粘贴。
broker.id=0 默认
port=9092 #端口号 
host.name=localhost #单机可直接用localhost
log.dirs=/data/kafka/kafka-log #日志存放路径可修改可不修改
zookeeper.connect=localhost:2181 #zookeeper地址和端口，单机配置部署，localhost:2181 默认
```
config目录下输入命令：vi  zookeeper.properties
```properties
修改内容为：
dataDir=/data/kafka/zookeeper/data/dataDir  #zookeeper数据目录  (可以修改可以不修改)
dataLogDir=/data/kafka/zookeeper/data/dataLogDir #zookeeper日志目录 （可以修改可以不修改）
clientPort=2181 
maxClientCnxns=100 
tickTime=2000 
initLimit=10
```
创建路径
```shell
mkdir -p /data/kafka/kafka-log
mkdir -p /data/kafka/zookeeper/data/dataLogDir
mkdir -p /data/kafka/zookeeper/data/dataDir 
```

Kafka启动脚本 vi /opt/kafka/kafkaStart.sh 

```shell
#!/bin/bash
#启动zookeeper
/opt/kafka/bin/zookeeper-server-start.sh /opt/kafka/config/zookeeper.properties &
sleep 3 
#默默等3秒后执行 
#启动kafka
/opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties &
```
Kafka关闭脚本 vi /opt/kafka/kafkaStop.sh 
```shell
#!/bin/bash
#关闭zookeeper
/opt/kafka/bin/zookeeper-server-stop.sh  /opt/kafka/config/zookeeper.properties &
sleep 3 
#默默等3秒后执行 
#关闭kafka
/opt/kafka/bin/kafka-server-stop.sh /opt/kafka/config/server.properties &
 
```
```shell
chmod +x /opt/kafka/kafkaStart.sh
chmod +x /opt/kafka/kafkaStop.sh
```

启动kafka

```shell
sh /opt/kafka/kafkaStart.sh
```
关闭kafka
```shell
sh /opt/kafka/kafkaSop.sh
```
```shell
创建topic
kafka-topics.sh --create --topic test130 --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1

向topic插入数据
kafka-console-producer.sh --broker-list localhost:9092 --topic test130

消费topic
kafka-console-consumer.sh --bootstrap-server 192.168.10.130:9092 --topic test130 --from-beginning

查看topic列表
kafka-topics.sh --bootstrap-server 192.168.10.130:9092 --list
```

#### oracle

```shell
检查依赖

rpm --query --queryformat "%{NAME}-%{VERSION}.%{RELEASE} (%{ARCH})\n" bc binutils compat-libcap1 compat-libstdc++-33 gcc gcc-c++ glibc glibc-devel ksh libaio libaio-devel libgcc libstdc++ libstdc++-devel make sysstat elfutils-libelf elfutils-libelf-devel fontconfig-devel libxcb smartmontools libX11 libXau libXtst libXrender libXrender-devel

安装依赖
yum install -y compat-libcap1 compat-libstdc++-33 gcc-c++ ksh libaio-devel libstdc++-devel elfutils-libelf-devel fontconfig-devel libXrender-devel

创建用户和组
groupadd oinstall
groupadd dba
groupadd asmdba
groupadd backupdba
groupadd dgdba
groupadd kmdba
groupadd racdba
groupadd oper
useradd -g oinstall -G dba,asmdba,backupdba,dgdba,kmdba,racdba,oper -m oracle

mkdir -p /oracle/product/19.0.0/dbhome_1 /oradata /arch
chown -Rf oracle:oinstall /oracle 
chown -R oracle:oinstall /oradata /arch

修改Linux内核
vi /etc/sysctl.conf

fs.aio-max-nr = 1048576
fs.file-max = 6815744
kernel.shmall = 16451328
kernel.shmmax = 33692319744
kernel.shmmni = 4096
kernel.sem = 250 32000 100 128
net.ipv4.ip_local_port_range = 9000 65500
net.core.rmem_default = 262144
net.core.rmem_max = 4194304
net.core.wmem_default = 262144
net.core.wmem_max = 1048576

加载生效
/sbin/sysctl -p


配置环境变量
vi /home/oracle/.bash_profile

export ORACLE_SID=neptune
export ORACLE_BASH=/oracle
export ORACLE_HOME=/oracle/product/19.0.0/dbhome_1
export LD_LIBRARY_PATH=/oracle/product/19.0.0/dbhome_1/lib
export NLS_DATE_FORMAT="YYYY-MM-DD HH24:MI:SS"
export TMP=/tmp
export TMPDIR=$TMP
export PATH=/oracle/product/19.0.0/dbhome_1/bin:/oracle/product/19.0.0/dbhome_1/OPatch:$PATH
export EDITOR=vi
export TNS_ADMIN=/oracle/product/19.0.0/dbhome_1/network/admin
export ORACLE_PATH=.:/oracle/dba_scripts/sql:/oracle/product/19.0.0/dbhome_1/rdbms/admin
export SQLPATH=/oracle/product/19.0.0/dbhome_1/sqlplus/admin
export NLS_LANG="AMERICAN_CHINA.ZHS16GBK"
export sqlplus="/oracle/product/19.0.0/dbhome_1/bin/sqlplus"
alias s='sqlplus / as sysdba'
alias r='rman target /'
#alias sqlplus='rlwrap sqlplus'
#alias rman='rlwrap rman'
#alias asmcmd='rlwrap asmcmd'


rm -rf / oracle/ oralnventory
chown oracle:oinstall LINUX.X64_193000_db_home.zip
su - oracle
soucre .bash_profile
unzip LINUX.X64_193000_db_home.zip -d /oracle/product/19.0.0/dbhome_1

安装Oracle
/oracle/product/19.0.0/dbhome_1/runInstaller -silent -force -noconfig -ignorePrereq \
oracle.install.responseFileVersion=/oracle/install/rspfmt_dbinstall_response_schema_v19.0.0 \
oracle.install.option=INSTALL_DB_SWONLY \
UNTX_GROUP_NAME=oinstall \
INVENTORY_LOCATION=/oracle/oraInventory \
ORACLE_BASE=/oracle \
ORACLE_HOME=/oracle/product/19.0.0/dbhome_1 \
oracle.install.db.InstallEdition=EE \
oracle.install.db.OSDBA_GROUP=oinstall \
oracle.install.db.OSOPER_GROUP=oinstall \
oracle.install.db.OSBACKUPDBA_GROUP=oinstall \
oracle.install.db.OSDGDBA_GROUP=oinstall \
oracle.install.db.OSKMDBA_GROUP=oinstall \
oracle.install.db.OSRACDBA_GROUP=oinstall \
oracle.install.db.rootconfig.executeRootscript=true \
oracle.install.db.rootconfig.configMethod=ROOT \

$ORACLE_HOME/bin/netca /silent /responsefile $ORACLE_HOME/assistants/netca/netca.rsp

vi /oracle/product/19.0.0/dbhome_1/assistants/dbca/dbca.rsp

gdbName=nep
sid=nep
databaseconfigType=sI
templateName=General_Purpose.dbc
sysPassword=123456
systemPassword=123456
datafileDestination=/oradata
修改完成后执行以下命令安装数据库实例

$ORACLE_HOME/bin/dbca -J-Doracle.assistants.dbca.validate.configurationParams=false -silent -createDatabase -responseFile $ORACLE_HOME/assistants/dbca/dbca.rsp

systemctl stop firewalld.service#停止firewall
systemctl disable firewalld.service#禁止firewall开机启动

每次重启服务器需要重新打开监听和实例
mount -o size=64G -o nr_inodes=1000000 -o noatime,nodiratime -o remount /dev/shm 同时修改/etc/fstab以保证下次重启主机后，不会出现相同的问题
lsnrctl start
sql> startup
```
#### oracle卸载实例

```perl
sqlplus / as sysdba
shutdown abort;
exit;
lsnrctl stop
find $ORACLE_BASH/ -name $ORACLE_SID|xargs -t -i rm -rf {}
find $ORACLE_BASH/ -name *$ORACLE_SID*|grep -v admin|grep -v oradata|xargs -t -i rm -rf {}
vim /etc/oratab --删除实例相关行
```

#### oracle卸载

```perl
lsnrctl stop
rm -f /etc/oratab
rm -f /etc/oraInst.loc

ls -l /usr/local/bin
rm -rf /usr/local/bin/coraenv
rm -rf /usr/local/bin/dbhome
rm -rf /usr/local/bin/oraenv

rm -rf /home/oracle

rm -rf /var/tmp/.oracle
rm -rf /tmp/.oracle
rm -rf /oracle/product
rm -rf /opt/ORCLfmap

userdel oracle
#ps -ef|grep oracle|grep -v grep|awk '{print $2}'|xargs -t -i kill -9 {}
groupdel oinstall
```

#### local debezium

```shell
vi $KAFKA_HOME/config/connect-standalone.properties

bootstrap.servers=localhost:9092
key.converter=org.apache.kafka.connect.json.JsonConverter
value.converter=org.apache.kafka.connect.json.JsonConverter
key.converter.schemas.enable=false
value.converter.schemas.enable=false
internal.key.converter=org.apache.kafka.connect.json.JsonConverter
internal.value.converter=org.apache.kafka.connect.json.JsonConverter
internal.key.converter.schemas.enable=false
internal.value.converter.schemas.enable=false
offset.storage.file.filename=/tmp/connect.offsets
config.storage.topic=amir-connect-configs
offset.storage.topic=amir-connect-offsets
status.storage.topic=amir-connect-statuses
config.storage.replication.factor=1
offset.storage.replication.factor=1
status.storage.replication.factor=1
offset.flush.interval.ms=10000
rest.advertised.host.name=localhost
cleanup.policy=compact
rest.host.name=localhost
#dbz端口
rest.port=8083
#连接器目录
plugin.path=/opt/kafka/connectors


mkdir /opt/kafka/connectors
将插件debezium-connector-postgres-1.6.0.Final-plugin.tar.gz存放到此目录

执行
export KAFKA_LOG4J_OPTS=-Dlog4j.configuration=file:/opt/kafka/config/connect-log4j.properties

connect-standalone.sh /opt/kafka/config/connect-standalone.properties &
connect-distributed.sh /opt/kafka/config/connect-distributed.properties &

oracle

wget https://download.oracle.com/otn_software/linux/instantclient/211000/instantclient-basic-linux.x64-21.1.0.0.0.zip

unzip instantclient-basic-linux.x64-21.1.0.0.0.zip
mv instantclient_21_1 /opt/instant_client

# 复制ojdbc.jar和xstreams.jar到Kafka的libs下
cp instant_client/ojdbc8.jar /opt/kafka/libs/
cp instant_client/xstreams.jar /opt/kafka/libs/

# 创建环境变量指向客户端目录
LD_LIBRARY_PATH=/opt/instant_client/
```

开启采集任务
```json
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" http://192.168.10.130:8083/connectors/ -d '
{
"name": "debezium-oracle",
"config": {
"connector.class" : "io.debezium.connector.oracle.OracleConnector",
"tasks.max" : "1",
"database.server.name" : "oracle130_test0628",
"database.hostname" : "192.168.10.130",
"database.port" : "1521",
"database.user" : "cdcuser",
"database.password" : "123456",
"database.dbname" : "nep",
"database.schema" : "neptune",
"database.connection.adapter": "logminer", 
"database.tablename.case.insensitive": "true",
"table.include.list" : "neptune.*", 
"snapshot.mode" : "initial",
"schema.include.list" : "neptune",
"database.history.kafka.bootstrap.servers" : "192.168.10.130:9092",
"database.history.kafka.topic": "schema-changes.inventory"
}
}'
```

#### logminer
```sql
oracle

开启归档日志
alter database add supplemental log data (all) columns;


创建采集用户
CREATE TABLESPACE CDCUSER_TBS DATAFILE '/oradata/NEP/cdcuser_tbs.dbf' SIZE 500M;
CREATE USER cdcuser IDENTIFIED BY 123456 DEFAULT TABLESPACE CDCUSER_TBS QUOTA UNLIMITED ON CDCUSER_TBS;

CREATE TABLESPACE neptune_TBS DATAFILE '/oradata/NEP/neptune_tbs.dbf' SIZE 25M REUSE AUTOEXTEND ON MAXSIZE UNLIMITED;
CREATE USER neptune IDENTIFIED BY 123456 DEFAULT TABLESPACE neptune_TBS QUOTA UNLIMITED ON neptune_TBS;

GRANT CREATE SESSION TO cdcuser;
GRANT SELECT ON V_$DATABASE TO cdcuser;
GRANT FLASHBACK ANY TABLE TO cdcuser;
GRANT SELECT ANY TABLE TO cdcuser;
GRANT SELECT_CATALOG_ROLE TO cdcuser;
GRANT EXECUTE_CATALOG_ROLE TO cdcuser;
GRANT SELECT ANY TRANSACTION TO cdcuser;
GRANT SELECT ANY DICTIONARY TO cdcuser;

GRANT CREATE TABLE TO cdcuser;
GRANT ALTER ANY TABLE TO cdcuser;
GRANT LOCK ANY TABLE TO cdcuser;
GRANT CREATE SEQUENCE TO cdcuser;

GRANT EXECUTE ON DBMS_LOGMNR TO cdcuser;
GRANT EXECUTE ON DBMS_LOGMNR_D TO cdcuser;
GRANT SELECT ON V_$LOGMNR_LOGS to cdcuser;
GRANT SELECT ON V_$LOGMNR_CONTENTS TO cdcuser;
GRANT SELECT ON V_$LOGFILE TO cdcuser;
GRANT SELECT ON V_$ARCHIVED_LOG TO cdcuser;
GRANT SELECT ON V_$ARCHIVE_DEST_STATUS TO cdcuser;
```

#### docker debezium

```shell
docker run -it --rm --name zookeeper -p 2181:2181 -p 2888:2888 -p 3888:3888 quay.io/debezium/zookeeper:1.9
 
service zookeeper start

sh /opt/kafka/kafkaStart.sh

需要将hostname 192.168.10.130添加进/etc/hosts文件里

docker run -d --name dbz_130_kisa -p 8083:8083 --add-host Neptune:192.168.10.130 -e BOOTSTRAP_SERVERS=192.168.10.130:9092 -e GROUP_ID=5 -e CONFIG_STORAGE_TOPIC=dbz_kisa_configs -e OFFSET_STORAGE_TOPIC=dbz_kisa_offsets -e STATUS_STORAGE_TOPIC=dbz_kisa_statuses -e LD_LIBRARY_PATH=/instant_client quay.io/debezium/connect:1.9

wget https://download.oracle.com/otn_software/linux/instantclient/211000/instantclient-basic-linux.x64-21.1.0.0.0.zip

docker cp instantclient-basic-linux.x64-21.1.0.0.0.zip dbz_130_kisa:/
unzip instantclient-basic-linux.x64-21.1.0.0.0.zip
mv instantclient_21_1 /instant_client

# 复制ojdbc.jar和xstreams.jar到Kafka的libs下
cp /instant_client/ojdbc8.jar /kafka/libs/
cp /instant_client/xstreams.jar /kafka/libs/

docker restart dbz_130_kisa
```

#### Xstream

```sql
alter system set db_recovery_file_dest_size=5G;

archive log list;
shutdown immediate;
startup mount;
alter database archivelog;
alter database open;
archive log list;

3.1查看数据库是否开启补充日志记录模式若已开启（YES)跳过3.2
SQL>SELECT SUPPLEMENTAL_LOG_DATA_ALL FROM v$database;
3.2开启补充日志记录模式
SQL>ALTER DATABASE ADD SUPPLEMENTAL LOG DATA(ALL) columns ;
3.3查看数据库是否开启强制日志记录模式若已开启（YES）跳过3.4
SQL>SELECT force_logging FROM v$database;
3.4开启强制日志记录模式
sQL>ALTER DATABASE FORCE LOGGING;#切换日志文件
sQL>ALTER SYSTEM SWITCH LOGFILE;#再次查询3.13.3确认

开启xstream
show parameters ENABLE_GOLDENGATE_REPLICATION;
alter system set ENABLE_GOLDENGATE_REPLICATION=true;

创建xstrmadmin用户
CREATE TABLESPACE xstream_TBS DATAFILE '/oradata/NEP/xstream_tbs.dbf' SIZE 500M;
CREATE USER xstrmadmin IDENTIFIED BY 123456 DEFAULT TABLESPACE xstream_TBS QUOTA UNLIMITED ON CDCUSER_TBS;

xstrmadmin授权
GRANT CREATE SESSION TO xstrmadmin;
GRANT SELECT ON v_$DATABASE to xstrmadmin;
GRANT FLASHBACK ANY TABLE TO xstrmadmin;
GRANT SELECT_CATALOG_ROLE  TO xstrmadmin;
GRANT EXECUTE_CATALOG_ROLE TO xstrmadmin;
GRANT CREATE DATABASE LINK TO xstrmadmin;
BEGIN
DBMS_XSTREAM_AUTH.GRANT_ADMIN_PRIVILEGE(
	grantee => 'xstrmadmin',
	privilege_type => 'CAPTURE',
	grant_select_privileges => TRUE
	);
END;
/


创建队列表
SQL> conn xstrmadmin/123456;
sQL> BEGIN
	DBMS_XSTREAM_ADM.SET_UP_QUEUE(
	queue_table => 'xstrmadmin.xstream_queue_table',
	queue_name => 'xstrmadmin.xstream_queue');
END;
/

source库名查询
select * from global_name;

创建捕获进程
BEGIN
	DBMS_CAPTURE_ADM.CREATE_CAPTURE(
		queue_name=>'xstrmadmin.xstream_queue',
		capture_name =>'xout_capture',
		capture_class => 'xstream',
		FIRST_SCN => null,
		use_database_link => true,
		SOURCE_DATABASE => '${global_name}'
		);
END;
/

添加捕获规则---已弃用
BEGIN
	DBMS_XSTREAM_ADM.ADD_SCHEMA_RULES(
		schema_name =>'${owner}',
		streams_type =>'capture',
        streams_name =>'xout_capture',
        queue_name =>'xstrmadmin.xstream_queue',
		include_dml => TRUE,
        include_ddl => TRUE,
		SOURCE_DATABASE => '${global_name}'
		);
END;
/

创建出站服务器---可直接添加规则
DECLARE
	tables DBMS_UTILITY.UNCL_ARRAY;
	schemas DBMS_UTILITY.UNCL_ARRAY;
BEGIN
	tables(1):=NULL;
	schemas(1):='${owner}';
	DBMS_XSTREAM_ADM.ADD_OUTBOUND(
    server_name=>'xout',
    queue_name =>'xstrmadmin.xstream_queue',
    SOURCE_DATABASE => '${global_name}',
    table_names=>tables,
    schema_names=>schemas);
END;
/

--创建出站服务器
BEGIN
	DBMS_XSTREAM_ADM.ADD_OUTBOUND(
    server_name=>'xout',
    queue_name =>'xstrmadmin.xstream_queue',
    SOURCE_DATABASE => '${global_name}',
    table_names=>'${table}');
END;
/
--add rule
BEGIN
	DBMS_XSTREAM_ADM.ALTER_OUTBOUND(
    server_name=>'xout',
    table_names=>'${table}',
    add=>TRUE,
    inclusion_rule=>TRUE);
END;
/
--drop rule
BEGIN
	DBMS_XSTREAM_ADM.ALTER_OUTBOUND(
    server_name=>'xout',
    table_names=>'${table}',
    add=>FALSE,
    inclusion_rule=>TRUE);
END;
/

回退xstream
exec dbms_apply_adm.stop_apply('XOUT');
exec dbms_capture_adm.stop_capture('XOUT_CAPTURE');
exec dbms_aqadm.stop_queue('xstrmadmin.xstream_queue');
exec dbms_capture_adm.drop_capture('XOUT_CAPTURE');
exec dbms_apply_adm.drop_apply('XOUT');
exec dbms_aqadm.drop_queue('xstrmadmin.xstream_queue');
exec dbms_aqadm.drop_queue_table('xstrmadmin.xstream_queue_table');
alter system set ENABLE_GOLDENGATE_REPLICATION=false;
archive log list;
shutdown immediate;
startup mount;
alter database noarchivelog;
alter database open;
archive log list;
#开启xstream会报错内存不足，关闭使用logminer
```

#### Monitor Xstream

```sql
--查看rules
SELECT STREAMS_NAME,STREAMS_TYPE,RULE_NAME,RULE_SET_TYPE,SCHEMA_NAME,OBJECT_NAME,RULE_TYPE FROM ALL_XSTREAM_RULES;
--查询capture进程
SELECT CAPTURE_NAME, STATUS FROM ALL_CAPTURE;
--查询apply进程
SELECT SERVER_NAME, STATUS FROM ALL_XSTREAM_OUTBOUND;
```



#### informix

```
dbaccess - $INFORMIXDIR/etc/syscdcv1.sql

选择sysmaster库
select name,is_buff_log from sysdatabases where name = 'test';
如果is_buff_log  0则已开启

给采集用户授权目标库的connect权限和syscdcv1库的dba权限
选择syscdcv1库
grant dba to cdcuser
选择目标库
grant connect to cdcuser
```

#### Xstream开启采集

```shell
#!/bin/bash

case $1 in
    1)
    curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" http://192.168.10.130:8083/connectors/ -d '
{
"name": "debezium-oracle",
"config": {
"connector.class" : "io.debezium.connector.oracle.OracleConnector",
"tasks.max" : "1",
"database.server.name" : "oracle130_test0907",
"database.hostname" : "192.168.10.130",
"database.port" : "1521",
"database.user" : "xstrmadmin",
"database.password" : "123456",
"database.dbname" : "nep",
"database.connection.adapter": "xstream",
"database.out.server.name": "xout",
"snapshot.mode" : "schema_only",
"table.include.list": "NEPTUNE.TEST",
"decimal.handling.mode": "string",
"database.history.kafka.bootstrap.servers" : "192.168.10.130:9092",
"database.history.kafka.topic": "schema-changes.oracle130_test0907"
}
}'
    ;;
    4)
    curl http://192.168.10.130:8083/connectors/
    ;;
    5)
    curl http://192.168.10.130:8083/connectors/"debezium-oracle" -X DELETE
    ;;
esac


```

