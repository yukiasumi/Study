## 一个线程一个栈，共享堆内存和方法区内存

实现线程的方式
<font color=33FF33 size = 5>1. 编写一个类，直接继承java.lang.Thread，重写run方法</font>
<font color=33FF33 size = 5>public class MyThread extends Thread{</font>
<font color=33FF33 size = 5>public void run(){</font>

<font color=33FF33 size = 5>}</font>
<font color=33FF33 size = 5>}</font>
<font color=33FF33 size = 5>//创建线程对象</font>
<font color=33FF33 size = 5>MyThread t = new MyThread();</font>
<font color=33FF33 size = 5>//启动线程</font>
<font color=33FF33 size = 5>t.start();</font>

```java
/**
 * 实现线程的第一种方式
 * 编写一个类，直接继承java.lang.Thread，重写run方法
 */
public class ThreadTest04 {
    public static void main(String[] args) {
        //这里是main方法，这里的代码术语主线程，在主线程中运行
        //新建一个分支线程对象
        MyThread myThread = new MyThread();
        //启动线程
       //start()方法的作用是:启动一个分支线程，在JVN中开辟一个新的栈空间，这段代码任务完成之后，瞬间就结束了
       //这段代码的任务只是为了开启一个新的栈空间，只要新的栈空间开出来，start()方法就结束了。线程就启动成功了。
        //启动成功的线程会自动调用run()方法，并且run方法在分支栈的栈底部(压栈)。
        //run方法在分支栈的栈底部，main()方法在主栈的栈底部。run和main是平级的。
        myThread.start();
        //这里的代码还是运行在主线程中
        for (int i = 0; i < 1000; i++) {
            System.out.println("主线程---->"+i);

        }
    }

}
class MyThread extends Thread{
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            System.out.println("分支线程--->"+i);

        }
    }
}
```
<font color=FF0000 size = 5>2. 编写一个类，实现java.lang.Runnable接口，实现run方法。</font>

<font color =FF0000 size = 5>第二种方式，除了实现run方法外还能继承其他类而第一种不行，面向接口编程</font>

<font color=FF0000 size = 5>//定义一个可运行的类</font>

<font color=FF0000 size = 5>public class MyRunnable implements Runnable{</font>

<font color=FF0000 size = 5>public void run(){</font>

<font color=FF0000 size = 5>}</font>

<font color=FF0000 size = 5>}</font>

<font color=FF0000 size = 5>//创建线程对象</font>

<font color=FF0000 size = 5>Thread t = new Thread(new MyRunnable());</font>

<font color=FF0000 size = 5>//启动线程</font>

<font color=FF0000 size = 5>t.start();</font>

```java
/**
 * 实现线程的第二种方式
 * 编写一个类，实现java.lang.Runnable接口，实现run方法。
 */
public class ThreadTest05 {
    public static void main(String[] args) {
        //创建一个可运行的对象
        MyRunnable runnable = new MyRunnable();
        //将可运行的对象封装成一个线程对象
        Thread thread = new Thread(runnable);
        //启动线程
        thread.start();

        for (int i = 0; i < 100; i++) {
            System.out.println("主线程----"+i);;

        }
    }


}
class  MyRunnable implements Runnable{

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            System.out.println("分支线程----"+i);;

        }

    }
}
```

```java
public static void main(String[] args) {
    //匿名内部类实现
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                System.out.println("分支线程----"+i);

            }
        }
    });
    thread.start();
    for (int i = 0; i < 100; i++) {
        System.out.println("主线程----"+i);

    }
}
```

### 关于线程对象 的生命周期？
<font color =FF0000 size =4>* 新建状态</font>
<font color =FF0000 size = 4>* 就绪状态</font>
<font color =FF0000 size = 4>* 运行状态</font>
<font color =FF0000 size =4>* 阻塞状态</font>
<font color =FF0000 size = 4>* 死亡状态</font>
![image-20220126151050200](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220126151050200.png)

![image-20220127104048133](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220127104048133.png)


