## <font color=FF0000>四大要点：IP、用户名、密码、端口</font>

* ssh aaa03@172.20.3.220

* hostname	主机名

* 分号【;】可以组合命令的显示结果

* echo $(date +%F%n%T) 打印时间 

  ```
  2022-03-26 11:36:19
  ```

  

* chown 用户名 文件名	改变文件拥有者

* cp -af 路径1 路径2	复制1到2

* cp –r test/ newtest

```xml
-a：此选项通常在复制目录时使用，它保留链接、文件属性，并复制目录下的所有内容。其作用等于dpR参数组合。
-f：覆盖已经存在的目标文件而不给出提示。
-r：若给出的源文件是一个目录文件，此时将复制该目录下所有的子目录和文件。 

```
* du
	* 查看文件目录大小
	* 常用组合1：du -h 文件名	
	* 常用组合2： du -sh	

```
	-h：自动识别单位(k/MB/G)
	-s：查看总文件(summary)
```

* ls

```
-a 列出目录所有文件，包含以.开始的隐藏文件
-l 除了文件名之外，还将文件的权限、所有者、文件大小等信息详细列出来
-t 以文件修改时间排序
-S 以文件大小排序
-h 以易读大小显示。 
常用组合1：ls -la 或者单独使用ls
```

* df
  * 常用组合1：df -h⭐
  * 常用组合2： df -T

```xml
常用查看存储空间
-h	自动识别单位(k/MB/G)
-T	显示文件系统的形式[看xfs文件类型]
```

* file   查看文件属性【不确定能不能打开使用file查看文件】⭐
  * 常用组合1：file 文件名
  * 常用组合2： file -c 文件名 查看指令执行过程


* ln  创建连接（link）
	* 常用组合1：ln -s 源目录 链接目录【建立软连接/符号连接】
	* 常用组合2：ln 源目录 链接目录
```
-s：建立软连接（符号连接）
软链接：
1.软链接，以路径的形式存在。类似于Windows操作系统中的快捷方式
2.软链接可以 跨文件系统 ，硬链接不可以
3.软链接可以对一个不存在的文件名进行链接
4.软链接可以对目录进行链接

硬链接：
1.硬链接，以文件副本的形式存在。但不占用实际空间。
2.不允许给目录创建硬链接
3.硬链接只有在同一个文件系统中才能创建
```

* cd 目录名	切换目录
```xml
~  home目录
. 目前所在的目录
.. 目前目录位置的上一层目录。
- 上一次目录
```

* mkdir	文件夹名	新建文件夹
```
-p 确保目录名称存在，不存在的就建一个。
```

* mv
  * mv 文件名A 文件名B	将A改名为B
  * mv 路径1 路径2	将1移动到2

* pwd	查看当前路径

* tee 文件名	读取控制台上输入的数据，并将其内容输出成文件
```
使用指令"tee"将用户输入的数据同时保存到文件"file1"和"file2"中，输入如下命令：
tee file1 file2     
```
* touch 文件名	新建一个文件
```
touch命令有两个功能：一是创建新的空文件，二是改变已有文件的时间戳属性。
touch file{1..5}.txt
touch file1.txt更改创建时间 stat进行验证
```
* find 路径 -name 文件名
	* 常用组合1：find . -name "*.c" ⭐
	* 常用组合2：find . -type f [查找文件类型是 f 的文件。]
	* 常用组合3：find . -ctime -20 [将当前目录及其子目录下所有最近 20 天内更新过的文件列出]
	* 常用组合4：find /var/log -type f -mtime +7 -ok rm {} \;[查找 /var/log 目录中更改时间在 7 日以前的普通文件，并在删除之前询问它们]
```xml
f	一般文件
c	字型装置文件
```
* man 指令名	指令帮助手册
* 指令名 --help	指令帮助
* crontab【cat /etc/crontab】
  * 分 时 日 月 周
  * crontab -e 编辑定时任务
  * crontab -l 列出定时任务
  * crontab -r 删除定时任务
* ps【ps -ef | grep nginx】
  * ps -ef 显示所有程序间的关系
  
  * ps -aux 显示所有包含其他使用者的行程
  
    ==两者区别==
  
    * 如果想看父进程id和完整的command命令，就使用ps -ef，aux会截断command列，而-ef不会。当结合grep时这种区别会影响到结果。
  
    * 如果想看某个进程的cpu和内存使用率，不想使用top等命令的话，可以使用ps aux
* kill
  * kill -9 id 强制杀死进程
  * kill -15 正常停止一个进程。
  * kill -1 id 重新加载进程
* time 指令名【显示指令执行的时间，以及使用机器性能等信息】
* env
  * env	显示所有环境变量
  * env -u 变量名	删除某个环境变量
  * env 变量名=值	定义指定的环境变量

* exit	退出终端

* who -H	用于显示系统中有哪些使用者正在上面，显示的资料包含了使用者 ID、使用的终端机、从哪边连上来的、上线时间
```
-H 或 --heading显示各栏位的标题信息列
```
* uname -a	依次输出内核名称，内核版本号，内核版本，硬件名，处理器类型，硬件平台类型，操作系统名称
```
-a或--all 　显示全部的信息。
```
* grep 样式 文件或目录	过滤[显示]指定的样式的文件内容
	* grep -v linuxtechi /etc/passwd
