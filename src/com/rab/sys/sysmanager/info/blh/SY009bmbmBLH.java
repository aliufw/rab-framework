package com.rab.sys.sysmanager.info.blh;

import java.util.ArrayList;
import java.util.List;

import sun.jdbc.rowset.CachedRowSet;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.dto.event.DataRequestEvent;
import com.rab.framework.comm.dto.event.DataResponseEvent;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.util.StringUtils;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.domain.blh.BaseDomainBLH;
import com.rab.sys.sysmanager.info.model.TSysCompanyPO;
import com.rab.sys.sysmanager.info.model.TSysDeptPO;

/**
 * 
 * @Description：部门信息
 * @Author：manan
 * @Date：2010-10-13
 */
public class SY009bmbmBLH extends BaseDomainBLH {
	private static final LogWritter logger = LogFactory
			.getLogger(SY007kmbmBLH.class);

	public BaseResponseEvent init(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		try{
			DataRequestEvent req = (DataRequestEvent) reqEvent;
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			DataResponseEvent res = new DataResponseEvent();
			CachedRowSet rs = dao.queryToCachedRowSetByKey("SY009bmbmBLH_init",null);
			res.addCombo("comp_caption", rs);
			return res;
		   }catch(Exception e){
			   throw new BaseCheckedException("", e);
	      }
	}

