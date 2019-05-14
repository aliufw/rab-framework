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
 * <P>程序说明：</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-8-18</P>
 *
 */
public class HibernateRelationMetadata {

    //缓存，用来保存metadata信息
    private static Map<String, Object> home = Collections.synchronizedMap(new HashMap<String, Object>());

    private final static LogWritter logger = LogFactory.getLogger(HibernateRelationMetadata.class);

    public static final int ONE_TO_ONE = 1;
    public static final int MANY_TO_ONE = 2;
    public static final int ONE_TO_MANY = 3;
    public static final int MANY_TO_MANY = 4;

    /**
     * 多对多情况时的关联表外键
     */
    private String[] subTableRef; //

    /**
     * 主表类名
     */
    private String mainClassName; //
    
    /**
     * 子表类名
     */
    private String subClassName;  //
    
    /**
     * 关联类型
     */
    private int relationshipType;  //
    
    /**
     * 主键
     */
    private String[] masterKey;  //
    
    /**
     * 外键
     */
    private String[] foreignKey;  //
    
    /**
     * 关联表名称
     */
    private String tableName;  //
    
    /**
     * 当多对多关联时，对应相对子表的名称
     */
    private String subTableName; //
    
    /**
     * 主键值
     */
    private Object mainKeyValue = null;
    
    /**
     * 子键值
     */
    private Object subKeyValue = null;