```java
/**
 * 1、怎么获取当前线程对象?
 *  Thread t = Thread.currentThread();
 *
 * 2、获取线程对象的名字?
 *  String name = 线程对象.getName();
 *
 * 3、修改线程对象的名字？
 * 线程对象.setName("线程名字");
 *
 * 4.当线程没有设置名字的时候，默认的名字有什么规律?
 * Thread-0
 * Thread-1
 * Thread-2
 * Thread-3
 * ···
 */
public class Thread06 {
    public static void main(String[] args) {
        //currentThread就是当前线程对象
        //这个代码出现在main方法当中，所以当前线程就是主线程。
        Thread currentThread = Thread.currentThread();
        System.out.println(currentThread.getName());

        //创建线程对象
        MyThread2 t = new MyThread2();

        //设置线程的名字
       t.setName("t1");

        //获取线程的名字
        System.out.println(t.getName());//默认Thread-0
        
        MyThread2 t2 = new MyThread2();
        t2.setName("t2");
        System.out.println(t2.getName());//Thread-1

        t2.start();
        //启动线程
         t.start();
    }

}
class MyThread2 extends Thread{
    public void run(){
        for (int i = 0; i < 100; i++) {
            //currentThread就是当前线程对象。当前线程是谁呢?
            // 当t1线程执行run方法，那么这个当前线程就是t1
            //当t2线程执行run方法，那么这个当前线程就是t2
            Thread currentThread = Thread.currentThread();
            System.out.println(currentThread.getName()+"---->"+i);

        }
    }
}
```

```java
/**
 * 关于线程的sleep方法:
 * static void sleep(Long millis)
 * 1、静态方法:Thread.sleep(1000);
 * 2、参数是毫秒
 * 3、作用:让当前线程进入休眠，进入"阻塞状态"，放弃占有CPU时间片，让给其它线程使用。
 * 这行代码出现在A线程中,A线程就会进入休眠。
 * 这行代码出现在B线程中,B线程就会进入休眠。
 * 4、Thread .sleep( )方法，可以做到这种效果:
 * 间隔特定的时间，去执行一段特定的代码，每隔多久执行一次。
 */
public class Thread07 {
    public static void main(String[] args) {
        //让当前线程进入休眠，睡眠5秒
        //当前线程是主线程
        /*try {
            Thread.sleep(1000 * 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //5秒后执行这里的代码
        System.out.println("hello world");*/

        for (int i = 0; i < 10; i++) {
            System.out.println(Thread.currentThread().getName());

            //睡眠一秒
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

}
```

```java
/**
 * 关于Thread.sleep()方法的一个面试题
 */
public class ThreadTest08 {
    public static void main(String[] args) {
        //创建线程对象
        Thread t = new MyThread3();
        t.setName("t");
        t.start();
        //调用sleep方法
        try {
            //问题：这行代码会让线程t进入休眠状态吗?
            t.sleep(1000 * 5);//在执行的时候还是会转换成: Thread.sleep( 1000 * 5);
                                    //这行代码的作用是:让当前线程进入休眠，也就是说main线程进入休眠。
                                    // 这样代码出现在main方法中,main线程睡眠。

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("hello World!");
    }
}

class MyThread3 extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            System.out.println(Thread.currentThread().getName() + "--->" + i);
        }
    }
}
```

```java
/**
 * sleep睡眠太久了，如果希望半道上醒来，你应该怎么办？就是说怎么叫醒一个正在睡眠的线程？
 * 注意：这个不是中断线程的执行，是终止线程的睡眠
 *
 */
public class ThreadTest09 {
    public static void main(String[] args) {
        Thread t = new Thread(new MyRunnable2());
        t.setName("t");
        t.start();
        //希望5秒后，t线程醒来
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            //打印异常信息
            e.printStackTrace();
        }
        //中断t线程的睡眠
        t.interrupt();//干扰，一盆冷水过去!
    }
}
class MyRunnable2 implements  Runnable{

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+"---->begin");
        //睡眠一年
        try {
            Thread.sleep(1000*60*60*24*365);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //一年后执行
        System.out.println(Thread.currentThread().getName()+"---->end");
    }

}
```

```java
/**
 * 在java中怎么强行终止一个线程的执行
 */
public class ThreadTest10 {
    public static void main(String[] args) {
        MyRunnable3 r = new MyRunnable3();
        Thread t = new Thread(r);
        t.setName("t");
        t.start();

        //模拟5秒
        try {
            Thread.sleep(1000 * 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //终止t线程
        //你想要什么时候终止t的执行，那么你把标记修改为false，就结束了。
        r.run = false;
    }
}

class MyRunnable3 implements Runnable {
    //打一个布尔标记
    boolean run = true;

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            if (run) {
                System.out.println(Thread.currentThread().getName() + "---->" + i);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                //return就结束了，你在结束之前还有什么没保存的。
                //在这里可以保存~
                //save....

                //终止当前线程
                return;
            }
        }
    }
}
```

### (这部分内容属于了解)关于线程的调度
* 常见的调度模型有哪些?
	* 抢占式调度模型:
		* 哪个线程的优先级比较高，抢到的CPU时间片的概率就高一些/多一些。java采用的就是抢占式调度模型.
* 均分式调度模型:
  * 平均分配CPU时间片。每个线程占有的CPU时间片时间长度一样。平均分配，一切平等。
    有一些编程语言，线程调度模型采用的是这种方式。
