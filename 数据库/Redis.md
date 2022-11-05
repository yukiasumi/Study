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

### 3.4 Redis集合(Set)

#### 3.4.1 简介

Redis set对外提供的功能与list类似是一个列表的功能，特殊之处在于set是可以==**自动排重且无序**==的，当你需要存储一个列表数据，又不希望出现重复数据时，set是一个很好的选择，并且set提供了判断某个成员是否在一个set集合内的重要接口，这个也是list所不能提供的。

Redis的Set是string类型的无序集合。它底层其实是一个value为null的hash表，所以添加，删除，查找的**复杂度都是O(1)**。

一个算法，随着数据的增加，执行时间的长短，如果是O(1)，数据增加，查找数据的时间不变

#### 3.4.2 常用命令

`sadd <key> <value1> <value2> ..... `将一个或多个 member 元素加入到集合 key 中，已经存在的 member 元素将被忽略

`smembers <key>`取出该集合的所有值。

```perl
127.0.0.1:6379> sadd k1 v1 v2 v3
(integer) 3
127.0.0.1:6379> smembers k1
1) "v3"
2) "v1"
3) "v2"
```

`sismember <key> <value>`判断集合`<key>`是否为含有该`<value>`值，有1，没有0

```perl
127.0.0.1:6379> sismember k1 v1
(integer) 1
127.0.0.1:6379> sismember k1 v9
(integer) 0
```

`scard <key>`返回该集合的元素个数。

`srem <key> <value1> <value2> .... `删除集合中的某个元素。

`spop <key>`随机从该集合中吐出一个值。

`srandmember <key> <n>`随机从该集合中取出n个值。不会从集合中删除 。

```perl
127.0.0.1:6379> sismember k1 v1
(integer) 1
127.0.0.1:6379> sismember k1 v9
(integer) 0
127.0.0.1:6379> scard k1
(integer) 3
127.0.0.1:6379> srem k1 v1 v2
(integer) 2
127.0.0.1:6379> spop k1 1
1) "v3"
127.0.0.1:6379> sadd k2 v1 v2 v3 v4
(integer) 4
127.0.0.1:6379> spop k2 1
1) "v3"
127.0.0.1:6379> srandmember k2 2
1) "v1"
2) "v2"
```

`smove <source> <destination> value`把集合中一个值从一个集合移动到另一个集合

```perl
127.0.0.1:6379> sadd k1 v1 v2 v3
(integer) 3
127.0.0.1:6379> sadd k2 v4 v5 v6
(integer) 3
127.0.0.1:6379> smove k1 k2 v3
(integer) 1
127.0.0.1:6379> smembers k1
1) "v1"
2) "v2"
127.0.0.1:6379> smembers k2
1) "v3"
2) "v6"
3) "v4"
4) "v5"
```

`sinter <key1> <key2>`返回两个集合的交集元素。

`sunion <key1> <key2>`返回两个集合的并集元素。

`sdiff <key1> <key2>`返回两个集合的差集元素(key1中的，不包含key2中的)

```perl
127.0.0.1:6379> sadd k3 v4 v6 v7
(integer) 3
127.0.0.1:6379> smembers k2
1) "v3"
2) "v6"
3) "v4"
4) "v5"
127.0.0.1:6379> smembers k3
1) "v6"
2) "v4"
3) "v7"
127.0.0.1:6379> sinter k2 k3
1) "v6"
2) "v4"
127.0.0.1:6379> sunion k2 k3
1) "v5"
2) "v3"
3) "v6"
4) "v4"
5) "v7"
127.0.0.1:6379> sdiff k2 k3
1) "v3"
2) "v5"
```

#### 3.4.3 数据结构

Set数据结构是dict字典，字典是用哈希表实现的。

Java中HashSet的内部实现使用的是HashMap，只不过所有的value都指向同一个对象。Redis的set结构也是一样，它的内部也使用hash结构，所有的value都指向同一个内部值。

### 3.5 Redis哈希(Hash)

#### 3.5.1 简介

Redis hash 是一个键值对集合。

Redis hash是一个string类型的field和value的映射表，hash特别适合用于存储对象。

类似Java里面的Map<String,Object>

用户ID为查找的key，存储的value用户对象包含姓名，年龄，生日等信息，如果用普通的key/value结构来存储

主要有以下2种存储方式：

![image-20221101214157075](C:\Users\hakuou\Documents\program\Git\images\image-20221101214157075.png)

第一种：每次修改用户的某个属性需要，先反序列化改好后再序列化回去。开销较大。

第二种：用户ID数据冗余

第三种：通过 ==key(用户ID) + field(属性标签)== 就可以操作对应属性数据了，既不需要重复存储数据，也不会带来序列化和并发修改控制的问题

#### 3.5.2 常用命令

