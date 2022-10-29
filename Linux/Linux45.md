## 前瞻

### 操作系统概念

操作系统（Operation System，OS）是**管理硬件**和**控制软件**运行的计算机**程序**, 是直接运行在“裸机”上的系统软件, 任何其他软件都必须在操作系统的支持下才能运行.

- 主要作用是**向下控制硬件向上支持软件的运行**，具有承上启下的作用.
- 大量的软件都直接依赖于操作系统的支持.

未做一个程序员之前  可能你们知道的操作系统就是windows系列或者mac     但是在程序员圈有一个操作系统 LInux，linux在生活中无处不在。   淘宝，腾讯什么的服务都是部署在LInux服务器上。手机上使用的安卓系统也是Linux的一个分支。家里用的路由器，机顶盒也可能是Linux系统的，银行里的ATM机，电视上的超级计算机如天河，神威太湖之光，基本上都是Linux系统；智能电视，智能手表，店里的POS机等等都是基于Linux系统的android系统。实际工作中，百分之九十以上的企业服务部署都是在linux系统上，而不是windows。  所以问题来了 为什么使用linux？

### 为什么要学习Linux？

- 首先，在服务器端，因为其**稳定、可靠、免费**的特点，Linux占据了大量的市场份额，世界上大部分公司的的后台服务器都是Linux系统，Linux可以在服务器上稳定安全地长期运行；
- 其次，由于**安全**性，在银行，政府等对信息安全要求较高的场所，使用的操作系统大部分也是Linux，基本不会使用安全性差的windows，而且绝大多数的黑客攻击手段和病毒都是针对windows系统的；
- 最后，在IT工作者的眼里，个人电脑端的Linux系统则是最适合于编程学习的操作系统，并且在其开源环境和社区里，可以最大程度地帮助一个程序员成长。
- Linux**占用系统资源很少**，一台linux服务器可“长期高教、稳定地工作“，windows由于系统不能很好地自动释放系统资源，必须每隔一段时间就重启系统一次。
- Linux开源，免费 可以根据自己的需求对操作系统进行**二次开发**，而且我们可以获取到整个操作系统的源码，系统到底对我们的电脑做了啥我们一清二楚。不像Windows、MacOS那样闭源，鬼知道他们对我们的隐私数据做了什么。

![image-20210727145724921](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210727145725.png)

### Linux发展史

1973 年初, 美国的 **Thompson** 和 **Ritchie** 使用 **C 语言**开发了 **Unix 操作系统**.

![image-20210727145753306](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210727145753.png)

​										**肯·汤普逊（左）和 丹尼斯·里奇（右）**

由于 Unix 版权问题, 大学不能再使用 Unix 源代码. 荷兰的 **Andrew S. Tanenbaum (塔能鲍姆)** 教授为了教学于 1987 年自行开发与 UNIX 兼容的 Minix 操作系统.

![image-20210727145815061](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210727145815.png)

​											**安德鲁·斯图尔特·塔能鲍姆**

1991 年, **林纳斯(Linus)** 就读于赫尔辛基大学期间, 受到 Minix 的影响, 并在 Minix 操作平台建立了一个新的操作系统的内核, 他把它叫做 Linux.

![image-20210727145831765](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210727145831.png)

1. 1993年, 大约有 100 余名程序员参与了 **Linux 内核**代码编写/修改工作, 其中核心组由5人组成.
2. 1994年3月，Linux 1.0 按照完全自由免费的协议发布.

https://blog.csdn.net/wolfking0608/article/details/79564051 (Git的诞生)

> 什么是内核?

内核, 是一个操作系统的核心. 是基于硬件的第一层软件扩充, 提供操作系统的最基本的功能, 是操作系统工作的基础.

它负责管理系统的进程、内存、设备驱动程序、文件和网络系统，决定着系统的性能和稳定性。

说白了内核 就是整个系统的核心 然后在这个核心的基础上在添加一些定制功能 然后去给用户使用 这个就是下面要说的发行版

### Linux 发行版

Linux发行版则是前述那些东西的基础上添加了一些工具软件的基础上构成的一套庞大复杂的操作系统。虽然内核都是一样的，但添加部分各不相同，这就构成了不同的发行版本。

Linux本身指的是一个操作系统内核，**只有内核是无法直接使用的**。我们需要的，可以使用的**操作系统是一个包含了内核和一批有用的程序的的一个集合体**，这个就是Linux发行版。

普通用户、甚至专业技术人员直接使用内核难度比较大. Linux 内核主要作为 Linux 发行版的一部分而使用.

这些发行版由个人, 松散组织的团队, 以及商业机构和志愿者组织编写. 它们通常包括了其他的系统软件和应用软件, 以及一个用来简化系统安装、软件安装、软件升级的集成管理器。 大多数系统还包括了像提供用户图形界面.

一个典型的 Linux 发行版包括: Linux 内核、命令行 SHELL、图形界面, 并包含数千种办公套件, 编译器, 文本编辑器等应用软件.

常见的 Linux 发行版本如下:

- **Ubuntu**

  国内乃至全球热门的Linux发行版。也是各种推荐入门Linux爱好者安装的一个Linux发行版。它的特点主要有以下:
  	1.安装简单
      2.Unity3D图形界面,比较华丽(因人而异)
      3.对一些专有驱动支持比较好,例如显卡驱动
      4.社区比较活跃,几乎遇到的问题都可以找到答案
      5.版本更新较快,基本半年一个版本

- Redhat

- Fedora

- openSUSE

- Debian

- **CentOS**   

  现在是大名鼎鼎的 RedHat Linux的社区版(可以说是剔除了专有代码的 RedHat),其特点就是相当相当的稳定,版本更新紧跟 RedHat。非常适合作为服务器操作系统使用

## 1.安装Centos

**见  windows安装Centos7.docx**

Centos7 阿里云下载地址 http://mirrors.aliyun.com/centos/7/isos/x86_64/

## 2.Linux操作系统核心命令

 linux 目录结构和Windows很不一样  最大的特点 linux没有盘符概念的

### 2.1Linux中的目录结构

![image-20210727150030780](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210727150030.png)

Linux跟windows不一样，windows有磁盘概念分c,d,e什么的， linux上是没有盘符概念的，不同的用户在home目录下有独立的目录，其他公共目录公用

- /：根目录，

  一般根目录下只存放目录，在 linux 下有且只有一个根目录，所有的东西都是从这里开始

  - 当在终端里输入 `/home`，其实是在告诉电脑，先从 `/`（根目录）开始，再进入到 `home` 目录

- /bin、/usr/bin：可执行二进制文件的目录，如常用的命令 ls、tar、mv、cat 等

- /boot：放置 linux 系统启动时用到的一些文件，如 linux 的内核文件：`/boot/vmlinuz`，系统引导管理器：`/boot/grub`

- /dev：存放linux系统下的设备文件，访问该目录下某个文件，相当于访问某个设备，常用的是挂载光驱`mount /dev/cdrom /mnt`

- /etc：系统配置文件存放的目录，不建议在此目录下存放可执行文件，重要的配置文件有

  - /etc/inittab
  - /etc/fstab
  - /etc/init.d
  - /etc/X11
  - /etc/sysconfig
  - /etc/xinetd.d

- /home：系统默认的用户家目录，新增用户账号时，用户的家目录都存放在此目录下

  - `~` 表示当前用户的家目录
  - `~edu` 表示用户 `edu` 的家目录

- /lib、/usr/lib、/usr/local/lib：系统使用的函数库的目录，程序在执行过程中，需要调用一些额外的参数时需要函数库的协助

- /lost+fount：系统异常产生错误时，会将一些遗失的片段放置于此目录下

- /mnt: /media：光盘默认挂载点，通常光盘挂载于 /mnt/cdrom 下，也不一定，可以选择任意位置进行挂载

- /opt：给主机额外安装软件所摆放的目录

