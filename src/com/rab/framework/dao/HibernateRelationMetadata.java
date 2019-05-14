package com.rab.framework.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.domain.po.PersistenceObject;

/**
 * 
 * <P>Title: HibernateRelationMetadata</P>
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
public class HibernateRelationMetadata {

    //���棬��������metadata��Ϣ
    private static Map<String, Object> home = Collections.synchronizedMap(new HashMap<String, Object>());

    private final static LogWritter logger = LogFactory.getLogger(HibernateRelationMetadata.class);

    public static final int ONE_TO_ONE = 1;
    public static final int MANY_TO_ONE = 2;
    public static final int ONE_TO_MANY = 3;
    public static final int MANY_TO_MANY = 4;

    /**
     * ��Զ����ʱ�Ĺ��������
     */
    private String[] subTableRef; //

    /**
     * ��������
     */
    private String mainClassName; //
    
    /**
     * �ӱ�����
     */
    private String subClassName;  //
    
    /**
     * ��������
     */
    private int relationshipType;  //
    
    /**
     * ����
     */
    private String[] masterKey;  //
    
    /**
     * ���
     */
    private String[] foreignKey;  //
    
    /**
     * ����������
     */
    private String tableName;  //
    
    /**
     * ����Զ����ʱ����Ӧ����ӱ������
     */
    private String subTableName; //
    
    /**
     * ����ֵ
     */
    private Object mainKeyValue = null;
    
    /**
     * �Ӽ�ֵ
     */
    private Object subKeyValue = null;


    /**
     * ����BO�����Զ��ϵ
     *
     * @param dao       ���ݷ��ʲ�ӿ�
     * @throws PersistenceCheckedException
     */
    public void updateM2MRelationship(PersistenceDAO dao)
            throws Exception {
        PreparedStatement ps = null;
        StringBuffer result = new StringBuffer("insert into ");      // ����insert��SQL
        result.append(getTableName()).append(" (").append(this.getForeignKey())
                .append(" , ").append(this.getMasterKey()) .append(")").append(" values (?,?)");
        logger.debug("��Ҫִ�е�SQLΪ��" + result.toString());
        try {
            ps = dao.getConnection().prepareStatement(result.toString());
            List<Object> param = new ArrayList<Object>();
            param.add(this.getMainKeyValue());
            param.add(this.getSubKeyValue());
            PersistenceUtils.prepareSqlParams(ps, param);      //���ò���
            logger.debug("���õĲ���Ϊ��" + param);
            ps.execute();
        } catch (SQLException e) {
            logger.error("��Ϊ������PreparedStatement���ò�����ʱ����δ֪�쳣������ʧ�ܣ�", e);
//            VHBaseCheckedException ex = new VHBaseCheckedException("2060", e);
            throw e;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                logger.error("�ر�PSʱ����δ֪�쳣", e);
            }
        }


    }

    /**
     * 
     * <p>����BO����һ�Զ��ϵ</p>
     *
     * @param dao       ���ݷ��ʲ�ӿ�
     * @param subBO
     * @throws Exception
     */
    public void updateO2MRelationship(PersistenceDAO dao, PersistenceObject subBO)
            throws Exception {
        PreparedStatement ps = null;
        StringBuffer result = new StringBuffer("update ");      // �������µ�SQL��ע�⣬��֧����������
        result.append(getTableName()).append(" set  ").append(this.getForeignKey()).append(" = ? ")
                .append(" where ").append(HibernateMetadataUtil.getIdName(dao, subBO.getClass())).append(" = ?");
        logger.debug("��Ҫִ�е�SQLΪ��" + result.toString());
        try {
            ps = dao.getConnection().prepareStatement(result.toString());
            List<Object> param = new ArrayList<Object>();
            param.add(this.getMainKeyValue());
            param.add(HibernateMetadataUtil.getBOIDValue(dao, subBO));   //�����ӱ�ID���¹�ϵ
            PersistenceUtils.prepareSqlParams(ps, param);      //���ò���
            logger.debug("���õĲ���Ϊ��" + param);
            ps.execute();
        } catch (SQLException e) {
            logger.error("��Ϊ������PreparedStatement���ò�����ʱ����δ֪�쳣������ʧ�ܣ�", e);
//            VHBaseCheckedException ex =
//                    new VHBaseCheckedException("2060", e);
            throw e;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                logger.error("�ر�PSʱ����δ֪�쳣", e);
            }
        }


    }

    /**
     * 
     * <p>����һ��ɾ��BO��ϵ�Ķ�ӦPreparedStatement</p>
     *
     * @param dao       ���ݷ��ʲ�ӿ�
     * @param main 
     * @param sub
     * @throws Exception
     */
    public void executeDeleteRelationship(PersistenceDAO dao, PersistenceObject main, PersistenceObject sub)
            throws Exception {
        List<Object> mainList = HibernateMetadataUtil.getIdValues(dao, main);  //ȡ������BO������Ϣ
        List<Object> subList = HibernateMetadataUtil.getIdValues(dao, sub);  //ȡ���ӱ�BO������Ϣ

        String sql = createRelationShipDeleteSQL();   //����SQL���
        PreparedStatement ps = null;

        try {
            ps = createDeletePreparedStatement(dao.getConnection(), sql, mainList, subList);       //����Ҫִ�е�PS
            ps.execute();

        } catch (SQLException e) {
            logger.error("��Ϊ������PreparedStatement���ò�����ʱ����δ֪�쳣������ʧ�ܣ�", e);
//            VHBaseCheckedException ex =
//                    new VHBaseCheckedException("002057", e);
            throw e;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                logger.error("�ر�PS��ʱ�����쳣", e);
            }
        }
    }


    /**
     * 
     * <p>����ɾ���������õ���PS</p>
     *
     * @param con
     * @param sql
     * @param main
     * @param sub
     * @return
     * @throws BaseCheckedException
     */
    private PreparedStatement createDeletePreparedStatement(Connection con, String sql, List<Object> main, List<Object> sub)
            throws Exception {
        PreparedStatement ps = null;

        try {
            ps = con.prepareStatement(sql);
            List<Object> params = new ArrayList<Object>(2);
            if (relationshipType == HibernateRelationMetadata.MANY_TO_MANY) { //�����Զ�����
                Iterator<Object> ite = main.iterator();
                while (ite.hasNext()) {
                    Object obj = ite.next() ;
                    logger.debug("��Ӧ�Ĳ���Ϊ��" + obj);
                    params.add(obj);
                }

                ite = sub.iterator();
                while (ite.hasNext()) {
                     Object obj = ite.next() ;
                    logger.debug("��Ӧ�Ĳ���Ϊ��" + obj);
                    params.add(obj);
                }
            } else if (relationshipType == HibernateRelationMetadata.ONE_TO_MANY) {  //����һ�Զ�����

                Iterator<Object> ite = sub.iterator();
                while (ite.hasNext()) {
                    Object obj = ite.next() ;
                    logger.debug("��Ӧ�Ĳ���Ϊ��" + obj);
                    params.add(obj);
                }
            }

            PersistenceUtils.prepareSqlParams(ps, params);
            return ps;
        } catch (SQLException e) {
            logger.error("��Ϊ������PreparedStatement���ò�����ʱ����δ֪�쳣������ʧ�ܣ�", e);
//            VHBaseCheckedException ex =
//                    new VHBaseCheckedException("002057", e);
            throw e;
        }


    }

    /**
     * 
     * <p>ɾ����Զ��������������������йصĹ������¼</p>
     *
     * @param dao
     * @throws Exception
     */
    public void deleteAllM2MRelationship(PersistenceDAO dao)
            throws Exception {
        PreparedStatement ps = null;
        StringBuffer result = new StringBuffer("delete from ");      // ����ɾ����SQL
        result.append(getTableName()).append(" where ").append(this.getForeignKey()).append(" = ? ");
        logger.debug("��Ҫִ�е�SQLΪ��" + result.toString());
        try {
            ps = dao.getConnection().prepareStatement(result.toString());
            List<Object> param = new ArrayList<Object>();
            param.add(this.getMainKeyValue());
            PersistenceUtils.prepareSqlParams(ps, param);      //���ò���
            logger.debug("���õĲ���Ϊ��" + param);
            ps.execute();
        } catch (SQLException e) {
            logger.error("��Ϊ������PreparedStatement���ò�����ʱ����δ֪�쳣������ʧ�ܣ�", e);
//            VHBaseCheckedException ex =
//                    new VHBaseCheckedException("2058", e);
//            ex.addParam(this.getMainKeyValue().toString());
            throw e;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                logger.error("�ر�PSʱ����δ֪�쳣", e);
            }
        }


    }

    /**
     * �÷���ɾ��ȫ���Ķ�Զ�ӳ���ϵ�ֵ��ӱ�BO��¼
     *
     * @param dao
     * @throws BaseCheckedException
     */
    public void deleteAllM2MSubBO(PersistenceDAO dao)
            throws Exception {
        PreparedStatement ps = null;
        StringBuffer result = new StringBuffer("delete  from ");      // ����ɾ����SQL

        //��û��ȡ�ñ�ɾ���Ĺ����ӱ����Ƶ�ʱ��
        if (this.getSubTableName() == null ||
                this.getSubTableName().trim().equals("")) {
            this.setSubTableName(HibernateMetadataUtil.getTableNameByClassName(dao,
                    this.getSubClassName()));
        }

        result.append(getSubTableName()).append(" where ").append(this.getForeignKey()).append(" = ?");
        logger.debug("��Ҫִ�е�SQLΪ��" + result.toString());
        try {
            ps = dao.getConnection().prepareStatement(result.toString());
            List<Object> param = new ArrayList<Object>();
            param.add(this.getMainKeyValue());
            PersistenceUtils.prepareSqlParams(ps, param);      //���ò���
            logger.debug("���õĲ���Ϊ��" + param);
            ps.execute();
        } catch (SQLException e) {
            logger.error("��Ϊ������PreparedStatement���ò�����ʱ����δ֪�쳣������ʧ�ܣ�", e);
//            VHBaseCheckedException ex =
//                    new VHBaseCheckedException("2058", e);
//            ex.addParam(this.getMainKeyValue().toString());
            throw e;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                logger.error("�ر�PSʱ����δ֪�쳣", e);
            }
        }

    }

    /**
     * ɾ�������Ӧ�Ĺ����ӱ����
     *
     * @param dao
     * @throws BaseCheckedException
     */
    public void deleteAllO2MSubBO(PersistenceDAO dao)
            throws Exception {
        PreparedStatement ps = null;
        StringBuffer result = new StringBuffer("delete  from ");      // ����ɾ����SQL
        result.append(getTableName()).append(" where ").append(this.getForeignKey()).append(" = ?");
        logger.debug("��Ҫִ�е�SQLΪ��" + result.toString());
        try {
            ps = dao.getConnection().prepareStatement(result.toString());
            List<Object> param = new ArrayList<Object>();
            param.add(this.getMainKeyValue());
            PersistenceUtils.prepareSqlParams(ps, param);      //���ò���
            logger.debug("���õĲ���Ϊ��" + param);
            ps.execute();
        } catch (SQLException e) {
            logger.error("��Ϊ������PreparedStatement���ò�����ʱ����δ֪�쳣������ʧ�ܣ�", e);
//            VHBaseCheckedException ex =
//                    new VHBaseCheckedException("2058", e);
//            ex.addParam(this.getMainKeyValue().toString());
            throw e;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                logger.error("�ر�PSʱ����δ֪�쳣", e);
            }
        }

    }


    /**
     * ɾ��һ�Զ�����������������Ĺ�����ϵ
     *
     * @param dao
     * @throws BaseCheckedException
     */
    public void deleteAllO2MRelationship(PersistenceDAO dao)
            throws Exception {
        PreparedStatement ps = null;
        StringBuffer result = new StringBuffer("update  ");      // ����ɾ����SQL
        result.append(getTableName()).append(" set ").append(this.getForeignKey())
                .append(" = null ").append(" where ").append(this.getForeignKey()).append(" = ?");
        logger.debug("��Ҫִ�е�SQLΪ��" + result.toString());
        try {
            ps = dao.getConnection().prepareStatement(result.toString());
            List<Object> param = new ArrayList<Object>();
            param.add(this.getMainKeyValue());
            PersistenceUtils.prepareSqlParams(ps, param);      //���ò���
            logger.debug("���õĲ���Ϊ��" + param);
            ps.execute();
        } catch (SQLException e) {
            logger.error("��Ϊ������PreparedStatement���ò�����ʱ����δ֪�쳣������ʧ�ܣ�", e);
            throw e;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                logger.error("�ر�PSʱ����δ֪�쳣", e);
            }
        }

    }


    //���ݶ�Ӧ��BOӳ��ģʽ������Ӧ��SQL
    private String createRelationShipDeleteSQL() {


        StringBuffer result = new StringBuffer();
        logger.debug("��Ҫ��" + getTableName() + "����ɾ������");
        if (relationshipType == HibernateRelationMetadata.MANY_TO_MANY) { //�����Զ�����
            result.append(" delete from  ").append(this.getTableName()).append(" where ");
//            Iterator ite = sub.keySet().iterator();
//            while (ite.hasNext()) {    //����������
            for(int i = 0 ; i < this.getForeignKey().length ; i++ ){
                result.append( (getForeignKey()[i])).append(" = ? and ");
            }
//            }
//            ite = main.keySet().iterator();
//            while (ite.hasNext()) {      //�����������
            for(int i = 0 ; i < this.getMasterKey().length ;i++){
                result.append(getMasterKey()[i]).append(" = ? ");
                if (i+1 < this.getMasterKey().length ) {
                    result.append(" and ");
                }
            }
//            }

//            result.append(this.getForeignKey()).append(" = ? and ").     //��Զ�ӳ����ɾ�����
//                    append(this.getMasterKey()).append(" = ?");
            logger.debug("��Ӧ�Ĳ���Ϊ��" + this.getMainKeyValue() + "��" + this.getForeignKey());


        } else if (relationshipType == HibernateRelationMetadata.ONE_TO_MANY) {  //����һ�Զ�����
            result.append(" update  ").append(this.getTableName()).append(" set ");
            result.append(this.getForeignKey()).append(" = null ").append(" where ");
//            Iterator ite = sub.keySet().iterator();
            for(int i = 0 ; i < this.getForeignKey().length ;i++){
                result.append(getForeignKey()[i]).append(" = ? ");
                if (i+1 < this.getForeignKey().length ) {
                    result.append(" and ");
                }
            }
        } else {
            //todo ȱ�ٶ�һ��һ�Ͷ��һ��֧��
        }


        logger.debug("��Ҫִ�е�SQLΪ:" + result);
        return result.toString();
    }


    public static HibernateRelationMetadata getMetadata(Class<?> mainBO,
                                                        Class<?> subBO) {
        Object result =
                home.get(getName(mainBO.getName(), subBO.getName()));
        logger.debug("ȡ�û������ " + result);
        return (HibernateRelationMetadata) result;
    }

    public static void setMetadata(HibernateRelationMetadata data) {
        logger.debug("���滺����� " + data);
        home.put(getName(data.getMainClassName(),
                data.getSubClassName()), data);
    }

    private static String getName(String mainBO, String subBO) {
        return mainBO + "-" + subBO;

    }

    /**
     * ��������ӱ�����ֵ ������ʱ���Խ�ʡ�ڴ� ��
     */
    public void clean() {
        this.mainKeyValue = null;
        this.subKeyValue = null;

    }


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final HibernateRelationMetadata that
                = (HibernateRelationMetadata) o;

        if (mainClassName != null ? !mainClassName.equals(that.mainClassName) : that.mainClassName != null) {
            return false;
        }
        if (subClassName != null ? !subClassName.equals(that.subClassName) : that.subClassName != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = (mainClassName != null ? mainClassName.hashCode() : 0);
        result = 29 * result + (subClassName != null ? subClassName.hashCode() : 0);
        return result;
    }


    public String toString() {
        StringBuffer result = new StringBuffer("in HibernateRelation Medata \n");
        result.append("main class type : ").append(this.getMainClassName()).append("\n");
        result.append("sub class type : ").append(this.getSubClassName()).append("\n");
        result.append("relationship type : ").append(this.getRelationshipType()).append("\n");
        result.append("master key : ").append(this.getMasterKey()).append("\n");
        result.append("ForeignKey value: ").append(this.getSubKeyValue()).append("\n");
        result.append("foreign key : ").append(this.getForeignKey()).append("\n");
        result.append(" master key value : ").append(this.getMainKeyValue()).append("\n");
        result.append("table name : ").append(this.getTableName()).append("\n");
        return result.toString();

    }

    public String[] getSubTableRef() {
        return subTableRef;
    }

    public void setSubTableRef(String[] subTableRef) {
        this.subTableRef = subTableRef;
    }

    public int getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(int relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String[] getMasterKey() {
        return masterKey;
    }

    public void setMasterKey(String[] masterKey) {
        this.masterKey = masterKey;
    }

    public String[] getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(String[] foreignKey) {
        this.foreignKey = foreignKey;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Object getMainKeyValue() {
        return mainKeyValue;
    }

    public void setMainKeyValue(Object mainKeyValue) {
        this.mainKeyValue = mainKeyValue;
    }

    public Object getSubKeyValue() {
        return subKeyValue;
    }

    public void setSubKeyValue(Object subKeyValue) {
        this.subKeyValue = subKeyValue;
    }

    public String getMainClassName() {
        return mainClassName;
    }

    public void setMainClassName(String mainclassName) {
        this.mainClassName = mainclassName;
    }

    public String getSubClassName() {
        return subClassName;
    }

    public void setSubClassName(String subClassName) {
        this.subClassName = subClassName;
    }

    public String getSubTableName() {
        return subTableName;
    }

    public void setSubTableName(String subTableName) {
        this.subTableName = subTableName;
    }
    
}