* java中提供了哪些方法是和线程调度有关系的呢?
  * 实例方法:
    * void setPriority (int newPriority)设置线程的优先级int getPriority 获取线程优先级
      最低优先级1
      默认优先级是5最高优先级10
      
    * 静态方法:
      static void yield()    让位方法
      暂停当前正在执行的线程对象，并执行其他线程
      yield()方法不是阻塞方法。让当前线程让位，让给其它线程使用。
      yield()方法的执行会让当前线程从"运行状态"回到"就绪状态”。
      注意:在回到就绪之后,有可能还会再次抢到。
      
    * 实例方法:
    
      ```java
      void join ()合并线程
          
      class MyThread1 extends Thread {
      public void dosome () {
      MyThread2 t = new MyThread2 () ;
      t.join();//当前线程进入阻塞，t线程执行，直到t线程结束。当前线程才可继续
      }
          
      class MyThread2 extends Thread{
      }
      
      ```
    
      

```java
/**
 * 了解:关于线程的优先级
 */
public class ThreadTest11 {
    public static void main(String[] args) {
/*        System.out.println("最高优先级"+Thread.MAX_PRIORITY);
        System.out.println("最低优先级"+Thread.MIN_PRIORITY);
        System.out.println("默认优先级"+Thread.NORM_PRIORITY);*/
        //获取当前线程对象，获取当前线程的优先级
        Thread currentThread=Thread.currentThread();
        //main线程的默认优先级是:5
//        System.out.println(currentThread.getName()+"线程的默认优先级是："+currentThread.getPriority());
        Thread t = new Thread(new MyRunnable5());
        t.setName("t");
        t.setPriority(10);
        t.start();

        //优先级搞得，只是抢到的CPU时间片相对多一些
        //大概率方向偏向于优先级比较高的
        for (int i = 0; i <10000 ; i++) {
            System.out.println(Thread.currentThread().getName()+"--->"+i);
        }
    }
}
class  MyRunnable5 implements Runnable{

    @Override
    public void run() {
        for (int i = 0; i <10000 ; i++) {
            System.out.println(Thread.currentThread().getName()+"--->"+i);
        }
        //获取线程优先级
     //   System.out.println(Thread.currentThread().getName()+"线程的默认优先级："+Thread.currentThread().getPriority());
    }
}
```

```java
/**
 * 让位，当前线程暂停，回到就绪状态，让给其它线程。
 * 静态方法: Thread .yieLd( );
 */
public class ThreadTest12 {
    public static void main(String[] args) {
    Thread t = new Thread(new MyRunnable6());
    t.setName("t");
    t.start();
        for (int i = 1; i <= 10000; i++) {
            System.out.println(Thread.currentThread().getName()+"--->"+i);
        }
    }
}
class MyRunnable6 implements  Runnable{

    @Override
    public void run() {
        for (int i = 1; i <= 10000; i++) {
            //每100个让位一次。
            if (i%100==0){
                Thread.yield();//当前线程暂停一下，然给主线程
            }
            System.out.println(Thread.currentThread().getName()+"--->"+i);
        }
    }
}
```

```java
public class ThreadTest13 {
    public static void main(String[] args) {
        System.out.println("main begin");
        Thread t = new Thread(new MyRunnable7());
        t.setName("t");
        t.start();

        //合并线程
        try {
            t.join();//t合并到当前线程中，当前线程受阻塞,t线程执行直到结束
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("main over");
    }
}
class MyRunnable7 implements  Runnable{

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            System.out.println(Thread.currentThread().getName()+"--->"+i);
        }
    }
}
```
关于多线程并发环境下，数据的安全问题。
* 为什么这个是重点?
	* 以后在开发中，我们的项目都是运行在服务器当中，而服务器已经将线程的定义，线程对象的创建，线程的启动等，都已经实现完了。这些代码我们都不需要编写。
	* 最重要的是:你要知道,你编写的程序需要放到一个多线程的环境下运行，你更需要关注的是这些数据在多线程并发的环境下是否是安全的。(重点:⭐⭐⭐⭐⭐)



什么时候数据在多线程并发的环境下会存在安全问题呢?
* 三个条件:
	* 条件1:多线程并发。
	* 条件2:有共享数据。
	* 条件3:共享数据有修改的行为。
	* 满足以上3个条件之后,就会存在线程安全问题。
怎么解决线程安全问题呢?
* 当多线程并发的环境下，有共享数据，并且这个数据还会被修改，此时就存在线程安全问题，怎么解决这个问题?
	* 线程排队执行(不能并发)用排队执行解决线程安全问题。这种机制被称为:线程同步机制。
	* 专业术语叫做:线程同步，实际上就是线程不能并发了，线程必须排队执行。