- /proc：此目录的数据都在内存中，如系统核心，外部设备，网络状态，由于数据都存放于内存中，所以不占用磁盘空间，比较重要的文件有：/proc/cpuinfo、/proc/interrupts、/proc/dma、/proc/ioports、/proc/net/* 等

- /root：系统管理员root的家目录

- /sbin、/usr/sbin、/usr/local/sbin：放置系统管理员使用的可执行命令，如 fdisk、shutdown、mount 等。与 /bin 不同的是，这几个目录是给系统管理员 root 使用的命令，一般用户只能"查看"而不能设置和使用

- /tmp：一般用户或正在执行的程序临时存放文件的目录，任何人都可以访问，重要数据不可放置在此目录下

- /srv：服务启动之后需要访问的数据目录，如 www 服务需要访问的网页数据存放在 /srv/www 内

- /usr：应用程序存放目录

  - /usr/bin：存放应用程序可执行二进制文件的目录
  - /usr/share：存放共享数据
  - /usr/lib：存放不能直接运行的，却是许多程序运行所必需的一些函数库文件
  - /usr/local：存放软件升级包
  - /usr/share/doc：系统说明文件存放目录
  - /usr/share/man：程序说明文件存放目录

- /var：放置系统执行过程中经常变化的文件

  - /var/log：随时更改的日志文件
  - /var/spool/mail：邮件存放的目录
  - /var/run：程序或服务启动后，其 PID 存放在该目录下

**重点:** **/home/用户名** 是用户的家目录.

 因为终端是无界面的，那我们如何去编辑操作文件呢？

### 2.2 VI编辑器

#### 2.2.1 vi与vim的区别

vi编辑器只能运行于unix中，而vim不仅可以运行于unix，还可用于windows、mac等多操作平台。

vim完全兼容vi 可以把vim当成vi来使用。

**小结**：**vi和vim都是Linux中的编辑器，不同的是vim比较高级，可以视为vi的升级版本。vi使用于文本编辑，但是vim更适用于coding。**

安装 vim     yum -y install vim*

安装ifconfig   yum install net-tools.x86_64

#### 2.2.2 vim编辑文件的基本流程

1、打开vim的命令:vim 文件名

2、打开文件之后,vim处于命令模式

3、在命令模式下:a键,i键可以进入编辑模式

​	a键代表在光标位置进行增加
​	i键代表在光标位置进行插入

​	wq 保存并退出

​	q 退出 不保存此次修改

4、编辑模式切换到命令榎式:esc键

物料文档

```
【学习进行时】国家主席习近平11日签署主席令，根据十三届全国人大常委会第二十一次会议11日下午表决通过的全国人大常委会关于授予在抗击新冠肺炎疫情斗争中作出杰出贡献的人士国家勋章和国家荣誉称号的决定，授予钟南山“共和国勋章”，授予张伯礼、张定宇、陈薇（女）“人民英雄”国家荣誉称号。他们有何事迹？他们身上体现了怎样的精神？新华社《学习进行时》原创品牌栏目“讲习所”今天推出文章，为您解读。

　　国家主席习近平11日签署主席令，根据十三届全国人大常委会第二十一次会议11日下午表决通过的全国人大常委会关于授予在抗击新冠肺炎疫情斗争中作出杰出贡献的人士国家勋章和国家荣誉称号的决定，授予钟南山“共和国勋章”，授予张伯礼、张定宇、陈薇（女）“人民英雄”国家荣誉称号。



　　钟南山长期致力于重大呼吸道传染病及慢性呼吸系统疾病的研究、预防与治疗，成果丰硕，实绩突出。新冠肺炎疫情发生后，他敢医敢言，提出存在“人传人”现象，强调严格防控，领导撰写新冠肺炎诊疗方案，在疫情防控、重症救治、科研攻关等方面作出杰出贡献。荣获国家科学技术进步奖一等奖和“全国先进工作者”“改革先锋”等称号。



　　张伯礼长期致力于中医药现代化研究，奠定中医素质教育和国际教育的标准化工作基础，推动中医药事业传承创新发展。新冠肺炎疫情发生后，他主持研究制定中西医结合救治方案，指导中医药全过程介入新冠肺炎救治，取得显著成效，为疫情防控作出重大贡献。荣获国家科学技术进步奖一等奖和“全国优秀共产党员”“全国先进工作者”等称号。



　　张定宇长期在医疗一线工作，曾带队赴汶川抗震救灾，多次参加国际医疗援助。2019年12月29日，在收治首批7名不明原因肺炎患者后，他立即组建隔离病区，率先采集样本开展病毒检测，组织动员遗体捐献，为确认新冠病毒赢得了时间，为开展新冠肺炎病理研究创造了条件。作为渐冻症患者，他冲锋在前，身先士卒，带领金银潭医院干部职工共救治2800余名新冠肺炎患者，为打赢湖北保卫战、武汉保卫战作出重大贡献。荣获“全国卫生健康系统新冠肺炎疫情防控工作先进个人”称号。



　　陈薇长期致力于生物危害防控研究，研制出我军首个SARS预防生物新药“重组人干扰素ω”、全球首个获批新药证书的埃博拉疫苗。新冠肺炎疫情发生后，她闻令即动，紧急奔赴武汉执行科研攻关和防控指导任务，在基础研究、疫苗、防护药物研发方面取得重大成果，为疫情防控作出重大贡献。荣获“全军防治非典先进个人”“全国十大杰出青年”等称号。

　　新冠肺炎疫情，是新中国成立以来在我国发生的传播速度最快、感染范围最广、防控难度最大的一次重大突发公共卫生事件。“沧海横流，方显英雄本色。”在这场严峻斗争中，各级党组织和广大党员、干部冲锋在前、英勇奋战，广大医务工作者白衣执甲、逆行出征，人民解放军指战员闻令即动、勇挑重担，广大社区工作者、公安干警、基层干部、下沉干部、志愿者不惧风雨、坚守一线，广大群众众志成城、踊跃参与，涌现出一大批可歌可泣的先进典型和感人事迹，钟南山、张伯礼、张定宇、陈薇就是其中的杰出代表。

　　崇尚英雄才会产生英雄，争做英雄才能英雄辈出。隆重表彰他们，目的是弘扬他们的崇高品质。

　　2019年9月29日，在国家勋章和国家荣誉称号颁授仪式上，习近平对英雄模范的三种崇高品质作了生动阐释。

　　——忠诚，就是英雄模范们都对党和人民事业矢志不渝、百折不挠，坚守一心为民的理想信念，坚守为中国人民谋幸福、为中华民族谋复兴的初心使命，用一生的努力谱写了感天动地的英雄壮歌。

　　——执着，就是英雄模范们都在党和人民最需要的地方冲锋陷阵、顽强拼搏，几十年如一日埋头苦干，为国为民奉献的志向坚定不移，对事业的坚守无怨无悔，为民族复兴拼搏奋斗的赤子之心始终不改。

　　——朴实，就是英雄模范们都在平凡的工作岗位上忘我工作、无私奉献，不计个人得失，舍小家顾大家，具有功成不必在我、功成必定有我的崇高精神，其中很多同志都是做隐姓埋名人、干惊天动地事的典型，展现了一种伟大的无我境界。

　　经过全国上下共同努力，目前我国疫情防控取得重大战略成果，在疫情防控和经济恢复上都走在世界前列。这是千千万万英雄模范和广大人民群众一起拼出来、干出来的。

　　英雄模范们用行动再次证明，伟大出自平凡，平凡造就伟大。习近平曾说，只要有坚定的理想信念、不懈的奋斗精神，脚踏实地把每件平凡的事做好，一切平凡的人都可以获得不平凡的人生，一切平凡的工作都可以创造不平凡的成就。
　　资料图，甘南 摄

8月9日，荆州市开发区联合街道一名68岁女退休职工因病住院时，经新冠病毒核酸检测为阳性，该女性为2月8日确诊的新冠肺炎患者治愈数月后再次复阳，非新发病例。目前该患者再次隔离治疗，所有接触者均进行核酸检测为阴性，其居所和活动区域均已彻底消毒，风险完全控制。尚无证据表明复阳病例存在传播风险，请市民不必恐慌、不信谣、不传谣。提醒广大市民常态化形势下也要重视个人防护。

（原标题：荆州一名2月确诊的新冠患者复阳，非新发病例）

延伸阅读

疫情报告：31省区市新增确诊25例

今天，国家卫生健康委员会公布了最新疫情数据，8月11日0—24时，31个省（自治区、直辖市）和新疆生产建设兵团报告新增确诊病例25例，其中境外输入病例16例（广东6例，上海4例，内蒙古1例，浙江1例，福建1例，山东1例，四川1例，陕西1例），本土病例9例（均在新疆）；无新增死亡病例；新增疑似病例1例，为境外输入病例（在上海）。

当日新增治愈出院病例58例，解除医学观察的密切接触者1385人，重症病例较前一日减少4例。

境外输入现有确诊病例158例（其中重症病例1例），现有疑似病例3例。累计确诊病例2216例，累计治愈出院病例2058例，无死亡病例。

截至8月11日24时，据31个省（自治区、直辖市）和新疆生产建设兵团报告，现有确诊病例761例（其中重症病例40例），累计治愈出院病例79342例，累计死亡病例4634例，累计报告确诊病例84737例，现有疑似病例3例。累计追踪到密切接触者802908人，尚在医学观察的密切接触者23039人。

31个省（自治区、直辖市）和新疆生产建设兵团报告新增无症状感染者20例（境外输入12例）；当日转为确诊病例2例（均为境外输入）；当日解除医学观察15例（境外输入6例）；尚在医学观察无症状感染者288例（境外输入141例）。

累计收到港澳台地区通报确诊病例4707例。其中，香港特别行政区4181例（出院3052例，死亡58例），澳门特别行政区46例（出院46例），台湾地区480例（出院443例，死亡7例）。

来源：北晚新视觉网综合 @荆州发布、北京日报客户端

流程编辑：TF015
```

