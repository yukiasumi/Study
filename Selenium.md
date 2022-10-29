Selenium：Web端自动化框架
组件：
IDE->浏览器插件，用于录制回放脚本动作
WebDriver->Web端自动化框架接口。
Grid->远程节点控制。
版本：
1.X->RC 
2.X->RX、WebDriver，Firefox内置浏览器驱动
3.X->WebDriver,官方支持各主流浏览器驱动
4.X->WebDriver成为W3C标准协议，IDE官方支持更多主流浏览器，Grid启动简化，Grid console界面调整。增加新的元素定位方式。


环境准备：
1.将浏览器执行文件的路径配置到环境变量PATH中
1.将浏览器驱动文件的路径配置到环境变量PATH中

C:\Program Files (x86)\Microsoft\Edge\Application
C:\Program Files\Mozilla Firefox\
I:\CS\selenium\edgedriver_win64

1、创建浏览驱动对象
WebDriver 驱动对象 = new 浏览器驱动类( );

方法:
get(String url) ;	访问指定的URL。
close();		关闭当前页面。
quit(); 		退出浏览器程序。
getCurrentUrl()：获取当前页面URL地址。
getTitle()：获取当前页面标题。
getWindowHandle()：获取当前窗口句柄(返回值String)。
getWindowHandles()：获取所有窗口的句柄(返回值Set<String>)。

switchTo()：切换动作。
->window(String 页面句柄)：切换窗口。
->frame(String id)：通过frame id切换框架。
->alert()：获取页面警告框对象。

manage()：浏览器管理
->window()：窗口管理
-->maximize()：最大化

->timeouts()：超时管理
-->pageLoadTimeout(int 值, TimeUnit 单位)：页面加载超时
-->setScriptTimeout(int 值, TimeUnit 单位)：异步脚本超时
-->implicitlyWait(int 值, TimeUnit 单位)：隐式等待时间
4.0
-->pageLoadTimeout(Duration.of 时间单位(long 值))：页面加载超时
-->setScriptTimeout(Duration.of 时间单位(long 值))：异步脚本超时
-->implicitlyWait(Duration.of 时间单位(long 值))：隐式等待时间
显示等待：
WebDriverWait	等待对象 = new WebDriverWait( 驱动对象,long 秒数)
4.0
WebDriverWait	等待对象 = new WebDriverWait( 驱动对象,Duration.of 时间单位(long 值))
方法：
until(ExpectedCondition.方式);	返回WebElement对象。
navigate();	页面导航
->forward();	页面前进
->back();		页面后退
->refresh();	页面刷新
->to(String URL);	切换指定URL页面



findElement(By.方式);	定位页面元素，返回WebElement对象。
findElements(By.方式);	定位页面元素，返回List<WebElement>对象。

定位方式：【页面切换后需要重新定位】
1.name，根据页面元素的name元素值来定位元素。
2.id，根据页面元素的id元素值来定位元素。
3.class，根据页面元素的class元素值来定位元素，复合class无法定位。
4.linkText,根据页面元素的超链接文本来定位元素。
5.partialLinkText，根据页面元素的超链接文本来定位页面元素。
6.tagName，根据页面元素的标签文本来定位页面元素。
7.xpath，根据页面元素路径来定位页面元素。
->绝对路径
->相对路径
//，代表根元素标签【html】
*，代表任意标签
标签名[@属性名/text()='属性值'/'文本值'][int 顺位] / / [contains(@属性值/text()，'包含值')] / [starts-with(@属性名/text(),'起始值')]
8.cssSelector，根据页面元素的css选择器来定位页面元素。
选择器1>选择器2>选择器3...
标签选择器：标签名[属性='值'] / [属性^='起始值'] /[属性$='结尾值'] / [属性*='包含值']
id选择器： #id值
class选择器： .类1.类2...

WebElement对象方法：
sendkeys(keys); 模拟键盘发送按键
click(); 模拟鼠标左键点击
sendKeys(String keys); 模拟键盘发送按键。
getText(); 返回标签文本。
getAttribute(String 属性名); 返回指定属性的值。
clear()：清除文本框内容。
isSelected()：判断是否被选择状态，返回布尔值。
isEnabled()：判断是否可用，返回布尔值。
submit()：提交页面表单。


Select对象：
Select 下拉框对象 = new Select(元素对象);
方法：
selectByIndex(int index)：通过下标选择。
selectByValue(String value)：通过value属性值选择。
selectByVisibleText(String text)：通过option的文本选择。

Alert对象：
Alert 警告框对象 = 浏览器驱动对象.switchTo( ).alert( );
方法：
accept();		确定
dismiss();		取消
getText();		获取警告框文本

Actions对象：
Actions 动作对象 = new Actions(驱动对象);
动作更丰富

4.0新特性：
1.相对定位
RelativeLocator.with(By by).above/below/near/toLeftOf/toRightOf(By/WebElement 对象);
above上层标签
below下层标签
near最近标签
toLeftOf左边标签
toRightOf右边标签

2.Duration
3.新标签新窗口
驱动对象.switchTo().newWindow(WindowType.TAB/WINDOW);

4.浏览器配置对象
浏览器Options = new浏览器Options( )
方法：
setCapability(String 参数名，参数值)
····

Grid：远程节点控制。
1.建立Hub4.0
java - jar selenium.jar包路径（C:\Users\hakuou\eclipse-workspace\T037\jars\selenium-server-4.1.0.jar）
hub 
可选参数：
【--host 设置hub的IP地址(默认为本机第一顺位网卡地址)】192.168.1.129
【--port/-p 设置hub端口(默认端口: 4444)】

Web管理访问Hub
http://Hub IP地址：Hub端口
java -jar C:\Users\hakuou\eclipse-workspace\T037\jars\selenium-server-4.1.0.jar hub --host 192.168.1.129 --port 4444

2.建立Node4.0
java -jar selenium包路径 
node
可选参数：
[--host 设置node的IP地址(默认为本机第一顺位网卡地址)]
[--port 设置node端口(默认端口:5555)]
[--hub http://Hub的IP地址:Hub的端口/grid/register/(默认为本机第一顺位网卡地址)]
java -jar C:\Users\hakuou\eclipse-workspace\T037\jars\selenium-server-4.1.0.jar node --host 192.168.1.129 --port 5556 --hub http://192.168.1.163:4444



3.节点属性对象
构造方法
DesiredCapabilities 属性对象 = new DesiredCapabilities();
DesiredCapabilities 属性对象 = new DesiredCapabilities(String 浏览器名,String selenium版本,Platform 系统);
//4.0后取消
DesiredCapabilities 属性对象 = new DesiredCapabilities().浏览器();

属性对象方法：
setBrowserName(String 浏览器名);
setVersion(String selenium版本);
setPlatform(Platform 系统);

4.创建URL对象
URL URL对象 = new URL("http://节点IP地址:节点端口号/wd/hub");
4.0
URL URL对象 = new URL("http://节点IP地址:节点端口号/wd/hub");

5.建立远程浏览器驱动对象
WebDriver 驱动对象 = new RemoteWebDriver(URL对象, 属性对象);