* 怎么解决线程安全问题呀?
使用"线程同步机制"
* 线程同步就是线程排队了，线程排队了就会牺牲一部分效率，没办法，数据安全第一位，只有数据安全了，我们才可以谈效率。数据不安全，没有效率的事儿。

说到线程同步这块，涉及到这两个专业术语:
* 异步编程模型:
	
	* 线程t1和线程t2，各自执行各自的，t1不管t2，t2不管t1，谁也不需要等谁,这种编程模型叫做:异步编程模型。其实就是:多线程并发(效率较高。)
	
	  异步就是并发
* 同步编程模型:
	
	* 线程t1和线程t2，在线程t1执行的时候，必须等待t2线程执行结束，或者说在t2线程执行的时候，必须等待t1线程执行结束，两个线程之间发生了等待关系，这就是同步编程模型。效率较低。线程排队执行。
	
	  同步就是排队



```java
/**
 * 银行账户
 * 不使用线程同步机制，多线程对同一个账户进行取款，出现线程安全问题
 */
public class Account {
    //账号
    private String actno;
    //余额
    private double balance;

    Object object = new Object();

    public Account() {

    }

    public Account(String actno, double balance) {
        this.actno = actno;
        this.balance = balance;
    }

    public String getActno() {
        return actno;
    }

    public void setActno(String actno) {
        this.actno = actno;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    //取款的方法
    public void wirhdraw(double money) {
        //以下这几行代码必须是线程排队的，不能并发。
        //一个线程把这里的代码全部执行结束之后，另一个线程才能进来。
/*        线程同步机制的语法是:
        synchronized( ) {
            //线程同步代码块。
        }
        synchronized后面小括号中传的这个"数据"是相当关键的。
        这个数据必须是多线程共享的数据。才能达到多线程排队

        ()中写什么？
            那要看你想让哪些线程同步。
            假设t1、t2、t3、t4、t5，有5个线程，
            你只希望t1 t2 t3排队，t4 t5不需要排队。怎么办?
            你一定要在()中写一个t1 t2 t3共享的对象。而这个
            对象对于t4 t5来说不是共享的。


            这里的共享对象是：账户对象。
            账户对象是共享的，那么this就算账户对象！！！

            在java语言中，任何一个对象都有f一把锁”，其实这把锁就是标记。(只是把它叫做锁。)100个对象，10日把锁。1个对象1把锁。

        以下代码的执行原理?
        1、假设t1和t2线程并发，开始执行以下代码的时候，肯定有一个先一个后。
        2、假设t1先执行了，遇到了synchronized，这个时候自动找“后面共享对象”的对象锁，
        找到之后，并占有这把锁，然后执行同步代码块中的程序，在程序执行过程中一直都是占有这把锁的。
        直到同步代码块代码结束，这把锁才会释放。
        3、假设t1已经占有这把锁，此时t2也遇到synchronized关键字，也会去占有后面共享对象的这把锁，
        结果这把锁被t1占有，t2只能在同步代码块外面等待t1的结束，直到t1把同步代码块执行结束了,
        t1会归还这把锁，此时t2终于等到这把锁﹐然后t2占有这把锁之后，进入同步代码块执行程序。


        这样就达到了线程排队执行。
        这里需要注意的是，这个共享对象一定要选好了，这个共享对象一定是
        你需要排队执行的这些线程对象所共享的。

        */

        Object object2 = new Object();
        //synchronized(object2) //这样编写不安全。object2是局部变量，不是共享对象
        //synchronized (this)
        //synchronized (object)  //object是实例变量，是共享对象
        synchronized ("abc"){   //"abc"在字符串常量池里，只有一份

            double before = this.getBalance();
            double after = before - money;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.setBalance(after);
        }
    }

}
```

```java
public class AccountThread extends Thread {
    //两个线程必须共享同一个账户对象
    private Account act;

    //通过构造方法传递过来账户对象
    public AccountThread(Account act){
        this.act = act;
    }
    @Override
    public void run() {
        //run方法的执行表示取款操作。
        //假设取款5000
        double money = 5000;
        //取款
        act.wirhdraw(money);
        System.out.println(Thread.currentThread().getName()+"对账户"+act.getActno()+"取款成功，余额"+act.getBalance());

    }
}
```

```java
public class Test {
    public static void main(String[] args) {
        //创建账户对象(只创建1个)
        Account act = new Account("act-001",10000);
        //创建两个线程
        Thread t1 = new AccountThread(act);
        Thread t2 = new AccountThread(act);
        //设置name
        t1.setName("t1");
        t2.setName("t2");
        //启动线程取款
        t1.start();
        t2.start();
    }
}
```

Java中有三大变量?【重要内容】
* 成员变量
	* 实例变量:在堆中。
	* 静态变量:在方法区。