#### 2.2.3vi常用指令

##### 插入文本类命令 ：

i ：在光标前
I ：在当前行首
a：光标后
A：在当前行尾
o：在当前行之下新开一行
O：在当前行之上新开一行

##### 移动光标类命令:

H ：光标移至当前屏幕顶行
M ：光标移至当前屏幕中间行
L ：光标移至当前屏幕最后行
G :光标移至文本的末尾
gg : 光标移至文本的首行

##### 屏幕翻滚类命令:

Ctrl+u：向文件首翻半屏

Ctrl＋b；向文件首翻一屏

Ctrl+d：向文件尾翻半屏

Ctrl+f：向文件尾翻一屏

##### 搜索:

/pattern：从光标开始处向文件尾搜索pattern
?pattern：从光标开始处向文件首搜索pattern

n：在同一方向重复上一次搜索命令
N：在反方向上重复上一次搜索命令

：s/p1/p2/g       将当前行中所有p1均用p2替代
：n1,n2s/p1/p2/g      将第n1至n2行中所有p1均用p2替代
：g/p1/s//p2/g        将文件中所有p1均用p2替换

### 2.3 常用基本操作整理

#### systemctl

systemctl命令是一个系统管理守护进程、工具和库的集合。初始进程主要负责控制systemd系统和服务管理器。通过Systemctl –help可以看到该命令主要分为：查询或发送控制命令给systemd服务，管理单元服务的命令，服务文件的相关命令，任务、环境、快照相关命令，systemd服务的配置重载，系统开机关机相关的命令

| **命令实例**                    | **作用**             |
| ------------------------------- | -------------------- |
| sys temctl start [server name]  | 启动服务             |
| systemctl restart [server name] | 重新启动服务         |
| systemctl stop [server name]    | 停止服务             |
| systemctl enable [server name]  | 设置服务开机自启动   |
| systemctl disable [server name] | 设备服务禁止开机启动 |
| systemctl status [server name]  | 查看服务的状态       |

**CentOS7中systemctl的使用** https://blog.csdn.net/u012486840/article/details/53161574

```python
# 防火墙相关 
systemctl status firewalld
systemctl stop firewalld
systemctl start firewalld
systemctl restart  firewalld
systemctl disable firewalld
```

#### 网络配置

我们可以通过   cat /etc/sysconfig/network-scripts/ifcfg-ens33  去查看我们电脑的网络配置信息

配置有两种方式

- 动态配置  如下图 最简答的一种方式，只要有网开机自动配置

![image-20210404102539468](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210404102539.png)

- 静态手工配置 
  - 要去修改网络配置文件 vi /etc/sysconfig/network-scripts/ifcfg-ens33
  - ![image-20210404103327620](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210404103327.png)
  - 重启网络服务 让网络配置生效   systemctl restart network

| **命令实例** | **作用**                   |
| ------------ | -------------------------- |
| ip a         | 查看网络连接信息           |
| ip addr      | 查看网络连接信息           |
| ifconfig     | 查看网络连接信息           |
| ipconfig     | 查看网络连接信息 (Windows) |

**ip**命令和**ifconfig**   https://www.cnblogs.com/gispathfinder/p/6158257.html

#### 常用基本命令

| 序号 | 命令               | 对应英文             | 作用                     |
| ---- | ------------------ | -------------------- | ------------------------ |
| 01   | ls                 | list                 | 查看当前文件夹下的内容   |
| 02   | pwd                | print wrok directory | 查看当前所在文件夹       |
| 03   | touch [文件名]     | touch                | 如果文件不存在，新建文件 |
| 04   | mkdir [目录名]     | make directory       | 创建目录                 |
| 05   | rm [文件名]        | remove               | 删除指定的文件           |
| 06   | cd [目录名]        | change directory     | 切换文件夹               |
| 07   | cp [文件名]        | copy                 | 拷贝指定的文件           |
| 08   | mv [文件名]        | move                 | 移动指定的文件           |
| 09   | tree [目录名]      | tree                 | 以树状方式显示目录结构   |
| 10   | clear              | clear                | 清屏                     |
| 11   | cat                | cat                  | 查看或者合并文件内容     |
| 12   | ln 源文件 链接文件 | ln                   | 建立软连接               |
| 13   | grep               | grep                 | 文本搜索                 |
| 14   | find               | find                 | 文件查找                 |
| 15   | tar                | tar                  | 文件压缩                 |
| 16   | zip/unzip          | zip/unzip            | 文件压缩                 |
| 17   | chmod              | chmod                | 修改文件权限             |
| 18   | top                | top                  | 查看系统的资源使用率     |
| 19   | ps                 | ps                   | 查看当前终端运行的进程   |
| 20   | kill pid           | kill                 | 根据进程号杀死进程       |

**小技巧:**

1. `ctrl shift +` 	放大终端窗口的字体显示
2. `ctrl -`      缩小终端窗口的字体显示

#### ls 选项

ls 是英文单词 list 的简写, 其功能为列出目录的内容, 是用户最常用的命令之一.

ls 常用选项:

| 选项 | 含义                                                        |
| :--: | ----------------------------------------------------------- |
|  -a  | 显示指定目录下的隐藏文件， 以点(**.**)开头的文件为隐藏文件. |
|  -l  | 以列表方式显示文件的详细信息.                               |
|  -h  | 配合 -l 显示文件大小单位.                                   |

```shell
#显示当前目录下的文件列表
ls -alh
```

#### tree 选项

用于在目录中显示文件和子目录  

输入tree 如果提示  未找到命令...则需要安装:sudo yum -y install tree

```shell
# 以树状结构显示目录下的结构
tree 目录名
```

#### pwd选项

```shell
#显示当前文件所在的文件夹 路径
pwd
```

#### 绝对路径和相对路径

1. 相对路径: 从当前目录算起的路径, 例如当前目录的上一级目录, 当前目录的下一级目录, 都是相对当前目录开始.
2. 绝对路径: 以  **/ ** 开始的路径.

#### mkdir 选项

通过 mkdir 命令可以创建一个新的目录. 选项 -p 可递归创建目录. 常用选项及含义如下表所示:

