package com.rab.framework.comm.locator;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJBHome;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.service.ejb.ServiceFacade;
import com.rab.framework.service.ejb.ServiceFacadeHome;

/**
 * 
 * <P>Title: EJBLocator</P>
 * <P>Description: </P>
 * <P>程序说明：EJB服务定位器</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class EJBLocator {
	/**
	 * 日志记录对象
	 */
	private final static LogWritter logger = LogFactory.getLogger(EJBLocator.class);
	
    private Map<String,ServiceFacadeHome> home  = new HashMap<String,ServiceFacadeHome>() ;
    
    private static EJBLocator ejbLocator = new EJBLocator();

    private EJBLocator() {
    }

    /**
     * 返回ejbLocator属性。
     * @return
     */
    public static EJBLocator singleton() {
        return ejbLocator;
    }

    public ServiceFacade getDomainFacade(String key) throws Exception {
    	Object obj = null;
        if (home.containsKey(key)) {
        	ServiceFacadeHome ejbHome = (ServiceFacadeHome) home.get(key); //如果缓存中有此对象，则返回此对象
            try {
                obj = ejbHome.create();
            }
            catch (Exception ex) { //当缓存中的实例过期时，重新生成缓存。
                clear(key);
                try {
                	ServiceFacadeHome ejbHomeBak = (ServiceFacadeHome)lookup(key);
                    obj = ejbHome.create();
                    home.put(key, ejbHomeBak);
                }
                catch (Exception ex1) { //封装异常
                    logger.error(ex1.getMessage() ,ex1) ;
                    throw ex1;
                }
            }
        }
        else { //缓存中无此实例，重新查找得到
        	ServiceFacadeHome ejbHome = null;
            try {
                ejbHome = (ServiceFacadeHome) lookup(key);
                obj = ejbHome.create();
                home.put(key, ejbHome);
            }
            catch (Exception ex2) { //封装异常
            	logger.error(ex2.getMessage() ,ex2) ;
            	throw ex2;
            }
        }
        
        return (ServiceFacade)obj;
    }
    
	private Object lookup(String name) throws Exception {
    	Map<String, Properties> map = (Map)ApplicationContext.singleton().getValueByKey("ejb");
    	
    	Properties props = (Properties)map.get(name);
    	
    	Properties env = new Properties();
    	env.setProperty(Context.INITIAL_CONTEXT_FACTORY, props.getProperty("factory"));
    	env.setProperty(Context.PROVIDER_URL, props.getProperty("url"));
		Context ctx = new InitialContext(env);
		
		Object obj = ctx.lookup(name);
//		System.out.println("lfw: obj = " + obj.getClass());
		EJBHome home = (EJBHome) PortableRemoteObject.narrow(obj, EJBHome.class);
		closeContext(ctx);
		
		return home;
	}

    /**
     * 实现方法。用来关闭Context类的实例。
     * @param context:Context 被关闭的Context类的实例
     * @throws NamingException
     */
    private void closeContext(Context context)
        throws NamingException{
        if(context != null){
            context.close();
        }
    }
    
    /**
     * 清空home属性中的所用缓存实例。然后调用LogWritter的静态方<br>
     * 法sysInfo,在日志中记录缓存已经清空的信息。
     */
    private void clear(String key) {
        logger.info("EJBLocator中的缓存 " + key + " 已经清空！");
        if(home.containsKey(key) ){
            home.remove(key) ;
        }
    }
}