```
-v 排除，反向过滤，不显示指定样式的内容
-A 打印后几行
-B 打印前几行
ls -l|grep -A3 -B4 test 打印关键字前四行后三行
```
* awk 规则 文件名	指定行列规则输出文本
	* ```
	  * 用法一：awk '{print $1,$4}' log.txt 【每行空格或TAB分割，输出文本中的1、4 列】
	  * 用法二：awk -F, '{print $1,$2}' log.txt 【使用","[逗号]分割】
	  * 用法三：awk -F '[ ,]'  '{print $1,$2,$5}' log.txt 先用空格分割，再用逗号分割
	  * 用法四： awk -va=1 '{print $1,$1+a}' log.txt  设置参数a=1
	  * 用法五:awk '{print $NF}' file1.txt  输出最后一列 【$(NF-1)倒数第二列】
	  * 用法六:   awk 'NR==2{print $3}' file1.txt 输出第二行第三列
	  
	  ```
	
	
	  ```shell
	若有多个关键字会循环打印出
	关键字前3行
	awk '{a[NR]=$0}END{for (i=1;i<=NR;i++) if (a[i]~/keyword/) for (j=i-3;j<i;j++) print a[j]}' file
	关键字后4行
	awk '{a[NR]=$0}END{for (i=1;i<=NR;i++) if (a[i]~/keyword/) for (j=i+1;j<=i+4;j++) print a[j]}' file
	  
	  ```
```
-F fs 或 --field-separator fs 
指定输入文件折分隔符，fs 是一个字符串或者是一个正则表达式，如-F:。
-v var=value --asign var=value
一个用户定义变量。
```
* dirname [选项] 参数 【dirname 路径】
命令去除文件名中的非目录部分，删除最后一个“\”后面的路径，显示父目录。
* basename [选项] 参数 【basename 路径】
basename命令用于打印目录或者文件的基本名称，显示最后的目录名或文件名。
* diff 文件1 文件2  -y -W 50	对比文件1和文件2【先备份，改的时候对比 cp log.txt log.txt.bak】
```
-y或--side-by-side 　将文件放在同一行比较
-W<宽度>或--width<宽度> 　在使用-y参数时，指定栏宽
```
* sort 文件	文件内容排序
* uniq 文件	文件内容去重
* sort 文件 | uniq -c | sort -r 
```
-r 降序排列
-c或--count 在每列旁边显示该行重复出现的次数。
```
* tail 文件	默认显示文件最后 10 行
	*tail -1000 /some/file | vim -	直接编辑 
```
-f	循环读取【实时更新for】
-n 行数	显示文件的尾部 n 行内容
```
* head 文件	默认显示前10行
* wc 文件	计算文件的行数、字数，以及字节数。
```
-l 显示行数
-w 显示字数
​```shell
* xargs 是给命令传递参数的一个过滤器，也是组合多个命令的一个工具。l 
	* cat test.txt | xargs 全部放在一行  
	* cat test.txt | xargs -n3  每行三个  
	* echo "nameXnameXnameXname" | xargs -dX  自定义分隔符X
	* cat 文件名 |xargs -i {} echo {} 循环打印文本内容
	* cat 文件名 |xargs -i {} useradd  {}⭐循环创建用户
```

```shell
* vim 文件	文本编辑工具
	* i 光标插入进入编辑模式 
	* 阅读模式
		* dd 删除一行 
		* u撤销上一步 
		* J合并一行 
		* ctrl+v 选中  
		* yy 复制整行
		* yw复制某个单词
		* y$复制光标到最后
		* y^复制光标到最前
		* p黏贴 
	* 底层命令行
		* :q退出，w保存，！强行
		* :set nu显示行号
		* :数字 到某一行 【:行号 跳到行号操作】
		* / 查找,n下一个，N上一个 
		* :s/1/2    搜索当前行第一个1并用2代替
		* :s/1/2/g  搜索当前行所有的1并用2代替
		* :%s/1/2/g   在整个文档中将1替换为2【%全局】
		* :s/1/2/c    每次替换都给出提示确认
		* :50,100 s/a/A/g	将50到100行的a改成A
```
-f：覆盖已经存在的目标文件而不给出提示。
-p：除复制文件的内容外，还把修改时间和访问权限也复制到新文件中。
-r：若给出的源文件是一个目录文件，此时将复制该目录下所有的子目录和文件。 
```
* sleep 6	睡眠6秒[睡眠默认以秒为单位]
* tmux	terminal multiplexer
	* 服务器的系统是 redhat、centos 或 fedora安装tmux用【yum install tmux】
	* tmux new -s 'session-name' 创建新会话 
	* tmux detach 离开会话
	* tmux ls 查看有哪些会话
	* tmux attach -t 'session-name' 接入会话
	* tmux kill-session -t 'session-name' 杀死会话 
	* tmux switch -t 'session-name' 切换会话
* curl
	* curl -o /dev/null -s -w %{http_code} www.linux.com   返回网站返回码
	* curl -O http://www.linux.com/hello.sh
	* curl -x 192.168.100.100:1080 http://www.linux.com  使用代理
	* curl url(获取该网址的文本信息)
* netstat	查看端口
	* netstat -antp | grep sshd
```
-a 或--all 显示所有连线中的套接字。
-n 或--numeric 直接使用IP地址，而不是通过域名服务器。
-p 或--programs 显示正在使用套接字的程序识别码和程序名称。
-t 或--tcp 显示TCP传输协议的连线情况。

```
* top	看系统性能

* free	查看内存使用率
	
	* free -m【df -h】
	
* ifconfig	查看ip

* ping www.baidu.com	查看网络连通性

* telnet ip port	 查看网络连通性[远程登录]

* lsof -P | grep ':3000'	查找正在使用3000端口的进程

* Useful Command Oneliner
	* For loop
	
```
	for i in `seq 1 100`; do echo $i; done
	for i in `seq 1 100`; do wget http://xxx/$i.png; done
	for i in `seq 1 100`; do wget http://11xxx/$(printf "%04d" $i).png; done
	```


​	
​	
​	* All IP Connect to my host
​	
​	```
​	$ netstat -lantp |  grep ESTABLISHED | awk '{print $5}' |  awk -F: '{print $1}' |  sort -u
​	```


​	
​	
​	* Repeat a command every 0.1 second
​	
​	```
​	watch -n 0.1 "ls -lh"
​	```


​	
​	
​	* Redirect tar extract to another directory
​	
​	```
​	tar xfz filename. tar.gz -C PathToDirectory
​	```


