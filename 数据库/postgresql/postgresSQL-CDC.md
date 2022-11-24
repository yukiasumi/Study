# CentOS 安装postgresql

### 安装

#### 安装rpm

```perl
yum install https://download.postgresql.org/pub/repos/yum/reporpms/EL-7-x86_64/pgdg-redhat-repo-latest.noarch.rpm
```

#### 安装客户端

```perl
yum install postgresql10
```

#### 安装服务端

```perl
yum install postgresql10-server
```

#### 启动服务

```perl
systemctl enable postgresql-10
systemctl start postgresql-10
```

### 创建用户和数据库

#### 使用postgres用户

```perl
su - postgres
```
#### 登录postgresql数据库
```perl
psql
```
#### 创建用户，数据库，并授权
```plsql
create user test_user with password 'abc123';            // 创建用户
create database test_db owner test_user;                 // 创建数据库，建库如果不指定owner，那么默认是postgres
grant all privileges on database test_db to test_user;   // 授权。
```

### 远程访问

#### 修改config

```perl
修改/var/lib/pgsql/10/data/postgresql.conf文件，取消 listen_addresses 的注释，将参数值改为“*”

修改/var/lib/pgsql/10/data/pg_hba.conf文件，增加Ipv4 local connections
host	all		all		0.0.0.0/0		md5
```

#### 切换root用户，重启postgresql服务

```perl
systemctl restart postgresql-10.service
```

# CDC 配置

#### 配置库、WAL和主从复制参数

将以下内容添加到`postgresql.conf`配置文件中来保证解码插件的正常

```perl
vim /var/lib/pgsql/10/data/postgres
wal_level= logical
```

以下无需配置

```bash
shared_preload_libraries='wal2json' //告诉数据库加载wal2json（使用 decoderbuf 来代表 protobuf）
wal_level= logical //将WAL日志与逻辑解码结合使用
max_wal_senders=4 //告诉服务器使用最多4个进行来跟踪处理WAL的改动
max_replication_slots=4 //告诉服务器应当允许最多4个备份slot来创建WAL事件更改流

Debezium使用PostgreSQL的逻辑解码，它使用主从复制slot。slot保证保留Debezium监视所需的所有WAL，即使在Debezium中断期间。因此，密切监视slot非常重要，以避免过多的磁盘消耗和其他可能发生的情况，如Debezium slot 闲置时间过长导致编目膨胀。
```

#### 开启主从复制

主从复制可以有拥有数据库适当权限的用户开启并且对开放的主机有效。为了给用户主从复制的权限，定义一个至少拥有REPLICATION和LOGIN权限的PG角色

```plsql
alter role cdcuser replication login;


--------------------------------------------------------------------------以下不需要
CREATE ROLE CDC;
GRANT REPLICATION_GROUP TO <original_owner>;
GRANT REPLICATION_GROUP TO <replication_user>;
ALTER TABLE <table_name> OWNER TO REPLICATION_GROUP;

\c test;	//切换数据库
\q		//退出psql
ALTER USER postgres WITH SUPERUSER;//授予超级用户
```

在`pg_hba.conf`中添加以下配置

告诉服务器允许本地的PG主从复制

允许本机的PG接受IPV4主从复制

允许本机的PG接受IPV6主从复制

```perl
vim /var/lib/pgsql/10/data/pg_hba.conf

############ REPLICATION ##############
local   replication    postgres                      trust
host    replication    postgres    127.0.0.1/32      trust 
host    replication    postgres    ::1/128           trust
```

#### 开启任务

```perl
###开启postgresql任务
POST http://192.168.10.130:8083/connectors/
Content-Type: application/json

{
  "name": "postgresql-connector-1124",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "database.hostname": "192.168.10.130",
    "database.port": "5432",
    "database.user": "cdcuser",
    "database.password": "123456",
    "database.dbname" : "test",
    "database.server.name": "postgresql-connector-1124",
    "table.include.list": "public.student",
    "plugin.name": "pgoutput"
  }
}
```

