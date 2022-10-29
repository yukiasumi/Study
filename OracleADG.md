## (Active) Data Guard 19c

### 环境规划

|                      | 主库           | 备库           | 说明           |
| :------------------- | :------------- | :------------- | :------------- |
| db_name              | neptune        | neptune        | 必须一致       |
| db_unique_name       | neptune        | uranus         | 必须不一致     |
| instance_name        | neptune        | uranus         | 一致不一致都行 |
| IP                   | 192.168.10.131 | 192.168.10.132 |                |
| tns_name             | neptune        | uranus         |                |
| Archive destination  | /arch          | /arch          |                |
| ORACLE_BASH          | /oracle        | /oracle        |                |
| datafile Destination | /oradata       | /oradata       |                |

### 环境准备

```perl
主库 192.168.10.131
备库 192.168.10.132
ORACLE_HOME=/oracle/product/19.0.0/dbhome_1

Oracle版本 19c
主库安装实例
备库仅安装Oracle软件，不安装监听和实例
```

### 开始搭建

#### 主库（开启归档）

```sql
su - oracle
sqlplus / as sysdba
alter system set log_archive_dest_1='LOCATION=/arch VALID_FOR=(ALL_LOGFILES,ALL_ROLES) DB_UNIQUE_NAME=neptune' scope=both;
shutdown immediate;
startup mount;
alter database archivelog;
shutdown immediate;
startup;
archive log list;
exit
```

```shell
cd $ORACLE_HOME/dbs
cp orapw* /tmp/
```

```sql
sqlplus / as sysdba
ALTER DATABASE FORCE LOGGING;
CREATE pfile='/tmp/init' from spfile;
exit
```

```shell
cp $ORACLE_HOME/network/admin/tnsnames.ora $ORACLE_HOME/network/admin/tnsnames.ora.bak
vi $ORACLE_HOME/network/admin/tnsnames.ora  --增加以下内容

neptune =
  (DESCRIPTION =
    (ADDRESS = (PROTOCOL = TCP)(HOST = 192.168.10.131)(PORT = 1521))
    (CONNECT_DATA =
      (SERVER = DEDICATED)
      (SERVICE_NAME = neptune)
    )
  )

uranus =
  (DESCRIPTION =
    (ADDRESS = (PROTOCOL = TCP)(HOST = 192.168.10.132)(PORT = 1521))
    (CONNECT_DATA =
      (SERVER = DEDICATED)
      (SERVICE_NAME = uranus)
    )
  )
:wq!	--保存退出
```

```shell
scp /tmp/orapw* 192.168.10.132:/tmp/
scp /tmp/init 192.168.10.132:/tmp
cat /tmp/init
*.audit_file_dest='/oracle/admin/neptune/adump'
*.audit_trail='db'
*.compatible='19.0.0'
*.control_files='/oradata/URANUS/control01.ctl','/oradata/URANUS/control02.ctl'
*.db_block_size=8192
*.db_name='neptune'
*.diagnostic_dest='/oracle'
*.dispatchers='(PROTOCOL=TCP) (SERVICE=neptuneXDB)'
*.local_listener='LISTENER_URANUS'
*.log_archive_dest_1='LOCATION=/arch VALID_FOR=(ALL_LOGFILES,ALL_ROLES) DB_UNIQUE_NAME=uranus'
*.memory_target=1572m
*.nls_language='SIMPLIFIED CHINESE'
*.nls_territory='CHINA'
*.open_cursors=300
*.processes=300
*.remote_login_passwordfile='EXCLUSIVE'
*.undo_tablespace='UNDOTBS1'
```

#### 备库（建库）