    /**
     * 创建BO对象多对多关系
     *
     * @param dao       数据访问层接口
     * @throws PersistenceCheckedException
     */
    public void updateM2MRelationship(PersistenceDAO dao)
            throws Exception {
        PreparedStatement ps = null;
        StringBuffer result = new StringBuffer("insert into ");      // 创建insert的SQL
        result.append(getTableName()).append(" (").append(this.getForeignKey())
                .append(" , ").append(this.getMasterKey()) .append(")").append(" values (?,?)");
        logger.debug("将要执行的SQL为：" + result.toString());
        try {
            ps = dao.getConnection().prepareStatement(result.toString());
            List<Object> param = new ArrayList<Object>();
            param.add(this.getMainKeyValue());
            param.add(this.getSubKeyValue());
            PersistenceUtils.prepareSqlParams(ps, param);      //设置参数
            logger.debug("设置的参数为：" + param);
            ps.execute();
        } catch (SQLException e) {
            logger.error("在为创建的PreparedStatement设置参数的时候发生未知异常，交易失败！", e);
//            VHBaseCheckedException ex = new VHBaseCheckedException("2060", e);
            throw e;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                logger.error("关闭PS时发生未知异常", e);
            }
        }


    }

    /**
     * 
     * <p>创建BO对象一对多关系</p>
     *
     * @param dao       数据访问层接口
     * @param subBO
     * @throws Exception
     */
    public void updateO2MRelationship(PersistenceDAO dao, PersistenceObject subBO)
            throws Exception {
        PreparedStatement ps = null;
        StringBuffer result = new StringBuffer("update ");      // 创建更新的SQL，注意，不支持联合主键
        result.append(getTableName()).append(" set  ").append(this.getForeignKey()).append(" = ? ")
                .append(" where ").append(HibernateMetadataUtil.getIdName(dao, subBO.getClass())).append(" = ?");
        logger.debug("将要执行的SQL为：" + result.toString());
        try {
            ps = dao.getConnection().prepareStatement(result.toString());
            List<Object> param = new ArrayList<Object>();
            param.add(this.getMainKeyValue());
            param.add(HibernateMetadataUtil.getBOIDValue(dao, subBO));   //根据子表ID更新关系
            PersistenceUtils.prepareSqlParams(ps, param);      //设置参数
            logger.debug("设置的参数为：" + param);
            ps.execute();
        } catch (SQLException e) {
            logger.error("在为创建的PreparedStatement设置参数的时候发生未知异常，交易失败！", e);
//            VHBaseCheckedException ex =
//                    new VHBaseCheckedException("2060", e);
            throw e;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                logger.error("关闭PS时发生未知异常", e);
            }
        }


    }

    /**
     * 
     * <p>创建一个删除BO关系的对应PreparedStatement</p>
     *
     * @param dao       数据访问层接口
     * @param main 
     * @param sub
     * @throws Exception
     */
    public void executeDeleteRelationship(PersistenceDAO dao, PersistenceObject main, PersistenceObject sub)
            throws Exception {
        List<Object> mainList = HibernateMetadataUtil.getIdValues(dao, main);  //取得主表BO主键信息
        List<Object> subList = HibernateMetadataUtil.getIdValues(dao, sub);  //取得子表BO主键信息

        String sql = createRelationShipDeleteSQL();   //创建SQL语句
        PreparedStatement ps = null;

        try {
            ps = createDeletePreparedStatement(dao.getConnection(), sql, mainList, subList);       //创建要执行的PS
            ps.execute();

        } catch (SQLException e) {
            logger.error("在为创建的PreparedStatement设置参数的时候发生未知异常，交易失败！", e);
//            VHBaseCheckedException ex =
//                    new VHBaseCheckedException("002057", e);
            throw e;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                logger.error("关闭PS的时候发生异常", e);
            }
        }
    }


    /**
     * 
     * <p>创建删除管理所用到的PS</p>
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
            if (relationshipType == HibernateRelationMetadata.MANY_TO_MANY) { //处理多对多的情况
                Iterator<Object> ite = main.iterator();
                while (ite.hasNext()) {
                    Object obj = ite.next() ;
                    logger.debug("对应的参数为：" + obj);
                    params.add(obj);
                }

                ite = sub.iterator();
                while (ite.hasNext()) {
                     Object obj = ite.next() ;
                    logger.debug("对应的参数为：" + obj);
                    params.add(obj);
                }
            } else if (relationshipType == HibernateRelationMetadata.ONE_TO_MANY) {  //处理一对多的情况

                Iterator<Object> ite = sub.iterator();
                while (ite.hasNext()) {
                    Object obj = ite.next() ;
                    logger.debug("对应的参数为：" + obj);
                    params.add(obj);
                }
            }

            PersistenceUtils.prepareSqlParams(ps, params);
            return ps;
        } catch (SQLException e) {
            logger.error("在为创建的PreparedStatement设置参数的时候发生未知异常，交易失败！", e);
//            VHBaseCheckedException ex =
//                    new VHBaseCheckedException("002057", e);
            throw e;
        }


    }

    /**
     * 
     * <p>删除多对多关联情况的所有与主表有关的关联表记录</p>
     *
     * @param dao
     * @throws Exception
     */
    public void deleteAllM2MRelationship(PersistenceDAO dao)
            throws Exception {
        PreparedStatement ps = null;
        StringBuffer result = new StringBuffer("delete from ");      // 创建删除的SQL
        result.append(getTableName()).append(" where ").append(this.getForeignKey()).append(" = ? ");
        logger.debug("将要执行的SQL为：" + result.toString());
        try {
            ps = dao.getConnection().prepareStatement(result.toString());
            List<Object> param = new ArrayList<Object>();
            param.add(this.getMainKeyValue());
            PersistenceUtils.prepareSqlParams(ps, param);      //设置参数
            logger.debug("设置的参数为：" + param);
            ps.execute();
        } catch (SQLException e) {
            logger.error("在为创建的PreparedStatement设置参数的时候发生未知异常，交易失败！", e);
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
                logger.error("关闭PS时发生未知异常", e);
            }
        }


    }

    /**
     * 该方法删除全部的多对多映射关系种的子表BO记录
     *
     * @param dao
     * @throws BaseCheckedException
     */
    public void deleteAllM2MSubBO(PersistenceDAO dao)
            throws Exception {
        PreparedStatement ps = null;
        StringBuffer result = new StringBuffer("delete  from ");      // 创建删除的SQL

        //还没有取得被删除的关联子表名称的时候；
        if (this.getSubTableName() == null ||
                this.getSubTableName().trim().equals("")) {
            this.setSubTableName(HibernateMetadataUtil.getTableNameByClassName(dao,
                    this.getSubClassName()));
        }

        result.append(getSubTableName()).append(" where ").append(this.getForeignKey()).append(" = ?");
        logger.debug("将要执行的SQL为：" + result.toString());
        try {
            ps = dao.getConnection().prepareStatement(result.toString());
            List<Object> param = new ArrayList<Object>();
            param.add(this.getMainKeyValue());
            PersistenceUtils.prepareSqlParams(ps, param);      //设置参数
            logger.debug("设置的参数为：" + param);
            ps.execute();
        } catch (SQLException e) {
            logger.error("在为创建的PreparedStatement设置参数的时候发生未知异常，交易失败！", e);
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
                logger.error("关闭PS时发生未知异常", e);
            }
        }

    }

    /**
     * 删除主表对应的关联子表对象
     *
     * @param dao
     * @throws BaseCheckedException
     */
    public void deleteAllO2MSubBO(PersistenceDAO dao)
            throws Exception {
        PreparedStatement ps = null;
        StringBuffer result = new StringBuffer("delete  from ");      // 创建删除的SQL
        result.append(getTableName()).append(" where ").append(this.getForeignKey()).append(" = ?");
        logger.debug("将要执行的SQL为：" + result.toString());
        try {
            ps = dao.getConnection().prepareStatement(result.toString());
            List<Object> param = new ArrayList<Object>();
            param.add(this.getMainKeyValue());
            PersistenceUtils.prepareSqlParams(ps, param);      //设置参数
            logger.debug("设置的参数为：" + param);
            ps.execute();
        } catch (SQLException e) {
            logger.error("在为创建的PreparedStatement设置参数的时候发生未知异常，交易失败！", e);
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
                logger.error("关闭PS时发生未知异常", e);
            }
        }

    }


    /**
     * 删除一对多情况的主表与关联表的关联关系
     *
     * @param dao
     * @throws BaseCheckedException
     */
    public void deleteAllO2MRelationship(PersistenceDAO dao)
            throws Exception {
        PreparedStatement ps = null;
        StringBuffer result = new StringBuffer("update  ");      // 创建删除的SQL
        result.append(getTableName()).append(" set ").append(this.getForeignKey())
                .append(" = null ").append(" where ").append(this.getForeignKey()).append(" = ?");
        logger.debug("将要执行的SQL为：" + result.toString());
        try {
            ps = dao.getConnection().prepareStatement(result.toString());
            List<Object> param = new ArrayList<Object>();
            param.add(this.getMainKeyValue());
            PersistenceUtils.prepareSqlParams(ps, param);      //设置参数
            logger.debug("设置的参数为：" + param);
            ps.execute();
        } catch (SQLException e) {
            logger.error("在为创建的PreparedStatement设置参数的时候发生未知异常，交易失败！", e);
            throw e;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                logger.error("关闭PS时发生未知异常", e);
            }
        }

    }


    //根据对应的BO映射模式创建对应的SQL
    private String createRelationShipDeleteSQL() {


        StringBuffer result = new StringBuffer();
        logger.debug("将要从" + getTableName() + "表中删除数据");
        if (relationshipType == HibernateRelationMetadata.MANY_TO_MANY) { //处理多对多的情况
            result.append(" delete from  ").append(this.getTableName()).append(" where ");
//            Iterator ite = sub.keySet().iterator();
//            while (ite.hasNext()) {    //添加外键条件
            for(int i = 0 ; i < this.getForeignKey().length ; i++ ){
                result.append( (getForeignKey()[i])).append(" = ? and ");
            }
//            }
//            ite = main.keySet().iterator();
//            while (ite.hasNext()) {      //添加主键属性
            for(int i = 0 ; i < this.getMasterKey().length ;i++){
                result.append(getMasterKey()[i]).append(" = ? ");
                if (i+1 < this.getMasterKey().length ) {
                    result.append(" and ");
                }
            }
//            }

//            result.append(this.getForeignKey()).append(" = ? and ").     //多对多映射表的删除语句
//                    append(this.getMasterKey()).append(" = ?");
            logger.debug("对应的参数为：" + this.getMainKeyValue() + "和" + this.getForeignKey());


        } else if (relationshipType == HibernateRelationMetadata.ONE_TO_MANY) {  //处理一对多的情况
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
            //todo 缺少对一对一和多对一的支持
        }


        logger.debug("将要执行的SQL为:" + result);
        return result.toString();
    }


    public static HibernateRelationMetadata getMetadata(Class<?> mainBO,
                                                        Class<?> subBO) {
        Object result =
                home.get(getName(mainBO.getName(), subBO.getName()));
        logger.debug("取得缓存对象： " + result);
        return (HibernateRelationMetadata) result;
    }

    public static void setMetadata(HibernateRelationMetadata data) {
        logger.debug("保存缓存对象： " + data);
        home.put(getName(data.getMainClassName(),
                data.getSubClassName()), data);
    }

    private static String getName(String mainBO, String subBO) {
        return mainBO + "-" + subBO;

    }

    /**
     * 清空主表子表主键值 ，缓存时用以节省内存 ；
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
