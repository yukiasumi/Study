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

## chrome浏览器无法翻译

==地址改为非国内==

## Oracle资源繁忙锁表

```sql
select l.session_id,o.owner,o.object_name from v$locked_object l,dba_objects o where l.object_id=o.object_id;

知道username可根据username直接查询
SELECT sid, serial#, username, oSUSEr,terminal,program ,action, prev_exec_start FROM v$session where sid = 上一步session_id的值;

alter system kill session '上一步sid的值,上一步的serial#的值';

```

## postgreSQL

```plsql
SQL 错误 [55000]: ERROR: cannot delete from table "xxxxx" because it does not have a replica identity and publishes updates
Hint: To enable updating from the table, set REPLICA IDENTITY using ALTER TABLE.
 
SQL 错误 [55000]: ERROR: cannot delete from table "xxxxx" because it does not have a replica identity and publishes deletes
Hint: To enable deleting from the table, set REPLICA IDENTITY using ALTER TABLE.

在数据库执行以下sql即可:
ALTER TABLE xxxxx REPLICA IDENTITY FULL;
```

## Edge浏览器取消置顶

```perl
CTRL+AIT+ESC，若无效果，尝试关闭edge，重新打开
```

