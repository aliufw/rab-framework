package com.rab.sys.sysmanager.base.blh;

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
import com.rab.framework.comm.util.DateUtils;
import com.rab.framework.component.dictcache.CacheUpdateHelper;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.domain.blh.BaseDomainBLH;
import com.rab.sys.sysmanager.base.model.TAcctYearPO;
import com.rab.sys.sysmanager.base.model.TSysCopyPO;

/**
 * 
 * @Description：账套信息
 * @Author：jingyang
 * @Date：2010-10-21
 */
public class SY002ztxxBLH extends BaseDomainBLH {
	private static final LogWritter logger = LogFactory
			.getLogger(SY002ztxxBLH.class);

	public BaseResponseEvent initInfo(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		String copyCode = req.getAttr("copyCode") == null ? null : (String)req.getAttr("copyCode");

		DataResponseEvent res = new DataResponseEvent();
		if(copyCode != null){
			try {
				String sqlKey = "SY002ztxxBLH_getCopyInfoByCode";
				PersistenceDAO dao = this.domainSession.getPersistenceDAO();
				List<Object> params = new ArrayList<Object>();
				params.add(new Integer(copyCode));
				CachedRowSet crs = dao.queryToCachedRowSetByKey(sqlKey, params);
				res.addForm("form", crs);
				res.addAttr("copyCode", copyCode);
			} catch (Exception e) {
				throw new BaseCheckedException("01002001", e);
			}
		}
		res.setPage("/page/sysmanager/base/sy002_ztxx_info.jsp");
		return res;
	}
	
