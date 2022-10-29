## <font color=FF0000 >Lambda表达式</font>

```java
/**
 * Lambda表达式的使用
 * <p>
 * 1.举例 (o1,o2) -> Integer.copare(o1,o2);
 *
 * 2.格式:
 * -> : Lambda操作符or箭头操作符
 * ->左边: Lambda形参列表(其实就是接口中的抽象方法的形参列表)
 * ->右边: Lambda体(其实就是重写的抽象方法的方法体)
 *
 *
 * 3.Lambda表达式的使用:(分为6种情况介绍)
 *
 *  总结：
 *  ->左边: Lambda形参列表的参数类型可以省略(类型推断);如果Lambda形参列表只有一个参数，其一对()也可以省略
 *  ->右边: Lambda体应该使用一对包裹:如果Lambda体只有一条执行语句(可能是return语句),可以省略这一对{}和return关键字
 *
 * 4.Lambda表达式的本质:作为函数式接口的实例
 *
 * 5.如果一个接口中，只声明了一个抽象方法，则此接口就称为函数式接口
 * 我们可以在一个接口上使用@FunctionalInterface注解，这样做可以检查它是否是一个函数式接口
 *
 * 6.所以以前用匿名实现类表示的，现在都可以用Lambda表达式来写
 */
```

```java
 //语法格式一：无参，无返回值
    @Test
    public void test1() {

        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("身无彩凤双飞翼");
            }
        };
        r1.run();
        System.out.println("------------------");

        Runnable r2 = () -> System.out.println("心有灵犀一点通");
        r2.run();
    }
     //语法格式二：Lambda需要一个参数，但是没有返回值
    @Test
    public void test2() {
        Consumer<String> consumer = new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(s);
            }
        };
        consumer.accept("噓の噓は真実");
        System.out.println("--------------");

        Consumer<String> consumer2 = (String s) -> {
            System.out.println(s);
        };
        consumer2.accept("寄蜉蝣于天地");
    }
      //语法格式三：数据类型可以省略，因为可以由编译器推断得出，称为"类型推断"

    @Test
    public void test3() {
        Consumer<String> consumer = (String s) ->{
            System.out.println(s);
        };
        consumer.accept("沧海月明珠有泪");

        System.out.println("--------------");

        Consumer<String> consumer2 = (s) -> {
            System.out.println(s);
        };
        consumer2.accept("蓝田玉暖日升烟");
    }
    @Test
    public void test4(){
        ArrayList<String> list = new ArrayList<>();//类型推断
        int[] nums = new int[]{1,2,3};
        int[] num = {1,2,3};//类型推断

    }
     //语法格式四:Lambda若只需要一个参数时，参数的小括号可以省略
    @Test
    public void test5() {
        Consumer<String> consumer = (String s) ->{
            System.out.println(s);
        };
        consumer.accept("沧海月明珠有泪");

        System.out.println("--------------");

        Consumer<String> consumer2 = s -> {
            System.out.println(s);
        };
        consumer2.accept("蓝田玉暖日升眼");
    }
      //语法格式五：Lambda需要两个或以上的参数，多条执行语句，并且可以有返回值
    @Test
    public void test6(){
        Comparator<Integer> comparator1 = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                System.out.println(o1);
                System.out.println(o2);
                return o1.compareTo(o2);
            }
        };
        System.out.println(comparator1.compare(12, 6));
        System.out.println("-----------------------");
        Comparator<Integer> comparator2 = (o1,o2) ->  {
            System.out.println(o1);
            System.out.println(o2);
            return o1.compareTo(o2);
        };
        System.out.println(comparator2.compare(6, 12));
    }
    
    //语法格式六：当Lambda体只有一条语句时，return与大括号若有，都可以省略
    @Test
    public void test7(){
        Comparator<Integer> comparator1 =(o1, o2) -> o1.compareTo(o2);

        System.out.println(comparator1.compare(12,6));
    }

    @Test
    public void test8(){
        Consumer<String> consumer1 = s -> {
            System.out.println(s);
        };
        consumer1.accept("落红不是无情物");

        System.out.println("---------------------");

        Consumer consumer2 = s -> System.out.println(s);

        consumer2.accept("化作春泥更护花");
    }
    
    
    
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * java内置的4大核心函数式接口
 *
 * 消费型接口Consumer<T>     void accept(T t)
 * 供给型接口Supplier<T>     T get()
 * 函数型接口Function<T,R>   R apply(T t)
 * 断定型接口Predicate<T>    booLean test(T t)
 *
 *
 */
public class LambdaTest07 {

    @Test
    public void  test1(){
        happyTime(10, (money)-> System.out.println("矿泉水："+money));
    }

    public void  happyTime(double money, Consumer<Double> consumer){
        consumer.accept(money);
    }

    @Test
    public void test2(){
List<String > list = Arrays.asList("东京","南京","北京","天津","普京");
        List<String> 京 = filterString(list, s -> s.contains("京"));
        System.out.println(京);

    }
    //根据给定的规则，过滤集合中的字符串。此规则由Predicate的方法决定
    public  List<String> filterString(List<String> list, Predicate<String> predicate){
        ArrayList<String> filterList = new ArrayList<>();
        for (String s : list) {
            if (predicate.test(s)){
                filterList.add(s);
            }
        }
        return filterList;
    }
}

```

