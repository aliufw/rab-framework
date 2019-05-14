package com.rab.sys.sysmanager.comm.blh;

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
import com.rab.framework.comm.util.StringUtils;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.domain.blh.BaseDomainBLH;
import com.rab.sys.sysmanager.comm.model.TSysCompanyPO;
import com.rab.sys.sysmanager.info.model.SysAcctSubjPO;
import com.rab.sys.sysmanager.info.model.TSysEmpPO;

/**
 * 
 * @Description：用户管理
 * @Author：manan
 * @Date：2010-10-12
 */
public class SysTreeBLH extends BaseDomainBLH {
	private static final LogWritter logger = LogFactory
			.getLogger(SysTreeBLH.class);

	public BaseResponseEvent compTreeInit(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
        String sql = null;
		DataResponseEvent res = new DataResponseEvent();
		User user = domainSession.getUser();
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		int userid = user.getUserid();
		CachedRowSet crs = null;
		try{
		if(userid==1){
			crs = dao.queryToCachedRowSetByKey("SysTreeBLH_getAllCompList1", null);
		}
		else {
			String sqlt = "select min(comp_id) comp_id from t_sys_dba_comp where dba_id=?";
			List<Object> params = new ArrayList<Object>();
			params.add(userid);
			CachedRowSet rs = dao.queryToCachedRowSetBySQL(sqlt, params);
			if(rs.next()){
				int ci = rs.getInt("comp_id");
				
				List<Object> params0 = new ArrayList<Object>();
				params0.add(ci);
				params0.add(ci);
				crs = dao.queryToCachedRowSetByKey("SysTreeBLH_getAllCompList2", params0);
			}else return res;
		}}catch(Exception e){
		}
		try {
			

			Map<String, String> nameMap = new HashMap<String, String>();
			nameMap.put("code", "comp_id");
			nameMap.put("leaf", "is_last");
			nameMap.put("pcode", "super_id");
			nameMap.put("caption", "comp_name");
			
			res.addTree("tree", crs, nameMap);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseCheckedException("", e);
		}
		
		return res;
	}

	public BaseResponseEvent compTreeLazyLoad(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		String id = req.getAttr("id") == null ? null : req.getAttr("id")
				.toString();
		if (id != null) {
			String sql = "select comp_id,super_id,comp_name,is_last from t_sys_company t where super_id=? and (t.is_stop=0 or t.is_stop is null)";

			List<Object> params = new ArrayList<Object>();
			params.add(id);
			try {
				CachedRowSet crs = this.domainSession.getPersistenceDAO()
						.queryToCachedRowSetBySQL(sql, params);

				Map<String, String> nameMap = new HashMap<String, String>();
				nameMap.put("code", "comp_id");
				nameMap.put("leaf", "is_last");
				nameMap.put("pcode", "super_id");
				nameMap.put("caption", "comp_name");

				res.addTree("data", crs, nameMap);
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new BaseCheckedException("", e);
			}
		}

		return res;
	}

	public BaseResponseEvent compCheckAllTreeInit(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataResponseEvent res = new DataResponseEvent();
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		String sql = null;
		User user = domainSession.getUser();
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		int userid = user.getUserid();
		CachedRowSet crs = null;
		Integer selectuser = Integer.parseInt((String) req.getAttr("puserid"));
		try {
			if (userid == 1) {
				sql = "select t.*,'false' checked from t_sys_company t where t.is_stop=0 or t.is_stop is null";
				crs = dao.queryToCachedRowSetBySQL(sql, null);
			} else {
				String sqlt = "select level_code from t_sys_company where comp_id = (select min(comp_id) comp_id from t_sys_dba_comp where dba_id=?)";
				List<Object> params = new ArrayList<Object>();
				params.add(userid);
				CachedRowSet rs = dao.queryToCachedRowSetBySQL(sqlt, params);
				if (rs.next()) {
					String ci = rs.getString("level_code");
					sql = "select t.*,'false' checked from t_sys_company t where t.level_code like ? and(t.is_stop=0 or t.is_stop is null)";
					List<Object> params0 = new ArrayList<Object>();
					params0.add(StringUtils.trimToEmpty(ci) + "%");
					crs = dao.queryToCachedRowSetBySQL(sql, params0);
				} else
					return res;
			}
			while(crs.next()){
				crs.updateString("comp_name", crs.getString("comp_code")+"   "+crs.getString("comp_name"));
			}
		} catch (Exception e) {
		}

		String sql2 = "select comp_id,super_id,comp_code,comp_name,is_last,'true' checked from t_sys_company t "
				+ "where t.comp_id in(select min(comp_id) comp_id from t_sys_dba_comp d where d.dba_id=?)";
		try {
			List<Object> params = new ArrayList<Object>();
			params.add(selectuser);
			CachedRowSet crs2 = this.domainSession.getPersistenceDAO()
					.queryToCachedRowSetBySQL(sql2, params);
			while(crs2.next()){
				crs2.updateString("comp_name", crs2.getString("comp_code")+"   "+crs2.getString("comp_name"));
			}
			Map<String, String> nameMap = new HashMap<String, String>();
			nameMap.put("code", "comp_id");
			nameMap.put("leaf", "is_last");
			nameMap.put("pcode", "super_id");
			nameMap.put("caption", "comp_name");
			nameMap.put("checked", "checked");
			res.addCheckTree("tree", crs, crs2, nameMap);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseCheckedException("", e);
		}

		return res;
	}

