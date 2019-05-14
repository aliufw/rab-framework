package com.rab.framework.component.dictcache;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.dao.PersistenceDAO;

/**
 * 
 * <P>Title: CacheUpdateManager</P>
 * <P>Description: </P>
 * <P>程序说明：更新缓存字典表的版本序号</P>
 * <P>更新缓存字典表的版本序号，用在字典表更新维护后，在字典表更新的BLH中调用</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-10-28</P>
 *
 */
public class CacheUpdateHelper {

	private static final LogWritter logger = LogFactory.getLogger(CacheUpdateHelper.class);

	/**
	 * 
	 * <p>更新字典表注册表中的版本序号</p>
	 *
	 * @param tableName      字典表名称
	 * @param dao            数据访问接口
	 * @param responseEvent  数据返回对象，用户设置数据缓存更新标识
	 */
	public static void setUpdateFlag(String tableName, PersistenceDAO dao,
			BaseResponseEvent responseEvent) throws BaseCheckedException {
		
		tableName = tableName.toLowerCase().trim();
		
		Properties props = (Properties)ApplicationContext.singleton().getValueByKey("codecache");
        String catalogTable = props.getProperty("catalog-table");
		
        PreparedStatement pstmt = null;
		try {
			//1. 检查缓存状态
			String sql = "select bm_mc from " + catalogTable + " where bm_mc=?";
			pstmt = dao.getConnection().prepareStatement(sql);
			pstmt.setString(1, tableName);
			ResultSet rs = pstmt.executeQuery();
			if(!rs.next()){
				logger.error("00000604：更新代码表 " + tableName + " 版本序号时出现异常, 没有找到同该名称对应的缓存表注册信息");
				List<String> params = new ArrayList<String>();
				params.add(tableName);
				throw new BaseCheckedException("00000604", params);
			}
			
			//2. 更新缓存版本标记
			sql = "update " + catalogTable + " set GX_XH = GX_XH + 1 where bm_mc=?";
			logger.debug("更新代码表 " + tableName + " 版本序号: sql = " + sql);

			pstmt = dao.getConnection().prepareStatement(sql);
			pstmt.setString(1, tableName);
			
			pstmt.execute();
			
			responseEvent.setFlushCachedDict(true);
			
		} 
		catch (SQLException e) {
			logger.error("00000605：更新代码表 " + tableName + " 版本序号时出现异常", e);
			List<String> params = new ArrayList<String>();
			params.add(tableName);
			throw new BaseCheckedException("00000605", params, e);
		}
		finally{
			try {
				if(pstmt != null){
					pstmt.close();
				}
				
			} catch (SQLException e) {
			}
		}
	}
}
