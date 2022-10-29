## hadoop入门

### hadoop伪分布式搭建

```
/etc/profile

export JAVA_HOME=/usr/java/jdk1.8.0_271
export CLASSPATH=$:CLASSPATH:$JAVA_HOME/lib/
export PATH=$PATH:$JAVA_HOME/bin

export KAFKA_HOME=/opt/kafka
export PATH=$PATH:$KAFKA_HOME/bin

export ZOOKEEPER_HOME=/usr/local/zookeeper
export PATH=$PATH:$ZOOKEEPER/bin

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

export HIVE_HOME=/opt/hive-3.1.2
export PATH=$PATH:$HIVE_HOME/bin


source /etc/profile

core-site.xml
<configuration>

   <property>
      <name>fs.default.name</name>
      <value>hdfs://localhost:9000</value>
   </property>

   <property>
      <name>hadoop.proxyuser.root.hosts</name>
      <value>*</value>
   </property>
   <property>
      <name>hadoop.proxyuser.root.groups</name>
      <value>*</value>
   </property>
</configuration>


hdfs-site.xml
<configuration>

<property>
      <name>dfs.replication</name>
      <value>1</value>
   </property>
   <property>
      <name>dfs.name.dir</name>
      <value>file:///data/hadoop/hdfs/namenode </value>
   </property>
   <property>
      <name>dfs.data.dir</name>
      <value>file:///data/hadoop/hdfs/datanode </value>
   </property>
</configuration>

yarn-site.xml
<configuration>

<!-- Site specific YARN configuration properties -->


   <property>
      <name>yarn.nodemanager.aux-services</name>
      <value>mapreduce_shuffle</value>
   </property>

</configuration>

mapred-site.xml
<configuration>

   <property>
      <name>mapreduce.framework.name</name>
      <value>yarn</value>
   </property>

</configuration>

第一次启动时需要初始化
hdfs namenode -format

vim /bin/myhadoop.sh

#!/bin/bash

case $1 in
        start)
                echo "========开启hdfs========"
                start-dfs.sh
                echo "========开启yarn========"
                start-yarn.sh
;;
        stop)
                echo "========关闭hdfs========"
                stop-dfs.sh
                echo "========关闭yarn========"
                stop-yarn.sh
;;
esac

myhadoop.sh start 开启Hadoop
myhadoop.sh stop  关闭Hadoop

浏览器访问192.168.10.130:9870 -----hdfs
浏览器访问192.168.10.130:8088 -----yarn
```



#### xsync分发脚本

（a）在/home/atguigu/bin目录下创建xsync文件

```
[atguigu@hadoop102 opt]$ cd /home/atguigu
[atguigu@hadoop102 ~]$ mkdir bin
[atguigu@hadoop102 ~]$ cd bin
[atguigu@hadoop102 bin]$ vim xsync
```

在该文件中编写如下代码

```shell
#!/bin/bash
#1. 判断参数个数
if [ $# -lt 1 ]
then
  echo Not Enough Arguement!
  exit;
fi

#2. 遍历集群所有机器

for host in hadoop102 hadoop103 hadoop104
do
  echo ====================  $host  ====================
#3. 遍历所有目录，挨个发送
	for file in $@
	do
#4. 判断文件是否存在
	if [ -e $file ]
	then
#5. 获取父目录
		pdir=$(cd -P $(dirname $file); pwd)
#6. 获取当前文件的名称
		fname=$(basename $file)
		ssh $host "mkdir -p $pdir"
		rsync -av $pdir/$fname $host:$pdir
	else
		echo $file does not exists!
	fi
  done
done
```

（b）修改脚本 xsync 具有执行权限

```
[atguigu@hadoop102 bin]$ chmod +x xsync
```

（c）测试脚本

```
[atguigu@hadoop102 ~]$ xsync /home/atguigu/bin
```

（d）将脚本复制到/bin中，以便全局调用

