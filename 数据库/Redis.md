## 1. Redis安装

```perl
#安装
gcc --version  #查看C语言环境，没有需要安装 yum -y install gcc
tar -zxvf redis-6.2.1.tar.gz -C /opt/
cd /opt/redis-6.2.1/
make
make install

#前台启动
cd /usr/local/bin/
redis-server

#后台启动设置daemonize no改成yes
cp redis.conf /etc/
vim /etc/redis.conf
:%s/daemonize no/daemonize yes/g
cd /usr/local/bin/
redis-server /etc/redis.conf

#查看redis进程
ps -ef|grep redis
#连接客户端
[root@Neptune bin]# redis-cli
127.0.0.1:6379> ping
PONG
127.0.0.1:6379>

#redis关闭
客户端执行shutdown
or
找到进程 kill -9
```

## 2. 简介

默认16个数据库，类似数组下标从0开始，初始默认使用0号库

使用命令 `select  <dbid>`来切换数据库。如: `select 8 `

统一密码管理，所有库同样密码。

`dbsize`查看当前数据库的key的数量

`flushdb`清空当前库

`flushall`通杀全部库

![image-20221031214056347](../images/image-20221031214056347.png)

## 3. 常用五大数据类型

### 3.1 Redis键(key)操作

```perl
keys *查看当前库所有key    (匹配：keys *1)
exists key判断某个key是否存在
type key 查看你的key是什么类型
del key       删除指定的key数据
unlink key   根据value选择非阻塞删除，仅将keys从keyspace元数据中删除，真正的删除会在后续异步操作。（异步）
expire key 10   10秒钟：为给定的key设置过期时间
ttl key 查看还有多少秒过期，-1表示永不过期，-2表示已过期

select命令切换数据库
dbsize查看当前数据库的key的数量
flushdb清空当前库
flushall通杀全部库
```



```perl
[root@Neptune bin]# redis-cli
127.0.0.1:6379> keys *
(empty array)
127.0.0.1:6379> set k1 alice
OK
127.0.0.1:6379> set k2 kirito
OK
127.0.0.1:6379> set k3 asuna
OK
127.0.0.1:6379> keys *
1) "k3"
2) "k1"
3) "k2"
127.0.0.1:6379> exists k1
(integer) 1
127.0.0.1:6379> exists k4
(integer) 0
127.0.0.1:6379> type k2
string
127.0.0.1:6379> del k3
(integer) 1
127.0.0.1:6379> keys *
1) "k1"
2) "k2"
127.0.0.1:6379> expire k1 10
(integer) 1
127.0.0.1:6379> ttl k1
(integer) 6
127.0.0.1:6379> ttl k1
(integer) 3
127.0.0.1:6379> ttl k1
(integer) -2
127.0.0.1:6379> ttl k2
(integer) -1
127.0.0.1:6379> select 1
OK
127.0.0.1:6379[1]> select 0
OK
127.0.0.1:6379> dbsize
(integer) 1
127.0.0.1:6379>
```



### 3.2 Redis字符串(String)

#### 3.2.1 简介

String是Redis最基本的类型，你可以理解成与Memcached一模一样的类型，一个key对应一个value。

String类型是二进制安全的。意味着Redis的string可以包含任何数据。比如jpg图片或者序列化的对象。

String类型是Redis最基本的数据类型，一个Redis中字符串value最多可以是512M

#### 3.2.2 常用命令

`set  <key><value>`添加键值对

 ```perl
127.0.0.1:6379> set k1 v100
OK
127.0.0.1:6379> set k2 v200
OK
127.0.0.1:6379> keys *
1) "k1"
2) "k2"

 ```

*NX：当数据库中key不存在时，可以将key-value添加数据库

*XX：当数据库中key存在时，可以将key-value添加数据库，与NX参数互斥

*EX：key的超时秒数

*PX：key的超时毫秒数，与EX互斥

`get  <key>`查询对应键值

```perl
127.0.0.1:6379> get k1
"v100"
127.0.0.1:6379> set k1 v1100
OK
127.0.0.1:6379> get k1
"v1100"
```

`append  <key><value>`将给定的<value> 追加到原值的末尾

```perl
127.0.0.1:6379> append k1 abc
(integer) 8
127.0.0.1:6379> get k1
```

`strlen  <key>`获得值的长度

```perl
127.0.0.1:6379> strlen k1
(integer) 8
```

