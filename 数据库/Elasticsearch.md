# 1.**Elasticsearch** 概述

## **1.1 Elasticsearch** 是什么

The Elastic Stack, 包括 Elasticsearch、Kibana、Beats 和 Logstash（也称为 ELK Stack）。能够安全可靠地获取任何来源、任何格式的数据，然后实时地对数据进行搜索、分析和可视化。

Elaticsearch，简称为 ES，ES 是一个**开源的高扩展的分布式全文搜索引擎**，是整个 Elastic Stack 技术栈的核心。它可以近乎实时的存储、检索数据；本身扩展性很好，可以扩展到上百台服务器，处理 PB 级别的数据。

## 1.2 全文搜索引擎

Google，百度类的网站搜索，它们都是根据网页中的关键字生成索引，在搜索的时候输入关键字，它们会将该关键字即索引匹配到的所有网页返回；还有常见的项目中应用日志的搜索等等。对于这些非结构化的数据文本，关系型数据库搜索不是能很好的支持。

在一些生产环境中，使用常规的搜索方式，性能是非常差的：

* 搜索的数据对象是大量的非结构化的文本数据。

* 文件记录量达到数十万或数百万个甚至更多。

* 支持大量基于交互式文本的查询。

* 需求非常灵活的全文搜索查询。

* 对高度相关的搜索结果的有特殊需求，但是没有可用的关系数据库可以满足。 

* 对不同记录类型、非文本数据操作或安全事务处理的需求相对较少的情况。

为了解决结构化数据搜索和非结构化数据搜索性能问题，就需要专业，健壮，强大的全文搜索引擎。

这里的全文搜索引擎指的是目前广泛应用的主流搜索引擎。它的工作原理是计算机索引程序通过扫描文章中的每一个词，对每一个词建立一个索引，指明该词在文章中出现的次数和位置，当用户查询时，检索程序就根据事先建立的索引进行查找，并将查找的结果反馈给用户的检索方式。这个过程类似于通过字典中的检索字表查字的过程。

## 1.3 Elasticsearch And Solr

目前市面上流行的搜索引擎软件，主流的就两款：**Elasticsearch** 和 **Solr**,这两款都是基于 Lucene 搭建的，可以独立部署启动的搜索引擎服务软件。由于内核相同，所以两者除了服务器安装、部署、管理、集群以外，对于数据的操作 修改、添加、保存、查询等等都十分类似。

![image-20221129212148300](C:\Users\hakuou\Documents\program\Git\images\image-20221129212148300.png)

## 1.4 **Elasticsearch** 应用案例

* **GitHub**: 2013 年初，抛弃了 Solr，采取 Elasticsearch 来做 PB 级的搜索。“GitHub 使用Elasticsearch 搜索 20TB 的数据，包括 13 亿文件和 1300 亿行代码”。

* **维基百科**：启动以 Elasticsearch 为基础的核心搜索架构

* **SoundCloud**：“SoundCloud 使用 Elasticsearch 为 1.8 亿用户提供即时而精准的音乐搜索服务”。

* **百度**：目前广泛使用 Elasticsearch 作为文本数据分析，采集百度所有服务器上的各类指标数据及用户自定义数据，通过对各种数据进行多维分析展示，辅助定位分析实例异常或业务层面异常。目前覆盖百度内部 20 多个业务线（包括云分析、网盟、预测、文库、直达号、钱包、风控等），单集群最大 100 台机器，200 个 ES 节点，每天导入 30TB+数据。

* **新浪**：使用 Elasticsearch 分析处理 32 亿条实时日志。

* **阿里**：使用 Elasticsearch 构建日志采集和分析体系。

* **Stack Overflow**：解决 Bug 问题的网站，全英文，编程人员交流的网站。

# 2. **Elasticsearch** **入门**

## **2.1 Elasticsearch** **安装**

Elasticsearch 的官方地址：https://www.elastic.co/cn/

Windows 版的 Elasticsearch 的安装很简单，解压即安装完毕

解压后，进入 bin 文件目录，点击 elasticsearch.bat 文件启动 ES 服务

==注意：9300 端口为 Elasticsearch 集群间组件的通信端口，9200 端口为浏览器访问的 http协议 RESTful 端口。==

打开浏览器（推荐使用谷歌浏览器），输入地址：http://localhost:9200，测试结果

![image-20221129211327940](C:\Users\hakuou\Documents\program\Git\images\image-20221129211327940.png)

Elasticsearch 是使用 java 开发的，且 7.8 版本的 ES 需要 JDK 版本 1.8 以上，默认安装包带有 jdk 环境，如果系统配置 JAVA_HOME，那么使用系统默认的 JDK，如果没有配置使用自带的 JDK，一般建议使用系统配置的 JDK。

双击启动窗口闪退，通过路径访问追踪错误，如果是“空间不足”，请修改config/jvm.options 配置文件