```
[atguigu@hadoop102 bin]$ sudo cp xsync /bin/
```

（e）同步环境变量配置（root所有者）

```
[atguigu@hadoop102 ~]$ sudo ./bin/xsync /etc/profile.d/my_env.sh
```

==注意：如果用了sudo，那么xsync一定要给它的路径补全。==

#### 配置workers

```
[atguigu@hadoop102 hadoop]$ vim /opt/module/hadoop-3.1.3/etc/hadoop/workers
```

在该文件中增加如下内容：

```
hadoop102
hadoop103
hadoop104
```

注意：该文件中添加的内容结尾不允许有空格，文件中不允许有空行。

同步所有节点配置文件

```
[atguigu@hadoop102 hadoop]$ xsync /opt/module/hadoop-3.1.3/etc
```

#### 启动集群

（1）如果集群是第一次启动，需要在hadoop102节点格式化NameNode（注意：格式化NameNode，会产生新的集群id，导致NameNode和DataNode的集群id不一致，集群找不到已往数据。如果集群在运行过程中报错，需==要重新格式化NameNode的话，一定要先停止namenode和datanode进程，并且要删除所有机器的data和logs目录，然后再进行格式化。==）

```
[atguigu@hadoop102 hadoop-3.1.3]$ hdfs namenode -format
```

（2）启动HDFS

```
[atguigu@hadoop102 hadoop-3.1.3]$ sbin/start-dfs.sh
```

（3）在配置了ResourceManager的节点（hadoop103）启动YARN

```
[atguigu@hadoop103 hadoop-3.1.3]$ sbin/start-yarn.sh
```

（4）Web端查看HDFS的NameNode

（a）浏览器中输入：http://hadoop102:9870

（b）查看HDFS上存储的数据信息

（5）Web端查看YARN的ResourceManager

（a）浏览器中输入：http://hadoop103:8088

（b）查看YARN上运行的Job信息

3）集群基本测试

（1）上传文件到集群

Ø 上传小文件

```
[atguigu@hadoop102 ~]$ hadoop fs -mkdir /input
[atguigu@hadoop102 ~]$ hadoop fs -put $HADOOP_HOME/wcinput/word.txt /input
```

Ø 上传大文件

```
[atguigu@hadoop102 ~]$ hadoop fs -put  /opt/software/jdk-8u212-linux-x64.tar.gz  /
```

（2）上传文件后查看文件存放在什么位置

Ø 查看HDFS文件存储路径

[atguigu@hadoop102 subdir0]$ pwd

```
/opt/module/hadoop-3.1.3/data/dfs/data/current/BP-1436128598-192.168.10.102-1610603650062/current/finalized/subdir0/subdir0
```

Ø 查看HDFS在磁盘存储文件内容

```
[atguigu@hadoop102 subdir0]$ cat blk_1073741825

hadoop yarn
hadoop mapreduce 
atguigu
atguigu
```

（3）拼接

```
-rw-rw-r--. 1 atguigu atguigu 134217728 5月 23 16:01 blk_1073741836
-rw-rw-r--. 1 atguigu atguigu  1048583 5月 23 16:01 blk_1073741836_1012.meta
-rw-rw-r--. 1 atguigu atguigu  63439959 5月 23 16:01 blk_1073741837
-rw-rw-r--. 1 atguigu atguigu   495635 5月 23 16:01 blk_1073741837_1013.meta

[atguigu@hadoop102 subdir0]$ cat blk_1073741836>>tmp.tar.gz
[atguigu@hadoop102 subdir0]$ cat blk_1073741837>>tmp.tar.gz
[atguigu@hadoop102 subdir0]$ tar -zxvf tmp.tar.gz
```

（4）下载

```
[atguigu@hadoop104 software]$ hadoop fs -get /jdk-8u212-linux-x64.tar.gz ./
```

（5）执行wordcount程序

```
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-3.1.3.jar wordcount /input /output
```

#### 集群启动/停止方式总结

