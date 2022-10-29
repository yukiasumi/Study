2、反射机制（比较简单，因为只要会查帮助文档，就可以了。）
	
	2.1、反射机制有什么用？
		通过java语言中的反射机制可以操作字节码文件。
		优点类似于黑客。（可以读和修改字节码文件。）
		通过反射机制可以操作代码片段。（class文件。）
	
	2.2、反射机制的相关类在哪个包下？
		java.lang.reflect.*;
	
	2.3、反射机制相关的重要的类有哪些？
	
		java.lang.Class：代表整个字节码，代表一个类型，代表整个类。
	
		java.lang.reflect.Method：代表字节码中的方法字节码。代表类中的方法。
	
		java.lang.reflect.Constructor：代表字节码中的构造方法字节码。代表类中的构造方法
	
		java.lang.reflect.Field：代表字节码中的属性字节码。代表类中的成员变量（静态变量+实例变量）。
	
		java.lang.Class：
			public class User{
				// Field
				int no;
	
				// Constructor
				public User(){
				
				}
				public User(int no){
					this.no = no;
				}
	
				// Method
				public void setNo(int no){
					this.no = no;
				}
				public int getNo(){
					return no;
				}
			}



```java
import java.util.Date;

/**
 *
 * 要操作一个类的字节码，首先要获取一个类的字节码，，怎么获取java.lang.Class实例？
 * 三种方式
 * 第一种: Class c = class.forName("完整类名带包名");
 * 第二种: class c =对象.getClass();
 * 第三种:class c =任何类型.class;
 */
public class ReflectTest01 {
    public static void main(String[] args) {
        /*
        Class.forName( )
            1、静态方法
            2、方法的参数是一个字符串。
            3、字符串需要的是一个完整类名。
            4、完整类名必须带有包名。java .Lang包也不能省略。
         */
        Class c1 = null;
        try {
            c1 = Class.forName("java.lang.String");//c1代表string.class文件，或者说c1代表string类型
            Class c2 = Class.forName("java.util.Date");// c2代表Date类型
            Class c3 = Class.forName("java.lang.Integer");//c3代表Integer类型
            Class c4 =Class.forName("java.lang.System");// c4代表System类型
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //java中任何一个对象都有一个方法 : getCLass()【Object老祖宗自带的基因】
        String s= "abc";
        Class x = s.getClass();//c1代表string.class文件，或者说c1代表string类型
        System.out.println(c1==x);//true（判断的是内存地址）

        //第三种方式，java语言中任何一种类型，包括基本数据类型，它都有.cLass属性。
            Class z = String.class;
            Class k = Date.class;
            Class f = int.class;

        System.out.println(z==x);
    }
}
```



![image-20220130141229812](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220130141229812.png)

```java
import bean.User;

/**
 * 通过CLass 的newInstance()方法来实例化对象。
 * 注意: newInstance()方法内部实际上调用了无参数构造方法，必须保证无参构造存在才可以。
 */
public class ReflectTest02 {
    public static void main(String[] args) {
        User user = new User();
        System.out.println(user);
        try {
            //通过反射机制，获取Class，通过Class来实例化对象
             Class c= Class.forName("Reflect.bean.User");
        //newInstance()这个方法会调用user这个类的无参数构造方法，完成对象的创建。
        //重点是：newInstance调用的是无参数构造，完成对象的创建
            Object object = c.newInstance();
            System.out.println(object);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
```

```java
import java.io.FileReader;
import java.util.Properties;

/**
 * 验证反射机制的灵活性
 *      java代码写一遍，在不改变java源代码的基础之上，可以做到不同对象的实例化。
 *      非常之灵活。(符合OCP开闭原则:对扩展开放，对修改关闭。)
 *
 * 后期你们要学习的是高级框架，而工作过程中，也都是使用高级框架，
 * 包括: ssh ssm
 *      Spring SpringMVC MyBatis
 *      Spring Struts Hibernate...
 * 这些高级框架底层实现原理:都采用了反射机制。所以反射机制还是重要的。
 * 学会了反射机制有利于你理解剖析框架底层的源代码。
 */
public class ReflectTest03 {
    public static void main(String[] args) throws Exception {

        //这种方式代码就写死了。只能创建一个User类型的对象
        // User user = new User();


        //以下代码是灵活的，代码不需要改动，可以修改配置文件，配置文件修改之后，可以创建出不同的实例对象
        //通过IO流读取classinfo.properties文件
        FileReader reader = new FileReader("I:\\IDEA\\T037_ranther\\src\\main\\java\\Reflect\\classinfo.properties");
        //创建属性类对象Map
        Properties properties = new Properties();//key value都是String
        //加载
        properties.load(reader);
        //关闭流
        reader.close();

        //通过key获取value
        String className =  properties.getProperty("className");
       // System.out.println(className);

        //通过反射机制实例化对象
        Class  c  = Class.forName(className);
        Object object = c.newInstance();
        System.out.println(object);

    }
}
```



