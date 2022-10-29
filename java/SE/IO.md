## 输入流(读)
```java
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Test21 {
    public static void main(String[] args) {
        //创建对象
        FileInputStream fileInputStream = null;
        try {
            //指定路径
            fileInputStream = new FileInputStream("C:\\Users\\hakuou\\Documents\\program\\cmd命令.txt");
            //创建byte数组
            byte[] bytes = new byte[4];
            //定义读取到的数量
            int readCount = 0;
            //读取并输出
            while ((readCount = fileInputStream.read(bytes)) != -1) {
                //将byte数组转化为字符串输出，
                System.out.println(new String(bytes, 0, readCount));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭流
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

```
```java
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Test22 {
    public static void main(String[] args) {
        FileInputStream fileInputStream = null;
        try {
            //创建文件路径
            fileInputStream = new FileInputStream("C:\\Users\\hakuou\\Documents\\program\\pycharm.txt");
            //创建byte数组，数组长度为剩余可读的字节长度(不能太大，百万以下没问题)
            byte[] bytes = new byte[fileInputStream.available()];
            //读取内容
            fileInputStream.read(bytes);
            //byte数组转字符串
            System.out.println(new String(bytes));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```
## 输出流(写)

```java

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Test23 {
    public static void main(String[] args) {
        FileOutputStream fileOutputStream = null;
        //循环写入
        for (int i = 0 ;i<100;i++){
            try {
                //创建文件路径
                fileOutputStream = new FileOutputStream("I:\\a.txt", true);
                //日期转字符串
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
                Date date = calendar.getTime();
                String s = simpleDateFormat.format(date)+"\r";
                //字符串转byte数组
                byte[] bytes = s.getBytes();
                //写入
               //将byte数组的一部分写出
//              fileOutputStream.write(bytes,0,2);
                fileOutputStream.write(bytes);
                //flush
                fileOutputStream.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //关闭流
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
```

## 文件复制
```java

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Test25 {
    public static void main(String[] args) {
        FileInputStream fileInputStream =null;
        FileOutputStream fileOutputStream = null;
        try {
            //创建要读取的文件路径
            fileInputStream = new FileInputStream("H:\\图片\\Twitter\\FJYdCDlakAMwZTE.jfif");
            //创建要复制到的目标文件路径
            fileOutputStream = new FileOutputStream("H:\\壁纸\\草莓.png");
            //创建byte数组，一次只能1M
            byte[] bytes = new byte[1024*1024];
            int readCount = 0;
            //边读取边写入
            while ((readCount = fileInputStream.read(bytes))!=-1){
                fileOutputStream.write(bytes,0,readCount);
            }
            //写入流都要刷新
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //关闭流
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```