1）各个模块分开启动/停止（配置ssh是前提）常用

​	（1）整体启动/停止HDFS

```
start-dfs.sh/stop-dfs.sh
```

​	（2）整体启动/停止YARN

```
start-yarn.sh/stop-yarn.sh
```

2）各个服务组件逐一启动/停止

​	（1）分别启动/停止HDFS组件

```
hdfs --daemon start/stop namenode/datanode/secondarynamenode
```

​	（2）启动/停止YARN

```
yarn --daemon start/stop  resourcemanager/nodemanager
```



#### hadoop常用脚本

1）Hadoop集群启停脚本（包含HDFS，yarn，historyserver)  myhadoop.sh

```
[atguigu@hadoop102 ~]$ cd /home/atguigu/bin
[atguigu@hadoop102 bin]$ vim myhadoop.sh
```
```shell
#!/bin/bash

if [ $# -lt 1 ]
then
    echo "No Args Input..."
    exit ;
fi

case $1 in
"start")
        echo " =================== 启动 hadoop集群 ==================="

        echo " --------------- 启动 hdfs ---------------"
        ssh hadoop102 "/opt/module/hadoop-3.1.3/sbin/start-dfs.sh"
        echo " --------------- 启动 yarn ---------------"
        ssh hadoop103 "/opt/module/hadoop-3.1.3/sbin/start-yarn.sh"
        echo " --------------- 启动 historyserver ---------------"
        ssh hadoop102 "/opt/module/hadoop-3.1.3/bin/mapred --daemon start historyserver"
;;
"stop")
        echo " =================== 关闭 hadoop集群 ==================="

        echo " --------------- 关闭 historyserver ---------------"
        ssh hadoop102 "/opt/module/hadoop-3.1.3/bin/mapred --daemon stop historyserver"
        echo " --------------- 关闭 yarn ---------------"
        ssh hadoop103 "/opt/module/hadoop-3.1.3/sbin/stop-yarn.sh"
        echo " --------------- 关闭 hdfs ---------------"
        ssh hadoop102 "/opt/module/hadoop-3.1.3/sbin/stop-dfs.sh"
;;
*)
    echo "Input Args Error..."
;;
esac
```
保存后退出，然后赋予脚本执行权限
```
[atguigu@hadoop102 bin]$ chmod +x myhadoop.sh
```
2）查看三台服务器Java进程脚本：jpsall
```
[atguigu@hadoop102 ~]$ cd /home/atguigu/bin
[atguigu@hadoop102 bin]$ vim jpsall
```
输入如下内容
```shell
#!/bin/bash

for host in hadoop102 hadoop103 hadoop104
do
        echo =============== $host ===============
        ssh $host jps 
done
```
保存后退出，然后赋予脚本执行权限
```
[atguigu@hadoop102 bin]$ chmod +x jpsall
```
3）分发/home/atguigu/bin目录，保证自定义脚本在三台机器上都可以使用
```
[atguigu@hadoop102 ~]$ xsync /home/atguigu/bin/
```

#### ==常用端口号说明==

| 端口名称                  | Hadoop2.x   | Hadoop3.x        |
| ------------------------- | ----------- | ---------------- |
| NameNode内部通信端口      | 8020 / 9000 | 8020 / 9000/9820 |
| NameNode HTTP UI          | 50070       | 9870             |
| MapReduce查看执行任务端口 | 8088        | 8088             |
| 历史服务器通信端口        | 19888       | 19888            |

#### ==常用的配置文件==

| Hadoop2.x       | Hadoop3.x       |
| --------------- | --------------- |
| core-site.xml   | core-site.xml   |
| hdfs-site.xml   | hdfs-site.xml   |
| yarn-site.xml   | yarn-site.xml   |
| mapred-site.xml | mapred-site.xml |
| works           | slaves          |

### HDFS

	1、HDFS文件块大小（面试重点）
		硬盘读写速度
		在企业中  一般128m（中小公司）   256m （大公司）
	2、HDFS的Shell操作（开发重点）
	3、HDFS的读写流程（面试重点）