* 局部变量:在栈中。

以上三大变量中:

* 局都变量永远都不会存在线程安全问题。
* 因为局都变量不共享。(一个线程一个栈。)
* 局部变量在栈中。所以局部变量永远都不会共享。
* 实例变量在堆中，维只有1个.
* 静态变量在方法区中,方法区只有1个。
* 堆和方法区都是多线程共享的，所以可能存在线程安全问题。

如果使用局部变量的话:
* 建议使用:stringBuilder.
* 因为局部变量不存在线程安全问题。
* 选择stringBuilder.stringBuffer效率比较低。
* ArrayList是非线程安全的。
* vector是线程安全的。
* HashMap Hashset是非线程安全的。
* Hashtable是线程安全的。



 ```java
  /**
   *在实例方法上可以使用synchronized吗?可以的。
   * synchronized出现在实例方法上，一定锁的是this。
   * 没得挑。只能是this。不能是其他的对象了。
   * 所以这种方式不灵活。
   *
   * 另外还有一个缺点: synchronized出现在实例方法上，
   * 表示整个方法体都需要同步，可能会无故扩大同步的范围，
   * 导致程序的执行效率降低。所以这种方式不常用。
   *
   * synchronized使用在实例方法上有什么优点？
   *  代码写得少了。节俭了。
   *
   * 如果共享的对象就是this，并且需要同步的代码块是整个方法体，
   * 建议使用这种方式。
   *
   */
  public synchronized void wirhdraw(double money) {
      {
  
          double before = this.getBalance();
          double after = before - money;
          try {
              Thread.sleep(1000);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
          this.setBalance(after);
      }
  }
 ```

总结:
* synchronized有三种写法:
	* 第一种:同步代码块
		* 灵活
		 synchronized(线程共享对象){
		 同步代码块;
		}
<font color = FF0000 size = 5>	* 第二种:在实例方法上使用synchronized</font>
<font color = FF0000 size = 5>		* 表示共享对象一定是this</font>
<font color = FF0000 size = 5>		 并且同步代码块是整个方法体。</font>
<font color = FF0000 size = 5>	* 第三种:在静态方法上使用synchronized</font>
<font color = FF0000 size = 5>		* 表示找类锁。</font>
<font color = FF0000 size = 5>		 类锁永远只有1把。</font>
<font color = FF0000 size = 5>		就算创建了100个对象,那类锁也只有一把。</font>
<font color = FF0000 size = 5>	* 对象锁:1个对象1把锁,100个对象100把锁。</font>
<font color = FF0000 size = 5>	* 类锁: 100个对象,也可能只是1把类锁。</font>



```java
//面试题:doOther方法执行的时候需要等待doSome方法的结束吗?
    //不需要，因为doOther()方法没有synchronized修饰
public class ThreadExam01 {
    public static void main(String[] args) throws InterruptedException {
    MyClass mc = new MyClass();

    Thread t1 = new MyThread7(mc);
    Thread t2 = new MyThread7(mc);

    t1.setName("t1");
    t2.setName("t2");

    t1.start();
    Thread.sleep(1000);//确保t1线程先执行
    t2.start();
    }
}
class  MyThread7 extends  Thread{
    private  MyClass mc;

    public MyThread7(MyClass mc) {
        this.mc = mc;
    }

    @Override
    public void run() {
            if (Thread.currentThread().getName()=="t1"){
                mc.doSome();
            }
            if (Thread.currentThread().getName()=="t2"){
                mc.doOther();
            }
    }
}
class MyClass {
    public synchronized void doSome(){
        System.out.println("doSome begin");
        try {
            Thread.sleep(1000*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("doSome over");
    }
    public void doOther(){
        System.out.println("doOther begin");
        System.out.println("doOther over");
    }

}
```

## <font color=FF0000>死锁</font>

![image-20220127181601142](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220127181601142.png)

```java
/**
 * 死锁代码要会写。
 * 一般面试官要求你会写。
 * 只有会写了，才会在以后的开发中注意这个事儿。
 * 因为死锁很难调试。
 *
 * synchronized最好不要嵌套，一不小心就可能会导致死锁现象的发生
 */
public class DeadLock {
    public static void main(String[] args) {
        Object object1 = new Object();
        Object object2 = new Object();
        //t1和t2两个线程共享o1,o2
        Thread t1 = new MyThread1(object1, object2);
        Thread t2 = new MyThread2(object1, object2);

        t1.start();
        t2.start();
    }

}

class MyThread1 extends Thread {
    Object object1;
    Object object2;

    public MyThread1(Object object1, Object object2) {
        this.object1 = object1;
        this.object2 = object2;
    }

    @Override
    public void run() {
        synchronized (object1) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (object2) {
                System.out.println(1);
            }
        }
    }
}

class MyThread2 extends Thread {
    Object object1;
    Object object2;

    public MyThread2(Object object1, Object object2) {
        this.object1 = object1;
        this.object2 = object2;
    }

    @Override
    public void run() {
        synchronized (object2) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (object1) {
                System.out.println(2);
            }
        }
    }
}
```

