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

![image-20221210183308626](../../images/image-20221210183308626.png)

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

### 2.1.1 Windows安装

Elasticsearch 的官方地址：https://www.elastic.co/cn/

Windows 版的 Elasticsearch 的安装很简单，解压即安装完毕

解压后，进入 bin 文件目录，点击 elasticsearch.bat 文件启动 ES 服务

==注意： ElasticSearch所放的磁盘占用不要超过95%，否则会无限被设置为index只读==

==注意：9300 端口为 Elasticsearch 集群间组件的通信端口，9200 端口为浏览器访问的 http协议 RESTful 端口。==

打开浏览器（推荐使用谷歌浏览器），输入地址：http://localhost:9200，测试结果

![image-20221210183341032](../../images/image-20221210183341032.png)

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

### 2.1.2 Linux安装

软件下载地址：https://www.elastic.co/cn/downloads/past-releases/elasticsearch-7-8-0

**解压软件**

```perl
# 解压缩
tar -zxvf elasticsearch-7.8.0-linux-x86_64.tar.gz -C /opt/module
```

**创建用户**

因为安全问题，Elasticsearch 不允许 root 用户直接运行，所以要创建新用户，在 root 用户中创建新用户

```perl
useradd es #新增 es 用户
passwd es #为 es 用户设置密码
userdel -r es #如果错了，可以删除再加
chown -R es:es /opt/elasticsearch-7.8.0/ #文件夹所有者
```

**修改配置文件**

```perl
vim /opt/elasticsearch-7.8.0/config/elasticsearch.yml

# 加入如下配置
cluster.name: elasticsearch
node.name: node-1
network.host: 0.0.0.0
http.port: 9200
cluster.initial_master_nodes: ["node-1"]
```

```perl
vim /etc/security/limits.conf

# 在文件末尾中增加下面内容
# 每个进程可以打开的文件数的限制
es soft nofile 65536
es hard nofile 65536
```

```perl
vim /etc/security/limits.d/20-nproc.conf

# 在文件末尾中增加下面内容
# 每个进程可以打开的文件数的限制
es soft nofile 65536
es hard nofile 65536
# 操作系统级别对每个用户创建的进程数的限制
* hard nproc 4096
# 注：* 带表 Linux 所有用户名称
```

```perl
vim /etc/sysctl.conf

# 在文件中增加下面内容
# 一个进程可以拥有的 VMA(虚拟内存区域)的数量,默认值为 65536
vm.max_map_count=655360
```

重新加载

```perl
sysctl -p
```

使用 ES 用户启动

启动时，会动态生成文件，如果文件所属用户不匹配，会发生错误，需要重新进行修改用户和用户组

```perl
cd /opt/module/es/
#启动
bin/elasticsearch
#后台启动
bin/elasticsearch -d
```

关闭防火墙

```perl
#暂时关闭防火墙
systemctl stop firewalld
#永久关闭防火墙
systemctl enable firewalld.service #打开放货抢永久性生效，重启后不会复原
systemctl disable firewalld.service #关闭防火墙，永久性生效，重启后不会复原
```

浏览器中输入地址：http://192.168.10.130:9200/ 测试结果

![image-20221209145144532](../../images/image-20221209145144532.png)

## **2.2 Elasticsearch** **基本操作**

### **2.2.1 RESTful**

每一个URI代表1种资源

客户端使用GET、POST、PUT、DELETE4个表示操作方式的动词对服务端资源进行操作：GET用来获取资源，POST用来新建资源（也可以用于更新资源），PUT用来更新资源，DELETE用来删除资源；

==POST 请求不具有幂等性==

==GET、PUT、DELETE请求具有幂等性，多次请求返回结果相同==

REST 指的是一组架构约束条件和原则。满足这些约束条件和原则的应用程序或设计就是 RESTful。

Web 应用程序最重要的 REST 原则是，客户端和服务器之间的交互在请求之间是无状态的。从客户端到服务器的每个请求都必须包含理解请求所必需的信息。如果服务器在请求之间的任何时间点重启，客户端不会得到通知。此外，无状态请求可以由任何可用服务器回答，这十分适合云计算之类的环境。客户端可以缓存数据以改进性能。

