## [centos系统磁盘扩容](https://www.cnblogs.com/renshengruxi/p/11095167.html)

1.查看磁盘空间大小，使用df -h 命令。

![img](https://img2018.cnblogs.com/blog/1331300/201906/1331300-20190627092253855-244486684.png)

\2. 增加磁盘空间，例如下图使用VM虚拟机增加的方式。物理机直接安装挂载上去。

![img](https://img2018.cnblogs.com/blog/1331300/201906/1331300-20190627092314009-1244211518.png)

\3. 使用fdisk /dev/sda, 创建新分区。

![img](https://img2018.cnblogs.com/blog/1331300/201906/1331300-20190627092430331-1759856701.png)

4.重启Linux操作系统，使用reboot命令。

![img](https://img2018.cnblogs.com/blog/1331300/201906/1331300-20190627092510826-149051908.png)

5.创建物理卷 使用pvcreate /dev/sda3命令。

![img](https://img2018.cnblogs.com/blog/1331300/201906/1331300-20190627092533311-973776797.png)

6.查看物理卷信息使用pvdisplay命令。

![img](https://img2018.cnblogs.com/blog/1331300/201906/1331300-20190627092557883-146911139.png)

7.将新增加的分区/dev/sda3加入到根目录分区centos中:使用vgextend centos /dev/sda3命令。

![img](https://img2018.cnblogs.com/blog/1331300/201906/1331300-20190627092634145-1046696515.png)

8.查看卷组信息，使用vgdisplay命令。

![img](https://img2018.cnblogs.com/blog/1331300/201906/1331300-20190627092703494-2132133960.png)

9.增加centos大小，增加100G。使用lvresize -L +100G /dev/mapper/centos-root命令。<font size=16 color=FF0000>（注意查看空间大小，100G？）</font>

![img](https://img2018.cnblogs.com/blog/1331300/201906/1331300-20190627092739835-1961898306.png)

10.重新识别centos大小，使用xfs_growfs /dev/mapper/centos-root命令。

![img](https://img2018.cnblogs.com/blog/1331300/201906/1331300-20190627092803367-2049210301.png)

11.查看扩容后的大小 ，使用df -h命令。

![img](https://img2018.cnblogs.com/blog/1331300/201906/1331300-20190627092823792-549440142.png)