`hset <key> <field> <value>`给`<key>`集合中的  `<field>`键赋值`<value>`

`hget <key1> <field>`从`<key1>`集合`<field>`取出 value 

```perl
127.0.0.1:6379> hset user:1001 id 1
(integer) 1
127.0.0.1:6379> hset user:1001 name ircus
(integer) 1
127.0.0.1:6379> hget user:1001 name
"ircus"
```

`hmset <key1> <field1> <value1> <field2> <value2>... `批量设置hash的值

`hexists <key1> <field>`查看哈希表 key 中，给定域 field 是否存在。 

`hkeys <key>`列出该hash集合的所有field

`hvals <key>`列出该hash集合的所有value

```perl
127.0.0.1:6379> hmset user:1002 id 2 name unicorn age 17
OK
127.0.0.1:6379> hexists user:1002 id
(integer) 1
127.0.0.1:6379> hexists user:1002 addr
(integer) 0
127.0.0.1:6379> hkeys user:1002
1) "id"
2) "name"
3) "age"
127.0.0.1:6379> hvals user:1002
1) "2"
2) "unicorn"
3) "17"
```

`hincrby <key> <field> <increment>`为哈希表 key 中的域 field 的值加上增量 1  -1

`hsetnx <key> <field> <value>`将哈希表 key 中的域 field 的值设置为 value ，当且仅当域 field 不存在 

```perl
127.0.0.1:6379> hincrby user:1002 age 2
(integer) 19
127.0.0.1:6379> hsetnx user:1002 age 16
(integer) 0
127.0.0.1:6379> hsetnx user:1002 addr azur
(integer) 1
127.0.0.1:6379> hkeys user:1002
1) "id"
2) "name"
3) "age"
4) "addr"
127.0.0.1:6379> hvals user:1002
1) "2"
2) "unicorn"
3) "19"
4) "azur"
```

#### 3.5.3 数据结构

Hash类型对应的数据结构是两种：ziplist（压缩列表），hashtable（哈希表）。当field-value长度较短且个数较少时，使用ziplist，否则使用hashtable。

### 3.6 Redis有序集合Zset(sorted set) 

#### 3.6.1 简介

Redis有序集合zset与普通集合set非常相似，是一个==没有重复元素==的字符串集合。

不同之处是有序集合的每个成员都关联了一个==**评分（score）**==,这个评分（score）被用来按照从最低分到最高分的方式排序集合中的成员。==集合的成员是唯一的，但是评分可以是重复的== 。

因为元素是有序的, 所以你也可以很快的根据评分（score）或者次序（position）来获取一个范围的元素。

访问有序集合的中间元素也是非常快的,因此你能够使用有序集合作为一个没有重复成员的智能列表。

#### 3.6.2 常用命令

`zadd  <key> <score1> <value1> <score2> <value2>…`将一个或多个 member 元素及其 score 值加入到有序集 key 当中。

`zrange <key> <start> <stop>  [WITHSCORES]  ` 返回有序集 key 中，下标在<start><stop>之间的元素,带WITHSCORES，可以让分数一起和值返回到结果集。

```perl
127.0.0.1:6379> zadd topn 200 java 300 c++ 400 mysql 500 php
(integer) 4
127.0.0.1:6379> zrange topn 0 -1
1) "java"
2) "c++"
3) "mysql"
4) "php"
127.0.0.1:6379> zrange topn 0 -1 withscores
1) "java"
2) "200"
3) "c++"
4) "300"
5) "mysql"
6) "400"
7) "php"
8) "500"
```

`zrangebyscore key minmax [withscores] [limit offset count]`返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。 

`zrevrangebyscore key maxmin [withscores] [limit offset count]  `      同上，改为从大到小排列。

```perl
127.0.0.1:6379> zrangebyscore topn 300 500
1) "c++"
2) "mysql"
3) "php"
127.0.0.1:6379> zrangebyscore topn 300 500 withscores
1) "c++"
2) "300"
3) "mysql"
4) "400"
5) "php"
6) "500"
127.0.0.1:6379> zrevrange topn 500 200 withscores
(empty array)
127.0.0.1:6379> zrevrangebyscore topn 500 200 withscores
1) "php"
2) "500"
3) "mysql"
4) "400"
5) "c++"
6) "300"
7) "java"
8) "200"
```

`zincrby <key> <increment> <value>`    为元素的score加上增量

`zrem  <key> <value>`删除该集合下，指定值的元素 

```perl
127.0.0.1:6379> zincrby topn 50 java
"250"
127.0.0.1:6379> zrem topn java php
(integer) 2
127.0.0.1:6379> zrange topn 0 -1
1) "c++"
2) "mysql"
```

`zcount <key> <min> <max>`统计该集合，分数区间内的元素个数 

