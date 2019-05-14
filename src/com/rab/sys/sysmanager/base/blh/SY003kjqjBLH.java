package com.rab.sys.sysmanager.base.blh;

import java.util.ArrayList;
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
import com.rab.framework.comm.util.DateUtils;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.domain.blh.BaseDomainBLH;
import com.rab.sys.sysmanager.base.model.TAcctYearPeriodPO;

/**
 * 
 * @Description：会计期间
 * @Author：jingyang
 * @Date：2010-10-21
 */
public class SY003kjqjBLH extends BaseDomainBLH {
	private static final LogWritter logger = LogFactory
			.getLogger(SY003kjqjBLH.class);

	public BaseResponseEvent initAcctMonthInfo(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		String copyCode = req.getAttr("copy") == null ? null : (String) req
				.getAttr("copy");
		String acctYear = req.getAttr("acctYear") == null ? null : (String) req
				.getAttr("acctYear");
		String company = req.getAttr("company") == null ? null : (String) req
				.getAttr("company");
		String acctMonth = req.getAttr("acctMonth") == null ? null : (String) req
				.getAttr("acctMonth");

		DataResponseEvent res = new DataResponseEvent();

			res.addAttr("copy", copyCode);
			res.addAttr("company", company);
			res.addAttr("acctYear", acctYear);
			if (acctMonth != null) {
				//update
				try {
					String sqlKey = "SY003kjqjBLH_getAcctYearPeriodInfo";
					PersistenceDAO dao = this.domainSession.getPersistenceDAO();
					List<Object> params = new ArrayList<Object>();
					params.add(acctYear);
					params.add(copyCode);
					params.add(acctMonth);
					CachedRowSet crs = dao.queryToCachedRowSetByKey(sqlKey,
							params);
					res.addForm("form", crs);
					res.addAttr("acctMonth", acctMonth);
				} catch (Exception e) {
					throw new BaseCheckedException("01003006", e);
				}
			}
		
		return res;
	}
	
	public BaseResponseEvent query(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		Map<String,Object> map = req.getFormData("form");

		String copyCode = (String)map.get("copy");
		String acctYear = (String)map.get("acctYear");
		
		DataResponseEvent res = new DataResponseEvent();

		List<Object> params = new ArrayList<Object>();
		params.add(acctYear);
		params.add(copyCode);		

		try {
			CachedRowSet crs = this.domainSession.getPersistenceDAO()
					.queryToCachedRowSetByKey("SY003kjqjBLH_getAcctYearPeriodList", params);

			res.addTable("table", crs, null);
			
			//取当前年度账设置中的期间数量，做页面控制
			CachedRowSet crs1 = this.domainSession.getPersistenceDAO()
			.queryToCachedRowSetByKey("SY002ztxxBLH_getAcctYearInfo", params);
			while(crs1.next()){
				res.addAttr("period_num", crs1.getInt("PERIOD_NUM"));
				res.addAttr("begin_date", DateUtils.toDateStr(crs1.getDate("BEGIN_DATE")));
				res.addAttr("end_date", DateUtils.toDateStr(crs1.getDate("END_DATE")));
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseCheckedException("01003001", e);
		}

		res.setPage("/page/sysmanager/base/sy003_kjqj_main.jsp");

		return res;
	}
	
	
	/**
	 * 获取帐套列表
	 * @param reqEvent
	 * @return
	 * @throws BaseCheckedException
	 */
	public BaseResponseEvent initCopyListByComp(BaseRequestEvent reqEvent) throws BaseCheckedException {
			
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
			String sqlKey = "SY016yhsqBLH_initCopyListByComp";
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			List<Object> params = new ArrayList<Object>();
			String compId = (String)req.getAttr("compId");
			params.add(new Integer(compId));
			CachedRowSet crs = dao.queryToCachedRowSetByKey(sqlKey, params);
			res.addCombo("copy", crs);
		} catch (Exception e) {
			throw new BaseCheckedException("01003002", e);
		}
		return res;
	}
	
	
	/**
	 * 获取会计年度列表
	 * @param reqEvent
	 * @return
	 * @throws BaseCheckedException
	 */
	public BaseResponseEvent getAcctYearList(BaseRequestEvent reqEvent) throws BaseCheckedException {
			
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
			String sqlKey = "SY003kjqjBLH_getAcctYear";
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			List<Object> params = new ArrayList<Object>();
			String copyId = (String)req.getAttr("copyId");
			params.add(new Integer(copyId));
			CachedRowSet crs = dao.queryToCachedRowSetByKey(sqlKey, params);
			res.addCombo("acctYear", crs);
		} catch (Exception e) {
			throw new BaseCheckedException("01003003", e);
		}
		return res;
	}
	
	
	public BaseResponseEvent saveOrUpdatePeriod(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;

		try {
			TAcctYearPeriodPO acctYearPeriodBO = (TAcctYearPeriodPO) req.getForm("form",
					TAcctYearPeriodPO.class);
			if (req.getAttr("copy") != null
					&& ((String) req.getAttr("copy")).length() > 0) {
				acctYearPeriodBO.setCopy_code((String) req.getAttr("copy"));
			}
			if (req.getAttr("acctYear") != null
					&& ((String) req.getAttr("acctYear")).length() > 0) {
				acctYearPeriodBO.setAcct_year((String) req.getAttr("acctYear"));
			}
			if (req.getAttr("acctMonth") != null
					&& ((String) req.getAttr("acctMonth")).length() > 0) {
				// update

				this.domainSession.getPersistenceDAO().updateSingleRow(
						acctYearPeriodBO);
			} else {
				// check before add

				List<Object> params = new ArrayList<Object>();
				params.add(acctYearPeriodBO.getAcct_year());
				params.add(acctYearPeriodBO.getCopy_code());
				params.add(acctYearPeriodBO.getAcct_month());
				CachedRowSet rs = this.domainSession.getPersistenceDAO()
						.queryToCachedRowSetByKey(
								"SY003kjqjBLH_getAcctYearPeriodInfo", params);
				if (rs.next()) {
					throw new BaseCheckedException("01003007");
				}
				// add
				acctYearPeriodBO.setCash_flag(0);
				acctYearPeriodBO.setFix_flag(0);
				acctYearPeriodBO.setMat_flag(0);
				acctYearPeriodBO.setMed_flag(0);
				acctYearPeriodBO.setDrugstore_flag(0);
				acctYearPeriodBO.setWage_flag(0);
				acctYearPeriodBO.setAcc_flag(0);
				acctYearPeriodBO.setBudg_flag(0);
				acctYearPeriodBO.setPerf_flag(0);
				acctYearPeriodBO.setCost_flag(0);
				acctYearPeriodBO.setIs_depreciation(0);
				acctYearPeriodBO.setDj_flag(0);
				
				this.domainSession.getPersistenceDAO().insertSingleRow(
						acctYearPeriodBO);
			}

		} catch (Exception e) {
			if (e instanceof BaseCheckedException) {
				throw (BaseCheckedException) e;
			} else {
				throw new BaseCheckedException("01003004", e);
			}
		}

		DataResponseEvent res = new DataResponseEvent();
		res.setSuccess(true);

		return res;
	}

	public BaseResponseEvent delete(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;

		try {
			List<TAcctYearPeriodPO> daleteData = (List<TAcctYearPeriodPO>) req
					.getTable("table", TAcctYearPeriodPO.class);
			this.domainSession.getPersistenceDAO().deleteBatchRow(daleteData);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseCheckedException("01003005", e);
		}
		DataResponseEvent res = new DataResponseEvent();
		res.setSuccess(true);

		return res;
	}
	
}
