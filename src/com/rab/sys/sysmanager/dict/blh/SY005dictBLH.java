package com.rab.sys.sysmanager.dict.blh;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.lang.xwork.StringUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import sun.jdbc.rowset.CachedRowSet;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.dto.event.DataRequestEvent;
import com.rab.framework.comm.dto.event.DataResponseEvent;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.component.dictcache.CacheTable;
import com.rab.framework.component.dictcache.ServerCacheManager;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.dao.PersistenceUtils;
import com.rab.framework.domain.blh.BaseDomainBLH;
import com.rab.sys.sysmanager.dict.model.DictVO;

public class SY005dictBLH extends BaseDomainBLH {

	private static final String DICT_META_FILE = "dictmeta.xml";
	
	
	private static final LogWritter logger = LogFactory
			.getLogger(SY005dictBLH.class);

	public BaseResponseEvent initHcbxxList(BaseRequestEvent reqEvent)
			throws BaseCheckedException {

		DataResponseEvent res = new DataResponseEvent();
		try{
			ServerCacheManager manager = (ServerCacheManager)ServerCacheManager.getDictCacheManager();
			Map<String,CacheTable> codeCachePool = manager.getCodeCachePool();
			List<Map<String,Object>> list = this.getTableList();
			if(codeCachePool!=null){
				for(ListIterator<Map<String, Object>> iter=list.listIterator();iter.hasNext();){
					Map<String,Object> mp = iter.next();
					
					if(!codeCachePool.containsKey((StringUtils.trimToEmpty((String)mp.get("code"))).toUpperCase())){
						iter.remove();
					}
				}
			}
			
			res.addCombo("tables", list);
		}catch(Exception e){
			throw new BaseCheckedException("01005004", e);
		}
		return res;
	}
	
	
	public BaseResponseEvent initDictInfo(BaseRequestEvent reqEvent)
	throws BaseCheckedException {

		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		
		try{
			String tableName = (String)req.getAttr("tableName");
			DictVO vo = this.getTableInfo(tableName);
			String widgetStr = this.createTableWidget(vo);
			if(StringUtils.isNotBlank(widgetStr)){
				PersistenceDAO dao = this.domainSession.getPersistenceDAO();
				String sql = "select * from "+tableName;
				CachedRowSet crs = dao.queryToCachedRowSetBySQL(sql, null);
				
				String tableStr = this.getTableInfo(vo);
				
				res.addAttr("tableStr", tableStr);
				res.addAttr("widgetStr", widgetStr);
				res.addTable("list", crs);
			}
		}catch (Exception e) {
			throw new BaseCheckedException("01005005", e);
		}
		
		return res;
	}
	
