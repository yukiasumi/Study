## 1、安装oracle

`docker search oracle`
![img](https://img2020.cnblogs.com/blog/1834947/202012/1834947-20201227173249411-1509470671.png)
这里使用的是国内的镜像安装的
`docker pull registry.cn-hangzhou.aliyuncs.com/helowin/oracle_11g`
![img](https://img2020.cnblogs.com/blog/1834947/202012/1834947-20201227170808853-1279412283.png)
查看镜像`docker images`
![img](https://img2020.cnblogs.com/blog/1834947/202012/1834947-20201227170845361-994326243.png)
安装镜像
`docker run --name myOracle -d -p 1521:1521 registry.cn-hangzhou.aliyuncs.com/helowin/oracle_11g`
![img](https://img2020.cnblogs.com/blog/1834947/202012/1834947-20201227170931114-370055117.png)
查看运行的镜像
![img](https://img2020.cnblogs.com/blog/1834947/202012/1834947-20201227171025670-1612423139.png)
发现oracle已经安装成功

## 2、配置oracle环境参数

进入镜像
`docker exec -it myOracle bash`
![img](https://img2020.cnblogs.com/blog/1834947/202012/1834947-20201227171202730-323634279.png)
修改/etc/profile文件
这里需要使用root用户权限
用户名：root 密码：helowin

```bash
export  ORACLE_BASE=/usr/oracle 
ORACLE_BASE=/home/oracle/app/oracle

# oracle home目录
export ORACLE_HOME=/home/oracle/app/oracle/product/11.2.0/dbhome_2   
# oracle 服务名或者 SID名，要记住这个名字helowin，它是naivcat登录的重要选项
export ORACLE_SID=helowin     
# oracle环境变量
export PATH=$ORACLE_HOME/bin:$PATH
```

![img](https://img2020.cnblogs.com/blog/1834947/202012/1834947-20201227171514179-2121971314.png)
![img](https://img2020.cnblogs.com/blog/1834947/202012/1834947-20201227171632945-290409718.png)
要记住export ORACLE_SID=helowin，helowin是naivcat登录的重要选项，最后使用指令`source /etc/profile`使环境变量立即生效
创建软连接，使用命令`ln -s $ORACLE_HOME/bin/sqlplus /usr/bin`
![img](https://img2020.cnblogs.com/blog/1834947/202012/1834947-20201227171650377-282548136.png)

使用`sqlplus /nolog`查看oracle并准备链接
![img](https://img2020.cnblogs.com/blog/1834947/202012/1834947-20201227172012978-1852675416.png)
链接oracle，发现链接失败，如果你能链接成功，就可以直接进行下边的操作
![img](https://img2020.cnblogs.com/blog/1834947/202012/1834947-20201227172103757-370024828.png)
这里没连接上是因为没有切换到oracle用户下
`su oracle`，查看连接oracle：`sqlplus /nolog` `conn /as sysdba`
![img](https://img2020.cnblogs.com/blog/1834947/202012/1834947-20201227172216903-950968443.png)
修改system的密码并设置密码的有效时间为无限

```sql
SQL> alter user system identified by oracle;
User altered.
SQL> ALTER PROFILE DEFAULT LIMIT PASSWORD_LIFE_TIME UNLIMITED;
Profile altered.
```

![img](https://img2020.cnblogs.com/blog/1834947/202012/1834947-20201227172347050-656430765.png)
![img](https://img2020.cnblogs.com/blog/1834947/202012/1834947-20201227172542226-879214266.png)
这时候创建表空间发现失败
![img](https://img2020.cnblogs.com/blog/1834947/202012/1834947-20201227172645594-1084985016.png)
执行`show parameter db_create_file;`
![img](https://img2020.cnblogs.com/blog/1834947/202012/1834947-20201227172704849-562738551.png)
设置表空间位置`ALTER SYSTEM SET db_create_file_dest = "/home/oracle/app/oracle/oradata";`
![img](https://img2020.cnblogs.com/blog/1834947/202012/1834947-20201227172833835-2047429144.png)
再创建表空间发现能创建成功
`create tablespace testTablespace;`
![img](https://img2020.cnblogs.com/blog/1834947/202012/1834947-20201227172943585-1198044850.png)

## 3、连接数据库

![img](https://img2020.cnblogs.com/blog/1834947/202012/1834947-20201227173127473-1605422100.png)