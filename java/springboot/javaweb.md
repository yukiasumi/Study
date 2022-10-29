# javaweb

### tomcat

更改tomact/conf下的server.xml文件的8080默认端口可以更改访问端口

![image-20220428202918188](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220428202918188.png)

![image-20220428202944175](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220428202944175.png)



### 控制台乱码

```
第一步:修改intellij idea配置文件：

找到intellij idea安装目录，bin文件夹下面idea64.exe.vmoptions和idea.exe.vmoptions这两个文件，分别在这两个文件中添加：-Dfile.encoding=UTF-8

第二步：找到intellij idea的file---settings---Editor---FileEncodings的GlobalEncoding和ProjectEncoding和Default encoding for properties都配置成UTF-8

第三步：在部署Tomcat的VM options项中添加：-Dfile.encoding=UTF-8

第四步：重启Intellij idea即可解决乱码问题

第五步：request.setCharacterEncoding("utf-8");
```

### 新建项目

<font color=red>修改代码后需要重新部署，注意servlet-class不要写错了 </font>

 ```
   1. 新建项目 - 新建模块
   2. 在模块中添加web
   3. 创建artifact - 部署包
   4. lib - artifact
      先有artifact，后来才添加的mysql.jar。此时，这个jar包并没有添加到部署包中
      那么在projectSettings中有一个Problems中会有提示的,我们点击fix选择add to...
      另外，我们也可以直接把lib文件夹直接新建在WEB-INF下。
      这样不好的地方是这个lib只能是当前这个moudle独享。如果有第二个moudle我们需要再次重复的新建lib。
   5. 在部署的时候，修改application Context。然后再回到server选项卡，检查URL的值。
      URL的值指的是tomcat启动完成后自动打开你指定的浏览器，然后默认访问的网址。
      启动后，报错404.404意味着找不到指定的资源。
      如果我们的网址是：http://localhost:8080/pro01/ , 那么表明我们访问的是index.html.
      我们可以通过<welcome-file-list>标签进行设置欢迎页(在tomcat的web.xml中设置，或者在自己项目的web.xml中设置)
   6. 405问题。当前请求的方法不支持。比如，我们表单method=post , 那么Servlet必须对应doPost。否则报405错误。
   7. 空指针或者是NumberFormatException 。因为有价格和库存。如果价格取不到，结果你想对null进行Integer.parseInt()就会报错。错误的原因大部分是因为 name="price"此处写错了，结果在Servlet端还是使用request.getParameter("price")去获取。
   8. <url-pattern>中以斜杠开头
 ```

   #### web.xml文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">
    <servlet>
        <servlet-name>AddServlet</servlet-name>
        <servlet-class>com.example.javaweb.servlets.AddServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>AddServlet</servlet-name>
        <url-pattern>/add</url-pattern>
    </servlet-mapping>
    <servlet>
        <servlet-name>TestServlet02 </servlet-name>
        <servlet-class>com.example.javaweb.servlets.TestServlet02</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>TestServlet02</servlet-name>
        <url-pattern>/test02</url-pattern>
    </servlet-mapping>
    <!--
    1.用户发请求 action=add
    2.项目中, web.xml中找到url-pattern = / add ->第12行
    3.找第11行的servlet-name = Addservlet
    4.找和servlet-mapping中servlet-name一致的servlet找到第7行
    5.找第8行的servlet-class -> com.atguigu.servlets.AddServlet
    6.用户发送的是post请求(method=post),因此 tomcat会执行Addservlet中的doPost方法
    -->
</web-app>
```



#### 设置编码

tomcat8之前，设置编码：
  ##### 1)get请求方式：
    //get方式目前不需要设置编码（基于tomcat8）
    //如果是get请求发送的中文数据，转码稍微有点麻烦（tomcat8之前）
    String fname = request.getParameter("fname");
    //1.将字符串打散成字节数组
    byte[] bytes = fname.getBytes("ISO-8859-1");
    //2.将字节数组按照设定的编码重新组装成字符串
    fname = new String(bytes,"UTF-8");
  ##### 2)post请求方式：
    request.setCharacterEncoding("UTF-8");
tomcat8开始，设置编码，只需要针对post方式
    request.setCharacterEncoding("UTF-8");
注意：
    需要注意的是，设置编码(post)这一句代码必须在所有的获取参数动作之前


#### Servlet的继承关系 - 重点查看的是<font color = red>服务方法（service()）</font>
##### 1. 继承关系
```java
javax.servlet.Servlet接口
  javax.servlet.GenericServlet抽象类
  	javax.servlet.http.HttpServlet抽象子类
```

##### 2. 相关方法

```java
javax.servlet.Servlet接口:
void init(config) - 初始化方法
void service(request,response) - 服务方法
void destory() - 销毁方法

  javax.servlet.GenericServlet抽象类：
