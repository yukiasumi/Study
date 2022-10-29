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
    
</configuration>

```

```
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