	public BaseResponseEvent formQuery(BaseRequestEvent reqEvent) 
	 throws BaseCheckedException {
	   try{
		    DataRequestEvent req = (DataRequestEvent) reqEvent;
			DataResponseEvent res = new DataResponseEvent();
			String id = req.getAttr("coCode") == null ? null : req
					.getAttr("coCode").toString();
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			String sql = "select * from t_sys_dept where dept_id = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(id);
			try {
				CachedRowSet rs = dao.queryToCachedRowSetBySQL(sql, params);
				res.addForm("form", rs);
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new BaseCheckedException("01009003", e);
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
		String deptc = null;
		String levelcode = null;
		try{
			TSysDeptPO bo = (TSysDeptPO) req.getForm("form");
			deptc=bo.getDept_code();
			
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			List<Object> params = new ArrayList<Object>();
			Integer superid = Integer.parseInt(supperid);
			params.add(superid);
			CachedRowSet rs = dao.queryToCachedRowSetByKey("SY009bmbmBLH_add", params);
			if(rs.next()){
				deptc = rs.getString("dept_code")+deptc;
	            levelcode = String.valueOf(Integer.parseInt(rs.getString("dept_level"))+1);
	            
			}else{
				levelcode = "1";
			}
			CachedRowSet rs3 = dao.queryToCachedRowSetByKey("SY009bmbmBLH_add2", params);
			if(rs3.next()){
				if(rs3.getString("is_stop").equals("2"))throw new BaseCheckedException("01009010");
			}
			List<Object> params1 = new ArrayList<Object>();
			params1.add(deptc);
			CachedRowSet rs1 = dao.queryToCachedRowSetByKey("SY009bmbmBLH_add1", params1);		
			if(rs1.next()){
				throw new Exception();
			}
		}catch(Exception e){
			if(e instanceof BaseCheckedException){
				throw (BaseCheckedException)e;
			}else{
				throw new BaseCheckedException("01009001", e);
			}
				
		}
		
		try {
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			String compid = (String)req.getAttr("compid");
			TSysDeptPO bo1 = new  TSysDeptPO();
			if(!supperid.equals("-1")){
			 bo1.setDept_id(Integer.parseInt(supperid));
			 bo1.setIs_last(0);
			 dao.updateSingleRow(bo1);}
		
			TSysDeptPO bo = (TSysDeptPO) req.getForm("form");
			if(!supperid.equals("-1"))
			bo.setSuper_id(Integer.parseInt(supperid));
			bo.setComp_id(Integer.parseInt(compid));
			long sq = dao.getSequence("SQ_SYS_COMPANY");
		    Integer s = new Integer((int)sq);
			bo.setDept_id(s);
			bo.setDept_code(deptc);
			bo.setDept_level(Integer.parseInt(levelcode));
			bo.setIs_budg(0);
			bo.setIs_func(0);
			dao.insertSingleRow(bo);
			DataResponseEvent res = new DataResponseEvent();
			res.addAttr("deptid",s );
			res.addAttr("deptcode",bo.getDept_code());
			return res;
		} catch (Exception e) {
			throw new BaseCheckedException("01009002", e);
		}
		
	}

	public BaseResponseEvent update(BaseRequestEvent reqEvent)
			throws BaseCheckedException {

		DataRequestEvent req = (DataRequestEvent) reqEvent;
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		String supperid = (String)req.getAttr("superid");
		String deptc = null;
		String levelcode = null;
		try{
			TSysDeptPO bo = (TSysDeptPO) req.getForm("form");
			deptc=bo.getDept_code();
			int deptid=bo.getDept_id();
			List<Object> params1 = new ArrayList<Object>();
			params1.add(deptc);
			params1.add(deptid);
			CachedRowSet rs1 = dao.queryToCachedRowSetByKey("SY009bmbmBLH_update", params1);		
			if(rs1.next()){
				throw new BaseCheckedException("01009001");
			}
			if (bo.getIs_stop()==2) {
				List<Object> params2 = new ArrayList<Object>();
				params2.add(bo.getDept_id());
				CachedRowSet rs = dao.queryToCachedRowSetByKey("SY009bmbmBLH_update1", params2);
				if (rs.next())
					throw new BaseCheckedException("01009005");
				List<Object> params3 = new ArrayList<Object>();
				params3.add(bo.getDept_id());
				CachedRowSet rs2 = dao.queryToCachedRowSetByKey("SY009bmbmBLH_update2", params3);
				if (rs2.next())
					throw new BaseCheckedException("01009006");
				
			}
		}catch(Exception e){
			if(e instanceof BaseCheckedException){
				throw (BaseCheckedException)e;
			}else{
				throw new BaseCheckedException("01009001", e);
			}
		}
		try {
			TSysDeptPO bo = (TSysDeptPO) req.getForm("form");
			int dpid = bo.getDept_id();
			String dpcode = bo.getDept_code();
			List<Object> params3 = new ArrayList<Object>();
			params3.add(dpid);
			CachedRowSet rs3 = dao.queryToCachedRowSetByKey("SY009bmbmBLH_update3", params3);
			if(rs3.next()){
				String ccode = rs3.getString("dept_code");
				String upcode = dpcode;
				if(!ccode.equals(upcode)){
					List<Object> params2 = new ArrayList<Object>();
					params2.add(StringUtils.trimToEmpty(ccode) + "%");
					CachedRowSet rs2 = dao.queryToCachedRowSetByKey("SY009bmbmBLH_update4", params2);
					while(rs2.next()){
						TSysDeptPO bo1 = new TSysDeptPO();
						String cpcode = rs2.getString("dept_code");	
						String ccc = cpcode.replace(ccode, upcode);
						bo1.setDept_code(ccc);
						bo1.setDept_id(rs2.getInt("dept_id"));
						dao.updateSingleRow(bo1);
					}
				}
			}
			dao.updateSingleRow(bo);
		} catch (Exception e) {
			throw new BaseCheckedException("10000000", e);
		}
		return new DataResponseEvent();
	}

	public BaseResponseEvent delete(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		TSysDeptPO bo=null;
		try {
			bo = (TSysDeptPO) req.getForm("form");
			List<Object> params = new ArrayList<Object>();
			params.add(bo.getDept_id());
			CachedRowSet rs = dao.queryToCachedRowSetByKey("SY009bmbmBLH_delete1", params);
			if(rs.next())throw new Exception();
		}catch(Exception e){
			throw new BaseCheckedException("01009009", e);
		}
		try {
			List<Object> params = new ArrayList<Object>();
			params.add(bo.getDept_id());
			CachedRowSet rs = dao.queryToCachedRowSetByKey("SY009bmbmBLH_delete2", params);
			if(rs.next())throw new Exception();
		}catch(Exception e){
			throw new BaseCheckedException("01009004", e);
		}
		try {
			if (bo.getIs_stop()==2)throw new Exception();
		}catch(Exception e){
			throw new BaseCheckedException("01009007", e);
		}
		try {
			List<Object> params = new ArrayList<Object>();
			params.add(bo.getDept_id());
			CachedRowSet rs = dao.queryToCachedRowSetByKey("SY009bmbmBLH_delete3", params);
			if(rs.next()){
				List<Object> params3 = new ArrayList<Object>();
				params3.add(bo.getDept_id());
				params3.add(rs.getInt("super_id"));
				CachedRowSet rs1 = dao.queryToCachedRowSetByKey("SY009bmbmBLH_delete4", params3);
				if(!rs1.next()){
					   TSysDeptPO bo2 = new TSysDeptPO();
		   			   bo2.setIs_last(1);
		   			   bo2.setDept_id(rs.getInt("super_id"));
		   			   dao.updateSingleRow(bo2);
		   			   res.addAttr("islast","1");
				}				
			}			
			bo.setIs_stop(1);
			dao.updateSingleRow(bo);
			Integer aa = bo.getDept_id();
			TSysDeptPO bo1 = new TSysDeptPO();
			bo1.setIs_stop(1);
			List<Object> params1 = new ArrayList<Object>();
			params1.add(aa);
			String sql1 = "update t_sys_dept set is_stop=1 where super_id = ?";
			dao.updateBatchRowBySQL(sql1, params1);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseCheckedException("10000000", e);
		}

		return res;
	}
}