在服务器端，应用程序状态和功能可以分为各种资源。资源是一个有趣的概念实体，它向客户端公开。资源的例子有：应用程序对象、数据库记录、算法等等。每个资源都使用 URI (Universal Resource Identifier) 得到一个唯一的地址。所有资源都共享统一的接口，以便在客户端和服务器之间传输状态。使用的是标准的 HTTP 方法，比如 GET、PUT、POST 和DELETE。

在 REST 样式的 Web 服务中，每个资源都有一个地址。资源本身都是方法调用的目标，方法列表对所有资源都是一样的。这些方法都是标准方法，包括 HTTP GET、POST、PUT、DELETE，还可能包括 HEAD 和 OPTIONS。简单的理解就是，如果想要访问互联网上的资源，就必须向资源所在的服务器发出请求，请求体中必须包含资源的网络路径，以及对资源进行的操作(增删改查)。

### 2.2.2 **数据格式**

Elasticsearch 是面向文档型数据库，一条数据在这里就是一个文档。

![image-20221210183404259](../../images/image-20221210183404259.png)

ES 里的 Index 可以看做一个库，而 Types 相当于表，Documents 则相当于表的行。这里 Types 的概念已经被逐渐弱化，Elasticsearch 6.X 中，一个 index 下已经只能包含一个type，Elasticsearch 7.X 中, Type 的概念已经被删除了。

```javascript
//JSON
//Javascript object Notation
var obj = { "name" : "zhangsan", "age":30，"info" : { "email" : "XXxxx"}}var objs = [obj,obj]
new 0bject ()I
//JSON字符串:网络中传递的字符串的格式符合JSON格式，
```

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

#### 2.2.2.1 倒排索引

正排（正向）索引

| id   | content              |
| ---- | -------------------- |
| 1001 | my name is zhang san |
| 1002 | my name is li si     |

倒排索引

| keyword | id        |
| ------- | --------- |
| name    | 1001,1002 |
| zhang   | 1001      |



### **2.2.3 HTTP** **操作**

#### **2.2.4.1** **索引操作**

##### 2.2.4.1.1 **创建索引**

对比关系型数据库，创建索引就等同于创建数据库

向 ES 服务器发 ==PUT== 请求 ：

```perl
###创建索引
PUT http://192.168.10.130:9200/shopping
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

向 ES 服务器发 ==GET== 请求 ：

```perl
###查看所有索引
GET http://192.168.10.130:9200/_cat/indices?v
Accept: application/json
```

这里请求路径中的_cat 表示查看的意思，indices 表示索引

整体含义就是查看当前 ES服务器中的所有索引，就好像 MySQL 中的 show tables 的感觉，服务器响应结果如下

```powershell
[
  {
    "health": "yellow",
    "status": "open",
    "index": "shopping",
    "uuid": "64eMG-gXQdyrsUXCsPL2Fg",
    "pri": "1",
    "rep": "1",
    "docs.count": "0",
    "docs.deleted": "0",
    "store.size": "208b",
    "pri.store.size": "208b"
  }
]
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

向 ES 服务器发 ==GET== 请求 ：

```perl
###查看单个索引
GET http://192.168.10.130:9200/shopping
```

```json
{ 
    "shopping"【索引名】: {         
        "aliases"【别名】: {}, 
        "mappings"【映射】: {}, 
        "settings"【设置】: { 
            "index"【设置 - 索引】: { 
                "creation_date"【设置 - 索引 - 创建时间】: "1614265373911", 
                "number_of_shards"【设置 - 索引 - 主分片数量】: "1", 
                "number_of_replicas"【设置 - 索引 - 副分片数量】: "1", 
                "uuid"【设置 - 索引 - 唯一标识】: "eI5wemRERTumxGCc1bAk2A", 
                "version"【设置 - 索引 - 版本】: { 
                    "created": "7080099" 
                }, 
                "provided_name"【设置 - 索引 - 名称】: "shopping" 
            } 
        } 
    }
}
```

##### 2.2.4.1.4 **删除索引**

向 ES 服务器发 ==DELETE== 请求：

```perl
###删除索引
DELETE http://192.168.10.130:9200/shopping
```

重新访问索引时，服务器返回响应：**索引不存在**

