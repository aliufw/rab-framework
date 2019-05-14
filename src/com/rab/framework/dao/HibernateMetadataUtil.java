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
 * <P>����˵����</P>
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
	 * ��־��¼��
	 */
	private final static LogWritter logger = LogFactory.getLogger(HibernateMetadataUtil.class);

    /**
     * Ĭ���޲ι������������Ǿ�̬�࣬����Ϊprivate
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
     * <p>ȡ��һ��BO��������Ϣ</p>
     *
     * @param dao       ���ݷ��ʲ�ӿ�
     * @param boClass   BO����
     * @return          BO��������Ϣ
     */
    public static ClassMetadata getClassMetadata(PersistenceDAO dao, Class<?> boClass) {
    	
    	return ((PersistenceDAOImpl)dao).getSession().getSessionFactory().getClassMetadata(boClass);
    	
    }


    /**
     * ȡ��һ��BO�����Ӧ����������
     *
     * @param dao       ���ݷ��ʲ�ӿ�
     * @param boClass Class BO����
     * @return String ��������
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
            logger.error("00000418:ȡBO [" + boClass + "] ������ʱ�����쳣��", e);
            List<String> params = new ArrayList<String>();
            params.add(boClass.getName());
            BaseCheckedException ex = new BaseCheckedException("00000418", params, e);
            throw ex;
        }
    }

    /**
     * 
     * <p>ȡ��һ��BO�����Ӧ������ֵ</p>
     *
     * @param dao       ���ݷ��ʲ�ӿ�
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
                   if (data.getIdentifierPropertyName() == null) {        //��������
                       type = (EmbeddedComponentType) data.getIdentifierType();
                       String[] columns = data.getIdentifierColumnNames();

                       for (int i = 0; i < columns.length; i++) {
                           result.add( type.getPropertyValue(bo, i, EntityMode.POJO));
                           logger.debug("����"+columns[i]+"��valuleΪ��"  +
                                   type.getPropertyValue(bo, i, EntityMode.POJO));
                       }
                   }else{    //��ֵ����
                       result.add(data.getIdentifier(bo , EntityMode.POJO )) ;
                   }
                   return result;
               } catch (HibernateException e) {
                   logger.error("00000419:ȡBO [" + bo + "] ������ֵʱ�����쳣��", e);
                   List<String> params = new ArrayList<String>();
                   params.add("" + bo);
                   BaseCheckedException ex =
                           new BaseCheckedException("00000419", e);
                   throw ex;
               }


    }

    /**
     * ������������������������ ��
     *
     * @param dao       ���ݷ��ʲ�ӿ�
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
            if (data.getIdentifierPropertyName() == null) {        //��������
                type = (EmbeddedComponentType) data.getIdentifierType();
                /**
                 * �������������Ƕ�ס���Ƕ�ף�ȡֵ���� by ZhangBin   
                 */
                Serializable identifier = data.getIdentifier(bo, EntityMode.POJO);
                if(identifier!=null){
	                String[] columns =  type.getPropertyNames();
	                for (int i = 0; i < columns.length; i++) {
	                    result.put(columns[i], type.getPropertyValue(identifier, i, EntityMode.POJO));
	                }
                }
            }
            else{    //��ֵ����
                result.put(data.getIdentifierColumnNames()[0] , data.getIdentifier(bo , EntityMode.POJO )) ;
            }
            return result;
        } catch (HibernateException e) {
        	 logger.error("00000420:ȡBO [" + bo + "] ����������������������ʱ�����쳣��", e);
             List<String> params = new ArrayList<String>();
             params.add("" + bo);
            throw new BaseCheckedException("00000420", e);
        }


    }

    /**
     * ȡ��BO���������Զ�Ӧ��Type
     *
     * @param dao       ���ݷ��ʲ�ӿ�
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
        	logger.error("00000421:ȡBO [" + boClass + "] ���������Զ�Ӧ��Typeʱ�����쳣��", e);
            List<String> params = new ArrayList<String>();
            params.add("" + boClass);
            BaseCheckedException ex =
                    new BaseCheckedException("00000421", e);
            throw ex;
        }

    }

    /**
     * ȡ��һ��BO���Զ�Ӧ��Hibernate ����
     *
     * @param dao       ���ݷ��ʲ�ӿ�
     * @param boClass      Class BO����
     * @param propertyName String ��������
     * @return Type ���Զ�Ӧ������
     * @throws BaseCheckedException
     */
    public static Type getPropertyType(PersistenceDAO dao, Class<?> boClass, String propertyName)
            throws BaseCheckedException {
        ClassMetadata data = null;
        try {
            data = getClassMetadata(dao, boClass);
            return data.getPropertyType(propertyName);
        } catch (HibernateException e) {
        	logger.error("00000422:ȡBO [" + boClass + "] �����Զ�Ӧ��Hibernate ����ʱ�����쳣��", e);
            List<String> params = new ArrayList<String>();
            params.add("" + boClass);
            BaseCheckedException ex =
                    new BaseCheckedException("00000422", e);
            throw ex;
        }
    }


    /**
     * ȡ��һ��BO�����Ե���ʵֵ
     *
     * @param dao       ���ݷ��ʲ�ӿ�
     * @param bo
     * @param pname   String ��������
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
     * <p>�÷�������ȡ��һ����ʵBO���������ֵ,��Ҫע����ǣ��������������ӵ�б�׼��getter�����������׳��쳣</p>
     *
     * @param dao       ���ݷ��ʲ�ӿ�
     * @param bo   BO������ֵ
     * @return
     * @throws BaseCheckedException
     */
    public static Object getBOIDValue(PersistenceDAO dao, PersistenceObject bo)
            throws BaseCheckedException {
        String idName = getIdName(dao, bo.getClass());
        StringBuffer methodName = new StringBuffer("get");
        methodName.append(changeName(idName));   //ȡ��ID��Ӧ��getter��������

        try {
            logger.debug("��װ������ID����Ϊ��" + methodName);
            if( idName== null ||idName.equals( "")) return  null ;
            Class<?>[] params = null;
            Method method =
                    bo.getClass().getMethod(methodName.toString(), params);

            Object[] params2 = null;
            return method.invoke(bo, params2); //����getter������ȡ������ֵ
        } catch (Exception e) {
        	logger.error("00000423:ȡBO [" + bo + "] ������ֵʱ�����쳣��", e);
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
     * ����һ��BO��class name��ȡ�ø�BO��Ӧ��Table name ��
     *
     * @param dao       ���ݷ��ʲ�ӿ�
     * @param className String ����������
     * @return String ������
     */
    public static String getTableNameByClassName(PersistenceDAO dao, String className) {
        //ǿ��ת�����͡�Hibernate����ṹ�֣�ÿһ��ClassMetadata��ʵ�ֶ��Ǽ�
        // �иó���ģ������������Ͳ��ᷢ������
    	Session session = ((PersistenceDAOImpl)dao).getSession();
        AbstractEntityPersister aep = (AbstractEntityPersister)
                session.getSessionFactory().getClassMetadata(className);

        if(aep==null){   //080424
            logger.error(className+" û��ע�ᵽ�־ò��У�����hbm�����ļ��Ƿ�ע��");
            throw new RuntimeException(className+" û��ע�ᵽ�־ò��У�����hbm�����ļ��Ƿ�ע��");
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