`zrank <key> <value>`返回该值在集合中的排名，从0开始。

```perl
127.0.0.1:6379> zcount topn 200 300
(integer) 2
127.0.0.1:6379> zrank topn java
(integer) 0
127.0.0.1:6379> zrange topn 0 -1
1) "java"
2) "c++"
3) "mysql"
4) "php"
127.0.0.1:6379> zrange topn 0 -1 withscores
1) "java"
2) "250"
3) "c++"
4) "300"
5) "mysql"
6) "400"
7) "php"
8) "500"
127.0.0.1:6379> zrank topn mysql
(integer) 2
```

#### 3.6.3 数据结构

SortedSet(zset)是Redis提供的一个非常特别的数据结构，一方面它等价于Java的数据结构Map<String, Double>，可以给每一个元素value赋予一个权重score，另一方面它又类似于TreeSet，内部的元素会按照权重score进行排序，可以得到每个元素的名次，还可以通过score的范围来获取元素的列表。

zset底层使用了两个数据结构

（1）hash，hash的作用就是关联元素value和权重score，保障元素value的唯一性，可以通过元素value找到相应的score值。

![image-20221101221030163](C:\Users\hakuou\Documents\program\Git\images\image-20221101221030163.png)

（2）跳跃表，跳跃表的目的在于给元素value排序，根据score的范围获取元素列表。

#### 3.6.4 跳跃表

![image-20221101221244639](C:\Users\hakuou\Documents\program\Git\images\image-20221101221244639.png)

对比有序链表和跳跃表，从链表中查询出51

（1） 有序链表

要查找值为51的元素，需要从第一个元素开始依次查找、比较才能找到。共需要6次比较。

（2） 跳跃表

从第2层开始，1节点比51节点小，向后比较。

21节点比51节点小，继续向后比较，后面就是NULL了，所以从21节点向下到第1层

在第1层，41节点比51节点小，继续向后，61节点比51节点大，所以从41向下

在第0层，51节点为要查找的节点，节点被找到，共查找4次。

## 4. Redis配置文件介绍

自定义配置文件：/etc/redis.conf

### 4.1 ###Units单位### 

配置大小单位,开头定义了一些基本的度量单位，只支持bytes，不支持bit，大小写不敏感

![image-20221102093615149](C:\Users\hakuou\Documents\program\Git\images\image-20221102093615149.png)

### 4.2 ###INCLUDES包含###

类似jsp中的include，多实例的情况可以把公用的配置文件提取出来

![image-20221102093642641](C:\Users\hakuou\Documents\program\Git\images\image-20221102093642641.png)

### 4.3 网络配置相关

#### 4.3.1 bind

默认情况bind=127.0.0.1只能接受本机的访问请求

不写的情况下，无限制接受任何ip地址的访问

生产环境肯定要写你应用服务器的地址；服务器是需要远程访问的，所以需要将其注释掉

==如果开启了protected-mode，那么在没有设定bind ip且没有设密码的情况下，Redis只允许接受本机的响应==

![image-20221102093836260](C:\Users\hakuou\Documents\program\Git\images\image-20221102093836260.png)

![image-20221102093935161](C:\Users\hakuou\Documents\program\Git\images\image-20221102093935161.png)

注释掉

![image-20221102094005419](C:\Users\hakuou\Documents\program\Git\images\image-20221102094005419.png)

重启redis服务

![image-20221102094302926](C:\Users\hakuou\Documents\program\Git\images\image-20221102094302926.png)

#### 4.3.2 protected-mode

将本机访问保护模式设置no

![image-20221102102140984](C:\Users\hakuou\Documents\program\Git\images\image-20221102102140984.png)

#### 4.3.3 port

端口号，默认6379

![image-20221102102224571](C:\Users\hakuou\Documents\program\Git\images\image-20221102102224571.png)

#### 4.3.4 tcp-backlog

设置tcp的backlog，backlog其实是一个连接队列，backlog队列总和=未完成三次握手队列 + 已经完成三次握手队列。

在高并发环境下你需要一个高backlog值来避免慢客户端连接问题。

注意Linux内核会将这个值减小到/proc/sys/net/core/somaxconn的值（128），所以需要确认增大/proc/sys/net/core/somaxconn和/proc/sys/net/ipv4/tcp_max_syn_backlog（128）两个值来达到想要的效果

#### 4.3.5 timeout

一个空闲的客户端维持多少秒会关闭，0表示关闭该功能。即==永不关闭==。

![image-20221102102400695](C:\Users\hakuou\Documents\program\Git\images\image-20221102102400695.png)

#### 4.3.6 tcp-keepalive

对访问客户端的一种==心跳检测==，每个n秒检测一次。

单位为秒，如果设置为0，则不会进行Keepalive检测，建议设置成60 