## <font color=FF0000>方法引用</font>

```java

/**
 * 方法引用的使用
 * <p>
 * 1.使用情境：当要传递给Lambda体的操作，已经有实现的方法了，可以使用方法引用！
 * <p>
 * 2.方法引用，本质上就是Lambda表达式，而Lambda表达式作为函数式接口的实例。所以
 * 方法引用，也是函数式接口的实例。
 * <p>
 * 3. 使用格式：  类(或对象) :: 方法名
 * <p>
 * 4. 具体分为如下的三种情况：
 * 情况1     对象 :: 非静态方法
 * 情况2     类 :: 静态方法
 * <p>
 * 情况3     类 :: 非静态方法
 * <p>
 * 5. 方法引用使用的要求：要求接口中的抽象方法的形参列表和返回值类型与方法引用的方法的
 * 形参列表和返回值类型相同！（针对于情况1和情况2）
 * <p>
 * Created by shkstart.
 */
public class MethodRefTest {

    // 情况一：对象 :: 实例方法
    //Consumer中的void accept(T t)
    //PrintStream中的void println(T t)
    @Test
    public void test1() {
        Consumer<String> con1 = str -> System.out.println(str);
        con1.accept("朝辞白帝彩云间");

        System.out.println("-------------------");

        PrintStream ps = System.out;
        Consumer<String> con2 = ps::println;
        con2.accept("孤帆一片日边来");
    }

    //Supplier中的T get()
    //Employee中的String getName()
    @Test
    public void test2() {
        Employee emp = new Employee(1001, "Tom", 23, 5600);

        Supplier<String> sup1 = () -> emp.getName();
        System.out.println(sup1.get());

        System.out.println("*******************");
        Supplier<String> sup2 = emp::getName;
        System.out.println(sup2.get());

    }

    // 情况二：类 :: 静态方法
    //Comparator中的int compare(T t1,T t2)
    //Integer中的int compare(T t1,T t2)
    @Test
    public void test3() {
        Comparator<Integer> com1 = (t1, t2) -> Integer.compare(t1, t2);
        System.out.println(com1.compare(12, 21));

        System.out.println("*******************");

        Comparator<Integer> com2 = Integer::compare;
        System.out.println(com2.compare(12, 3));

    }

    //Function中的R apply(T t)
    //Math中的Long round(Double d)
    @Test
    public void test4() {
        Function<Double, Long> func = new Function<Double, Long>() {
            @Override
            public Long apply(Double d) {
                return Math.round(d);
            }
        };

        System.out.println("*******************");

        Function<Double, Long> func1 = d -> Math.round(d);
        System.out.println(func1.apply(12.3));

        System.out.println("*******************");

        Function<Double, Long> func2 = Math::round;
        System.out.println(func2.apply(12.6));
    }

    // 情况三：类 :: 实例方法  (有难度)
    // Comparator中的int comapre(T t1,T t2)
    // String中的int t1.compareTo(t2)
    @Test
    public void test5() {
        Comparator<String> com1 = (s1, s2) -> s1.compareTo(s2);
        System.out.println(com1.compare("abc", "abd"));

        System.out.println("-----------------------");

        Comparator<String> com2 = String::compareTo;
        System.out.println(com2.compare("abd", "abm"));
    }

    //BiPredicate中的boolean test(T t1, T t2);
    //String中的boolean t1.equals(t2)
    @Test
    public void test6() {
        BiPredicate<String, String> pre1 = (s1, s2) -> s1.equals(s2);
        System.out.println(pre1.test("abc", "abc"));

        System.out.println("*******************");
        BiPredicate<String, String> pre2 = String::equals;
        System.out.println(pre2.test("abc", "abd"));
    }

    // Function中的R apply(T t)
    // Employee中的String getName();
    @Test
    public void test7() {
        Employee employee = new Employee(1001, "Jerry", 23, 6000);


        Function<Employee, String> func1 = e -> e.getName();
        System.out.println(func1.apply(employee));

        System.out.println("*******************");


        Function<Employee, String> func2 = Employee::getName;
        System.out.println(func2.apply(employee));


    }

}

```

