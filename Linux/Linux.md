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
	```
	$ netstat -lantp |  grep ESTABLISHED | awk '{print $5}' |  awk -F: '{print $1}' |  sort -u
	```


​	
​	
​	* Repeat a command every 0.1 second
​	
	```
	watch -n 0.1 "ls -lh"
	```


​	
​	
​	* Redirect tar extract to another directory
​	
	```
	tar xfz filename. tar.gz -C PathToDirectory
	```


​	
​	
​	* Copy files between hosts⭐⭐
​	```
​	scp aaa.img target_username@target_host:
​	scp aaa.img target_username@target_host:/tmp/bbb.img
​	
	rsync -avrz -e ssh source_dir/ target_username@target_host:/tmp/target_dir/【传文件，前传后】
	```
	* diff two different command outputs
	
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

