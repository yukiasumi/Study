



# ClickHouse入门
# 1 **ClickHouse** 简介

ClickHouse 是俄罗斯的 Yandex 于 2016 年开源的列式存储数据库（DBMS），使用 C++语言编写，主要用于在线分析处理查询（OLAP），能够使用 SQL 查询实时生成分析数据报告。

## **1.1 ClickHouse** 的特点

### **1.1.1** **列式存储**

以下表为例

| Id   | Name | Age  |
| ---- | ---- | ---- |
| 1    | 张三 | 18   |
| 2    | 李四 | 22   |
| 3    | 王五 | 34   |

* **采用行式存储时，数据在磁盘上的组织结构为：**

![image-20221204132133360](../../images/image-20221204132133360.png)

* **采用列式存储时，数据在磁盘上的组织结构为：**

![image-20221204132147494](../../images/image-20221204132147494.png)

* **列式储存的好处：**
  * 对于列的聚合，计数，求和等统计操作远优于行式存储。
  * 由于某一列的数据类型都是相同的，针对于数据存储更容易进行数据压缩，每一列选择更优的数据压缩算法，大大提高了数据的压缩比重。
  * 由于数据压缩比更好，一方面节省了磁盘空间，另一方面对于 cache 也有了更大的发挥空间。

### **1.1.2 DBMS** **的功能**

几乎覆盖了标准 SQL 的大部分语法，包括 DDL 和 DML，以及配套的各种函数，用户管理及权限管理，数据的备份与恢复。

### **1.1.3** **多样化引擎**

ClickHouse 和 MySQL 类似，把表级的存储引擎插件化，根据表的不同需求可以设定不同的存储引擎。目前包括合并树、日志、接口和其他四大类 20 多种引擎。

### **1.1.4** **高吞吐写入能力**

* ClickHouse 采用类 ==LSM Tree==的结构，数据写入后定期在后台 Compaction（压缩）。
* 通过类 LSM tree的结构，ClickHouse 在数据导入时全部是顺序 append 写，写入后数据段不可更改，在后台compaction 时也是多个段 merge sort 后顺序写回磁盘。顺序写的特性，充分利用了磁盘的吞吐能力，即便在 HDD 上也有着优异的写入性能。

* 官方公开 benchmark 测试显示能够达到 50MB-200MB/s 的写入吞吐能力，按照每行100Byte 估算，大约相当于 50W-200W 条/s 的写入速度。

### **1.1.5** **数据分区与线程级并行**

* ClickHouse 将数据划分为多个 partition，每个 partition 再进一步划分为多个 index granularity(索引粒度)，然后通过多个 CPU核心分别处理其中的一部分来实现并行数据处理。在这种设计下，单条 Query 就能利用整机所有 CPU。极致的并行处理能力，极大的降低了查询延时。
* ClickHouse 即使对于大量数据的查询也能够化整为零平行处理。弊端是对于单条查询使用多 cpu，不利于同时并发多条查询。所以对于高 qps 的查询业务，ClickHouse 并不是强项。

### **1.1.6** **性能对比**

* **单表查询**

![image-20221204132740972](../../images/image-20221204132740972.png)

* **关联查询**

![image-20221204132803364](../../images/image-20221204132803364.png)

* **结论:** 

ClickHouse 像很多 OLAP 数据库一样，单表查询速度优于关联查询，而且 ClickHouse的两者差距更为明显。

# 2 **ClickHouse** **的安装**

## **2.1** **准备工作**

### **2.1.1** **确定防火墙处于关闭状态**

```perl
#查看防火状态
systemctl status firewalld
service  iptables status

#暂时关闭防火墙
systemctl stop firewalld
service  iptables stop

#永久关闭防火墙
systemctl disable firewalld
chkconfig iptables off

#重启防火墙
systemctl enable firewalld
service iptables restart  

#永久关闭后重启
chkconfig iptables on　
```

### **2.1.2 CentOS** **取消打开文件数限制**

* 在 hadoop102 的 /etc/security/limits.conf 文件的末尾加入以下内容

```perl
sudo vim /etc/security/limits.conf
* soft nofile 65536
* hard nofile 65536
* soft nproc 131072
* hard nproc 131072
```

* 在 hadoop102 的/etc/security/limits.d/20-nproc.conf 文件的末尾加入以下内容

```perl
sudo vim /etc/security/limits.d/20-nproc.conf 
* soft nofile 65536
* hard nofile 65536
* soft nproc 131072
* hard nproc 131072
```

* 执行同步操作

```perl
sudo /home/atguigu/bin/xsync /etc/security/limits.conf
sudo /home/atguigu/bin/xsync /etc/security/limits.d/20-nproc.conf
```

### **2.1.3** **安装依赖**

在hadoop102、hadoop103、hadoop104上执行

```perl
sudo yum install -y libtool 
sudo yum install -y *unixODBC*
```

### **2.1.4 CentOS** **取消** **SELINUX**

* 在hadoop102修改/etc/selinux/config 中的 SELINUX=disabled

```perl
sudo vim /etc/selinux/config
SELINUX=disabled
```

* 执行同步操作

```perl
sudo /home/atguigu/bin/xsync /etc/selinux/config
```

* ==重启三台服务器==

```perl
sudo reboot
```

## **2.2** **单机安装**

官网：https://clickhouse.tech/

下载地址：http://repo.red-soft.biz/repos/clickhouse/stable/el7/

* 在/opt/software 下创建 clickhouse 目录

```perl
 mkdir /opt/software/clickhouse
```

*  将4个rpm 安装包上传到/opt/oftware/clickhouse 目录下

![image-20221204141442359](../../images/image-20221204141442359.png)

* 同步安装包

```perl
xsync /opt/software/clickhouse
```

* 在三台服务器上安装

```perl
sudo rpm -ivh /opt/software/clickhouse/*.rpm

#设置密码时直接按回车可无密码登录
默认用户default
```

* 查看安装情况

```perl
sudo rpm -qa|grep clickhouse
```

* 卸载

```perl
#重复执行
sudo rpm -qa|grep clickhouse|xargs -i sudo rpm -e {}

sudo rm -rf /var/lib/clickhouse
sudo rm -rf /etc/clickhouse-*
sudo rm -rf /var/log/clickhouse-server
sudo userdel clickhouse
sudo userdel clickhouse-bridge
```

* 在hadoop102上修改配置文件

```perl
 sudo vim /etc/clickhouse-server/config.xml
 
 把 <listen_host>::</listen_host> 的注释打开，这样的话才能让 ClickHouse 被除本机以外的服务器访问
 
在这个文件中，有 ClickHouse 的一些默认路径配置，比较重要的
数据文件路径：<path>/var/lib/clickhouse/</path>
日志文件路径：<log>/var/log/clickhouse-server/clickhouse-server.log</log>
```

* 分发配置文件

```perl
sudo /home/atguigu/bin/xsync /etc/clickhouse-server/config.xml
```

* 启动server

```perl
 sudo systemctl start clickhouse-server
```

* 三台机器上关闭开机自启

```perl
sudo systemctl disable clickhouse-server
```

* 使用 client 连接 server

`-m` :可以在命令窗口输入多行命令

