package com.rab.framework.dao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.EmbeddedComponentType;
import org.hibernate.type.Type;

import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.domain.po.PersistenceObject;

/**
 * 
 * 
 * <P>Title: HibernateMetadataUtil</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-8-18</P>
 *
 */
public class HibernateMetadataUtil {

	/**
	 * 日志记录器
	 */
	private final static LogWritter logger = LogFactory.getLogger(HibernateMetadataUtil.class);

    /**
     * 默认无参构造器，由于是静态类，所以为private
     */
    private HibernateMetadataUtil() {

    }

//    private static SessionFactoryImplementor
//            getSessionFactoryImplementor(PersistenceDAO dao) {
//
//    	Session session = ((PersistenceDAOImpl)dao).getSession();
//    	return (SessionFactoryImplementor) session.getSessionFactory();
//
//    }

    
    public static String[] getPropertyNames(
            PersistenceDAO dao, Class<?> theClass) throws BaseCheckedException {

        ClassMetadata data = getClassMetadata(dao, theClass);
        return data.getPropertyNames();

    }


    /**
     * 
     * <p>取得一个BO的描述信息</p>
     *
     * @param dao       数据访问层接口
     * @param boClass   BO类型
     * @return          BO的描述信息
     */
    public static ClassMetadata getClassMetadata(PersistenceDAO dao, Class<?> boClass) {
    	
    	return ((PersistenceDAOImpl)dao).getSession().getSessionFactory().getClassMetadata(boClass);
    	
    }


    /**
     * 取得一个BO对象对应的主键名称
     *
     * @param dao       数据访问层接口
     * @param boClass Class BO类型
     * @return String 主键名称
     * @throws BaseCheckedException
     */
    public static String getIdName(PersistenceDAO dao, Class<?> boClass)
            throws BaseCheckedException {
        AbstractEntityPersister data = null;
        try {
            data = (AbstractEntityPersister) getClassMetadata(dao, boClass);
            if(data == null){
              return null ;
            }

            return data.getIdentifierPropertyName();
        } catch (HibernateException e) {
            logger.error("00000418:取BO [" + boClass + "] 的主键时出现异常！", e);
            List<String> params = new ArrayList<String>();
            params.add(boClass.getName());
            BaseCheckedException ex = new BaseCheckedException("00000418", params, e);
            throw ex;
        }
    }

    /**
     * 
     * <p>取得一个BO对象对应的主键值</p>
     *
     * @param dao       数据访问层接口
     * @param bo
     * @return
     * @throws BaseCheckedException
     */
    public static List<Object> getIdValues(PersistenceDAO dao , PersistenceObject bo)
            throws BaseCheckedException {
        AbstractEntityPersister data = null;
               EmbeddedComponentType type = null;
               List<Object> result = new ArrayList<Object>();
               try {
                   data = (AbstractEntityPersister) getClassMetadata(dao, bo.getClass());
                   if (data.getIdentifierPropertyName() == null) {        //联合主键
                       type = (EmbeddedComponentType) data.getIdentifierType();
                       String[] columns = data.getIdentifierColumnNames();

                       for (int i = 0; i < columns.length; i++) {
                           result.add( type.getPropertyValue(bo, i, EntityMode.POJO));
                           logger.debug("主键"+columns[i]+"的valule为："  +
                                   type.getPropertyValue(bo, i, EntityMode.POJO));
                       }
                   }else{    //单值主键
                       result.add(data.getIdentifier(bo , EntityMode.POJO )) ;
                   }
                   return result;
               } catch (HibernateException e) {
                   logger.error("00000419:取BO [" + bo + "] 的主键值时出现异常！", e);
                   List<String> params = new ArrayList<String>();
                   params.add("" + bo);
                   BaseCheckedException ex =
                           new BaseCheckedException("00000419", e);
                   throw ex;
               }


    }