```java
/**
 * 研究一下：Class.forName()发生了什么?
 *  记住，重点:
 *      如果你只是希望一个类的静态代码块执行，其它代码一律不执行，
 *      你可以使用:
 *          Class.forName("完整类名");
 *      这个方法的执行会导致类加载，类加载时，静态代码块执行。
 *     后面JDBC需要使用
 */
public class ReflectTest04 {
    public static void main(String[] args) {
        try {
            //class.forName ( )这个方法的执行会导致:类加载。
            Class c = Class.forName("Reflect.MyClass");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class MyClass{
    //静态代码块在类加载时执行，并且只执行一次
    static {
        System.out.println("MyClass类的静态代码块执行了!");
    }
}
```

3、关于JDK中自带的类加载器：（聊一聊，不需要掌握，知道当然最好！）
	3.1、什么是类加载器？
		专门负责加载类的命令/工具。
		ClassLoader

```java
import java.io.FileNotFoundException;


/**
 * 研究一下文件路径的问题
 *
 */
public class AboutPath {
    public static void main(String[] args) throws FileNotFoundException {
        //这种方式的路径缺点是:移植性差，在IDEA中默认的当前路径是project的根。
        //这个代码假设离开了IDEA，换到了其它位置，可能当前路径就不是project的根了，这时这个路径就无效了。
    //    FileReader reader = new FileReader("src\\main\\java\\Reflect\\classinfo.properties");
        //接下来说一种比较通用的一种路径。即使代码换位置了，这样编写仍然是通用的。
        // 注意:使用以下通用方式的前提是:这个文件必须在类路径下。
        //什么类路径下?方式在src下的都是类路径下。【记住它】
        // src是类的根路径。
        /*
        解释:
            Thread.currentThread()当前线程对象
            getContextCLassLoader()是线程对象的方法，可以获取到当前线程的类加载器对象。
            getResource()【获取资源】这是类加载器对象的方法，当前线程的类加载器默认从类的根路径下加载资源。
           */
        String path = Thread.currentThread().getContextClassLoader().getResource("classinfo2.properties").getPath();
        //采用以上的代码可以拿到一个文件的绝对路径。
        //可以不写classinfo2.properties，查看路径

        System.out.println(path);

        //获取java文件的绝对路径【class文件】
        String path2 = Thread.currentThread().getContextClassLoader().getResource("Reflect/bean/User.class").getPath();
        System.out.println(path2);
    }
}
```

​	

