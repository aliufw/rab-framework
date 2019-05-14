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
import com.rab.framework.comm.util.StringUtils;
import com.rab.framework.component.dictcache.CacheUpdateHelper;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.dao.PersistenceUtils;
import com.rab.framework.domain.blh.BaseDomainBLH;
import com.rab.sys.sysmanager.info.model.TSysCompanyPO;

/**
 * 
 * @Description：单位信息
 * @Author：manan
 * @Date：2010-10-13
 */
public class SY008dwxxBLH extends BaseDomainBLH {
	private static final LogWritter logger = LogFactory
			.getLogger(SY007kmbmBLH.class);

	public BaseResponseEvent init(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;

		DataResponseEvent res = new DataResponseEvent();

		res.setPage("/page/sysmanager/info/sy008_dwxx_main.jsp");

		return res;
	}

	public BaseResponseEvent formQuery(BaseRequestEvent reqEvent) 
	 throws BaseCheckedException {
	   try{
		    DataRequestEvent req = (DataRequestEvent) reqEvent;
			DataResponseEvent res = new DataResponseEvent();
			String id = req.getAttr("coCode") == null ? null : req
					.getAttr("coCode").toString();
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			List<Object> params = new ArrayList<Object>();
			params.add(id);
			try {
				CachedRowSet rs = dao.queryToCachedRowSetByKey("SY008dwxxBLH_formQuery", params);
				res.addForm("form", rs);
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new BaseCheckedException("10000000", e);
			}
			return res;
	   }catch(Exception e){
		   throw new BaseCheckedException("", e);
     }
	}
	
	public BaseResponseEvent add(BaseRequestEvent reqEvent) 
	 throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;		
		String supperid = (String)req.getAttr("superid");
		String compc = "";
		String levelcode0 = "";
		try{
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			Integer superid = Integer.parseInt(supperid);
			List<Object> params = new ArrayList<Object>();
			params.add(superid);
			CachedRowSet rs = dao.queryToCachedRowSetByKey("SY008dwxxBLH_add", params);
			if(rs.next()){
               compc = rs.getString("comp_code");
               levelcode0 = rs.getString("level_code");
			}else{
				throw new BaseCheckedException("01008005");
			}
			
			TSysCompanyPO bo = (TSysCompanyPO) req.getForm("form");
			String cmpcode = bo.getComp_code();
			cmpcode=compc+cmpcode;
			List<Object> params1 = new ArrayList<Object>();
			params1.add(cmpcode);
			CachedRowSet rs1 = dao.queryToCachedRowSetByKey("SY008dwxxBLH_add1", params1);
			if(rs1.next()){
				throw new BaseCheckedException("01008001");
			}
		}catch(Exception e){
			if(e instanceof BaseCheckedException){
				throw (BaseCheckedException)e;
			}else{
				throw new BaseCheckedException("01008001", e);
			}	
		}
	