	public BaseResponseEvent save(BaseRequestEvent reqEvent)
	throws Exception {

		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		
		List<Map<String,Object>> addList = req.getInsertTableData("list");
		List<Map<String,Object>> updList = req.getUpdateTableData("list");
		List<Map<String,Object>> delList = req.getDeleteTableData("list");
		
		String tableName = (String)req.getAttr("tableName");
		String tableStr = (String)req.getAttr("tableStr");
		
		String[] pk = tableStr.split("__");
		String[] pks = null;
		String tr ="";
		
		if(pk.length>0){
			tr = pk[0];
			pks = pk[1].split(",");
		}			
		String[] st = tr.split("#");
		Map<String,String> map = new HashMap<String,String>();
		for(int i=0;i<st.length;i++){
			String[] col = st[i].split("@");
			if(col.length>1){
				map.put(col[0], col[1]);
			}
		}
		
		StringBuffer addSql = new StringBuffer("insert into "+tableName+" (");
		StringBuffer updSql = new StringBuffer("update "+tableName+" set ");
		StringBuffer delSql = new StringBuffer("delete from "+tableName+" where ");
		
		StringBuffer addS = new StringBuffer("");
		String tp = "";
		List<String> pms = new ArrayList<String>();
		for(Iterator<String> it = map.keySet().iterator();it.hasNext();){
			tp = it.next();
			addSql.append(tp+",");
			addS.append("?,");
			updSql.append(tp+" = ? ,");
			
			delSql.append(tp+" = ? and ");
			pms.add(tp);
		}
		addSql.delete(addSql.length()-1, addSql.length());
		addS.delete(addS.length()-1, addS.length());
		addSql.append(") values (").append(addS+")");
		updSql.delete(updSql.length()-1, updSql.length());
		updSql.append(" where ");
		for(int i=0;i<pks.length;i++){
			updSql.append(pks[i]).append(" = ? and ");
		}
		updSql.delete(updSql.length()-4, updSql.length());
		
		delSql.delete(delSql.length()-4, delSql.length());
		
		boolean result =true;
		if(addList!=null && addList.size()>0){
			try{
				PreparedStatement ps = dao.getConnection().prepareStatement(addSql.toString());
				for(int i=0;i<addList.size();i++){
					List<Object> list = this.getParams(addList.get(i),pms);
					ps = PersistenceUtils.prepareSqlParams(ps, list);
					result = ps.execute();
				}
			}catch(Exception e){
				throw new BaseCheckedException("01005001", e);
			}
		}
		
		if(updList!=null && updList.size()>0){
			try{
				PreparedStatement ps = dao.getConnection().prepareStatement(updSql.toString());
				
				for(int i=0;i<updList.size();i++){
					List<Object> list = this.getParams(updList.get(i),pms);
					for(int k=0;k<pks.length;k++){
						list.add(updList.get(i).get(pks[k]));
					}
					ps = PersistenceUtils.prepareSqlParams(ps, list);
					result = ps.execute();
				}
			}catch(Exception e){
				throw new BaseCheckedException("01005002", e);
			}
		}
		
		if(delList!=null && delList.size()>0){
			try{
				PreparedStatement ps = dao.getConnection().prepareStatement(delSql.toString());
				for(int i=0;i<delList.size();i++){
					List<Object> list = this.getParams(delList.get(i),pms);
					ps = PersistenceUtils.prepareSqlParams(ps, list);
					result = ps.execute();
				}
			}catch(Exception e){
				throw new BaseCheckedException("01005003", e);
			}
		}
		String sql = "select * from "+tableName;
		CachedRowSet crs = dao.queryToCachedRowSetBySQL(sql, null);
		
		res.addTable("list", crs);
			
		
		return res;
	}
	
	private List<Object> getParams(Map<String,Object> map,List<String> pms){
		
		List<Object>  params = new ArrayList<Object>();
		if(pms!=null)
		for(int i=0;i<pms.size();i++){
			params.add(map.get(pms.get(i)));
		}
		return params;
	}
	
	private String getTableInfo(DictVO vo){
		StringBuffer str = new StringBuffer();
		String pk="";
		if(vo!=null){
			List<Map<String,String>> list = vo.getFields();
			if(list!=null && list.size()>0){
				
				for(int i=0;i<list.size();i++){
					Map<String,String> map = list.get(i);
					str.append(map.get("column-name")).append("@").append(map.get("data-type")).append("#");
					if(map.get("is-pk").equalsIgnoreCase("Y")){
						pk+=map.get("column-name")+",";
					}
				}
				if(pk.length()>0){
					pk = pk.substring(0,pk.length()-1);
				}
			}
			str.delete(str.length()-1, str.length());
			str.append("__"+pk);
		}
		return str.toString();
	}
	
