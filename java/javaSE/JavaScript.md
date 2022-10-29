## 1、JavaScript代码嵌入方式

### [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_1html文档内)①HTML文档内

- JavaScript代码要写在script标签内
- script标签可以写在文档内的任意位置
- 为了能够方便查询或操作HTML标签（元素）script标签可以写在body标签后面

可以参考简化版的HelloWorld

```html
<!-- 在HBuilderX中，script标签通过打字“sc”两个字母就可以直接完整生成 -->
<script type="text/javascript">
	
	// 下面是同样实现HelloWorld功能的简化版代码
	document.getElementById("helloBtn").onclick = function() {
		alert("Hello simple");
	};
	
</script>
```

### [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_2引入外部javascript文档)②引入外部JavaScript文档

在script标签内通过src属性指定外部xxx.js文件的路径即可。但是要注意以下两点：

- 引用外部JavaScript文件的script标签里面不能写JavaScript代码
- 先引入，再使用
- script标签不能写成单标签

![./images](data:image/png;base64,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)

引入方式如下：

```html
<body>
</body>

<!-- 使用script标签的src属性引用外部JavaScript文件，和Java中的import语句类似 -->
<!-- 引用外部JavaScript文件的script标签里面不能写JavaScript代码 -->
<!-- 引用外部JavaScript文件的script标签不能改成单标签 -->
<!-- 外部JavaScript文件一定要先引入再使用 -->
<script src="/pro02-JavaScript/scripts/outter.js" type="text/javascript" charset="utf-8"></script>

<script type="text/javascript">
	
	// 调用外部JavaScript文件中声明的方法
	showMessage();
</script>
```

## [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_2、声明和使用变量)2、声明和使用变量

### [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_1javascript数据类型)①JavaScript数据类型

- 基本数据类型

  - 数值型：JavaScript不区分整数、小数

  - 字符串：JavaScript不区分字符、字符串；单引号、双引号意思一样。

  - 布尔型：true、false

    在JavaScript中，其他类型和布尔类型的自动转换。

    true：非零的数值，非空字符串，非空对象

    false：零，空字符串，null，undefined

    例如："false"放在if判断中

    ```javascript
    // "false"是一个非空字符串，直接放在if判断中会被当作『真』处理
    if("false"){
    	alert("true");
    }else{
    	alert("false");
    }
    ```

- 引用类型

  - 所有new出来的对象
  - 用[]声明的数组
  - 用{}声明的对象

### [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_2变量)②变量

- 关键字：var

- 数据类型：JavaScript变量可以接收任意类型的数据

- 标识符：严格区分大小写

- 变量使用规则

  - 如果使用了一个没有声明的变量，那么会在运行时报错

    Uncaught ReferenceError: b is not defined

  - 如果声明一个变量没有初始化，那么这个变量的值就是undefined

## [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_3、函数)3、函数

### [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_1内置函数)①内置函数

内置函数：系统已经声明好了可以直接使用的函数。

#### [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_1-弹出警告框)[1]弹出警告框

```javascript
alert("警告框内容");
```

1

#### [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_2-弹出确认框)[2]弹出确认框

用户点击『确定』返回true，点击『取消』返回false

```javascript
var result = confirm("老板，你真的不加个钟吗？");
if(result) {
	console.log("老板点了确定，表示要加钟");
}else{
	console.log("老板点了确定，表示不加钟");
}
```



#### [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_3-在控制台打印日志)[3]在控制台打印日志

```javascript
console.log("日志内容");
```



![./images](data:image/png;base64,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)

### [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_2声明函数)②声明函数

写法1：

```javascript
		function sum(a, b) {
			return a+b;
		}
```



写法2：

```javascript
		var total = function() {
			return a+b;
		};
```



写法2可以这样解读：声明一个函数，相当于创建了一个『函数对象』，将这个对象的『引用』赋值给变量total。最后加的分号不是给函数声明加的，而是给整体的赋值语句加的分号。

### [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_3调用函数)③调用函数

JavaScript中函数本身就是一种对象，函数名就是这个**『对象』**的**『引用』**。而调用函数的格式是：**函数引用()**。

```javascript
		function sum(a, b) {
			return a+b;
		}
		
		var result = sum(2, 3);
		console.log("result="+result);
```



或：

```javascript
		var total = function() {
			return a+b;
		}
		
		var totalResult = total(3,6);
		console.log("totalResult="+totalResult);
```



## [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_4、对象)4、对象

JavaScript中没有『类』的概念，对于系统内置的对象可以直接创建使用。

### [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_1使用new关键字创建对象)①使用new关键字创建对象

```javascript
		// 创建对象
		var obj01 = new Object();
		
		// 给对象设置属性和属性值
		obj01.stuName = "tom";
		obj01.stuAge = 20;
		obj01.stuSubject = "java";
		
		// 在控制台输出对象
		console.log(obj01);
```



### [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_2使用-创建对象)②使用{}创建对象