| 选项 | 含义               |
| :--- | ------------------ |
| -p   | 创建所依赖的文件夹 |

需要注意的是新建目录的名称不能与当前目录中已有的目录或文件同名, 并且目录创建者必须对当前目录具有写权限.

```shell
# 在当前目录下创建文件夹test
mkdir test
# 在当前目录下创建递归目录  test2下有test，test下有test
mkdir -p test2/test/test
```

#### cd 

cd 后面可跟绝对路径, 也可以跟相对路径. 如果省略目录, 默认切换到当前用户的主目录.

| 命令  | 含义                                                         |
| :---: | ------------------------------------------------------------ |
|  cd   | 切换到当前用户的主目录(/home/用户目录)，用户登陆的时候，默认的目录就是用户的主目录。 |
| cd ~  | 切换到当前用户的主目录(/home/用户目录)                       |
| cd .  | 切换到当前目录                                               |
| cd .. | 切换到上级目录                                               |

```shell
#回到用户的主目录
cd 
#回到当前目录下的 test2的文件夹下的test
cd test2/test/  #相对路径
cd /home/zhangjing/test2/test/  #绝对路径
```

#### touch选项

在指定目录下创建指定文件

```shell
#在 test2/test/下创建5.txt
touch /home/zhangjing/test2/test/5.txt  #绝对路径
#直接在当前目录下创建文件7.txt
touch 7.txt
```

#### cp 选项

cp 命令的功能是将指定的**文件**或**目录**复制到另一个文件或目录中.

常用选项说明:

| 选项 | 含义                                                         |
| :--- | ------------------------------------------------------------ |
| -i   | 交互式复制，在覆盖目标文件之前将给出提示要求用户确认         |
| -r   | 若给出的源文件是目录文件，则cp将递归复制该目录下的所有子目录和文件，目标文件必须为一个目录名。 |
| -v   | 显示拷贝后的路径描述                                         |

```shell
# 将当前目录下的5.txt 复制到当前目录下的test文件夹下面
cp 5.txt test/
# 将当前目录下的5.txt 复制到当前目录下的10.txt 没有10.txt则新建有就覆盖
cp 5.txt 10.txt
# 将当前文件夹下的test文件复制到 testcp 没有新建 复制目录需要加r
cp -r  test/ testcp/
```

#### mv 选项

用户可以使用 mv 命令来**移动文件或目录**, 也可以给**文件或目录重命名**.

常用选项说明：

| 选项 | 含义                                                         |
| :--- | ------------------------------------------------------------ |
| -i   | 确认交互方式操作，如果mv操作将导致对已存在的目标文件的覆盖，系统会询问是否重写，要求用户回答以避免误覆盖文件 |
| -v   | 显示移动后的路径描述                                         |

```shell
# 将当前目录下的 5.txt  重命名为6.txt  如果6.txt存在就覆盖
mv 5.txt 6.txt
# 将当前文件下的 6.txt 移动到 当前目录下的test目录下
mv 6.txt  test/
# 将当前目录下的testcp文件夹移动到 当前目录下的test目录下
mv testcp/ test
```

#### rm 选项

可通过 rm 删除**文件**或**目录**. 使用 rm 命令要小心, 因为文件删除后不能恢复. 为了防止文件误删, 可以在 rm 后使用 -i 参数以逐个确认要删除的文件.

常用选项说明:

| 选项 | 含义                                             |
| :--- | ------------------------------------------------ |
| -i   | 以进行交互式方式执行                             |
| -f   | 强制删除，忽略不存在的文件，无需提示             |
| -r   | 递归地删除目录下的内容，删除文件夹时必须加此参数 |
| -d   | 删除空目录                                       |

#### tail 选项

tail 命令可用于查看文件的内容，有一个常用的参数 **-f** 常用于查阅正在改变的日志文件。 通常用来读取日志

常用选项说明:

| 选项     | 含义                                                    |
| :------- | ------------------------------------------------------- |
| -n<行数> | 显示文件的尾部 n 行内容                                 |
| -f       | 循环读取                                                |
| -s       | --sleep-interval=S 与-f合用,表示在每次反复的间隔休眠S秒 |

**tail -f filename** 会把 filename 文件里的最尾部的内容显示在屏幕上，并且不断刷新，只要 filename 更新就可以看到最新的文件内容。

#### cat选项  

**cat查看文件内容**

```shell
cat 文件名
-n, --number             对输出的所有行编号
-b, --number-nonblank    对非空输出行编号
```

**cat 经常配合 >(重定向使用)**

Linux允许将命令执行结果重定向到一个文件，**本应显示在终端上的内容保存到指定文件中。**

``` shell	
# 将 ls -alh 查询出来的内容 保存到文件中
ls -alh > ls.txt
# 将文件6.txt test.txt的内容都保存到新文件 111.txt中
cat 6.txt test.txt > 1111.txt
```

 **>输出重定向会覆盖原来的内容，>>输出重定向则会追加到文件的尾部。**

**cat  文件1 文件2 > 新文件**

#### ln选项

Linux链接文件类似于Windows下的快捷方式。

链接文件分为软链接和硬链接。

软链接：软链接不占用磁盘空间，源文件删除则软链接失效。

硬链接：**硬链接只能链接普通文件，不能链接目录。**

使用格式：

```shell
ln 源文件 链接文件   # 硬连接
ln -s 源文件 链接文件  # 软连接
```

如果`没有-s`选项代表建立一个硬链接文件，两个文件占用相同大小的硬盘空间，即使删除了源文件，链接文件还是存在，所以-s选项是更常见的形式。

#### grep选项

grep命令是一种强大的文本搜索工具，grep允许对文本文件进行模式查找。如果找到匹配模式， grep打印包含模式的所有行。 通常**配合 | (管道) 使用**

**管道：一个命令的输出可以通过管道做为另一个命令的输入。**

grep一般格式为：

```
grep [-选项] ‘搜索内容串’文件名
```

在grep命令中输入字符串参数时，最好引号或双引号括起来。

例如：

```shell
# 查看 1.txt文件中是否有a，有的话返回结果没有返回空
grep 'a' 1.txt
```

常用选项说明：

| 选项 | 含义                                     |
| :--- | ---------------------------------------- |
| -n   | 显示匹配行及行号                         |
| -v   | 显示不包含匹配文本的所有行（相当于求反） |
| -i   | 忽略大小写                               |

```shell
#查看 test.txt文本中是否有 txt忽略大小写 并返回行号
grep -ni "txt" test.txt
#查看 test.txt文本中不否有 txt忽略大小写 并返回行号
grep -niv "txt" test.txt
```

grep搜索内容串可以是正则表达式。

正则表达式是对字符串操作的一种逻辑公式，就是用事先定义好的一些特定字符、及这些特定字符的组合，组成一个“规则字符串”，这个“规则字符串”用来表达对字符串的一种过滤逻辑。

grep常用正则表达式：

| 参数 | 含义                                         |
| :--- | -------------------------------------------- |
| ^a   | 行首,搜寻以 m 开头的行；grep -n '^a' 1.txt   |
| ke$  | 行尾,搜寻以 ke 结束的行；grep -n 'ke$' 1.txt |

常看终端运行的python命令

ps aux|grep python

#### find选项

find命令功能非常强大，通常用来在特定的目录下搜索符合条件的文件，也可以用来搜索特定用户属主的文件。

常用用法：

```shell
# 按照名字查找 find -name 文件名称
find ./ -name test.sh
# 查看找出来的文件的详细信息 find -ls
# 按照名字查找忽略大小写find -iname 文件名
find ./ -name test.sh
# 按照文件的类型查询 find -type 类型参数
# f 普通文件 l 符号连接 d 目录 
find -type d
# 按照文件的所属用户查询 find -user 用户名
# 按照文件所属的文件组查询 find -group 组名称
# 限制最大深度 find -maxdepth 深度值
# 限制最小深度 find -mindepth 深度值 
# 根据文件时间戳进行搜索
访问时间（-atime/天，-amin/分钟）：用户最近一次访问时间。
修改时间（-mtime/天，-mmin/分钟）：文件最后一次修改时间。
变化时间（-ctime/天，-cmin/分钟）：文件数据元（例如权限等）最后一次修改时间

# 查找当前目录下所有后缀为.sh的文件
find ./ -name '*.sh'
# 查找当前目录下所有以大写字母开头的文件
find ./ -name "[A-Z]*"
```

