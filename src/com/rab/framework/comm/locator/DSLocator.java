package com.rab.framework.comm.locator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;

/**
 * 
 * <P>Title: DSLocator</P>
 * <P>Description: </P>
 * <P>程序说明：数据源定位器</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-8-10</P>
 *
 */
public class DSLocator {
	/**
	 * 日志记录对象
	 */
	private final static LogWritter logger = LogFactory.getLogger(EJBLocator.class);

    /**
     * 缓存，用来存放查找到的DataSource对象。Map中的key是DataSource的jndi名，value 是DataSource对象。
     */
    private static Map<String,DataSource> dsMap = new HashMap<String,DataSource>();
    
    /**
     * DSLocator的静态变量,经过类的初始化后成为全局的一个实例.
     */
    private static DSLocator dsLocator = new DSLocator();

    /**
     * 私有构造器，阻止从外部创建实例
     */
    private DSLocator() {
    }

    /**
     * 单例，返回返回dsLocator
     * 
     * @return DSLocator - 返回的属性。
     */
    public static DSLocator singleton() {
        return dsLocator;
    }

    /**
     * 用来根据实例的jndi名得到DataSource实例。方法会先在dsMap属性中查找是否有给实例的缓存。
     * 如果有，则用实例生成Connection实例并返回。如果生成出错，则调用clear方法，
     * 然后调用lookup方法找到该DataSource实例，放入dsMap中。如果缓存中没有该实例，
     * 调用lookup方法找到该DataSource实例，放入dsMap中。
     * 
     * @param key:String 实例名
     * @return Connection 得到的Connection
     * @throws Exception
     */
    public Connection getConnection(String key) throws Exception {
        if (dsMap.containsKey(key)) { //如果存在于缓存中，返回缓存
            try {
                DataSource ds = dsMap.get(key);
                Connection con = ds.getConnection();
                return con;
            } //缓存的内容过期，重新查找
            catch (SQLException ex) {
                clear(key);
                Connection con = null;
                try {
                    DataSource ds = (DataSource) lookup(key);
                    dsMap.put(key, ds);
                    con = ds.getConnection();
                }
                catch (Exception ex1) {
                    logger.error(ex1.getMessage(), ex1);
                    throw ex1; 
                }
                
                return con;
            }
        }
        else { //如果不存在于缓存中，查找实例
            Connection con = null;
            try {
                DataSource ds = (DataSource) lookup(key);
                dsMap.put(key, ds);
                
                System.out.println(ds.getConnection());
                
                con = ds.getConnection();
            }
            catch (SQLException ex2) {
            	logger.error(ex2.getMessage() ,ex2) ;
                throw ex2;
            }

            return con;
        }
    }
    
	private Object lookup(String name) throws Exception {
    	Map<String, Properties> map = (Map)ApplicationContext.singleton().getValueByKey("data-source");
    	
    	Properties props = (Properties)map.get(name);
    	
    	Properties env = new Properties();
    	env.setProperty(Context.INITIAL_CONTEXT_FACTORY, props.getProperty("factory"));
    	env.setProperty(Context.PROVIDER_URL, props.getProperty("url"));
		
    	Context ctx = new InitialContext(env);
		
		Object obj = ctx.lookup(name);

		closeContext(ctx);
		
		return obj;
	}

    /**
     * 清空DSMap属性中的所用缓存实例。
     * 
     * @param key
     */
    private void clear(String key) {
        if(dsMap.containsKey(key) ){
            dsMap.remove(key) ;
        }
    }

	  /**
	   * 实现方法。用来关闭Context类的实例。
	   * @param context:Context 被关闭的Context类的实例
	   * @throws NamingException
	   */
	  protected void closeContext(Context context)
	      throws NamingException{
	      if(context != null){
	          context.close();
	      }
	  }
	  
//	public static void main(String[] args) {
//		Connection con = null;
//		try {
//			con = DSLocator.singleton().getConnection("ds_oracle");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		System.out.println(con);
//
//	}
  
}