```perl
###查看单个索引
GET http://192.168.10.130:9200/shopping
```

```json
{
  "error": {
    "root_cause": [
      {
        "type": "index_not_found_exception",
        "reason": "no such index [shopping]",
        "resource.type": "index_or_alias",
        "resource.id": "shopping",
        "index_uuid": "_na_",
        "index": "shopping"
      }
    ],
    "type": "index_not_found_exception",
    "reason": "no such index [shopping]",
    "resource.type": "index_or_alias",
    "resource.id": "shopping",
    "index_uuid": "_na_",
    "index": "shopping"
  },
  "status": 404
}
```

#### **2.2.4.2** **文档操作**

索引已经创建好了，接下来创建文档，并添加数据。

这里的文档可以类比为关系型数据库中的表数据，添加的数据格式为 JSON 格式

##### 2.2.4.2.1 **创建文档**

向 ES 服务器发 ==POST==请求，不可以用PUT，因为**ID是随机生成的，返回结果不具有幂等性不可用PUT**

```perl
### 创建文档
POST http://192.168.10.130:9200/shopping/_doc
Content-Type: application/json

{
  "title":"小米手机",
  "category":"小米",
  "images":"https://github.com/yukiasumi/Study/blob/master/images/aniya.png",
  "price":3999.00
}
```

```json
{
  "_index"【索引】: "shopping",
  "_type"【类型-文档】: "_doc",
  "_id"【唯一标识】: "dlEI8oQBH4gCHR81eoI5", #可以类比为 MySQL 中的主键，随机生成
  "_version"【版本】: 1,
  "result"【结果】: "created",#这里的 create 表示创建成功
  "_shards"【分片】: {
    "total"【分片 - 总数】: 2,
    "successful"【分片 - 成功】: 1,
    "failed"【分片 - 失败】: 0
  },
  "_seq_no": 0,
  "_primary_term": 1
}
```

上面的数据创建后，由于没有指定数据唯一性标识（ID），默认情况下，ES 服务器会随机生成一个。

如果想要自定义唯一性标识，需要在创建时指定：

```perl
### 创建文档，自定义唯一性标识
POST http://192.168.10.130:9200/shopping/_doc/1
Content-Type: application/json

{
  "title":"华为手机",
  "category":"华为",
  "images":"https://github.com/yukiasumi/Study/blob/master/images/aniya.png",
  "price":3999.00
}
```

```json
{
  "_index": "shopping",
  "_type": "_doc",
  "_id": "1",
  "_version": 1,
  "result": "created",
  "_shards": {
    "total": 2,
    "successful": 1,
    "failed": 0
  },
  "_seq_no": 1,
  "_primary_term": 3
}
```

==此处需要注意：如果增加数据时明确数据主键，那么请求方式也可以为 PUT==

##### 2.2.4.2.2 **查看文档**

查看文档时，需要指明文档的唯一性标识，类似于 MySQL 中数据的**主键查询**

###### 2.2.4.2.2.1 主键查询

向 ES 服务器发 ==GET== 请求 ：

```perl
### 查看文档
GET http://192.168.10.130:9200/shopping/_doc/1
```

```json
{
  "_index"【索引】: "shopping",
  "_type"【文档类型】: "_doc",
  "_id": "1",
  "_version": 1,
  "_seq_no": 1,
  "_primary_term": 3,
  "found"【查询结果】: true, # true 表示查找到，false 表示未查找到
  "_source"【文档源信息】: {
    "title": "华为手机",
    "category": "华为",
    "images": "https://github.com/yukiasumi/Study/blob/master/images/aniya.png",
    "price": 3999.00
  }
}
```

###### 2.2.4.2.2.2 **查询所有**

```perl
### 查看文档
GET http://192.168.10.130:9200/shopping/_search
```

```json
{
  "took": 111,
  "timed_out": false,
  "_shards": {
    "total": 1,
    "successful": 1,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": {
      "value": 2,
      "relation": "eq"
    },
    "max_score": 1.0,
    "hits": [
      {
        "_index": "shopping",
        "_type": "_doc",
        "_id": "1",
        "_score": 1.0,
        "_source": {
          "title": "华为手机",
          "category": "华为",
          "images": "https://github.com/yukiasumi/Study/blob/master/images/aniya.png",
          "price": 4999.00
        }
      },
      {
        "_index": "shopping",
        "_type": "_doc",
        "_id": "2",
        "_score": 1.0,
        "_source": {
          "title": "小米手机",
          "category": "小米",
          "images": "https://github.com/yukiasumi/Study/blob/master/images/aniya.png",
          "price": 3999.00
        }
      }
    ]
  }
}
```