```java
/**
 * java.util包下提供了一个资源绑定器，便于获取属性配置文件中的内容。
 * 使用以下这种方式的时候，属性配置文件xxx.properties必须放到类路径下。
 */
public class ResourceBundleTest {
    public static void main(String[] args) {
        //资源绑定器，只能绑定xxx. properties文件。并且这个文件必须在类路怪下。
        //文件扩展名也必须是properties并且在写路径的时候，路径后面的扩展名不能写。
      //  ResourceBundle bundle = ResourceBundle.getBundle("classinfo3");

        ResourceBundle bundle = ResourceBundle.getBundle("Reflect\\bean\\db");


        String className = bundle.getString("className");
        System.out.println(className);

    }
}
```

	3.2、JDK中自带了3个类加载器
		启动类加载器:rt.jar
		扩展类加载器:ext/*.jar
		应用类加载器:classpath
	
	3.3、假设有这样一段代码：
		String s = "abc";
		
		代码在开始执行之前，会将所需要类全部加载到JVM当中。
		通过类加载器加载，看到以上代码类加载器会找String.class
		文件，找到就加载，那么是怎么进行加载的呢？
	
			首先通过“启动类加载器”加载。
				注意：启动类加载器专门加载：C:\Program Files\Java\jdk1.8.0_101\jre\lib\rt.jar
				rt.jar中都是JDK最核心的类库。
			
			如果通过“启动类加载器”加载不到的时候，
			会通过"扩展类加载器"加载。
				注意：扩展类加载器专门加载：C:\Program Files\Java\jdk1.8.0_101\jre\lib\ext\*.jar
			
			如果“扩展类加载器”没有加载到，那么
			会通过“应用类加载器”加载。
				注意：应用类加载器专门加载：classpath中的类。【环境变量里】


​		
​	3.4、java中为了保证类加载的安全，使用了双亲委派机制。
​		优先从启动类加载器中加载，这个称为“父”
​		“父”无法加载到，再从扩展类加载器中加载，这个称为“母”。双亲委派。
​		如果都加载不到，才会考虑从应用类加载器中加载。直到加载到为止。


​	


1、回顾反射机制

```
1.1、什么是反射机制？反射机制有什么用？
	反射机制：可以操作字节码文件
	作用：可以让程序更加灵活。

1.2、反射机制相关的类在哪个包下？
	java.lang.reflect.*;

1.3、反射机制相关的主要的类？
	java.lang.Class
	java.lang.reflect.Method;
	java.lang.reflect.Constructor;
	java.lang.reflect.Field;

1.4、在java中获取Class的三种方式？
	第一种：	 
		Class c = Class.forName("完整类名");
	第二种：
		Class c = 对象.getClass();
	第三种：
		Class c = int.class;
		Class c = String.class;

1.5、获取了Class之后，可以调用无参数构造方法来实例化对象

	//c代表的就是日期Date类型
	Class c = Class.forName("java.util.Date");

	//实例化一个Date日期类型的对象
	Object obj = c.newInstance();

	一定要注意：
		newInstance()底层调用的是该类型的无参数构造方法。
		如果没有这个无参数构造方法会出现"实例化"异常。

1.6、如果你只想让一个类的“静态代码块”执行的话，你可以怎么做？
	Class.forName("该类的类名");
	这样类就加载，类加载的时候，静态代码块执行！！！！
	在这里，对该方法的返回值不感兴趣，主要是为了使用“类加载”这个动作。

1.7、关于路径问题？

	String path = Thread.currentThread().getContextClassLoader()
					  .getResource("写相对路径，但是这个相对路径从src出发开始找").getPath();	

	String path = Thread.currentThread().getContextClassLoader()
					  .getResource("abc").getPath();	//必须保证src下有abc文件。

	String path = Thread.currentThread().getContextClassLoader()
					  .getResource("a/db").getPath();	//必须保证src下有a目录，a目录下有db文件。
	
	String path = Thread.currentThread().getContextClassLoader()
					  .getResource("com/bjpowernode/test.properties").getPath();	
					  //必须保证src下有com目录，com目录下有bjpowernode目录。
					  //bjpowernode目录下有test.properties文件。

	这种方式是为了获取一个文件的绝对路径。（通用方式，不会受到环境移植的影响。）
	但是该文件要求放在类路径下，换句话说：也就是放到src下面。
	src下是类的根路径。

	直接以流的形式返回：
	InputStream in = Thread.currentThread().getContextClassLoader()
							.getResourceAsStream("com/bjpowernode/test.properties");

1.8、IO + Properties，怎么快速绑定属性资源文件？

	//要求：第一这个文件必须在类路径下
	//第二这个文件必须是以.properties结尾。
	ResourceBundle bundle = ResourceBundle.getBundle("com/bjpowernode/test");
	String value = bundle.getString(key);
```

```java
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 反射属性Field
 * 反射Student类当中所有的Field
 */
