### AIX加磁盘空间
```perl
chfs -a size=+2000000 /home
chfs -a size=4000000 /home
```

### 安装Informix

```perl
onclean -ky
删除/var/adm/ifx缓存文件
```



```perl
mkgroup informix

useradd -m -g informix informix

passwd informix

```

/work/ids_install

安装引导，不创建实例

ipcs查看共享内存

ipcrm -m 删除共享内存【不是0xffffffff的】



配置环境变量

```perl
su - informix

vi .profile

INFORMIXDIR=/work/informix

INFORMIXSERVER=cib

ONCONFIG=onconfig.cib

INFORMIXSQLHOSTS=$INFORMIXDIR/etc/sqlhosts

LIBPATH=$INFORMIXDIR/lib:$INFORMIXDIR/lib/esql:/usr/lib

PATH=$INFORMIXDIR/bin:$PATH

DBDATE=Y4MD

export DBDATE PATH INFORMIXDIR INFORMIXSERVER ONCONFIG INFORMIXSQLHOSTS LIBPATH

. ./.profile
```

```perl
cd $INFORMIXDIR/etc

vi sqlhosts

cib onsoctcp 10.7.41.217 cibsvc
```

root用户

```perl
cp onconfig.std onconfig.cib

vi onconfig.cib

ROOTPATH=$INFORMIXDIR/dbs/rootdbs

DBSERVERNAME cib

FULL_DISK_INIT 1
```

```perl
vi /etc/services

cibsvc	50000/tcp
```

```perl
mkdir $INFORMIXDIR/dbs

touch $INFORMIXDIR/dbs/rootdbs

chmod 660 $INFORMIXDIR/dbs/rootdbs
```

```perl
su - informix

oninit -ivy
```

提示onconfig.cib和sqlhosts不属于informix

```perl
chown informix:informix onconfig.cib

chown informix:informix sqlhosts

chmod 660 sqlhosts
```

启动成功

dbaccess->database->select->选择库后enter->

1809:Server rejected the connection

拒绝连接

重装时切换端口



### Informix命令

```perl
启动 oninit-vy
首次初始化实例 oninit-ivy
关闭 onmode-ky
清空并切换日志	onmode-l
查看会话	onstat -g ses
查看sql语句进程	onstat -g sql
自动切换日志 ontape -a
查看逻辑日志 onstat -l
查看日志 onstat -o或者tail -100f $INFORMIXDIR/tmp/online.log
配置文件位置	
$INFORMIXDIR/etc/onconfig（常用）
$INFORMIXDIR/etc/sqlhost
```
### Informix连接不上
<font color=red size=24>不要执行chown -R informix:informix /informix！！！</font>

### 增加空间

```perl
初始化informix数据库： oninit -ivy
此时，数据库是空的，什么都没。
我们需要规划数据库空间，创建相应的物理表空间，逻辑表空间，临时表空间。
首先在dbs目录下创建相应的文件并给予权限：
touch phydbs logdbs appdbs temp01 temp02 temp03 temp04
chmod 660 phydbs logdbs appdbs temp01 temp02 temp03 temp04
执行创建表空间的命令：
onspaces -c -d phydbs -p {informixdir}/dbs/phydbs -o 0 -s 500000 【一般为300M。此处创建500M】
onspaces -c -d logdbs -p {informixdir}/dbs/logdbs -o 0 -s 1000000 【一般为1G，共10个逻辑文件】
onspaces -c -d appdbs -p {informixdir}/dbs/appdbs -o 0 -s 4000000 【自己数据库所在空间，大小根据实际情况而定】
onspaces -c -d temp01 -t -p {informixdir}/dbs/temp01 -o 0 -s 500000 【临时表空间，一般为4个，大小为500M，注意到创建临时表空间命令中多了 -t 】
onspaces -c -d temp02 -t -p {informixdir}/dbs/temp02 -o 0 -s 500000
onspaces -c -d temp03 -t -p {informixdir}/dbs/temp03 -o 0 -s 500000
onspaces -c -d temp04 -t -p {informixdir}/dbs/temp04 -o 0 -s 500000

切换物理日志：
onparams -p -s 480000 -d phydbs (注意：大小要比创建phydbs时稍微小一些不然会提示大小太大。)
此时可能会提示：IBM Informix Dynamic Server must be in quiescent mode
可以用onmode -sy切换到静态模式。也可以重启oninit -sy

切换逻辑日志：
首先创建逻辑文件：
for((i=0;i<10;i++))do onparams -a -d logdbs -s 100000 -i ;done;
最后一个逻辑文件创建会提示不成功，像之前的物理日志提示一样，要将大小修改的稍微小一些。

第二：切换逻辑日志
多次执行onmode -l 直到逻辑日志切换到新创建的逻辑文件中。
第：删除原逻辑文件
onparams -d -l {逻辑文件号} -y

```

### 导出表结构

```perl
dbschema命令导出表结构
dbschema -d [dbname] 导出所有表
-t [table]

dbschema -d lsk > lsk.sql
将revoke注释掉替换revoke为--revoke
dbaccess lsk lsk.sql
```

### 命令行执行语句

```perl
1.sql文件
cat > 1.sql
select * from sysmaster;

dbaccess 库名 1.sql

2.语句
echo "select * from sysmaster" | dbaccess 1.sql

3.数据文件
dbaccess 库名 <<!
load from 1.unl insert into test;
!

dbaccess 库名 <<!
unload to 1.unl select * from test;
!

unload to 1.unl select first 1 * from test;
```

### 开启CDC

```perl
dbaccess - $INFORMIXDIR/syscdcv1.sql
```