###### 2.2.4.2.2.3 条件查询

```perl
### 查看文档（条件）
GET http://192.168.10.130:9200/shopping/_search?q=category:小米


### 查看文档条件查询（请求体形式）
GET http://192.168.10.130:9200/shopping/_search
Content-Type: application/json

{
  "query": {
    "match_phrase": {	#完全匹配
      "category": "米"
    }
  }
}
```

```json
{
  "took": 51,
  "timed_out": false,
  "_shards": {
    "total": 1,
    "successful": 1,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": {
      "value": 1,
      "relation": "eq"
    },
    "max_score": 1.3862942,
    "hits": [
      {
        "_index": "shopping",
        "_type": "_doc",
        "_id": "2",
        "_score": 1.3862942,
        "_source": {
          "title": "小米手机",
          "category": "小米",
          "images": "https://github.com/yukiasumi/Study/blob/master/images/aniya.png",
          "price": 3999.00
        }
      }
    ]
  }
}
```

###### 2.2.4.2.2.4 分页查询

```perl
### 分页查询
GET http://192.168.10.130:9200/shopping/_search
Content-Type: application/json

{
  "query": {
    "match_all": {
    }
  },
  "from": 0, #起始位置 = (页码-1）* 每页数据条数
  "size": 1, #查询1条
  "source_": ["title"],	#指定查询的字段，不写默认全字段
  "sort": {			#排序
  	"price": {
  		"order": "desc"
  	}
  }
}
```

```json
{
  "took": 1,
  "timed_out": false,
  "_shards": {
    "total": 1,
    "successful": 1,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": {
      "value": 2,	#查询命中总条数
      "relation": "eq"
    },
    "max_score": 1.0,
    "hits": [		#分页查询实际命中
      {
        "_index": "shopping",
        "_type": "_doc",
        "_id": "1",
        "_score": 1.0,
        "_source": {
          "title": "苹果",
          "category": "华为",
          "images": "https://github.com/yukiasumi/Study/blob/master/images/aniya.png",
          "price": 4999.0
        }
      }
    ]
  }
}
```

###### 2.2.4.2.2.5 多条件查询

```perl
### 多条件查询
GET http://192.168.10.130:9200/shopping/_search
Content-Type: application/json

{
  "query": {
    "bool": {
      "must": [			#should
        {
          "match": {
            "category": "小米"
          }
        },
        {
          "match": {
            "price": 3999.0
          }
        }
      ]
    }
  }
}


### 多条件查询
GET http://192.168.10.130:9200/shopping/_search
Content-Type: application/json

{
  "query": {
    "bool": {
      "should": [
        {
          "match": {
            "category": "小米"
          }
        },
        {
          "match": {
            "category": "华为"
          }
        }
      ]
    }
  }
}

### 多条件查询
GET http://192.168.10.130:9200/shopping/_search
Content-Type: application/json

{
  "query": {
    "bool": {
      "should": [
        {
          "match": {
            "category": "小米"
          }
        },
        {
          "match": {
            "category": "华为"
          }
        }
      ],
      "filter" : {			#过滤
      	"range" : {
      		"price" : {
      			"gt" : 3999	#大于 greater than
      		}
      	}
      }
    }
  }
}
```

###### 2.2.4.2.2.6 聚合查询

```perl
### 分组查询
GET http://192.168.10.130:9200/shopping/_search
Content-Type: application/json

{
	"aggs" : {	//聚合操作
	"price_group" : {	//名称，随意起名
		"terms" : {		//分组
			"field" : "price"	//分组字段
		}
	}
	},
	  "size" : 0	//减少显示信息
}

### 聚合查询
GET http://192.168.10.130:9200/shopping/_search
Content-Type: application/json

{
	"aggs" : {	
	"price_avg" : {	
		"avg" : {		
			"field" : "price"	
		}
	}
	},
	  "size" : 0	
}
```