```perl
  clickhouse-client -m
  
  Ctrl +d 退出
```

# 3 数据类型

## **3.1** **整型**

固定长度的整型，包括有符号整型或无符号整型。

整型范围（-2^n-1^~2^n-1^-1）：

Int8 - [-128 : 127]

Int16 - [-32768 : 32767]

Int32 - [-2147483648 : 2147483647]

Int64 - [-9223372036854775808 : 9223372036854775807]

无符号整型范围（0~2^n^-1）：

UInt8 - [0 : 255]

UInt16 - [0 : 65535]

UInt32 - [0 : 4294967295]

UInt64 - [0 : 18446744073709551615]

==使用场景： 个数、数量、也可以存储型id==

## **3.2** **浮点型**

Float32 - float

Float64 – double

建议尽可能以整数形式存储数据。例如，将固定精度的数字转换为整数值，如时间用毫秒为单位表示，因为浮点型进行计算时可能引起四舍五入的误差。

```powershell
Neptune :) select 1.0-0.9;

SELECT 1. - 0.9

Query id: 009367a5-fd6b-4f54-b5b6-20c889e86486

┌──────minus(1., 0.9)─┐
│ 0.09999999999999998 │
└─────────────────────┘

1 rows in set. Elapsed: 0.004 sec.
```

==使用场景：一般数据值比较小，不涉及大量的统计计算，精度要求不高的时候。比如保存商品的重量。==

## **3.3** **布尔型**

没有单独的类型来存储布尔值。可以使用 UInt8 类型，取值限制为 0 或 1。

## **3.4 Decimal** **型**

有符号的浮点数，可在加、减和乘法运算过程中保持精度。对于除法，最低有效数字会被丢弃（不舍入）。

有三种声明：

* Decimal32(s)，相当于 Decimal(9-s,s)，有效位数为 1~9

* Decimal64(s)，相当于 Decimal(18-s,s)，有效位数为 1~18

* Decimal128(s)，相当于 Decimal(38-s,s)，有效位数为 1~38

==s 标识小数位==

==使用场景： 一般金额字段、汇率、利率等字段为了保证小数点精度，都使用 Decimal进行存储。==

## **3.5** **字符串**

* **String**

字符串可以任意长度的。它可以包含任意的字节集，包含空字节。

* **FixedString(N)**

固定长度 N 的字符串，N 必须是严格的正自然数。当服务端读取长度小于 N 的字符串时候，通过在字符串末尾==添加空字节==来达到 N 字节长度。 当服务端读取长度大于 N 的字符串时候，将返回错误消息。

**与 String 相比，极少会使用 FixedString，因为使用起来不是很方便。**

==使用场景：名称、文字描述、字符型编码。 固定长度的可以保存一些定长的内容，比如一些编码，性别等但是考虑到一定的变化风险，带来收益不够明显，所以定长字符串使用意义有限。==

## 3.6 **枚举类型**

包括 Enum8 和 Enum16 类型。Enum 保存 'string'= integer 的对应关系。

Enum8 用 'String'= Int8 对描述。

Enum16 用 'String'= Int16 对描述。

* 创建一个带有一个枚举 Enum8('hello' = 1, 'world' = 2) 类型的列

```sql
CREATE TABLE t_enum(
 x Enum8('hello' = 1, 'world' = 2)
)ENGINE = TinyLog;
```

* 这个 x 列只能存储类型定义中列出的值：'hello'或'world'

```sql
INSERT INTO t_enum VALUES ('hello'), ('world'), ('hello');
```

* 如果尝试保存任何其他值，ClickHouse 抛出异常

```sql
insert into t_enum values('a')
```

![image-20221205195137328](../../images/image-20221205195137328.png)

* 如果需要看到对应行的数值，则必须将 Enum 值转换为整数类型

```sql
SELECT CAST(x, 'Int8') FROM t_enum;
```

![image-20221205195310433](../../images/image-20221205195310433.png)

==使用场景：对一些状态、类型的字段算是一种空间优化，也算是一种数据约束。但是实际使用中往往因为一些数据内容的变化增加一定的维护成本，甚至是数据丢失问题。所以谨慎使用。==

## **3.7** **时间类型**

```sql
获取当前时间戳：select toUnixTimestamp(now())
获取指定时间的时间戳：select toUnixTimestamp('2021-05-11 14:37:37')
获取当前日期时间：select now()
获取当前日期：select today()
获取昨日日期：select yesterday()

日期时间转为整形：toYYYYMMDDhhmmss()
将字符串型的日期转为时间类型：parseDateTimeBestEffort()

时间格式化
SELECT 
    now() AS dt,
    toYYYYMMDDhhmmss(dt) AS dt_int,
    toString(dt) AS dt_str,
    parseDateTimeBestEffort(toString(dt_int)) AS datetime

日期时间转日期：select toDate('2021-05-11 14:31:31')
日期转时间戳：select toUnixTimestamp(toDateTime('2021-05-01'))
时间戳转日期时间：select toDateTime(1620714857)

获取指定时间的年份：select toYear(toDateTime('2021-05-11 10:10:10'))
获取当前日期的年份：select toYear(now())
获取指定时间的月份：select toMonth(toDateTime('2021-05-11 10:10:10'))
获取当前日期的月份：select toMonth(now())
获取指定时间的季度：select toQuarter(toDateTime('2021-05-11 10:10:10'))
获取当前年份中的第几天：select toDayOfYear(toDateTime('2021-05-11 10:10:10'))
获取当前月份的第几天：select toDayOfMonth(toDateTime('2021-05-11 10:10:10'))
获取星期几：select toDayOfWeek(toDateTime('2021-05-11 10:10:10'))
获取指定时间的小时：select toHour(toDateTime('2021-05-11 10:10:10'))
获取指定时间的分钟：select toMinute(toDateTime('2021-05-11 10:10:10'))
获取指定时间的秒数：select toSecond(toDateTime('2021-05-11 10:10:10'))

获取当前年份的第一天：select toStartOfYear(toDateTime('2021-05-11 10:10:10'))
获取当前月份的第一天：select toStartOfMonth(toDateTime('2021-05-11 10:10:10'))
获取当前季度的第一天：select toStartOfQuarter(toDateTime('2021-05-11 10:10:10'))
获取当前周的第一天：select toMonday(toDateTime('2021-05-11 10:10:10'))
获取当前时间的起始时间：select toStartOfDay(toDateTime('2021-05-11 10:10:10'))
获取当前时间的起始小时时间：select toStartOfHour(toDateTime('2021-05-11 10:10:10'))
获取当前时间的起始分钟时间：select toStartOfMinute(toDateTime('2021-05-11 10:10:10'))
获取当前时间的起始秒数时间：select toStartOfSecond(toDateTime('2021-05-11 10:10:10'))

时间增加一年：select addYears(toDateTime('2021-05-11 10:10:10'),1)
时间增加一月：select addMonths(toDateTime('2021-05-11 10:10:10'),1)
时间增加一周：select addWeeks(toDateTime('2021-05-11 10:10:10'),1)
时间增加一天：select addDays(toDateTime('2021-05-11 10:10:10'),1)
时间增加一小时：select addHours(toDateTime('2021-05-11 10:10:10'),1)
时间增加一分钟：select addMinutes(toDateTime('2021-05-11 10:10:10'),1)
时间增加一秒钟：select addSeconds(toDateTime('2021-05-11 10:10:10'),1)
时间增加一季度：select addQuarters(toDateTime('2021-05-11 10:10:10'),1)

增加用add,减去用subtract,例如时间减去一年：select subtractYears(toDateTime('2021-05-11 10:10:10'),1)

计算时间差值：dateDiff()
SELECT
    dateDiff('year', toDateTime('2021-05-11 10:10:10'), toDateTime('2021-05-11 18:04:44')) as diff_years,
    dateDiff('month', toDateTime('2021-05-11 10:10:10'), toDateTime('2021-05-11 18:04:44')) as diff_months,
    dateDiff('week', toDateTime('2021-05-11 10:10:10'), toDateTime('2021-05-11 18:04:44')) as diff_week,
    dateDiff('day', toDateTime('2021-05-11 10:10:10'), toDateTime('2021-05-11 18:04:44')) as diff_days,
    dateDiff('hour', toDateTime('2021-05-11 10:10:10'), toDateTime('2021-05-11 18:04:44')) as diff_hours,
    dateDiff('minute', toDateTime('2021-05-11 10:10:10'), toDateTime('2021-05-11 18:04:44')) as diff_minutes,
    dateDiff('second', toDateTime('2021-05-11 10:10:10'), toDateTime('2021-05-11 18:04:44')) as diff_seconds

```