public class ReflectTest05 {
    public static void main(String[] args) throws Exception{
        //Field翻译为字段、属性、成员
        //获取整个类
        Class studentClass = Class.forName("Reflect.bean.Student");
        //Reflect.bean.Student
        String className = studentClass.getName();
        System.out.println("完整类名："+className);
        //Student
        String simpleName = studentClass.getSimpleName();
        System.out.println("简类名："+simpleName);

        //获取类中所有的public修饰的Field
       Field[] fields =  studentClass.getFields();
        System.out.println(fields.length);//测试数组中只有一个元素
        //取出这个Field
        Field field = fields[0];
        //取出这个Field它的名字
        String fieldName = field.getName();
        System.out.println(fieldName);

        System.out.println("-------------------");

        //获取所有的Field
        Field[] fs = studentClass.getDeclaredFields();
        System.out.println(fs.length);//4
        //遍历
        for (Field f : fs) {
            //获取属性修饰符列表
            int i = f.getModifiers();//返回
            System.out.println(i);//返回的修饰符是一个数字，每个数字是修饰符的代号!!!
            //可以将这个代号数字转换成字符串吗?
            String s = Modifier.toString(i);
            System.out.println(s);

            //获取属性的类型
            Class  fieldType= f.getType();
            //String fName = fieldType.getName();//java.lang.String
            String fName = fieldType.getSimpleName();//String
            System.out.println(fName);

            //获取属性的名字
            System.out.println(f.getName());
        }

    }
}
```

```java
public class Student {
    //4个Field分别采用了不同的访问控制权限修饰
    private String name;//Field对象
    public int no;//Field对象
    protected  int age;
    boolean sex;
    public static final double MATH_PI = 3.1415926;
}
```

```java
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

//通过反射机制，反编译一个类的属性FieLd
public class ReflectTest06 {
    public static void main(String[] args) throws Exception {
        //创建这个是为了拼接字符串。
        StringBuilder s = new StringBuilder();

       // Class studentClass = Class.forName("Reflect.bean.Student");
        Class studentClass = Class.forName("java.lang.Integer");

        s.append(Modifier.toString(studentClass.getModifiers())+" class "+studentClass.getSimpleName()+"{\n");

        Field[] fields = studentClass.getDeclaredFields();
        for (Field field : fields) {
            s.append("\t");
            s.append(Modifier.toString(field.getModifiers()));
            s.append(" ");
            s.append(field.getType().getSimpleName());
            s.append(" ");
            s.append(field.getName());
            s.append(";\n");
        }

        s.append("}");
        System.out.println(s);

    }

}
```

```java
import Reflect.bean.Student;

import java.lang.reflect.Field;

/**
 * 必须掌握:
 * 怎么通过反射机制访问一个java对象的属性?
 * 给属性赋值set
 * 获取属性的值get
 */
public class ReflectTest07 {
    public static void main(String[] args) throws Exception{
        //我们不使用反射机制，怎么去访问一个对象的属性呢?
        Student student = new Student();

        //给属性赋值
        student.no = 11111;//三要素：给s对象的no属性赋值11111
                            //要素1：对象
                            //要素2：no属性
                            //要素3:11111

        //读属性值
        //两个要素：获取s对象的no属性的值
        System.out.println(student.no);

        //使用反射机制，怎么去访问一个对象的属性。(set get)
        Class studentClass = Class.forName("Reflect.bean.Student");
        Object object = studentClass.newInstance();//obj就是Student对象，底层调用无参数构造方法。

        //获取no属性(根据属性的名称来获取Field)
        Field noField = studentClass.getDeclaredField("no");

        //给obj对象(Student对象)的no属性赋值
        /*
        虽然使用了反射机制，但是三要素还是缺一不可
            要素1：object对象
            要素2：no属性
            要素3：22222值
         注意:反射机制让代码复杂了，但是为了一个“灵活”，这也是值得的。
         */
        noField.set(object,22222);//给obj对象的no属性赋值22222

        //读取属性的值
        //两个要素：获取object对象的no属性的值
        System.out.println(noField.get(object));


        //可以访问私有的属性吗？
       Field nameFiled =  studentClass.getDeclaredField("name");

       //打破封装(反射机制的缺点:打破封装，可能会给不法分子留下机会!!!)
       //这样设置完之后，在外部也是可以访问private的。
        nameFiled.setAccessible(true);

       //给name属性赋值
        nameFiled.set(object,"tom");
        //获取name属性的值
        System.out.println(nameFiled.get(object));

    }
}
```

```java
/*
    可变长度参数
    int...args 这就是可变长度参数
    语法是：类型...(注意一定是3个点)

    1.可变长度参数要求的参数个数是:0~N个。
    2.可变长度参数在参数列表中必须在最后一个位置上，而且可变长度参数只能有1个
    3.可变长度参数可以当做一个数组来看待
 */