		try {
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			TSysCompanyPO bo1 = new  TSysCompanyPO();
			bo1.setComp_id(Integer.parseInt(supperid));
			bo1.setIs_last(0);
			dao.updateSingleRow(bo1);
			TSysCompanyPO bo = (TSysCompanyPO) req.getForm("form");
			bo.setSuper_id(Integer.parseInt(supperid));	
			int id = (int)dao.getSequence("SQ_SYS_COMPANY");
			//String idall = supperid+String.valueOf(id);
			bo.setComp_id(id);
			compc = compc + bo.getComp_code();
			bo.setComp_code(compc);
			String levelcode = levelcode0+"-"+Integer.toString(id);
			bo.setLevel_code(levelcode);
			dao.insertSingleRow(bo);
			DataResponseEvent res = new DataResponseEvent();
			res.addAttr("compid",id);
			res.addAttr("compcode",bo.getComp_code());
			CacheUpdateHelper.setUpdateFlag("t_sys_company", this.domainSession.getPersistenceDAO(), res);
			return res;
		} catch (Exception e) {
			throw new BaseCheckedException("01008002", e);
		}
	}

	public BaseResponseEvent update(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		String compc="";
		String levelcode0="";	
		try{
			TSysCompanyPO bo = (TSysCompanyPO) req.getForm("form");
			String cmpcode = bo.getComp_code();
			int cmpid = bo.getComp_id();
			cmpcode=compc+cmpcode;
			List<Object> params1 = new ArrayList<Object>();
			params1.add(cmpcode);
			params1.add(cmpid);
			CachedRowSet rs1 = dao.queryToCachedRowSetByKey("SY008dwxxBLH_update", params1);
			if(rs1.next()){
				throw new Exception();
			}
			
			List<Object> params3 = new ArrayList<Object>();
			params3.add(cmpid);
			CachedRowSet rs3 = dao.queryToCachedRowSetByKey("SY008dwxxBLH_update1", params3);
			if(rs3.next()){
				String ccode = rs3.getString("comp_code");
				String upcode = cmpcode;
				if(!ccode.equals(upcode)){
					List<Object> params2 = new ArrayList<Object>();
					params2.add(StringUtils.trimToEmpty(ccode) + "%");
					CachedRowSet rs2 = dao.queryToCachedRowSetByKey("SY008dwxxBLH_update2", params2);
					while(rs2.next()){
						TSysCompanyPO bo1 = new TSysCompanyPO();
						String cpcode = rs2.getString("comp_code");	
						String ccc = cpcode.replace(ccode, upcode);
						bo1.setComp_code(ccc);
						bo1.setComp_id(rs2.getInt("comp_id"));
						dao.updateSingleRow(bo1);
					}
				}
			}
			
		}catch(Exception e){
			throw new BaseCheckedException("01008001", e);	
		}
		
		try {
			DataResponseEvent res = new DataResponseEvent();
			TSysCompanyPO bo = (TSysCompanyPO) req.getForm("form");
			dao.updateSingleRow(bo);
			CacheUpdateHelper.setUpdateFlag("t_sys_company", this.domainSession.getPersistenceDAO(), res);
			return res;
		} catch (Exception e) {
			throw new BaseCheckedException("10000000", e);
		}
		
	}

	public BaseResponseEvent initdw(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;	
		
		try {
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			TSysCompanyPO bo = (TSysCompanyPO) req.getForm("form");
			int id = (int)dao.getSequence("SQ_SYS_COMPANY");
			//String idall = supperid+String.valueOf(id);
			bo.setComp_id(id);
			bo.setLevel_code(bo.getComp_code());
			
			dao.insertSingleRow(bo);
			DataResponseEvent res = new DataResponseEvent();
			res.addAttr("compid",id);
			CacheUpdateHelper.setUpdateFlag("t_sys_company", this.domainSession.getPersistenceDAO(), res);
			return res;
		} catch (Exception e) {
			throw new BaseCheckedException("01008002");
		}
	}

	public BaseResponseEvent dwCheck(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			CachedRowSet rs = dao.queryToCachedRowSetByKey("SY008dwxxBLH_dwCheck", null);
			if(rs.next())res.addAttr("comp","1");
			else res.addAttr("comp","0");
			return res;
		} catch (Exception e) {
			throw new BaseCheckedException("01008002");
		}
	}

	public BaseResponseEvent delete(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		try {
			TSysCompanyPO bo = (TSysCompanyPO) req.getForm("form");
			List<Object> params = new ArrayList<Object>();
			params.add(bo.getComp_id());
			CachedRowSet rs = dao.queryToCachedRowSetByKey("SY008dwxxBLH_delete", params);
			if(rs.next())throw new Exception();
		}catch(Exception e){
			throw new BaseCheckedException("01008003", e);
		}
		try {
			TSysCompanyPO bo = (TSysCompanyPO) req.getForm("form");
			List<Object> params = new ArrayList<Object>();
			params.add(bo.getComp_id());
			CachedRowSet rs = dao.queryToCachedRowSetByKey("SY008dwxxBLH_delete1", params);
			if(rs.next())throw new Exception();
		}catch(Exception e){
			throw new BaseCheckedException("01008004", e);
		}
		try {
			
			TSysCompanyPO bo = (TSysCompanyPO) req.getForm("form");
			Integer superid = bo.getComp_id();
			List<Object> params = new ArrayList<Object>();
			params.add(superid);
			CachedRowSet rs = dao.queryToCachedRowSetByKey("SY008dwxxBLH_delete2", params);
			String levelcode0="";
			if(rs.next()){
				levelcode0 = rs.getString("level_code");
				List<Object> params2 = new ArrayList<Object>();
				params2.add(superid);
				params2.add(rs.getInt("super_id"));
				CachedRowSet rs1 = dao.queryToCachedRowSetByKey("SY008dwxxBLH_delete3", params2);
				if(rs1.next()){
						 
				}else{
				
		               TSysCompanyPO bo2 = new TSysCompanyPO();
		   			   bo2.setIs_last(1);
		   			   bo2.setComp_id(rs.getInt("super_id"));
		   			   dao.updateSingleRow(bo2);
		   			   res.addAttr("islast","1");
				}
               
			}	
			TSysCompanyPO bo1 = new TSysCompanyPO();
			bo1.setIs_stop(1);
			bo1.setComp_id(superid);
			dao.updateSingleRow(bo1);
			CacheUpdateHelper.setUpdateFlag("t_sys_company", this.domainSession.getPersistenceDAO(), res);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseCheckedException("10000000", e);
		}

		return res;
	}

}