	private String createTableWidget(DictVO vo){

		StringBuffer str = new StringBuffer();
		
		if(vo!=null){
			List<Map<String,String>> list = vo.getFields();
			if(list!=null && list.size()>0){
				
				int minLength=0;
				String maxLength = null;
				String type = "string";
				boolean isNull = true;
				for(int i=0;i<list.size();i++){
					minLength=0;
					Map<String,String> map = list.get(i);
					StringBuffer st = new StringBuffer();
					st.append("<div name='"+map.get("column-name")+"' type='text'");
					st.append(" caption='"+map.get("display-name")+"'");
					type = map.get("data-type");
					maxLength = map.get("data-length")!=null?new BigDecimal(map.get("data-length")).divide(new BigDecimal("2"),0,BigDecimal.ROUND_DOWN).toString():new BigDecimal(Long.MAX_VALUE).toString();
					isNull = map.get("is-null").equalsIgnoreCase("Y");
					
					if(type.equalsIgnoreCase("number")){
						maxLength = map.get("data-length")!=null?map.get("data-length"):String.valueOf(Integer.MAX_VALUE);
						if(!isNull){
							minLength = 1;
						}
						st.append(" vtype='").append("number_length;").append(minLength).append(";").append(maxLength);
						if(!isNull){ 
							st.append("_must'");
						}else{
							st.append("'");
						}
						
					}else if(type.equalsIgnoreCase("float")){
						
					}else{
						maxLength = map.get("data-length")!=null?new BigDecimal(map.get("data-length")).divide(new BigDecimal("2"),0,BigDecimal.ROUND_DOWN).toString():new BigDecimal(Long.MAX_VALUE).toString();
						
						if(!isNull){
							st.append(" vtype='must_length;0;").append(maxLength).append("' ");
						}else{
							st.append(" vtype='length;0;").append(maxLength).append("' ");
						}
						
					}
					
					st.append("></div>");
					if(map.get("is-pk").equalsIgnoreCase("Y")){
						str.insert(0, st);
					}else{
						str.append(st);
					}
				}
			}
		}
		return str.toString();
	}
	

	private List<Map<String,Object>> getTableList() {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		InputStream is = getFileInputStream();
 
		Document doc = null;
		SAXBuilder builder = new SAXBuilder();
		try {
			doc = builder.build(is);
			Element root = doc.getRootElement(); // 获得根元素element
			List<Element> l1 = root.getChildren("table");
			if(l1!=null ){
				for(int i=0;i<l1.size();i++){
					Element e = l1.get(i);
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("code", e.getChildText("table-name"));
					map.put("caption", e.getChildText("table-description"));
					list.add(map);
				}
			}
		}catch (Exception ex) {
			ex.printStackTrace();
		}finally{
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			is = null;
		}
		return list;
	}

	private DictVO getTableInfo(String tableName) {
		
		DictVO vo = null ;
		if(StringUtils.isNotBlank(tableName)){
			InputStream is = getFileInputStream();
	
			Document doc = null;
			SAXBuilder builder = new SAXBuilder();
			try {
				doc = builder.build(is);
				Element root = doc.getRootElement(); // 获得根元素element
				List<Element> l1 = root.getChildren("table");
				if(l1!=null ){
					for(int i=0;i<l1.size();i++){
						Element e = l1.get(i);
						if(tableName.equals(e.getChildText("table-name"))){
							vo = new DictVO();
							vo.setTableName(tableName);
							vo.setDescription(e.getChildText("table-description"));
							List<Map<String,String>> fields = new ArrayList<Map<String,String>>();
							List<Element> l2 = e.getChildren("fields");
							if(l2!=null){
								for(int k=0;k<l2.size();k++){
									
									Element p = l2.get(k);
									List<Element> l3 = p.getChildren();
									if(l3!=null){
										for(int d=0;d<l3.size();d++){
											Map<String,String> map = new HashMap<String,String>();
											List<Attribute> l4 = (l3.get(d)).getAttributes();
											if(l4!=null){
												for(int a = 0;a<l4.size();a++){
													Attribute w = l4.get(a);
													map.put(w.getName().toLowerCase(), w.getValue().toLowerCase());
												}
												fields.add(map);
											}
										}
									}
								}
							}
							vo.setFields(fields);
							break;
						}
						
					}
				}
				 
	
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally{
	
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				is = null;
			}
		}
		return vo;

	}

	private InputStream getFileInputStream() {
		ClassLoader loader = this.getClass().getClassLoader();
		URL url = loader.getResource(SY005dictBLH.DICT_META_FILE);
		InputStream is = null;
		if (url != null) {
			try {
				is = url.openStream();
			} catch (Exception e) {
				logger.error("字典表配置文件【" + SY005dictBLH.DICT_META_FILE
						+ "】读取错误，请检查！", e);
			}
		} else {
			logger
					.error("字典表配置文件【" + SY005dictBLH.DICT_META_FILE
							+ "】不存在，请检查！");

		}
		return is;
	}

}
