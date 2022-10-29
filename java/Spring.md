#### 常用注解

```java
@Configuration 标识当前类是配置类
@ComponentScan 包扫描注解 扫描注解
@Bean 标识该方法的返回值交给Spring容器管理
@Scope 控制多例和单例
@Lazy 懒加载
@PostConstruct 初始化方法
@PreDestroy 销毁方法
@Component 将当前类未来的对象交给容器管理
@Autowired 按照类型进行注入,如果类型不匹配(不唯一) 按照name进行注入
@Qualifier 按照名称进行注入
@Repository 标识持久层注解
@Service 标识Service层
@Controller 标识Controller层
@Value 为属性赋值 @Value(“${key}”)
@PropertySource 加载指定路径的配置文件properties
@Aspect 标识当前类是一个切面类
@Pointcut 用于定义切入点表达式 表达式写法4种
@EnableAspectJAutoProxy 让AOP的注解有效果
@Before AOP-前置通知
@AfterReturning AOP-后置通知
@AfterThrowing AOP-异常通知
@After AOP-最终通知
@Around AOP-环绕通知
@Order(1) //可以利用order关键字 实现AOP的排序 数字越小越先执行.
@ResponseBody 将返回的数据转化为JSON串, 如果是字符串本身 原数据返回
@RequestMapping(“/hello”) 实现浏览器的请求路径与方法的映射
@PathVariable restFul结构,接收参数的注解.
@GetMapping(“”) 只能接收GET请求类型
@DeleteMapping(“”) 只能接收DELETE请求类型
@PostMapping(“”) 只能接收POST请求类型
@PutMapping(“”) 只能接收PUT请求类型
@RestController 表示Controller类,同时要求返回值为JSON
@CrossOrigin 允许跨域访问
@RequestBody 参数接收时,将JSON串转化为java对象 json中的key与对象的属性一致.
@Data lombok动态生成get/set/toString/equals/hashcode等方法
@Accessors 控制是否开启链式加载结构
@NoArgsConstructor 生成无参构造方法
@AllArgsConstructor 生成全参构造方法
@Mapper mybatis将当前的接口交给Spring容器管理. Map<类名小写,JDK动态代理对象>
@SpringBootTest 该注解的作用在进行代码测试时启动spring容器,之后动态的获取对象 注意包路径 主启动类的同包及子包中.
@Param Mybatis中将参数封装为Map集合. @Param(“maxAge”) int maxAge
@Alias Mybatis中定义对象的别名 @Alias(“User”)
@MapperScan Mybatis中扫描指定包路径的接口 为其创建代理对象.
@Insert Mybatis 新增操作注解
@Update Mybatis 修改操作注解
@Delete Mybatis 删除操作注解
@Select Mybatis 查询操作注解
@Transactional Spring中用来控制事务
@RestControllerAdvice Controller层的全局异常处理
@ExceptionHandler 按照某种异常类型进行拦截
@TableName(“item_cat”) MybatisPlus注解POJO与数据表绑定 注意表名
@TableId(type = IdType.AUTO) MybatisPlus注解 标识主键/主键自增
@TableField(exist = false) MybatisPlus注解 标识属性是否存在,及名称是否一致
```