#####  常用命令实操

###### 准备工作

```
1）启动Hadoop集群（方便后续的测试）
[atguigu@hadoop102 hadoop-3.1.3]$ sbin/start-dfs.sh
[atguigu@hadoop103 hadoop-3.1.3]$ sbin/start-yarn.sh

2）-help：输出这个命令参数
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs -help rm

3）创建/sanguo文件夹
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs -mkdir /sanguo
```



###### 上传

```
1）-moveFromLocal：从本地剪切粘贴到HDFS

[atguigu@hadoop102 hadoop-3.1.3]$ vim shuguo.txt
输入：
shuguo
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs  -moveFromLocal  ./shuguo.txt  /sanguo

2）-copyFromLocal：从本地文件系统中拷贝文件到HDFS路径去

[atguigu@hadoop102 hadoop-3.1.3]$ vim weiguo.txt
输入：
weiguo
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs -copyFromLocal weiguo.txt /sanguo

3）-put：等同于copyFromLocal，生产环境更习惯用put

[atguigu@hadoop102 hadoop-3.1.3]$ vim wuguo.txt
输入：
wuguo
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs -put ./wuguo.txt /sanguo

4）-appendToFile：追加一个文件到已经存在的文件末尾

[atguigu@hadoop102 hadoop-3.1.3]$ vim liubei.txt
输入：
liubei
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs -appendToFile liubei.txt /sanguo/shuguo.txt
```



###### 下载

```
1）-copyToLocal：从HDFS拷贝到本地
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs -copyToLocal /sanguo/shuguo.txt ./

2）-get：等同于copyToLocal，生产环境更习惯用get
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs -get /sanguo/shuguo.txt ./shuguo2.txt
```



###### HDFS直接操作

```
1）-ls: 显示目录信息
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs -ls /sanguo

2）-cat：显示文件内容
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs -cat /sanguo/shuguo.txt

3）-chgrp、-chmod、-chown：Linux文件系统中的用法一样，修改文件所属权限
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs  -chmod 666  /sanguo/shuguo.txt
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs  -chown  atguigu:atguigu  /sanguo/shuguo.txt

4）-mkdir：创建路径
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs -mkdir /jinguo

5）-cp：从HDFS的一个路径拷贝到HDFS的另一个路径
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs -cp /sanguo/shuguo.txt /jinguo

6）-mv：在HDFS目录中移动文件
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs -mv /sanguo/wuguo.txt /jinguo
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs -mv /sanguo/weiguo.txt /jinguo

7）-tail：显示一个文件的末尾1kb的数据
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs -tail /jinguo/shuguo.txt

8）-rm：删除文件或文件夹
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs -rm /sanguo/shuguo.txt

9）-rm -r：递归删除目录及目录里面内容
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs -rm -r /sanguo

10）-du统计文件夹的大小信息
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs -du -s -h /jinguo
27  81  /jinguo
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs -du  -h /jinguo
14  42  /jinguo/shuguo.txt
7  21  /jinguo/weiguo.txt
6  18  /jinguo/wuguo.tx
说明：27表示文件大小；81表示27*3个副本；/jinguo表示查看的目录

11）-setrep：设置HDFS中文件的副本数量
[atguigu@hadoop102 hadoop-3.1.3]$ hadoop fs -setrep 10 /jinguo/shuguo.txt
```

##### 查看fsimage镜像文件

```
hdfs oiv -p 文件类型 -i镜像文件 -o 转换后文件输出路径


[atguigu@hadoop102 current]$ pwd
/opt/module/hadoop-3.1.3/data/dfs/name/current
[atguigu@hadoop102 current]$ hdfs oiv -p XML -i fsimage_0000000000000000025 -o /opt/module/hadoop-3.1.3/fsimage.xml
[atguigu@hadoop102 current]$ cat /opt/module/hadoop-3.1.3/fsimage.xml
```