目前 ClickHouse 有三种时间类型：

* Date 接受年-月-日的字符串比如 ‘2019-12-16’
* Datetime 接受年-月-日 时:分:秒的字符串比如 ‘2019-12-16 20:50:10’
* Datetime64 接受年-月-日 时:分:秒.亚秒的字符串比如‘2019-12-16 20:50:10.66’

日期类型，用两个字节存储，表示从 1970-01-01 (无符号) 到当前的日期值。

还有很多数据结构，可以参考官方文档：https://clickhouse.yandex/docs/zh/data_types/

## **3.8** **数组**

**Array(T)**：由 T 类型元素组成的数组。

T 可以是任意类型，包含数组类型。 但不推荐使用多维数组，ClickHouse 对多维数组的支持有限。例如，不能在 MergeTree 表中存储多维数组。

创建数组有两种方式：

* 使用 array 函数

```sql
SELECT array(1, 2) AS x, toTypeName(x) ;
```

![image-20221205200759964](../../images/image-20221205200759964.png)

* 使用方括号

```sql
SELECT [1, 2] AS x, toTypeName(x);
```

![image-20221205200847602](../../images/image-20221205200847602.png)

# 4 **表引擎**

表引擎是 ClickHouse 的一大特色。表引擎决定了如何存储表的数据。包括：

* 数据的存储方式和位置，写到哪里以及从哪里读取数据。
* 支持哪些查询以及如何支持。
* 并发数据访问。
* 索引的使用（如果存在）。
* 是否可以执行多线程请求。
* 数据复制参数。

表引擎的使用方式就是必须显式在创建表时定义该表使用的引擎，以及引擎使用的相关参数。

==特别注意：引擎的名称大小写敏感==

## **4.1 TinyLog**

* 以列文件的形式保存在磁盘上，不支持索引，没有并发控制。

* 一般保存少量数据的小表，生产环境上作用有限。可以用于平时练习测试用。

```sql
create table t_tinylog ( id String, name String) engine=TinyLog;
```

## 4.2 **Memory**

* 内存引擎，数据以未压缩的原始形式直接保存在内存当中，服务器重启数据就会消失。

* 读写操作不会相互阻塞，不支持索引。简单查询下有非常非常高的性能表现（==超过 10G/s==）。

* 一般用到它的地方不多，除了用来测试，就是在需要非常高的性能，同时数据量又不太大（上限大概 1 亿行）的场景。

## 4.3 **MergeTree**

ClickHouse 中==最强大的表引擎==当属 MergeTree（合并树）引擎及该系列（*MergeTree）中的其他引擎，支持索引和分区，地位可以相当于 innodb 之于 Mysql。

* **建表语句**

```sql
create table t_order_mt(
 id UInt32,
 sku_id String,
 total_amount Decimal(16,2),
 create_time Datetime
) engine =MergeTree
 partition by toYYYYMMDD(create_time)
 primary key (id)
 order by (id,sku_id);
```

* **插入数据**

```sql
insert into t_order_mt values
(101,'sku_001',1000.00,'2020-06-01 12:00:00') ,
(102,'sku_002',2000.00,'2020-06-01 11:00:00'),
(102,'sku_004',2500.00,'2020-06-01 12:00:00'),
(102,'sku_002',2000.00,'2020-06-01 13:00:00'),
(102,'sku_002',12000.00,'2020-06-01 13:00:00'),
(102,'sku_002',600.00,'2020-06-02 12:00:00');
```

MergeTree 有很多参数(绝大多数用默认值即可)，较为重要的是`partition by`， `primary key`，`order by`

### 4.3.1  partition by 分区(可选)

* **作用：**
降低扫描的范围，优化查询速度

* **如果不填：** 
只会使用一个分区。

* **分区目录：** 
MergeTree 是以列文件+索引文件+表定义文件组成的，但是如果设定了分区那么这些文件就会保存到不同的分区目录中。

* **并行：** 
分区后，面对涉及跨分区的查询统计，ClickHouse 会以分区为单位并行处理。

* **数据写入与分区合并：** 
任何一个批次的数据写入都会产生一个临时分区，不会纳入任何一个已有的分区。写入后的某个时刻（大概 10-15 分钟后），ClickHouse 会自动执行合并操作（等不及也可以手动通过 optimize 执行），把临时分区的数据，合并到已有分区中。

```sql
optimize table xxxx final;
```

* **举例：**

再次执行上面的插入操作

```sql
insert into t_order_mt values
(101,'sku_001',1000.00,'2020-06-01 12:00:00') ,
(102,'sku_002',2000.00,'2020-06-01 11:00:00'),
(102,'sku_004',2500.00,'2020-06-01 12:00:00'),
(102,'sku_002',2000.00,'2020-06-01 13:00:00'),
(102,'sku_002',12000.00,'2020-06-01 13:00:00'),
(102,'sku_002',600.00,'2020-06-02 12:00:00');
```

查看数据并没有纳入任何分区

```sql
select * from  t_order_mt;
```

```shell
Neptune :) select * from  t_order_mt;

SELECT *
FROM t_order_mt

Query id: eff5b5f6-b3be-43ff-bd58-14d182dd5ea0

┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 102 │ sku_002 │       600.00 │ 2020-06-02 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘
┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 102 │ sku_002 │       600.00 │ 2020-06-02 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘
┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 101 │ sku_001 │      1000.00 │ 2020-06-01 12:00:00 │
│ 102 │ sku_002 │      2000.00 │ 2020-06-01 11:00:00 │
│ 102 │ sku_002 │      2000.00 │ 2020-06-01 13:00:00 │
│ 102 │ sku_002 │     12000.00 │ 2020-06-01 13:00:00 │
│ 102 │ sku_004 │      2500.00 │ 2020-06-01 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘
┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 101 │ sku_001 │      1000.00 │ 2020-06-01 12:00:00 │
│ 102 │ sku_002 │      2000.00 │ 2020-06-01 11:00:00 │
│ 102 │ sku_002 │      2000.00 │ 2020-06-01 13:00:00 │
│ 102 │ sku_002 │     12000.00 │ 2020-06-01 13:00:00 │
│ 102 │ sku_004 │      2500.00 │ 2020-06-01 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘

12 rows in set. Elapsed: 0.005 sec.

```

