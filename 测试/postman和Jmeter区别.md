* 区别一：用例组织方式

```
不同的目录结构与组织方式代表不同工具的测试思想，学习一个测试工具应该首先了解其组织方式。

Jmeter的组织方式相对比较扁平，它首先没有WorkSpace（工作空间）的概念，直接是TestPlan（测试计划），TestPlan（测试计划）下创建的Threads Group（线程组）就相当于TestCase（测试用例），并没有TestSuite（测试套件）的层级。TheadsGroup（线程组）中的Sampler（取样器）、管理器代表一个Step（测试步骤）
Postman功能上更简单，组织方式也更轻量级，它主要针对的就是单个的HTTP请求。Collection就相当于是Project（项目），而Collection中可以创建不定层级的Folders（文件夹），可以自己组织TestSuite（测试套件）。每个Request（请求）可以当做是一个TestCase（测试用例）或者Step（测试步骤）
```



* 区别二：创建Request步骤

```
Postman和Jmeter都是创建http请求：

区别1：Postman请求的请求URL是一个整体，Jmeter分成了4个部分（协议、主机、端口、路径）
区别2：Postman可以在请求中直接填写请求头信息， Jmeter需要通过添加http信息头管理器来添加请求头
区别3：对于cookie，Postman通过Cookies可以对cookie做管理，Jmeter只需添加http cookie管理器即可完成cookie的处理，并且是自动处理cookie信息
Postman在Pre-request Script可以添加前置请求，对请求参数进行处理；通过Tests获取响应数据，比较容易进行json结果的处理，很方便的提取json数据。Jmeter不仅可以处理json数据（json提取器），还可以提取其他数据（正则表达式提取器）
```



* 区别三：支持的接口类型与测试类型

```
Jmeter的功能更强大，可以测试各种类型的接口，不支持的也可以通过网上或自己编写的插件进行扩展
Postman更轻量级，定位也不同，可用来测试Rest接口
```



* 区别四：自定义变量以及变量的作用域

```
除以下表格中所列的变量之外，两个工具也都有系统变量，没有列出。
Jmeter：

变量类型	作用域
TestPlan中用户定义的变量	所有Threads Group
配置元件 - 用户定义的变量	根据元件位置而定
CSV data set config、random variable等	根据元件位置而定
前置、后置处理器	当前Threads Group
Postman：

变量类型	作用域
Environment Variable	当前环境的Collection
Global Variable	所有Collections
CSV/JSON datafile	Runner当前的Collection
```



* 区别五：数据源、生成器，进行参数化

```
区别1：Jmeter比较适合进行数据与操作分离，而Postman比较适合把数据和操作放在一起，显然Postman操作更简单，Jmeter更便于维护
区别2：Postman也支持csv数据文件的导入，但是每次执行时都需要收工加载数据文件，不方便（所以只能做半自动化）。Jmeter可使用csv数据导入、CSVRead函数、用户定义的变量、用户参数等多种方式实现参数化，使用更方便
Jmeter可以进行完全自动化，特别是引入ant后效果更明显
Postman也可以用newman命令行做自动化，使用方法参考newman命令行测试
Jmeter：

数据源	生成器	循环
CSV Data Set Config读取csv文件	Random Variable计数器	ForEach控制器、循环控制器、While控制器
Postman：

数据源	生成器	循环
Runner中运行时，可加载CSV/JSON文件	无（只能通过脚本）	Runner中的Iteration
```



* 区别六：流程控制

```
Jmeter：由Switch控制器、If控制器、随机控制器等一系列控制器实现流程控制，以及Beanshell脚本
Postman：通过JavaScript脚本控制
```



* 区别七：断言

```
区别1：Postman有很多自带的断言函数，直接引用即可，操作非常方便。 jmeter也自带断言组件，操作非常直观。 区别：Postman用函数断言， Jmeter用元件进行断言
区别2：Jmeter支持正则表达式断言，Postman不支持
区别3：Jmeter的断言更丰富。 Postman需要通过编程来实现同样的效果，所以难度更大
Jmeter：TestPlan、Threads Group、Sampler均可添加断言
Postman：请求的Tests中可添加断言
```



* 区别八：执行

```
区别：默认执行，postman不能保存结果，jmeter可以保存结果
Postman：可以通过newman实现批量执行和保存结果，使用方法参考newman命令行测试
Jmeter：可以通过ant实现批量执行和保存结果
```



* 区别九：脚本扩展能力

```
Jmeter：Bean shell（Java）
Postman：JavaScript
```



* 区别十：其他

```
Postman比较适合做手工接口测试，用来发现BUG，验证后台程序，因为简单，可以实现半自动化
Jmeter比较适合自动化接口测试，冒烟测试，因为功能强大并且可以保存脚本，批量执行设置很容易
```