​	
​	
​	* Copy files between hosts⭐⭐
​	```
​	scp aaa.img target_username@target_host:
​	scp aaa.img target_username@target_host:/tmp/bbb.img
​	
​	rsync -avrz -e ssh source_dir/ target_username@target_host:/tmp/target_dir/【传文件，前传后】
​	```
​	* diff two different command outputs
​	
	```
	diff -urN <(command1 arg1) <(command2 arg1 arg2)
	vimdiff <(command1 arg1) <(command2 arg1 arg2)
	```

* git commod

```
git config --global user.name "ASxx" 
git config --global user.email "123456789@qq.com"
git代码库
  git clone ssh://git@47.101.174.187:10022/liwenchang/liwenchang-devops.git
  推送：
  git add README.md
  git add --all                           //相反的命令restore
  git commit -m "first commit"            //相反的命令 git rm 
  git remote add origin ssh://git@47.101.174.187:10022/liwenchang/liwenchang-devops.git
  git push -u origin master
  git知识了解：https://www.cnblogs.com/miracle77hp/articles/11163532.html
  拓展：
    git在push东西的时候，系统会检测本地的基础代码（就是更改前的代码）和仓库中的基础代码是不是一样，如果不一样就会报错
    git fetch origin/master           //同步远程分支，然后在执行下面的指令进行创建，就是新的分支
    git branch [branch-name]          //新建一个分支，但依然停留在当前分支
    git checkout -b [branch]          //新建一个分支，并切换到该分支
  分支操作：
    查看分支：
      git branch                  //查看当前分支
      git branch -a               //查看全部分支
    git日志：
      git log 
      git log -x      //x是变量，换成数字就是显示前x个提交的日志
    合并分支：
      第一步你要把你当前分支都add进暂存区，然后暂存区commit提交到分支，清空暂存区。
        git add .
        git commit -m ‘dev'                    //如果本地目录有要删减的地方，删减以后，加-a的这个参数，就会让远程和本地同步
        git push -u origin dev
      第二部切换要合并的分支，比如说我们切换到master分支，然后一定要pull一下最新的代码，我专门把这个步骤加粗一下：
        git checkout master
        git pull origin master
      第三步合并代码：
        git merge dev
      第四步：查看git状态，没有问题直接合并
        git status
        这一步查看状态就是看看又没有冲突，为提交，暂存区还有东西等问题，没有问题就可以直接解决了
        git push origin master
    新建分支：
```

```
docker pull registry.cn-hangzhou.aliyuncs.com/zhuyijun/oracle:19c
```

```
ss -tlp
ss -ntl
ss-atl
ss -tna


```



```shell
[root@localhost ansible]# ansible websrvs -m command -a 'chdir=/etc creates=/data/f1.txt cat centos-release'
192.168.241.129 | CHANGED | rc=0 >>
CentOS Linux release 7.6.1810 (Core)
[root@localhost ansible]# ansible websrvs -m command -a 'chdir=/etc removes=/data/f1.txt cat centos-release'
192.168.241.129 | SUCCESS | rc=0 >>
skipped, since /data/f1.txt does not exist
```

操作数据库

```shell
#!/bin/bash
#author bollenv@qq.com 2018-02-05
#查询数据库并将查询结果做参数发送HTTP请求
#SQL查询结果列数
columnNum=2
#通过参数行数和行索引位置
function getValue(){
  #调用方法传入的第一个参数，$0 表示方法名
  colIndex=$1
  #调用方法传入的第二个参数
  rowIndex=$2
  #定位到指定行，数组索引0为第一个元素
  #数学算术运算使用 $((a+b))
  idx=$(($columnNum*$colIndex+$rowIndex-1))
  #判读索引值是否大于结果行数
  #${#arrays_name[@]}获取数组长度
  if [ $idx -le ${#user_attrs[@]} ]; then
    echo ${user_attrs[$idx]}
  fi
}
#数据库查询结果，结果为每行从左到右每个单元格为一行（首行为SQL查询结果的列名）
#数组默认分割符号是空格，当查询结果中包含空格字符时，会导致一个字段被分割开，例如：create_time 2017-01-01 12:12:12 会变成两条 
#2017-01-01
#12:12:12
#因此，IFS=$'\t'采用tab来分割字段的值
#mysql -u 用户名 -p 密码 -h 主机host 数据库名 -e 执行脚本内容
IFS=$'\t'
user_attrs=(`mysql -udb_user -pdb_pwd -hdb_host dbname -e 'SELECT \`city_name\`,\`name\` FROM t_user"'`)
#循环遍历查询结果行数
for (( i=$columnNum; i<=${#user_attrs[@]}; i=i+1))
do
   #查询结果name为参数name的值，name为第二列，rowIndex传入 2
   #调用方法需要用``包上
   name=`getValue $i 2`
   #查询结果city_name为city_name的值，city_name为第一列，rowIndex传入 1
   city_name=`getValue $i 1`
   #url中含有大括号时需要转义
   url="https://api.yourdomain.com/api/register?params=\{\"name\":\"$name\",\"city_name\":\"$city_name\"}"
   echo $url
   result=$(curl -X GET $url)
   echo $result
   sleep 0.8
done
```

### CPU

```
一般来说，物理CPU个数×每颗核数就应该等于逻辑CPU的个数，如果不相等的话，则表示服务器的CPU支持超线程技术。
逻辑CPU个数（4个）=物理cpu数量（1个） x cpu cores（4） x 1(不支持ht超线程技术)
逻辑CPU个数（4个）=物理cpu数量（1个） x cpu cores（4） x 2(支持ht超线程技术)

查看物理CPU个数
cat /proc/cpuinfo | grep "physical id" | sort | uniq | wc –l

查看CPU核数
cat /proc/cpuinfo | grep  "cores" | uniq

查看逻辑CPU个数 
cat /proc/cpuinfo |grep "processor" |wc -l

查看CPU名称
cat /proc/cpuinfo | grep "model name"
cat /proc/cpuinfo | grep name | cut -f2 -d: | uniq -c
```

