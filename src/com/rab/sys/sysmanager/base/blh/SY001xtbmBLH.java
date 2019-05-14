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
import com.rab.framework.domain.blh.BaseDomainBLH;
import com.rab.sys.security.LoginAuthorizationManager;
import com.rab.sys.sysmanager.base.model.TSysParaDataPO;

/**
 * 
 * @Description：科目编码
 * @Author：jingyang
 * @Date：2010-10-19
 */
public class SY001xtbmBLH extends BaseDomainBLH {
	private static final LogWritter logger = LogFactory
			.getLogger(SY001xtbmBLH.class);

	public BaseResponseEvent init(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataResponseEvent res = new DataResponseEvent();

		
		try {
			
			List<Object> params = new ArrayList<Object>();
			params.add(super.domainSession.getLogonEnvironment().getOrgId());
			
			
			int editFlag1 = 0;
//			int editFlag2 = 0;
//			int editFlag3 = 0;
//			int editFlag4 = 0;
			CachedRowSet crs1 = this.domainSession.getPersistenceDAO()
					.queryToCachedRowSetByKey("SY001xtbmBLH_checkSysDept",
							params);
//			CachedRowSet crs2 = this.domainSession.getPersistenceDAO()
//					.queryToCachedRowSetByKey("SY001xtbmBLH_checkSysVen",
//							params);
//			CachedRowSet crs3 = this.domainSession
//					.getPersistenceDAO()
//					.queryToCachedRowSetByKey("SY001xtbmBLH_checkSysDuty", null);
//			CachedRowSet crs4 = this.domainSession.getPersistenceDAO()
//					.queryToCachedRowSetByKey("SY001xtbmBLH_checkSysDict",
//							params);

			
			while (crs1.next()) {
				if (crs1.getInt(1) == 0) {
					//无相关记录，可编辑
					editFlag1 = 1;
				}
			}
//			while (crs2.next()) {
//				if (crs2.getInt(1) == 0) {
//					editFlag2 = 1;
//				}
//			}
//			while (crs3.next()) {
//				if (crs3.getInt(1) == 0) {
//					editFlag3 = 1;
//				}
//			}
//			while (crs4.next()) {
//				if (crs4.getInt(1) == 0) {
//					editFlag4 = 1;
//				}
//			}
			res.addAttr("edit1", editFlag1);
//			res.addAttr("edit2", editFlag2);
//			res.addAttr("edit3", editFlag3);
//			res.addAttr("edit4", editFlag4);
			
			String sql = "SELECT para_code,para_name,para_value,para_option,describe,para_type,data_type,comp_code,copy_code"
					+ " FROM t_sys_para_data"
					+ " WHERE para_code like '02%'";
			
			if(!LoginAuthorizationManager.ADMIN_COMPANY_CODE.equals(super.domainSession.getLogonEnvironment().getOrgId())){
				sql += " and comp_code=? ";				
			}
			sql +=  " ORDER BY para_code ASC";
			CachedRowSet crs = null;
			if(LoginAuthorizationManager.ADMIN_COMPANY_CODE.equals(super.domainSession.getLogonEnvironment().getOrgId())){
				//超级管理员，不加单位过滤
				crs = this.domainSession.getPersistenceDAO()
				.queryToCachedRowSetBySQL(sql, null);
			}else{
				crs = this.domainSession.getPersistenceDAO()
				.queryToCachedRowSetBySQL(sql, params);
			}			

			res.addTable("table", crs, null);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseCheckedException("01001001", e);
		}

		res.setPage("/page/sysmanager/base/sy001_xtbm_main.jsp");

		return res;
	}

	public BaseResponseEvent update(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		
		try {
			List<TSysParaDataPO> updateData = (List<TSysParaDataPO>)req.getUpdateTable("table", TSysParaDataPO.class);
			List<TSysParaDataPO> insertData = (List<TSysParaDataPO>)req.getInsertTable("table", TSysParaDataPO.class);
			this.domainSession.getPersistenceDAO().updateBatchRow(updateData);
			
			for(TSysParaDataPO sysParaDataBO : insertData){
				//check before add
				String sql = "SELECT * FROM t_sys_para_data"
					+ " WHERE para_code = ?";
				List<Object> params = new ArrayList<Object>();
				params.add(sysParaDataBO.getPara_code());
				CachedRowSet rs = this.domainSession.getPersistenceDAO().queryToCachedRowSetBySQL(sql, params);
				if(rs.next()){
					List<String> exParams = new ArrayList<String>();
					exParams.add(sysParaDataBO.getPara_code());
					throw new BaseCheckedException("01001003", exParams);
				}
			}
			this.domainSession.getPersistenceDAO().insertBatchRow(insertData);
		} catch (Exception e) {
			logger.error(e.getMessage());
			if(e instanceof BaseCheckedException){
				throw (BaseCheckedException)e;
			}else{
				throw new BaseCheckedException("01001002", e);
			}
		}		
		DataResponseEvent res = new DataResponseEvent();
		res.setSuccess(true);
		
		return init(reqEvent);
	}	
}