void service(request,response) - 仍然是抽象的

  javax.servlet.http.HttpServlet 抽象子类：
    void service(request,response) - 不是抽象的
    1. String method = req.getMethod(); 获取请求的方式
        2. 各种if判断，根据请求方式不同，决定去调用不同的do方法
        if (method.equals("GET")) {
            this.doGet(req,resp);
        } else if (method.equals("HEAD")) {
            this.doHead(req, resp);
        } else if (method.equals("POST")) {
            this.doPost(req, resp);
        } else if (method.equals("PUT")) {
        3. 在HttpServlet这个抽象类中，do方法都差不多:
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = lStrings.getString("http.method_get_not_supported");
        if (protocol.endsWith("1.1")) {
            resp.sendError(405, msg);
        } else {
            resp.sendError(400, msg);
        }
        }
```

  ##### 3.小结：

1) 继承关系： HttpServlet -> GenericServlet -> Servlet
​2) Servlet中的核心方法： init() , service() , destroy()

<font color=red>
3) 服务方法： 当有请求过来时，service方法会自动响应（其实是tomcat容器调用的）
        在HttpServlet中我们会去分析请求的方式：到底是get、post、head还是delete等等
        然后再决定调用的是哪个do开头的方法
        那么在HttpServlet中这些do方法默认都是405的实现风格-要我们子类去实现对应的方法，否则默认会报405错误</font>
4) 因此，我们在新建Servlet时，我们才会去考虑请求方法，从而决定重写哪个do方法


#### Servlet的生命周期
    1） 生命周期：从出生到死亡的过程就是生命周期。对应Servlet中的三个方法：init(),service(),destroy()
    2） 默认情况下：
        第一次接收请求时，这个Servlet会进行实例化(调用构造方法)、初始化(调用init())、然后服务(调用service())
        从第二次请求开始，每一次都是服务
        当容器关闭时，其中的所有的servlet实例会被销毁，调用销毁方法
    3） 通过案例我们发现：
        - Servlet实例tomcat只会创建一个，所有的请求都是这个实例去响应。
        - 默认情况下，第一次请求时，tomcat才会去实例化，初始化，然后再服务.这样的好处是什么？ 提高系统的启动速度 。 这样的缺点是什么？ 第一次请求时，耗时较长。
        - 因此得出结论： 如果需要提高系统的启动速度，当前默认情况就是这样。如果需要提高响应速度，我们应该设置Servlet的初始化时机。
    4） Servlet的初始化时机：
        - 默认是第一次接收请求时，实例化，初始化
        - 我们可以通过<load-on-startup>来设置servlet启动的先后顺序,数字越小，启动越靠前，最小值0
    5） Servlet在容器中是：单例的、线程不安全的
        - 单例：所有的请求都是同一个实例去响应
        - 线程不安全：一个线程需要根据这个实例中的某个成员变量值去做逻辑判断。但是在中间某个时机，另一个线程改变了这个成员变量的值，从而导致第一个线程的执行路径发生了变化
        - 我们已经知道了servlet是线程不安全的，给我们的启发是： 尽量的不要在servlet中定义成员变量。如果不得不定义成员变量，那么不要去：①不要去修改成员变量的值 ②不要去根据成员变量的值做一些逻辑判断

![image-20220430113250357](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220430113250357.png)

<font color=red>将servlet构造方法私有化会报500错误</font>

#### Http协议

    1） Http 称之为 超文本传输协议
    2） Http是无状态的
    3） Http请求响应包含两个部分：请求和响应
    
      - 请求：
        请求包含三个部分： 1.请求行 ； 2.请求消息头 ； 3.请求主体
        1)请求行包含是三个信息： 1. 请求的方式 ； 2.请求的URL ； 3.请求的协议（一般都是HTTP1.1）
        2)请求消息头中包含了很多客户端需要告诉服务器的信息，比如：我的浏览器型号、版本、我能接收的内容的类型、我给你发的内容的类型、内容的长度等等
        3)请求体，三种情况
          get方式，没有请求体，但是有一个queryString
          post方式，有请求体，form data
          json格式，有请求体，request payload
      - 响应：
        响应也包含三本： 1. 响应行 ； 2.响应头 ； 3.响应体
        1)响应行包含三个信息：1.协议 2.响应状态码(200) 3.响应状态(ok)
        2)响应头：包含了服务器的信息；服务器发送给浏览器的信息（内容的媒体类型、编码、内容长度等）
    3)响应体：响应的实际内容（比如请求add.html页面时，响应的内容就是<html><head><body><form....）

#### 会话

<font color=red>sessionID绑定浏览器</font>

    1） Http是无状态的
        - HTTP 无状态 ：服务器无法判断这两次请求是同一个客户端发过来的，还是不同的客户端发过来的
            - 无状态带来的现实问题：第一次请求是添加商品到购物车，第二次请求是结账；如果这两次请求服务器无法区分是同一个用户的，那么就会导致混乱
            - 通过会话跟踪技术来解决无状态的问题。
    
    2） 会话跟踪技术
        - 客户端第一次发请求给服务器，服务器获取session，获取不到，则创建新的，然后响应给客户端
        - 下次客户端给服务器发请求时，会把sessionID带给服务器，那么服务器就能获取到了，那么服务器就判断这一次请求和上次某次请求是同一个客户端，从而能够区分开客户端
        - 常用的API：
          request.getSession() -> 获取当前的会话，没有则创建一个新的会话
          request.getSession(true) -> 效果和不带参数相同
          request.getSession(false) -> 获取当前会话，没有则返回null，不会创建新的
    
          session.getId() -> 获取sessionID
          session.isNew() -> 判断当前session是否是新的
          session.getMaxInactiveInterval() -> session的非激活间隔时长，默认1800秒
          session.setMaxInactiveInterval()
          session.invalidate() -> 强制性让会话立即失效
          ....
    
    3） session保存作用域
      - session保存作用域是和具体的某一个session对应的
      - 常用的API：
        void session.setAttribute(k,v)
        Object session.getAttribute(k)
        void removeAttribute(k)

![image-20220430172343902](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220430172343902.png)

![image-20220430145900955](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220430145900955.png)

![image-20220430172409481](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220430172409481.png)

#### 服务器内部转发以及客户端重定向

    1） 服务器内部转发 : request.getRequestDispatcher("...").forward(request,response);
      - 一次请求响应的过程，对于客户端而言，内部经过了多少次转发，客户端是不知道的
      - 地址栏没有变化
    2） 客户端重定向： response.sendRedirect("....");
      - 两次请求响应的过程。客户端肯定知道请求URL有变化
      - 地址栏有变化
![image-20220430204408349](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220430204408349.png)

![image-20220430204751616](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220430204751616.png)

#### Thymeleaf - 视图模板技术

    1） 添加thymeleaf的jar包
    2） 新建一个Servlet类ViewBaseServlet
    3） 在web.xml文件中添加配置
       - 配置前缀 view-prefix
       - 配置后缀 view-suffix
    4） 使得我们的Servlet继承ViewBaseServlet
    
    5） 根据逻辑视图名称 得到 物理视图名称
    //此处的视图名称是 index
    //那么thymeleaf会将这个 逻辑视图名称 对应到 物理视图 名称上去
    //逻辑视图名称 ：   index
    //物理视图名称 ：   view-prefix + 逻辑视图名称 + view-suffix
    //所以真实的视图名称是：      /       index       .html
    super.processTemplate("index",request,response);
    6） 使用thymeleaf的标签
      th:if   ,  th:unless   , th:each   ,   th:text

![image-20220430214158143](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220430214158143.png)

// 200 : 正常响应
// 404 : 找不到资源
// 405 : 请求方式不支持
// 500 : 服务器内部错误

### 1. 保存作用域

```
原始情况下，保存作用域我们可以认为有四个： 
page（页面级别，现在几乎不用）,
request（一次请求响应范围） , 
session（一次会话范围） , 
application（整个应用程序范围）
```



#### 1)request：一次请求响应范围

```java
//演示request保存作用域
@WebServlet("/test1")
public class ServletTest01 extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.向request保存作用域保存数据
        request.setAttribute("name","lisa");
        //2.客户端重定向(request范围外)
        //response.sendedirect("test2");
        //3.服务器端转发(request范围内)
        request.getRequestDispatcher("test2").forward(request, response);
    }
}