手动 optimize 之后

```sql
optimize table t_order_mt final;
```

再次查询

```sql
select * from  t_order_mt;
```

```shell
Neptune :) select * from  t_order_mt;

SELECT *
FROM t_order_mt

Query id: 8b81b9fa-3ee0-4e10-935d-382dfd3136d0

┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 101 │ sku_001 │      1000.00 │ 2020-06-01 12:00:00 │
│ 101 │ sku_001 │      1000.00 │ 2020-06-01 12:00:00 │
│ 102 │ sku_002 │      2000.00 │ 2020-06-01 11:00:00 │
│ 102 │ sku_002 │      2000.00 │ 2020-06-01 13:00:00 │
│ 102 │ sku_002 │     12000.00 │ 2020-06-01 13:00:00 │
│ 102 │ sku_002 │      2000.00 │ 2020-06-01 11:00:00 │
│ 102 │ sku_002 │      2000.00 │ 2020-06-01 13:00:00 │
│ 102 │ sku_002 │     12000.00 │ 2020-06-01 13:00:00 │
│ 102 │ sku_004 │      2500.00 │ 2020-06-01 12:00:00 │
│ 102 │ sku_004 │      2500.00 │ 2020-06-01 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘
┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 102 │ sku_002 │       600.00 │ 2020-06-02 12:00:00 │
│ 102 │ sku_002 │       600.00 │ 2020-06-02 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘

12 rows in set. Elapsed: 0.003 sec.

```

### 4.3.2 primary key 主键(可选)

* ClickHouse 中的主键，和其他数据库不太一样，**它只提供了数据的一级索引，但是却不是唯一约束。**这就意味着是可以存在相同 primary key 的数据的。

* 主键的设定主要依据是查询语句中的 where 条件。

* 根据条件通过对主键进行某种形式的二分查找，能够定位到对应的 index granularity,避免了全表扫描。

**index granularity：**索引粒度，指在稀疏索引中两个相邻索引对应数据的间隔。ClickHouse 中的 MergeTree 默认是 8192。官方不建议修改这个值，除非该列存在大量重复值，比如在一个分区中几万行才有一个不同数据。 

**稀疏索引：**稀疏索引的好处就是可以用很少的索引数据，定位更多的数据，代价就是只能定位到索引粒度的第一行，然后再进行进行一点扫描。

![image-20221205205538068](../../images/image-20221205205538068.png)

### 4.3.3 **order by**（必选）

* order by 设定了==分区内==的数据按照哪些字段顺序进行有序保存。
* order by 是 MergeTree 中唯一一个必填项，甚至比 primary key 还重要，因为当用户不设置主键的情况，很多处理会依照 order by 的字段进行处理（比如去重和汇总）。

==要求：主键必须是 order by 字段的前缀字段。==

比如 order by 字段是 (id,sku_id) 那么主键必须是 id 或者(id,sku_id)

### 4.3.4 二级索引

目前在 ClickHouse 的官网上二级索引的功能在 v20.1.2.4 之前是被标注为实验性的，这个版本之后默认开启。

* **老版本使用二级索引前需要增加设置**

是否允许使用实验性的二级索引（==v20.1.2.4 开始，这个参数已被删除，默认开启==）

```sql
set allow_experimental_data_skipping_indices=1;
```

* **创建测试表**

```sql
create table t_order_mt2(
 id UInt32,
 sku_id String,
 total_amount Decimal(16,2),
 create_time Datetime,
INDEX a total_amount TYPE minmax GRANULARITY 5
) engine =MergeTree
 partition by toYYYYMMDD(create_time)
 primary key (id)
 order by (id, sku_id);
```

其中 GRANULARITY N 是设定二级索引对于一级索引粒度的粒度。

* **插入数据**

```sql
insert into t_order_mt2 values
(101,'sku_001',1000.00,'2020-06-01 12:00:00') ,
(102,'sku_002',2000.00,'2020-06-01 11:00:00'),
(102,'sku_004',2500.00,'2020-06-01 12:00:00'),
(102,'sku_002',2000.00,'2020-06-01 13:00:00'),
(102,'sku_002',12000.00,'2020-06-01 13:00:00'),
(102,'sku_002',600.00,'2020-06-02 12:00:00');
```

* **对比效果**

使用下面语句进行测试，可以看出二级索引能够为非主键字段的查询发挥作用。

```perl
 clickhouse-client --send_logs_level=trace <<< 'select * from t_order_mt2 where total_amount > toDecimal32(900., 2)';
```

![image-20221205211030088](../../images/image-20221205211030088.png)

### 4.3.5 **数据** **TTL**

TTL 即 Time To Live，MergeTree 提供了可以管理数据表或者列的==生命周期==的功能。

#### 4.3.5.1 **列级别** **TTL**

==TTL子句不能被用于主键字段。==

* 创建测试表

```sql
create table t_order_mt3(
 id UInt32,
 sku_id String,
 total_amount Decimal(16,2) TTL create_time+interval 10 SECOND,
 create_time Datetime
) engine =MergeTree
partition by toYYYYMMDD(create_time)
 primary key (id)
 order by (id, sku_id);
```

* 插入数据

```sql
insert into t_order_mt3 values
(106,'sku_001',1000.00,now()),
(107,'sku_002',2000.00,now()),
(110,'sku_003',600.00,now());
```

* ==手动合并==，查看效果，到期后，指定的字段数据归 0

==到期后竟然要重启才能归零？==

```perl
clickhouse restart
```

```sql
optimize table t_order_mt3 final;
select * from t_order_mt3;
```

```shell
Neptune :) select * from t_order_mt3;

SELECT *
FROM t_order_mt3

Query id: 630fcb61-256d-411c-8b5c-0c5237f62d07

┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 106 │ sku_001 │         0.00 │ 2022-12-05 21:43:41 │
│ 107 │ sku_002 │         0.00 │ 2022-12-05 21:43:41 │
│ 110 │ sku_003 │         0.00 │ 2022-12-05 21:43:41 │
└─────┴─────────┴──────────────┴─────────────────────┘

3 rows in set. Elapsed: 0.003 sec.
```

#### 4.3.5.2 **表级** **TTL**

下面的这条语句是数据会在 create_time 之后 10 秒丢失

```sql
alter table t_order_mt3 MODIFY TTL create_time + INTERVAL 10 SECOND;
```

涉及判断的字段必须是 Date 或者 Datetime 类型，推荐使用分区的日期字段。

能够使用的时间周期：

-  SECOND

- MINUTE

- HOUR

- DAY

- WEEK

- MONTH