```json
{
  "took": 6,
  "timed_out": false,
  "_shards": {
    "total": 1,
    "successful": 1,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": {
      "value": 2,
      "relation": "eq"
    },
    "max_score": null,
    "hits": []
  },
  "aggregations": {
    "price_group": {
      "doc_count_error_upper_bound": 0,
      "sum_other_doc_count": 0,
      "buckets": [
        {
          "key": 3999.0,
          "doc_count": 1
        },
        {
          "key": 4999.0,
          "doc_count": 1
        }
      ]
    }
  }
}
```

##### 2.2.4.2.3 **修改文档**

和新增文档一样，输入相同的 URL 地址请求，如果请求体变化，会将原有的数据内容覆盖。

向 ES 服务器发 ==POST== 请求 ：

```perl
### 全量修改文档
POST http://192.168.10.130:9200/shopping/_doc/1
Content-Type: application/json

{
 "title":"华为手机",
 "category":"华为",
 "images":"https://github.com/yukiasumi/Study/blob/master/images/aniya.png",
 "price":4999.00
}
```

```json
{
  "_index": "shopping",
  "_type": "_doc",
  "_id": "1",
  "_version"【版本】: 2,
  "result"【结果】: "updated",# updated 表示数据被更新
  "_shards": {
    "total": 2,
    "successful": 1,
    "failed": 0
  },
  "_seq_no": 2,
  "_primary_term": 3
}
```

##### 2.2.4.2.4 **修改字段**

修改数据时，也可以只修改某一给条数据的局部信息

向 ES 服务器发 ==POST== 请求

```perl
### 修改字段
POST http://192.168.10.130:9200/shopping/_update/1
Content-Type: application/json

{
 "doc": {
 "price":3000.00
 }
}
```

```json
{
  "_index": "shopping",
  "_type": "_doc",
  "_id": "1",
  "_version": 3,
  "result": "updated",
  "_shards": {
    "total": 2,
    "successful": 1,
    "failed": 0
  },
  "_seq_no": 3,
  "_primary_term": 3
}
```

根据唯一性标识，查询文档数据，文档数据已经更新

```perl
### 查看文档
GET http://192.168.10.130:9200/shopping/_doc/1

{
  "_index": "shopping",
  "_type": "_doc",
  "_id": "1",
  "_version": 3,
  "_seq_no": 3,
  "_primary_term": 3,
  "found": true,
  "_source": {
    "title": "华为手机",
    "category": "华为",
    "images": "https://github.com/yukiasumi/Study/blob/master/images/aniya.png",
    "price": 3000.0
  }
}
```

##### 2.2.4.2.5 **删除文档**

删除一个文档不会立即从磁盘上移除，它只是被标记成已删除（逻辑删除）。

向 ES 服务器发 ==DELETE== 请求 ：

```perl
### 删除文档
DELETE http://192.168.10.130:9200/shopping/_doc/1
```

```json
{
  "_index": "shopping",
  "_type": "_doc",
  "_id": "1",
  "_version"【版本】: 4, #对数据的操作，都会更新版本
  "result"【结果】: "deleted",# deleted 表示数据被标记为删除
  "_shards": {
    "total": 2,
    "successful": 1,
    "failed": 0
  },
  "_seq_no": 4,
  "_primary_term": 3
}
```

删除后再查询当前文档信息

```perl
### 查看文档
GET http://192.168.10.130:9200/shopping/_doc/1

{
  "_index": "shopping",
  "_type": "_doc",
  "_id": "1",
  "found": false
}
```

删除一个并不存在的文档

```perl
### 删除文档
DELETE http://192.168.10.130:9200/shopping/_doc/1

{
  "_index": "shopping",
  "_type": "_doc",
  "_id": "1",
  "_version": 1,
  "result"【结果】: "not_found",# not_found 表示未查找到
  "_shards": {
    "total": 2,
    "successful": 1,
    "failed": 0
  },
  "_seq_no": 5,
  "_primary_term": 3
}
```

##### 2.2.4.2.6 **条件删除文档**

一般删除数据都是根据文档的唯一性标识进行删除，实际操作时，也可以根据条件对多条数据进行删除

首先分别增加多条数据:

```perl
POST http://192.168.10.130:9200/shopping/_doc/1
Content-Type: application/json

{
  "title":"华为手机",
  "category":"华为",
  "images":"https://github.com/yukiasumi/Study/blob/master/images/aniya.png",
  "price":3999.00
}


POST http://192.168.10.130:9200/shopping/_doc/2
Content-Type: application/json

{
  "title":"小米手机",
  "category":"小米",
  "images":"https://github.com/yukiasumi/Study/blob/master/images/aniya.png",
  "price":3999.00
}
```

向 ES 服务器发 ==POST== 请求 ：

```perl
### 条件删除文档
POST http://192.168.10.130:9200/shopping/_delete_by_query
Content-Type: application/json

{
  "query": {
    "match": {
      "price": 3999.00
    }
  }
}
```

```json
{
  "took"【耗时】: 26,
  "timed_out"【是否超时】: false,
  "total"【总数】: 1,
  "deleted"【删除数量】: 1,
  "batches": 1,
  "version_conflicts": 0,
  "noops": 0,
  "retries": {
    "bulk": 0,
    "search": 0
  },
  "throttled_millis": 0,
  "requests_per_second": -1.0,
  "throttled_until_millis": 0,
  "failures": []
}
```

#### **2.2.4.3** **映射操作**

有了索引库，等于有了数据库中的 database。

接下来就需要建索引库(index)中的映射了，类似于数据库(database)中的表结构(table)。

创建数据库表需要设置字段名称，类型，长度，约束等；索引库也一样，需要知道这个类型下有哪些字段，每个字段有哪些约束信息，这就叫做映射(mapping)。

##### 2.2.4.3.1 **创建映射**

向 ES 服务器发 ==PUT==请求 ：

```perl
###创建索引
PUT http://192.168.10.130:9200/student

### 创建映射（需先创建索引）
PUT http://192.168.10.130:9200/student/_mapping
Content-Type: application/json

{
  "properties": {
    "name": {
      "type": "text",
      "index": true
    },
    "sex": {
      "type": "text",
      "index": false
    },
    "age": {
      "type": "long",
      "index": false
    }
  }
}

### 插入数据
POST http://192.168.10.130:9200/student/_create/1001
Content-Type: application/json

{
  "age": 12,
  "name": "琉璃",
  "sex": "女"
}
```

```json
{
  "acknowledged": true
}
```

映射数据说明：

* 字段名：任意填写，下面指定许多属性，例如：title、subtitle、images、price

* type：类型，Elasticsearch 中支持的数据类型非常丰富，说几个关键的：

  * String 类型，又分两种：
    * text：可分词
    * keyword：==不可分词，数据会作为完整字段进行匹配==
  * Numerical：数值类型，分两类
    * 基本数据类型：long、integer、short、byte、double、float、half_float
    * 浮点数的高精度类型：scaled_float
  * Date：日期类型
  * Array：数组类型
  * Object：对象

* index：是否索引，默认为 true，也就是说你不进行任何配置，所有字段都会被索引。

  * true：字段会被索引，则可以用来进行搜索
  * false：==字段不会被索引，不能用来搜索==

* store：是否将数据进行独立存储，默认为 false

  原始的文本会存储在_source 里面，默认情况下其他提取出来的字段都不是独立存储的，是从_source 里面提取出来的。当然你也可以独立的存储某个字段，只要设置"store": true 即可，获取独立存储的字段要比从_source 中解析快得多，但是也会占用更多的空间，所以要根据实际业务需求来设置。

* analyzer：分词器，这里的 ik_max_word 即使用 ik 分词器。

##### 2.2.4.3.2 **查看映射**

向 ES 服务器发 ==GET==请求

```perl
### 查看映射
GET http://192.168.10.130:9200/student/_mapping
```

```json
{
  "student": {
    "mappings": {
      "properties": {
        "age": {
          "type": "long",
          "index": false
        },
        "name": {
          "type": "text"
        },
        "sex": {
          "type": "text",
          "index": false
        }
      }
    }
  }
}
```

##### 2.2.4.3.3 **创建索引时创建映射**

向 ES 服务器发 ==PUT== 请求