```shell
chown oracle:oinstall /tmp/orapw*
chown oracle:oinstall /tmp/init

su - oracle
ls -l /oradata
mkdir /oradata/URANUS/

mkdir -p /oracle/admin/uranus/adump
cp /tmp/orapw* $ORACLE_HOME/dbs/orapwuranus

vi $ORACLE_HOME/network/admin/listener.ora

SID_LIST_LISTENER=
  (SID_LIST=
    (SID_DESC =
      (GLOBAL_DBNAME= uranus)
      (ORACLE_HOME =/oracle/product/19.0.0/dbhome_1)
      (SID_NAME = uranus)
    )
  )
	
LISTENER=
  (DESCRIPTION =
    (ADDRESS = (PROTOCOL= TCP)(HOST = 192.168.10.132)(PORT = 1521))
  )
  
ADR_BASE_LISTENER =/oracle
:wq!	--保存退出

vi $ORACLE_HOME/network/admin/tnsnames.ora 
neptune =
  (DESCRIPTION =
    (ADDRESS = (PROTOCOL = TCP)(HOST = 192.168.10.131)(PORT = 1521))
    (CONNECT_DATA =
      (SERVER = DEDICATED)
      (SERVICE_NAME = neptune)
    )
  )

uranus =
  (DESCRIPTION =
    (ADDRESS = (PROTOCOL = TCP)(HOST = 192.168.10.132)(PORT = 1521))
    (CONNECT_DATA =
      (SERVER = DEDICATED)
      (SERVICE_NAME = uranus)
    )
  )
:wq!	--保存退出

vi /tmp/init
:%s/neptune/uranus/g	--将neptune替换为uranus
:%s/NEPTUNE/URANUS/g	--将neptune替换为uranus
*.db_name='neptune' db_name替换为neptune	--db_name需保持一致
:wq!	--保存退出
```

```sql
sqlplus / as sysdba
create spfile from pfile='/tmp/init';
startup nomount;

alter system set db_unique_name='uranus' scope=spfile;
alter system set log_archive_format="uranus_%t_%s_%r.arc" scope=spfile;
alter system set standby_file_management='AUTO' scope=spfile;
alter system set db_file_name_convert='/oradata/NEPTUNE/', '/oradata/URANUS/' scope=spfile;
alter system set log_file_name_convert='/oradata/NEPTUNE/' , '/oradata/URANUS/' scope=spfile;

alter system set log_archive_config='DG_CONFIG=(neptune,uranus)' scope=spfile;
alter system set log_archive_dest_state_1=enable scope=spfile;
alter system set log_archive_dest_1='LOCATION=/arch VALID_FOR=(ALL_LOGFILES,ALL_ROLES) DB_UNIQUE_NAME=uranus' scope=spfile;
alter system set fal_client='uranus' scope=spfile;
alter system set fal_server='neptune' scope=spfile;
alter system set log_archive_dest_state_2=defer scope=spfile;
alter system set log_archive_dest_2='service=neptune lgwr async noaffirm delay=0 optional compression=disable valid_for=(online_logfiles,primary_role) db_unique_name=neptune' scope=spfile;

shutdown immediate;
startup nomount;
exit;
```

```perl
lsnrctl start
lsnrctl status

tnsping neptune
sqlplus sys/123456@neptune as sysdba
exit
```

#### 主库（测试连接备库）

```perl
tnsping uranus
sqlplus sys/123456@uranus as sysdba
exit
```

#### 备库（rman同步）

```perl
rman target sys/123456@neptune
exit
rman target sys/123456@uranus
exit

rman target sys/123456@neptune auxiliary sys/123456@uranus
duplicate target database for standby from active database nofilenamecheck;
exit

sqlplus / as sysdba
ALTER DATABASE ADD STANDBY LOGFILE THREAD 1 GROUP 21 ('/oradata/URANUS/styredo21_01.log', '/oradata/URANUS/styredo21_02.log') SIZE 200M;
ALTER DATABASE ADD STANDBY LOGFILE THREAD 1 GROUP 22 ('/oradata/URANUS/styredo22_01.log', '/oradata/URANUS/styredo22_02.log') SIZE 200M;
ALTER DATABASE ADD STANDBY LOGFILE THREAD 1 GROUP 23 ('/oradata/URANUS/styredo23_01.log', '/oradata/URANUS/styredo23_02.log') SIZE 200M;
ALTER DATABASE ADD STANDBY LOGFILE THREAD 1 GROUP 24 ('/oradata/URANUS/styredo24_01.log', '/oradata/URANUS/styredo24_02.log') SIZE 200M;

```

