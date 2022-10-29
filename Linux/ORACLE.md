中文报错

1. 
   [oracle@source_pc ~]$ export NLS_LANG=AMERICAN_AMERICA.WE8ISO8859P1
2. --这样只是针对该窗口，临时生效，对于其他会话不生效。
3. 
4. 要想永久生效，可以写入.bash_profile文件。
5. [oracle@source_pc ~]$ pwd
6. /home/oracle
7. [oracle@source_pc ~]$ vi .bash_profile
8. 添加一行：export NLS_LANG=AMERICAN_AMERICA.ZHS16GBK
9. [oracle@source_pc ~]$ source .bash_profile

![image-20220305174148707](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220305174148707.png)

```
su oracle`，查看连接oracle：`sqlplus /nolog` `conn /as sysdba
```

ORA-01097: cannot shutdown while in a transaction - commit or rollback first

【SQL> commit;】

show parameter db_create_file;

create tablespace testTablespace;创建表空间







su oracle

密码：oracle

su root 

密码：helowin

oracle下

cd 

source .bash_profile 加载环境变量

root下

source /etc/profile 加载环境变量





* 查看容器
  * docker ps 
* 进入容器
  * docker exec -it 容器id bash 
* 查看环境变量
  * export
* 切换路径
  * cd
* 加载环境变量
  * source .bash_profile
* 以dba（数据库管理员，最高权限）登录数据库
  * sqlplus / as sysdba
* 验证用户是否正确
  * show user
* 退出数据库
  * exit【或Ctrl+d】

* 每次执行完需要提交
  * commit

连接navicat

服务名helowin

用户名system

密码oracle





查看表空间
```
select * from v$tablespace;
```
查看数据文件
```
select file_name,tablespace_name from dba_data_files;
```

systemctl start docker

查看表名

```select * from user_tables;```