@WebServlet("/test2")
public class ServletTest02 extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.获取request保存的作用域保存的数据
        Object name = request.getAttribute("name");
        System.out.println(name);
    }
}
```

![request请求作用域](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\01.Request保存作用域.png)

#### session：一次会话范围有效

只要不换浏览器就可以访问到

```java
//演示session保存作用域
@WebServlet("/test3")
public class ServletTest03 extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.向request保存作用域保存数据
        request.getSession().setAttribute("name","lisa");
        //2.客户端重定向(session范围内)
        response.sendRedirect("test4");
        //3.服务器端转发(session范围内)
        //request.getRequestDispatcher("test2").forward(request, response);
    }
}

@WebServlet("/test4")
public class ServletTest04 extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.获取session保存的作用域保存的数据
        Object name = request.getSession().getAttribute("name");
        System.out.println(name);
    }
}
```

![session作用域](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\02.Session保存作用域.png)

#### application： 一次应用程序范围有效
不关闭tomcat就能访问到

```java
//演示application保存作用域
@WebServlet("/test5")
public class ServletTest05 extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.向application保存作用域保存数据
        //ServletContext: Servlet上下文
        ServletContext application = request.getServletContext();
        application.setAttribute("name","lisa");
        //2.客户端重定向(application范围内)
        response.sendRedirect("test6");
        //3.服务器端转发(application范围内)
        //request.getRequestDispatcher("test2").forward(request, response);
    }
}

@WebServlet("/test6")
public class ServletTest06 extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.获取application保存的作用域保存的数据
        ServletContext application = request.getServletContext();
        Object name = application.getAttribute("name");
        System.out.println(name);
    }
}

```

![application作用域](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\03.Application保存作用域.png)
### 路径问题
1） 相对路径
2） 绝对路径

<img src="C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\04.相对路径_绝对路径_base标签_@{}.png" alt="路径"  />

### Thymeleaf

Thymeleaf 模板引擎支持多种表达式：

- 变量表达式：${...}
- 选择变量表达式：*{...}
- 链接表达式：@{...}
- 国际化表达式：#{...}
- 片段引用表达式：~{...}





