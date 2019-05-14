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
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.domain.blh.BaseDomainBLH;
import com.rab.sys.sysmanager.authority.model.TSysGroupPO;
import com.rab.sys.sysmanager.authority.model.TSysUserPO;
import com.rab.sys.sysmanager.info.model.TSysEmpPO;

/**
 * 
 * @Description：职工信息
 * @Author：manan
 * @Date：2010-10-13
 */
public class SY010zgxxBLH extends BaseDomainBLH {
	private static final LogWritter logger = LogFactory
			.getLogger(SY007kmbmBLH.class);

	public BaseResponseEvent init(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		try{
			DataRequestEvent req = (DataRequestEvent) reqEvent;
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			DataResponseEvent res = new DataResponseEvent();
			CachedRowSet rs = dao.queryToCachedRowSetByKey("SY010zgxxBLH_init",null);
			res.addCombo("comp_caption", rs);
			return res;
		   }catch(Exception e){
			   throw new BaseCheckedException("", e);
	      }
	}

	public BaseResponseEvent tableQuery(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		try {
			DataRequestEvent req = (DataRequestEvent) reqEvent;
			DataResponseEvent res = new DataResponseEvent();
			String id = req.getAttr("coCode") == null ? null : req.getAttr(
					"coCode").toString();
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			List<Object> params = new ArrayList<Object>();
			params.add(id);
			try {
				CachedRowSet rs = dao.queryToCachedRowSetByKey("SY010zgxxBLH_tableQuery", params);
				res.addTable("list", rs);
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new BaseCheckedException("10000000", e);
			}
			return res;
		} catch (Exception e) {
			throw new BaseCheckedException("01010001", e);
		}
	}

	public BaseResponseEvent detailinit(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;

		DataResponseEvent res = new DataResponseEvent();

		res.setPage("/page/sysmanager/info/sy010_zgxx_detail.jsp");

		return res;
	}
	
	public BaseResponseEvent add(BaseRequestEvent reqEvent) 
	 throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		try{
			TSysEmpPO bo = (TSysEmpPO) req.getForm("form");
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			List<Object> params = new ArrayList<Object>();
			params.add(bo.getEmp_code());
			CachedRowSet rs = dao.queryToCachedRowSetByKey("SY010zgxxBLH_add", params);
			if(rs.next()){throw new Exception();}
		}catch(Exception e){
			throw new BaseCheckedException("01010005", e);
		}
		try {
			String supperid = (String)req.getAttr("superid");
			
			TSysEmpPO bo = (TSysEmpPO) req.getForm("form");
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			long sq = dao.getSequence("SQ_SYS_EMP");
			Integer s = new Integer((int)sq);
			bo.setEmp_id(s);
			bo.setDept_id(Integer.parseInt(supperid));
			dao.insertSingleRow(bo);
			DataResponseEvent res = new DataResponseEvent();
			res.addAttr("emp_id", s.toString());
            return res;
		} catch (Exception e) {
			throw new BaseCheckedException("01010002", e);
		}
	}

	public BaseResponseEvent update(BaseRequestEvent reqEvent)
			throws BaseCheckedException {

		DataRequestEvent req = (DataRequestEvent) reqEvent;
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		try{
			TSysEmpPO bo = (TSysEmpPO) req.getForm("form");
			List<Object> params = new ArrayList<Object>();
			params.add(bo.getEmp_code());
			params.add(bo.getEmp_id());
			CachedRowSet rs = dao.queryToCachedRowSetByKey("SY010zgxxBLH_update", params);
			if(rs.next()){throw new Exception();}
		}catch(Exception e){
			throw new BaseCheckedException("01010005", e);
		}
		try {
			TSysEmpPO bo = (TSysEmpPO) req.getForm("form");
			dao.updateSingleRow(bo);
			return new DataResponseEvent();
		} catch (Exception e) {
			throw new BaseCheckedException("01010003", e);
		}
		
	}
	public BaseResponseEvent delete(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		try {
			List list =  req.getTable("list");
			if (list != null && list.size() > 0) {
				int len = list.size();
				for (int i = 0; i < len; i++) {
					TSysEmpPO bo = (TSysEmpPO) list.get(i);
					bo.setIs_stop(1);
					dao.updateSingleRow(bo);
					Integer emp_id = bo.getEmp_id();
					TSysUserPO bo1 = new TSysUserPO();
					List<Object> params = new ArrayList<Object>();
					params.add(emp_id);
					bo1.setScbj(1);
					String sql="update t_sys_user set scbj=1 where emp_id=?";
				    dao.updateBatchRowBySQL(sql, params);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseCheckedException("01010004", e);
		}

		return res;
	}

}
