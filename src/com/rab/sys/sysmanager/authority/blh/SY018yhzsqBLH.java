package com.rab.sys.sysmanager.authority.blh;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import sun.jdbc.rowset.CachedRowSet;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.dto.event.DataRequestEvent;
import com.rab.framework.comm.dto.event.DataResponseEvent;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.security.User;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.dao.PersistenceUtils;
import com.rab.framework.domain.blh.BaseDomainBLH;
import com.rab.sys.sysmanager.authority.model.TSysGroupCodePO;
import com.rab.sys.sysmanager.authority.model.TSysGroupPermPO;
import com.rab.sys.sysmanager.authority.model.TSysTablePO;

/**
 * @Description：用户功能授权
 * @Author：ZhangBin
 * @Date：2010-10-18
 */
public class SY018yhzsqBLH extends BaseDomainBLH {
	
	public BaseResponseEvent initGroupQxDetail(BaseRequestEvent reqEvent)
	throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			List<Object> params1 = new ArrayList<Object>();
			String groupId = (String)req.getAttr("groupId");
			params1.add(groupId);
			String sql2 = "SY018yhzsqBLH_initGroupQxDetail";
			CachedRowSet rs2 = dao.queryToCachedRowSetByKey(sql2,params1);
			res.addTable("list", rs2);	
			
		} catch (Exception e) {
			throw new BaseCheckedException("01018001", e);
		}
		return res;
	}
	
	public BaseResponseEvent initGroupQxTree(BaseRequestEvent reqEvent)
	throws BaseCheckedException {
		
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
		
			List<Object> params2 = new ArrayList<Object>();
		
			String groupId = (String)req.getAttr("groupId");
			String copyCode = (String)req.getAttr("copyCode");
			String modCode = (String)req.getAttr("modCode");
			
			String sqlKey2 = "initGroupQxTree";
			params2.add(groupId);
			params2.add(copyCode);
			params2.add(modCode);
		
			CachedRowSet crs2 = this.domainSession.getPersistenceDAO()
					.queryToCachedRowSetByKey(sqlKey2, params2);
		
			res.addTree("tree",crs2, null);
		
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseCheckedException("01018001", e);
		}
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

		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
			String sqlKey = "SY016yhsqBLH_initCompList_byUser";
			User user = this.domainSession.getUser();

			List<Object> params = new ArrayList<Object>();

			if(user.isSuperadmin()){
				sqlKey = "SY018yhzsqBLH_initCompList_bySuperAdmin";
				
			} else if(user.isAdmin()) {
				sqlKey = "SY016yhsqBLH_initCompList_byAdmin";
				params.add(user.getUserid());
				params.add(user.getUserid());
			} else {
				params.add(user.getUserid());
			}

			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			CachedRowSet crs = dao.queryToCachedRowSetByKey(sqlKey, params);
			res.addCombo("comp", crs);
		} catch (Exception e) {
			throw new BaseCheckedException("01018002", e);
		}
		return res;
	}

	public BaseResponseEvent initModList(BaseRequestEvent reqEvent)
			throws BaseCheckedException {

		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
			String sqlKey = "";
			String copyCode = (String) req.getAttr("copyCode");
			User user = this.domainSession.getUser();

			List<Object> params = new ArrayList<Object>();
			
			boolean flag = true;
			if (user.isSuperadmin()) {
				
				if(StringUtils.isNotBlank(copyCode)){
					sqlKey = "SY016yhsqBLH_initModList_byCopyWithSuperAdmin";
					params.add(copyCode);
				}else{
					sqlKey = "SY016yhsqBLH_initModList_bySuperAdmin";
				}
				
			} else if (user.isAdmin()) {
				if(StringUtils.isNotBlank(copyCode)){
					sqlKey = "SY016yhsqBLH_initModList_byCopyAndUser";
					params.add(user.getUserid());
					params.add(copyCode);
				}else{
					sqlKey = "SY016yhsqBLH_initModList_byAdmin";
					params.add(user.getUserid());
				}

			} else{
				flag = false;
				
			}
			if(flag){
				PersistenceDAO dao = this.domainSession.getPersistenceDAO();
				CachedRowSet crs = dao.queryToCachedRowSetByKey(sqlKey, params);
	
				res.addCombo("mod", crs);
			}

		} catch (Exception e) {
			throw new BaseCheckedException("01018003", e);
		}
		return res;
	}

	/**
	 * 用户组功能授权
	 * 
	 * @param reqEvent
	 * @return
	 * @throws BaseCheckedException
	 */
	public BaseResponseEvent yhzGnsq(BaseRequestEvent reqEvent)
			throws BaseCheckedException {

		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			String groupId = StringUtils.trimToEmpty((String) req
					.getAttr("groupId"));
			String modCode = StringUtils.trimToEmpty((String) req
					.getAttr("modCode"));
			String copyCode = (String) req.getAttr("copyCode");

			String adds = StringUtils.trimToEmpty((String) req.getAttr("adds"));

			if (StringUtils.isNotBlank(adds)) {
				String[] insertList = adds.split("_");
				List<TSysGroupPermPO> addList = new ArrayList<TSysGroupPermPO>();
				if (insertList != null && insertList.length > 0) {
					for (int i = 0; i < insertList.length; i++) {
						TSysGroupPermPO bo = new TSysGroupPermPO();
						bo.setMod_code(modCode);
						bo.setGroup_id(new Integer(groupId));
						bo.setCopy_code(copyCode);
						bo.setFunc_id(new Integer(insertList[i]));
						addList.add(bo);
					}
					dao.insertBatchRow(addList);
				}
			}

			String dels = StringUtils.trimToEmpty((String) req.getAttr("dels"));
			if (StringUtils.isNotBlank(dels)) {
				String[] deleteList = dels.split("_");
				List<TSysGroupPermPO> delList = new ArrayList<TSysGroupPermPO>();
				if (deleteList != null && deleteList.length > 0) {
					for (int i = 0; i < deleteList.length; i++) {
						TSysGroupPermPO bo = new TSysGroupPermPO();
						bo.setMod_code(modCode);
						bo.setGroup_id(new Integer(groupId));
						bo.setCopy_code(copyCode);
						bo.setFunc_id(new Integer(deleteList[i]));
						delList.add(bo);
					}
					dao.deleteBatchRow(delList);
				}
			}

		} catch (Exception e) {
			throw new BaseCheckedException("01018004", e);
		}
		return res;
	}

	public BaseResponseEvent initCheckTree(BaseRequestEvent reqEvent)
			throws BaseCheckedException {

		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {

			User user = this.domainSession.getUser();

			List<Object> params1 = new ArrayList<Object>();
			List<Object> params2 = new ArrayList<Object>();

			Map<String, Object> map = req.getFormData("form");
			String groupId = (String) map.get("groupId");
			String copyCode = (String) map.get("copy");
			String modCode = (String) map.get("mod");

			String sqlKey1 = "SY016yhsqBLH_initCheckTree_gnzyList";
			String sqlKey2 = "SY018yhzsqBLH_initCheckTree";

			if (user.isSuperadmin()) {
				sqlKey1 = "SY016yhsqBLH_initCheckTree_gnzyAll";
				params1.add(modCode);

			} else {
				params1.add(user.getUserid());
				params1.add(modCode);
			}

			params2.add(groupId);
			params2.add(copyCode);
			params2.add(modCode);

			CachedRowSet crs1 = this.domainSession.getPersistenceDAO()
					.queryToCachedRowSetByKey(sqlKey1, params1);

			CachedRowSet crs2 = this.domainSession.getPersistenceDAO()
					.queryToCachedRowSetByKey(sqlKey2, params2);

			res.addCheckTree("tree", crs1, crs2, null);

		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseCheckedException("01018005", e);
		}
		return res;
	}

	/**
	 * 
	 * @param reqEvent
	 * @return
	 * @throws BaseCheckedException
	 */
	public BaseResponseEvent initCheckList(BaseRequestEvent reqEvent)
			throws BaseCheckedException {

		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
			initDSCheckList(req, res);

		} catch (Exception e) {
			throw new BaseCheckedException("01018005", e);
		}
		return res;
	}

	private void initDSCheckList(DataRequestEvent req, DataResponseEvent res)
			throws BaseCheckedException {
		String sqlKey = "SY016yhsqBLH_getDataSourceById";
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		String sourceId = (String) req.getAttr("sourceId");
		String copyCode = (String) req.getAttr("copyCode");
		String compId = (String) req.getAttr("compId");
		String groupId = (String) req.getAttr("groupId");

		List<Object> params = new ArrayList<Object>(1);
		params.add(sourceId);
		try {
			CachedRowSet crs = dao.queryToCachedRowSetByKey(sqlKey, params);
			List boList = PersistenceUtils
					.rowset2VOList(TSysTablePO.class, crs);
			if (boList != null && boList.size() > 0) {
				TSysTablePO bo = (TSysTablePO) boList.get(0);
				List<Object> pm = new ArrayList<Object>();

				pm.add(new Integer(groupId));
				pm.add(new Integer(compId));
				pm.add(bo.getTable_id());
				StringBuffer sql = new StringBuffer();
				sql
						.append("select * from (select distinct item.")
						.append(bo.getCode_field())
						.append(" column_code ,item.is_stop,")
						.append(" item.")
						.append(bo.getName_field())
						.append(
								" column_value,t.flag,t.table_id,t.comp_id,t.copy_code,t.code,t.group_id,t.acct_year, ")
						.append(
								" case t.is_read when 1 then 1 else 0 end is_read, case t.is_write when 1 then 1 else 0 end is_write from ")
						.append(bo.getTable_id())
						.append(" item ")
						.append(
								" left join (select uc.*,'true' flag from t_sys_group_code uc  where uc.group_id =? and uc.comp_id = ? and uc.table_id=? ");

				if (StringUtils.isNotBlank(copyCode)) {
					sql.append(" and uc.copy_code = ? ");
					pm.add(copyCode);
				} else {
					sql.append(" and uc.copy_code is null ");
				}
				sql.append(" ) t on item.").append(bo.getCode_field()).append(
						" = t.code order by item.").append(bo.getCode_field()).append("  ) tb where tb.is_stop=0");

				CachedRowSet rs = dao.queryToCachedRowSetBySQL(sql.toString(),
						pm);
				res.addAttr("codeDataType", bo.getCode_data_type());
				res.addTable("list", rs);
			}
		} catch (Exception e) {
			throw new BaseCheckedException("01018005", e);
		}
	}

	public BaseResponseEvent yhzSjsq(BaseRequestEvent reqEvent)
			throws BaseCheckedException {

		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			String groupId = (String) req.getAttr("groupId");
			String compId = (String) req.getAttr("compId");
			String copyCode = (String) req.getAttr("copyCode");
			String codeDataType = (String) req.getAttr("codeDataType");
			String sourceId = (String) req.getAttr("sourceId");
			List list = req.getTable("list");
			if (list != null && list.size() > 0) {
				int len = list.size();
				List<TSysGroupCodePO> updList = new ArrayList<TSysGroupCodePO>();
				List<TSysGroupCodePO> addList = new ArrayList<TSysGroupCodePO>();
				List<TSysGroupCodePO> delList = new ArrayList<TSysGroupCodePO>();
				for (int i = 0; i < len; i++) {
					TSysGroupCodePO bo = (TSysGroupCodePO) list.get(i);
					bo.setTable_id(sourceId);
					bo.setComp_id(new Integer(compId));
					bo.setGroup_id(new Integer(groupId));
					if (StringUtils.isNotBlank(bo.getCode())) {
						if(bo.getIs_read()==0 && bo.getIs_write()==0){
							delList.add(bo);
						}else{
							updList.add(bo);
						}
						
					} else {
						bo.setCode(bo.getColumn_code());
						bo.setCopy_code(copyCode);
						bo.setGroup_id(new Integer(groupId));
						
						if(bo.getIs_read()!=0 || bo.getIs_write()!=0){
							addList.add(bo);
						}
						
					}

				}
				dao.insertBatchRow(addList);
				dao.updateBatchRow(updList);
				dao.deleteBatchRow(delList);
				initDSCheckList(req, res);
			}
		} catch (Exception e) {
			throw new BaseCheckedException("01018006", e);
		}
		return res;
	}
	
	public BaseResponseEvent yhzSjsqAll(BaseRequestEvent reqEvent)
	throws BaseCheckedException {
		
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			String groupId = (String) req.getAttr("groupId");
			String compId = (String) req.getAttr("compId");
			String copyCode = (String) req.getAttr("copyCode");
			String codeDataType = (String) req.getAttr("codeDataType");
			String sourceId = (String) req.getAttr("sourceId");
			String rw = (String)req.getAttr("rw");
			
			List list = req.getTable("list");
			if (list != null && list.size() > 0) {
				int len = list.size();
				List<TSysGroupCodePO> updList = new ArrayList<TSysGroupCodePO>();
				List<TSysGroupCodePO> addList = new ArrayList<TSysGroupCodePO>();
				for (int i = 0; i < len; i++) {
					TSysGroupCodePO bo = (TSysGroupCodePO) list.get(i);
					bo.setTable_id(sourceId);
					bo.setComp_id(new Integer(compId));
					bo.setGroup_id(new Integer(groupId));
					if(rw.equals("write")){
						bo.setIs_write(1);
					}else{
						bo.setIs_read(1);
					}
					
					if (StringUtils.isNotBlank(bo.getCode())) {
						updList.add(bo);
						
					} else {
						bo.setCode(bo.getColumn_code());
						bo.setCopy_code(copyCode);
						bo.setGroup_id(new Integer(groupId));
						
						addList.add(bo);
					}
		
				}
				dao.insertBatchRow(addList);
				dao.updateBatchRow(updList);
				initDSCheckList(req, res);
			}
		} catch (Exception e) {
			throw new BaseCheckedException("", e);
		}
		return res;
	}

}