```java

/**
 * 一、构造器引用
 *      和方法引用类似，函数式接口的抽象方法的形参列表和构造器的形参列表一致。
 *      抽象方法的返回值类型即为构造器所属的类的类型
 *
 * 二、数组引用
 *     大家可以把数组看做是一个特殊的类，则写法与构造器引用一致。
 *
 * Created by shkstart
 */
public class ConstructorRefTest {
	//构造器引用
    //Supplier中的T get()
    //Employee的空参构造器：Employee()
    @Test
    public void test1(){

        Supplier<Employee> sup = new Supplier<Employee>() {
            @Override
            public Employee get() {
                return new Employee();
            }
        };
        System.out.println("*******************");

        Supplier<Employee>  sup1 = () -> new Employee();
        System.out.println(sup1.get());

        System.out.println("*******************");

        Supplier<Employee>  sup2 = Employee :: new;
        System.out.println(sup2.get());
    }

	//Function中的R apply(T t)
    @Test
    public void test2(){
        Function<Integer,Employee> func1 = id -> new Employee(id);
        Employee employee = func1.apply(1001);
        System.out.println(employee);

        System.out.println("*******************");

        Function<Integer,Employee> func2 = Employee :: new;
        Employee employee1 = func2.apply(1002);
        System.out.println(employee1);

    }

	//BiFunction中的R apply(T t,U u)
    @Test
    public void test3(){
        BiFunction<Integer,String,Employee> func1 = (id,name) -> new Employee(id,name);
        System.out.println(func1.apply(1001,"Tom"));

        System.out.println("*******************");

        BiFunction<Integer,String,Employee> func2 = Employee :: new;
        System.out.println(func2.apply(1002,"Tom"));

    }

	//数组引用
    //Function中的R apply(T t)
    @Test
    public void test4(){
        Function<Integer,String[]> func1 = length -> new String[length];
        String[] arr1 = func1.apply(5);
        System.out.println(Arrays.toString(arr1));

        System.out.println("*******************");

        Function<Integer,String[]> func2 = String[] :: new;
        String[] arr2 = func2.apply(10);
        System.out.println(Arrays.toString(arr2));

    }
}


```

```java
public class Employee {

	private int id;
	private String name;
	private int age;
	private double salary;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public Employee() {
		System.out.println("Employee().....");
	}

	public Employee(int id) {
		this.id = id;
		System.out.println("Employee(int id).....");
	}

	public Employee(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public Employee(int id, String name, int age, double salary) {

		this.id = id;
		this.name = name;
		this.age = age;
		this.salary = salary;
	}

	@Override
	public String toString() {
		return "Employee{" + "id=" + id + ", name='" + name + '\'' + ", age=" + age + ", salary=" + salary + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Employee employee = (Employee) o;

		if (id != employee.id)
			return false;
		if (age != employee.age)
			return false;
		if (Double.compare(employee.salary, salary) != 0)
			return false;
		return name != null ? name.equals(employee.name) : employee.name == null;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = id;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + age;
		temp = Double.doubleToLongBits(salary);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
}


```
```java
public class EmployeeData {
	
	public static List<Employee> getEmployees(){
		List<Employee> list = new ArrayList<>();
		
		list.add(new Employee(1001, "马化腾", 34, 6000.38));
		list.add(new Employee(1002, "马云", 12, 9876.12));
		list.add(new Employee(1003, "刘强东", 33, 3000.82));
		list.add(new Employee(1004, "雷军", 26, 7657.37));
		list.add(new Employee(1005, "李彦宏", 65, 5555.32));
		list.add(new Employee(1006, "比尔盖茨", 42, 9500.43));
		list.add(new Employee(1007, "任正非", 26, 4333.32));
		list.add(new Employee(1008, "扎克伯格", 35, 2500.32));
		
		return list;
	}
	
}

```


