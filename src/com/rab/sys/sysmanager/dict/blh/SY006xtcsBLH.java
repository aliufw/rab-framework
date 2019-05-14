package com.rab.sys.sysmanager.dict.blh;

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
import com.rab.sys.sysmanager.base.model.TSysParaDataPO;

/**
 * 
 * @Description£ºÏµÍ³±àÂë
 * @Author£ºjingyang
 * @Date£º2010-10-21
 */
public class SY006xtcsBLH extends BaseDomainBLH {
	private static final LogWritter logger = LogFactory
			.getLogger(SY006xtcsBLH.class);

	public BaseResponseEvent init(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataResponseEvent res = new DataResponseEvent();
		
		try {
//			int editFlag1 = 0;
//			int editFlag2 = 0;
//			int editFlag3 = 0;
//			int editFlag4 = 0;
//			int editFlag5 = 0;
//			CachedRowSet crs1 = this.domainSession.getPersistenceDAO()
//					.queryToCachedRowSetByKey("SY006xtcsBLH_checkSysComp",
//							null);
//			CachedRowSet crs2 = this.domainSession.getPersistenceDAO()
//					.queryToCachedRowSetByKey("SY006xtcsBLH_checkAcctLedger",
//							null);
//			CachedRowSet crs3 = this.domainSession.getPersistenceDAO()
//					.queryToCachedRowSetByKey(
//							"SY006xtcsBLH_checkAcctCheckItems", null);
//			CachedRowSet crs4 = this.domainSession.getPersistenceDAO()
//					.queryToCachedRowSetByKey("SY006xtcsBLH_checkBudgMateType",
//							null);
//			CachedRowSet crs5 = this.domainSession.getPersistenceDAO()
//					.queryToCachedRowSetByKey("SY006xtcsBLH_checkEquiKindDict",
//							null);
			
//			while (crs1.next()) {
//				if (crs1.getInt(1) == 0) {
//					editFlag1 = 1;
//				}
//			}
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
//			while (crs5.next()) {
//				if (crs5.getInt(1) == 0) {
//					editFlag5 = 1;
//				}
//			}
//			res.addAttr("edit1", editFlag1);
//			res.addAttr("edit2", editFlag2);
//			res.addAttr("edit3", editFlag3);
//			res.addAttr("edit4", editFlag4);
//			res.addAttr("edit5", editFlag5);
			
			String sql = "SELECT para_code,para_name,para_value,para_option,describe,para_type,data_type,comp_code,copy_code"
					+ " FROM t_sys_para_data"
					+ " WHERE comp_code is null and para_code like '01%'"
					+ " ORDER BY para_code ASC";
			
			CachedRowSet crs = this.domainSession.getPersistenceDAO()
					.queryToCachedRowSetBySQL(sql, null);

			res.addTable("table", crs, null);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseCheckedException("01006001", e);
		}

		res.setPage("/page/sysmanager/dict/sy006_xtcs_main.jsp");

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
					throw new BaseCheckedException("01006004", exParams);
				}
			}
			this.domainSession.getPersistenceDAO().insertBatchRow(insertData);
		} catch (Exception e) {
			logger.error(e.getMessage());
			if(e instanceof BaseCheckedException){
				throw (BaseCheckedException)e;
			}else{
				throw new BaseCheckedException("01006002", e);
			}
		}
		DataResponseEvent res = new DataResponseEvent();
		res.setSuccess(true);

		return init(reqEvent);
	}

	public BaseResponseEvent delete(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;

		try {
			List<TSysParaDataPO> deleteData = (List<TSysParaDataPO>) req
					.getTable("table", TSysParaDataPO.class);
			this.domainSession.getPersistenceDAO().deleteBatchRow(deleteData);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseCheckedException("01006003", e);
		}
		DataResponseEvent res = new DataResponseEvent();
		res.setSuccess(true);

		return res;
	}
}
