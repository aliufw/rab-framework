启动类初始化:
--------------------------------------
在web.xml中添加如下启动servlet

	<servlet>
		<servlet-name>DynamicSessionServlet</servlet-name>
		<servlet-class>com.rab.framework.web.dynamicsession.DynamicSessionServlet</servlet-class>
		<init-param>
			<param-name>session-timeout</param-name>
			<param-value>10</param-value><!-- 单位: 分钟 -->
		</init-param> 
        <load-on-startup>1</load-on-startup>
     </servlet>

     
使用接口:
--------------------------------------
保存session数据:
DynamicSessionManager.singleton().setData(request, key, value)

读取session数据:   
Object data = DynamicSessionManager.singleton().getData(request, key)
 