```perl
### 索引映射关联（创建索引时定义映射）
PUT http://192.168.10.130:9200/student
Content-Type: application/json

{
  "settings": {},
  "mappings": {
    "properties": {
      "name": {
        "type": "text",
        "index": true
      },
      "sex": {
        "type": "text",
        "index": false
      },
      "age": {
        "type": "long",
        "index": false
      }
    }
  }
}
```

```json
{
  "acknowledged": true,
  "shards_acknowledged": true,
  "index": "student1"
}
```

#### **2.2.4.4** **高级查询**

Elasticsearch 提供了基于 JSON 提供完整的查询 DSL 来定义查询

##### 2.2.4.4.1 定义数据

```perl
###删除索引
DELETE http://192.168.10.130:9200/student

### 索引映射关联（创建索引时定义映射）
PUT http://192.168.10.130:9200/student
Content-Type: application/json

{
  "settings": {},
  "mappings": {
    "properties": {
      "name": {
        "type": "text",
        "index": true
      },
      "sex": {
        "type": "text",
        "index": false
      },
      "age": {
        "type": "long",
        "index": false
      }
    }
  }
}

### 插入数据1
POST http://192.168.10.130:9200/student/_doc/1001
Content-Type: application/json

{
  "name": "zhangsan",
  "nickname": "zhangsan",
  "sex": "男",
  "age": 30
}

### 插入数据2
POST http://192.168.10.130:9200/student/_doc/1002
Content-Type: application/json

{
  "name": "lisi",
  "nickname": "lisi",
  "sex": "男",
  "age": 20
}

### 插入数据3
POST http://192.168.10.130:9200/student/_doc/1003
Content-Type: application/json

{
  "name": "wangwu",
  "nickname": "wangwu",
  "sex": "女",
  "age": 40
}

### 插入数据4
POST http://192.168.10.130:9200/student/_doc/1004
Content-Type: application/json

{
  "name": "zhangsan1",
  "nickname": "zhangsan1",
  "sex": "女",
  "age": 50
}

### 插入数据5
POST http://192.168.10.130:9200/student/_doc/1005
Content-Type: application/json

{
  "name": "zhangsan2",
  "nickname": "zhangsan2",
  "sex": "女",
  "age": 30
}
```

##### 2.2.4.4.2 查询所有

```perl
### 查看所有文档
GET http://192.168.10.130:9200/student/_search
Content-Type: application/json

{
  "query": {
    "match_all": {
    }
  }
}

# "query"：这里的 query 代表一个查询对象，里面可以有不同的查询属性
# "match_all"：查询类型，例如：match_all(代表查询所有)， match，term ， range 等等
# {查询条件}：查询条件会根据类型的不同，写法也有差异
```

```json
{
  "took"【查询花费时间，单位毫秒】: 707,
  "timed_out"【是否超时】: false,
  "_shards"【分片信息】: {
    "total"【总数】: 1,
    "successful"【成功】: 1,
    "skipped"【忽略】: 0,
    "failed"【失败】: 0
  },
  "hits"【搜索命中结果】: {
    "total"【搜索条件匹配的文档总数】: {
      "value"【总命中计数的值】: 5,
      "relation"【计数规则】: "eq"	# eq 表示计数准确， gte 表示计数不准确
    },
    "max_score"【匹配度分值】: 1.0,
    "hits"【命中结果集合】: [...]
  }
}
```

##### 2.2.4.4.3 **匹配查询**

match 匹配类型查询，会把查询条件进行分词，然后进行查询，多个词条之间是 or 的关系

```perl
### 匹配查询
GET http://192.168.10.130:9200/student/_search
Content-Type: application/json

{
  "query": {
    "match": {			
      "name":"zhangsan"	
    }
  }
}
```

##### 2.2.4.4.4 **字段匹配查询**

multi_match 与 match 类似，不同的是它可以在多个字段中查询。

```perl
### 字段匹配查询
GET http://192.168.10.130:9200/student/_search
Content-Type: application/json

{
  "query": {
    "multi_match": {
      "query": "zhangsan",
      "fields": ["name","nickname"]
    }
  }
}
```

##### 2.2.4.4.5 **关键字精确查询**

term 查询，精确的关键词匹配查询，不对查询条件进行分词。

