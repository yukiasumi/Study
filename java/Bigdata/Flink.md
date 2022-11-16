# 1 简介

## 1.1 Flink是什么【快速、灵巧】

官网：

- https://flink.apache.org/
- https://flink.apache.org/zh/usecases.html

- Apache Flink是由Apache软件基金会开发的**开源**流处理**框架**
- 其核心是用Java和Scala编写的**分布式**流数据流引擎
- Flink以**数据并行**和**流水线**方式执行任意流数据程序
- Flink的流水线运行时系统可以执行**批处理和流处理**程序
- Flink的运行时本身也支持迭代算法的执行
- **有界或无界数据流进行状态计算**

- - Flink可以对流执行任意数量的变换，这些流可以被编排为有向无环数据流图，允许应用程序分支和合并数据流
  - Flink的数据流API支持有界或无界数据流上的转换（如过滤器、聚合和窗口函数），包含了20多种不同类型的转换，可以在Java和Scala中使用。



2010 StructPhere => 2014 Apache基金会 => 2017 Alibaba开发

## 1.2 为什么用Flink

流数据更真实地反映了我们的生活方式

- 实时聊天，一条就直接发

传统的数据架构是基于有限数据集的

- 批量处理，隔一段时间数据攒齐了再计算

- - Spark Streaming就是这样，需要设置批处理时间间隔几百毫秒到几秒



**目标：**

- 低延迟

- - 想要做到毫秒级别延迟

- 高吞吐

- - 分区处理，然后再合并
  - 内存，扩容代价

- 结果的准确性和良好的容错性

- - 乱序问题
  - 一个节点挂了，回滚到最近的一个状态，然后继续处理



**应用场景：**

电商和市场营销

- 数据报表

- - 要求今晚12点前的数据出一个报表
  - 数据叠加计算，直接输出一个结果

- 广告投放
- 业务流程需要

物联网(IOT)

- 传感器实时数据采集和显示
- 实时报警
- 交通运输业

电信业

- 基站流量调配

- - 救援

银行和金融业

- 实时结算和通知推送

- - 银行的E+决策计算引擎，就是FlinkSQL

- - - 每一笔都做计算

- - 盘点核算

- 实时检测异常行为

## 1.3 流处理的发展和演变

### 1.3.1 传统事务处理

![image-20221114134355794](../../images/image-20221114134355794.png)

### 1.3.2 有状态的流处理

- 事务处理瓶颈~关联查询
- 解决：

- - 数据放内存里，保存成本地状态，替代关系型数据库的表；
  - 同时扩展使用集群
  - 内存~存盘，恢复机制（周期性检查点）

- 但扩展时，会存在数据乱序问题

![image-20221114134456819](../../images/image-20221114134456819.png)

#### 1.3.2.1 事件驱动型（Event-Driven）应用

![image-20221114134827581](../../images/image-20221114134827581.png)

#### 1.3.2.2 数据分析（Data Analysis）型应用

![image-20221114134848375](../../images/image-20221114134848375.png)

#### 1.3.2.3 数据管道（Data Pipeline）型应用

![image-20221114134912897](../../images/image-20221114134912897.png)

### 1.3.4 lambda流处理架构

用两套系统，同时保证低延迟和结果准确

- 需要自己写两套系统，并且保证正确性

![image-20221114134546998](../../images/image-20221114134546998.png)

### 1.3.5 Flink流处理