聊一聊，我们以后开发中应该怎么解决线程安全问题?
* 是一上来就选择线程同步吗? synchronized
	* 不是，synchronized会让程序的执行效率降低，用户体验不好。系统的用户吞吐量降低。用户体验差。在不得已的情况下再选择线程同步机制。
* 第一种方案:尽量使用局部变量代替"实例变量和静态变量"。
* 第二种方案:如果必须是实例变量，那么可以考虑创建多个对象，这样实例变量的内存就不共享了。(一个线程对应1个对象，100个线程对应100个对象，对象不共享,就没有数据安全问题了。
* 第三种方案:如果不能使用局部变量，对象也不能创建多个，这个时候就只能选择synchronized了。线程同步机制。

线程这块还有那些内容呢?列举一下
* 守护线程
      	* java语言中线程分为两大类:
           * 一类是:用户线程
          * 一类是:守护线程(后台线程)
          * 其中具有代表性的就是:垃圾回收线程（守护线程)。
           	* 守护线程的特点:
               	   * 一般守护线程是一个死循环，所有的用户线程只要结束，守护线程自动结束
                       	* 注意:主线程main方法是一个用户线程。
             * 守护线程用在什么地方呢?
                * 每天00 : 00的时候系统数据自动备份。
             * 这个需要使用到定时器，并且我们可以将定时器设置为守护线程。一直在那里看着，每到00:00的时候就备份一次。所有的用户线程如果结束了，守护线程自动退出，没有必要进行数据备份了。



```java
/**
 * 守护线程
 */
public class ThreadTest14 {
    public static void main(String[] args) {
            Thread t = new BakDataThread();
            t.setName("备份数据的线程");
        //启动线程之前，讲线程设置为守护线程
        t.setDaemon(true);
            t.start();

            //主线程：主线程是用户线程
        for (int i = 0; i < 10; i++) {
            System.out.println(Thread.currentThread().getName()+"---->"+(++i));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
class BakDataThread extends Thread{
    public void run(){
        int i = 0;
        //即使是死循环，但由于该线程是守护者，当用户线程结束，守护线程自动终止。
        while (true){
            System.out.println(Thread.currentThread().getName()+"---->"+(++i));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
```

* 定时器
  * 定时器的作用:
    - 间隔特定的时间，执行特定的程序。
    - 每周要进行银行账户的总账操作。每天要进行数据的备份操作。

    - 在实际的开发中，每隔多久执行一段特定的程序，这种需求是很常见的，那么在java中其实可以采用多种方式实现:
    	- 可以使用sleep方法，睡眠，设置睡眠时间，每到这个时间点醒来，执行任务。这种方式是最原始的定时器。(比较low )
    	- 在java的类库中已经写好了一个定时器: java.util.Timer，可以直接拿来用。不过，这种方式在目前的开发中也很少用，因为现在有很多高级框架都是支持定时任务的。
    	- 在实际的开发中，目前使用较多的是spring框架中提供的springTask框架，这个框架只要进行简单的配置，就可以完成定时器的任务。

```java
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 使用定时器指定定时任务
 */

public class TimerTest {
    public static void main(String[] args) throws ParseException {
        //创建定时器对象
        Timer timer = new Timer();
        // Timer timer = new Timer(true);//守护线程的方式

        //指定定时任务
        //timer.schedule(定时任务,第一次执行时间，间隔多久执行一次);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date firstime = simpleDateFormat.parse("2022-01-28 15:12:00");
 //       timer.schedule(new LogTimeTask(),firstime,1000*10);
        //匿名内部类方式
        timer.schedule(new TimerTask() {
            public void run() {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String strTime = simpleDateFormat.format(new Date())+"\r";
                FileOutputStream fileOutputStream = null;
                FileInputStream fileInputStream = null;
                try {
                    fileOutputStream = new FileOutputStream("log/log.txt",true);
                    fileInputStream = new FileInputStream("log/log.txt");
                        try {
                            fileOutputStream.write(strTime.getBytes());
                            fileOutputStream.flush();
                            byte[] bytes = new byte[fileInputStream.available()];
                            fileInputStream.read(bytes);
                            System.out.println(new String(bytes));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }finally {
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
        }, firstime, 1000 * 10);

    }
}

//编写一个定时任务类
//假设是一个记录日志的定时任务
class LogTimeTask extends TimerTask {

    @Override
    public void run() {
        //编写你需要执行的任务就行了
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strTime = simpleDateFormat.format(new Date());
        System.out.println(strTime + ":成功完成了一次数据备份!");
    }
}
```