- QUARTER

- YEAR

## 4.4 **ReplacingMergeTree**

ReplacingMergeTree 是 MergeTree 的一个变种，它存储特性完全继承 MergeTree，只是多了一个==去重==的功能。 尽管 MergeTree 可以设置主键，但是 primary key 其实没有唯一约束的功能。如果想处理掉重复的数据，可以借助 ReplacingMergeTree。

* **去重时机**

==数据的去重只会在合并的过程中出现==。合并会在未知的时间在后台进行，所以无法预先作出计划。有一些数据可能仍未被处理。

* **去重范围**

==如果表经过了分区，去重只会在分区内部进行去重，不能执行跨分区的去重。==

所以 ReplacingMergeTree 能力有限， ReplacingMergeTree 适用于在后台清除重复的数据以节省空间，但是它不保证没有重复的数据出现。

* **举例**

**1. 创建表**

```sql
create table t_order_rmt(
 id UInt32,
 sku_id String,
 total_amount Decimal(16,2) ,
 create_time Datetime 
) engine =ReplacingMergeTree(create_time)
 partition by toYYYYMMDD(create_time)
 primary key (id)
 order by (id, sku_id);
```

==ReplacingMergeTree() 填入的参数为版本字段，重复数据保留版本字段值最大的。如果不填版本字段，默认按照插入顺序保留最后一条。==

**2. 向表中插入数据**

```sql
insert into t_order_rmt values
(101,'sku_001',1000.00,'2020-06-01 12:00:00') ,
(102,'sku_002',2000.00,'2020-06-01 11:00:00'),
(102,'sku_004',2500.00,'2020-06-01 12:00:00'),
(102,'sku_002',2000.00,'2020-06-01 13:00:00'),
(102,'sku_002',12000.00,'2020-06-01 13:00:00'),
(102,'sku_002',600.00,'2020-06-02 12:00:00');
```

**3. 执行查询**

```sql
select * from t_order_rmt;

--若未去重执行以下语句
OPTIMIZE TABLE t_order_rmt FINAL;
select * from t_order_rmt;
```

```shell
Neptune :)  select * from t_order_rmt;

SELECT *
FROM t_order_rmt

Query id: 112ebbec-9cfa-4405-a2c2-44af05444d1d

┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 102 │ sku_002 │       600.00 │ 2020-06-02 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘
┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 101 │ sku_001 │      1000.00 │ 2020-06-01 12:00:00 │
│ 102 │ sku_002 │     12000.00 │ 2020-06-01 13:00:00 │
│ 102 │ sku_004 │      2500.00 │ 2020-06-01 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘

4 rows in set. Elapsed: 0.004 sec.
```

* **结论**
  * 实际上是使用 order by 字段作为唯一键
  * 去重不能跨分区
  * 只有同一批插入（新版本）或合并分区时才会进行去重
  * 认定重复的数据保留，版本字段值最大的
  * 如果版本字段相同则按插入顺序保留最后一笔

## 4.5 **SummingMergeTree**

对于不查询明细，只关心以维度进行汇总聚合结果的场景。如果只使用普通的MergeTree的话，无论是存储空间的开销，还是查询时临时聚合的开销都比较大。

ClickHouse 为了这种场景，提供了一种能够“预聚合”的引擎 SummingMergeTree

* **举例**

```sql
--创建表
create table t_order_smt(
 id UInt32,
 sku_id String,
 total_amount Decimal(16,2) ,
 create_time Datetime 
) engine =SummingMergeTree(total_amount)
 partition by toYYYYMMDD(create_time)
 primary key (id)
 order by (id,sku_id );
 
 --插入数据
 insert into t_order_smt values
(101,'sku_001',1000.00,'2020-06-01 12:00:00'),
(102,'sku_002',2000.00,'2020-06-01 11:00:00'),
(102,'sku_004',2500.00,'2020-06-01 12:00:00'),
(102,'sku_002',2000.00,'2020-06-01 13:00:00'),
(102,'sku_002',12000.00,'2020-06-01 13:00:00'),
(102,'sku_002',600.00,'2020-06-02 12:00:00');

--查询结果
select * from t_order_smt;

--若未聚合，执行以下语句
OPTIMIZE TABLE t_order_smt FINAL;
select * from t_order_smt;
```

```shell
Neptune :) select * from t_order_smt;

SELECT *
FROM t_order_smt

Query id: e12d4dab-e4cc-42aa-a7f3-96f9a7bdd232

┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 101 │ sku_001 │      1000.00 │ 2020-06-01 12:00:00 │
│ 102 │ sku_002 │     16000.00 │ 2020-06-01 11:00:00 │
│ 102 │ sku_004 │      2500.00 │ 2020-06-01 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘
┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 102 │ sku_002 │       600.00 │ 2020-06-02 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘

4 rows in set. Elapsed: 0.004 sec.

```

* **结论**
  * 以 SummingMergeTree（）中指定的列作为汇总数据列
  * 可以填写多列必须数字列，如果不填，以所有非维度列且为数字列的字段为汇总数据列
  * 以 order by 的列为准，作为维度列
  * 其他的列按插入顺序保留第一行
  * 不在一个分区的数据不会被聚合
  * 只有在同一批次插入(新版本)或分片合并时才会进行聚合

* **开发建议**

设计聚合表，唯一键值、流水号可以去掉，所有字段全部是维度、度量或者时间戳。

* **问题**

能不能直接执行以下 SQL 得到汇总值

```sql
select total_amount from XXX where province_name=' ' and create_date='xxx';
```

不行，==可能会包含一些还没来得及聚合的临时明细==

如果要是获取汇总值，还是需要使用 sum 进行聚合，这样效率会有一定的提高，但本身 ClickHouse 是列式存储的，效率提升有限，不会特别明显。

```sql
select sum(total_amount) from province_name=' ' and create_date='xxx’
```

# 5 **SQL** **操作**

基本上来说传统关系型数据库（以 MySQL 为例）的 SQL 语句，ClickHouse 基本都支持，只介绍 ClickHouse 与标准 SQL（MySQL）不一致的地方。

## **5.1 Insert**

基本与标准 SQL（MySQL）基本一致

* 标准

```sql
insert into [table_name] values(…),(….)
```

* 从表到表的插入

```sql
insert into [table_name] select a,b,c from [table_name_2]
```

## **5.2 Update** **和** **Delete**

ClickHouse 提供了 Delete 和 Update 的能力，这类操作被称为 Mutation 查询，它可以看做 Alter 的一种。

虽然可以实现修改和删除，但是和一般的 OLTP 数据库不一样，**Mutation 语句是一种很“重”的操作，而且不支持事务。**

“重”的原因主要是每次修改或者删除都会导致放弃目标数据的原有分区，重建新分区。所以尽量做批量的变更，不要进行频繁小数据的操作。

* 删除操作

```sql
alter table t_order_smt delete where sku_id ='sku_001';
```

* 修改操作

```sql
alter table t_order_smt update total_amount=toDecimal32(2000.00,2) where id =102;
```

由于操作比较“重”，所以 Mutation 语句分两步执行：

