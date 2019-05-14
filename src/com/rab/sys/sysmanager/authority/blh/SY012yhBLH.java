package com.rab.sys.sysmanager.authority.blh;

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
import com.rab.framework.comm.security.User;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.domain.blh.BaseDomainBLH;
import com.rab.sys.sysmanager.authority.model.TSysUserGroupPO;
import com.rab.sys.sysmanager.authority.model.TSysUserPO;
/**
 * 
 * @Description：用户管理 
 * @Author：manan
 * @Date：2010-10-12
 */
public class SY012yhBLH extends BaseDomainBLH {
	private static final LogWritter logger = LogFactory
			.getLogger(SY012yhBLH.class);
	public BaseResponseEvent init(BaseRequestEvent reqEvent)
	throws BaseCheckedException {
		try {
			DataRequestEvent req = (DataRequestEvent) reqEvent;
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			DataResponseEvent res = new DataResponseEvent();
			CachedRowSet rs = dao.queryToCachedRowSetByKey("SY012yhBLH_init", null);
			res.addCombo("comp_caption", rs);
			return res;
		} catch (Exception e) {
			throw new BaseCheckedException("", e);
		}
	}

	public BaseResponseEvent tableQuery(BaseRequestEvent reqEvent) 
	 throws BaseCheckedException {
	   try{
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		
		List<Object> params = new ArrayList<Object>();
		User user = domainSession.getUser();
		String id = req.getAttr("coCode") == null ? null : req
				.getAttr("coCode").toString();
		params.add(id);
		params.add(new Integer(user.getUserid()));
		DataResponseEvent res = new DataResponseEvent();
		CachedRowSet rs = dao.queryToCachedRowSetByKey("SY012yhBLH_tableQuery", params);
		Map<String, String> cacheMap = new HashMap<String, String>();
		cacheMap.put("emp_id", "sys_subj_type");
		res.addCacheInfo("list", cacheMap);
		res.addTable("list", rs);
		return res;
	   }catch(Exception e){
		   throw new BaseCheckedException("01012004", e);
     }
	}
	public BaseResponseEvent tableDbaQuery(BaseRequestEvent reqEvent) 
	 throws BaseCheckedException {
	   try{
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		
		List<Object> params = new ArrayList<Object>();
		User user = domainSession.getUser();
		String id = req.getAttr("coCode") == null ? null : req
				.getAttr("coCode").toString();
		params.add(id);
		params.add(new Integer(user.getUserid()));
		DataResponseEvent res = new DataResponseEvent();
		CachedRowSet rs = dao.queryToCachedRowSetByKey("SY012yhBLH_tableDbaQuery", params);
		Map<String, String> cacheMap = new HashMap<String, String>();
		cacheMap.put("emp_id", "sys_subj_type");
		res.addCacheInfo("list", cacheMap);
		res.addTable("list", rs);
		return res;
	   }catch(Exception e){
		   throw new BaseCheckedException("01012004", e);
    }
	}
	public BaseResponseEvent add(BaseRequestEvent reqEvent) 
	 throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		
		try{
			TSysUserPO bo = (TSysUserPO) req.getForm("form");
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			List<Object> params = new ArrayList<Object>();
			params.add(bo.getUser_code());
			CachedRowSet rs = dao.queryToCachedRowSetByKey("SY012yhBLH_add", params);
			if(rs.next()){throw new Exception();}
		}catch(Exception e){
			throw new BaseCheckedException("01012006", e);
		}
		