public class ArgsTest {
    public static void main(String[] args) {
        m();
        m(10);
        m(10,20,3,0);

        m2(100);
        m2(200,"asd","def","init");

        m3("x","y","z","r");

        String[] strings = {"a","b","c","d"};

        m3(strings);

        m3(new String[]{"1","2","3","4","5"});


    }

    public static void m(int...args) {
        System.out.println("m方法执行了~");
    }
    
    //必须在最后只能有一个
    public  static  void m2(int a,String...args1){
        
    }
    
    public static  void m3(String...args){
        //args有length属性，说明args是一个数组!
        //可以将可变长度参数当做一个数组来看。

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            System.out.println(arg);
        }
    }
}
```

```java
/**
 * 用户业务类
 */
public class UserService {
    /**
     * 登录方法
     * name用户名
     * password密码
     * true表示登录成功，false表示登录失败
     */
    public boolean login(String name ,String password){
        if ("admin".equals(name)&&"123".equals(password)){
            return true;
        }

        return false;
    }
    /**
     * 退出系统的方法
     */
    public  void logout(){
        System.out.println("系统已安全退出");
    }
}
```

```java
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/*
  作为了解内容(不需要掌握)：
      反射Method
 */
public class ReflectTest08 {
    public static void main(String[] args) throws Exception {
        //获取类
        Class userServiceClass = Class.forName("Reflect.bean.UserService");

        //获取所有的Method(包括私有的)
        Method[] methods = userServiceClass.getDeclaredMethods();
        System.out.println(methods.length);

        //遍历Method
        for (Method method : methods) {
            //获取修饰符列表
            System.out.println(Modifier.toString(method.getModifiers()));

            //获取方法的返回值类型
            System.out.println(method.getReturnType().getSimpleName());

            //获取方法名
            System.out.println(method.getName());

            //方法的修饰符列表（一个方法的参数可能会有多个）。
            Class[] parameterTypes = method.getParameterTypes();
            for (Class parameterType : parameterTypes) {
                System.out.println(parameterType.getSimpleName());
            }

        }
    }
}
```

```java
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/*
了解一下不需要掌握(反编译一个类的方法)
 */
public class ReflectTest09 {
    public static void main(String[] args) throws  Exception{
       StringBuilder s = new StringBuilder();
    //Reflect.bean.UserService
        Class userServiceClass = Class.forName("java.lang.String");

        s.append(Modifier.toString(userServiceClass.getModifiers())+" class "+userServiceClass.getSimpleName()+"\n");

        Method[] methods = userServiceClass.getDeclaredMethods();
        for (Method method : methods) {
            s.append("\t");
            s.append(Modifier.toString(method.getModifiers()));
            s.append(" ");
            s.append(method.getReturnType().getSimpleName());
            s.append(" ");
            s.append(method.getName());
            s.append("(");
            Class[] parameterTypes = method.getParameterTypes();
            for (Class parameterType : parameterTypes) {
                s.append(parameterType.getSimpleName());
                s.append(",");
            }
            if (parameterTypes.length>0){
                s.deleteCharAt(s.length()-1);
            }

            s.append("){}\n");
        }
        s.append("}");
        System.out.println(s);


    }
}
```

```java
import Reflect.bean.UserService;

import java.lang.reflect.Method;

/**
 * 重点：必须掌握，通过反射机制怎么调用一个对象的方法？
 * 五颗星⭐⭐⭐⭐⭐
 *
 * 反射机制，让代码很具有通用性，可变化的内容都是写到配置文件当中，
 * 将来修改配置文件之后，创建的对象不一样了，调用的方法也不同了，
 * 但是java代码不需要做任何改动。这就是反射机制的魅力。
 */
public class ReflectTest10 {
    public static void main(String[] args) throws Exception{
        //不使用反射机制，怎么调用方法
        //创建对象
        UserService userService = new UserService();
        //调用方法
        /*
        要素分析：
            要素1：对象userService
            要素2：login方法名
            要素3：实参列表
            要素4：返回值
         */
        boolean loginSucess = userService.login("admin","123");
        System.out.println(loginSucess?"登录成功":"登陆失败");

        //使用反射机制来调用一个对象的方法该怎么做？
        Class userServiceClass = Class.forName("Reflect.bean.UserService");
        //创建对象
        Object object =  userServiceClass.newInstance();
        //java中怎么区分一个方法，方法名和参数列表
        //获取Method
        Method loginMethod = userServiceClass.getDeclaredMethod("login", String.class, String.class);
        //userServiceClass.getDeclaredMethod("login", int.class);
        //调用方法
        //调用方法有几个要素：也需要4要素
        //invoke调用
        //反射机制中最最最最最重要的一个方法，必须记住。
        /*四要素:
           loginMethod方法obj对象
           "admin " , "123”实参
           retValue 返回值
           */

        Object retValue=loginMethod.invoke(object,"admin","123");
        System.out.println(retValue);
    }
}
```

```java
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/*
反编译一个类的constructor构造方法
 */