* 同步执行的部分其实只是进行新增数据新增分区和并把旧分区打上逻辑上的失效标记。
* 触发分区合并的时候，才会删除旧数据释放磁盘空间，一般不会开放这样的功能给用户，由管理员完成。

## **5.3** **查询操作**

* 支持子查询

* 支持 CTE(Common Table Expression 公用表表达式 with 子句)

* 支持各种 JOIN，但是 JOIN 操作无法使用缓存，所以即使是两次相同的 JOIN 语句，ClickHouse 也会视为两条新 SQL

* ==窗口函数(官方正在测试中...)==

* 不支持自定义函数

### GROUP BY 

GROUP BY 操作增加了 with rollup\with cube\with total 用来计算小计和总计。

维度是a,b
`rollup`：上卷  
      group by 
      group by a 
      group by a,b 
`cube`：多维分析
      group by  a,b 
      group by  a
      group by  b 
      group by 
`total`：总计
      group by  a,b 
      group by 

**插入数据**

```sql
alter table t_order_mt delete where 1=1;
insert into t_order_mt values
(101,'sku_001',1000.00,'2020-06-01 12:00:00'),
(101,'sku_002',2000.00,'2020-06-01 12:00:00'),
(103,'sku_004',2500.00,'2020-06-01 12:00:00'),
(104,'sku_002',2000.00,'2020-06-01 12:00:00'),
(105,'sku_003',600.00,'2020-06-02 12:00:00'),
(106,'sku_001',1000.00,'2020-06-04 12:00:00'),
(107,'sku_002',2000.00,'2020-06-04 12:00:00'),
(108,'sku_004',2500.00,'2020-06-04 12:00:00'),
(109,'sku_002',2000.00,'2020-06-04 12:00:00'),
(110,'sku_003',600.00,'2020-06-01 12:00:00');
```

#### **with rollup**

==从右至左去掉维度进行小计==

group by (a,b,c)

a,b,c

a,b

a



```sql
select id , sku_id,sum(total_amount) from t_order_mt group by id,sku_id with rollup;
```

![image-20221205222932789](../../images/image-20221205222932789.png)

#### with cube

==从右至左去掉维度进行小计，再从左至右去掉维度进行小计==

```sql
select id , sku_id,sum(total_amount) from t_order_mt group by id,sku_id with cube;
```

![image-20221205223232566](../../images/image-20221205223232566.png)

#### with totals

**只计算合计**

```sql
select id , sku_id,sum(total_amount) from t_order_mt group by id,sku_id with totals;
```

```shell
Neptune :) select id , sku_id,sum(total_amount) from t_order_mt group by id,sku_id with totals;

SELECT
    id,
    sku_id,
    sum(total_amount)
FROM t_order_mt
GROUP BY
    id,
    sku_id
    WITH TOTALS

Query id: 4a6f6607-b8d0-4816-9410-8cda67b18f6a

┌──id─┬─sku_id──┬─sum(total_amount)─┐
│ 110 │ sku_003 │            600.00 │
│ 109 │ sku_002 │           2000.00 │
│ 107 │ sku_002 │           2000.00 │
│ 106 │ sku_001 │           1000.00 │
│ 104 │ sku_002 │           2000.00 │
│ 101 │ sku_002 │           2000.00 │
│ 103 │ sku_004 │           2500.00 │
│ 108 │ sku_004 │           2500.00 │
│ 105 │ sku_003 │            600.00 │
│ 101 │ sku_001 │           1000.00 │
└─────┴─────────┴───────────────────┘

Totals:
┌─id─┬─sku_id─┬─sum(total_amount)─┐
│  0 │        │          16200.00 │
└────┴────────┴───────────────────┘

10 rows in set. Elapsed: 0.004 sec.

```

## **5.4 alter** **操作**

**新增字段**

```sql
alter table tableName add column newcolname String after col1;
```

**修改字段类型**

```sql
alter table tableName modify column newcolname String;
```

**删除字段**

```sql
alter table tableName drop column newcolname;
```

## **5.5** **导出数据**

```perl
clickhouse-client --query "select * from t_order_mt where 
create_time='2020-06-01 12:00:00'" --format CSVWithNames> 
/opt/module/data/rs1.csv
```

更多支持格式参照：https://clickhouse.tech/docs/en/interfaces/formats/

# 6 副本

副本主要是保障数据的高可用性，即使一台 ClickHouse 节点宕机，那么也可以从其他服务器获得相同的数据。

https://clickhouse.tech/docs/en/engines/table-engines/mergetree-family/replication/

## **6.1** **副本写入流程**

![image-20221206090712604](../../images/image-20221206090712604.png)

## **6.2** **配置步骤**

* 启动 zookeeper 集群

* 在 hadoop102 的/etc/clickhouse-server/config.d 目录下创建一个名为 metrika.xml的配置文件,内容如下：

  ==注：也可以不创建外部文件，直接在 config.xml 中指定<zookeeper>==

```xml
<?xml version="1.0"?>
	<yandex>
		<zookeeper-servers>
			<node index="1">
				<host>hadoop102</host>
				<port>2181</port>
			</node>
			<node index="2">
				<host>hadoop103</host>
				<port>2181</port>
			</node>
			<node index="3">
				<host>hadoop104</host>
				<port>2181</port>
			</node>
		</zookeeper-servers>
</yandex>
```

* 同步到 hadoop103 和 hadoop104 上

```perl
sudo /home/atguigu/bin/xsync /etc/clickhouse-server/config.d/metrika.xml
```

* 在 hadoop102 的/etc/clickhouse-server/config.xml 中<yandex>标签里的增加

```perl
<zookeeper incl="zookeeper-servers" optional="true" />
<include_from>/etc/clickhouse-server/config.d/metrika.xml</include_from>
```

* 同步到 hadoop103 和 hadoop104 上

```perl
sudo /home/atguigu/bin/xsync /etc/clickhouse-server/config.xml
```

* 分别在 hadoop102 和 hadoop103 上启动 ClickHouse 服务

==注意：因为修改了配置文件，如果以前启动了服务需要重启==

```perl
 sudo clickhouse restart
```

==注意：演示副本操作只需要在 hadoop102 和 hadoop103 两台服务器即可，上面的操作，hadoop104 可以不用同步，这里为了保证集群中资源的一致性，做了同步。==

* 在 hadoop102 和 hadoop103 上分别建表

==副本只能同步数据，不能同步表结构，所以需要在每台机器上自己手动建表==

①hadoop102

```sql
create table t_order_rep2 (
 id UInt32,
 sku_id String,
 total_amount Decimal(16,2),
 create_time Datetime
) engine =ReplicatedMergeTree('/clickhouse/table/01/t_order_rep','rep_102')
 partition by toYYYYMMDD(create_time)
 primary key (id)
 order by (id,sku_id);
```

②hadoop103

```sql
create table t_order_rep2 (
 id UInt32,
 sku_id String,
 total_amount Decimal(16,2),
 create_time Datetime
) engine =ReplicatedMergeTree('/clickhouse/table/01/t_order_rep','rep_103')
 partition by toYYYYMMDD(create_time)
 primary key (id)
 order by (id,sku_id);
```

③参数解释

ReplicatedMergeTree 中：