![image-20221102102452686](C:\Users\hakuou\Documents\program\Git\images\image-20221102102452686.png)

### 4.4 ###GENERAL通用###

#### 4.4.1 daemonize
是否为后台进程，设置为yes
守护进程，后台启动

![image-20221102102609315](C:\Users\hakuou\Documents\program\Git\images\image-20221102102609315.png)

#### 4.4.2 pidfile
存放pid文件的位置，每个实例会产生一个不同的pid文件

![image-20221102102650864](C:\Users\hakuou\Documents\program\Git\images\image-20221102102650864.png)

#### 4.4.3 loglevel 
指定日志记录级别，Redis总共支持四个级别：debug、verbose、notice、warning，默认为==notice==
<font color=green>四个级别根据使用阶段来选择，生产环境选择notice 或者warning</font>

![image-20221102102832657](C:\Users\hakuou\Documents\program\Git\images\image-20221102102832657.png)

#### 4.4.5 databases 16 
设定库的数量 默认16，默认数据库为0，可以使用SELECT <dbid>命令在连接上指定数据库id

![image-20221102102906028](C:\Users\hakuou\Documents\program\Git\images\image-20221102102906028.png)

### 4.5 ###SECURITY安全###

#### 设置密码

![image-20221102103335427](C:\Users\hakuou\Documents\program\Git\images\image-20221102103335427.png)

访问密码的查看、设置和取消

在命令中设置密码，只是临时的。重启redis服务器，密码就还原了。

永久设置，需要再配置文件中进行设置。

```perl
127.0.0.1:6379> config get requirepass
1) "requirepass"
2) ""
127.0.0.1:6379> config set requirepass 123456
OK
127.0.0.1:6379> config get requirepass
1) "requirepass"
2) "123456"
127.0.0.1:6379> auth 123456
OK
127.0.0.1:6379> exit
[root@Neptune redis-6.2.1]# myredis.sh stop
已杀死
[root@Neptune redis-6.2.1]# myredis.sh start
[root@Neptune redis-6.2.1]# redis-cli
127.0.0.1:6379> config get requirepass
1) "requirepass"
2) ""
127.0.0.1:6379>
```

### 4.6 #### LIMITS限制 ###
#### 4.6.1 maxclients
* 设置redis同时可以与多少个客户端进行连接。

* 默认情况下为10000个客户端。
* 如果达到了此限制，redis则会拒绝新的连接请求，并且向这些连接请求方发出“max number of clients reached”以作回应。

![image-20221102104300249](C:\Users\hakuou\Documents\program\Git\images\image-20221102104300249.png)

#### 4.6.2 maxmemory 
* 建议==必须设置==，否则，将内存占满，造成服务器宕机
* 设置redis可以使用的内存量。一旦到达内存使用上限，redis将会试图移除内部数据，移除规则可以通过==maxmemory-policy==来指定。
* 如果redis无法根据移除规则来移除内存中的数据，或者设置了“不允许移除”，那么redis则会针对那些需要申请内存的指令返回错误信息，比如SET、LPUSH等。
* 但是对于无内存申请的指令，仍然会正常响应，比如GET等。如果你的redis是主redis（说明你的redis有从redis），那么在设置内存使用上限时，需要在系统中留出一些内存空间给同步队列缓存，只有在你设置的是“不移除”的情况下，才不用考虑这个因素。

![image-20221103134556903](../images/image-20221103134556903.png)

#### 4.6.3 maxmemory-policy
* volatile-lru：使用LRU算法移除key，只对设置了过期时间的键；（最近最少使用）
* allkeys-lru：在所有集合key中，使用LRU算法移除key
* volatile-random：在过期集合中移除随机的key，只对设置了过期时间的键
* allkeys-random：在所有集合key中，移除随机的key
* volatile-ttl：移除那些TTL值最小的key，即那些最近要过期的key
* noeviction：不进行移除。针对写操作，只是返回错误信息

#### 4.6.4 maxmemory-samples
* 设置样本数量，LRU算法和最小TTL算法都并非是精确的算法，而是估算值，所以你可以设置样本的大小，redis默认会检查这么多个key并选择其中LRU的那个。
* 一般设置3到7的数字，数值越小样本越不准确，但性能消耗越小。

![image-20221103135003967](../images/image-20221103135003967.png)

## 5 Redis的发布与订阅

### 5.1 什么是发布和订阅

Redis 发布订阅 (pub/sub) 是一种消息通信模式：发送者 (pub) 发送消息，订阅者 (sub) 接收消息。

Redis 客户端可以订阅任意数量的频道。

### 5.2 Redis的发布和订阅

1、客户端可以订阅频道如下图

![image-20221103135412748](../images/image-20221103135412748.png)