public class ReflectTest11 {
    public static void main(String[] args) throws Exception{
        StringBuilder s = new StringBuilder();
        Class vipClass = Class.forName("java.lang.String");
        s.append(Modifier.toString(vipClass.getModifiers()));
        s.append(" class ");
        s.append(vipClass.getSimpleName());
        s.append("{\n");

        //拼接构造方法
        Constructor[] constructors = vipClass.getDeclaredConstructors();
        for (Constructor constructor : constructors) {
            s.append("\t");
            s.append(Modifier.toString(constructor.getModifiers()));
            s.append(" ");
            s.append(vipClass.getSimpleName());
            s.append("(");
            //拼接参数
            Class[] parameterTypes = constructor.getParameterTypes();
            for (Class parameterType : parameterTypes) {
                s.append(parameterType.getSimpleName());
                s.append(",");

            }
            if (parameterTypes.length>0){
                s.deleteCharAt(s.length()-1);
            }
            s.append("){}\n");
        }

        s.append("}");
        System.out.println(s);
    }
}
```

```java
public class Vip {
    int no;
    String name;
    String birth;
    boolean sex;

    public Vip() {
    }

    public Vip(int no, String name, String birth, boolean sex) {
        this.no = no;
        this.name = name;
        this.birth = birth;
        this.sex = sex;
    }

    public Vip(int no, String name, String birth) {
        this.no = no;
        this.name = name;
        this.birth = birth;
    }

    public Vip(int no, String name) {
        this.no = no;
        this.name = name;
    }

    public Vip(int no) {
        this.no = no;
    }

    @Override
    public String toString() {
        return "Vip{" +
                "no=" + no +
                ", name='" + name + '\'' +
                ", birth='" + birth + '\'' +
                ", sex=" + sex +
                '}';
    }
}
```

```java
import Reflect.bean.Vip;

import java.lang.reflect.Constructor;

/**
 *
 * 通过反射机制调用构造方法实例化java对象。（这个不是重点)
 */
public class ReflectTest12 {
    public static void main(String[] args) throws Exception {
        //不使用反射机制怎么创建对象
        Vip v1 = new Vip();
        Vip v2 = new Vip(110,"zhangsan","2002-01-01",true);

        //使用反射机制怎么创建对象呢？
        Class c = Class.forName("Reflect.bean.Vip");
        //调用无参数构造方法
        Object object =c.newInstance();
        System.out.println(object);
        //调用有参数的构造方法
        //第一步：先获取刀这个有参数的构造方法
        Constructor constructor = c.getDeclaredConstructor(int.class,String.class,String.class,boolean.class);
        //第二步：调用构造方法new对象
        Object object1 =constructor.newInstance(110,"tom","2000-12-11",true);
        System.out.println(object1);

        //获取无参数构造方法
        Constructor constructor1 = c.getDeclaredConstructor();
        Object object2 =constructor1.newInstance();
        System.out.println(object2);
    }
}
```

```java
/*
重点:给你一个类，怎么获取这个类的父类，已经实现了哪些接口?
 */
public class ReflectTest13 {
    public static void main(String[] args) throws Exception {
        //String举例
        Class stringClass = Class.forName("java.lang.String");

        //获取String的父类
        Class superclass = stringClass.getSuperclass();
        System.out.println(superclass.getName());

        //获取String类实现的所有接口(一个类可以实现多个接口。)
        Class[] interfaces = stringClass.getInterfaces();
        for (Class anInterface : interfaces) {
            System.out.println(anInterface.getName());
        }
    }
}
```

2、今日反射机制的重点内容
	2.1、通过反射机制访问对象的某个属性。
	2.2、通过反射机制调用对象的某个方法。
	2.3、通过反射机制调用某个构造方法实例化对象。
	2.4、通过反射机制获取父类以及父类型接口。

```java
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ResourceBundle;

