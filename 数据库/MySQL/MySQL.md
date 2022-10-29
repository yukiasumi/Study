source 文件名(.sql) 导入

\c结束语句

注意：in不是一个区间。in后面跟的是具体的值。

	not 可以取非，主要用在 is 或 in 中
		is null
		is not null
		in
		not in
concat(upper(substr(name,1,1)),substr(name,2,length(name) - 1))	首字母大写

case..when..then..when..then..else..end

		select 
			ename,
			job, 
			sal as oldsal,
			(case job when 'MANAGER' then sal*1.1 when 'SALESMAN' then sal*1.5 else sal end) as newsal 
		from 
			emp;

* 单行处理函数

  * lower 转换小写

  * upper 转换大写

  * substr( 被截取的字符串, 起始下标,截取的长度)   取子串

  * concat函数进行字符串的拼接

  * length(值) 取长度

  * trim(值) 去空格

  * str_to_date 将字符串转换成日期

  * date_format 格式化日期

  * format 设置千分位

  * round(值)四舍五入【0为整数，-1为十位，1为一位小数】

  * rand() 生成随机数【<1，可以插入使用】

  * ifnull(字段名，空值转化)

* 分组函数（多行处理函数）
	* count	计数
	* sum	求和
	* avg	平均值
	* max	最大值
	* min	最小值

* 注意
  * 分组函数自动忽略NULL，你不需要提前对NULL进行处理。
  
  * 分组函数中count(*)会计算NULL行和count(具体字段)不会计算NULL行
  
  * 分组函数不能够直接使用在where子句中。
  
  * 所有的分组函数可以组合起来一起用。
  
* 分组查询【字段可联合分组】
	* 关键字顺序
		
		```xml
		select 
			...
		from
			...
		where
			...
		group by
			...
		having
			...
		order by
			...
		```
		
	* 执行顺序
		1. from
		2. where
		3. group by
		4. having
		5. select
		6. order by



where和having优先选择where



去重关键字distinct

distinct只能出现在所有字段的最前方

select distinct job,deptno from emp;【联合去重job和deptno】

select count(distinct job) from emp;【统计岗位的种类】



无条件的连接查询条目数是两表条目数的乘积

内连接

```xml
		select 
			...
		from
			a
(inner) join
			b
		on
			a和b的连接条件
		where
			筛选条件

```

<font size=6 color=FF0000>内连接之等值连接。</font>

```xml
select 
	e.ename,d.dname
from
	emp e
inner join
	dept d
on
	e.deptno = d.deptno; // 条件是等量关系，所以被称为等值连接。
```
<font size=6 color=FF0000>内连接之非等值连接</font>

```xml
select 
	e.ename, e.sal, s.grade
from
	emp e
inner join
	salgrade s
on
	e.sal between s.losal and s.hisal;//条件不是一个等量关系，称为非等值连接。
```

```sql
select A.sname '姓名',B.score '语文',C.score '数学',D.score '英语'

from student A,sc B,sc C,sc D 

where A.id=B.sid and B.cid in(select id from course where cname='语文')

and A.id=C.sid and C.cid in(select id from course where cname='数学')

and A.id=D.sid and D.cid in(select id from course where cname='英语');
```