```shell
$ lscpu
Architecture:          x86_64  #架构 
CPU op-mode(s):        32-bit, 64-bit #运行方式
Byte Order:            Little Endian  #字节顺序
CPU(s):                2  #逻辑cpu颗数 
On-line CPU(s) list:   0,1  #在线CPU
Thread(s) per core:    2  #每个核心线程
Core(s) per socket:    1  #每个cpu插槽核数/每颗物理cpu核数 
Socket(s):             1  #cpu插槽数 
NUMA node(s):          1  #非统一内存访问节点
Vendor ID:             GenuineIntel  #cpu厂商ID 
CPU family:            6   #cpu系列 
Model:                 63  #型号编号
Model name:            Intel(R) Xeon(R) CPU E5-2680 v3 @ 2.50GHz #型号名称
Stepping:              2  #步进
CPU MHz:               2494.222  #cpu主频 
BogoMIPS:              4988.44
Hypervisor vendor:     KVM  #虚拟化架构
Virtualization type:   full  #cpu支持的虚拟化技术 
L1d cache:             32K  #一级缓存(具体为L1数据缓存） 
L1i cache:             32K  #一级缓存（具体为L1指令缓存） 
L2 cache:              256K #二级缓存
L3 cache:              30720K #三级缓存
NUMA node0 CPU(s):     0,1
```



# 现象