public class Test40 {
    public static void main(String[] args) {
        //配置文件获取类名
        ResourceBundle resourceBundle = ResourceBundle.getBundle("classinfo");
        String className = resourceBundle.getString("className");
        try {
            //获取类
            Class c = Class.forName(className);

            //获取父类
            Class superclass = c.getSuperclass();
            System.out.println("父类: "+superclass.getName());

            //获取接口
            Class[] interfaces = c.getInterfaces();
            int i =1;
            for (Class anInterface : interfaces) {
                System.out.println("接口"+i+++": "+anInterface.getSimpleName());
            }

            //获取属性
            Field[] declaredFields = c.getDeclaredFields();
            StringBuilder s = new StringBuilder();
            for (Field declaredField : declaredFields) {
                //获取访问修饰符
                s.append(Modifier.toString(declaredField.getModifiers()));
                s.append(" ");
                //获取类型
                s.append(declaredField.getType().getSimpleName());
                s.append(" ");
                //获取属性名字
                s.append(declaredField.getName());
                s.append(";\n");
            }
            System.out.println(s);

            //获取构造方法
            Constructor[] constructors = c.getConstructors();
            StringBuilder s1 = new StringBuilder();
            for (Constructor constructor : constructors) {
                s1.append(Modifier.toString(constructor.getModifiers()));
                s1.append(" ");
                s1.append(c.getSimpleName());
                s1.append("(");
                //获取参数列表
                Class[] parameterTypes = constructor.getParameterTypes();
                for (Class parameterType : parameterTypes) {
                    s1.append(parameterType.getSimpleName());
                    s1.append(",");
                }
                if (constructor.getParameterCount()>0){
                    s1.deleteCharAt(s1.length()-1);
                }
                s1.append("){}\n");

            }
            System.out.println(s1);

            //获取方法
            Method[] declaredMethods = c.getDeclaredMethods();
            StringBuilder s2 = new StringBuilder();
            for (Method declaredMethod : declaredMethods) {
                //获取访问修饰符
                s2.append(Modifier.toString(declaredMethod.getModifiers()));
                s2.append(" ");
                //获取返回值类型
                s2.append(declaredMethod.getReturnType().getSimpleName());
                s2.append(" ");
                //获取方法名
                s2.append(declaredMethod.getName());
                s2.append("(");
                //获取参数列表
                Class[] parameterTypes = declaredMethod.getParameterTypes();
                for (Class parameterType : parameterTypes) {
                    s2.append(parameterType.getSimpleName());
                    s2.append(",");
                }
                if (declaredMethod.getParameterCount()>0){
                    s2.deleteCharAt(s2.length()-1);
                }
                s2.append("){}\n");
            }
            System.out.println(s2);




        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
}
```

```java
public class User {
    private String name;
    private  String password;
    private int no;
    public boolean sex;

    public User() {
        System.out.println("无参构造方法");
    }

    public User(String s) {
        System.out.println("???");
    }

    public User(String name, int no, boolean sex) {
        this.name = name;
        this.no = no;
        this.sex = sex;
    }
    public void login(String name ,String password){
        if (name==this.name&&password==this.password){
            System.out.println("sucess");
        }else {
            System.out.println("failure");
        }
    }
}
```

```java
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ResourceBundle;

public class Test41 {
    public static void main(String[] args) {
        //配置文件获取类名
        ResourceBundle resourceBundle = ResourceBundle.getBundle("classinfo");
        String name = resourceBundle.getString("use");
        try {
            //加载类
            Class c = Class.forName(name);
            //获取对象
            Object o = c.newInstance();

            //获取属性
            Field field = c.getDeclaredField("name");
            //打破封装
            field.setAccessible(true);
            //设置值
            field.set(o, "admin");
            //获取值
            System.out.println(field.get(o));

            Field field2 = c.getDeclaredField("password");
            field2.setAccessible(true);
            field2.set(o, "123");
            System.out.println(field2.get(o));

            //获取方法(方法名和参数列表)
            Method method = c.getDeclaredMethod("login", String.class,String.class);
            method.setAccessible(true);
            //调用方法
            method.invoke(o, field.get(o),field2.get(o));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
```