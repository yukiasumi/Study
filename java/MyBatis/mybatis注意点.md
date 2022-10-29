-  Mapper interface和xml文件的定义对应不上（需要检查包名，namespace） 
-  函数名称等能否对应不上（需要比较细致的对比，经常就写错了一两个字母搞的很长时间找不到错误） 

解决的思路一般是：

-  检查xml文件所在的package名称是否和interface对应的package名称一一对应 
-  检查xml文件的namespace是否和xml文件的package名称一一对应 
-  检查函数名称能否对应上 
-  去掉xml文件中的中文注释 
-  随意在xml文件中加一个空格或者空行然后保存