* 实现线程的第三种方式:FutureTask方式，实现Callable接口。)(JDK8新特性。)
  * 这种方式实现的线程可以获取线程的返回值-
  - 之前讲解的那两种方式是无法获取线程返回值的，因为run方法返回void.
  - 思考:
  	- 系统委派一个线程去执行一个任务，该线程执行完任务之后，可能会有一个执行结果，我们怎么能拿到这个执行结果呢?
  	- 使用第三种方式:实现callable接口方式。

```java
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import java.util.concurrent.FutureTask;//JUC包下的，属于java的并发包，老JDK中没有这个包。新特性。

/**
 * 实现线程的第三种方式
 * 实现Callable接口
 * 这种方式的优点:可以获取到线程的执行结果。
 * 这种方式的缺点:效率比较低，在获取t线程执行结果的时候，当前线程受阻塞，效率较低。
 */
public class ThreadTest15 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //第一步:创建一个"未来任务类"对象。
        //参数非常重要，需要给一个callable接口实现类对象。
        FutureTask task = new FutureTask(new Callable() {
            @Override
            public Object call() throws Exception {//call()方法就相当与run方法，只不过这个有返回值。
                //线程执行一个任务，执行之后可能会有一个执行结果
                //模拟执行
                System.out.println("call method begin");
                Thread.sleep(1000 * 10);
                System.out.println("call method end!");
                int a = 100;
                int b = 200;
                return a + b;//自动装箱(300结果变成Integer)
            }
        });
        //创建线程对象
        Thread t = new Thread(task);

        //启动线程
        t.start();

        //这里是main方法，这是在主线程中。
        //在主线程中，怎么获取t线程的返回结果
        //get()方法的执行会导致"当前线程阻塞”
        Object object = task.get();
        System.out.println("结果"+object);

        // main方法这里的程序要想执行必须等待get()方法的结束
        //  而get()方法可能需要很久。因为get()方法是为了拿另一个线程的执行结果
        //  另一个线程执行是需要时间的。
        System.out.println("hello World");
    }
}
```

* 关于object类中的wait和notify方法。(生产者和消费者模式！)
  * 第一：wait和notify方法不是线程对象的方法，是java中任何一个java对象都有的方法，因为这两个方式是Object类中自带的。
    - wait方法和notify方法不是通过线程对象调用，
    - 不是这样的：t.wait()，也不是这样的：t.notify()..不对。
  - 第二：wait()方法作用？
            Object o = new Object();
            o.wait();
  - 表示：
  		- 让正在o对象上活动的线程进入等待状态，无期限等待，直到被唤醒为止。
  		- o.wait();方法的调用，会让“当前线程（正在o对象上活动的线程）”进入等待状态。
  	
  - 第三：notify()方法作用？
            Object o = new Object();
            o.notify();
  - 表示：
  		- 唤醒正在o对象上等待的线程。
  		- 还有一个notifyAll()方法：
  			- 这个方法是唤醒o对象上处于等待的所有线程。

![image-20220128194354050](C:\Users\hakuou\AppData\Roaming\Typora\typora-user-images\image-20220128194354050.png)

