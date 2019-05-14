

=====================================================================
                         浏览器端代码表缓存管理程序
=====================================================================

原理： 
	将代码表缓存到browser所在的操作系统的硬盘中，缓存程序用applet管理。

缓存管理算法：
	1. 系统初始缓存数据为空
	2. 用户读取数据时，直接从本地文件读取
	3. 如果本地硬盘中没有对应的数据，则从web服务器下载指定的数据，并缓存到本地硬盘中，并返回用户。
	4. 本地缓存数据按照设定的事件间隔定时从web服务器更新
	
管理接口：
	1. 见示例程序 testapplet.html
	
文件组成：
	1. BrowserCacheManager.java 管理主程序，Applet
	2. BrowserCacheTable.java   辅助程序
	3. BrowserCacheLog.java     日志记录程序
	4. Test.java                demo及调试用程序
	5. appletdeploy.bat         applet发布示例脚本
	6. keystore                 证书文件，发布组件签名用
	7. testapplet.html          示例web页面程序


运行示例：
	1. 在web.xml中添加如下servlet定义信息
		<!--================================================================-->
		<!--         WebCodeCacheServlet（startup servlet）                    -->
		<!--================================================================-->
		<servlet>
			<servlet-name>WebCodeCacheServlet</servlet-name>
			<servlet-class>com.rab.framework.component.dictcache.WebCodeCacheServlet</servlet-class>
			<load-on-startup>3</load-on-startup>
		</servlet>
		<servlet-mapping>
			<servlet-name>WebCodeCacheServlet</servlet-name>
			<url-pattern>/WebCodeCacheServlet</url-pattern>
		</servlet-mapping>
		
	2. 执行appletdeploy.bat脚本，完成applet的打包和发布
	
	3. 在浏览器中访问testapplet.html程序即可。
	
