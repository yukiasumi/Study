## 集群部署 

**0）** 官方下载地址：http://kafka.apache.org/downloads.html[ ](http://kafka.apache.org/downloads.html)

**1）** 解压安装包 

```shell
tar -zxvf kafka_2.12-3.0.0.tgz -C /opt/module/ 
```

**2）** 修改解压后的文件名称 

```shell
mv kafka_2.12-3.0.0/ kafka 
```

**3）** 进入到/opt/module/kafka 目录，修改配置文件 

```shell
cd config/ 
vim server.properties 
```

输入以下内容： 

```shell
#broker的全局唯一编号，不能重复，只能是数字。 
broker.id=0 
#处理网络请求的线程数量 
num.network.threads=3 
#用来处理磁盘IO的线程数量 num.io.threads=8 
#发送套接字的缓冲区大小 
socket.send.buffer.bytes=102400 
#接收套接字的缓冲区大小 
socket.receive.buffer.bytes=102400 
#请求套接字的缓冲区大小 
socket.request.max.bytes=104857600 
#kafka运行日志(数据)存放的路径，路径不需要提前创建，kafka自动帮你创建，可以配置多个磁盘路径，路径与路径之间可以用"，"分隔 
log.dirs=/opt/module/kafka/datas 
#topic在当前broker上的分区个数 
num.partitions=1 
#用来恢复和清理data下数据的线程数量 
num.recovery.threads.per.data.dir=1 
# 每个topic创建时的副本数，默认时1个副本 
offsets.topic.replication.factor=1 
#segment文件保留的最长时间，超时将被删除 
log.retention.hours=168 
#每个segment文件的大小，默认最大1G 
log.segment.bytes=1073741824 
# 检查过期数据的时间，默认5分钟检查一次是否数据过期 
log.retention.check.interval.ms=300000 
#配置连接Zookeeper集群地址（在zk根目录下创建/kafka，方便管理） 
zookeeper.connect=hadoop102:2181,hadoop103:2181,hadoop104:2181/kafka 
```

```shell
主要改
#broker的全局唯一编号，不能重复，只能是数字。 
broker.id=0 
#kafka运行日志(数据)存放的路径，路径不需要提前创建，kafka自动帮你创建，可以配置多个磁盘路径，路径与路径之间可以用"，"分隔 
log.dirs=/opt/module/kafka/datas 
#配置连接Zookeeper集群地址（在zk根目录下创建/kafka，方便管理） 
zookeeper.connect=hadoop102:2181,hadoop103:2181,hadoop104:2181/kafka 
```

**4）**分发安装包 

```shell
xsync kafka/ 
```

**5）** 分别在 hadoop103 和 hadoop104 上修改配置文件
/opt/module/kafka/config/server.properties 中的 broker.id=1、broker.id=2 

 ==注：broker.id 不得重复，整个集群中唯一。== 

```shell
vim kafka/config/server.properties 修改: 
# The id of the broker. This must be set to a unique integer for each broker. broker.id=1 

vim kafka/config/server.properties 修改: 
# The id of the broker. This must be set to a unique integer for each broker. broker.id=2 
```

**6）** 配置环境变量 

（1） 在/etc/profile.d/my_env.sh 文件中增加 kafka 环境变量配置 

```shell
sudo vim /etc/profile.d/my_env.sh 
增加如下内容： 
#KAFKA_HOME 
export KAFKA_HOME=/opt/module/kafka export PATH=$PATH:$KAFKA_HOME/bin 
```

（2） 刷新一下环境变量。 

```shell
source /etc/profile 
```

（3） 分发环境变量文件到其他节点，并 source。 

```shell
sudo /home/atguigu/bin/xsync /etc/profile.d/my_env.sh 
source /etc/profile 
source /etc/profile 
```

**7）** 启动集群 

（1） 先启动 Zookeeper 集群，然后启动 Kafka。 

```shell
zk.sh start  
```

（2） 依次在 hadoop102、hadoop103、hadoop104 节点上启动 Kafka。 
```shell
bin/kafka-server-start.sh config/server.properties 
bin/kafka-server-start.sh config/server.properties 
bin/kafka-server-start.sh config/server.properties
```
==注意：配置文件的路径要能够到 server.properties。==

**8）** 关闭集群 

```shell
bin/kafka-server-stop.sh  
bin/kafka-server-stop.sh  
bin/kafka-server-stop.sh  
```

## 集群启停脚本 

1） 在/home/atguigu/bin 目录下创建文件 kf.sh 脚本文件 

```shell
vim kf.sh 
```

脚本如下： 

```shell
#!/bin/bash
case $1 in
"start")
	for i in hadoop102 hadoop103 hadoop104
	do
		echo " --------启动 $i Kafka-------"         
		ssh $i "/opt/module/kafka/bin/kafka-server-start.sh -daemon /opt/module/kafka/config/server.properties"
	done
;;
"stop")
	for i in hadoop102 hadoop103 hadoop104
	do
		echo " --------停止 $i Kafka-------"         
		ssh $i "/opt/module/kafka/bin/kafka-server-stop.sh -daemon /opt/module/kafka/config/server.properties"
	done
;;
esac
```

2） 添加执行权限 

```shell
chmod +x kf.sh 
```

3） 启动集群命令 

```shell
kf.sh start 
```

4） 停止集群命令 

```shell
kf.sh stop 
```

==注意：停止 Kafka 集群时，一定要等 Kafka 所有节点进程全部停止后再停止 Zookeeper 集群。因为 Zookeeper 集群当中记录着 Kafka 集群相关信息，Zookeeper 集群一旦先停止，Kafka 集群就没有办法再获取停止进程的信息，只能手动杀死 Kafka 进程了.==