```powershell
# 设置 JVM 初始内存为 1G。此值可以设置与-Xmx 相同，以避免每次垃圾回收完成后 JVM 重新分配内存
# Xms represents the initial size of total heap space
# 设置 JVM 最大可用内存为 1G
# Xmx represents the maximum size of total heap space
-Xms1g
-Xmx1g
```

## **2.2 Elasticsearch** **基本操作**

### **2.2.1 RESTful**

REST 指的是一组架构约束条件和原则。满足这些约束条件和原则的应用程序或设计就是 RESTful。

Web 应用程序最重要的 REST 原则是，客户端和服务器之间的交互在请求之间是无状态的。从客户端到服务器的每个请求都必须包含理解请求所必需的信息。如果服务器在请求之间的任何时间点重启，客户端不会得到通知。此外，无状态请求可以由任何可用服务器回答，这十分适合云计算之类的环境。客户端可以缓存数据以改进性能。

在服务器端，应用程序状态和功能可以分为各种资源。资源是一个有趣的概念实体，它向客户端公开。资源的例子有：应用程序对象、数据库记录、算法等等。每个资源都使用 URI (Universal Resource Identifier) 得到一个唯一的地址。所有资源都共享统一的接口，以便在客户端和服务器之间传输状态。使用的是标准的 HTTP 方法，比如 GET、PUT、POST 和DELETE。

在 REST 样式的 Web 服务中，每个资源都有一个地址。资源本身都是方法调用的目标，方法列表对所有资源都是一样的。这些方法都是标准方法，包括 HTTP GET、POST、PUT、DELETE，还可能包括 HEAD 和 OPTIONS。简单的理解就是，如果想要访问互联网上的资源，就必须向资源所在的服务器发出请求，请求体中必须包含资源的网络路径，以及对资源进行的操作(增删改查)。

### 2.2.2 **数据格式**

Elasticsearch 是面向文档型数据库，一条数据在这里就是一个文档。

![image-20221129213434745](C:\Users\hakuou\Documents\program\Git\images\image-20221129213434745.png)

ES 里的 Index 可以看做一个库，而 Types 相当于表，Documents 则相当于表的行。这里 Types 的概念已经被逐渐弱化，Elasticsearch 6.X 中，一个 index 下已经只能包含一个type，Elasticsearch 7.X 中, Type 的概念已经被删除了。

用 JSON 作为文档序列化的格式

```json
{
 "name" : "John",
 "sex" : "Male",
 "age" : 25,
 "birthDate": "1990/05/01",
 "about" : "I love to go rock climbing",
 "interests": [ "sports", "music" ]
}
```

### **2.2.3 HTTP** **操作**

#### **2.2.4.1** **索引操作**

##### 2.2.4.1.1 **创建索引**

对比关系型数据库，创建索引就等同于创建数据库

在 Postman 中，向 ES 服务器发 ==PUT== 请求 ：

```perl
http://127.0.0.1:9200/shopping
```

```json
{
 "acknowledged"【响应结果】: true, # true 操作成功
 "shards_acknowledged"【分片结果】: true, # 分片操作成功
 "index"【索引名称】: "shopping"
}
```

==注意：创建索引库的分片数默认 1 片，在 7.0.0 之前的 Elasticsearch 版本中，默认 5 片==

如果重复添加索引，会返回错误信息`already exists`

##### 2.2.4.1.2 **查看所有索引**

在 Postman 中，向 ES 服务器发 ==GET== 请求 ：

```perl
http://127.0.0.1:9200/_cat/indices?v
```

这里请求路径中的_cat 表示查看的意思，indices 表示索引

整体含义就是查看当前 ES服务器中的所有索引，就好像 MySQL 中的 show tables 的感觉，服务器响应结果如下

```powershell
C:\Users\hakuou>curl http://127.0.0.1:9200/_cat/indices?v
health status index    uuid                   pri rep docs.count docs.deleted store.size pri.store.size
yellow open   shopping 22l48PmzTXGdcz7MWtJpBw   1   1          2            0       11kb           11kb
```

| 表头           | 含义                                                         |
| -------------- | ------------------------------------------------------------ |
| health         | 当前服务器健康状态：<font color=green>**green**</font>(集群完整) 、<font color=orange>**yellow**</font>(单点正常、集群不完整)、 <font color=red>**red**</font>(单点不正常) |
| status         | 索引打开、关闭状态                                           |
| index          | 索引名                                                       |
| uuid           | 索引统一编号                                                 |
| pri            | 主分片数量                                                   |
| rep            | 副本数量                                                     |
| docs.count     | 可用文档数量                                                 |
| docs.deleted   | 文档删除状态（逻辑删除）                                     |
| store.size     | 主分片和副分片整体占空间大小                                 |
| pri.store.size | 主分片占空间大小                                             |

##### 2.2.4.1.3 **查看单个索引**

在 Postman 中，向 ES 服务器发 ==GET== 请求 ：

```perl
http://127.0.0.1:9200/shopping
```

```json

```