	public BaseResponseEvent compCheckTreeInit(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataResponseEvent res = new DataResponseEvent();
		DataRequestEvent req = (DataRequestEvent) reqEvent;
        String sql = null;
		User user = domainSession.getUser();
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		int userid = user.getUserid();
		CachedRowSet crs = null;
		Integer selectuser = Integer.parseInt((String)req.getAttr("puserid"));
		try{
		if(userid==1){
			crs = dao.queryToCachedRowSetByKey("SysTreeBLH_getAllCompList3", null);
		}
		else {
			String sqlt = "select min(comp_id) comp_id from t_sys_dba_comp where dba_id=?";
			List<Object> params = new ArrayList<Object>();
			params.add(userid);
			CachedRowSet rs = dao.queryToCachedRowSetBySQL(sqlt, params);
			if(rs.next()){
				int ci = rs.getInt("comp_id");
				List<Object> params0 = new ArrayList<Object>();
				params0.add(ci);
				params0.add(ci);
				crs = dao.queryToCachedRowSetByKey("SysTreeBLH_getAllCompList4", params0);
			}else return res;
		}}catch(Exception e){
		}
		
		
		String sql2 = "select comp_id,super_id,comp_name,is_last,'true' checked from t_sys_company t "
	         +"where t.comp_id in(select min(comp_id) comp_id from t_sys_dba_comp d where d.dba_id=?)";
		try {
			List<Object> params = new ArrayList<Object>();
			params.add(selectuser);
			CachedRowSet crs2 = this.domainSession.getPersistenceDAO()
			.queryToCachedRowSetBySQL(sql2, params);
			Map<String, String> nameMap = new HashMap<String, String>();
			nameMap.put("code", "comp_id");
			nameMap.put("leaf", "is_last");
			nameMap.put("pcode", "super_id");
			nameMap.put("caption", "comp_name");
			nameMap.put("checked", "checked");
			res.addCheckTree("tree", crs,crs2, nameMap);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseCheckedException("", e);
		}

		return res;
	}

	public BaseResponseEvent compCheckTreeLazyLoad(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		String id = req.getAttr("id") == null ? null : req.getAttr("id")
				.toString();
		if (id != null) {
			String sql = " select comp_id,super_id,comp_name,is_last,'false' checked from t_sys_company t where super_id=? and (t.is_stop=0 or t.is_stop is null)";

			List<Object> params = new ArrayList<Object>();
			params.add(id);
			Integer selectuser = Integer.parseInt((String)req.getAttr("puserid"));
			String sql2 = "select comp_id,super_id,comp_name,is_last,'true' checked from t_sys_company t "
		         +"where t.comp_id in(select comp_id from t_sys_dba_comp d where d.dba_id=?) and (t.is_stop=0 or t.is_stop is null)";
			List<Object> params0 = new ArrayList<Object>();
			params0.add(selectuser);
			try {
				CachedRowSet crs = this.domainSession.getPersistenceDAO()
						.queryToCachedRowSetBySQL(sql, params);
				CachedRowSet crs2 = this.domainSession.getPersistenceDAO()
				.queryToCachedRowSetBySQL(sql2, params0);
				Map<String, String> nameMap = new HashMap<String, String>();
				nameMap.put("code", "comp_id");
				nameMap.put("leaf", "is_last");
				nameMap.put("pcode", "super_id");
				nameMap.put("caption", "comp_name");
				nameMap.put("checked", "checked");
				res.addCheckTree("data", crs, crs2,nameMap);
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new BaseCheckedException("", e);
			}
		}

		return res;
	}

	public BaseResponseEvent deptTreeInit(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		try{
			DataRequestEvent req = (DataRequestEvent) reqEvent;
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			String id = req.getAttr("comp_id") == null ? null : req.getAttr("comp_id")
					.toString();	
			List<Object> params = new ArrayList<Object>();
			params.add(id);
			params.add(id);
			// CachedRowSet rs= dao.queryToCachedRowSet(sql, params);
			DataResponseEvent res = new DataResponseEvent();
			CachedRowSet rs = dao.queryToCachedRowSetByKey("SysTreeBLH_depTreeInit", params);
			Map<String, String> nameMap = new HashMap<String, String>();
			nameMap.put("code", "dept_id");
			nameMap.put("leaf", "is_last");
			nameMap.put("pcode", "super_id");
			nameMap.put("caption", "dept_name");
			res.addTree("tree", rs, nameMap);			
			return res;
		   }catch(Exception e){
			   throw new BaseCheckedException("", e);
	      }
	}