	public BaseResponseEvent initAcctYearInfo(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		String copyCode = req.getAttr("copyCode") == null ? null : (String) req
				.getAttr("copyCode");
		String acctYear = req.getAttr("acctYear") == null ? null : (String) req
				.getAttr("acctYear");
		

		DataResponseEvent res = new DataResponseEvent();
		try {
			if (copyCode != null) {
				res.addAttr("copyCode", copyCode);
				if (acctYear != null) {

					String sqlKey = "SY002ztxxBLH_getAcctYearInfo";
					PersistenceDAO dao = this.domainSession.getPersistenceDAO();
					List<Object> params = new ArrayList<Object>();
					params.add(acctYear);
					params.add(copyCode);
					CachedRowSet crs = dao.queryToCachedRowSetByKey(sqlKey,
							params);
					res.addForm("form", crs);
					res.addAttr("acctYear", acctYear);

				}
				List<Object> params1 = new ArrayList<Object>();
				params1.add(copyCode);
				CachedRowSet crs1 = this.domainSession.getPersistenceDAO()
						.queryToCachedRowSetByKey(
								"SY002ztxxBLH_getCopyInfoByCode", params1);
				while (crs1.next()) {
					res.addAttr("copy_begin_year", crs1.getString("COPY_START_YEAR"));
					res.addAttr("copy_begin_month", crs1.getString("COPY_START_MONTH"));
				}

			}
		} catch (Exception e) {
			throw new BaseCheckedException("01002002", e);
		}
		return res;
	}
	
	
	/**
	 * 获取账套列表
	 * @param reqEvent
	 * @return
	 * @throws BaseCheckedException
	 */
	public BaseResponseEvent getCopyListByComp(BaseRequestEvent reqEvent) throws BaseCheckedException {
			
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
			String sqlKey = "SY002ztxxBLH_getCopyListByComp";
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			List<Object> params = new ArrayList<Object>();
			String compId = (String)req.getAttr("compId");
			params.add(new Integer(compId));
			CachedRowSet crs = dao.queryToCachedRowSetByKey(sqlKey, params);
			res.addTable("copyList", crs);
		} catch (Exception e) {
			throw new BaseCheckedException("01002003", e);
		}
		return res;
	}
	
	/**
	 * 获取年度账列表
	 * @param reqEvent
	 * @return
	 * @throws BaseCheckedException
	 */
	public BaseResponseEvent getAcctYearByCopy(BaseRequestEvent reqEvent) throws BaseCheckedException {
			
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
			String sqlKey = "SY002ztxxBLH_getAcctYearByCopy";
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			List<Object> params = new ArrayList<Object>();
			String copyCode = (String)req.getAttr("copyCode");
			params.add(new Integer(copyCode));
			CachedRowSet crs = dao.queryToCachedRowSetByKey(sqlKey, params);
			res.addTable("acctYearList", crs);
			res.addAttr("copyCode", copyCode);
		} catch (Exception e) {
			throw new BaseCheckedException("01002004", e);
		}
		return res;
	}

	public BaseResponseEvent saveOrUpdateYear(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;

		try {
			TAcctYearPO acctYearBO = (TAcctYearPO) req.getForm("form",
					TAcctYearPO.class);
			if (req.getAttr("copyId") != null
					&& ((String) req.getAttr("copyId")).length() > 0) {
				acctYearBO.setCopy_code((String) req.getAttr("copyId"));
			}
			if (req.getAttr("acctYear") != null
					&& ((String) req.getAttr("acctYear")).length() > 0) {
				// update

				this.domainSession.getPersistenceDAO().updateSingleRow(
						acctYearBO);
			} else {
				//check before add

				List<Object> params = new ArrayList<Object>();
				params.add(acctYearBO.getAcct_year());
				params.add(acctYearBO.getCopy_code());
				CachedRowSet rs = this.domainSession.getPersistenceDAO().queryToCachedRowSetByKey("SY002ztxxBLH_getAcctYearInfo", params);
				if(rs.next()){
					throw new BaseCheckedException("01002013");
				}
				// add

				this.domainSession.getPersistenceDAO().insertSingleRow(
						acctYearBO);
			}

		} catch (Exception e) {
			if(e instanceof BaseCheckedException){
				throw (BaseCheckedException)e;
			}else{
				throw new BaseCheckedException("01002006", e);
			}
		}

		DataResponseEvent res = new DataResponseEvent();
		res.setSuccess(true);

		return res;
	}
	public BaseResponseEvent saveOrUpdateCopy(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		int result = 0;
		try {
			TSysCopyPO sysCopyBO = (TSysCopyPO) req.getForm("form",
					TSysCopyPO.class);
			if (req.getAttr("copyId") != null
					&& ((String) req.getAttr("copyId")).length() > 0) {
				//update
				result = this.domainSession.getPersistenceDAO().updateSingleRow(
						sysCopyBO);
			} else {
				//check before add
				List<Object> params = new ArrayList<Object>();
				params.add(sysCopyBO.getCopy_code());
				CachedRowSet rs = this.domainSession.getPersistenceDAO().queryToCachedRowSetByKey("SY002ztxxBLH_getCopyInfoByCode", params);
				if(rs.next()){
					throw new BaseCheckedException("01002010");
				}
				//add
				if (req.getAttr("compId") != null
						&& ((String) req.getAttr("compId")).length() > 0){
					sysCopyBO.setComp_id(Integer.parseInt((String) req.getAttr("compId")));
					//TODO
					sysCopyBO.setCopy_type(1);
					sysCopyBO.setIs_analyse(0);
					sysCopyBO.setIs_check(0);
					//sysCopyBO.setIs_stop(0);
					if(this.domainSession.getPersistenceDAO().insertSingleRow(
							sysCopyBO) != null){
						result = 1;
					}
				}		
				
			}
			if(result > 0){
				//更新字典表注册表中的版本序号并返回更新标志以供action刷新缓存字典表
				CacheUpdateHelper.setUpdateFlag("t_sys_copy", this.domainSession.getPersistenceDAO(), res);
			}

		} catch (Exception e) {
			if(e instanceof BaseCheckedException){
				throw (BaseCheckedException)e;
			}else{
				throw new BaseCheckedException("01002007", e);
			}
			
		}
		
		res.setSuccess(true);

		return res;
	}
	
	/**
	 * 获取单位列表
	 * 
	 * @param reqEvent
	 * @return
	 * @throws BaseCheckedException
	 */
	public BaseResponseEvent initCompList(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataResponseEvent res = new DataResponseEvent();
		try {
			String sqlKey = "SY016yhsqBLH_initCompList_byUser";
			List<Object> params = new ArrayList<Object>();
			params.add(this.domainSession.getUser().getUserid());
			if(this.domainSession.getUser().isAdmin()){
				if(this.domainSession.getUser().getUserid() == 1){
					//super admin					
					sqlKey = "SY002ztxxBLH_getAllCompList";
					params = null;
					
				}else{
					sqlKey = "SY016yhsqBLH_initCompList_byAdmin";
					params.add(this.domainSession.getUser().getUserid());
				}
				
			}			
			
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			CachedRowSet crs = dao.queryToCachedRowSetByKey(sqlKey, params);
			res.addCombo("comp", crs);
		} catch (Exception e) {
			throw new BaseCheckedException("01002008", e);
		}
		return res;
	}

	public BaseResponseEvent deleteAcctYear(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;

		try {
			List<TAcctYearPO> deleteData = (List<TAcctYearPO>) req
					.getTable("acctYearList", TAcctYearPO.class);
			this.domainSession.getPersistenceDAO().deleteBatchRow(deleteData);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseCheckedException("01002009", e);
		}
		DataResponseEvent res = new DataResponseEvent();
		res.setSuccess(true);

		return res;
	}

	public BaseResponseEvent deleteCopy(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;

		try {
			List<TSysCopyPO> daleteData = (List<TSysCopyPO>) req.getTable(
					"copyList", TSysCopyPO.class);
			for(TSysCopyPO tSysCopyBO : daleteData){
				//check before delete				
				List<Object> params = new ArrayList<Object>();
				params.add(tSysCopyBO.getCopy_code());
				CachedRowSet rs = this.domainSession.getPersistenceDAO().queryToCachedRowSetByKey("SY002ztxxBLH_getAcctYearByCopy", params);
				if(rs.next()){
					List<String> exParams = new ArrayList<String>();
					exParams.add(tSysCopyBO.getCopy_code());
					throw new BaseCheckedException("01002012", exParams);
				}
			}			

			this.domainSession.getPersistenceDAO().deleteBatchRow(daleteData);

		} catch (Exception e) {
			logger.error(e.getMessage());
			if(e instanceof BaseCheckedException){
				throw (BaseCheckedException)e;
			}else{
				throw new BaseCheckedException("01002011", e);
			}
		}
		DataResponseEvent res = new DataResponseEvent();
		res.setSuccess(true);

		return res;
	}
}