    /**
     * 返回联合主键或单主键的属性 ；
     *
     * @param dao       数据访问层接口
     * @param bo
     * @return
     * @throws BaseCheckedException
     */
    public static Map<String, Object> getIdInfo(PersistenceDAO dao, PersistenceObject bo)
            throws BaseCheckedException {
        AbstractEntityPersister data = null;
        EmbeddedComponentType type = null;
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            data = (AbstractEntityPersister) getClassMetadata(dao, bo.getClass());
            if (data.getIdentifierPropertyName() == null) {        //联合主键
                type = (EmbeddedComponentType) data.getIdentifierType();
                /**
                 * 解决联合主键（嵌套、非嵌套）取值问题 by ZhangBin   
                 */
                Serializable identifier = data.getIdentifier(bo, EntityMode.POJO);
                if(identifier!=null){
	                String[] columns =  type.getPropertyNames();
	                for (int i = 0; i < columns.length; i++) {
	                    result.put(columns[i], type.getPropertyValue(identifier, i, EntityMode.POJO));
	                }
                }
            }
            else{    //单值主键
                result.put(data.getIdentifierColumnNames()[0] , data.getIdentifier(bo , EntityMode.POJO )) ;
            }
            return result;
        } catch (HibernateException e) {
        	 logger.error("00000420:取BO [" + bo + "] 的联合主键或单主键的属性时出现异常！", e);
             List<String> params = new ArrayList<String>();
             params.add("" + bo);
            throw new BaseCheckedException("00000420", e);
        }


    }

    /**
     * 取得BO的主键属性对应的Type
     *
     * @param dao       数据访问层接口
     * @param boClass
     * @return
     * @throws BaseCheckedException
     */
    public static Type getIdType(PersistenceDAO dao, Class<?> boClass)
            throws BaseCheckedException {
        ClassMetadata data = null;
        try {
            data = getClassMetadata(dao, boClass);
            return data.getIdentifierType();
        } catch (HibernateException e) {
        	logger.error("00000421:取BO [" + boClass + "] 的主键属性对应的Type时出现异常！", e);
            List<String> params = new ArrayList<String>();
            params.add("" + boClass);
            BaseCheckedException ex =
                    new BaseCheckedException("00000421", e);
            throw ex;
        }

    }

    /**
     * 取得一个BO属性对应的Hibernate 类型
     *
     * @param dao       数据访问层接口
     * @param boClass      Class BO类型
     * @param propertyName String 属性名称
     * @return Type 属性对应的类型
     * @throws BaseCheckedException
     */
    public static Type getPropertyType(PersistenceDAO dao, Class<?> boClass, String propertyName)
            throws BaseCheckedException {
        ClassMetadata data = null;
        try {
            data = getClassMetadata(dao, boClass);
            return data.getPropertyType(propertyName);
        } catch (HibernateException e) {
        	logger.error("00000422:取BO [" + boClass + "] 的属性对应的Hibernate 类型时出现异常！", e);
            List<String> params = new ArrayList<String>();
            params.add("" + boClass);
            BaseCheckedException ex =
                    new BaseCheckedException("00000422", e);
            throw ex;
        }
    }


    /**
     * 取得一个BO中属性的真实值
     *
     * @param dao       数据访问层接口
     * @param bo
     * @param pname   String 属性名称
     * @return
     * @throws BaseCheckedException
     */
    public static Object getPorpertyValue(PersistenceDAO dao, PersistenceObject bo, String pname)
            throws BaseCheckedException {
        ClassMetadata data = getClassMetadata(dao, bo.getClass());
        return data.getPropertyValue(bo, pname, EntityMode.POJO);

    }

    /**
     * 
     * <p>该方法用来取得一个真实BO对象的主键值,需要注意的是，这里的主键必须拥有标准的getter方法，否则抛出异常</p>
     *
     * @param dao       数据访问层接口
     * @param bo   BO的主键值
     * @return
     * @throws BaseCheckedException
     */
    public static Object getBOIDValue(PersistenceDAO dao, PersistenceObject bo)
            throws BaseCheckedException {
        String idName = getIdName(dao, bo.getClass());
        StringBuffer methodName = new StringBuffer("get");
        methodName.append(changeName(idName));   //取得ID对应的getter方法名称

        try {
            logger.debug("封装的主键ID方法为：" + methodName);
            if( idName== null ||idName.equals( "")) return  null ;
            Class<?>[] params = null;
            Method method =
                    bo.getClass().getMethod(methodName.toString(), params);

            Object[] params2 = null;
            return method.invoke(bo, params2); //调用getter方法来取得主键值
        } catch (Exception e) {
        	logger.error("00000423:取BO [" + bo + "] 的主键值时出现异常！", e);
            List<String> params = new ArrayList<String>();
            params.add("" + bo);
            BaseCheckedException ex =
                    new BaseCheckedException("00000423", e);
            throw ex;

        } 

    }


    private static String changeName(String name) {
        if(name == null||  name.trim().equals("") ) return "" ;
        String result;
        String frist = name.substring(0, 1);
        frist.toUpperCase();
        result = name.replaceFirst(name.substring(0, 1), frist.toUpperCase());
        return result;
    }


    /**
     * 根据一个BO的class name，取得该BO对应的Table name ；
     *
     * @param dao       数据访问层接口
     * @param className String 对象类名称
     * @return String 表名称
     */
    public static String getTableNameByClassName(PersistenceDAO dao, String className) {
        //强制转换类型。Hibernate对象结构种，每一种ClassMetadata的实现都是继
        // 承该超类的，所以上溯造型不会发生错误；
    	Session session = ((PersistenceDAOImpl)dao).getSession();
        AbstractEntityPersister aep = (AbstractEntityPersister)
                session.getSessionFactory().getClassMetadata(className);

        if(aep==null){   //080424
            logger.error(className+" 没有注册到持久层中，请检查hbm配置文件是否注册");
            throw new RuntimeException(className+" 没有注册到持久层中，请检查hbm配置文件是否注册");
        }

        return aep.getTableName();

    }


    public static String getColumnNameByPropertyName(PersistenceDAO dao ,String className , String propertyName ){
    	Session session = ((PersistenceDAOImpl)dao).getSession();
    	AbstractEntityPersister aep = (AbstractEntityPersister)
                session.getSessionFactory().getClassMetadata(className);
        return aep.getPropertyColumnNames(propertyName)[0] ;
    }

}