##### 查看edits编辑文件

```
hdfs oev -p 文件类型 -i编辑日志 -o 转换后文件输出路径

[atguigu@hadoop102 current]$ hdfs oev -p XML -i edits_0000000000000000012-0000000000000000013 -o /opt/module/hadoop-3.1.3/edits.xml
[atguigu@hadoop102 current]$ cat /opt/module/hadoop-3.1.3/edits.xml
```
### MapReduce

	1、InputFormat
		1）默认的是TextInputformat  kv  key偏移量，v :一行内容
		2）处理小文件CombineTextInputFormat 把多个文件合并到一起统一切片
	2、Mapper 
		setup()初始化；  map()用户的业务逻辑； clearup() 关闭资源；
	3、分区
		默认分区HashPartitioner ，默认按照key的hash值%numreducetask个数
		自定义分区
	4、排序
		1）部分排序  每个输出的文件内部有序。
		2）全排序：  一个reduce ,对所有数据大排序。
		3）二次排序：  自定义排序范畴， 实现 writableCompare接口， 重写compareTo方法
			总流量倒序  按照上行流量 正序
	5、Combiner 
		前提：不影响最终的业务逻辑（求和 没问题   求平均值）
		提前聚合map  => 解决数据倾斜的一个方法
	6、Reducer
		用户的业务逻辑；
		setup()初始化；reduce()用户的业务逻辑； clearup() 关闭资源；
	7、OutputFormat
		1）默认TextOutputFormat  按行输出到文件
		2）自定义
### yarn

==yarn-site.xml添加以下内容，根据实际情况修改==

