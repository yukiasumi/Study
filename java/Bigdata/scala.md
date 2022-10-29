## 数据类型
![image-20220828133711939](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220828133711939.png)


==Any是一切类型的父类,Nothing是一切类型的子类==

scala中==等同于equals方法，若需要判断引用地址是否相同可以使用eq方法

## 伴生对象

==object就是java中的static部分，定义的是类中静态属性。==

==类和伴生对象之间没有界限——它们可以互相访问彼此的private字段和private方法。==

```
当同一个文件内同时存在object x和class x的声明时：

我们称class x称作object x的伴生类。
其object x称作class x的伴生对象。
其中伴生类和伴生对象需要同名。
伴生对象也叫单例对象
```