#### tar 选项  需要指定扩展名  .tar.gz

计算机中的数据经常需要备份，tar是Unix/Linux中最常用的备份工具，此命令可以把一系列文件归档到一个大文件中，也可以把档案文件解开以恢复数据。

tar使用格式 tar [选项] 打包文件名 文件

常用参数：

| 选项 | 含义                                                      |
| :--- | --------------------------------------------------------- |
| -c   | 生成档案文件，创建打包文件                                |
| -v   | 列出归档解档的详细过程，显示进度                          |
| -f   | 指定档案文件名称，f后面一定是.tar文件，所以必须放选项最后 |
| -x   | 解开档案文件                                              |
| -z   | 压缩                                                      |

**注意：除了f需要放在参数的最后，其它参数的顺序任意。**

```shell
#压缩用法：
tar -zcvf 压缩包包名 文件1 文件2 ...
# 将文件夹test 压缩成 a.tar.gz
tar -zcvf a.tar.gz test
#将文件 5.txt和 6.txt 压缩成 b  可以成功 但不推荐 要加后缀名 b改成b.tar.gz
tar -zcvf b 5.txt 6.txt 

#解压用法： 
tar -zxvf 压缩包包名
# 解压到指定目录：-C （大写字母“C”）
tar -zxvf 压缩包包名 -C 目录地址
```

####  zip 压缩格式

**通过zip压缩文件的目标文件不需要指定扩展名，默认扩展名为zip。**

```shell
# 压缩文件：
zip 目标文件(没有扩展名) 源文件
# 将当前目录下的test压缩成b.zip   不需要写拓展名  会默认添加
zip b test
#解压文件
unzip -d 解压后目录文件地址(文件要解压到哪里) 压缩文件
# 将当前文件下的 b.zip 解压到 /home/zhangjing
unzip -d /home/zhangjing b.zip
```

#### chmod

修改文件权限：

chmod 修改文件权限有两种使用格式：字母法与数字法。

![image-20210408143619563](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210408143619.png)

**字母法：chmod u/g/o/a +/-/= rwx 文件**

| [ u/g/o/a ] | 含义                                                      |
| :---------- | --------------------------------------------------------- |
| u           | user 表示该文件的所有者                                   |
| g           | group 表示与该文件的所有者属于同一组( group )者，即用户组 |
| o           | other 表示其他以外的人                                    |
| a           | all 表示这三者皆是                                        |

| [ +-= ] | 含义     |
| ------- | -------- |
| +       | 增加权限 |
| -       | 撤销权限 |
| =       | 设定权限 |

| rwx  | 含义                                                         |
| ---- | ------------------------------------------------------------ |
| r    | read 表示可读取，对于一个目录，如果没有r权限，那么就意味着不能通过ls查看这个目录的内容。 |
| w    | write 表示可写入，对于一个目录，如果没有w权限，那么就意味着不能在目录下创建新的文件。 |
| x    | excute 表示可执行，对于一个目录，如果没有x权限，那么就意味着不能通过cd进入这个目录。 |

**数字法：“rwx” 这些权限也可以用数字来代替**

| 字母 | 说明                         |
| ---- | ---------------------------- |
| r    | 读取权限，数字代号为 "4"     |
| w    | 写入权限，数字代号为 "2"     |
| x    | 执行权限，数字代号为 "1"     |
| -    | 不具任何权限，数字代号为 "0" |

**注意：如果想递归所有目录加上相同权限，需要加上参数“ -R ”。 如：chmod 777 test/ -R 递归 test 目录下所有文件加 777 权限**

```shell
#给文件test.txt 文件的所有者添加rwx  用户组添加rx  其他人添加 r
chmod u=rwx,g=rx,o=r test.txt # 字母法
chmod 754 test.txt # 数字法
```

#### netstat 

监听端口

a——显示所有选项。

t ——显示tcp相关选项。

u——显示udp相关选项。

n——拒绝显示别名，能显示数字的全部转化成数字。

l——仅列出有在 Listen (监听) 的服务状态。

p——显示建立相关链接的程序名。

r——显示路由信息，路由表。

e——显示扩展信息，例如uid等。

s——按各个协议进行统计 (重要)。

c——每隔一个固定时间，执行该netstat命令。

**netstat -lanp | grep 程序名/端口号**

#### which 和whereis

查看命令位置：

which命令的作用是，在PATH变量指定的路径中，搜索某个系统命令的位置，并且**返回第一个搜索结果**。也就是说，使用which命令，就可以看到某个系统命令是否存在，以及执行的到底是哪一个位置的命令。

whereis命令只能用于程序名的搜索，而且**只搜索二进制文件**（参数-b）、man说明文件（参数-m）和源代码文件（参数-s）。如果省略参数，则返回所有信息。

```shell
whereis cd 
which cd 
```

####  type

查看命令类别 哪些是内部命令，哪些是外部命令 

```shell
type cd 
```

#### sudo -s

切换到管理员账号

#### whoami

查看当前用户

#### exit

退出登录账户

#### who

查看所有的登录用户

#### date  

查看日期时间:

```shell
# 直接查看系统当前时间 
date
# 修改时间  需要管理员权限
# 修改时间为 2020-02-02 12:12:12
date -s "2020-02-02 12:12:12"
# 与世界时间校准
yum -y install ntpdate  #需要先安装这个包
ntpdate asia.pool.ntp.org
```

#### passwd  

设置用户密码

在Unix/Linux中，超级用户可以使用passwd命令为普通用户设置或修改用户密码。用户也可以直接使用该命令来修改自己的密码，而无需在命令后面使用用户名。

#### top

查看系统的资源使用率

直接top即可

具体参数见文档

https://blog.csdn.net/xujiamin0022016/article/details/89072116

```shell
# 设置刷新间隔为1秒
top -d 1
# 输出三次后便退出
top -n 3
```

#### free

显示系统内存的使用

```shell
# 显示当前系统内存的使用情况 加上 -h 选项，输出的结果会友好很多
# 有时我们需要持续的观察内存的状况，此时可以使用 -s 选项并指定间隔的秒数：
free -h -s 3
```

#### ps

查看当前终端运行的程序

```shell
# 查看系统当前运行程序
ps aux
# 通常配合管道使用 验证某个程序是否运行
# 查看是否有python程序运行
ps aux|grep python
```

详细参数见文档

https://blog.csdn.net/ShiXueTanLang/article/details/80781089

#### kill 

杀死进程

kill pid  杀死进程

kill -9 pid 强制终止

#### reboot、shutdown

关机重启

| 命令            | 含义                                       |
| :-------------- | ------------------------------------------ |
| reboot          | 重新启动操作系统                           |
| shutdown –r now | 重新启动操作系统，shutdown会给别的用户提示 |
| shutdown now    | 立刻关机，其中now相当于时间为0的状态       |
| shutdown 20:25  | 系统在今天的20:25 会关机                   |
| shutdown +10    | 系统再过十分钟后自动关机                   |

#### Linux用户和用户组

Linux 是**多用户多任务操作系统**，换句话说，Linux 系统支持多个用户在同一时间内登陆，不同用户可以执行不同的任务，并且互不影响。

例如，某台 Linux 服务器上有 4 个用户，分别是 root、www、ftp 和 mysql，在同一时间内，root 用户可能在查看系统日志、管理维护系统；www 用户可能在修改自己的网页程序；ftp 用户可能在上传软件到服务器；mysql 用户可能在执行自己的 SQL 查询，每个用户互不干扰，有条不紊地进行着自己的工作。与此同时，每个用户之间不能越权访问，比如 www 用户不能执行 mysql 用户的 SQL 查询操作，ftp 用户也不能修改 www 用户的网页程序。

