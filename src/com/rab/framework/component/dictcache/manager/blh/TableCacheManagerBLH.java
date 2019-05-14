package com.rab.framework.component.dictcache.manager.blh;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.component.dictcache.ServerCacheManager;
import com.rab.framework.component.dictcache.manager.DictMetaInfo;
import com.rab.framework.component.dictcache.manager.event.TableCacheManagerRequestEvent;
import com.rab.framework.component.dictcache.manager.event.TableCacheManagerResponseEvent;
import com.rab.framework.domain.blh.BaseDomainBLH;


public class TableCacheManagerBLH extends BaseDomainBLH {


	public BaseResponseEvent insert(BaseRequestEvent reqEvent) throws BaseCheckedException{
		TableCacheManagerRequestEvent req = (TableCacheManagerRequestEvent)reqEvent;
		TableCacheManagerResponseEvent resp = new TableCacheManagerResponseEvent();

		String tablename = req.getTableName();
		List<Map<String,String>> datarows = req.getDatarows();
		Connection con = this.domainSession.getPersistenceDAO().getConnection();
		
		DictMetaInfo td = ServerCacheManager.getDictCacheManager().getDictMetaInfos().get(tablename.toLowerCase());
		String sql = "insert into " + tablename;
		sql += "(";
		List<Properties> fields = td.getFields();
		String sqlparams = "";
		for(int i=0; i<fields.size()-1; i++){
			sql += fields.get(i).getProperty("column-name") + ",";
			sqlparams += "?,";
		}
		sql += fields.get(fields.size()-1).getProperty("column-name");
		sqlparams += "?";
		
		sql += ") values (";
		sql += sqlparams;
		sql += ")";
		
		logger.debug("��Ӵ��������: sql = " + sql);
		
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(sql);
			
			for(Map<String,String> datarow : datarows){
				logger.debug("��Ӵ��������: datarow = " + datarow2str(datarow));
				for(int i=1; i<=fields.size(); i++){
					Properties field = fields.get(i-1);
					String columnName = field.getProperty("column-name");
					
					String strData = datarow.get(columnName);
					String dataType = field.getProperty("data-type");
					pstmt = this.setParam(pstmt, i, dataType, strData);

				}

				pstmt.execute();
			}
			
		} catch (Exception e) {
			logger.error("��Ӵ��������ʱ�����쳣��");
			List<String> params = new ArrayList<String>();
			params.add(tablename);
			throw new BaseCheckedException("00000602", params, e); 
		}
		finally{
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					logger.error("��Ӵ�������ݣ��ر� PreparedStatement �����ǳ����쳣��",e);
				}
			}
		}
		
		return resp;
	}
	
	public BaseResponseEvent update(BaseRequestEvent reqEvent) throws BaseCheckedException{
		TableCacheManagerRequestEvent req = (TableCacheManagerRequestEvent)reqEvent;
		TableCacheManagerResponseEvent resp = new TableCacheManagerResponseEvent();

		String tablename = req.getTableName();
		List<Map<String,String>> datarows = req.getDatarows();
		Connection con = this.domainSession.getPersistenceDAO().getConnection();
	
		DictMetaInfo td = ServerCacheManager.getDictCacheManager().getDictMetaInfos().get(tablename.toLowerCase());
		String sql = "update " + tablename + " set ";
		List<Properties> fields = td.getFields();
		for(int i=0; i<fields.size()-1; i++){
			if(fields.get(i).getProperty("is-pk").equalsIgnoreCase("Y")){ //������������
				continue;
			}
			sql += fields.get(i).getProperty("column-name") + "=?,";
		}
		
		if(fields.get(fields.size()-1).getProperty("is-pk").equalsIgnoreCase("Y")){  //������������
			sql = sql.substring(0, sql.length()-1); //�������һ��","�ַ�
		}
		else{
			sql += fields.get(fields.size()-1).getProperty("column-name")+ "=?";
		}
		
		
		sql += " where ";
		
		for(Properties field : fields){
			if(field.getProperty("is-pk").equalsIgnoreCase("Y")){
				sql += field.getProperty("column-name") + "=?";
				break;
			}
		}
		
		
		logger.info("���´��������: sql = " + sql);
		
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(sql);
			
			String pkDataTYpe="";
			String pkValue = "";
			for(Map<String,String> datarow : datarows){
				logger.debug("���´��������: datarow = " + datarow2str(datarow));
				int pointer = 1;
				for(int i=1; i<=fields.size(); i++){
					Properties field = fields.get(i-1);
					
					String columnName = field.getProperty("column-name");
					String strData = datarow.get(columnName);
					String dataType = field.getProperty("data-type");
					
					if(field.getProperty("is-pk").equalsIgnoreCase("Y")){ //������������
						pkValue = strData;
						pkDataTYpe = dataType;
						continue;
					}
					pstmt = this.setParam(pstmt, pointer, dataType, strData);
					pointer ++;
				}

				pstmt = this.setParam(pstmt, pointer, pkDataTYpe, pkValue); //������ֵ
				
				pstmt.execute();
			}
			
		} catch (Exception e) {
			logger.error("���´��������ʱ�����쳣��");
			List<String> params = new ArrayList<String>();
			params.add(tablename);
			throw new BaseCheckedException("00000603", params, e); 
		}
		finally{
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					logger.error("���´�������ݣ��ر� PreparedStatement �����ǳ����쳣��",e);
				}
			}
		}
		
		return resp;
	}
	
	public BaseResponseEvent delete(BaseRequestEvent reqEvent) throws BaseCheckedException{
		TableCacheManagerRequestEvent req = (TableCacheManagerRequestEvent) reqEvent;
		TableCacheManagerResponseEvent resp = new TableCacheManagerResponseEvent();

		String tablename = req.getTableName();
		List<Map<String, String>> datarows = req.getDatarows();
		Connection con = this.domainSession.getPersistenceDAO().getConnection();
		
		DictMetaInfo td = ServerCacheManager.getDictCacheManager().getDictMetaInfos().get(tablename.toLowerCase());
		String sql = "delete from " + tablename + " where ";
		List<Properties> fields = td.getFields();
		
		String pkDataTYpe="";
		String pkColumnName = "";
		for(int i=0; i<fields.size(); i++){
			if(fields.get(i).getProperty("is-pk").equalsIgnoreCase("Y")){ //������������
				sql += fields.get(i).getProperty("column-name") + "=?";
				pkDataTYpe = fields.get(i).getProperty("data-type");
				pkColumnName = fields.get(i).getProperty("column-name");
				break;
			}
		}
		
		logger.info("ɾ�����������: sql = " + sql);
		
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(sql);
			
			for(Map<String,String> datarow : datarows){
				logger.debug("ɾ�����������: datarow = " + datarow2str(datarow));
				String pkValue = datarow.get(pkColumnName);
				
				pstmt = this.setParam(pstmt, 1, pkDataTYpe, pkValue); //������ֵ
				
				pstmt.execute();
			}
			
		} catch (Exception e) {
			logger.error("ɾ�����������ʱ�����쳣��");
			List<String> params = new ArrayList<String>();
			params.add(tablename);
			throw new BaseCheckedException("00000604", params, e); 
		}
		finally{
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					logger.error("ɾ����������ݣ��ر� PreparedStatement �����ǳ����쳣��",e);
				}
			}
		}
		
		return resp;
		
	}
	
	private PreparedStatement setParam(PreparedStatement pstmt, int index, String dataType, String strData) throws Exception{
		if(dataType.equals("string")){ //�ִ�
			pstmt.setString(index, strData);
		}
		else if(dataType.equals("date")){//ʱ��
			Date date = Date.valueOf(strData);
			pstmt.setDate(index, date);
		}
		else if(dataType.equals("number")){//���֣���Ȼ��
			pstmt.setInt(index,Integer.parseInt(strData));
		}
		else if(dataType.equals("float")){//���֣�������
			pstmt.setFloat(index,Float.parseFloat(strData));
		}
		
		return pstmt;
	}
	

	private String datarow2str(Map<String,String> datarow){
		String s = "{";
		Iterator<String> iter = datarow.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			String value = datarow.get(key);
			s += key + " = " + value + ", ";
		}
		s += "}";
		
		return s;
	}
}