shell脚本中使用[curl](https://so.csdn.net/so/search?q=curl&spm=1001.2101.3001.7020)命令出现下列错误:

```
* Illegal characters found in URL
* Closing connection -1
  curl: (3) Illegal characters found in URL
```

# 原因

文本中含有`\r`等换行符,curl识别文本时将`\r`识别成了`\`和`r`两个字符

# 解决办法

- 方案一, 使用`vi` 命令检查
  例如:

```

vi test.sh 

:set ff?
```

输入`:set ff?` 前需要先按Esc键

如果出现`fileforma＝dos`那么就可以确定是这个问题
可以使用以下命令转换程序格式:

按`Esc`输入`:wq`命令保存



方案二, 使用`dos2unix `命令直接转换脚本格式为unix
例如:

```
dos2unix test.sh
```

方案三, 直接删除链接文本中的`\r`字符
命令如下:

```
URL=${URL%$'\r'}
```

```shell
  URL=$(curl -i -X GET -H "X-Auth-User: MyUserna,e" -H "X-Auth-Key: MyAPIKey" "https://urlToAuthServer.tld/auth/v1.0/" | grep "X-Storage-Url:" | awk '{print $2}')     

  URL=${URL%$'\r'}

  curl -X GET -H "X-Auth-Token: MyAuthTok" "${URL}/folder/myfile.txt" -o ./myfile.txt
```

1. *docker cp 宿主机文件/路径 容器名：容器内路径* ---宿主机到容器
2. *docker cp 容器名：文件/路径 宿主机路径*---容器到宿主机
3. 都是在宿主机上执行



**查看内核版本命令**

 1) cat /proc/version 

 2)uname -a （可以查看系统是多少位）

hostname~查看主机名

```
配置文件目录
etc
config
conf
引入第三方jar包目录
lib
可执行文件，系统命令，启动，停止
bin
```

```shell
URL=${URL%$'\r'}-----去掉字符串中看不出的\r

:nohl     取消高亮
```

# Linux curl命令详解

　　curl是一个非常实用的、用来与服务器之间传输数据的工具；支持的协议包括 (DICT, FILE, FTP, FTPS, GOPHER, HTTP, HTTPS, IMAP, IMAPS, LDAP, LDAPS, POP3, POP3S, RTMP, RTSP, SCP, SFTP, SMTP, SMTPS, TELNET and TFTP)，curl设计为无用户交互下完成工作；

　　curl提供了一大堆非常有用的功能，包括代理访问、用户认证、ftp上传下载、HTTP POST、SSL连接、cookie支持、断点续传...。

**一、curl命令语法：**

```
curl [options] [URL...]
```


**二、curl命令参数详解：**
　　由于linux curl功能十分强大，所以命令参数十分多，下表只是爱E族(aiezu.com)帅选出来的部分参数，更多参数请运行“man curl”命令查看。

|                            参数组                            | 参数                                                         | 描述                                                         |
| :----------------------------------------------------------: | :----------------------------------------------------------- | :----------------------------------------------------------- |
|                             url                              | url                                                          | 需要抓取的一到多个URLs； 多个下面通配符的方式： 　　1、http://{www,ftp,mail}.aiezu.com； 　　2、http://aiezu.com/images/[001-999].jpg； 　　3、http://aiezu.com/images/[1-999].html； 　　4、ftp://aiezu.com/file[a-z].txt |
|                           请 求 头                           | -H "name: value" --header "name: value"                      | (HTTP)添加一个http header(http请求头)；                      |
|                 -H "name:" --header "name:"                  | (HTTP)移除一个http header(http请求头)；                      |                                                              |
| -A "string" --user-agent "string" [【参考】](http://aiezu.com/article/linux_curl_referer_useragent.html) | (HTTP)设置Http请求头“User-Agent”，服务器通过“User-Agent”可以判断客户端使用的浏览器名称和操作系统类型，伪造此参数能导致服务器做出错误判断。 也可以使用“-H”, “--header option”设置此选项； |                                                              |
| -e <URL> --referer <URL> [【参考】](http://aiezu.com/article/linux_curl_referer_useragent.html) | (HTTP)设置访问时的来源页面，告诉http服务从哪个页面进入到此页面； -e "aiezu.com"相当于“-H "Referer: www.qq.com"”； |                                                              |
|                           响 应 头                           | -I --head                                                    | (HTTP)只输出HTTP-header，不获取内容(HTTP/FTP/FILE)。 用于HTTP服务时，获取页面的http头；  （如：curl -I http://aiezu.com） 用于FTP/FILE时，将会获取文件大小、最后修改时间；  （如：curl -I file://test.txt） |
|                         -i --include                         | (HTTP)输出HTTP头和返回内容；                                 |                                                              |
|                -D <file> --dump-header <file>                | (HTTP)转储http响应头到指定文件；                             |                                                              |
|                            cookie                            | -b name=data --cookie name=data [【参考】](http://aiezu.com/article/linux_curl_http_cookie.html) | (HTTP)发送cookie数据到HTTP服务器，数据格式为："NAME1=VALUE1; NAME2=VALUE2"；  如果行中没有“=”，将把参数值当作cookie文件名；  这个cookie数据可以是由服务器的http响应头“Set-Cookie:”行发送过来的； |
| -c filename --cookie-jar file name [【参考】](http://aiezu.com/article/linux_curl_http_cookie.html) | (HTTP)完成操作后将服务器返回的cookies保存到指定的文件； 指定参数值为“-”将定向到标准输出“如控制台”； |                                                              |
|                  -j --junk-session-cookies                   | (HTTP)告诉curl放弃所有的"session cookies"； 相当于重启浏览器； |                                                              |
|                             代理                             | -x host:port -x [protocol://[user:pwd@]host[:port] --proxy [protocol://[user:pwd@]host[:port] [【参考】](http://aiezu.com/article/linux_curl_proxy_http_socks.html) | 使用HTTP代理访问；如果未指定端口，默认使用8080端口; protocol默认为http_proxy，其他可能的值包括： http_proxy、HTTPS_PROXY、socks4、socks4a、socks5； 如： --proxy 8.8.8.8:8080； -x "http_proxy://aiezu:123@aiezu.com:80" |
|                       -p --proxytunnel                       | 将“-x”参数的代理，作为通道的方式去代理非HTTP协议，如ftp；    |                                                              |
| --socks4 <host[:port]> --socks4a <host[:port]> --socks5 <host[:port]> [【参考】](http://aiezu.com/article/linux_curl_proxy_http_socks.html) | 使用SOCKS4代理； 使用SOCKS4A代理； 使用SOCKS5代理； 此参数会覆盖“-x”参数； |                                                              |
| --proxy-anyauth --proxy-basic --proxy-diges --proxy-negotiate --proxy-ntlm | http代理认证方式，参考： --anyauth --basic --diges --negotiate --ntlm |                                                              |
|       -U <user:password> --proxy-user <user:password>        | 设置代理的用户名和密码；                                     |                                                              |
|                          数据 传输                           | -G --get [【参考】](http://aiezu.com/article/linux_curl_getpost_datafile_json.html) | 如果使用了此参数，“-d/”、“--data”、“--data-binary”参数设置的数据，讲附加在url上，以GET的方式请求； |
| -d @file -d "string" --data "string" --data-ascii "string" --data-binary "string" --data-urlencode "string" [【参考】](http://aiezu.com/article/linux_curl_getpost_datafile_json.html) | (HTTP)使用HTTP POST方式发送“key/value对”数据，相当于浏览器表单属性（method="POST"，enctype="application/x-www-form-urlencoded"） 　　-d，--data：HTTP方式POST数据； 　　--data-ascii：HTTP方式POST ascii数据； 　　--data-binary：HTTP方式POST二进制数据； 　　--data-urlencode：HTTP方式POST数据（进行urlencode）； 如果数据以“@”开头，后紧跟一个文件，将post文件内的内容； |                                                              |
| -F name=@file -F name=<file -F name=content --form name=content [【参考】](http://aiezu.com/article/linux_curl_getpost_datafile_json.html) | (HTTP)使用HTTP POST方式发送类似“表单字段”的多类型数据，相当于同时设置浏览器表单属性（method="POST"，enctype="multipart/form-data"），可以使用此参数上传二进制文件。  如果字段内容以“@”开头，剩下的部分应该是文件名，curl将会上传此文件，如： curl -F "pic=@pic.jpg" http://aiezu.com； curl -F "page=@a.html;type=text/html" http://aiezu.com curl -F "page=@/tmp/a;filename=a.txt" http://aiezu.com  如果字段内容以“<”开头，剩下的部分应该是文件名，curl将从文件中获取作为此字段的值，如：curl -F "text=<text.txt" http://aiezu.com； |                                                              |
|                  --form-string <key=value>                   | (HTTP)类似于“--form”，但是“@”、“<”无特殊含义；               |                                                              |
|                  -T file --upload-file file                  | 通过“put”的方式将文件传输到远程网址；  选项参数只使用字符"-"，将通过stdin读入文件内容； 如： cat test.txt\|curl "http://aiezu.com/a.php" -T -  curl "http://aiezu.com/a.php" -T - <test.txt  此参数也可以使用通配符： curl -T "{file1,file2}" http://aiezu.com curl -T "img[1-1000].png" http://aiezu.com |                                                              |
|                          断点 续传                           | -C <offset> --continue-at <offset>                           | 断点续转，从文件头的指定位置开始继续下载/上传； offset续传开始的位置，如果offset值为“-”，curl会自动从文件中识别起始位置开始传输； |
|                  -r <range> --range <range>                  | (HTTP/FTP/SFTP/FILE) 只传输内容的指定部分： 0-499：最前面500字节； -500：最后面500字节； 9500-：最前面9500字节； 0-0,-1：最前面和最后面的1字节； 100-199,500-599：两个100字节； |                                                              |
|                             认证                             | --basic                                                      | (HTTP)告诉curl使用HTTP Basic authentication（HTTP协议时），这是默认认证方式； |
|                            --ntlm                            | (HTTP)使用NTLM身份验证方式，用于HTTP协议； 一般用于IIS使用NTLM的网站； |                                                              |
|                           --digest                           | (HTTP)使用HTTP Digest authentication加密，用于HTTP协议； 配合“-u/--user”选项，防止密码使用明文方式发送； |                                                              |
|                         --negotiate                          | (HTTP)使用GSS-Negotiate authentication方式，用于HTTP协议； 它主要目的是为它的主要目的是为kerberos5认证提供支持支持； |                                                              |
|                          --anyauth                           | (HTTP)告诉curl自动选择合适的身份认证方法，并选用最安全的方式； |                                                              |
|            -u user:password --user user:password             | 使用用户名、密码认证，此参数会覆盖“-n”、“--netrc”和“--netrc-optional”选项；  如果你只提供用户名，curl将要求你输入密码；  如果你使用“SSPI”开启的curl库做“NTLM”认证，可以使用不含用户名密码的“-u:”选项，强制curl使用当前登录的用户名密码进行认证；  此参数相当于设置http头“Authorization：”； |                                                              |
|                             证书                             | -E <证书[:密码]> --cert <证书[:密码]>                        | (SSL)指定“PEM”格式的证书文件和证书密码；                     |
|                      --cert-type <type>                      | (SSL)告诉curl所提供证书的类型：PEM、DER、ENG等； 默认为“PEM”； |                                                              |
|                      --cacert <CA证书>                       | (SSL)告诉curl所以指定的CA证书文件，必须是“PEM”格式；         |                                                              |
|                    --capath <CA证书路径>                     | (SSL)告诉curl所以指定目录下的CA证书用来验证； 这些证书必须是“PEM”格式； |                                                              |
|                       --crlfile <file>                       | (HTTPS/FTPS)提供一个PEM格式的文件，用于指定被吊销的证书列表； |                                                              |
|                        -k --insecure                         | (SSL)设置此选项将允许使用无证书的不安全SSL进行连接和传输。   |                                                              |
|                           SSL 其他                           | --ciphers <list of ciphers>                                  | (SSL)指定SSL要使用的加密方式；如：“aes_256_sha_256”；        |
|                       --engine <name>                        | 设置一个OpenSSL加密引擎用于加密操作； 使用“curl --engine list”查看支持的加密引擎列表； |                                                              |
|                        --random-file                         | (SSL)指定包含随机数据的文件路径名；数据是用来为SSL连接产生随机种子为； |                                                              |
|                      --egd-file <file>                       | (SSL)为随机种子生成器EGD(Entropy Gathering Daemon socket)指定的路径名； |                                                              |
| -1/--tlsv1 --tlsv1.0 --tlsv1.1 --tlsv1.2 -2/--sslv2 -3/--sslv3 | (SSL)使用TLS版本2与远程服务器通讯； (SSL)使用TLS 1.0版本与远程服务器通讯； (SSL)使用TLS 1.1版本与远程服务器通讯； (SSL)使用TLS 1.2版本与远程服务器通讯； (SSL)使用SSL版本2与远程服务器通讯； (SSL)使用SSL版本3与远程服务器通讯； |                                                              |
|                          私钥 公钥                           | --key <key>                                                  | (SSL/SSH)指定一个私钥文件名；为指定时自动尝试使用下面文件：“~/.ssh/id_rsa”、“~/.ssh/id_dsa”、“./id_rsa'”、 “./id_dsa”； |
|                      --key-type <type>                       | (SSL)指定私钥文件类型，支持：DER、PEM、ENG，默认是PEM；      |                                                              |
|                       --pass <phrase>                        | (SSL/SSH)指定私钥文件的密码；                                |                                                              |
|                        --pubkey <key>                        | (SSH)使用指定文件提供的您公钥；                              |                                                              |
|                             FTP                              | -P --ftp-port <接口>                                         | (FTP)FTP主动模式时，设置一个地址等待服务器的连接，如： 网卡：eth1 IP：8.8.8.8 主机名：aiezu.com 可以加端口号：eth1:20000-21000; |
|                            --crlf                            | (FTP)上传时将换行符(LF)转换为回车换行(CRLF)；                |                                                              |
|                     --ftp-account [data]                     | (FTP)ftp帐号信息；                                           |                                                              |
|                    --ftp-method [method]                     | (FTP)可选值：multicwd/nocwd/singlecwd；                      |                                                              |
|                          --ftp-pasv                          | (FTP)使用使用PASV(被动)/EPSV模式；                           |                                                              |
|                      --ftp-skip-pasv-ip                      | (FTP)使用PASV的时,跳过指定IP；                               |                                                              |
|                      --ftp-create-dirs                       | (FTP)上传时自动创建远程目录；                                |                                                              |
|                        -l --list-only                        | (FTP)列出ftp文件列表；                                       |                                                              |
|                        -B --use-ascii                        | (FTP/LDAP)使用Ascii传输模式，用于FTP、LDAP；在ftp中相当与使用了“type=A;”模式。 |                                                              |
|                        --disable-epsv                        | (FTP)告诉curl在PASV(被动模式)时不要使用EPSV；                |                                                              |
|                        --disable-eprt                        | (FTP)告诉curl在主动模式时禁用EPRT和LPRT；                    |                                                              |
|                             限速                             | --limit-rate <speed>                                         | 限制curl使用的最大带宽；如果未指定单位，默认单位为“bytes/秒”，你也可以指定单位为“K”、“M”、“G”等单位，如：“--limit-rate 1m”为限制最大使用带宽为“1m字节/秒”； |
|                    -y --speed-time <time>                    | If a download is slower than speed-limit bytes per second during a speed-time period, the download gets aborted. If speed-time is used, the default speed-limit will be 1 unless set with -Y. This option controls transfers and thus will not affect slow connects etc. If this is a concern for you, try the --connect-timeout option. |                                                              |
|                   -Y --speed-limit <speed>                   | If a download is slower than this given speed (in bytes per second) for speed-time seconds it gets aborted. speed-time is set with -y and is 30 if not set. |                                                              |
|                          其他 选项                           | -0/--http1.0                                                 | (HTTP) 强制curl使用HTTP 1.0而不是使用默认的HTTP 1.1；        |
|                      --interface <name>                      | 使用指定的网卡接口访问； curl --interface eth0 http://aiezu.com curl --interface 10.0.0.101 http://aiezu.com |                                                              |
|               -X <command> --request <command>               | （HTTP）指定与服务器通信使用的请求方法，如：GET、PUT、POST、DELETE等，默认GET； |                                                              |
|                  --keepalive-time <seconds>                  | 设置keepalive时间                                            |                                                              |
|                        --no-keepalive                        | 关闭keepalive功能；                                          |                                                              |
|                         --no-buffer                          | 禁用对输出流缓冲；                                           |                                                              |
|                           --buffer                           | 启用输出流缓冲；                                             |                                                              |
|                        -L --location                         | (HTTP/HTTPS)追随http响应头“Location：”定向到跳转后的页面； (在http响应码为3XX时使用，如301跳转、302跳转) |                                                              |
|                      --location-trusted                      | (HTTP/HTTPS)同“--location”，但跳转后会发送跳转前的用户名和密码； |                                                              |
|                         --compressed                         | (HTTP)请求对返回内容使用压缩算法进行压缩；curl支持对gzip压缩进行解压； |                                                              |
|                 --connect-timeout <seconds>                  | 指定最大连接超时，单位“秒”；                                 |                                                              |
|                -m seconds --max-time seconds                 | 限制整个curl操作的最长时间，单位为秒；                       |                                                              |
|                         -s --silent                          | 安静模式。不要显示进度表或错误消息；                         |                                                              |
|                      -# --progress-bar                       | 显示进度条；                                                 |                                                              |
|                          错误 选项                           | -f --fail                                                    | (HTTP)连接失败时（400以上错误）不返回默认错误页面，而是返回一个curl错误码“22”； |
| --retry <num> --retry-delay <seconds> --retry-max-time <seconds> | 失败重试次数； 重试间隔时间； 最大重试时间；                 |                                                              |
|                       -S --show-error                        | 安静模式下显示错误信息；                                     |                                                              |
|                       --stderr <file>                        | 错误信息保存文件；                                           |                                                              |
|                             输出                             | -o file --output file                                        | 将返回内容输出到文件。 如果是用过通配符获取多个url，可以使用“#”后跟“数字序号”，curl会自动将它替换对应的关键词，如： 　　curl "http://aiezu.com/{a,b}.txt" -o "#1.txt"; 　　将保存为：“a.txt”,“b.txt”;  　　curl "http://aiezu.com/{a,b}_[1-3].txt" -o "#1#2.txt"; 　　将保存为：a1.txt、a2.txt、a3.txt、b1.txt、b2.txt、b3.txt  　　如果要根据规则创建保存目录，参考：“--create-dirs”  指定“-”将定向到标准输出“如控制台”； |
|                       -O --remote-name                       | 将返回内容输出到当前目录下，和url中文件名相同的文件中（不含目录）； |                                                              |
|                        --create-dirs                         | 与“-o”参数配合使用，创建必要的本地目录层次结构               |                                                              |
|                    -w --write-out format                     | 操作完成后在返回信息尾部追加指定的内容；要追加的内容可以是一个字符串“string”、从文件中获取“@filename”、从标准输入中获取“@-”  格式参数中可以用%{variable_name} 方式使用响应信息的相关变量，如：%{content_type}、%{http_code}、%{local_ip}...，更多变量参考“man curl”获取；  格式参数可以使用“\n”、“\r”、“\t”等转义字符； |                                                              |
|                             调试                             | --trace <file>                                               | 转储所有传入和传出的数据到文件，包括描述信息； 使用“-”作为文件名将输出发送到标准输出。 |
|                      --trace-ascii file                      | 转储所有传入和传出的数据到文件，包括描述信息，只转储ASCII部分，更容易阅读； 使用“-”作为文件名将输出发送到标准输出。 这个选项会覆盖之前使用的-v、 --verbose、 --trace-ascii选项； |                                                              |
|                         --trace-time                         | 转储文件中添加时间信息；                                     |                                                              |
|                  -K --config <config file>                   | 从配置文件中读取参数，参考：http://curl.haxx.se/docs/        |                                                              |
|                         -v --verbose                         | 显示更详细的信息，调试时使用；                               |                                                              |
|                             帮助                             | -M --manual                                                  | 显示完整的帮助手册；                                         |
|                          -h --help                           | linux curl用法帮助；                                         |                                                              |


**三、Linux curl命令退出码：**
下面是linux curl命令的错误代码和她们的相应的错误消息，可能会出现在恶劣的环境。

| 退出码 | 错误描述                                                     |
| :----: | :----------------------------------------------------------- |
|   1    | Unsupported protocol. This build of curl has no support for this protocol. |
|   2    | Failed to initialize.                                        |
|   3    | URL malformed. The syntax was not correct.                   |
|   5    | Couldn't resolve proxy. The given proxy host could not be resolved. |
|   6    | Couldn't resolve host. The given remote host was not resolved. |
|   7    | Failed to connect to host.                                   |
|   8    | FTP weird server reply. The server sent data curl couldn't parse. |
|   9    | FTP access denied. The server denied login or denied access to the particular resource or directory you wanted to reach. Most often you tried to change to a directory that doesn't exist on the server. |
|   11   | FTP weird PASS reply. Curl couldn't parse the reply sent to the PASS request. |
|   13   | FTP weird PASV reply, Curl couldn't parse the reply sent to the PASV request. |
|   14   | FTP weird 227 format. Curl couldn't parse the 227-line the server sent. |
|   15   | FTP can't get host. Couldn't resolve the host IP we got in the 227-line. |
|   17   | FTP couldn't set binary. Couldn't change transfer method to binary. |
|   18   | Partial file. Only a part of the file was transferred.       |
|   19   | FTP couldn't download/access the given file, the RETR (or similar) command failed. |
|   21   | FTP quote error. A quote command returned error from the server. |
|   22   | HTTP page not retrieved. The requested url was not found or returned another error with the HTTP error code being 400 or above. This return code only appears if -f/--fail is used. |
|   23   | Write error. Curl couldn't write data to a local filesystem or similar. |
|   25   | FTP couldn't STOR file. The server denied the STOR operation, used for FTP uploading. |
|   26   | Read error. Various reading problems.                        |
|   27   | Out of memory. A memory allocation request failed.           |
|   28   | Operation timeout. The specified time-out period was reached according to the conditions. |
|   30   | FTP PORT failed. The PORT command failed. Not all FTP servers support the PORT command, try doing a transfer using PASV instead! |
|   31   | FTP couldn't use REST. The REST command failed. This command is used for resumed FTP transfers. |
|   33   | HTTP range error. The range "command" didn't work.           |
|   34   | HTTP post error. Internal post-request generation error.     |
|   35   | SSL connect error. The SSL handshaking failed.               |
|   36   | FTP bad download resume. Couldn't continue an earlier aborted download. |
|   37   | FILE couldn't read file. Failed to open the file. Permissions? |
|   38   | LDAP cannot bind. LDAP bind operation failed.                |
|   39   | LDAP search failed.                                          |
|   41   | Function not found. A required LDAP function was not found.  |
|   42   | Aborted by callback. An application told curl to abort the operation. |
|   43   | Internal error. A function was called with a bad parameter.  |
|   45   | Interface error. A specified outgoing interface could not be used. |
|   47   | Too many redirects. When following redirects, curl hit the maximum amount. |
|   48   | Unknown TELNET option specified.                             |
|   49   | Malformed telnet option.                                     |
|   51   | The peer's SSL certificate or SSH MD5 fingerprint was not ok. |
|   52   | The server didn't reply anything, which here is considered an error. |
|   53   | SSL crypto engine not found.                                 |
|   54   | Cannot set SSL crypto engine as default.                     |
|   55   | Failed sending network data.                                 |
|   56   | Failure in receiving network data.                           |
|   58   | Problem with the local certificate.                          |
|   59   | Couldn't use specified SSL cipher.                           |
|   60   | Peer certificate cannot be authenticated with known CA certificates. |
|   61   | Unrecognized transfer encoding.                              |
|   62   | Invalid LDAP URL.                                            |
|   63   | Maximum file size exceeded.                                  |
|   64   | Requested FTP SSL level failed.                              |
|   65   | Sending the data requires a rewind that failed.              |
|   66   | Failed to initialize SSL Engine.                             |
|   67   | The user name, password, or similar was not accepted and curl failed to log in. |
|   68   | File not found on TFTP server.                               |
|   69   | Permission problem on TFTP server.                           |
|   70   | Out of disk space on TFTP server.                            |
|   71   | Illegal TFTP operation.                                      |
|   72   | Unknown TFTP transfer ID.                                    |
|   73   | File already exists (TFTP).                                  |
|   74   | No such user (TFTP).                                         |
|   75   | Character conversion failed.                                 |
|   76   | Character conversion functions required.                     |
|   77   | Problem with reading the SSL CA cert (path? access rights?). |
|   78   | The resource referenced in the URL does not exist.           |
|   79   | An unspecified error occurred during the SSH session.        |
|   80   | Failed to shut down the SSL connection.                      |
|   82   | Could not load CRL file, missing or wrong format (added in 7.19.0). |
|   83   | Issuer check failed (added in 7.19.0).                       |
|   XX   | More error codes will appear here in future releases. The existing ones are meant to never change. |


**四、用法演示：**
1、下载页面：

```
curl -o index.html http:``//aiezu``.com
```


2、下载文件并显示简单进度条：

```
curl -``# -o centos6.8.iso http://mirrors.aliyun.com/centos/6.8/isos/x86_64/CentOS-6.8-x86_64-minimal.iso
```


3、断点续传：

```
#继续完成上次终止的未完成的下载``curl -``# -o centos6.8.iso -C - http://mirrors.aliyun.com/centos/6.8/isos/x86_64/CentOS-6.8-x86_64-minimal.iso
```


4、伪造来源页面：

```
#告诉爱E族，我是从百度来的``curl -e http:``//baidu``.com http:``//aiezu``.com
```


 5、伪造代理设备：

```
#告诉爱E族，我是GOOGLE爬虫蜘蛛（其实我是curl命令）``curl -A ``" Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)"` `http:``//aiezu``.com` `#告诉爱E族，我用的是微信内置浏览器``curl -A ``"Mozilla/5.0 AppleWebKit/600 Mobile MicroMessenger/6.0"` `http:``//aiezu``.com
```


6、http头：

```
# 看看本站的http头是怎么样的``curl -I http:``//aiezu``.com
```

输出：

```
HTTP/1.1 200 OK``Date: Fri, 25 Nov 2016 16:45:49 GMT``Server: Apache``Set-Cookie: rox__Session=abdrt8vesprhnpc3f63p1df7j4; path=/``Expires: Thu, 19 Nov 1981 08:52:00 GMT``Cache-Control: no-store, no-cache, must-revalidate, post-check=0, pre-check=0``Pragma: no-cache``Vary: Accept-Encoding``Content-Type: text/html; charset=utf-8
```


6、设置http请求头：

```
curl -H ``"Cache-Control:no-cache"` `http:``//aiezu``.com
```


7、发送表单数据：

```
curl -F ``"pic=@logo.png"` `-F ``"site=aiezu"` `http:``//aiezu``.com/
```


8、发送cookie：

```
curl -b ``"domain=aiezu.com"` `http:``//aiezu``.com
```