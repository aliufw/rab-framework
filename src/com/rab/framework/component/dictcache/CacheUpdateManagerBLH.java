package com.rab.framework.component.dictcache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.domain.blh.BaseDomainBLH;

/**
 * 
 * <P>Title: CacheManagerHandler</P>
 * <P>Description: </P>
 * <P>程序说明：代码表加载管理服务器端逻辑,主要完成数据的加载和更新操作.</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-16</P>
 *
 */
public class CacheUpdateManagerBLH extends BaseDomainBLH {
    /**
     * 日志记录对象  
     */
	protected static final LogWritter log = LogFactory.getLogger(CacheUpdateManagerBLH.class);

//	public BaseResponseEvent execute(BaseRequestEvent reqEvent) throws VHBaseCheckedException {
//        CacheReqEvent req = (CacheReqEvent)reqEvent;
//
//        Connection con = this.domainSession.getPersistenceDAO().getConnection();
//
//        String method = req.getLoadMethod();
//        CacheResEvent res = new CacheResEvent();
//
//        Map<String,CacheTable> ret = null;
//        try {
//            if(CacheManager.SERVER_METHOD_LOADALL.equals(method)){
//                ret = loadall(req, con);
//            }
//            else if(CacheManager.SERVER_METHOD_LOADONE.equals(method)){
//                ret = loadone(req, con);
//            }
//            else if(CacheManager.SERVER_METHOD_LOADWITHSQL.equals(method)){
//                ret = loadwithsql(req, con);
//            }
//            else if(CacheManager.SERVER_METHOD_UPDATE.equals(method)){
//                ret = update(req, con);
//            }
//
//        } catch (Exception e) {
//            log.error("读取缓存数据时出现异常！", e);
//        }
//        finally{
//            if (con!=null) {
//                try {
//                    con.close();
//                } catch (SQLException e) {
//                }
//            }
//        }
//
//        res.setCacheTables(ret);
//
//        return res;
//    }

    /**
     * 加载全部数据
     * 
     * @param req 请求对象CacheReqEvent
     * 
     * @return 加载的数据
     */
    public CacheResEvent loadall(BaseRequestEvent reqEvent) throws BaseCheckedException{
    	CacheResEvent res = new CacheResEvent();
    	Connection con = this.domainSession.getPersistenceDAO().getConnection();
    	
        Map<String,CacheTable> dataSet = new HashMap<String,CacheTable>();
        String sqlCatalog = null;
        
        Properties props = (Properties)ApplicationContext.singleton().getValueByKey("codecache");
        String catalogTable = props.getProperty("catalog-table");
        String selectSQL = null;
        Statement stmtDetail = null;
        ResultSet rsCatalog = null;
        Statement stmt = null;
        try {
            stmtDetail = con.createStatement();
            sqlCatalog = "select * from " + catalogTable;
            stmt = con.createStatement();
            rsCatalog = stmt.executeQuery(sqlCatalog);
            while (rsCatalog.next()) {
                String tableName = rsCatalog.getString("bm_mc");
                int version = rsCatalog.getInt("gx_xh");
                int cacheType = rsCatalog.getInt("cacheType");
                String orderByCol = rsCatalog.getString("orderby");
                String descFlag = rsCatalog.getString("descbj");
                String codeName = rsCatalog.getString("codeName");
                String valueName = rsCatalog.getString("valueName");
                tableName = tableName.toUpperCase();  //表名大写

                CacheTable ct = new CacheTable();
                ct.setTableName(tableName);
                ct.setCacheType(cacheType);
                ct.setVersion(version);
                ct.setKeyColName(codeName); 
                ct.setValueColName(valueName);
                ct.setOrderByCol(orderByCol);
                ct.setDescFlag(descFlag);
                
                if (cacheType == CacheTable.CACHE_TYPE_DB) {
                    dataSet.put(tableName, ct);
                    continue;
                }
                selectSQL = buildSelectSQL(tableName, null, orderByCol, descFlag, null);

                ResultSet rsDetail = null;
                try {
                    rsDetail = stmtDetail.executeQuery(selectSQL);
                    List<Map<String,Object>> table = this.createCacheTable(rsDetail);
                    ct.setCacheData(table);
                    ct.setSize(table.size());
                    dataSet.put(tableName, ct);
                    log.debug("从数据库中读取代码表： tableName = " + ct.getTableName());
                } catch (Exception e) {
                    log.error("读取缓存数据时出现异常！tablename = " + tableName, e);
                    //continue;
                } finally {
                    closeResultSet(rsDetail);
                }
            }
        } catch (Exception e) {
            log.error("读取缓存数据时出现异常！执行的SQL操作为：" + sqlCatalog, e);
            
        } finally {
            closeStatement(stmtDetail);
            closeResultSet(rsCatalog);
            closeStatement(stmt);
        }

        res.setCacheTables(dataSet);
        return res;
    }