因此，如果要使用 Linux 系统的资源，就必须向系统管理员申请一个账户，然后通过这个账户进入系统（账户和用户是一个概念）。**通过建立不同属性的用户，一方面可以合理地利用和控制系统资源，另一方面也可以帮助用户组织文件，提供对用户文件的安全性保护。**

每个用户都有唯一的用户名和密码。在登录系统时，只有正确输入用户名和密码，才能进入系统和自己的主目录。

用户组是具有相同特征用户的逻辑集合。简单的理解，有时我们需要让多个用户具有相同的权限，比如查看、修改某一个文件的权限，一种方法是分别对多个用户进行文件访问授权，如果有 10 个用户的话，就需要授权 10 次，那如果有 100、1000 甚至更多的用户呢？

显然，这种方法不太合理。最好的方式是建立一个组，让这个组具有查看、修改此文件的权限，然后将所有需要访问此文件的用户放入这个组中。那么，所有用户就具有了和组一样的权限，这就是用户组。

将用户分组是 Linux 系统中对用户进行管理及控制访问权限的一种手段，通过定义用户组，很多程序上简化了对用户的管理工作。

用户和用户组的对应关系有以下 4 种：

1. 一对一：一个用户可以存在一个组中，是组中的唯一成员；
2. 一对多：一个用户可以存在多个用户组中，此用户具有这多个组的共同权限；
3. 多对一：多个用户可以存在一个组中，这些用户具有和组相同的权限；
4. 多对多：多个用户可以存在多个组中，也就是以上 3 种关系的扩展。

本质就是：  **用户就是你一个登陆的账号  可以有多个账号 而用户组其实是方便管理用户权限的 你在这个组里你这个用户就有这个组的权限**

##### 用户组操作

```shell
# 添加一个新的用户组 testing
groupadd testing
# 查看所有群组的信息
cat /etc/group
# 查看root用户的所在组信息 
groups root
# 将 组 testing 重命名 组testings
groupmod -n testings testing
# 删除组 testings
groupdel testings
```

##### 用户操作

```shell
# 新增一个用户张三  默认同时将新增一个对应的名为张三的组
useradd zhangsan

#新增一个用户李四 并将其加入zhangsan组
useradd -g zhangsan lisi

#将用户张三换到 zhangjing组
usermod -g zhangjing zhangsan
# 将用户张三附加到test组
usermod -G test zhangsan

#删除用户zhangsan
userdel zhangsan
# 强制删除用户 zhangsan（即使该用户已经登录）
userdel -f zhangsan
# 删除用户张三 并删除其主目录
userdel -r qiang

# 给用户设置密码  需要root权限
#切换用户 切换到root用户
su root
#给test用户设置密码
passwd test
su test
```

## 4.Linux下安装Mysql和WoniuSales

### 4.1 centos7安装mysql

1. 在用户的个人根目录下 **新建software**  后期存放软件包

2. 下载并安装 MySQL官方的 Yum Repository

   - 查询电脑是否已安装mysql   rpm -qa |grep mysql
   - 强力卸载 mysql:  rpm -e --nodeps mysql
   - 下载并安装MySQL官方的 Yum Repository (wget找不到  yum -y install wget)
   - wget -i -c http://dev.mysql.com/get/mysql57-community-release-el7-10.noarch.rpm

3. 使用上面的命令就直接下载了安装用的Yum Repository，大概25KB的样子，然后就可以直接yum安装

4. yum -y install mysql57-community-release-el7-10.noarch.rpm

5. 开始安装MySQL服务器 yum -y install mysql-community-server

6. 启动MySQL  systemctl start  mysqld

7. 查看MySQL运行状态 systemctl status mysqld

8. ![image-20210409105422311](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210409105422.png)表示成功运行

9. 修改密码策略  目的：为了重设密码时 能够使用弱密码

10. vi /etc/my.cnf

11. 添加validate_password_policy配置

    validate_password_policy=0

    #关闭密码策略

    validate_password = off

12. ![image-20210409105524601](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210409105524.png)

13. 修改mysql的字符编码（不修改会产生中文乱码问题）

14. 在如图位置 添加下面两行
    character_set_server=utf8

    init_connect='SET NAMES utf8'

15. ![image-20210409110247528](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210409110247.png)

16. 保存退出

17. 重启mysql服务使配置生效  systemctl restart mysqld

18. 此时MySQL已经开始正常运行，不过要想进入MySQL还得先找出此时root用户的密码，通过如下命令可以在日志文件中找出密码：grep "password" /var/log/mysqld.log

19. ![image-20210409105603194](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210409105603.png)如红色方框就是我的密码

20. 如下命令进入数据库： mysql -uroot -p   回车 确定然后输入密码

21. MySQL默认必须修改密码之后才能操作数据库

22. ALTER USER 'root'@'localhost' IDENTIFIED BY '123456'

23. 其中123456就是你的新密码 你想用其他密码就把123456换成其他

24. 使用 exit  退出mysql

25. 重启mysql  systemctl restart mysqld

26. 重新进入mysql  mysql -uroot -p   回车 输入密码

27. 输入 show variables like "%character%"

28. ![image-20210409110339912](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210409110339.png)

29. 开启mysql的允许远程访问

30. grant all privileges on *.* to 'root'@'%' identified by '123456' with grant option;  

31. 上面表示允许所有ip地址（%表示允许所有ip  如果指定ip就把%换成指定ip）可以用root 账号 密码 是123456 进行远程登

32. 然后再输入下面两行命令  flush privileges; 

33. exit;

34. 最后通过本机的Navicat 测试能否连接远程数据库

35. ![image-20210409110643267](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210409110643.png)

### 4.2 centos7安装jdk  tomcat 部署woniusales

1. 使用工具的上传按钮 在software目录下 上传下面两个包

2. ![image-20210409110905511](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210409110905.png)

3. ![image-20210409110920895](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210409110920.png)

4. 接下来的操作都需要root权限 用root账号操作

5. 先把几个所需要的的包上传到 software文件夹下 在**该文件夹下执行**   tar -xzvf jdk-8u251-linux-x64.tar.gz -C /opt

6. 配置环境变量   vim ~/.bash_profile

7. 用户环境变量，添加如下三行:

   JAVA_HOME=/opt/jdk1.8.0_251

   PATH=$PATH:$HOME/bin:$JAVA_HOME/bin

   CLASSPATH=$JAVA_HOME/lib

   ![image-20210409111643641](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210409111643.png)

8. 保存退出后,执行命令是配置生效:  source ~/.bash_profile

9. 分别输入java和 javac并回车,如果能够正常看到命令帮助说明jdk配置成功

   输入java有效 javac无效 使用   yum -y install java-devel

   安装 java-devel 后 然后javac验证  

10. 安装tomcat 

11. 解压到指定目录  tar -xzvf apache-tomcat-9.0.35.tar.gz  -C  /opt/

12. 配置环境变量   vim ~/.bash_profile

13. 用户环境变量后面添加下面两行

    CATALINA_HOME=/opt/apache-tomcat-9.0.35

    CATALINA_BASE=$CATALINA_HOME

14. 保存退出后,执行命令是配置生效:  source ~/.bash_profile

15. cd /opt/apache-tomcat-9.0.35/bin  

    ./startup.sh  启动 tomcat服务