`setnx  <key><value>`只有在 key 不存在时   设置 key 的值

 ```perl
127.0.0.1:6379> setnx k1 v1
(integer) 0
127.0.0.1:6379> get k1
"v1100abc"
127.0.0.1:6379> setnx k3 v3
(integer) 1
127.0.0.1:6379> get k3
"v3"
 ```

`incr  <key>`将 key 中储存的数字值增1，只能对数字值操作，如果为空，新增值为1

```perl
127.0.0.1:6379> set k4 500
OK
127.0.0.1:6379> incr k4
(integer) 501
127.0.0.1:6379> get k4
"501"
```

`decr  <key>`将 key 中储存的数字值减1，只能对数字值操作，如果为空，新增值为-1

```perl
127.0.0.1:6379> decr k4
(integer) 500
```

`incrby / decrby  <key> <步长>`将 key 中储存的数字值增减。自定义步长。

```perl
127.0.0.1:6379> incrby k4 10
(integer) 510
127.0.0.1:6379> decrby k4 20
(integer) 490
```

==原子性==

INCR key
起始版本:1.0.0时间复杂度:O(1)
对存储在指定key的数值执行**原子**的加1操作。

所谓**原子**操作是指不会被线程调度机制打断的操作；

这种操作一旦开始，就一直运行到结束，中间不会有任何 context switch （切换到另一个线程）。

（1）在单线程中， 能够在单条指令中完成的操作都可以认为是"原子操作"，因为中断只能发生于指令之间。

（2）在多线程中，不能被其它进程（线程）打断的操作就叫原子操作。

Redis单命令的原子性主要得益于Redis的单线程。



`mset  <key1><value1><key2><value2>  ..... `同时设置一个或多个 key-value对  

```perl
127.0.0.1:6379> mset k1 v1 k2 v2 k3 v3
OK
127.0.0.1:6379> keys *
1) "k3"
2) "k1"
3) "k2"
```

`mget  <key1><key2><key3> .....`同时获取一个或多个 value  

```perl
127.0.0.1:6379> mget k1 k2 k3
1) "v1"
2) "v2"
3) "v3"
```

`msetnx <key1><value1><key2><value2>  ..... `同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在。

==原子性，有一个失败则都失败==

```perl
127.0.0.1:6379> msetnx k11 v11 k12 v12 k3 v13
(integer) 0
127.0.0.1:6379> keys *
1) "k3"
2) "k1"
3) "k2"
127.0.0.1:6379> msetnx k11 v11 k12 v12 k3 v3
(integer) 0
127.0.0.1:6379> msetnx k11 v11 k12 v12 k13 v13
(integer) 1
127.0.0.1:6379> keys *
1) "k1"
2) "k12"
3) "k13"
4) "k2"
5) "k3"
6) "k11"
```

`getrange  <key><起始位置><结束位置>`获得值的范围，类似java中的substring，**前包，后包**

```perl
127.0.0.1:6379> set name alicesoft
OK
127.0.0.1:6379> getrange name 0 4
"alice"
```

`setrange  <key><起始位置><value>`用 <value>  覆写<key>所储存的字符串值，从<起始位置>开始(**索引从0开始**)。

 ```perl
127.0.0.1:6379> setrange name 3 xxx
(integer) 9
127.0.0.1:6379> get name
"alixxxoft"
 ```

`setex  <过期时间>`设置键值的同时，设置过期时间，单位秒。

```perl
127.0.0.1:6379> setex age 20 value20
OK
127.0.0.1:6379> ttl age
(integer) 16
127.0.0.1:6379> ttl age
(integer) 11
```

`getset <key><value>`以新换旧，设置了新值同时获得旧值。

```perl
127.0.0.1:6379> getset name asuna
"alixxxoft"
127.0.0.1:6379> get name
"asuna"
```

#### 3.2.3 数据结构

String的数据结构为简单动态字符串(Simple Dynamic String,缩写SDS)。是可以修改的字符串，内部结构实现上类似于Java的ArrayList，采用预分配冗余空间的方式来减少内存的频繁分配.