    private void closeStatement(Statement stmtDetail) {
        if (stmtDetail != null) {
            try {
                stmtDetail.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeResultSet(ResultSet rsCatalog) {
        if (rsCatalog != null) {
            try {
                rsCatalog.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

	private String buildSelectSQL(String tableName, List<CacheFilter> filters, String orderby, String descbj, String whereSQl) {
        StringBuffer buffer;
        buffer = new StringBuffer("select * from ").append(tableName);
        if(filters != null && filters.size() > 0){
            buffer.append(" where 1=1");
            for (int i = 0; i < filters.size(); i++) {
                CacheFilter cf = filters.get(i);
                String fieldName = cf.getFieldName();
                if(cf.getFilterOperator().equals(CacheFilter.FILTER_OPERATOR_EQUAL)
                        || cf.getFilterOperator().equals(CacheFilter.FILTER_OPERATOR_NOT_EQUAL)){
                    buffer.append(" and ").append(fieldName).append(cf.getFilterOperator()).append("?");
                } else if(cf.getFilterOperator().equals(CacheFilter.FILTER_OPERATOR_IN)
                        || (cf.getFilterOperator().equals(CacheFilter.FILTER_OPERATOR_NOT_IN))){
                	List<String> value = (List)cf.getFieldValue(); //数据类型可能不对，需要真实数据检验一下20100616
                    buffer.append(" and ").append(fieldName).append(" ").append(cf.getFilterOperator()).append(" (");
                    for(int k=0; k<value.size()-1; k++){
                        buffer.append("?,");
                    }
                    if (value.size()>0) {
                        buffer.append("?");
                    }
                    buffer.append(")");
                }
            }
        }else if(whereSQl!=null){
            buffer.append(" ").append(whereSQl).append(" ");
        }

        if (orderby != null && !("").equals(orderby.trim())) {
            buffer.append(" order by ").append(orderby).append(" ");
            if (descbj != null && descbj.equals("1")) {
                buffer.append("desc");
            }
        }
        log.debug("代码表查询的sql语句是：" + buffer);
        return buffer.toString();
    }

    private List<Map<String,Object>> createCacheTable(ResultSet rs) throws Exception{
        List<Map<String,Object>> table = new ArrayList<Map<String,Object>>();
        ResultSetMetaData metaData = rs.getMetaData();
        while(rs.next()){
            Map<String,Object> row = new HashMap<String,Object>();
            for(int i=1; i<=metaData.getColumnCount(); i++){
                String columnTypeName = metaData.getColumnTypeName(i);
                if("timestamp".equalsIgnoreCase(columnTypeName)){
                    continue;
                }
                String colummName = metaData.getColumnName(i).toUpperCase();
                Object data = rs.getObject(colummName);
                row.put(colummName, data);
            }
            table.add(row);
        }

        return table;
    }

    /**
     * 加载单个数据表,主要用于不从缓存而直接从应用服务器加载缓存数据
     *
     * @param req 请求对象CacheReqEvent
     * 
     * @return 加载的数据
     */
    public CacheResEvent loadone(BaseRequestEvent reqEvent) throws BaseCheckedException{
    	CacheReqEvent req = (CacheReqEvent)reqEvent;
    	CacheResEvent res = new CacheResEvent();
    	Connection con = this.domainSession.getPersistenceDAO().getConnection();

        Map<String,CacheTable> dataSet = new HashMap<String,CacheTable>();
        Map<String,CacheElement> cacheElements = req.getCacheElements();
        String sqlDetail = null;

        CacheElement ce = null;
        Iterator<CacheElement> iter = cacheElements.values().iterator();
        if (iter.hasNext()) {
            ce = (CacheElement) iter.next();
        }

        ResultSet rsDetail = null;
        PreparedStatement pstmt = null;
        try {
            String tableName = ce.getTableName().toUpperCase();
            
            if(!this.checkCachedTable(tableName, con)){
            	throw new RuntimeException("需要查询的表 " + tableName + " 在系统代码表缓存中没有注册！");
            }
            
            //从主表中取字典表的元数据
            Properties props = (Properties)ApplicationContext.singleton().getValueByKey("codecache");
            String catalogTable = props.getProperty("catalog-table");
            String sql = "select * from " + catalogTable + " where bm_mc = '" + tableName.toUpperCase() + "'";
            Statement stmt = con.createStatement();
            ResultSet rsCatalog = stmt.executeQuery(sql);
            rsCatalog.next();
            int version = rsCatalog.getInt("gx_xh");
            int cacheType = rsCatalog.getInt("cacheType");
            String orderByCol = rsCatalog.getString("orderby");
            String descFlag = rsCatalog.getString("descbj");
            String codeName = rsCatalog.getString("codeName");
            String valueName = rsCatalog.getString("valueName");
            tableName = tableName.toUpperCase();  //表名大写

            //创建缓存字典表对象
            CacheTable ct = new CacheTable();
            ct.setTableName(tableName);
            ct.setCacheType(cacheType);
            ct.setVersion(version);
            ct.setKeyColName(codeName);
            ct.setValueColName(valueName);
            ct.setOrderByCol(orderByCol);
            ct.setDescFlag(descFlag);

            //取字典表数据
            List<CacheFilter> filters = ce.getFilters();
            sqlDetail = this.buildSelectSQL(tableName, filters, orderByCol, descFlag, null);// "select * from " + tableName;
            pstmt = con.prepareStatement(sqlDetail);
            this.setParamsValues(pstmt, filters);
            rsDetail = pstmt.executeQuery();
            List<Map<String,Object>> table = this.createCacheTable(rsDetail);
            
            //将数据放入缓存字典表对象中
            ct.setCacheData(table);
            ct.setSize(table.size());
            
            //返回数据
            dataSet.put(tableName, ct);

            log.debug("从数据库中读取代码表： tableName = " + ct.getTableName());
        } 
        catch (RuntimeException e) {
            log.error("读取缓存数据时出现异常！" + e.getMessage(), e);
        } 
        catch (Exception e) {
            log.error("读取缓存数据时出现异常！执行的SQL操作为：" + sqlDetail, e);
        } 
        finally {
            this.closeResultSet(rsDetail);
            this.closeStatement(pstmt);
        }

        res.setCacheTables(dataSet);
        return res;

    }

    private void setParamsValues(PreparedStatement pstmt, List<CacheFilter> filters) throws SQLException {
        if (filters==null) {
            return;
        }
        int index = 1;
        for (int i = 0; i < filters.size(); i++) {
            CacheFilter cf = (CacheFilter)filters.get(i);
            if(cf.getFilterOperator().equals(CacheFilter.FILTER_OPERATOR_EQUAL)
                    || cf.getFilterOperator().equals(CacheFilter.FILTER_OPERATOR_NOT_EQUAL)){
                Object value = cf.getFieldValue();
                this.judgeAndSetValue(pstmt,value,index);
                index++;
            } else if(cf.getFilterOperator().equals(CacheFilter.FILTER_OPERATOR_IN)
                    || (cf.getFilterOperator().equals(CacheFilter.FILTER_OPERATOR_NOT_IN))){
                List<Object> value = (List)cf.getFieldValue(); //数据类型可能不对，需要真实数据检验一下！20100616
                for (int j = 0; j < value.size(); j++) {
                    Object lsvalue = value.get(j);
                    this.judgeAndSetValue(pstmt,lsvalue,index);
                    index++;
                }
            }
        }
    }

    private void judgeAndSetValue(PreparedStatement pstmt, Object value, int index) throws SQLException {
        if(value instanceof Integer){
            int intValue = ((Integer)value).intValue();
            pstmt.setInt(index,intValue);
        }else if(value instanceof Double){
            double doubleValue = ((Double)value).doubleValue();
            pstmt.setDouble(index,doubleValue);
        }else if(value instanceof String){
            pstmt.setString(index,(String) value);
        }else{
            pstmt.setObject(index,value); //只处理这三种
        }
    }

    /**
     * 加载单个数据表,主要用于不从缓存而直接从应用服务器加载缓存数据
     *
     * @param req 请求对象CacheReqEvent
     * @return 加载的数据
     */
    public CacheResEvent loadwithsql(BaseRequestEvent reqEvent) throws BaseCheckedException{
    	CacheReqEvent req = (CacheReqEvent)reqEvent;
    	CacheResEvent res = new CacheResEvent();
    	Connection con = this.domainSession.getPersistenceDAO().getConnection();
        Map<String,CacheTable> dataSet = new HashMap<String,CacheTable>();
        String tableName = null;
        String sqlWhere = null;
        String sqlDetail = null;
        ResultSet rsDetail = null;
        Statement stmt = null;
        try {
            tableName = req.getTableName().toUpperCase();
            if(!this.checkCachedTable(tableName, con)){
            	throw new RuntimeException("需要查询的表 " + tableName + " 在系统代码表缓存中没有注册！");
            }
            
            //从主表中取字典表的元数据
            Properties props = (Properties)ApplicationContext.singleton().getValueByKey("codecache");
            String catalogTable = props.getProperty("catalog-table");
            String sql = "select * from " + catalogTable + " where bm_mc = '" + tableName.toUpperCase() + "'";
            stmt = con.createStatement();
            ResultSet rsCatalog = stmt.executeQuery(sql);
            rsCatalog.next();
            int version = rsCatalog.getInt("gx_xh");
            int cacheType = rsCatalog.getInt("cacheType");
            String orderByCol = rsCatalog.getString("orderby");
            String descFlag = rsCatalog.getString("descbj");
            String codeName = rsCatalog.getString("codeName");
            String valueName = rsCatalog.getString("valueName");
            tableName = tableName.toUpperCase();  //表名大写

            //创建缓存字典表对象
            CacheTable ct = new CacheTable();
            ct.setTableName(tableName);
            ct.setCacheType(cacheType);
            ct.setVersion(version);
            ct.setKeyColName(codeName);
            ct.setValueColName(valueName);
            ct.setOrderByCol(orderByCol);
            ct.setDescFlag(descFlag);

            //取字典表数据
            sqlWhere = req.getSqlWhere();
            sqlDetail = this.buildSelectSQL(tableName, null, orderByCol, descFlag, sqlWhere);
            stmt = con.createStatement();
            rsDetail = stmt.executeQuery(sqlDetail);
            List<Map<String,Object>> table = this.createCacheTable(rsDetail);
            
          //将数据放入缓存字典表对象中
            ct.setCacheData(table);
            ct.setSize(table.size());
            
            //返回数据
            dataSet.put(tableName, ct);

            log.debug("从数据库中读取代码表： tableName = " + ct.getTableName());
        } 
        catch (RuntimeException e) {
            log.error("读取缓存数据时出现异常！" + e.getMessage(), e);
        } 
        catch (Exception e) {
            log.error("读取缓存数据时出现异常！执行的SQL操作为：" + sqlDetail, e);
        } 
        finally {
            this.closeResultSet(rsDetail);
            this.closeStatement(stmt);
        }

        res.setCacheTables(dataSet);
        return res;

    }

    /**
     * 更新已经缓存的数据
     *
     * @param req 请求对象CacheReqEvent
     * @return 需要更新的缓存数据
     */
    public CacheResEvent update(BaseRequestEvent reqEvent) throws BaseCheckedException{
    	CacheReqEvent req = (CacheReqEvent)reqEvent;
    	CacheResEvent res = new CacheResEvent();
    	Connection con = this.domainSession.getPersistenceDAO().getConnection();
   	
        Map<String,CacheTable> dataSet = new HashMap<String,CacheTable>();
        Properties props = (Properties)ApplicationContext.singleton().getValueByKey("codecache");
        String bm_mc = props.getProperty("catalog-table");

        Map<String,CacheElement> cacheElements = req.getCacheElements();
        Statement stmtDetail = null;
        ResultSet rsCatalog = null;
        Statement stmt = null;
        try {
            stmtDetail = con.createStatement();
            String sqlCatalog = "select * from " + bm_mc;
            stmt = con.createStatement();
            rsCatalog = stmt.executeQuery(sqlCatalog);
            while (rsCatalog.next()) {
                String tableName = rsCatalog.getString("bm_mc");
//                int version = rsCatalog.getInt("gx_xh");
//                int cacheType = rsCatalog.getInt("cacheType");
//                String orderby = rsCatalog.getString("orderby");
//                String descbj = rsCatalog.getString("descbj");
                
                int version = rsCatalog.getInt("gx_xh");
                int cacheType = rsCatalog.getInt("cacheType");
                String orderByCol = rsCatalog.getString("orderby");
                String descFlag = rsCatalog.getString("descbj");
                String codeName = rsCatalog.getString("codeName");
                String valueName = rsCatalog.getString("valueName");
                tableName = tableName.toUpperCase();  //表名大写
                
               
                CacheElement ce = (CacheElement) cacheElements.get(tableName);
                if (ce != null) {
                    if (version <= ce.getVersion()) {
                        continue;
                    }
                }
                //创建缓存字典表对象
                CacheTable ct = new CacheTable();
                ct.setTableName(tableName);
                ct.setCacheType(cacheType);
                ct.setVersion(version);
                ct.setKeyColName(codeName);
                ct.setValueColName(valueName);
                ct.setOrderByCol(orderByCol);
                ct.setDescFlag(descFlag);

                //取字典表数据
                if (cacheType == CacheTable.CACHE_TYPE_DB) {
                    dataSet.put(tableName, ct);
                    continue;
                }
                String sqlDetail = this.buildSelectSQL(tableName, null, orderByCol, descFlag, null);
                ResultSet rsDetail = null;
                try {
                    rsDetail = stmtDetail.executeQuery(sqlDetail);
                    List<Map<String,Object>> table = this.createCacheTable(rsDetail);
                    ct.setCacheData(table);
                    ct.setSize(table.size());
                    dataSet.put(tableName, ct);
                    log.debug("加载了缓存代码表： tableName = " + ct.getTableName());
                } catch (Exception e) {
                    log.error("读取缓存数据时出现异常！", e);
                    //continue;
                } finally {
                    this.closeResultSet(rsDetail);
                }
            }
        } catch (Exception e) {
            log.error("读取缓存数据时出现异常！表名称为： " + bm_mc, e);
        } finally {
            this.closeStatement(stmtDetail);
            this.closeResultSet(rsCatalog);
            this.closeStatement(stmt);
        }

        res.setCacheTables(dataSet);
        
        return res;
    }

//    public Connection getConnection() {
//        Connection con = null;
//        try {
//            Class.forName("oracle.jdbc.OracleDriver");
//            con = DriverManager.getConnection(
//                    "jdbc:oracle:thin:@10.10.13.36:1521:simprj", "lfw",
//                    "lfw");
//        } catch (Exception e) {
//            log.error("在取得数据库连接的时候出错" , e);
//        }
//
//        return con;
//    }

//    private String[] getOrderbyInfo(String tableName,Connection con){
//        String [] twoString = new String[2];
//        //从缓存表信息中取得排序信息
//        Properties props = (Properties)ApplicationContext.singleton().getValueByKey("codecache");
//        String hcbxxs = props.getProperty("catalog-table");
//
//        if (hcbxxs!=null&&!hcbxxs.equals("")) {
//            ResultSet rsCatalog = null;
//            Statement stmt = null;
//            String sqlCatalog = null;
//            try {
//                sqlCatalog = "select orderby, descbj from " + hcbxxs + " where BM_MC='" + tableName + "'";
//
//                stmt = con.createStatement();
//                rsCatalog = stmt.executeQuery(sqlCatalog);
//                if (rsCatalog.next()) {
//                    twoString[0] = rsCatalog.getString("orderby");
//                    twoString[1] = rsCatalog.getString("descbj");
//                }
//            } catch (SQLException e) {
//            	log.error("代码表缓存管理错误：sqlCatalog = " + sqlCatalog, e);
//            } 
//            finally {
//                this.closeResultSet(rsCatalog);
//                this.closeStatement(stmt);
//            }
//        }
//        return twoString;
//    }

    /**
     * 检查当前需要查询的表是否是代码表
     * 
     * @param tablename 待查询的表名称
     * @param con 数据库连接
     * @return  是注册缓存的代码表，返回true， 反之，返回false
     */
    private boolean checkCachedTable(String tablename, Connection con){
        Properties props = (Properties)ApplicationContext.singleton().getValueByKey("codecache");
        String catalogTable = props.getProperty("catalog-table");

    	tablename = tablename.trim();
    	tablename = tablename.toUpperCase(); //转大写！
    	String sql = "select * from " + catalogTable + " where bm_mc='" + tablename + "'";
        ResultSet rs = null;
        Statement stmt = null;
        
        try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			if(rs.next()){
				return true;
			}
		} 
        catch (SQLException e) {
			log.error("代码表缓存管理错误：sql = " + sql, e);
		} 
		finally {
            this.closeResultSet(rs);
            this.closeStatement(stmt);
        }

    	return false;
    }
}