2、当给这个频道发布消息后，消息就会发送给订阅的客户端

![image-20221103135427471](../images/image-20221103135427471.png)

### 5.3 发布订阅命令行实现

1、 打开一个客户端订阅channel1

`SUBSCRIBE channel1`

```perl
127.0.0.1:6379> SUBSCRIBE channel1
Reading messages... (press Ctrl-C to quit)
1) "subscribe"
2) "channel1"
3) (integer) 1
```

2、打开另一个客户端，给channel1发布消息hello

`publish channel1 hello`

```perl
[root@Neptune ~]# redis-cli
127.0.0.1:6379> publish channel1 hello
(integer) 1
127.0.0.1:6379> publish channel1 world
(integer) 1
127.0.0.1:6379>
```

返回的1是订阅者数量

3、打开第一个客户端可以看到发送的消息

```perl
127.0.0.1:6379> SUBSCRIBE channel1
Reading messages... (press Ctrl-C to quit)
1) "subscribe"
2) "channel1"
3) (integer) 1
1) "message"
2) "channel1"
3) "hello"
1) "message"
2) "channel1"
3) "world"
```

**注：发布的消息没有持久化，只能收到订阅后发布的消息**

## 6.Redis新数据类型
### 6.1 Bitmaps
#### 6.1.1 简介

现代计算机用二进制（位） 作为信息的基础单位， 1个字节等于8位， 例如“abc”字符串是由3个字节组成， 但实际在计算机存储时将其用二进制表示， “abc”分别对应的ASCII码分别是97、 98、 99， 对应的二进制分别是01100001、 01100010和01100011，如下图

 ![image-20221103142337231](../images/image-20221103142337231.png)

合理地使用操作位能够有效地提高内存使用率和开发效率。

​	Redis提供了Bitmaps这个“数据类型”可以实现对==位==的操作：

（1） Bitmaps本身不是一种数据类型， 实际上它就是字符串（key-value） ， 但是它可以对字符串的位进行操作。

（2） Bitmaps单独提供了一套命令， 所以在Redis中使用Bitmaps和使用字符串的方法不太相同。 可以把Bitmaps想象成一个以位为单位的数组， 数组的每个单元只能存储0和1， 数组的下标在Bitmaps中叫做偏移量。

![image-20221103142345780](../images/image-20221103142345780.png)

#### 6.1.2 命令

1、setbit

（1）格式

`setbit <key> <offset> <value>`设置Bitmaps中某个偏移量的值（0或1）

 ```perl
127.0.0.1:6379> setbit k1 0 1
(integer) 0
127.0.0.1:6379> setbit k1 1 0
(integer) 0
 ```

==offset:偏移量从0开始==

（2）实例

每个独立用户是否访问过网站存放在Bitmaps中， 将访问的用户记做1， 没有访问的用户记做0， 用偏移量作为用户的id。

设置键的第offset个位的值（从0算起） ， 假设现在有20个用户，userid=1， 6， 11， 15， 19的用户对网站进行了访问， 那么当前Bitmaps初始化结果如图

![image-20221103145954587](../images/image-20221103145954587.png)

unique:users:20201106代表2020-11-06这天的独立访问用户的Bitmaps

```perl
127.0.0.1:6379> setbit unique:users:20201106 1 1
(integer) 0
127.0.0.1:6379> setbit unique:users:20201106 2 1
(integer) 0
127.0.0.1:6379> setbit unique:users:20201106 3 0
(integer) 0
127.0.0.1:6379> setbit unique:users:20201106 11 1
(integer) 0
127.0.0.1:6379> setbit unique:users:20201106 19 1
(integer) 0
```

==注：==

很多应用的用户id以一个指定数字（例如10000） 开头， 直接将用户id和Bitmaps的偏移量对应势必会造成一定的浪费， 通常的做法是每次做setbit操作时将用户id减去这个指定数字。

在第一次初始化Bitmaps时， 假如偏移量非常大， 那么整个初始化过程执行会比较慢， 可能会造成Redis的阻塞。

2、getbit

（1）格式

`getbit <key> <offset>`获取Bitmaps中某个偏移量的值

```perl
127.0.0.1:6379> getbit k1 0
(integer) 1
```

（2）实例

获取id=3的用户是否在2020-11-06这天访问过， 返回0说明没有访问过：

```perl
127.0.0.1:6379> getbit unique:users:20201106 3
(integer) 0
127.0.0.1:6379> getbit unique:users:20201106 100
(integer) 0
```

**注：因为100根本不存在，所以也是返回0**

3、`bitcount`

统计字符串被设置为1的bit数。一般情况下，给定的整个字符串都会被进行计数，通过指定额外的 start 或 end 参数，可以让计数只在特定的位上进行。start 和 end 参数的设置，都可以使用负数值：比如 -1 表示最后一个位，而 -2 表示倒数第二个位，start、end 是指bit组的字节的下标数，二者皆包含。

