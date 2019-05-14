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
 * <P>����˵��������Դ��λ��</P>
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
	 * ��־��¼����
	 */
	private final static LogWritter logger = LogFactory.getLogger(EJBLocator.class);

    /**
     * ���棬������Ų��ҵ���DataSource����Map�е�key��DataSource��jndi����value ��DataSource����
     */
    private static Map<String,DataSource> dsMap = new HashMap<String,DataSource>();
    
    /**
     * DSLocator�ľ�̬����,������ĳ�ʼ�����Ϊȫ�ֵ�һ��ʵ��.
     */
    private static DSLocator dsLocator = new DSLocator();

    /**
     * ˽�й���������ֹ���ⲿ����ʵ��
     */
    private DSLocator() {
    }

    /**
     * ���������ط���dsLocator
     * 
     * @return DSLocator - ���ص����ԡ�
     */
    public static DSLocator singleton() {
        return dsLocator;
    }

    /**
     * ��������ʵ����jndi���õ�DataSourceʵ��������������dsMap�����в����Ƿ��и�ʵ���Ļ��档
     * ����У�����ʵ������Connectionʵ�������ء�������ɳ��������clear������
     * Ȼ�����lookup�����ҵ���DataSourceʵ��������dsMap�С����������û�и�ʵ����
     * ����lookup�����ҵ���DataSourceʵ��������dsMap�С�
     * 
     * @param key:String ʵ����
     * @return Connection �õ���Connection
     * @throws Exception
     */
    public Connection getConnection(String key) throws Exception {
        if (dsMap.containsKey(key)) { //��������ڻ����У����ػ���
            try {
                DataSource ds = dsMap.get(key);
                Connection con = ds.getConnection();
                return con;
            } //��������ݹ��ڣ����²���
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
        else { //����������ڻ����У�����ʵ��
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
     * ���DSMap�����е����û���ʵ����
     * 
     * @param key
     */
    private void clear(String key) {
        if(dsMap.containsKey(key) ){
            dsMap.remove(key) ;
        }
    }

	  /**
	   * ʵ�ַ����������ر�Context���ʵ����
	   * @param context:Context ���رյ�Context���ʵ��
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
