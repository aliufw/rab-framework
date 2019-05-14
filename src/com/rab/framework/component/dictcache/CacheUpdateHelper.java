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
 * <P>����˵�������»����ֵ��İ汾���</P>
 * <P>���»����ֵ��İ汾��ţ������ֵ�����ά�������ֵ����µ�BLH�е���</P>
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
	 * <p>�����ֵ��ע����еİ汾���</p>
	 *
	 * @param tableName      �ֵ������
	 * @param dao            ���ݷ��ʽӿ�
	 * @param responseEvent  ���ݷ��ض����û��������ݻ�����±�ʶ
	 */
	public static void setUpdateFlag(String tableName, PersistenceDAO dao,
			BaseResponseEvent responseEvent) throws BaseCheckedException {
		
		tableName = tableName.toLowerCase().trim();
		
		Properties props = (Properties)ApplicationContext.singleton().getValueByKey("codecache");
        String catalogTable = props.getProperty("catalog-table");
		
        PreparedStatement pstmt = null;
		try {
			//1. ��黺��״̬
			String sql = "select bm_mc from " + catalogTable + " where bm_mc=?";
			pstmt = dao.getConnection().prepareStatement(sql);
			pstmt.setString(1, tableName);
			ResultSet rs = pstmt.executeQuery();
			if(!rs.next()){
				logger.error("00000604�����´���� " + tableName + " �汾���ʱ�����쳣, û���ҵ�ͬ�����ƶ�Ӧ�Ļ����ע����Ϣ");
				List<String> params = new ArrayList<String>();
				params.add(tableName);
				throw new BaseCheckedException("00000604", params);
			}
			
			//2. ���»���汾���
			sql = "update " + catalogTable + " set GX_XH = GX_XH + 1 where bm_mc=?";
			logger.debug("���´���� " + tableName + " �汾���: sql = " + sql);

			pstmt = dao.getConnection().prepareStatement(sql);
			pstmt.setString(1, tableName);
			
			pstmt.execute();
			
			responseEvent.setFlushCachedDict(true);
			
		} 
		catch (SQLException e) {
			logger.error("00000605�����´���� " + tableName + " �汾���ʱ�����쳣", e);
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