```xml
<!-- 选择调度器，默认容量 -->
<property>
	<description>The class to use as the resource scheduler.</description>
	<name>yarn.resourcemanager.scheduler.class</name>
	<value>org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler</value>
</property>

<!-- ResourceManager处理调度器请求的线程数量,默认50；如果提交的任务数大于50，可以增加该值，但是不能超过3台 * 4线程 = 12线程（去除其他应用程序实际不能超过8） -->
<property>
	<description>Number of threads to handle scheduler interface.</description>
	<name>yarn.resourcemanager.scheduler.client.thread-count</name>
	<value>6</value>
</property>

<!-- 是否让yarn自动检测硬件进行配置，默认是false，如果该节点有很多其他应用程序，建议手动配置。如果该节点没有其他应用程序，可以采用自动 -->
<property>
	<description>Enable auto-detection of node capabilities such as
	memory and CPU.
	</description>
	<name>yarn.nodemanager.resource.detect-hardware-capabilities</name>
	<value>false</value>
</property>

<!-- 是否将虚拟核数当作CPU核数，默认是false，采用物理CPU核数 -->
<property>
	<description>Flag to determine if logical processors(such as
	hyperthreads) should be counted as cores. Only applicable on Linux
	when yarn.nodemanager.resource.cpu-vcores is set to -1 and
	yarn.nodemanager.resource.detect-hardware-capabilities is true.
	</description>
	<name>yarn.nodemanager.resource.count-logical-processors-as-cores</name>
	<value>false</value>
</property>

<!-- 虚拟核数和物理核数乘数，默认是1.0 -->
<property>
	<description>Multiplier to determine how to convert phyiscal cores to
	vcores. This value is used if yarn.nodemanager.resource.cpu-vcores
	is set to -1(which implies auto-calculate vcores) and
	yarn.nodemanager.resource.detect-hardware-capabilities is set to true. The	number of vcores will be calculated as	number of CPUs * multiplier.
	</description>
	<name>yarn.nodemanager.resource.pcores-vcores-multiplier</name>
	<value>1.0</value>
</property>

<!-- NodeManager使用内存数，默认8G，修改为4G内存 -->
<property>
	<description>Amount of physical memory, in MB, that can be allocated 
	for containers. If set to -1 and
	yarn.nodemanager.resource.detect-hardware-capabilities is true, it is
	automatically calculated(in case of Windows and Linux).
	In other cases, the default is 8192MB.
	</description>
	<name>yarn.nodemanager.resource.memory-mb</name>
	<value>4096</value>
</property>

<!-- nodemanager的CPU核数，不按照硬件环境自动设定时默认是8个，修改为4个 -->
<property>
	<description>Number of vcores that can be allocated
	for containers. This is used by the RM scheduler when allocating
	resources for containers. This is not used to limit the number of
	CPUs used by YARN containers. If it is set to -1 and
	yarn.nodemanager.resource.detect-hardware-capabilities is true, it is
	automatically determined from the hardware in case of Windows and Linux.
	In other cases, number of vcores is 8 by default.</description>
	<name>yarn.nodemanager.resource.cpu-vcores</name>
	<value>3</value>
</property>

<!-- 容器最小内存，默认1G -->
<property>
	<description>The minimum allocation for every container request at the RM	in MBs. Memory requests lower than this will be set to the value of this	property. Additionally, a node manager that is configured to have less memory	than this value will be shut down by the resource manager.
	</description>
	<name>yarn.scheduler.minimum-allocation-mb</name>
	<value>1024</value>
</property>

<!-- 容器最大内存，默认8G，修改为2G -->
<property>
	<description>The maximum allocation for every container request at the RM	in MBs. Memory requests higher than this will throw an	InvalidResourceRequestException.
	</description>
	<name>yarn.scheduler.maximum-allocation-mb</name>
	<value>2048</value>
</property>

<!-- 容器最小CPU核数，默认1个 -->
<property>
	<description>The minimum allocation for every container request at the RM	in terms of virtual CPU cores. Requests lower than this will be set to the	value of this property. Additionally, a node manager that is configured to	have fewer virtual cores than this value will be shut down by the resource	manager.
	</description>
	<name>yarn.scheduler.minimum-allocation-vcores</name>
	<value>1</value>
</property>

<!-- 容器最大CPU核数，默认4个，修改为2个 -->
<property>
	<description>The maximum allocation for every container request at the RM	in terms of virtual CPU cores. Requests higher than this will throw an
	InvalidResourceRequestException.</description>
	<name>yarn.scheduler.maximum-allocation-vcores</name>
	<value>2</value>
</property>

<!-- 虚拟内存检查，默认打开，修改为关闭 -->
<property>
	<description>Whether virtual memory limits will be enforced for
	containers.</description>
	<name>yarn.nodemanager.vmem-check-enabled</name>
	<value>false</value>
</property>

<!-- 虚拟内存和物理内存设置比例,默认2.1 -->
<property>
	<description>Ratio between virtual memory to physical memory when	setting memory limits for containers. Container allocations are	expressed in terms of physical memory, and virtual memory usage	is allowed to exceed this allocation by this ratio.
	</description>
	<name>yarn.nodemanager.vmem-pmem-ratio</name>
	<value>2.1</value>
</property>
```

#### 配置多队列的公平调度器

##### 1）修改yarn-site.xml文件，加入以下参数

```xml
<property>
    <name>yarn.resourcemanager.scheduler.class</name>
    <value>org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler</value>
    <description>配置使用公平调度器</description>
</property>

<property>
    <name>yarn.scheduler.fair.allocation.file</name>
    <value>/opt/module/hadoop-3.1.3/etc/hadoop/fair-scheduler.xml</value>
    <description>指明公平调度器队列分配配置文件</description>
</property>

<property>
    <name>yarn.scheduler.fair.preemption</name>
    <value>false</value>
    <description>禁止队列间资源抢占</description>
</property>
```

##### 2）配置fair-scheduler.xml