		try {
			TSysUserPO bo = (TSysUserPO) req.getForm("form");
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			long sq =  dao.getSequence("SQ_SYS_USER");
			Integer s = new Integer((int)sq);
			bo.setUser_id(s);
			User user = domainSession.getUser();
			bo.setSj_id(user.getUserid());
			dao.insertSingleRow(bo);
			TSysUserGroupPO bo1 = new TSysUserGroupPO();
			bo1.setGroup_id(Integer.parseInt((req.getFormData("form").get("group_id")).toString()));
			bo1.setUser_id(s);
			dao.insertSingleRow(bo1);
			DataResponseEvent res = new DataResponseEvent();
            res.addAttr("user_id", s.toString());
            return res;
		} catch (Exception e) {
			throw new BaseCheckedException("01012001", e);
		}
		
	}
	public BaseResponseEvent initAdd(BaseRequestEvent reqEvent) 
	 throws BaseCheckedException {
	try{
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		User user = domainSession.getUser();
		List<Object> params = new ArrayList<Object>();
		params.add(user.getUserid());
		List<Object> params0 = new ArrayList<Object>();
		String deptid = req.getAttr("deptid").toString();
		params0.add(deptid);
		DataResponseEvent res = new DataResponseEvent();
		CachedRowSet rs = dao.queryToCachedRowSetByKey("SY012yhBLH_initAdd",params0);
		res.addCombo("emp_id", rs);
		CachedRowSet rs1 = dao.queryToCachedRowSetByKey("SY012yhBLH_initAdd2",params);
		res.addCombo("group_id", rs1);
		res.addAttr("dept_id", deptid);
		return res;
	   }catch(Exception e){
		   throw new BaseCheckedException("", e);
      }
	}
	public BaseResponseEvent initDetail(BaseRequestEvent reqEvent) 
	 throws BaseCheckedException {
	try{
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		User user = domainSession.getUser();
		List<Object> params = new ArrayList<Object>();
		params.add(user.getUserid());
		List<Object> params0 = new ArrayList<Object>();
		String deptid = req.getAttr("deptid").toString();
		String empid = req.getAttr("empid").toString();
		params0.add(deptid);
		DataResponseEvent res = new DataResponseEvent();
		CachedRowSet rs = dao.queryToCachedRowSetByKey("SY012yhBLH_initDetail",params0);
		res.addCombo("emp_id", rs);
		CachedRowSet rs1 = dao.queryToCachedRowSetByKey("SY012yhBLH_initDetail2",params);
		res.addCombo("group_id", rs1);	
		
		
		
		return res;
	   }catch(Exception e){
		   throw new BaseCheckedException("", e);
     }
	}
	
	public BaseResponseEvent initYhQxDetail(BaseRequestEvent reqEvent)
	throws BaseCheckedException {
		
		try{
			DataRequestEvent req = (DataRequestEvent) reqEvent;
			DataResponseEvent res = new DataResponseEvent();
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			
			List<Object> params1 = new ArrayList<Object>();
			String userId = (String)req.getAttr("userid");
			params1.add(userId);
			params1.add(userId);
			String sql2 = "SY012yhBLH_initDetail_yhqxList";
			CachedRowSet rs2 = dao.queryToCachedRowSetByKey(sql2,params1);
			res.addTable("list", rs2);
			return res;
		}catch(Exception e){
		   throw new BaseCheckedException("01018001", e);
		}
	}

	public BaseResponseEvent update(BaseRequestEvent reqEvent)
			throws BaseCheckedException {

		DataRequestEvent req = (DataRequestEvent) reqEvent;
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		TSysUserPO bo = null;
		try{
			bo = (TSysUserPO) req.getForm("form");
			List<Object> params = new ArrayList<Object>();
			params.add(bo.getUser_code());
			params.add(bo.getUser_id());
			CachedRowSet rs = dao.queryToCachedRowSetByKey("SY012yhBLH_update", params);
			if(rs.next()){throw new Exception();}
		}catch(Exception e){
			throw new BaseCheckedException("01012006", e);
		}
		try {
			List<Object> params = new ArrayList<Object>();
			params.add(bo.getUser_id());
			String sqlw = "update t_sys_user_group set group_id = "
				+Integer.parseInt((req.getFormData("form").get("group_id")).toString())+"where user_id = ?";
			dao.updateBatchRowBySQL(sqlw, params);
			dao.updateSingleRow(bo);
			return new DataResponseEvent();
		} catch (Exception e) {
			throw new BaseCheckedException("01012003", e);
		}
		
	}

	public BaseResponseEvent delete(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		try {
			List list = req.getTable("list");
			if (list != null && list.size() > 0) {
				int len = list.size();
				for (int i = 0; i < len; i++) {
					TSysUserPO bo = (TSysUserPO) list.get(i);
					bo.setScbj(1);
					dao.updateSingleRow(bo);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseCheckedException("01012002", e);
		}

		return res;
	}
	


	public BaseResponseEvent userInit(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		try {
			DataRequestEvent req = (DataRequestEvent) reqEvent;
			DataResponseEvent res = new DataResponseEvent();
			User user = domainSession.getUser();
			if (req.getAttr("admin") != null) {
				res.addAttr("admin", req.getAttr("admin").toString());
				res.addAttr("isgly", user.isAdmin());
			}
			return res;
		} catch (Exception e) {
			throw new BaseCheckedException("", e);
		}
	}


	public BaseResponseEvent initYhqxTree(BaseRequestEvent reqEvent)
	throws BaseCheckedException {
		
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
		
		
			List<Object> params1 = new ArrayList<Object>();
		
			String userId = (String) req.getAttr("userId");
			String copyCode = (String) req.getAttr("copyCode");
			String modCode = (String) req.getAttr("modCode");
		
			String sqlKey1 = "SY012yhBLH_initYhqxTree";
		
			
			params1.add(userId);
			params1.add(copyCode);
			params1.add(modCode);
			params1.add(userId);
			params1.add(copyCode);
			params1.add(modCode);
			
			CachedRowSet crs1 = this.domainSession.getPersistenceDAO()
					.queryToCachedRowSetByKey(sqlKey1, params1);
		
			res.addTree("tree", crs1,null);
		
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseCheckedException("", e);
		}
		return res;
		} 

}