#### 主库（初始化参数）

```sql
sqlplus / as sysdba

create pfile='/tmp/neptune.pfile' from spfile;	--备份参数文件
alter system set log_archive_config='DG_CONFIG=(neptune,uranus)' scope=both;
alter system set log_archive_dest_state_2='defer' scope=both;
alter system set log_archive_dest_state_1='enable' scope=both;
alter system set log_archive_dest_2='service=uranus lgwr async noaffirm delay=0 optional compression=disable valid_for=(online_logfiles,primary_role) db_unique_name=uranus' scope=both;
alter system set fal_client='neptune' scope=both;
alter system set fal_server='uranus' scope=both;

alter system set standby_file_management='AUTO' scope=both;
alter system set db_file_name_convert='/oradata/URANUS/' , '/oradata/NEPTUNE/' scope=spfile;
alter system set log_file_name_convert='/oradata/URANUS/', ' /oradata/NEPTUNE/' scope=spfile;
alter system set log_archive_dest_state_2=enable scope=both ;

```

#### 备库（recover）

```sql
alter system set log_archive_dest_state_2=enable;
ALTER DATABASE RECOVER MANAGED STANDBY DATABASE DISCONNECT FROM SESSION;
```

#### 主库（添加standby日志）

```sql
ALTER DATABASE ADD STANDBY LOGFILE THREAD 1 GROUP 21 ('/oradata/NEPTUNE/styredo21_01.log', '/oradata/NEPTUNE/styredo21_02.log') SIZE 200M;
ALTER DATABASE ADD STANDBY LOGFILE THREAD 1 GROUP 22 ('/oradata/NEPTUNE/styredo22_01.log', '/oradata/NEPTUNE/styredo22_02.log') SIZE 200M;
ALTER DATABASE ADD STANDBY LOGFILE THREAD 1 GROUP 23 ('/oradata/NEPTUNE/styredo23_01.log', '/oradata/NEPTUNE/styredo23_02.log') SIZE 200M;
ALTER DATABASE ADD STANDBY LOGFILE THREAD 1 GROUP 24 ('/oradata/NEPTUNE/styredo24_01.log', '/oradata/NEPTUNE/styredo24_02.log') SIZE 200M;
```

#### 备库（open）

```sql
ALTER DATABASE RECOVER MANAGED STANDBY DATABASE CANCEL;
ALTER DATABASE OPEN READ ONLY;
ALTER DATABASE RECOVER MANAGED STANDBY DATABASE DISCONNECT FROM SESSION;
SELECT NAME,OPEN_MODE FROM v$DATABASE;
```

### 搭建完成

#### 测试

```sql
--在主库
create table test (id number(18,2));
insert into test values(1478);
commit;

--在备库
select * from test;
```

#### 注意事项

```sql
--上下游db_name一致
--下游配置unique_name区分

--rman同步时
--上游数据库open状态
--下游数据库nomount状态

--若备库无法打开 查询上下游DG
show parameter log_archive_config;

--以下语句执行结果应一致
select nvl(last_change#,0),checkpoint_change# from v$datafile;
select checkpoint_change# from v$database;
select checkpoint_change# from v$datafile_header;

--scope=spfile 更改spfile，不更改内存，不立即生效，下次数据库启动生效。
--scope=memory 更改内存，不更改spfile。下次数据库启动失效。(有一些参数只允许用这种方法更改)
--scope=both 内存和spfile都更改。
--不指定scope参数，等同于scope=both。

```

