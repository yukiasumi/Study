### <font color = FF0000>jdbc:mysql://localhost:3306/ranxue?useSSL=false", "root", "root"</font>

JDBC封装分层目录

![image-20220124094929039](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220124094929039.png)

```
 dao （代码分层）
    com.aaa.dao       存放dao相关的类型 例如  StudentDAOImpl  处理 数据库的链接 存取数据
    com.aaa.servlet   存放servlet相关的类 例如：StudentServlet 处理 与浏览器交互的类
    com.aaa.entity    存放实体类    例如 Student    接受数据库对象模型
    com.aaa.util      存放工具类    例如  DBUtil
```

```
模拟 servlet调用 dao
    2.1 创建一个数据库表 Student( id name age )
    2.2 创建数据库表对应的实体类 （为什么要创建实体类？作用：以后数据库表中的数据 提取到 java中的时候 用 对象来存储）
    2.3 创建DAO
        A 组成 两部分： 接口 （ 声明 ）和 实现类
          接口的命名规则例如： IStudentDAO     I 代表这是一个接口 Student 对应业务名称（表名） DAO 后缀 表明当前是一个dao接口
          实现类的命名规则例如： StudentDAOImpl    Student 代表业务名称(表明) DAOImpl 代表dao接口的实现
          为什么需要接口+实现类？ 因为 我们需要一个多态的特征。
          ---------------------多态------------------------
          什么是多态？ 多种状态
          如何产生多态？ 继承多态     父类的引用 子类的对象 例如 ：  Animal a = new Dog();    基本上不用
                         接口多态     接口的应用  实现类的对象  例如： IStudentDAO  dao = new StudentDAOImpl();
          为什么要使用多态？  程序解耦  解除程序的耦合性
          （不使用多态会产生什么问题？）
          ------------------------------------------------
        B 接口声明的方法(接口中该写哪些方法)
          重要：根据业务需求   /     CRUD 增删改查
     2.4 接口中 声明 五个方法    （一个方法：返回值  参数   试想一下 这个业务的sql ）
         A 添加新学生
         B 根据id删除学生
         C 根据id修改学生
         D 根据id查询学生
         E 查询所有学生
     2.5 对方法完成实现
     2.6 测试代码
```

```
jdbc封装
    我们发现 在dao的实现类中 有很多重复代码 我们可以将其封装起来
    3.1 创建一个类 DBUtil
    3.2 加载驱动和建立链接的代码 完全一样
        加載驅動写到静态代码快中 ：因为 驱动只需要加载一次即可 比如：你安装了一台电脑  只需要下载一次qq 以后直接使用就可以了
                                   类的静态代码块 随着类的加载 只执行一次
        建立链接的代码 单独创建一个方法 通过返回值返回链接对象：为什么链接要链接多次 因为链接用完了就关闭了
    3.3 关闭的代码也需要封装
    3.4 增删改的预处理代码 也基本一样
```

JDBC封装