![img](file:///C:\Users\hakuou\AppData\Local\Temp\ksohtml24320\wps5.jpg) 

如图中所示，内部为当前字符串实际分配的空间capacity一般要高于实际字符串长度len。当字符串长度小于1M时，扩容都是加倍现有的空间，如果超过1M，扩容时一次只会多扩1M的空间。需要注意的是字符串最大长度为512M。

### 3.3 Redis列表(List)

#### 3.3.1 简介

单键多值

Redis 列表是简单的字符串列表，按照插入顺序排序。你可以添加一个元素到列表的头部（左边）或者尾部（右边）。

它的底层实际是个==双向链表==，对两端的操作性能很高，通过索引下标的操作中间的节点性能会较差。

![img](file:///C:\Users\hakuou\AppData\Local\Temp\ksohtml24320\wps6.png)

#### 3.3.2 常用命令

`lpush/rpush  <key><value1><value2><value3> .... `从左边/右边插入一个或多个值。

`lpush` 从左插入，之前进入的值会被推到右边

`rpush` 从左插入，之前进入的值会被推到左边

```perl
127.0.0.1:6379> lpush k1 v1 v2 v3
(integer) 3
127.0.0.1:6379> lrange k1 0 -1
1) "v3"
2) "v2"
3) "v1"
127.0.0.1:6379> rpush k2 v1 v2 v3
(integer) 3
127.0.0.1:6379> lrange k2 0 -1
1) "v1"
2) "v2"
3) "v3"
```

![image-20221031223942191](../images/image-20221031223942191.png)

`lpop/rpop  <key>`从左边/右边吐出一个值。==值在键在，值光键亡。==

 ```perl
127.0.0.1:6379> lpop k1
"v3"
127.0.0.1:6379> lpop k1
"v2"
127.0.0.1:6379> lpop k1
"v1"
127.0.0.1:6379> lpop k1
(nil)
127.0.0.1:6379> keys *
1) "k2"
127.0.0.1:6379> lpop k2
"v1"
127.0.0.1:6379> rpop k2
"v3"
127.0.0.1:6379> rpop k2
"v2"
127.0.0.1:6379> rpop k2
(nil)
 ```

`rpoplpush  <key1> <key2>`从<key1>列表右边吐出一个值，插到<key2>列表左边。

 ```perl
127.0.0.1:6379> rpoplpush k1 k2
"v1"
127.0.0.1:6379> lrange k2 0 -1
1) "v1"
2) "v11"
3) "v12"
4) "v13"
127.0.0.1:6379> lrange k1 0 -1
1) "v3"
2) "v2"
 ```

`lrange <key> <start> <stop>`按照索引下标获得元素(从左到右)

`lrange mylist 0 -1`  0左边第一个，-1右边第一个，（0-1表示获取所有）

```perl
127.0.0.1:6379> lrange k2 0 -1
1) "v1"
2) "v11"
3) "v12"
4) "v13"
```

`lindex <key> <index>`按照索引下标获得元素(从左到右)

```perl
127.0.0.1:6379> lrange k2 0 -1
1) "v1"
2) "v11"
3) "v12"
4) "v13"
127.0.0.1:6379> lindex k2 0
"v1"
127.0.0.1:6379> lindex k2 2
"v12"
```

`llen <key>`获得列表长度 

 ```perl
127.0.0.1:6379> llen k2
(integer) 4
 ```



`linsert <key>  before <value> <newvalue>`在<value>的后面插入<newvalue>插入值

```perl
127.0.0.1:6379> linsert k2 after "v12" "newv11"
(integer) 6
127.0.0.1:6379> lrange k2 0 -1
1) "v1"
2) "newv11"
3) "v11"
4) "v12"
5) "newv11"
6) "v13"
```

`lrem <key> <n> <value>`从<value>左边删除n个value(从左到右)

```perl
127.0.0.1:6379> lrem k2 2 "newv11"
(integer) 2
127.0.0.1:6379> lrange k2 0 -1
1) "v1"
2) "v11"
3) "v12"
4) "v13"
```

`lset <key> <index> <value>`将列表key下标为index的值替换成value

```perl
127.0.0.1:6379> lset k2 1 neptune
OK
127.0.0.1:6379> lrange k2 0 -1
1) "v1"
2) "neptune"
3) "v12"
4) "v13"
```

#### 3.3.3 数据结构

List的数据结构为快速链表quickList。

首先在列表元素较少的情况下会使用一块连续的内存存储，这个结构是ziplist，也即是压缩列表。

它将所有的元素紧挨着一起存储，分配的是一块连续的内存。

当数据量比较多的时候才会改成quicklist。

因为普通的链表需要的附加指针空间太大，会比较浪费空间。比如这个列表里存的只是int类型的数据，结构上还需要两个额外的指针prev和next。

![img](file:///C:\Users\hakuou\AppData\Local\Temp\ksohtml24320\wps7.jpg) 

Redis将链表和ziplist结合起来组成了quicklist。也就是将多个ziplist使用双向指针串起来使用。这样既满足了快速的插入删除性能，又不会出现太大的空间冗余。

### 3.4

