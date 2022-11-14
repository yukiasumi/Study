## 前端请求400

注意后端接受数据格式，后端为日期类型，前端传入为String类型

## hdfs启动出现警告

```perl
util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicableutil.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
```

环境变量添加

```perl
export HADOOP_OPTS="-Djava.library.path=${HADOOP_HOME}/lib/native" 
```