* 创建实体类Student【定义字段名，Setter Getter方法，构造方法，重写toString方法】
* 创建IStudent接口【增删改查方法】
* 创建StudentImpl接口实现类【返回为String的Sql语句】
* 创建工具类DBUtil【连接数据库，增删改(返回布尔值)，查询(返回List集合，泛型为Map<String,Object>】
* 创建测试类Test01【创建学生对象，使用DBUtil执行SQL语句(可通过学生对象完善SQL语句)】

### <font color = FF0000>getColumnName只能查询到字段名，getColumnLabel可以查询到别名</font>

```java
public class Student {
    private int id;
    private String name;
    private int age;

    public Student() {
    }

    public Student(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

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

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
```

```java
public interface IStudent {
    //添加新学生
    String add(int id ,String name ,int age);

    // 根据id删除学生
    String delete(int id);

    //根据id修改学生
    String update(int id,String name,int age);

    //根据id查询学生
    String selectId(int id);

    //查询所有学生
    String selectAll();
}
```

```java
import edu.ranther.dao.IStudent;

public class StudentImpl implements IStudent {

    @Override
    public String add(int id, String name, int age) {
        return "insert into student(id,name,age)values(" + id + ",'" + name + "'," + age + ")";
    }

    @Override
    public String delete(int id) {
        return "delete from student where id = " + id;
    }

    @Override
    public String update(int id, String name, int age) {
        return "update student set name = '" + name + "',age=" + age + " where id = " + id;
    }

    @Override
    public String selectId(int id) {
        return "select * from student where id =" + id;
    }

    @Override
    public String selectAll() {
        return "select * from student";
    }
}
```

```java
import java.sql.*;
import java.util.*;

public class DBUtil {
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //连接数据库
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/ranxue?useSSL=false", "root", "root");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //增删改
    public static boolean executeADU(String sql) {
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            return preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    //    查询学生
    public static List<Map<String, Object>> excuteSelect(String sql) {
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            //获取字段数
            int count = resultSet.getMetaData().getColumnCount();
            List<Map<String, Object>> list = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> map = new HashMap<>();
                for (int i = 1; i <=count; i++) {
                    //获取字段名
                   String name =  resultSet.getMetaData().getColumnLabel(i);
                    map.put(name,resultSet.getObject(i));
                }
                list.add(map);
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}

```

```java
import edu.ranther.dao.IStudent;
import edu.ranther.dao.daoImpl.StudentImpl;
import edu.ranther.entity.Student;
import edu.ranther.util.DBUtil;

import javax.sound.midi.Soundbank;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Test01 {

    public static void main(String[] args) {
        Student student = new Student();
        student.setId(2);
        student.setName("dog");
        student.setAge(19);
        IStudent iStudent = new StudentImpl();
       //添加
        DBUtil.executeADU(iStudent.add(student.getId(), student.getName(), student.getAge()));
        //更改
//        DBUtil.executeADU(iStudent.update(student.getId(), "Panda", 20));
        //删除数据
//        DBUtil.executeADU(iStudent.delete(12));
        //查询
        List<Map<String, Object>> list = DBUtil.excuteSelect(iStudent.selectAll());
        Iterator iterator = list.iterator();
        for (Map<String, Object> map : list) {
            System.out.println(map.toString());
        }
    }
}
```
### <font color = FF0000>?占位符赋值</font>
```java
//获取预处理块
preparedStatement = connection.prepareStatement("select * from  student where name = ? and age = ?");
//给?赋值
preparedStatement.setString(1,"Panda");
preparedStatement.setString(2,"20");
//执行SQL语句
resultSet = preparedStatement.executeQuery();
```
### <font color = FF000>IO+JDBC</font>
```java

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test26 {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = null;
            FileOutputStream fileOutputStream = null;
            FileInputStream fileInputStream = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ranxue?useSSL=false", "root", "root");
                //获取预处理块
                preparedStatement = connection.prepareStatement("select * from  student where name = ? and age = ?");
                //给?赋值
                preparedStatement.setString(1, "Panda");
                preparedStatement.setString(2, "20");
                //执行SQL语句
                resultSet = preparedStatement.executeQuery();
                List<Map<String, Object>> list = new ArrayList<>();
                //存放数据
                while (resultSet.next()) {
                    //map必须在循环里定义
                    Map<String, Object> map = new HashMap<>();
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        map.put(resultSet.getMetaData().getColumnLabel(i), resultSet.getObject(resultSet.getMetaData().getColumnLabel(i)));
                    }
                    list.add(map);
                }
                try {
                    fileOutputStream = new FileOutputStream("log/a.txt");
                    fileInputStream = new FileInputStream("log/a.txt");
                    byte[] bytes = new byte[16];
                    //写入文件
                    for (Map<String, Object> map : list) {
                        try {
                            fileOutputStream.write(map.toString().getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //读取文件
                    int readCount = 0;
                    while (true) {
                        try {
                            if (!((readCount = fileInputStream.read(bytes)) != -1))
                                break;
                            System.out.println(new String(bytes, 0, readCount));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

```

![image-20220206115430004](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220206115430004.png)

修改数据库的字符集：alter database dbname character set utf8;

修改表的字符集：alter table tablename convert to character set utf8;