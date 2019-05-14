package com.rab.sys.sysmanager.info.blh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.jdbc.rowset.CachedRowSet;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.dto.event.DataRequestEvent;
import com.rab.framework.comm.dto.event.DataResponseEvent;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.pagination.PaginationMetaData;
import com.rab.framework.comm.util.StringUtils;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.domain.blh.BaseDomainBLH;
import com.rab.sys.sysmanager.info.model.SysAcctSubjPO;

/**
 * 
 * @Description£º¿ÆÄ¿±àÂë
 * @Author£ºmanan
 * @Date£º2010-10-08
 */
public class SY007kmbmBLH extends BaseDomainBLH {
	private static final LogWritter logger = LogFactory
			.getLogger(SY007kmbmBLH.class);

	public BaseResponseEvent init(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;

		DataResponseEvent res = new DataResponseEvent();

		res.setPage("/page/sysmanager/info/sy007_kmbm_main.jsp");

		return res;
	}

	public BaseResponseEvent query(BaseRequestEvent reqEvent) 
	 throws BaseCheckedException {
	   try{
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		SysAcctSubjPO bo=null;
		bo = (SysAcctSubjPO) req.getForm("form");	
		List<Object> params = new ArrayList<Object>();
		params.add("%" + StringUtils.trimToEmpty(bo.getAcct_subj_code()) + "%");
		params.add("%" + StringUtils.trimToEmpty(bo.getAcct_subj_name()) + "%");
		String sql =  "SELECT s.acct_subj_code,c.co_name,s.acct_subj_name,s.acct_subj_name_all,s.subj_type_code,s.subj_nature_code"
				+",s.direction,s.super_code,s.co_code,s.subj_level"
		              +" FROM t_sys_acct_subj s"
			+" LEFT JOIN t_sys_subj_type t ON s.co_code=t.co_code AND s.subj_type_code=t.subj_type_code"
			+" LEFT JOIN t_sys_subj_nature n ON s.subj_nature_code=n.subj_nature_code"
		    +" ,t_sys_co_type c WHERE s.acct_subj_code LIKE ?"
			+" AND s.acct_subj_name LIKE ? and s.co_code=c.co_code";
		// CachedRowSet rs= dao.queryToCachedRowSet(sql, params);
		String code = bo.getCo_code();
		if(code!=null){
		  params.add(bo.getCo_code());
		  sql+=" AND s.co_code=?";
		}
		sql+=" ORDER BY acct_subj_code asc";
		PaginationMetaData metaData = req.getTablePageInfo("list");
		DataResponseEvent res = new DataResponseEvent();
		Map<String, String> cacheMap = new HashMap<String, String>();
		cacheMap.put("subj_type_code", "t_sys_subj_type");
		cacheMap.put("subj_nature_code", "t_sys_subj_nature");
		CachedRowSet rs = dao.queryPageToCachedRowSetBySQL(sql, params,metaData);
		res.addCacheInfo("list", cacheMap);
		res.addTable("list", rs, metaData);
		return res;
	   }catch(Exception e){
		   throw new BaseCheckedException("", e);
      }
	}
	public  BaseResponseEvent update(BaseRequestEvent reqEvent)
	throws BaseCheckedException {
		
	  try{
		DataRequestEvent req = (DataRequestEvent)reqEvent;
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		List gridtd = null;
		
		gridtd = req.getTable("list");
		SysAcctSubjPO bo = (SysAcctSubjPO)gridtd.get(0);  
		dao.updateSingleRow(bo);
		DataResponseEvent res = new DataResponseEvent();
		return res;
	  }catch(Exception e){	
		  throw new BaseCheckedException("", e);
	  }
		
	}
	public BaseResponseEvent delete(BaseRequestEvent reqEvent)
	throws Exception {
		DataRequestEvent req = (DataRequestEvent)reqEvent;
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		List list = req.getTable("list",SysAcctSubjPO.class);
		dao.deleteBatchRow(list);
		DataResponseEvent res = new DataResponseEvent();
		
		return res;
	}
	public BaseResponseEvent add(BaseRequestEvent reqEvent) 
	 throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		try {
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			SysAcctSubjPO bo = (SysAcctSubjPO) req.getForm("form");
			
			List<Object> params1 = new ArrayList<Object>();
			params1.add(bo.getAcct_subj_code());
			CachedRowSet rs1 = dao.queryToCachedRowSetByKey("SY007kmbmBLH_add", params1);
			if(rs1.next()){
				throw new Exception();
			}
			
			dao.insertSingleRow(bo);
		} catch (Exception e) {
			throw new BaseCheckedException("01007001", e);
		}
		return new DataResponseEvent();
	}
}