```java
import java.util.ArrayList;
import java.util.List;

/**
 * 1、使用wait方法和notify方法实现“生产者和消费者模式”
 * <p>
 * 2、什么是“生产者和消费者模式”？
 * 生产线程负责生产，消费线程负责消费。
 * 生产线程和消费线程要达到均衡。
 * 这是一种特殊的业务需求，在这种特殊的情况下需要使用wait方法和notify方法。
 * <p>
 * 3、wait和notify方法不是线程对象的方法，是普通java对象都有的方法。
 * <p>
 * 4、wait方法和notify方法建立在线程同步的基础之上。因为多线程要同时操作一个仓库。有线程安全问题。
 * <p>
 * 5、wait方法作用: o.wait()让正在o对象上活动的线程t进入等待状态，并且释放掉t线程之前占有的o对象的锁。
 * <p>
 * 6、notify方法作用: o.notify()让正在o对象上等待的线程唤醒，只是通知，不会释放o对象上之前占有的锁。
 * <p>
 * 7、模拟这样一个需求:
 * 仓库我们采用List集合。
 * List集合中假设只能存储1个元素。1个元素就表示仓库满了。
 * 如果List集合中元素个数是0，就表示仓库空了。
 * 保证List集合中永远都是最多存储1个元素。
 * <p>
 * 必须做到这种效果:生产1个消费1个。
 */
public class ThreadTest16 {
    public static void main(String[] args) {
        //创建一个仓库对象
        List list = new ArrayList();
        //创建两个线程对象
        //生产者线程
        Thread t1 = new Thread(new Producer(list));
        //消费者线程
        Thread t2 = new Thread(new Consumer(list));
        t1.setName("生产者线程");
        t2.setName("消费者线程");

        t1.start();
        t2.start();

    }
}

//生产线程
class Producer implements Runnable {
    //仓库
    private List list;

    public Producer(List list) {
        this.list = list;
    }

    @Override
    public void run() {
        //一直生产(使用死循环来模拟一直生产)
        while (true) {
            //给仓库对象list加锁
            synchronized (list) {
                if (list.size() > 0) {//大于0，说明仓库中已经有一个元素了
                    //当前线程进入等待状态,并且释放Producer之前占有的list集合的锁
                    try {
                        list.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //程序能够执行到这里说明仓库是空的，可以生产
                Object object = new Object();
                list.add(object);
                System.out.println(Thread.currentThread().getName() + "---->" + object);
                //唤醒消费者进行消费
                list.notify();
            }
        }
    }
}


//消费线程
class Consumer implements Runnable {
    //仓库
    private List list;

    public Consumer(List list) {
        this.list = list;
    }

    @Override
    public void run() {
        //一直消费
        while (true) {
            synchronized (list) {
                if (list.size() == 0) {
                    //仓库已经空了
                    //消费者线程等待，释放掉list集合的锁
                    try {
                        list.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //程序能够执行到此处说明仓库中有数据，进行消费
               Object object =  list.remove(0);
                System.out.println(Thread.currentThread().getName()+"---->"+object);
                //唤醒生产者生产
                list.notify();
            }
        }
    }
}
```

```java
/**
 * 使用生产者和消费者模式实现，交替输出:假设只有两个线程,输出以下结果:
 * t1-->1
 * t2-->2
 * t1-->3
 * t2-->4
 * t1-->5
 * t2-->6...
 * <p>
 * 要求:必须交替，并且t1线程负责输出奇数。t2线程负责输出偶数。两个线程共享一个数字，每个线程执行时都要对这个数字进行:++
 * public class Num {
 * int i;
 * synchronized (num){
 * if (nun是奇数){
 * synchronized (num){
 * if(num是偶数){
 * num.wait(l
 * }
 */
public class ThreadTest17 {
    public static void main(String[] args) {
        Numble num = new Numble();
        Thread t1 = new Thread(new Odd(num));
        Thread t2 = new Thread(new Even(num));
        t1.setName("t1");
        t2.setName("t2");
        t1.start();
        t2.start();
    }
}

class Numble{
    int i;
}


//奇数
class Odd implements Runnable {
    Numble  num;

    public Odd(Numble num) {
        this.num = num;
    }

    @Override
    public void run() {
        for ( num.i = 1; num.i < 100; num.i++) {
            synchronized (num) {
                if (num.i % 2 == 0) {
                    try {
                       num.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println(Thread.currentThread().getName() + "---->" + num.i);
                num.notifyAll();
            }

        }
    }
}


//偶数
class Even implements Runnable {
    Numble num;

    public Even(Numble num) {
        this.num = num;
    }

    @Override
    public void run() {
        for ( num.i = 1; num.i < 100; num.i++) {
            synchronized (num) {
                if (num.i % 2 != 0) {
                    try {
                        num.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println(Thread.currentThread().getName() + "---->" + num.i);
                num.notifyAll();
            }

        }
    }
```

```java
public class Test02 {
    public static void main(String[] args) {
        Num num = new Num();
        Print p = new Print(num);


        Thread t1 = new Thread(p, "t1");
        Thread t2 = new Thread(p, "t2");

        t1.start();
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t2.start();

    }
}

class Num {
    int i = 1;
}

class Print implements Runnable {
    Num num;

    public Print(Num num) {
        this.num = num;
    }

    public void run() {
        while (num.i <= 100) {
            synchronized (num) {
                System.out.println(Thread.currentThread().getName() + "-->" + num.i);
                num.i++;
                num.notify();
                try {
                    num.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
```

### 选择合适的线程数

#### CPU密集型

```
—般公式:CPU核数＋1
通过以下代码动态获取CPU核数:
Runtime.getRuntime().availableProcessors()
```

#### IO密集型
```
(1）配置方式一
IO密集型任务线程并不是一直在执行任务，则应该配置尽可能多的线程
例如:CPU核数*2

(2)配置方式二
CPU核数/(1–阻塞系数)，阻塞系数在0.8~0.9之间
两种方式并没有绝对的最优，实际生产环境应该结合压力测试进行调整
```