```perl
### 关键字精确查询
GET http://192.168.10.130:9200/student/_search
Content-Type: application/json

{
  "query": {
    "term": {
      "name": {
        "value": "zhangsan"
      }
    }
  }
}
```

##### 2.2.4.4.6 **多关键字精确查询**

erms 查询和 term 查询一样，但它允许你指定多值进行匹配。

如果这个字段包含了指定值中的任何一个值，那么这个文档满足条件，类似于 mysql 的 in

```perl
### 多关键字精确查询
GET http://192.168.10.130:9200/student/_search
Content-Type: application/json

{
  "query": {
    "terms": {
      "name": ["zhangsan","lisi"]
    }
  }
}
```

##### 2.2.4.4.7 **指定查询字段**

默认情况下，Elasticsearch 在搜索的结果中，会把文档中保存在_source 的所有字段都返回。

如果只想获取其中的部分字段，可以添加_source 的过滤

```perl
### 指定查询字段
GET http://192.168.10.130:9200/student/_search
Content-Type: application/json

{
  "_source": ["name","nickname"],
  "query": {
    "terms": {
      "nickname": ["zhangsan"]
    }
  }
}
```

##### 2.2.4.4.8 **过滤字段**

* includes：指定想要显示的字段

* excludes：指定不想要显示的字段

```perl
### 过滤字段白名单
GET http://192.168.10.130:9200/student/_search
Content-Type: application/json

{
  "_source": {
    "includes": ["name","nickname"]
  },
  "query": {
    "terms": {
      "nickname": ["zhangsan"]
    }
  }
}

### 过滤字段黑名单
GET http://192.168.10.130:9200/student/_search
Content-Type: application/json

{
  "_source": {
    "excludes": ["name","nickname"]
  },
  "query": {
    "terms": {
      "nickname": ["zhangsan"]
    }
  }
}
```

##### 2.2.4.4.9 **组合查询**

`bool`把各种其它查询通过`must`（必须 ）、`must_not`（必须不）、`should`（应该）的方式进行组合

```perl
### 组合查询
GET http://192.168.10.130:9200/student/_search
Content-Type: application/json
        
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "name": "zhangsan"
          }
        }
      ],
      "must_not": [
        {
          "match": {
            "age": "40"
          }
        }
      ],
      "should": [
        {
          "match": {
            "sex": "男"
          }
        }
      ]
    }
  }
}
```

##### 2.2.4.4.10 **范围查询**

range 查询找出那些落在指定区间内的数字或者时间。range 查询允许以下字符

| 操作符 | 说明          |
| ------ | ------------- |
| gt     | ＞（大于）    |
| gte    | ≥（大于等于） |
| lt     | ＜（小于）    |
| lte    | ≤（小于等于） |

```perl
### 范围查询
GET http://192.168.10.130:9200/student/_search
Content-Type: application/json

{
  "query": {
    "range": {
      "age": {
        "gte": 30,
        "lte": 35
      }
    }
  }
}
```

##### 2.2.4.4.11 **模糊查询**

返回包含与搜索字词相似的字词的文档。

编辑距离是将一个术语转换为另一个术语所需的一个字符更改的次数。这些更改可以包括：

* 更改字符（box → fox）

* 删除字符（black → lack）

* 插入字符（sic → sick）

* 转置两个相邻字符（act → cat）

为了找到相似的术语，fuzzy 查询会在指定的编辑距离内创建一组搜索词的所有可能的变体或扩展。然后查询返回每个扩展的完全匹配。

通过 fuzziness 修改编辑距离。一般使用默认值 AUTO，根据术语的长度生成编辑距离。

```perl
### 模糊查询
GET http://192.168.10.130:9200/student/_search
Content-Type: application/json

{
  "query": {
    "fuzzy": {
      "name": {
        "value": "zhangsan"
      }
    }
  }
}

### 模糊查询2
GET http://192.168.10.130:9200/student/_search
Content-Type: application/json

{
  "query": {
    "fuzzy": {
      "name": {
        "value": "zhangsan",
        "fuzziness": 2
      }
    }
  }
}
```

##### 2.2.4.4.12 **单字段排序**

sort 可以按照不同的字段进行排序，并且通过 order 指定排序的方式。desc 降序，asc升序。