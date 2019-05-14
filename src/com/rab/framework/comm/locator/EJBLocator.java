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
 * <P>����˵����EJB����λ��</P>
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
	 * ��־��¼����
	 */
	private final static LogWritter logger = LogFactory.getLogger(EJBLocator.class);
	
    private Map<String,ServiceFacadeHome> home  = new HashMap<String,ServiceFacadeHome>() ;
    
    private static EJBLocator ejbLocator = new EJBLocator();

    private EJBLocator() {
    }

    /**
     * ����ejbLocator���ԡ�
     * @return
     */
    public static EJBLocator singleton() {
        return ejbLocator;
    }

    public ServiceFacade getDomainFacade(String key) throws Exception {
    	Object obj = null;
        if (home.containsKey(key)) {
        	ServiceFacadeHome ejbHome = (ServiceFacadeHome) home.get(key); //����������д˶����򷵻ش˶���
            try {
                obj = ejbHome.create();
            }
            catch (Exception ex) { //�������е�ʵ������ʱ���������ɻ��档
                clear(key);
                try {
                	ServiceFacadeHome ejbHomeBak = (ServiceFacadeHome)lookup(key);
                    obj = ejbHome.create();
                    home.put(key, ejbHomeBak);
                }
                catch (Exception ex1) { //��װ�쳣
                    logger.error(ex1.getMessage() ,ex1) ;
                    throw ex1;
                }
            }
        }
        else { //�������޴�ʵ�������²��ҵõ�
        	ServiceFacadeHome ejbHome = null;
            try {
                ejbHome = (ServiceFacadeHome) lookup(key);
                obj = ejbHome.create();
                home.put(key, ejbHome);
            }
            catch (Exception ex2) { //��װ�쳣
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
     * ʵ�ַ����������ر�Context���ʵ����
     * @param context:Context ���رյ�Context���ʵ��
     * @throws NamingException
     */
    private void closeContext(Context context)
        throws NamingException{
        if(context != null){
            context.close();
        }
    }
    
    /**
     * ���home�����е����û���ʵ����Ȼ�����LogWritter�ľ�̬��<br>
     * ��sysInfo,����־�м�¼�����Ѿ���յ���Ϣ��
     */
    private void clear(String key) {
        logger.info("EJBLocator�еĻ��� " + key + " �Ѿ���գ�");
        if(home.containsKey(key) ){
            home.remove(key) ;
        }
    }
}