**第一个参数**是分片的 zk_path 一般按照：/clickhouse/table/{shard}/{table_name} 的格式写，如果只有一个分片就写 01 即可。

**第二个参数**是副本名称，==相同的分片副本名称不能相同==。

* 在 hadoop102 上执行 insert 语句

```sql
insert into t_order_rep2 values
(101,'sku_001',1000.00,'2020-06-01 12:00:00'),
(102,'sku_002',2000.00,'2020-06-01 12:00:00'),
(103,'sku_004',2500.00,'2020-06-01 12:00:00'),
(104,'sku_002',2000.00,'2020-06-01 12:00:00'),
(105,'sku_003',600.00,'2020-06-02 12:00:00');
```

* 在 hadoop103 上执行 select，可以查询出结果，说明副本配置正确

```sql
select * from t_order_rep2;
```

```shell
hadoop103 :) select * from t_order_rep2;

SELECT *
FROM t_order_rep2

Query id: 763972e2-ba4c-441d-b9d5-461f40d2c8eb

┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 101 │ sku_001 │      1000.00 │ 2020-06-01 12:00:00 │
│ 102 │ sku_002 │      2000.00 │ 2020-06-01 12:00:00 │
│ 103 │ sku_004 │      2500.00 │ 2020-06-01 12:00:00 │
│ 104 │ sku_002 │      2000.00 │ 2020-06-01 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘
┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 105 │ sku_003 │       600.00 │ 2020-06-02 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘

5 rows in set. Elapsed: 0.003 sec.

```

# 7 **分片集群**

副本虽然能够提高数据的可用性，降低丢失风险，但是每台服务器实际上必须容纳全量数据，对数据的==横向扩容==没有解决。

要解决数据水平切分的问题，需要引入分片的概念。通过分片把一份完整的数据进行切分，不同的分片分布到不同的节点上，再通过 Distributed 表引擎把数据拼接起来一同使用。

==Distributed 表引擎本身不存储数据==，有点类似于 MyCat 之于 MySql，成为一种中间件，通过分布式逻辑表来写入、分发、路由来操作多台节点不同分片的分布式数据。

==注意：ClickHouse 的集群是表级别的，实际企业中，大部分做了高可用，但是没有用分片，避免降低查询性能以及操作集群的复杂性。==

## 7.1 集群写入流程（3 分片 2 副本共 6 个节点）

==`internal_replication`参数一般为true，内部同步打开==

![image-20221206172532241](../../images/image-20221206172532241.png)

## 7.2 集群读取流程（3 分片 2 副本共 6 个节点）

`errors_count`：**发生错误的次数**

![image-20221206172655298](../../images/image-20221206172655298.png)

## 7.3 3 分片 2 副本共 6 个节点集群配置（供参考）

配置的位置还是在/etc/clickhouse-server/config.d/metrika.xml，内容如下

==注：也可以不创建外部文件，直接在 config.xml 的<remote_servers>中指定==

```xml
<yandex>
	<remote_servers>
		<neptune_cluster> <!-- 集群名称，自定义名称--> 
			<shard> <!--集群的第一个分片-->
				<internal_replication>true</internal_replication>
				<!--该分片的第一个副本-->
				<replica> 
					<host>hadoop101</host>
					<port>9000</port>
				</replica>
				<!--该分片的第二个副本-->
				<replica> 
					<host>hadoop102</host>
					<port>9000</port>
				</replica>
			</shard>
			<shard> <!--集群的第二个分片-->
				<internal_replication>true</internal_replication>
				<replica> <!--该分片的第一个副本-->
					<host>hadoop103</host>
					<port>9000</port>
				</replica>
				<replica> <!--该分片的第二个副本-->
					<host>hadoop104</host>
					<port>9000</port>
				</replica>
			</shard>
			<shard> <!--集群的第三个分片-->
				<internal_replication>true</internal_replication>
				<replica> <!--该分片的第一个副本-->
					<host>hadoop105</host>
					<port>9000</port>
				</replica>
				<replica> <!--该分片的第二个副本-->
					<host>hadoop106</host>
					<port>9000</port>
				</replica>
			</shard>
		</neptune_cluster>
	</remote_servers>
</yandex>
```

## **7.4** **配置三节点版本集群及副本**

### 7.4.1 集群及副本规划（2 个分片，只有第一个分片有副本）

![image-20221206191958799](../../images/image-20221206191958799.png)

```xml
集群规划：

hadoop102
<macros>
	<shard>01</shard> 
	<replica>rep_1_1</replica>
</macros>

hadoop103
<macros>
	<shard>01</shard> 
	<replica>rep_1_2</replica>
</macros>

hadoop104
<macros>
	<shard>02</shard> 
	<replica>rep_2_1</replica>
</macros>
```

### **7.4.2** **配置步骤**

* **在 hadoop102 的/etc/clickhouse-server/config.d 目录下创建 metrika-shard.xml 文件**

==注：也可以不创建外部文件，直接在 config.xml 的<remote_servers>中指定==

```xml
<?xml version="1.0"?>
<yandex>
	<remote_servers>
		<neptune_cluster><!--集群名称-->
			<shard><!--集群的第一个分片-->
				<internal_replication>true</internal_replication>
				<replica><!--该分片的第一个副本-->
					<host>hadoop102</host>
					<port>9000</port>
				</replica>
				<replica><!--该分片的第二个副本-->
					<host>hadoop103</host>
					<port>9000</port>
				</replica>
			</shard>
			<shard><!--集群的第二个分片-->
				<internal_replication>true</internal_replication>
				<replica><!--该分片的第一个副本-->
					<host>hadoop104</host>
					<port>9000</port>
				</replica>
			</shard>
		</neptune_cluster>
	</remote_servers>
	<zookeeper-servers>
		<node index="1">
			<host>hadoop102</host>
			<port>2181</port>
		</node>
		<node index="2">
			<host>hadoop103</host>
			<port>2181</port>
		</node>
		<node index="3">
			<host>hadoop104</host>
			<port>2181</port>
		</node>
	</zookeeper-servers>
        <!-- 宏: 将来建表的时候, 可以从这里自动读取, 每个机器上的建表语句就可以一样了 相当于变量 -->
	<macros>
		<shard>01</shard><!--不同机器放的分片数不一样-->
		<replica>rep_1_1</replica><!--不同机器放的副本数不一样-->
	</macros>
</yandex>
```

* **将** **hadoop102** **的** **metrika-shard.xml** **同步到** **103** **和** **104**

```perl
sudo /home/atguigu/bin/xsync /etc/clickhouse-server/config.d/metrika-shard.xml
```

* **修改hadoop103和hadoop104中metrika-shard.xml宏的配置**

```perl
#hadoop103上修改
sudo vim /etc/clickhouse-server/config.d/metrika-shard.xml
	<macros>
		<shard>01</shard><!--不同机器放的分片数不一样-->
		<replica>rep_1_2</replica><!--不同机器放的副本数不一样-->
	</macros>
#hadoop104上修改
sudo vim /etc/clickhouse-server/config.d/metrika-shard.xml
	<macros>
		<shard>02</shard><!--不同机器放的分片数不一样-->
		<replica>rep_2_1</replica><!--不同机器放的副本数不一样-->
	</macros>
```