```javascript
		// 创建对象
		var obj02 = {
			"soldierName":"john",
			"soldierAge":35,
			"soldierWeapon":"gun"
		};
		
		// 在控制台输出对象
		console.log(obj02);
```



### [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_3给对象设置函数属性)③给对象设置函数属性

```javascript
// 创建对象
var obj01 = new Object();

// 给对象设置属性和属性值
obj01.stuName = "tom";
obj01.stuAge = 20;
obj01.stuSubject = "java";

obj01.study = function() {
	console.log(this.stuName + " is studying");
};

// 在控制台输出对象
console.log(obj01);
// 调用函数
obj01.study();
```



或：

```javascript
// 创建对象
var obj02 = {
	"soldierName":"john",
	"soldierAge":35,
	"soldierWeapon":"gun",
	"soldierShoot":function(){
		console.log(this.soldierName + " is using " + this.soldierWeapon);
	}
};

// 在控制台输出对象
console.log(obj02);
// 调用函数
obj02.soldierShoot();
```



### [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_4this关键字)④this关键字

this关键字只有两种情况：

- 在函数外面：this关键字指向window对象（代表当前浏览器窗口）
- 在函数里面：this关键字指向调用函数的对象

```javascript
// 直接打印this
console.log(this);

// 函数中的this
// 1.声明函数
function getName() {
	console.log(this.name);
}

// 2.创建对象
var obj01 = {
	"name":"tom",
	"getName":getName
};
var obj02 = {
	"name":"jerry",
	"getName":getName
};

// 3.调用函数
obj01.getName();
obj02.getName();
```



## [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_5、数组)5、数组

### [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_1使用new关键字创建数组)①使用new关键字创建数组

```javascript
// 1.创建数组对象
var arr01 = new Array();

// 2.压入数据
arr01.push("apple");
arr01.push("orange");
arr01.push("banana");
arr01.push("grape");

// 3.遍历数组
for (var i = 0; i < arr01.length; i++) {
	console.log(arr01[i]);
}

// 4.数组元素反序
arr01.reverse();
for (var i = 0; i < arr01.length; i++) {
	console.log(arr01[i]);
}

// 5.数组元素拼接成字符串
var arrStr = arr01.join(",");
console.log(arrStr);

// 6.字符串拆分成数组
var arr02 = arrStr.split(",");
for (var i = 0; i < arr02.length; i++) {
	console.log(arr02[i]);
}

// 7.弹出数组中最后一个元素
var ele = arr01.pop();
console.log(ele);
```



### [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_2使用-创建数组)②使用[]创建数组

```javascript
// 8.使用[]创建数组
var arr03 = ["cat","dog","tiger"];
console.log(arr03);
```



## [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_6、json)6、JSON

### [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_1json格式的用途)①JSON格式的用途

在开发中凡是涉及到**『跨平台数据传输』**，JSON格式一定是首选。

### [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_2json格式的说明)②JSON格式的说明

- JSON数据两端要么是**{}**，要么是**[]**
- **{}**定义JSON对象
- **[]**定义JSON数组
- JSON对象的格式是：

```json
{key:value,key:value,...,key:value}
```



- JOSN数组的格式是：

```json
[value,value,...,value]
```



- key的类型固定是字符串
- value的类型可以是：
  - 基本数据类型
  - 引用类型：JSON对象或JSON数组

正因为JSON格式中value部分还可以继续使用JSON对象或JSON数组，所以JSON格式是可以**『多层嵌套』**的，所以JSON格式不论多么复杂的数据类型都可以表达。

```json
{
	"stuId":556,
	"stuName":"carl",
	"school":{
		"schoolId":339,
		"schoolName":"atguigu"
	},
	"subjectList":[
		{
			"subjectName":"java",
			"subjectScore":50
		},
		{
			"subjectName":"PHP",
			"subjectScore":35
		},
		{
			"subjectName":"python",
			"subjectScore":24
		}
	],
	"teacherMap":{
		"aaa":{
			"teacherName":"zhangsan",
			"teacherAge":20
		},
		"bbb":{
			"teacherName":"zhangsanfeng",
			"teacherAge":108
		},
		"ccc":{
			"teacherName":"zhangwuji",
			"teacherAge":25
		}
	}
}
```



### [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_3json对象和json字符串互转)③JSON对象和JSON字符串互转

#### [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_1-json对象转json字符串)[1]JSON对象转JSON字符串

```javascript
var jsonObj = {"stuName":"tom","stuAge":20};
var jsonStr = JSON.stringify(jsonObj);

console.log(typeof jsonObj); // object
console.log(typeof jsonStr); // string
```



#### [#](https://heavy_code_industry.gitee.io/code_heavy_industry/pro001-javaweb/lecture/chapter03/verse03.html#_2-json字符串转json对象)[2]JSON字符串转JSON对象

```javascript
jsonObj = JSON.parse(jsonStr);
console.log(jsonObj); // {stuName: "tom", stuAge: 20}
```