	public BaseResponseEvent deptTreeLazyLoad(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		String id = req.getAttr("id") == null ? null : req.getAttr("id")
				.toString();
		if (id != null) {
			String sql = "select dept_id, is_last, super_id,dept_name from t_sys_dept"
                         +" where super_id = ?  and (is_stop=0 or is_stop is null or is_stop=2)";

			List<Object> params = new ArrayList<Object>();
			params.add(id);
			try {
				CachedRowSet crs = this.domainSession.getPersistenceDAO()
						.queryToCachedRowSetBySQL(sql, params);

				Map<String, String> nameMap = new HashMap<String, String>();
				nameMap.put("code", "dept_id");
				nameMap.put("leaf", "is_last");
				nameMap.put("caption", "dept_name");
				nameMap.put("pcode", "super_id");

				res.addTree("data", crs, nameMap);
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new BaseCheckedException("", e);
			}
		}
		return res;
	}

	public BaseResponseEvent empTreeInit(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		try {
			DataRequestEvent req = (DataRequestEvent) reqEvent;
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			String id = req.getAttr("comp_id") == null ? null : req.getAttr(
					"comp_id").toString();
			List<Object> params = new ArrayList<Object>();
			params.add(id);
			params.add(id);
			// CachedRowSet rs= dao.queryToCachedRowSet(sql, params);
			DataResponseEvent res = new DataResponseEvent();
			CachedRowSet rs = dao.queryToCachedRowSetByKey("SysTreeBLH_empTreeInit", params);
			Map<String, String> nameMap = new HashMap<String, String>();
			nameMap.put("code", "dept_id");
			nameMap.put("leaf", "is_last");
			nameMap.put("pcode", "super_id");
			nameMap.put("caption", "dept_name");
			res.addTree("tree", rs, nameMap);
			return res;
		} catch (Exception e) {
			throw new BaseCheckedException("", e);
		}
	}

	public BaseResponseEvent empTreeLazyLoad(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		String id = req.getAttr("id") == null ? null : req.getAttr("id")
				.toString();
		if (id != null) {
			String sql = "select dept_id, is_last, super_id,dept_name from t_sys_dept"
					+ " where super_id = ?  and (is_stop=0 or is_stop is null)";

			List<Object> params = new ArrayList<Object>();
			params.add(id);
			try {
				CachedRowSet crs = this.domainSession.getPersistenceDAO()
						.queryToCachedRowSetBySQL(sql, params);

				Map<String, String> nameMap = new HashMap<String, String>();
				nameMap.put("code", "dept_id");
				nameMap.put("leaf", "is_last");
				nameMap.put("caption", "dept_name");
				nameMap.put("pcode", "super_id");

				res.addTree("data", crs, nameMap);
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new BaseCheckedException("", e);
			}
		}
		return res;
	}

	public BaseResponseEvent compCombo(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		try {
			DataRequestEvent req = (DataRequestEvent) reqEvent;
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			User user = domainSession.getUser();
			int userid = user.getUserid();
			String sql = null;
			DataResponseEvent res = new DataResponseEvent();
			CachedRowSet rs = null;
			if(userid==1){
				sql = "select c.comp_id code,c.comp_name caption,c.comp_code title from t_sys_company c where c.is_stop=0 or c.is_stop is null order by c.comp_code";
				rs = dao.queryToCachedRowSetBySQL(sql, null);
			}else{
				String sql0 = "select level_code from t_sys_company where comp_id = (select min(comp_id) comp_id from t_sys_dba_comp where dba_id=?) and (is_stop=0 or is_stop is null)";
				List<Object> params0 = new ArrayList<Object>();
				params0.add(userid);
				CachedRowSet rs0 = dao.queryToCachedRowSetBySQL(sql0, params0);
				if(rs0.next()){
					sql = "select c.comp_id code,c.comp_name caption,c.comp_code title from t_sys_company c"
						+" where c.level_code like ? and (c.is_stop=0 or c.is_stop is null) order by c.comp_code";
					List<Object> params = new ArrayList<Object>();
					String levelcode = rs0.getString("level_code");
					params.add(StringUtils.trimToEmpty(levelcode) + "%");
					rs = dao.queryToCachedRowSetBySQL(sql, params);
				}else return res;
				
			}
			while(rs.next()){
				rs.updateString("caption", rs.getString("title")+"   "+rs.getString("caption"));
			}
			res.addCombo("comp_caption", rs);
			if(req.getAttr("admin")!=null){
			  res.addAttr("admin", req.getAttr("admin").toString());
			  res.addAttr("isgly", user.isAdmin());
			}
			return res;
		} catch (Exception e) {
			throw new BaseCheckedException("", e);
		}
	}
}