（1）格式

`bitcount <key> [start end] `统计字符串从start字节到end字节比特值为1的数量

（2）实例

==举例： K1 【01000001 01000000  00000000 00100001】==，对应【0，1，2，3】
`bitcount K1 1 2`  ： 统计下标1、2字节组中bit=1的个数，

即`01000000  00000000` => `bitcount K1 1 2` 　=> 1

`bitcount K1 1 3`  ： 统计下标1、2字节组中bit=1的个数，

即`01000000  00000000 00100001`=> `bitcount K1 1 3`　=> 3

`bitcount K1 0 -2`  ： 统计下标0到下标倒数第2，字节组中bit=1的个数，

即`01000001  01000000   00000000`=> `bitcount K1 0 -2`   => 3

注意：==redis的setbit设置或清除的是**bit**位置，而bitcount计算的是**byte**位置==

4、`bittop`

(1)格式

`bitop and(or/not/xor) <destkey> [key…]`

![img](file:///C:\Users\hakuou\AppData\Local\Temp\ksohtml2884\wps4.jpg) 

bitop是一个复合操作， 它可以做多个Bitmaps的**and（交集） 、 or（并集） 、 not（非） 、 xor（异或**） 操作并将结果保存在destkey中。

(2)实例

```perl
2020-11-04 日访问网站的userid=1,2,5,9。
setbit unique:users:20201104 1 1
setbit unique:users:20201104 2 1
setbit unique:users:20201104 5 1
setbit unique:users:20201104 9 1

2020-11-03 日访问网站的userid=0,1,4,9。
setbit unique:users:20201103 0 1
setbit unique:users:20201103 1 1
setbit unique:users:20201103 4 1
setbit unique:users:20201103 9 1
 
 计算出两天都访问过网站的用户数量
bitop and unique:users:and:20201104_03 unique:users:20201103 unique:users:20201104
bitcount unique:users:and:20201104_03  

计算出任意一天都访问过网站的用户数量（例如月活跃就是类似这种） ， 可以使用or求并集
bitop or unique:users:and:20201104_03 unique:users:20201103 unique:users:20201104
bitcount unique:users:and:20201104_03
```

#### 6.1.3 Bitmaps与set对比

假设网站有1亿用户， 每天独立访问的用户有5千万， 如果每天用集合类型和Bitmaps分别存储活跃用户可以得到表

| set和Bitmaps存储一天活跃用户对比 |                    |                  |                        |
| -------------------------------- | ------------------ | ---------------- | ---------------------- |
| 数据类型                         | 每个用户id占用空间 | 需要存储的用户量 | 全部内存量             |
| 集合类型                         | 64位               | 50000000         | 64位*50000000 = 400MB  |
| Bitmaps                          | 1位                | 100000000        | 1位*100000000 = 12.5MB |

很明显， 这种情况下使用Bitmaps能节省很多的内存空间， 尤其是随着时间推移节省的内存还是非常可观的

| set和Bitmaps存储独立用户空间对比 |        |        |       |
| -------------------------------- | ------ | ------ | ----- |
| 数据类型                         | 一天   | 一个月 | 一年  |
| 集合类型                         | 400MB  | 12GB   | 144GB |
| Bitmaps                          | 12.5MB | 375MB  | 4.5GB |

但Bitmaps并不是万金油， 假如该网站每天的独立访问用户很少， 例如只有10万（大量的僵尸用户） ， 那么两者的对比如下表所示， 很显然， 这时候使用Bitmaps就不太合适了， 因为基本上大部分位都是0。

| set和Bitmaps存储一天活跃用户对比（独立用户比较少） |                    |                  |                        |
| -------------------------------------------------- | ------------------ | ---------------- | ---------------------- |
| 数据类型                                           | 每个userid占用空间 | 需要存储的用户量 | 全部内存量             |
| 集合类型                                           | 64位               | 100000           | 64位*100000 = 800KB    |
| Bitmaps                                            | 1位                | 100000000        | 1位*100000000 = 12.5MB |

### 6.2 HyperLogLog

#### 6.2.1 简介

在 Redis 里面，每个 HyperLogLog 键只需要花费 12 KB 内存，就可以计算接近 2^64 个不同元素的基数。这和计算基数时，元素越多耗费内存就越多的集合形成鲜明对比。

但是，因为 HyperLogLog 只会根据输入元素来计算基数，而不会储存输入元素本身，所以 HyperLogLog 不能像集合那样，返回输入的各个元素。

什么是基数?

比如数据集 {1, 3, 5, 7, 5, 7, 8}， 那么这个数据集的基数集为 {1, 3, 5 ,7, 8}, 基数(不重复元素)为5。 基数估计就是在误差可接受的范围内，快速计算基数。

**占用内存更小的set，无法返回值，只能返回size**

#### 6.2.2 命令

1、pfadd 

`pfadd <key> < element> [element ...] ` 添加指定元素到 HyperLogLog 中  

将所有元素添加到指定HyperLogLog数据结构中。如果执行命令后HLL估计的近似基数发生变化，则返回1，否则返回0。

2、pfcount

`pfcount <key> [key ...]` 计算HLL的近似基数，可以计算多个HLL，比如用HLL存储每天的UV，计算一周的UV可以使用7天的UV合并计算即可  

3、pfmerge

`pfmerge <destkey> <sourcekey> [sourcekey ...] ` 将一个或多个HLL合并后的结果存储在另一个HLL中，比如每月活跃用户可以使用每天的活跃用户来合并计算可得 

### 6.3 Geospatial 

#### 6.3.1 简介

Redis 3.2 中增加了对GEO类型的支持。GEO，Geographic，地理信息的缩写。该类型，就是元素的2维坐标，在地图上就是经纬度。redis基于该类型，提供了经纬度设置，查询，范围查询，距离查询，经纬度Hash等常见操作。

#### 6.3.2 命令

1、geoadd

`geoadd <key> < longitude> <latitude> <member> [longitude latitude member...]`  添加地理位置（经度，纬度，名称） 

（2）实例

```perl
geoadd china:city 121.47 31.23 shanghai
geoadd china:city 106.50 29.53 chongqing 114.05 22.52 shenzhen 116.38 39.90 beijing 
```

两极无法直接添加，一般会下载城市数据，直接通过 Java 程序一次性导入。

有效的经度从 -180 度到 180 度。有效的纬度从 -85.05112878 度到 85.05112878 度。

当坐标位置超出指定范围时，该命令将会返回一个错误。

已经添加的数据，是无法再次往里面添加的。

2、geopos  

（1）格式

`geopos  <key> <member> [member...] ` 获得指定地区的坐标值 

（2）实例

 ```perl
geopos china:city shanghai
 ```

3、geodist

（1）格式

`geodist <key> <member1> <member2>  [m|km|ft|mi ] ` 获取两个位置之间的直线距离

（2）实例

获取两个位置之间的直线距离

```perl
geodist china:city beijing shanghai km 
```

单位：

m 表示单位为米[默认值]。

km 表示单位为千米。

mi 表示单位为英里。

ft 表示单位为英尺。

如果用户没有显式地指定单位参数， 那么 GEODIST 默认使用米作为单位

4、georadius

（1）格式

`georadius <key> < longitude> <latitude> radius m|km|ft|mi ` 以给定的经纬度为中心，找出某一半径内的元素

经度 纬度 距离 单位

（2）实例

```perl
georadius china:city 110 30 1000 km 
```

## 7. Redis_Jedis_测试

### 7.1 Jedis所需要的jar包
```xml
<dependency>
<groupId>redis.clients</groupId>
<artifactId>jedis</artifactId>
<version>3.2.0</version>
</dependency>
```
### 7.2 连接Redis注意事项
禁用Linux的防火墙：Linux(CentOS7)里执行命令
systemctl stop/disable firewalld.service   
redis.conf中注释掉bind 127.0.0.1 ,然后 protected-mode no

### 7.3 Jedis常用操作
#### 7.3.1 创建测试程序
```java
import redis.clients.jedis.Jedis;
public class Demo01 {
public static void main(String[] args) {
Jedis jedis = new Jedis("192.168.137.3",6379);
String pong = jedis.ping();
System.out.println("连接成功："+pong);
jedis.close();
}
}
```
### 7.4 测试相关数据类型
#### 7.4.1 Jedis-API:    Key
```java
jedis.set("k1", "v1");
jedis.set("k2", "v2");
jedis.set("k3", "v3");
Set<String> keys = jedis.keys("*");
System.out.println(keys.size());
for (String key : keys) {
System.out.println(key);
}
System.out.println(jedis.exists("k1"));
System.out.println(jedis.ttl("k1"));                
System.out.println(jedis.get("k1"));
```
#### 7.4.2 Jedis-API:    String

```java
jedis.mset("str1","v1","str2","v2","str3","v3");
System.out.println(jedis.mget("str1","str2","str3"));
```
#### 7.4.3 Jedis-API:    List
```java
List<String> list = jedis.lrange("mylist",0,-1);
for (String element : list) {
System.out.println(element);
}
```
#### 7.4.4 Jedis-API:    set
```java
jedis.sadd("orders", "order01");
jedis.sadd("orders", "order02");
jedis.sadd("orders", "order03");
jedis.sadd("orders", "order04");
Set<String> smembers = jedis.smembers("orders");
for (String order : smembers) {
System.out.println(order);
}
jedis.srem("orders", "order02");
```
#### 7.4.5 Jedis-API:    hash
```java
jedis.hset("hash1","userName","lisi");
System.out.println(jedis.hget("hash1","userName"));
Map<String,String> map = new HashMap<String,String>();
map.put("telphone","13810169999");
map.put("address","atguigu");
map.put("email","abc@163.com");
jedis.hmset("hash2",map);
List<String> result = jedis.hmget("hash2", "telphone","email");
for (String element : result) {
System.out.println(element);
}
```
#### 7.4.6 Jedis-API:    zset
```java
jedis.zadd("zset01", 100d, "z3");
jedis.zadd("zset01", 90d, "l4");
jedis.zadd("zset01", 80d, "w5");
jedis.zadd("zset01", 70d, "z6");
 
Set<String> zrange = jedis.zrange("zset01", 0, -1);
for (String e : zrange) {
System.out.println(e);
}
```

## 8 Redis_Jedis_实例
### 8.1 完成一个手机验证码功能
要求：
1、输入手机号，点击发送后随机生成6位数字码，2分钟有效
2、输入验证码，点击验证，返回成功或失败
3、每个手机号每天只能输入3次

### 8.2 需求分析

![image-20221105163951207](../images/image-20221105163951207.png)

### 8.3 实现代码

```java
package database;

import redis.clients.jedis.Jedis;

import java.util.Random;

public class PhoneCode {
    public static void main(String[] args) {
        //模拟验证码发送
        //verifyCode("13789418196");

        getRedisCode("13789418196","520939");
    }

    //1. 生成6位数字密码
    public static final String getCode() {
        Random random = new Random();
        String code = "";
        for (int i = 0; i < 6; i++) {
            code += random.nextInt(10);
        }
        return code;
    }

    //2. 每个手机每天只能发送三次，验证码放在redis中，设置过期时间
    public static final void verifyCode(String phone) {
        //连接redis
        Jedis jedis = new Jedis("192.168.10.130", 6379);

        //拼接key
        //手机发送次数key
        String countKey = "VerifyCode" + phone + ":count";
        //验证码Key
        String codeKey = "VerifyCode" + phone + ":code";

        //每个手机每天只能发送三次
        String count = jedis.get(countKey);
        if (count == null) {
            //没有发送，第一次发送
            //设置发送次数是1
            jedis.setex(countKey, 24 * 60 * 60, "1");
        } else if (Integer.parseInt(count) < 3) {
            //发送次数+1
            jedis.incr(countKey);
        } else {
            //发送三次，不能再发送
            System.out.println("发送次数超过三次");
            jedis.close();
            return;
        }

        //发送验证码到redis
        jedis.setex(codeKey,120,getCode());
        jedis.close();

    }

    //3. 验证码校验
    public static final void getRedisCode(String phone, String code) {
        //连接redis
        Jedis jedis = new Jedis("192.168.10.130", 6379);

        //验证码Key
        String codeKey = "VerifyCode" + phone + ":code";

        String redisCode = jedis.get(codeKey);

        //判断
        if (redisCode.equals(code)) {
            System.out.println("成功");
        } else {
            System.out.println("失败");
        }

        jedis.close();
    }
}

```

## 9 Redis与Spring Boot整合
Spring Boot(2.7.5)整合Redis非常简单，只需要按如下步骤整合即可
### 9.1 整合步骤
1、在pom.xml文件中引入redis相关依赖
```xml
        <!--redis起步依赖： 直接在项目中使用RedisTemplate(StringRedisTemplate)-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
```
2、application.properties配置redis配置
```properties
#Redis服务器地址
spring.redis.host=192.168.140.136
#Redis服务器连接端口
spring.redis.port=6379
#Redis数据库索引（默认为0）
spring.redis.database= 0
#连接超时时间（毫秒）
spring.redis.timeout=1800000
#连接池最大连接数（使用负值表示没有限制）
spring.redis.lettuce.pool.max-active=20
#最大阻塞等待时间(负数表示没限制)
spring.redis.lettuce.pool.max-wait=-1
#连接池中的最大空闲连接
spring.redis.lettuce.pool.max-idle=5
#连接池中的最小空闲连接
spring.redis.lettuce.pool.min-idle=0
```
3、测试一下
RedisTestController中添加测试方法

```java
@RestController
@RequestMapping("/redisTest")
public class RedisTestController {
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping
    public String testRedis() {
        //设置值到redis
        redisTemplate.opsForValue().set("name","lucy");
        //从redis获取值
        String name = (String)redisTemplate.opsForValue().get("name");
        return name;
    }
}
```