* **在 hadoop102 上修改/etc/clickhouse-server/config.xml**

```perl
sudo vim /etc/clickhouse-server/config.xml
在yandex标签内添加

<zookeeper incl="zookeeper-servers" optional="true" />
<include_from>/etc/clickhouse-server/config.d/metrika-shard.xml</include_from>
```

* **同步/etc/clickhouse-server/config.xml 到 103 和 104**

```perl
sudo /home/atguigu/bin/xsync /etc/clickhouse-server/config.xml
```

* **重启三台服务器上的 ClickHouse 服务**

```perl
sudo clickhouse restart
ps -ef |grep click
```

* **在 hadoop102 上执行建表语句**

➢ 会自动同步到 hadoop103 和 hadoop104 上

➢ 集群名字要和配置文件中的一致

➢ 分片和副本名称从配置文件的宏定义中获取

```sql
 create table st_order_mt on cluster neptune_cluster (
 id UInt32,
 sku_id String,
 total_amount Decimal(16,2),
 create_time Datetime
) engine 
=ReplicatedMergeTree('/clickhouse/tables/{shard}/st_order_mt','{replica}')
 partition by toYYYYMMDD(create_time)
 primary key (id)
 order by (id,sku_id);
```

```shell
hadoop102 :)  create table st_order_mt on cluster neptune_cluster (
:-]  id UInt32,
:-]  sku_id String,
:-]  total_amount Decimal(16,2),
:-]  create_time Datetime
:-] ) engine
:-] =ReplicatedMergeTree('/clickhouse/tables/{shard}/st_order_mt','{replica}')
:-]  partition by toYYYYMMDD(create_time)
:-]  primary key (id)
:-]  order by (id,sku_id);

CREATE TABLE st_order_mt ON CLUSTER neptune_cluster
(
    `id` UInt32,
    `sku_id` String,
    `total_amount` Decimal(16, 2),
    `create_time` Datetime
)
ENGINE = ReplicatedMergeTree('/clickhouse/tables/{shard}/st_order_mt', '{replica}')
PARTITION BY toYYYYMMDD(create_time)
PRIMARY KEY id
ORDER BY (id, sku_id)

Query id: 02ce3708-16c9-4a2b-82b0-1cfdafa0110f

┌─host──────┬─port─┬─status─┬─error─┬─num_hosts_remaining─┬─num_hosts_active─┐
│ hadoop102 │ 9000 │      0 │       │                   2 │                1 │
│ hadoop104 │ 9000 │      0 │       │                   1 │                1 │
└───────────┴──────┴────────┴───────┴─────────────────────┴──────────────────┘
┌─host──────┬─port─┬─status─┬─error─┬─num_hosts_remaining─┬─num_hosts_active─┐
│ hadoop103 │ 9000 │      0 │       │                   0 │                0 │
└───────────┴──────┴────────┴───────┴─────────────────────┴──────────────────┘

3 rows in set. Elapsed: 0.171 sec.

```

可以到 hadoop103 和 hadoop104 上查看表是否创建成功

```perl
clickhouse-client -m

desc st_order_mt;
```

* **在 hadoop102 上创建 Distribute 分布式表**

```sql
create table st_order_mt_all2 on cluster neptune_cluster(
 id UInt32,
 sku_id String,
 total_amount Decimal(16,2),
 create_time Datetime
)engine = Distributed(neptune_cluster,default, st_order_mt,hiveHash(sku_id));
```

**参数含义：**

Distributed（集群名称，库名，本地表名，分片键）

分片键必须是整型数字，所以用 hiveHash 函数转换，也可以 rand()

* **在 hadoop102 上插入测试数据**

```sql
insert into st_order_mt_all2 values
(201,'sku_001',1000.00,'2020-06-01 12:00:00') ,
(202,'sku_002',2000.00,'2020-06-01 12:00:00'),
(203,'sku_004',2500.00,'2020-06-01 12:00:00'),
(204,'sku_002',2000.00,'2020-06-01 12:00:00'),
(205,'sku_003',600.00,'2020-06-02 12:00:00');
```

* **通过查询分布式表和本地表观察输出结果**

  * 分布式表

    ```sql
    SELECT * FROM st_order_mt_all2;
    ```

  * 本地表

    ```sql
    select * from st_order_mt;
    ```

  * 观察数据的分布

```sql
hadoop102 :) SELECT * FROM st_order_mt_all2;

SELECT *
FROM st_order_mt_all2

Query id: d71b64c8-4ec1-4a2f-9674-bdc77b5c7c22

┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 202 │ sku_002 │      2000.00 │ 2020-06-01 12:00:00 │
│ 203 │ sku_004 │      2500.00 │ 2020-06-01 12:00:00 │
│ 204 │ sku_002 │      2000.00 │ 2020-06-01 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘
┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 201 │ sku_001 │      1000.00 │ 2020-06-01 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘
┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 205 │ sku_003 │       600.00 │ 2020-06-02 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘

5 rows in set. Elapsed: 0.009 sec.

hadoop102 :) select * from st_order_mt;

SELECT *
FROM st_order_mt

Query id: ac346ced-cb30-4798-8b40-bbb0376e913c

┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 202 │ sku_002 │      2000.00 │ 2020-06-01 12:00:00 │
│ 203 │ sku_004 │      2500.00 │ 2020-06-01 12:00:00 │
│ 204 │ sku_002 │      2000.00 │ 2020-06-01 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘

3 rows in set. Elapsed: 0.006 sec.

hadoop103 :) select * from st_order_mt;

SELECT *
FROM st_order_mt

Query id: c4df55b0-08b0-45ac-8d4d-44b7f84b9555

┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 202 │ sku_002 │      2000.00 │ 2020-06-01 12:00:00 │
│ 203 │ sku_004 │      2500.00 │ 2020-06-01 12:00:00 │
│ 204 │ sku_002 │      2000.00 │ 2020-06-01 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘

3 rows in set. Elapsed: 0.011 sec.

hadoop104 :) select * from st_order_mt;

SELECT *
FROM st_order_mt

Query id: 204ec94d-45af-4f86-9ede-4c44ea735d73

┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 205 │ sku_003 │       600.00 │ 2020-06-02 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘
┌──id─┬─sku_id──┬─total_amount─┬─────────create_time─┐
│ 201 │ sku_001 │      1000.00 │ 2020-06-01 12:00:00 │
└─────┴─────────┴──────────────┴─────────────────────┘

2 rows in set. Elapsed: 0.007 sec.

```

## **7.5** **项目为了节省资源，就使用单节点，不用集群**

不需要改文件引用，因为已经使用集群建表了，如果改为引用 metrika-shard.xml 的话，启动会报错。

只启动 hadoop102 即可。

# ClickHouse 高级

# 1 **Explain** **查看执行计划**

在 clickhouse 20.6 版本之前要查看 SQL 语句的执行计划需要设置日志级别为 trace 才能可以看到，在执行日志里面查看。在 20.6 版本引入了原生的执行计划的语法。在 20.6.3 版本成为正式版本的功能。

==本文档基于目前较新稳定版 21.7.3.14。==



