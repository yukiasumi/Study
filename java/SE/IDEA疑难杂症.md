


### IDEA出现"GBK" will be used by compiler，非法字符

* 将I:\IDEA\T037_ranther\.idea\encodings.xml\encodings.xml文件的GBK改UTF-8

### 注释颜色FF2F80

![image-20220313173409454](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220313173409454.png)

![image-20220313173456164](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220313173456164.png)

### 导入模板

![image-20220404144702669](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220404144702669.png)

### 统一编码

![image-20220404144805234](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220404144805234.png)

### 隐藏.idea文件

![image-20220404151526084](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220404151526084.png)

### 单独打包一个类【可执行jar包】

![image-20220408124325371](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220408124325371.png)

![image-20220408124340982](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220408124340982.png)

输出路径

点击➕添加

![image-20220408124427555](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220408124427555.png)

![image-20220408124458518](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220408124458518.png)

添加主类文件

加完直接点OK，有报错直接关掉

![image-20220408124547063](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220408124547063.png)

![image-20220408124618122](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220408124618122.png)

点击即可完成

![image-20220408124750626](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220408124750626.png)

```
java -jar search.jar 运行
传参用main方法里的args变量传
java -jar search.jar 参数
```

### 修改jar包中某个类

打开idea，随便新建一个java项目（[maven](https://so.csdn.net/so/search?q=maven&spm=1001.2101.3001.7020)），如我自己命名为test

![image-20220410104144259](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220410104144259.png)

打开File -> Project Structure，在Libraries中加入需要修改的jar包[源码](https://so.csdn.net/so/search?q=源码&spm=1001.2101.3001.7020)，如下图，点击java后，会弹出窗口，找到jar包路径后点ok

![image-20220410104258422](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220410104258422.png)

![image-20220410104313340](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220410104313340.png)

加载成果后项目结构如下：

![image-20220410104408890](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220410104408890.png)

假如我们需要修改的就是上图中红框的.class文件，打开该文件，可以看到上方的package com.cn.securityhealer.utils;因此在test–>src–>main–>java里面创建一个同路径的package

![image-20220410104451036](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220410104451036.png)

![image-20220410104505927](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220410104505927.png)

该代码没有导入其他依赖的package或文件，因此只需要在test–>src–>main–>java–>com.cn.securityhealear.utils下新建一个一模一样名称的java文件，并将class的内容（class内容其实就是源码）复制过来。（如果该class文件依赖别的文件，需要也将别的文件复制到test项目下）
![image-20220410104533308](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220410104533308.png)

![image-20220410104551418](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220410104551418.png)

可以看到复制过来的代码有显示红色的地方，说明有问题。这里是因为代码里有import org.springframework.context.ApplicationContext;，需要在test项目下的maven xml里面导入该包，而我们的xml配置文件还没导，因此找到源码当中的xml直接替换test项目下的xml。此外代码有问题，还可能是该文件依赖其他文件，如果有依赖，把其他文件也复制过来，注意复制的时候路径保持一样（因为代码首行会package xxx）


![image-20220410104620097](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220410104620097.png)

此时就修改test下新创建的jave源码（根据自己的实际需要）

 修改完成后就可以进行编译了

![image-20220410104706631](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220410104706631.png)

编译完成后，就会生成一个target文件夹，新生成的.class编译文件就在target下。

 使用解压缩工具直接打开原始的jar包，找到需要修改的.class文件，将重新编译的.class文件拖到原来文件的目录，覆盖即可。【或者把jar包改zip，进去替换之后再改回来】

![image-20220410104807297](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220410104807297.png)

### 多行选中小技巧

```
alt+shift，然后用鼠标左键点击文本，可以让光标在多个位置出现

按住 Ctrl+Shift+Alt，用鼠标选则多行文本，可以每处选择都是整齐的矩形。（框选）

鼠标右键—>选中columu select module,然后按住鼠标左键拖动选中多行。

快捷键：Alt+Shift+Insert

与ctrl+alt+shift不同的是，Alt+Shift+Insert切换的矩形选择状态，可以选空白的地方。

```

### debug

![image-20220430203348207](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220430203348207.png)

按F8可重置debug，使线程运行

![image-20220430203443713](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220430203443713.png)

### pom.xml文件无法下载依赖

重复点击切换离线模式

![image-20220430203707839](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220430203707839.png)

#### 直接复制的模块没有导入到项目中

![image-20220501174300344](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220501174300344.png)

![image-20220501174325422](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220501174325422.png)

![image-20220501174343399](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220501174343399.png)

选择文件

![image-20220501174400379](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220501174400379.png)

### Java8反射参数名称

![image-20220519081925495](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220519081925495.png)