![img](https://cdn.nlark.com/yuque/0/2021/png/524492/1619403279152-43c36f8f-d3b1-4987-9f6a-64eb5cb3e797.png)

## 1.4 Flink的主要特点

### 1.4.1 事件驱动

![img](https://cdn.nlark.com/yuque/0/2021/png/524492/1619403504782-c44f3675-6b57-4930-9bcf-1c3033123f2a.png)

### 1.4.2 基于流的世界观

- 在Flink 的世界观中，一切都是由流组成的，离线数据是有界的流;
- 实时数据是一个没有界限的流:这就是所谓的有界流和无界流
- 状态：过去，现在，未来

![image-20221114135603467](../../images/image-20221114135603467.png)

### 1.4.3 分层API

- 越顶层越抽象，表达含义越简明，使用越方便
- 越底层越具体,表达能力越丰富，使用越灵活



离线的叫DataSet, 实时的叫DataStream

有状态的事件驱动：可以定时，自定义状态

![image-20221114135229685](../../images/image-20221114135229685.png)

### 1.4.4 其它

* **高吞吐和低延迟**。每秒处理数百万个事件，毫秒级延迟。
* 结果的**准确性**。Flink 提供了事件时间**（event-time）**和处理时间**（processing-time ）**语义。对于乱序事件流，事件时间语义仍然能提供一致且准确的结果。
* **精确一次**（exactly-once）的状态一致性保证。
* **可以连接到最常用的存储系统**，如 Apache Kafka、Apache Cassandra、Elasticsearch、
  JDBC、Kinesis 和（分布式）文件系统，如 HDFS 和 S3。
* **高可用**。本身高可用的设置，加上与 K8s，YARN 和 Mesos 的紧密集成，再加上从故障中快速恢复和动态扩展任务的能力，Flink 能做到以极少的停机时间 **7×24 全天候运行**。
* **动态拓展**能够更新应用程序代码并将作业（jobs）迁移到不同的 Flink 集群，而不会丢失应用程序的状态。

## 1.5 Flink Vs Spark Streaming

- 流处理 VS 微批处理
- 数据模型

- - spark采用RDD模型，spark streaming的 DStream 实际上也就是一组组小批数据RDD的集合
  - flink 基本数据模型是数据流，以及事件(Event)序列

- 运行时架构

- - spark 是批计算，将DAG划分为不同的stage，一个完成后才可以计算下一个

- - - 转换算子和行动算子
    - 假如当前的分布式处理不同的分区、不同的节点处理有先后，那我当前节点处理完了，但是别的分区没处理完，那就得等，等到当前Stage结束，因为还得做Shuffle等调整

- - flink 是标准的流执行模式，一个事件在一个节点处理完后可以直接发往下一个节点进行处理

- - - 没有等待的过程，当前节点处理完毕，立马到下一节点处理

![img](https://cdn.nlark.com/yuque/0/2021/png/524492/1619404337044-2edfa97f-4637-494e-9b59-db9b2f31a8bf.png)

![img](https://cdn.nlark.com/yuque/0/2021/png/524492/1619405392576-edf9f5aa-a83c-4ba2-8491-b04883dc36f8.png)

# 2 快速上手

## 2.1 WordCount Flink 批处理

新建maven项目

引入依赖

```xml
    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <flink.version>1.13.0</flink.version>
        <java.version>1.8</java.version>
        <scala.binary.version>2.12</scala.binary.version>
        <slf4j.version>1.7.30</slf4j.version>
    </properties>

    <dependencies>
        <!-- 引入 Flink 相关依赖-->
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-java</artifactId>
            <version>${flink.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-streaming-java_${scala.binary.version}</artifactId>
            <version>${flink.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-clients_${scala.binary.version}</artifactId>
            <version>${flink.version}</version>
        </dependency>
        <!-- 引入日志管理相关依赖-->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>${slf4j.version}</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>${slf4j.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-to-slf4j</artifactId>
            <version>2.14.0</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>3.8.1</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

在resource目录下配置log4j日志

```properties
log4j.rootLogger=error, stdout
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%-4r [%t] %-5p %c %x - %m%n
```



批处理

```java
package org.neptune.wc;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.operators.UnsortedGrouping;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class BatchWordCount {
    public static void main(String[] args) throws Exception {

        // 1. 创建执行环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        // 2. 从文件读取数据 按行读取(存储的元素就是每行的文本)
        DataSource<String> lineDataSource = env.readTextFile("input/word.txt");

        // 3. 转换数据格式,转换成二元组
        FlatMapOperator<String, Tuple2<String, Long>> wordToOne = lineDataSource.flatMap((String line, Collector<Tuple2<String, Long>> out) -> {
            String[] words = line.split(" ");
            //将每个单词转换成二元组
            for (String word : words) {
                out.collect(Tuple2.of(word, 1L));
            }
        }).returns(Types.TUPLE(Types.STRING, Types.LONG));

        //当 Lambda 表达式使用 Java 泛型的时候, 由于泛型擦除的存在, 需要显示的声明类型信息
        // 4. 按照 word 进行分组
        UnsortedGrouping<Tuple2<String, Long>> wordToOneGroup = wordToOne.groupBy(0);

        // 5. 分组内聚合统计
        AggregateOperator<Tuple2<String, Long>> sum = wordToOneGroup.sum(1);

        // 6. 打印结果
        sum.print();
    }

}

```

## 2.2 WorkCount Flink 流处理

- 来一个处理一个，事件触发, 中间保存它的**状态**
- 先写好处理方式，然后等数据过来，一个个处理

```java
package org.neptune.wc;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class StreamWordCount {
    public static void main(String[] args) throws Exception {
        // 1. 创建流式执行环境
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();

        //从参数中提取主机名和端口号
/*        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        String hostname = parameterTool.get("host");
        int port = parameterTool.getInt("port");*/

        // 2. 读取文本流
        DataStreamSource<String> lineDataStream = environment.socketTextStream("192.168.10.130", 7777);

        // 3. 转换计算
        SingleOutputStreamOperator<Tuple2<String, Long>> wordToOne = lineDataStream.flatMap((String line, Collector<Tuple2<String, Long>> out) -> {
            String[] words = line.split(" ");
            for (String word : words) {
                out.collect(Tuple2.of(word, 1L));
            }
        }).returns(Types.TUPLE(Types.STRING, Types.LONG));

        // 4. 按照 word 进行分组
        KeyedStream<Tuple2<String, Long>, String> wordToOneKeyedStream = wordToOne.keyBy(data -> data.f0);

        // 5. 分组内聚合统计
        SingleOutputStreamOperator<Tuple2<String, Long>> sum = wordToOneKeyedStream.sum(1);

        // 6. 打印结果
        sum.print();

        // 7. 启动执行
        environment.execute();

    }
}

```

通过在Linux主机上使用nc工具模拟

```perl
nc -lk 7777
```

# 3 Flink部署

![image-20221114140913939](../../images/image-20221114140913939.png)

## 3.1 部署模式

在一些应用场景中，对于集群资源分配和占用的方式，可能会有特定的需求。Flink 为各种场景提供了不同的部署模式，主要有以下三种：

* 会话模式（Session Mode）
  * 适合于单个规模小、执行时间短的大量作业
  * 资源是共享的，资源不够，提交新的作业会失败

![image-20221114213732060](../../images/image-20221114213732060.png)

* 单作业模式（Per-Job Mode）

  集群只为这个作业而生。同样由客户端运行应用程序，然后启动集群，作业被提交给 JobManager，进而分发给 TaskManager 执行。作业作业完成后，集群就会关闭。

  * 每个作业一个集群
  * 实际应用的首选模式
  * 作业完成后，集群就会关闭，所有资源也会释放
  * Flink 本身无法直接这样运行，所以单作业模式一般需要借助YARN、Kubernetes启动集群 

![image-20221114213748584](../../images/image-20221114213748584.png)

* 应用模式（Application Mode）

直接把应用提交到 JobManger 上运行。为每一个提交的应用单独启动一个 JobManager，也就是创建一个集群。这个 JobManager 只为执行这一个应用而存在，执行结束之后 JobManager 就关闭了，这就是所谓的应用模式。

![image-20221114213759022](../../images/image-20221114213759022.png)

总结：

* 在会话模式下，集群的生命周期独立于集群上运行的任何作业的生命周期，并且提交的所有作业共享资源。

* 单作业模式为每个提交的作业创建一个集群，带来了更好的资源隔离，这时集群的生命周期与作业的生命周期绑定。

* 应用模式为每个应用程序创建一个会话集群，在 JobManager 上直接调用应用程序的 main()方法。

它们的区别主要在于：集群的生命周期以及资源的分配方式；以及应用的 main 方法到底在哪里执行——客户端（Client）还是 JobManager。

## 3.2 独立模式（Standalone） 

### 3.2.1 会话模式部署

#### 3.2.1.1 环境配置

准备 3 台 Linux 机器，搭建集群环境。具体要求如下：

* 系统环境为 CentOS 7.5 版本。
* 安装 Java 8。
* 安装 Hadoop 集群，Hadoop 建议选择 Hadoop 2.7.5 以上版本。
* 配置集群节点服务器间时间同步以及免密登录，关闭防火墙。
  三台服务器的具体设置如下：
* 节点服务器 1，IP 地址为 192.168.10.102，主机名为 hadoop102。
* 节点服务器 2，IP 地址为 192.168.10.103，主机名为 hadoop103。
* 节点服务器 3，IP 地址为 192.168.10.104，主机名为 hadoop104。

#### 3.2.1.2 本地启动

Flink 可以运行在 Linux、Mac OS X 和 Windows 上。本地模式的安装唯一需要的只是Java 1.7.x或更高版本，本地运行会启动Single JVM，主要用于**测试调试代码**。

```perl
tar -zxvf flink-1.13.0-bin-scala_2.12.tgz -C /opt/module/
cd flink-1.13.0/
bin/start-cluster.sh

访问 http://hadoop102:8081 对 flink 集群和任务进行监控管理

#关闭应用
bin/stop-cluster.sh
```

#### 3.2.1.3 集群启动

| 节点服务器 | hadoop102  | hadoop103   | hadoop104   |
| ---------- | ---------- | ----------- | ----------- |
| 角色       | JobManager | TaskManager | TaskManager |

配置主节点

/etc/profile

```shell
#JAVA_HOME
export JAVA_HOME=/usr/java/jdk1.8.0_271
export CLASSPATH=$:CLASSPATH:$JAVA_HOME/lib/
export PATH=$PATH:$JAVA_HOME/bin

#KAFKA_HOME
export KAFKA_HOME=/opt/kafka
export PATH=$PATH:$KAFKA_HOME/bin

#ZOOKEEPER_HOME
export ZOOKEEPER_HOME=/usr/local/zookeeper
export PATH=$PATH:$ZOOKEEPER/bin

#HADOOP_HOME
export HADOOP_HOME=/opt/hadoop-3.1.3
export HADOOP_MAPRED_HOME=$HADOOP_HOME
export HADOOP_COMMON_HOME=$HADOOP_HOME
export HADOOP_HDFS_HOME=$HADOOP_HOME
export YARN_HOME=$HADOOP_HOME
export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native export
PATH=$PATH:$HADOOP_HOME/sbin:$HADOOP_HOME/bin

export HDFS_NAMENODE_USER=root
export HDFS_DATANODE_USER=root
export HDFS_SECONDARYNAMENODE_USER=root
export YARN_RESOURCEMANAGER_USER=root
export YARN_NODEMANAGER_USER=root

#HIVE_HOME
export HIVE_HOME=/opt/hive-3.1.2
export PATH=$PATH:$HIVE_HOME/bin
export FLUME_HOME=/opt/flume-1.9.0
export PATH=$PATH:$FLUME_HOME/bin

# kafkaEFAK
export KE_HOME=/opt/efak
export PATH=$PATH:$KE_HOME/bin

#HBASE_HOME
export HBASE_HOME=/opt/hbase
export PATH=$PATH:$HBASE_HOME/bin

#debzium-oracle
export LD_LIBRARY_PATH=/root/instantclient_21_1

#SPARK_HOME
export SPARK_HOME=/opt/spark
export PATH=$PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin

#FLINK_HOME
export FLINK_HOME=/opt/flink-1.13.0
export PATH=$PATH:$FLINK_HOME/bin

```



```perl
tar -zxvf flink-1.13.0-bin-scala_2.12.tgz -C /opt/module/

#配置环境变量
vim /etc/profile
#FLINK_HOME
export FLINK_HOME=/opt/flink-1.13.0
export PATH=$PATH:$FLINK_HOME/bin

source  /etc/profile

cd flink-1.13.0/conf

#修改JobManager 节点地址.
vim flink-conf.yaml
jobmanager.rpc.address: hadoop102

#修改 workers 文件，将另外两台节点服务器添加为本 Flink 集群的 TaskManager 节点
vim workers 
hadoop103
hadoop104
```

 JobManager 和 TaskManager 组件的优化配置项如下：

* `jobmanager.memory.process.size`：对 JobManager 进程可使用到的全部内存进行配置，包括 JVM 元空间和其他开销，默认为 1600M，可以根据集群规模进行适当调整。
* `taskmanager.memory.process.size`：对 TaskManager 进程可使用到的全部内存进行配置，包括 JVM 元空间和其他开销，默认为 1600M，可以根据集群规模进行适当调整。
* `taskmanager.numberOfTaskSlots`：对每个 TaskManager 能够分配的 Slot 数量进行配置，默认为 1，可根据 TaskManager 所在的机器能够提供给 Flink 的 CPU 数量决定。所谓Slot 就是 TaskManager 中具体运行一个任务所分配的计算资源。
* `parallelism.default`：Flink 任务执行的默认并行度，优先级低于代码中进行的并行度配置和任务提交时使用参数指定的并行度数量。关于 Slot 和并行度的概念，下一章做详细讲解。

分发安装目录

```perl
sudo /home/atguigu/bin/xsync /opt/module/flink-1.13.0
```

启动集群

```perl
#主节点执行
bin/start-cluster.sh

#查看进程
jps
```

启动成功后，访问 http://hadoop102:8081 对 flink 集群和任务进行监控管理

可以看到，当前集群的 TaskManager 数量为 2；由于默认每个 TaskManager 的 Slot

数量为 1，所以总 Slot 数和可用 Slot 数都为 2。

#### 3.2.1.4 提交作业

引入插件 maven-assembly-plugin 进行打包，方便自定义结构和定制依赖。

在pom.xml 文件中添加打包插件的配置

```xml
<build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>3.0.0</version>
                <configuration>
                    <descriptorRefs>
                        <descriptorRef>jar-with-dependencies</descriptorRef>
                    </descriptorRefs>
                </configuration>
                <executions>
                    <execution>
                        <id>make-assembly</id>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

插件配置完毕后（刷新maven依赖），可以使用 IDEA 的 Maven 工具执行 package 命令

打 包 完 成 后 ， 在 target 目 录 下 有FlinkTutorial-1.0-SNAPSHOT.jar 和 FlinkTutorial-1.0-SNAPSHOT-jar-with-dependencies.jar，集群中已经具备任务运行所需的所有依赖，所以建议使用 FlinkTutorial-1.0-SNAPSHOT.jar。

##### 3.2.1.4.1 页面提交

![image-20221114154507089](../../images/image-20221114154507089.png)

![image-20221114204642814](../../images/image-20221114204642814.png)

![image-20221114204833210](../../images/image-20221114204833210.png)

命令行读取参数

```perl
java -jar xxx.jar --host 192.168.10.130 --port 7777
```

IDEA中读取参数

![image-20221114205115372](../../images/image-20221114205115372.png)

![image-20221114205153543](../../images/image-20221114205153543.png)

Flink页面

![image-20221114205405687](../../images/image-20221114205405687.png)

![image-20221114210236952](../../images/image-20221114210236952.png)

![image-20221114210134422](../../images/image-20221114210134422.png)

![image-20221114210426792](../../images/image-20221114210426792.png)

Linux主机先开启nc再提交作业，查看任务

![image-20221114210409589](../../images/image-20221114210409589.png)

![image-20221114210530534](../../images/image-20221114210530534.png)

详细信息

![image-20221114210622383](../../images/image-20221114210622383.png)

![image-20221114210715280](../../images/image-20221114210715280.png)

![image-20221114210726139](../../images/image-20221114210726139.png)

![image-20221114210735970](../../images/image-20221114210735970.png)

取消作业

![image-20221114211105191](../../images/image-20221114211105191.png)

![image-20221114211115378](../../images/image-20221114211115378.png)

##### 3.2.3.2 命令行提交

```perl
先打开nc工具
nc -lk 7777

cd flink-1.13.0/
flink run -m 192.168.10.130:8081 -c org.neptune.wc.StreamWordCount Flink-1.0-SNAPSHOT.jar
任务已提交Ctrl+C不影响
flink list查看任务


```

![image-20221114212653509](../../images/image-20221114212653509.png)

![image-20221114211607531](../../images/image-20221114211607531.png)

==flink的log目录下有当前任务的日志输出==

![image-20221114212844428](../../images/image-20221114212844428.png)

### 3.2.2 单作业模式部署

Flink 本身无法直接以单作业方式启动集群，一般需要借助一些资源管理平台。所以 Flink 的独立（Standalone）集群并==不支持==单作业模式部署。

### 3.2.3 应用模式部署

应用模式下不会提前创建集群，所以不能调用 start-cluster.sh 脚本。我们可以使用同样在bin 目录下的standalone-job.sh 来创建一个 JobManager。

具体步骤如下：

1. 进入到 Flink 的安装路径下，将应用程序的 jar 包放到 lib/目录下。

```perl
cp ./FlinkTutorial-1.0-SNAPSHOT.jar lib/
```

2. 执行以下命令，启动 JobManager。

```perl
#这里我们直接指定作业入口类，脚本会到 lib 目录扫描所有的 jar 包。
./bin/standalone-job.sh start --job-classname com.atguigu.wc.StreamWordCount
```

3. 同样是使用 bin 目录下的脚本，启动 TaskManager。

```perl
./bin/taskmanager.sh start
```

4. 如果希望停掉集群，同样可以使用脚本，命令如下。

```perl
./bin/standalone-job.sh stop
./bin/taskmanager.sh stop
```

### 3.3.4 高可用(High Availability )

 JobManager 做主备冗余，这就是所谓的高可用（High Availability，简称 HA）。

让集群在任何时候都有一个主 JobManager 和多个备用 JobManagers

![image-20221114220207267](../../images/image-20221114220207267.png)

1. 进入 Flink 的安装路径下的 conf 目录下，修改配置文件: flink-conf.yaml，增加如下配置。

```yaml
high-availability: zookeeper
high-availability.storageDir: hdfs://hadoop102:9820/flink/standalone/ha
high-availability.zookeeper.quorum: 
hadoop102:2181,hadoop103:2181,hadoop104:2181
high-availability.zookeeper.path.root: /flink-standalone
high-availability.cluster-id: /cluster_atguigu
```

2. 修改配置文件: masters，配置备用 JobManager 列表。

```perl
hadoop102:8081
hadoop103:8081
```

3. 分发修改后的配置文件到其他节点服务器。

```perl
xsync /opt/module/flink-1.13.0
```

4）在`/etc/profile.d/my_env.sh`中配置环境变量

```perl
export HADOOP_CLASSPATH=`hadoop classpath`
```

注意: 

* 需要提前保证 HAOOP_HOME 环境变量配置成功

* 分发到其他节点

具体部署方法如下：

1. 首先启动 HDFS 集群和 Zookeeper 集群。

2. 执行以下命令，启动 standalone HA 集群。

```perl
bin/start-cluster.sh
```

3. 可以分别访问两个备用 JobManager 的 Web UI 页面。

http://hadoop102:8081

http://hadoop103:8081

4. 在 zkCli.sh 中查看谁是 leader。

```perl
[zk: localhost:2181(CONNECTED) 1] get /flink-standalone/cluster_atguigu/leader/rest_server_lock
```

杀死 hadoop102 上的 Jobmanager, 再看 leader。

```perl
[zk: localhost:2181(CONNECTED) 7] get /flink-standalone/cluster_atguigu/leader/rest_server_lock
```

**注意: 不管是不是 leader，从 WEB UI 上是看不到区别的, 都可以提交应用。**

## 3.3 YARN 模式

客户端把 Flink 应用提交给 Yarn 的 ResourceManager, Yarn 的 ResourceManager 会向 Yarn 的 NodeManager 申请容器。在这些容器上，Flink 会部署JobManager 和 TaskManager 的实例，从而启动集群。Flink 会根据运行在 JobManger 上的作业所需要的 Slot 数量动态分配 TaskManager 资源。

### 3.3.1 环境准备

1. 下载并解压安装包，并将解压后的安装包重命名为 flink-1.13.0-yarn，本节的相关操作都将默认在此安装路径下执行。

```perl
tar -zxvf flink-1.13.0-bin-scala_2.12.tgz -C /opt/module/
mv flink-1.13.0 flink-1.13.0-yarn
```

1. 配置环境变量，增加环境变量配置如下：

```shell
sudo vim /etc/profile.d/my_env.sh

HADOOP_HOME=/opt/module/hadoop-3.1.3/
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
export HADOOP_CONF_DIR=${HADOOP_HOME}/etc/hadoop
export HADOOP_CLASSPATH=`hadoop classpath`

source /etc/profile
```

**这里必须保证设置了环境变量 HADOOP_CLASSPATH。**

3. 启动 Hadoop 集群，包括 HDFS 和 YARN。

```perl
start-dfs.sh
start-yarn.sh
```

```shell
[atguigu@hadoop102 module]$ myhadoop.sh start
 =================== 启动 hadoop集群 ===================
 --------------- 启动 hdfs ---------------
Starting namenodes on [hadoop102]
Starting datanodes
Starting secondary namenodes [hadoop104]
 --------------- 启动 yarn ---------------
Starting resourcemanager
Starting nodemanagers
 --------------- 启动 historyserver ---------------
[atguigu@hadoop102 module]$
[atguigu@hadoop102 module]$ jpsall
=============== hadoop102 ===============
8528 JobHistoryServer
8309 NodeManager
7917 DataNode
7743 NameNode
8623 Jps
=============== hadoop103 ===============
4688 ResourceManager
4489 DataNode
4825 NodeManager
5195 Jps
=============== hadoop104 ===============
97876 DataNode
97999 SecondaryNameNode
98095 NodeManager
98239 Jps
[atguigu@hadoop102 module]$

```

4. 进入 conf 目录，修改 flink-conf.yaml 文件，修改以下配置，这些配置项的含义在进行 Standalone 模式配置的时候进行过讲解，若在提交命令中不特定指明，这些配置将作为默认配置。

```perl
cd /opt/module/flink-1.13.0-yarn/conf/
vim flink-conf.yaml
jobmanager.memory.process.size: 1600m
taskmanager.memory.process.size: 1728m
taskmanager.numberOfTaskSlots: 8
parallelism.default: 1
```

### 3.3.2 会话模式部署

YARN 的会话模式与独立集群略有不同，需要首先申请一个 YARN 会话（YARN session）来启动 Flink 集群。

```java
        //从参数中提取主机名和端口号
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        String hostname = parameterTool.get("host");
        int port = parameterTool.getInt("port");
```

#### 3.3.2.1 启动集群

* 启动 hadoop 集群(HDFS, YARN)。

* 执行脚本命令向 YARN 集群申请资源，开启一个 YARN 会话，启动 Flink 集群。

  ```perl
  yarn-session.sh -nm test
  ```

可用参数解读：

* `-d`：分离模式，如果你不想让 Flink YARN 客户端一直前台运行，可以使用这个参数，即使关掉当前对话窗口，YARN session 也可以后台运行。

* `-jm(--jobManagerMemory)`：配置 JobManager 所需内存，默认单位 MB。

* `-nm(--name)`：配置在 YARN UI 界面上显示的任务名。

* `-qu(--queue)`：指定 YARN 队列名。

* `-tm(--taskManager)`：配置每个 TaskManager 所使用内存。

![image-20221114230300513](../../images/image-20221114230300513.png)

**注意：Flink1.11.0 版本不再使用-n 参数和-s 参数分别指定 TaskManager 数量和 slot 数量**，

YARN 的会话模式也不会把集群资源固定， 会按照需求动态分配 TaskManager 和 slot。

YARN Session 启动之后会给出一个 web UI 地址以及一个 YARN application ID，用户可以通过 web UI 或者命令行两种方式提交作业。

```shell
2022-11-15 09:10:09,099 INFO  org.apache.flink.runtime.util.config.memory.ProcessMemoryUtils [] - The derived from fraction jvm overhead memory (160.000mb (167772162 bytes)) is less than its min value 192.000mb (201326592 bytes), min value will be used instead
2022-11-15 09:10:09,113 INFO  org.apache.flink.yarn.YarnClusterDescriptor                  [] - Submitting application master application_1668474185976_0001
2022-11-15 09:10:09,496 INFO  org.apache.hadoop.yarn.client.api.impl.YarnClientImpl        [] - Submitted application application_1668474185976_0001
2022-11-15 09:10:09,496 INFO  org.apache.flink.yarn.YarnClusterDescriptor                  [] - Waiting for the cluster to be allocated
2022-11-15 09:10:09,499 INFO  org.apache.flink.yarn.YarnClusterDescriptor                  [] - Deploying cluster, current state ACCEPTED
2022-11-15 09:10:20,640 INFO  org.apache.flink.yarn.YarnClusterDescriptor                  [] - YARN application has been deployed successfully.
2022-11-15 09:10:20,642 INFO  org.apache.flink.yarn.YarnClusterDescriptor                  [] - Found Web Interface hadoop103:44391 of application 'application_1668474185976_0001'.
JobManager Web Interface: http://hadoop103:44391
```

http://hadoop103:44391 为flink界面

![image-20221115091839150](../../images/image-20221115091839150.png)

#### 3.3.2.2 提交作业

##### 3.3.2.2.1 页面提交

这种方式比较简单，与上文所述 Standalone 部署模式基本相同。

##### 3.3.2.2.2 命令行提交

```perl
#提交任务前先打卡nc
[atguigu@hadoop102 ~]$ nc -lk 7777
```

1. 将 Standalone 模式讲解中打包好的任务运行 JAR 包上传至集群

2. 执行以下命令将该任务提交到已经开启的 Yarn-Session 中运行。

```perl
flink run -c org.neptune.wc.StreamWordCount Flink-1.0-SNAPSHOT.jar --host hadoop102 --port 7777
```

客户端可以自行确定 JobManager 的地址，也可以通过-m 或者-jobmanager 参数指定JobManager 的地址，JobManager 的地址在 YARN Session 的启动页面中可以找到。

![image-20221115091645147](../../images/image-20221115091645147.png)

3. 任务提交成功后，可在 YARN 的 Web UI 界面查看运行情况。

![image-20221115091718445](../../images/image-20221115091718445.png)

从图中可以看到创建的 Yarn-Session 实际上是一个 Yarn 的Application，并且有唯一的 Application ID。

4. 也可以通过 Flink 的 Web UI 页面查看提交任务的运行情况。

![image-20221115091621094](../../images/image-20221115091621094.png)

### 3.3.3 单作业模式部署

在 YARN 环境中，由于有了外部平台做资源调度，所以我们也可以==直接向 YARN 提交一个单独的作业==，从而启动一个 Flink 集群。

1. 执行命令提交作业

```perl
bin/flink run -d -t yarn-per-job -c com.atguigu.wc.StreamWordCount FlinkTutorial-1.0-SNAPSHOT.jar
或
#注意这里是通过参数-m yarn-cluster 指定向 YARN 集群提交任务。
bin/flink run -m yarn-cluster -c com.atguigu.wc.StreamWordCount FlinkTutorial-1.0-SNAPSHOT.jar
```

2. 可在 YARN 的 ResourceManager 界面/ Flink Web UI 页面进行监控查看执行情况

3. 可以使用命令行查看或取消作业

```perl
./bin/flink list -t yarn-per-job -Dyarn.application.id=application_XXXX_YY

./bin/flink cancel -t yarn-per-job -Dyarn.application.id=application_XXXX_YY <jobId>

这里的 application_XXXX_YY 是当前应用的 ID，<jobId>是作业的 ID。注意如果取消作业，整个 Flink 集群也会停掉。
```

### 3.3.4 应用模式部署

与单作业模式类似，直接执行 flink run-application 命令即可。

1. 执行命令提交作业。

```perl
bin/flink run-application -t yarn-application -c com.atguigu.wc.StreamWordCount FlinkTutorial-1.0-SNAPSHOT.jar
```

2. 在命令行中查看或取消作业。

```perl
./bin/flink list -t yarn-application -Dyarn.application.id=application_XXXX_YY

./bin/flink cancel -t yarn-application -Dyarn.application.id=application_XXXX_YY <jobId>
```

3. 也可以通过 yarn.provided.lib.dirs 配置选项指定位置，将 jar 上传到远程。

```perl
./bin/flink run-application -t yarn-application -Dyarn.provided.lib.dirs="hdfs://myhdfs/my-remote-flink-dist-dir" hdfs://myhdfs/jars/my-application.jar
```

**这种方式下 jar 可以预先上传到 HDFS，而不需要单独发送到集群，这就使得作业提交更加轻量了**

### 3.3.5 高可用

YARN 模式的高可用和独立模式（Standalone）的高可用原理不一样。

* Standalone 模式中, 同时启动多个 JobManager, 一个为“领导者”（leader），其他为“后备”（standby）, 当 leader 挂了, 其他的才会有一个成为 leader。

* YARN 的高可用是只启动一个 Jobmanager, 当这个 Jobmanager 挂了之后, YARN 会再次启动一个, 所以其实是利用的 YARN 的重试次数来实现的高可用。

1. 在 yarn-site.xml 中配置

```xml
<property>
 <name>yarn.resourcemanager.am.max-attempts</name>
 <value>4</value>
 <description>
 The maximum number of application master execution attempts.
 </description>
</property>
```

2. 分发并重启 YARN。

3. 在 flink-conf.yaml 中配置

```yaml
yarn.application-attempts: 3
high-availability: zookeeper
high-availability.storageDir: hdfs://hadoop102:9820/flink/yarn/ha
high-availability.zookeeper.quorum: 
hadoop102:2181,hadoop103:2181,hadoop104:2181
high-availability.zookeeper.path.root: /flink-yarn
```

3. 启动 yarn-session

4. 杀死 JobManager, 查看复活情况。

**注意: yarn-site.xml 中配置的是 JobManager 重启次数的上限, flink-conf.xml 中的次数应该小于这个值。**

## 3.4 k8s 模式

容器化部署时目前业界很流行的一项技术，基于Docker镜像运行能够让用户更加方便地对应用进行管理和运维.容器管理工具中最为流行的就是 Kubernetes(kSs) ，而Flink也在最近的版本中支持了k8s部署模式。

### 3.4.1 搭建Kurbernetes集群

### 3.4.2 配置各组件的yaml文件

在k8s上构建Flink Session Cluster，雷要将Flink集群的组件对应的docker镜像分别在k8s 上启动，包括obManager、TaskManager、JobManagerService三个镜像服务。每个镜像服务都可以从中央镜像仓库中获取。

### 3.4.3 启动 Flink Session Cluster

```perl
#启动jobmonager-service服务
kubectl create -f jobmanager-service.yaml

#启动jobmanager--deployment服务
kubectl create -f jobmanager-deployment.yaml

#启动taskmanoger-depl oymert 服务
kubectl create -f taskmanager-deployment.yaml
```

### 3.4.4 访问Flink UI页面

集群启动后，就可以通过JobManagerservicers中配置的webUI端口，用浏览器输入以下url来访问Flink UI页面了;

```perl
http://{JobManagerHost:Port}/api/v1/namespaces/default/services/fink-jobmanager:ui/proxy
```

# 4 Flink运行架构

## 4.1 系统架构

### 4.1.1 整体构成

两大组件

* 作业管理器（JobManger）—Master，负责管理调度，所以在不考虑高可用的情况下只能有一个
* 任务管理器（TaskManager）—Worker、Slave，负责执行任务处理数据，所以可以有一个或多个

![image-20221115105220698](../../images/image-20221115105220698.png)

* 客户端只负责作业的提交。
  * 调用程序的 main 方法，将代码转换成数据流图（Dataflow Graph）并生成作业图（JobGraph），发送给 JobManager。
  * 提交后，任务的执行就跟客户端没有关系了，可以在客户端选择断开与 JobManager 的连接, 也可以继续保持连接。

之前在命令行提交作业时，加上的`-d`参数，就是表示分离模式（detached mode)，也就是断开连接。

### 4.1.2 作业管理器（JobManager）

JobManager 是集群任务管理和调度的核心，控制应用执行的主进程。

* 每个应用JobManager 唯一
* 高可用（HA）场景， 一个leader，多个备用节点standby

JobManger 又包含 3 个不同的组件。

#### 4.1.2.1 JobMaster
* JobMaster 最核心的组件，负责处理单独的作业（Job）
  *  JobMaster和Job 一一对应的，集群可以有多个Job, 每个 Job 都有一个自己的 JobMaster。
  *  作业提交时，JobMaster 会先接收到客户端提交的应用
     * Jar 包
     * 数据流图（dataflow graph）
     * 作业图（JobGraph）
  * 把作业图（JobGraph） 转换成一个物理层面的数据流图—执行图（ExecutionGraph），它包含了所有可以并发执行的任务
  * 向资源管理器（ResourceManager）发出请求，申请执行任务必要的资源、
  * 获取到足够的资源，就会将执行图分发到真正运行它们的 TaskManager 上
  * 运行过程中，JobMaster 会负责所有需要中央协调的操作，比如检查点（checkpoints）的协调

#### 4.1.2.2 资源管理器（ResourceManager）

* ResourceManager 主要负责资源的分配和管理，在 Flink 集群中只有一个
* 资源主要是指 TaskManager 的任务槽（task slots）
  * 任务槽：用来执行计算的一组 CPU 和内存资源。每一个任务（Task）都需要分配到一个 slot 上执行

* Flink 内置的 ResourceManager 和其他资源管理平台（比如 YARN）的ResourceManager 区别
  * Flink 的 ResourceManager 只能分发可用 TaskManager 的任务槽，不能单独启动新TaskManager
  * 资源管理平台的ResourceManager 
    * 可以分配有空闲槽位的 TaskManager 给 JobMaster
    * 没有足够的任务槽时，可向资源提供平台发起会话，请求提供启动 TaskManager 进程的容器
    * 负责停掉空闲的 TaskManager，释放计算资源

#### 4.1.2.3 分发器（Dispatcher）

* 主要负责提供一个 REST 接口，用来提交应用
* 为每一个新提交的作业启动一个新的 JobMaster 组件
* Dispatcher 也会启动一个 Web UI，用来方便地展示和监控作业执行的信息
* Dispatcher 在架构中并不是必需的，在不同的部署模式下可能会被忽略掉

### 4.1.3 任务管理器（TaskManager）

TaskManager 是 Flink 中的工作进程，数据流的具体计算就是它来做的（Worker）。

* Flink 集群中必须至少有一个 TaskManager，分布式计算会有多个 TaskManager 
* 每一个 TaskManager 都包含了一定数量的任务槽（task slots）
* Slot是资源调度的最小单位，slot 的数量限制了 TaskManager 能够并行处理的任务数量

作业启动后

* TaskManager 会向资源管理器注册它的 slots
* 收到资源管理器的指令后，TaskManager 就会将一个或者多个槽位提供给 JobMaster 调用
* JobMaster 就可以分配任务来执行了
* 在执行过程中，TaskManager 可以缓冲数据，还可以跟其他运行同一应用的 TaskManager交换数据。

## 4.2 作业提交流程

### 4.2.1 高层级抽象视角

![image-20221115165607936](../../images/image-20221115165607936.png)

1. 客户端（App）通过分发器提供的 REST 接口，将作业提交给JobManager。

2. 由分发器启动 JobMaster，并将作业（包含 JobGraph）提交给 JobMaster。

3. JobMaster 将 JobGraph 解析为可执行的 ExecutionGraph，得到所需的资源数量，然后
   向资源管理器请求资源（slots）。

4. 资源管理器判断当前是否由足够的可用资源；如果没有，启动新的 TaskManager。

5. TaskManager 启动之后，向 ResourceManager 注册自己的可用任务槽（slots）。

6. 资源管理器通知 TaskManager 为新的作业提供 slots。

7. TaskManager 连接到对应的 JobMaster，提供 slots。

8. JobMaster 将需要执行的任务分发给 TaskManager。

9. TaskManager 执行任务，互相之间可以交换数据。

* 根据部署模式、集群环境不同（例如 Standalone、YARN、K8S 等），其中一些步骤可能会不同或被省略，也可能有些组件会运行在同一个 JVM 进程中。

* 独立集群环境的会话模式，就是需要先启动集群，如果资源不够，只能等待资源释放，而不会直接启动新的 TaskManager。

### 4.2.2 独立模式（Standalone）

除第 4 步不会启动 TaskManager，而且直接向已有的 TaskManager 要求资源，其他步骤与抽象流程一致。

![image-20221115175707502](../../images/image-20221115175707502.png)

### 4.2.3 YARN 集群

#### 4.2.3.1 会话（Session）模式

**会话模式需要先启动一个 YARN session，这个会话会创建一个 Flink 集群。**



![image-20221115180158067](../../images/image-20221115180158067.png)

除了**请求资源时要上报YARN 的资源管理器**，其他与所述抽象流程一样。

![image-20221115180215476](../../images/image-20221115180215476.png)

#### 4.2.3.2 单作业（Per-Job）模式

单作业模式Flink 集群不会预先启动，而是在提交作业时，才启动新的 JobManager。

区别在于**JobManager 的启动方式，以及省去了分发器**。

当第 2 步作业提交给JobMaster，之后的流程就与会话模式完全一样了。

![image-20221115180833740](../../images/image-20221115180833740.png)

#### 4.2.3.3 应用（Application）模式

* 应用模式与单作业模式的提交流程非常相似

* 初始提交给 YARN 资源管理器的不再是具体的作业，而是整个应用

* 一个应用中可能包含了多个作业，这些作业都将在 Flink 集群中启动各自对应的 JobMaster

## 4.3 重要概念

### 4.3.1 数据流图（Dataflow Graph）

* Flink 是流式计算框架。它的程序结构，其实就是定义了一连串的处理操作，每一个数据输入之后都会依次调用每一步计算。

* 在 Flink 代码中，我们定义的每一个处理转换操作都叫作**算子（Operator）**，所以我们的程序可以看作是一串算子构成的管道，数据则像水流一样有序地流过。
* 所有的 Flink 程序都可以归纳为由三部分构成：Source、Transformation 和 Sink。
  * `Source` ——源算子，负责读取数据源。
  * `Transformation` ——转换算子，利用各种算子进行处理加工。
  * `Sink` ——下沉算子，负责数据的输出。

* Flink 程序映射成的算子，按照逻辑顺序连接在一起的**有向无环图（DAG）**叫**数据流图（dataflow graph）**or **逻辑数据流（logical dataflow)**
* 每一条数据流（dataflow）以一个或多个 source 算子开始，以一个或多个 sink 算子结束。

* 提交作业之后，打开 Flink 自带的 Web UI，点击作业就能看到对应的 dataflow
* 在大部分情况下，dataflow 中的算子，和程序中的转换运算是一一对应的关系。

以下数据流图可以看到 Source、Transformation、Sink 三部分。

![image-20221115211129142](../../images/image-20221115211129142.png)

### 4.3.2 并行度（Parallelism）

#### 4.3.2.1 并行计算

* 任务并行：不同的算子操作任务，分配到不同的节点上执行。

* 数据并行：一个算子被拆分成了多个并行的子任务（subtasks），分发到不同节点执行。

#### 4.3.2.2 并行度

* 一个特定算子的子任务（subtask）的个数被称之为其并行度（parallelism）。

* 包含并行子任务的数据流，就是并行数据流，它需要多个分区（stream partition）来分配并行任务。

* **一个流程序的并行度，就是其所有算子中最大的并行度。**
* 一个程序中，不同的算子可能具有不同的并行度。

如图 4-8 所示，当前数据流中有 source、map、window、sink 四个算子，除最后 sink，其他算子的并行度都为 2。整个程序包含了 7 个子任务，至少需要 2 个分区来并行执行。这段流处理程序的并行度就是 2。

![image-20221115214636214](../../images/image-20221115214636214.png)

#### 4.3.2.3 并行度的设置

在 Flink 中，可以用不同的方法来设置并行度，它们的有效范围和优先级别也是不同的。

* 代码中设置

  * 在算子后跟着调用 setParallelism()方法

    ```java
    //只针对当前算子有效
    stream.map(word -> Tuple2.of(word, 1L)).setParallelism(2);
    ```

  * 直接调用执行环境的 setParallelism()方法，全局设定并行度

    ```java
    //代码中所有算子有效，无法动态扩容，一般不使用
    env.setParallelism(2);
    ```

* 提交应用时设置

  * 使用 flink run 提交应用时，增加`-p `参数来指定当前应用程序执行的并行度，类似于执行环境的全局设置

    ```perl
    flink run -p 2 -c org.neptune.wc.StreamWordCount Flink-1.0-SNAPSHOT.jar
    ```

  * 在 Web UI 上提交作业时在对应输入框中直接添加并行度。

* 在配置文件flink-conf.yaml 中设置`parallelism.default`，对整个集群有效

  ```yaml
  parallelism.default: 2
  ```

  * 初始值为 1。
  * 代码和应用提交未设置，会采用此并行度。

* 开发环境中，没有配置文件，默认并行度就是当前机器的 CPU 核心数。

**优先级**

1. 代码中是否单独指定并行度
2. 代码中执行环境全局设置的并行度。
3. 提交时-p 参数指定的并行度。
4. 集群配置文件中的默认并行度。

**算子的并行度有时会受到自身具体实现的影响。**

对于本身就是非并行的 Source 算子（如socketTextStream），无论怎么设置，它在运行时的并行度都是 1

==建议在代码中只针对算子设置并行度，不设置全局并行度，方便提交作业时进行动态扩容。==

### 4.3.3 算子链（Operator Chain）

#### 4.3.3.1 算子间的数据传输

* 一对一（One-to-one，forwarding），数据流都是相同分区的算子，无shuffle，类似于 Spark 中的窄依赖
* 重分区（Redistributing），数据流包含不同分区算子，有shuffle，类似于 Spark 中的宽依赖

![image-20221115222248556](../../images/image-20221115222248556.png)

#### 4.3.3.2 合并算子链

相同分区的算子合并成一个算子，的技术被称为算子链（Operator Chain）。

![image-20221115222200650](../../images/image-20221115222200650.png)

### 4.3.4 作业图（JobGraph）与执行图（ExecutionGraph）

Flink 中任务调度执行的图，按照生成顺序可以分成四层：

逻辑流图（StreamGraph）→ 作业图（JobGraph）→ 执行图（ExecutionGraph）→ 物理图（Physical Graph）

==重点： 作业图（JobGraph）和执行图（ExecutionGraph）==

#### 4.3.4.1 逻辑流图（StreamGraph）

* 根据DataStream API 编写的代码生成的最初的 DAG 图，用来表示程序的拓扑结构。
* 一般在客户端完成。

#### 4.3.4.2 作业图（JobGraph）

* StreamGraph 经过优化后生成作业图，是提交给 JobManager 的数据结构。
* 优化：将多个符合条件的节点合并成一个任务节点，形成算子链，减少数据交换的消耗。
* 一般在客户端完成
* 作业提交时传递给 JobMaster。

#### 4.3.4.3 执行图（ExecutionGraph）

* JobMaster 收到 JobGraph 后，会根据它来生成执行图。
* 是作业图 的并行化版本，是调度层最核心的数据结构。
* 与作业图 最大的区别是按照并行度对并行子任务进行了拆分，并明确了任务间数据传输的方式。

#### 4.3.4.4 物理图（Physical Graph）

* JobMaster 生成执行图后，分发给 TaskManager生成物理图。
* 这只是具体执行层面的图，并不是一个具体的数据结构。
* 在执行图的基础上，进一步确定数据存放的位置和收发的具体方式。
* TaskManager 根据物理图对传递来的数据进行处理计算。

### 4.3.5 任务（Tasks）和任务槽（Task Slots）

#### 4.3.5.1 任务槽（Task Slots）

* ==Flink 中每一个 TaskManager（worker）都是一个 JVM 进程==

* ==它可以启动多个独立的线程，来并行执行多个子任务（subtask）。==

每个任务槽（task slot）是 TaskManager 拥有计算**资源的一个固定大小的子集**。

**这些资源就是用来独立执行一个子任务的。**

* 下图中，一个 TaskManager 有三个 slot，那么它会将管理的==内存==平均分成三份，每个 slot 独自占据一份。

* 这样，在 slot 上执行一个子任务时，就不需要跟来自其他作业的任务去竞争内存资源了。

* 所以现在只要 2 个 TaskManager，就可以并行处理分配好的 5 个任务了

![image-20221115224804661](../../images/image-20221115224804661.png)

#### 4.3.5.2 任务槽数量的设置

在集群配置文件flink-conf.yaml设置`taskmanager.numberOfTaskSlots`

```yaml
taskmanager.numberOfTaskSlots: 8
```

通过调整 slot 的数量，可以控制子任务之间的隔离级别。

* TaskManager
  * 隔离级别高，进程隔离，完全独立运行
  * 彼此间的影响可以降到最小

* slot
  * 隔离级别低，线程隔离，可共享 TCP 连接和心跳消息，共享数据集和数据结构
  * 减少运行开销，提升性能
  * 仅隔离内存，不会涉及 CPU 的隔离

**具体应用时，将 slot 数量配置为机器的 CPU 核心数，可避免不同任务之间对 CPU 的竞争。**

这也是开发环境默认并行度设为机器 CPU 数量的原因。

#### 4.3.5.3 任务对任务槽的共享

==slot共享：同一分区的所有算子在同一个slot执行==

每个任务节点的并行子任务一字排开，占据不同的 slot

不同的任务节点的子任务可以共享 slot。

一个 slot 中，所有任务都在这里执行，我们把它叫作保存了整个作业的运行管道（pipeline）。

![image-20221115230818414](../../images/image-20221115230818414.png)

**slot共享优点**

* 当资源密集型和非密集型的任务同时放到一个 slot 中，可以自行分配资源占用比例，**保证资源充分利用**
* 允许保存完整的作业管道，某个 TaskManager出现故障宕机，其他节点不受影响，**作业的任务可继续执行**

**slot 共享组（SlotSharingGroup）**

Flink 默认允许 slot 共享，如果希望某个算子对应的任务完全独占一个 slot，或者只有某一部分算子共享 slot。

可以通过设置**slot 共享组（SlotSharingGroup）**手动指定：

```java
.map(word -> Tuple2.of(word, 1L)).slotSharingGroup(“1”);
```

* 只有属于同一个 slot 共享组的子任务，才会开启 slot 共享

* 不同组之间的任务是完全隔离的，必须分配到不同的 slot 上。

* 需要的 slot 数量，就是各个 slot共享组最大并行度的总和。

#### 4.3.5.4 任务槽和并行度的关系

整个流处理程序的并行度 = 所有算子并行度中最大的那个 = 运行程序需要的 slot 数量

==结论：充分利用资源时并行度=任务槽数量==

举例：

slot数量为9

source→ flatMap→ reduce→ sink

source→ flatMap合并为一个算子

3个任务节点

**并行度默认是1**

![image-20221115233236120](../../images/image-20221115233236120.png)

**提交作业时，并行度设置为2**

![image-20221115233308592](../../images/image-20221115233308592.png)

**直接将并行度设置为9（与slot一样）**

![image-20221115233409988](../../images/image-20221115233409988.png)



**输出是写入文件，不希望并行写入多个文件，讲 sink 算子的并行度设置为 1。**

![image-20221115233417755](../../images/image-20221115233417755.png)

# 5 DataStreamAPI

一个 Flink 程序，其实就是对 DataStream 的各种转换。具体来说，代码基本上都由以下几部分构成

* 获取执行环境（execution environment）

* 读取数据源（source）

* 定义基于数据的转换操作（transformations）

* 定义计算结果的输出位置（sink）

* 触发程序执行（execute）

其中，获取环境和触发执行，都可以认为是针对执行环境的操作。

![image-20221116085245328](../../images/image-20221116085245328.png)

## 5.1 执行环境

Flink 程序可以在各种上下文环境中运行：我们可以在本地 JVM 中执行程序，也可以提交到远程集群上运行。

提交作业时，必须先获取Flink 的运行环境（上下文），才能将具体的任务调度到不同的 TaskManager 执行。

### 5.1.1 创建执行环境

#### 5.1.1.1 **getExecutionEnvironment**

会根据当前运行的上下文直接得到正确的结果 

* 独立运行 ==> 返回本地执行环境
* 提交到集群执行 ==> 返回集群执行环境

```java
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
```

#### 5.1.1.2 createLocalEnvironment

返回本地执行环境。

可选参数一个

* 传入参数，可指定默认的并行度；

* 不传入，默认并行度为本地的 CPU 核心数。

```java
StreamExecutionEnvironment localEnv = StreamExecutionEnvironment.createLocalEnvironment();
```

#### 5.1.1.3 createRemoteEnvironment

返回集群执行环境。

必选参数三个

*  JobManager 的主机名
*  JobManager 的端口号
* 在集群中运行的 Jar 包。

```java
StreamExecutionEnvironment remoteEnv = StreamExecutionEnvironment
 .createRemoteEnvironment(
 "host", // JobManager 主机名
 1234, // JobManager 进程端口号
 "path/to/jarFile.jar" // 提交给 JobManager 的 JAR 包
);
```

### 5.1.2 执行模式(Execution Mode)

1.12.0 版本起，Flink 实现了 API 上的流批统一。

DataStream API 可以支持不同的执行模式，通过简单的设置就可以让Flink 程序在流处理和批处理之间切换。

因此弃用了DataSet API 。

```java
// 批处理环境
ExecutionEnvironment batchEnv = ExecutionEnvironment.getExecutionEnvironment();
// 流处理环境
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
```

* 流执行模式（STREAMING）

  一般用于需要持续实时处理的无界数据流。默认情况下，程序使用的就是 STREAMING 执行模式。

* 批执行模式（BATCH）

  专门用于批处理的执行模式,  类似于 MapReduce 框架。对于不会持续计算的有界数据，用这种模式处理。

* 自动模式（AUTOMATIC）

  程序根据输入数据源是否有界，来自动选择执行模式。

**BATCH模式的配置方法**

* 命令行配置

  在提交作业时，增加 `execution.runtime-mode` 参数，指定值为 BATCH。

```perl
bin/flink run -Dexecution.runtime-mode=BATCH ...
```

* 代码配置

  基于执行环境调用 setRuntimeMode 方法，传入 BATCH 模式

```java
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

env.setRuntimeMode(RuntimeExecutionMode.BATCH);
```

==建议: 不要在代码中配置，而是使用命令行，与设置并行度类似，动态拓展==

**什么时候选择** **BATCH** **模式**

* 处理**批量数据**时用 BATCH 模式，数据有界时，直接输出结果会更加高效

* 处理流式数据时用 STREAMING模式，数据无界时, 只有 STREAMING 模式才能处理持续的数据流。

### 5.1.3 触发程序执行

* 输出（sink）操作并不代表程序已经结束。

* main()方法被调用时，只定义了每个执行操作并添加到数据流图中，没有处理数据——因为数据可能还没来。

* Flink 是由事件驱动的，只有数据到来，才会触发真正的计算——延迟执行or懒执行（lazy execution）。

* 需要调用执行环境的 execute()方法，触发程序执行。等作业完成，返回执行结果（JobExecutionResult）。

```java
env.execute();
```

## 5.2 源算子（Source）

![image-20221116094708081](../../images/image-20221116094708081.png)

Flink可以从各种来源获取数据，然后构建 DataStream 进行转换处理

程序的输入端，调用执行环境的 addSource()方法

```java
DataStream<String> stream = env.addSource(...);
```

### 5.2.1 前置准备

准备一个实体类Envent，有以下特点

* 类是公有（public）的

* 有一个无参的构造方法

* 所有属性都是公有（public）的

* 所有属性的类型都是可以序列化的

Flink 会把这样的类作为一种特殊的 POJO 数据类型来对待，方便数据的解析和序列化。

![image-20221116164322525](../../images/image-20221116164322525.png)

```java
import java.sql.Timestamp;
public class Event {
 public String user;
 public String url;
 public Long timestamp;
 public Event() {
 }
 public Event(String user, String url, Long timestamp) {
 this.user = user;
 this.url = url;
 this.timestamp = timestamp;
 }
 @Override
 public String toString() {
 return "Event{" +
 "user='" + user + '\'' +
 ", url='" + url + '\'' +
 ", timestamp=" + new Timestamp(timestamp) +
 '}';
 }
}
```

### 5.2.2 从集合中读取数据

**将数据临时存储到内存中，形成特殊的数据结构后，作为数据源使用，一般用于测试**

```java
    public static void main(String[] args) throws Exception {
        
        //创建环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //直接创建一个 Java 集合，然后调用执行环境的fromCollection 方法进行读取。
        ArrayList<Event> clicks = new ArrayList<>();
        clicks.add(new Event("Mary", "./home", 1000L));
        clicks.add(new Event("Bob", "./cart", 2000L));
        DataStream<Event> stream = env.fromCollection(clicks);
        stream.print();

        //不构建集合，直接将元素列举出来，调用 fromElements 方法进行读取数据：
        DataStreamSource<Event> stream2 = env.fromElements(
                new Event("Mary", "./home", 1000L),
                new Event("Bob", "./cart", 2000L)
        );
        stream2.print();

        //启动执行
        env.execute();

    }
```

### 5.2.3 从文件中读取数据

**读取日志文件，批处理中最常见的读取方式。**

```java
DataStream<String> stream = env.readTextFile("clicks.csv");
```

* 目录、文件

* 路径可以是相对路径、绝对路径
  * 相对路径：
    * 系统属性user.dir 获取路径
    * idea 下是 project 的根目录
    * standalone 模式下是集群节点根目录

* hdfs 目录（hdfs://...）需要加入依赖 

```xml
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-client</artifactId>
            <version>3.1.3</version>
            <scope>provided</scope>
        </dependency>
```

### 5.2.4 从 Socket 读取数据

**吞吐量小、稳定性差，一般用于测试**

```java
DataStream<String> stream = env.socketTextStream("localhost", 7777);
```

### 5.2.5 从 Kafka 读取数据

Kafka 分布式消息传输队列、高吞吐、易于扩展的消息系统

消息队列的传输方式，与流处理完全一致，Kafka 和 Flink 是当前处理流式数据的双子星。

实时流处理应用中，由 Kafka 进行数据的收集和传输，Flink 进行分析计算的架构已经成为众多企业的首选。

![image-20221116195452493](../../images/image-20221116195452493.png)

* Flink 官方提供的 Kafka 连接器，会自动跟踪最新版本的 Kafka 客户端。

* 目前最新版本只支持 **0.10.0 版本以上**的 Kafka，

导入以下依赖

```xml
        <dependency>
            <groupId>org.apache.flink</groupId>
            <artifactId>flink-connector-kafka_${scala.binary.version}</artifactId>
            <version>${flink.version}</version>
        </dependency>
```

调用 env.addSource()，传入 FlinkKafkaConsumer 的对象实例

创建 FlinkKafkaConsumer 时需要传入三个参数：

* 第一个参数 topic，定义了从哪些主题中读取数据。可以是一个 topic，也可以是 topic列表，还可以是匹配所有想要读取的 topic 的正则表达式。当从多个 topic 中读取数据时，Kafka 连接器将会处理所有 topic 的分区，将这些分区的数据放到一条流中去。

* 第二个参数是一个 DeserializationSchema 或者 KeyedDeserializationSchema。Kafka 消息被存储为原始的字节数据，所以需要反序列化成 Java 或者 Scala 对象。上面代码中使用的 SimpleStringSchema，是一个内置的 DeserializationSchema，它只是将字节数组简单地反序列化成字符串。DeserializationSchema 和 KeyedDeserializationSchema 是公共接口，所以我们也可以自定义反序列化逻辑。

* 第三个参数是一个 Properties 对象，设置了 Kafka 客户端的一些属性。

```java
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

public class SourceKafkaTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.10.130:9092");
        properties.setProperty("group.id", "Neptune");
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("auto.offset.reset", "latest");
        DataStreamSource<String> stream = env.addSource(new
                FlinkKafkaConsumer<String>(
                "test",
                new SimpleStringSchema(),
                properties
        ));
        stream.print("Kafka");
        env.execute();
        
    }
}
```

### 5.2.6 自定义 Source

#### 5.2.6.1 SourceFunction 

**SourceFunction 接口定义的数据源，并行度只能为 1，大于 1 则会抛出异常。**

需要实现 SourceFunction 接口。主要重写两个关键方法：run() 和 cancel()。

* run()方法：使用运行时上下文对象（SourceContext）向下游发送数据；

* cancel()方法：通过标识位控制退出循环，来达到中断数据源的效果。

```java
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.neptune.pojo.Event;

import java.util.Calendar;
import java.util.Random;

public class ClickSource implements SourceFunction<Event> {
    // 声明一个布尔变量，作为控制数据生成的标识位
    private Boolean running = true;

    @Override
    public void run(SourceContext<Event> ctx) throws Exception {
        Random random = new Random(); // 在指定的数据集中随机选取数据

        String[] users = {"Mary", "Alice", "Bob", "Cary"};
        String[] urls = {"./home", "./cart", "./fav", "./prod?id=1",
                "./prod?id=2"};
        while (running) {
            ctx.collect(new Event(
                    users[random.nextInt(users.length)],
                    urls[random.nextInt(urls.length)],
                    Calendar.getInstance().getTimeInMillis()
            ));
            // 隔 1 秒生成一个点击事件，方便观测
            Thread.sleep(1000);
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
}
```

读取自定义source

```java
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.neptune.pojo.Event;

public class SourceCustom {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        //有了自定义的 source function，调用 addSource 方法
        DataStreamSource<Event> stream = env.addSource(new ClickSource());
        stream.print("SourceCustom");
        env.execute();
    }

}
```

#### 5.2.6.2 ParallelSourceFunction

自定义并行数据源

```java
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;

import java.util.Random;

public class ParallelSourceExample {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();
        env.addSource(new CustomSource()).setParallelism(2).print();
        env.execute();
    }

    public static class CustomSource implements ParallelSourceFunction<Integer> {
        private boolean running = true;
        private Random random = new Random();

        @Override
        public void run(SourceContext<Integer> sourceContext) throws Exception {
            while (running) {
                sourceContext.collect(random.nextInt());
            }
        }

        @Override
        public void cancel() {
            running = false;
        }
    }
}
```

### 5.2.7 Flink 支持的数据类型

* 基本类型

  * 所有 Java 基本类型及其包装类
  * Void、String、Date、BigDecimal 和 BigInteger。

* 数组类型

  * 基本类型数组（PRIMITIVE_ARRAY）和对象数组(OBJECT_ARRAY)

* 复合数据类型

  * Java 元组类型（TUPLE）： Flink内置元组类型，最多25 个字段（Tuple0~Tuple25），不支持空字段
  * Scala 样例类及 Scala 元组：不支持空字段
  * 行类型（ROW）：可以认为是具有任意个字段的元组,并支持空字段
  * POJO：Flink 自定义的类似于 Java bean 模式的类
    * 类是公共的（public）和独立的（standalone，没有非静态的内部类）
    * 类有一个公共的无参构造方法
    * 类中的所有字段是 public 且非 final 的
    * 有一个公共的 getter 和 setter 方法，这些方法需要符合 Java bean 的命名规范

* 辅助类型

  * Option、Either、List、Map 等

* 泛型类型（GENERIC）

  * 未按 POJO 类型的要求定义，会被 Flink 当作泛型类

  * 不是由 Flink 本身序列化的，而是由Kryo 序列化

**项目实践中，往往会将流处理程序中的元素类型定为 Flink 的==POJO 类型==**

类型提示（Type Hints）

* Flink 专门提供了 TypeHint 类，它可以捕获泛型的类型信息，并且一直记录下来

* 可以通过.returns()方法，明确地指定转换之后的 DataStream 里元素的类型。

```java
returns(new TypeHint<Tuple2<Integer, SomeType>>(){})
```

## 5.3 转换算子（Transformation）

![image-20221116214439875](../../images/image-20221116214439875.png)