```xml
<?xml version="1.0"?>
<allocations>
  <!-- 单个队列中Application Master占用资源的最大比例,取值0-1 ，企业一般配置0.1 -->
  <queueMaxAMShareDefault>0.5</queueMaxAMShareDefault>
  <!-- 单个队列最大资源的默认值 test atguigu default -->
  <queueMaxResourcesDefault>4096mb,4vcores</queueMaxResourcesDefault>

  <!-- 增加一个队列test -->
  <queue name="test">
    <!-- 队列最小资源 -->
    <minResources>2048mb,2vcores</minResources>
    <!-- 队列最大资源 -->
    <maxResources>4096mb,4vcores</maxResources>
    <!-- 队列中最多同时运行的应用数，默认50，根据线程数配置 -->
    <maxRunningApps>4</maxRunningApps>
    <!-- 队列中Application Master占用资源的最大比例 -->
    <maxAMShare>0.5</maxAMShare>
    <!-- 该队列资源权重,默认值为1.0 -->
    <weight>1.0</weight>
    <!-- 队列内部的资源分配策略 -->
    <schedulingPolicy>fair</schedulingPolicy>
  </queue>
  <!-- 增加一个队列atguigu -->
  <queue name="atguigu" type="parent">
    <!-- 队列最小资源 -->
    <minResources>2048mb,2vcores</minResources>
    <!-- 队列最大资源 -->
    <maxResources>4096mb,4vcores</maxResources>
    <!-- 队列中最多同时运行的应用数，默认50，根据线程数配置 -->
    <maxRunningApps>4</maxRunningApps>
    <!-- 队列中Application Master占用资源的最大比例 -->
    <maxAMShare>0.5</maxAMShare>
    <!-- 该队列资源权重,默认值为1.0 -->
    <weight>1.0</weight>
    <!-- 队列内部的资源分配策略 -->
    <schedulingPolicy>fair</schedulingPolicy>
  </queue>

  <!-- 任务队列分配策略,可配置多层规则,从第一个规则开始匹配,直到匹配成功 -->
  <queuePlacementPolicy>
    <!-- 提交任务时指定队列,如未指定提交队列,则继续匹配下一个规则; false表示：如果指定队列不存在,不允许自动创建-->
    <rule name="specified" create="false"/>
    <!-- 提交到root.group.username队列,若root.group不存在,不允许自动创建；若root.group.user不存在,允许自动创建 -->
    <rule name="nestedUserQueue" create="true">
        <rule name="primaryGroup" create="false"/>
    </rule>
    <!-- 最后一个规则必须为reject或者default。Reject表示拒绝创建提交失败，default表示把任务提交到default队列 -->
    <rule name="reject" />
  </queuePlacementPolicy>
</allocations>
```

##### 3) 分发配置并重启Yarn

```xml
[atguigu@hadoop102 hadoop]$ xsync yarn-site.xml
[atguigu@hadoop102 hadoop]$ xsync fair-scheduler.xml

[atguigu@hadoop103 hadoop-3.1.3]$ sbin/stop-yarn.sh
[atguigu@hadoop103 hadoop-3.1.3]$ sbin/start-yarn.sh
```

#### yarn总结
	1、Yarn的工作机制（面试题）
	2、Yarn的调度器
		1）FIFO/容量/公平
		2）apache 默认调度器  容量； CDH默认调度器 公平
		3）公平/容量默认一个default ，需要创建多队列
		4）中小企业：hive  spark flink  mr  
		5）中大企业：业务模块：登录/注册/购物车/营销
		6）好处：解耦  降低风险  11.11  6.18  降级使用
		7）每个调度器特点：
			相同点：支持多队列，可以借资源，支持多用户
			不同点：容量调度器：优先满足先进来的任务执行
					公平调度器，在队列里面的任务公平享有队列资源
		8）生产环境怎么选：
			中小企业，对并发度要求不高，选择容量
			中大企业，对并发度要求比较高，选择公平。
	3、开发需要重点掌握：
		1）队列运行原理	
		2）Yarn常用命令
		3）核心参数配置
		4）配置容量调度器和公平调度器。
		5）tool接口使用。