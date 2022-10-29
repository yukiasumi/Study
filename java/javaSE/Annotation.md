3、注解

	3.1、注解，或者叫做注释类型，英文单词是：Annotation
		疑问：注解到底是干啥的？？？？？？？？？
	
	3.2、注解Annotation是一种引用数据类型。编译之后也是生成xxx.class文件。
	
	3.3、怎么自定义注解呢？语法格式？
	
		 [修饰符列表] @interface 注解类型名{
	
		 }
	
	3.4、注解怎么使用，用在什么地方？
	
		第一：注解使用时的语法格式是：
			@注解类型名
		
		第二：注解可以出现在类上、属性上、方法上、变量上等....
		注解还可以出现在注解类型上。
	
	3.5、JDK内置了哪些注解呢？
	
		java.lang包下的注释类型：
	
			掌握：
			Deprecated 用 @Deprecated 注释的程序元素，
			不鼓励程序员使用这样的元素，通常是因为它很危险或存在更好的选择。 
	
			掌握：
			Override 表示一个方法声明打算重写超类中的另一个方法声明。 
	
			不用掌握：
			SuppressWarnings 指示应该在注释元素（以及包含在该注释元素中的
			所有程序元素）中取消显示指定的编译器警告。 
	
	3.6、元注解
		什么是元注解？
			用来标注“注解类型”的“注解”，称为元注解。
	
		常见的元注解有哪些？
			Target
			Retention
		
		关于Target注解：
			这是一个元注解，用来标注“注解类型”的“注解”
			这个Target注解用来标注“被标注的注解”可以出现在哪些位置上。
	
			@Target(ElementType.METHOD)：表示“被标注的注解”只能出现在方法上。
			@Target(value={CONSTRUCTOR, FIELD, LOCAL_VARIABLE, METHOD, PACKAGE, MODULE, PARAMETER, TYPE})
				表示该注解可以出现在：
					构造方法上
					字段上
					局部变量上
					方法上
					....
					类上...
		
		关于Retention注解：
			这是一个元注解，用来标注“注解类型”的“注解”
			这个Retention注解用来标注“被标注的注解”最终保存在哪里。
	
			@Retention(RetentionPolicy.SOURCE)：表示该注解只被保留在java源文件中。
			@Retention(RetentionPolicy.CLASS)：表示该注解被保存在class文件中。
			@Retention(RetentionPolicy.RUNTIME)：表示该注解被保存在class文件中，并且可以被反射机制所读取。
		
	3.7、Retention的源代码
	
			//元注解	
			public @interface Retention {
				//属性
				RetentionPolicy value();
			}
			
		RetentionPolicy的源代码：
			public enum RetentionPolicy {
				 SOURCE,
				 CLASS,
				 RUNTIME
			}
	
			//@Retention(value=RetentionPolicy.RUNTIME)
			@Retention(RetentionPolicy.RUNTIME)
			public @interface MyAnnotation{}



	3.8、Target的源代码


	3.9、注解在开发中有什么用呢？
	
		需求：
			假设有这样一个注解，叫做：@Id
			这个注解只能出现在类上面，当这个类上有这个注解的时候，
			要求这个类中必须有一个int类型的id属性。如果没有这个属性
			就报异常。如果有这个属性则正常执行！