16. [http://ip地址:8080/](http://192.168.107.136:8080/) 能否正常显示如下页面说明 tomcat配置成功

17. ![image-20210409112009598](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210409112009.png)

18. 登陆数据库创建woniusales的空库，然后在库名上鼠标右键选择执行woniusales.sql文件

    ![image-20210409112046240](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210409112046.png)

    ![image-20210409112056625](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210409112056.png)

19. 上传woniusales的war包到指定目录下 在  /opt/apache-tomcat-9.0.35/webapps/

20. ![image-20210409112126326](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210409112126.png)

21. 启动tomcat  /opt/apache-tomcat-9.0.35/bin/startup.sh

22. 关闭tomcat  /opt/apache-tomcat-9.0.35/bin/shutdown.sh

23. 修改woniusales的数据库连接信息   vim /opt/apache-tomcat-9.0.35/webapps/WoniuSales-20180508-V1.4-bin/WEB-INF/classes/db.properties 

24. 重新开启tomcat服务  /opt/apache-tomcat-9.0.35/bin/startup.sh

25. 使用浏览器访问   http://IP地址:8080/WoniuSales-20180508-V1.4-bin/

    账号 admin

    密码  123456

### 4.3 安装文档

安装mysql  见文档  **linux下安装mysql.docx**

安装WoniuSales  见文档  **linux下安装WoniuSales.docx**

## 5.Docker

### 5.1Docker简介

Docker 是一个[开源](https://baike.baidu.com/item/开源/246339)的应用容器引擎，让开发者可以打包他们的应用以及依赖包到一个可移植的镜像中，然后发布到任何流行的 [Linux](https://baike.baidu.com/item/Linux)或[Windows](https://baike.baidu.com/item/Windows/165458) 机器上，也可以实现[虚拟化](https://baike.baidu.com/item/虚拟化/547949)。容器是完全使用[沙箱](https://baike.baidu.com/item/沙箱/393318)机制，相互之间不会有任何接口。

#### 1.**Docker解决的问题：**

由于不同的机器有不同的操作系统，以及不同的库和组件，在将一个应用部署到多台机器上需要进行大量的环境配置操作。

Docker 主要解决环境配置问题，它是一种虚拟化技术，对进程进行隔离，被隔离的进程独立于宿主操作系统和其它隔离的进程。使用 Docker 可以不修改应用程序代码，不需要开发人员学习特定环境下的技术，就能够将现有的应用程序部署在其它机器上。

#### 2.**Docker与传统虚拟机比较：**

![image-20210727150646708](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210727150646.png)

**Docker容器**并非**虚拟机**

#### 3.Docker优缺点

##### 优点

1. **部署方便**

搭建环境这一步往往会耗费我们好几个小时的时间，而且其中一个小问题可能需要找很久才能够解决。而有了容器之后，这些都变得非常容易，你的开发环境就只是一个或者几个容器镜像的地址，最多再需要一个控制部署流程的执行脚本。或者进一步将你的环境镜像以及镜像脚本放入一个git项目，发布到云端，需要的时候将它拉到本地就可以了。

2. 部署安全

我们可以通过容器技术将开发环境和测试环境以及生产环境保持版本和依赖上的统一，保证代码在一个高度统一的环境上执行。而测试环境的统一，也同样能解决CI流程对环境的要求。

分布式技术和扩容需求日益增长的今天，如果运维能够使用容器技术来进行环境的部署，不仅仅在部署时间上节省不少，也能把很多因为人工配置环境产生的失误降到最低。 

3. **隔离性好**

不管是开发还是生产，往往我们一台机器上可能需要跑多个服务，而服务各自需要的依赖配置不尽相同，假如说两个应用需要使用同一个依赖，或者两个应用需要的依赖之间会有一些冲突，这个时候就很容易出现问题了。 所以同一台服务器上不同应用提供的不同服务，最好还是将其隔离起来。而容器在这方面有天生的优势，每一个容器就是一个隔离的环境，你对容器内部提供服务的要求，容器可以自依赖的全部提供。这种高内聚的表现可以实现快速的分离有问题的服务，在一些复杂系统中能实现快速排错和及时处理。(当然需要说明的是，这个隔离性只是相对于服务器比较的，虚机技术要拥有更好的隔离性)

4. 快速回滚

容器之前的回滚机制，一般需要基于上个版本的应用重新部署，且替换掉目前的问题版本。在最初的时代，可能是一套完整的开发到部署的流程，而执行这一套流程往往需要很长的时间。在基于git的环境中，可能是回退某个历史提交，然后重新部署。这些跟容器技术相比都不够快，而且可能会引起新的问题（因为是基于新版本的修改）。而容器技术天生带有回滚属性，因为每个历史容器或者镜像都会有保存，而替换一个容器或者某个历史镜像是非常快速和简单的。

5. **成本低**

这可能是一个最明显和有用的优点了，在容器出现之前，我们往往构筑一个应用就需要一台新的服务器或者一台虚机。服务器的购置成本和运维成本都很高，而虚机需要占用很多不必要的资源。相比之下，容器技术就小巧轻便的多，只需要给一个容器内部构建应用需要的依赖就可以了，这也是容器技术发展迅速的最主要原因。

6. **管理成本更低**

随着容器技术的不断普及和发展，随之而来的容器管理和编排技术也同样得到发展。诸如Docker Swarm，Kubernetes, Mesos等编排工具也在不断的迭代更新，这让容器技术在生产环境中拥有了更多的可能性和更大的发挥空间。而随着大环境的发展，docker等容器的使用和学习的成本也是愈发降低，成为更多开发者和企业的选择。

##### 缺点

1. 隔离性

虚拟机技术，在隔离性上比容器技术要更好，它们的系统硬件资源完全是虚拟化的，当一台虚机出现系统级别的问题，往往不会蔓延到同一宿主机上的其他虚机。但是容器就不一样了，容器之间共享同一个操作系统内核以及其他组件，所以在收到攻击之类的情况发生时，更容易通过底层操作系统影响到其他容器。当然，这个问题可以通过在虚机中部署容器来解决，可是这样又会引出新的问题，比如成本的增加以及下面要提到的问题：性能。

2. 性能

不管是虚机还是容器，都是运用不同的技术，对应用本身进行了一定程度的封装和隔离，在降低应用和应用之间以及应用和环境之间的耦合性上做了很多努力，但是随机而来的，就会产生更多的网络连接转发以及数据交互，这在低并发系统上表现不会太明显，而且往往不会成为一个应用的瓶颈（可能会分散于不同的虚机或者服务器上），但是当同一虚机或者服务器下面的容器需要更高并发量支撑的时候，也就是并发问题成为应用瓶颈的时候，容器会将这个问题放大，所以，并不是所有的应用场景都是适用于容器技术的。 

3. 存储方案

容器的诞生并不是为OS抽象服务的，这是它和虚机最大的区别，这样的基因意味着容器天生是为应用环境做更多的努力，容器的伸缩也是基于容器的这一disposable特性，而与之相对的，需要持久化存储方案恰恰相反。这一点docker容器提供的解决方案是利用volume(卷)接口形成数据的映射和转移，以实现数据持久化的目的。但是这样同样也会造成一部分资源的浪费和更多交互的发生，不管是映射到宿主机上还是到网络磁盘，都是退而求其次的解决方案。

随着硬件技术和网络技术的迭代发展，容器技术的缺点会变得越来越不那么明显，而且随着容器技术的发展和普及，对应的解决方案也会越来越多。所以总体来看，docker等容器技术会朝着更加普及的趋势走近我们技术领域。 也希望每一位热爱技术的小伙伴们能更加了解这些新技术，让它们能够更好的为我们服务。

#### 4.Docker的三个重要概念

##### 镜像（Images）

Docker镜像是一个只读的模板。包含了容器运行时所需要的文件系统和一些参数。镜像是无状态的，也不会改变。**镜像是用来创建容器**的。你可以使用docker pull命令获取一个别人已创建好的镜像，或者使用docker build来构建一个自己的镜像。

##### 容器（Containers）

Docker容器就像是一个文件夹，容器中包含了应用运行所需的一切。每个容器都是一个隔离的和安全的应用平台。容器是镜像的一个实例，它是有状态的，而且随时会改变，容器一般是短暂的。

**仓库(repository)：**

仓库（Repository）是集中存放镜像文件的场所。有时候会把仓库和仓库注册服务器（Registry）混为一谈，并不严格区分。实际上，仓库注册服务器上往往存放着多个仓库，每个仓库中又包含了多个镜像，每个镜像有不同的标签（tag）。

仓库分为公开仓库（Public）和私有仓库（Private）两种形式。最大的公开仓库是 Docker Hub，存放了数量庞大的镜像供用户下载。国内的公开仓库包括 时速云 、网易云 等，可以提供大陆用户更稳定快速的访问。当然，用户也可以在本地网络内创建一个私有仓库。

当用户创建了自己的镜像之后就可以使用 push 命令将它上传到公有或者私有仓库，这样下次在另外一台机器上使用这个镜像时候，只需要从仓库上 pull 下来就可以了。

### 5.2Docker安装

```shell
# 1、卸载旧版本
yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine
                  
# 2、安装依赖安装包
yum install -y yum-utils       
yum install -y yum-utils device-mapper-persistent-data lvm2
# 3、设置镜像的仓库
yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo   # 默认国外的

yum-config-manager \
    --add-repo \
    http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo # 推荐使用阿里云
    
# 4、yum升级，更新索引
yum -y update
yum -y makecache fast    

# 5、安装docker docker-ce 社区版 ee企业版
#yum install docker-ce docker-ce-cli containerd.io
yum -y install docker-ce 
# 6、启动docker服务
systemctl start docker

# 7、查看版本信息
docker version
docker info

# 8.如果下载速度慢 可以配置国内阿里云的镜像源操作步骤如下
#新建daemon.json配置文件
touch /etc/docker/daemon.json
#配置镜像地址列表和dns，复制下面一段配置信息至daemon.json文件中
vi /etc/docker/daemon.json

{
"registry-mirrors": [
"http://f1361db2.m.daocloud.io",
"https://docker.mirrors.ustc.edu.cn",
"https://kfwkfulq.mirror.aliyuncs.com",
"https://kjghzxej.mirror.aliyuncs.com",
"https://2lqq34jg.mirror.aliyuncs.com",
"https://pee6w651.mirror.aliyuncs.com",
"https://registry.docker-cn.com",
"http://hub-mirror.c.163.com"
]
}

#重启docker服务使配置生效
 systemctl restart docker
#查看镜像源是否生效
 docker info
```

### 5.3Docker常用命令

#### 1.帮助命令

```shell
docker version			# 显示版本信息
docker info				# 显示系统信息，包括镜像和容器数量等等
docker 命令 --help	   # 帮助命令
```

命令帮助文档地址：  https://docs.docker.com/reference/

#### 2.镜像命令

**[docker images](https://docs.docker.com/engine/reference/commandline/images/)** 查看所有本地主机的镜像

![image-20210409145726925](https://woniumd.oss-cn-hangzhou.aliyuncs.com/test/zhangjing/20210409145727.png)

```shell
[root@localhost /]# docker images
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE

# 解释
REPOSITORY  镜像的仓库源
TAG         镜像的标签
IMAGE ID    镜像的ID
CREATED     镜像创建的时间
SIZE        镜像的大小

[root@localhost /] docker images --help
Usage:  docker images [OPTIONS] [REPOSITORY[:TAG]]
List images
Options:
  -a, --all             Show all images (default hides intermediate images)
      --digests         Show digests
  -f, --filter filter   Filter output based on conditions provided
      --format string   Pretty-print images using a Go template
      --no-trunc        Don't truncate output
  -q, --quiet           Only show numeric IDs

```

**[docker search](https://docs.docker.com/engine/reference/commandline/search/)** 搜索镜像 

**[docker pull](https://docs.docker.com/engine/reference/commandline/pull/)** 镜像名[:tag] 下载镜像

**[docker rmi](https://docs.docker.com/engine/reference/commandline/rmi/)** 删除镜像

```shell
docker rmi -f 容器id/容器名称			# 删除指定镜像
docker rmi -f 容器id 容器id 容器id  	# 删除多个镜像
docker rmi -f $(docker images -aq)	# 删除全部镜像
```

#### 3.容器命令

说明：**有了镜像才可以创建容器，镜像=类  容器=对象**

[**docker run**](https://docs.docker.com/engine/reference/commandline/run/)  创建容器并启动

```shell
# 常用参数说明
--name=Name				# 容器的名字，区分容器
-d						# 后台方式运行 类似nohup
-it						# 交互方式运行，进入容器查看内容（启动时直接进入）
-p						# 指定端口 -p 8080:80  容器的80端口映射到宿主机8080端口
-v                      # 绑定数据卷，容器数据卷

# -it 组合可以跟run exec配用，一般还要指定进入的环境 /bin/bash
# 进入容器后，如果要退出可用 exit 命令 不用-d创建的容器会停止运行
# Ctrl+p+q 容器不会停止，退出
# docker run -p 6800:6800 -name name -d -v 本机地址:镜像地址 镜像名
```

[**docker ps**](https://docs.docker.com/engine/reference/commandline/ps/) 列出容器

```shell
# 默认列出正在运行的容器
-a			# 列出所有容器
-n=?		# 显示最近的容器
-q			# 只显示ID
```

[**docker rm**](https://docs.docker.com/engine/reference/commandline/rm/) 删除容器

```shell
docker rm 容器名/容器id		# 删除指定容器，不能删除正在运行的容器，如果要强制删除 用-f参数
docker rm -f $(docker ps -aq)	# 强制删除所有容器 -f 强制
```

**docker start**  启动容器

**docker restart**  重启容器

**docker stop**  停止容器

**docker kill**  强制停止容器

**后台启动容器**

```shell
docker run -d 镜像名

# 问题 docker ps, 发现容器停止了

# 常见的坑： docker 容器使用后台运行，就必须要有一个前台进程，docker发现没有应用，就会自动停止
# nginx,容器启动后，发现自己没有提供服务，就会立刻停止
```

**进入当前正在运行的容器**

```shell
# 我们通常容器都是后台方式运行的，需要进入容器，修改一些配置
# 命令
#1
docker exec -it 容器 /bin/bash # 开启新终端 exit -d的不会被关闭
#2
docker attach 容器 # 进入容器运行终端 exit -d的也会被关闭
```

### 5.4Docker 部署tomcat

```shell
# 查找tomcat镜像
docker search tomcat

# 拉取官方镜像
docker pull tomcat

#如果拉取镜像速度很慢，试试这个方法
vim /etc/docker/daemon.json  编辑这个文件没做过操作应该是空文件
# 添加:
{
  "registry-mirrors": ["https://kjghzxej.mirror.aliyuncs.com"]
}
# 保存后重启
sudo systemctl daemon-reload//重新加载
sudo systemctl restart docker//重启
#再重新拉取
# 查看本地是否有镜像
docker images

# 运行容器
docker run --name tomcat -d -p 8888:8080 -v /opt/apache-tomcat-9.0.35/webapps/WoniuSales-20180508-V1.4-bin.war:/usr/local/tomcat/webapps.dist/WoniuSales-20180508-V1.4-bin.war tomcat
#进入容器内容
docker exec -it 容器号 /bin/bash
# 删除
rm -rf webapps
#重命名 
mv webapps.dist/ webapps
# 退出
exit
# 访问
http://localhost:8888/
#监听端口
netstat -lnp|grep 端口号
```

### 5.5Docker部署mysql

用 Docker 的人都知道，我们在查询远端镜像仓库中镜像的时候，在命令行只能看到镜像名，说明等信息，而看不到标签。因此，如果我想要查看镜像有哪些标签，就只能通过网页的方式查看，比如通过 https://hub.docker.com/ 查看，这样实在是太麻烦，所以我们可以自己写个脚本

#### 列出镜像 标签的脚本 list_img_tags.sh

```shell
# 创建文件 
touch list_img_tags.sh
#编辑文件  
vim list_img_tags.sh

# 填入以下内容
#!/bin/sh

repo_url=https://registry.hub.docker.com/v1/repositories
image_name=$1

curl -s ${repo_url}/${image_name}/tags | json_reformat | grep name | awk '{print $2}' | sed -e 's/"//g'

#测试 
sudo bash list_img_tags.sh mysql
```

#### 下载5.7的mysql镜像

```shell
#拉取
docker pull mysql:5.7
#运行
docker run --name mysql -d -p 3333:3306 -e MYSQL_ROOT_PASSWORD=123456 mysql:5.7
```
