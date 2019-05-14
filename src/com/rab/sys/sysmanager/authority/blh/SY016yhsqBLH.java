package com.rab.sys.sysmanager.authority.blh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import sun.jdbc.rowset.CachedRowSet;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.dto.event.DataRequestEvent;
import com.rab.framework.comm.dto.event.DataResponseEvent;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.pagination.PaginationMetaData;
import com.rab.framework.comm.security.User;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.dao.PersistenceUtils;
import com.rab.framework.domain.blh.BaseDomainBLH;
import com.rab.sys.sysmanager.authority.model.TSysTablePO;
import com.rab.sys.sysmanager.authority.model.TSysUserCodePO;
import com.rab.sys.sysmanager.authority.model.TSysUserPermPO;

/**
 * @Description：用户功能授权
 * @Author：ZhangBin
 * @Date：2010-10-18
 */
public class SY016yhsqBLH extends BaseDomainBLH {

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
			String isAdmin = StringUtils.trimToEmpty((String)req.getAttr("admin"));
			String userId = StringUtils.trimToEmpty((String)req.getAttr("userId"));
			List<Object> params = new ArrayList<Object>();
			
			if(isAdmin.equals("true")){
				sqlKey = "SY016yhsqBLH_initCompList_byAdmin";
				params.add(new Integer(userId));
				params.add(new Integer(userId));
			}else{
				
				params.add(userId);
			}
			
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			CachedRowSet crs = dao.queryToCachedRowSetByKey(sqlKey, params);
			res.addCombo("comp", crs);
		} catch (Exception e) {
			throw new BaseCheckedException("01016001", e);
		}
		return res;
	}

	/**
	 * 获取帐套列表
	 * 
	 * @param reqEvent
	 * @return
	 * @throws BaseCheckedException
	 */
	public BaseResponseEvent initCopyListByComp(BaseRequestEvent reqEvent)
			throws BaseCheckedException {

		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
			String sqlKey = "SY016yhsqBLH_initCopyListByComp";
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			List<Object> params = new ArrayList<Object>();
			String compId = (String) req.getAttr("compId");
			if(StringUtils.isNotBlank(compId)){
				params.add(new Integer(compId));
				CachedRowSet crs = dao.queryToCachedRowSetByKey(sqlKey, params);
				res.addCombo("copy", crs);
			}
			
		} catch (Exception e) {
			throw new BaseCheckedException("01016002", e);
		}
		return res;
	}

	/**
	 * 获取模块列表
	 * 
	 * @param reqEvent
	 * @return
	 * @throws BaseCheckedException
	 */
	public BaseResponseEvent initModList(BaseRequestEvent reqEvent)
			throws BaseCheckedException {

		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
			String sqlKey = "";
			String copyCode = (String) req.getAttr("copyCode");
			String admin = StringUtils.trimToEmpty((String)req.getAttr("admin"));
			String userId = StringUtils.trimToEmpty((String)req.getAttr("userId"));
			
			List<Object> params = new ArrayList<Object>();
			
			User user = this.domainSession.getUser();
			boolean flag = true;
			if(user.isSuperadmin()){
				
				if(admin.equals("true")){
					sqlKey = "SY016yhsqBLH_initModList_byCopyAndUser";
					if(StringUtils.isNotBlank(copyCode)){
						params.add(new Integer(userId));
						params.add(copyCode);
					}else{
						sqlKey = "SY016yhsqBLH_initModList_byAdmin";
						params.add(new Integer(userId));
					}
				}else{
					if(StringUtils.isNotBlank(copyCode)){
						sqlKey = "SY016yhsqBLH_initModList_byCopyWithSuperAdmin";
						params.add(copyCode);
					}else{
						sqlKey = "SY016yhsqBLH_initModList_bySuperAdmin";
						
					}
					
				}
			}else if(user.isAdmin()){
				if(admin.equals("true")){
					sqlKey ="SY016yhsqBLH_initModList_byCopyWithAdmin";
					if(StringUtils.isNotBlank(copyCode)){
						
						params.add(copyCode);
						params.add(new Integer(userId));
						params.add(user.getUserid());
					}else{
						sqlKey = "SY016yhsqBLH_initModList_byAdminWithAdmin";
						params.add(new Integer(userId));
						params.add(user.getUserid());
					}
				}else{
					if(StringUtils.isNotBlank(copyCode)){
						sqlKey = "SY016yhsqBLH_initModList_byCopyAndUser";
						params.add(user.getUserid());
						params.add(copyCode);
						
					}else{
						sqlKey = "SY016yhsqBLH_initModList_byAdmin";
						params.add(new Integer(user.getUserid()));
					}
				}
				
			}else{
				flag = false;
			}
			if(flag){
				PersistenceDAO dao = this.domainSession.getPersistenceDAO();
				CachedRowSet crs = dao.queryToCachedRowSetByKey(sqlKey, params);

				res.addCombo("mod", crs);
			}

		} catch (Exception e) {
			throw new BaseCheckedException("01016003", e);
		}
		return res;
	}

	public BaseResponseEvent initModListByUserId(BaseRequestEvent reqEvent)
			throws BaseCheckedException {

		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
			String sqlKey = "SY016yhsqBLH_initModListByUserId";
			String userId = (String) req.getAttr("userId");
			User user = this.domainSession.getUser();
			List<Object> params = new ArrayList<Object>();
			if (user.isSuperadmin()) {
				sqlKey = "SY016yhsqBLH_initModListBySysUser";
			} else {

				params.add(new Integer(userId));
			}

			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			CachedRowSet crs = dao.queryToCachedRowSetByKey(sqlKey, params);

			res.addCombo("mod", crs);

		} catch (Exception e) {
			throw new BaseCheckedException("01016003", e);
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

			String userId = (String) req.getAttr("userId");
			String copyCode = (String) req.getAttr("copyCode");
			String modCode = (String) req.getAttr("modCode");

			String sqlKey1 = "SY016yhsqBLH_initCheckTree_gnzyList";
			String sqlKey2 = "SY016yhsqBLH_initCheckTree_gnzyListChecked";

			if (user.isSuperadmin()) {
				sqlKey1 = "SY016yhsqBLH_initCheckTree_gnzyAll";
				params1.add(modCode);

			}else if(modCode.equals("01")){
				sqlKey1 = "SY016yhsqBLH_initCheckTree_gnzyListByAdmin";
				params1.add(user.getUserid());
				params1.add(user.getUserid()); 
			}else{
				params1.add(user.getUserid());
				params1.add(modCode);
			}
 
			if (StringUtils.isBlank(copyCode)) {
				sqlKey2 = "SY016yhsqBLH_initCheckTree_gnzyListWithOutCopy";
				params2.add(userId);
				params2.add(modCode);
				params2.add(userId);
				params2.add(modCode);
			} else {

				params2.add(userId);
				params2.add(copyCode);
				params2.add(modCode);
				params2.add(userId);
				params2.add(copyCode);
				params2.add(modCode);
			}
			CachedRowSet crs1 = this.domainSession.getPersistenceDAO()
					.queryToCachedRowSetByKey(sqlKey1, params1);

			CachedRowSet crs2 = this.domainSession.getPersistenceDAO()
					.queryToCachedRowSetByKey(sqlKey2, params2);

			res.addCheckTree("tree", crs1, crs2,null);

		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new BaseCheckedException("01016004", e);
		}
		return res;
	} 

	/**
	 * 获取帐套列表
	 * 
	 * @param reqEvent
	 * @return
	 * @throws BaseCheckedException
	 */
	public BaseResponseEvent initCopyListByUserId(BaseRequestEvent reqEvent)
			throws BaseCheckedException {

		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
			String sqlKey = "SY016yhsqBLH_initCopyListByUserId";
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			String compId = (String)req.getAttr("compId");
			List<Object> params = new ArrayList<Object>();
			if(StringUtils.isNotBlank(compId)){
				sqlKey = "SY016yhsqBLH_initCopyListByComp";
				params.add(new Integer(compId));
			}else{
				String userId = (String) req.getAttr("userId");
				params.add(new Integer(userId));
			}
			CachedRowSet crs = dao.queryToCachedRowSetByKey(sqlKey, params);
			res.addCombo("copy", crs);
		} catch (Exception e) {
			throw new BaseCheckedException("01016002", e);
		}
		return res;
	}

	/**
	 * 获取数据资源列表
	 * 
	 * @param reqEvent
	 * @return
	 * @throws BaseCheckedException
	 */
	public BaseResponseEvent initDataSourceList(BaseRequestEvent reqEvent)
			throws BaseCheckedException {

		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
			
			String sqlKey = "SY016yhsqBLH_initDataSourceList";
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			CachedRowSet crs = dao.queryToCachedRowSetByKey(sqlKey, null);
			res.addCombo("datasource", crs);
		} catch (Exception e) {
			throw new BaseCheckedException("01016005", e);
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
			throw new BaseCheckedException("01016004", e);
		}
		return res;
	}

	/**
	 * 用户功能授权
	 * 
	 * @param reqEvent
	 * @return
	 * @throws BaseCheckedException
	 */
	public BaseResponseEvent yhGnsq(BaseRequestEvent reqEvent)
			throws BaseCheckedException {

		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			String userId = StringUtils.trimToEmpty((String) req
					.getAttr("userId"));
			String modCode = StringUtils.trimToEmpty((String) req
					.getAttr("modCode"));
			String copyCode = (String) req
					.getAttr("copyCode");

			String adds = StringUtils.trimToEmpty((String) req.getAttr("adds"));
			User user = this.domainSession.getUser();
			
			if((user.getUserid()+"").equals(userId)){
				res.addAttr("optState", "0");
				
			}else{
			
				if (StringUtils.isNotBlank(adds)) {
					String[] insertList = adds.split("_");
					List<TSysUserPermPO> addList = new ArrayList<TSysUserPermPO>();
					if (insertList != null && insertList.length > 0) {
						for (int i = 0; i < insertList.length; i++) {
							TSysUserPermPO bo = new TSysUserPermPO();
							bo.setMod_code(modCode);
							bo.setUser_id(new Integer(userId));
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
					List<TSysUserPermPO> delList = new ArrayList<TSysUserPermPO>();
					if (deleteList != null && deleteList.length > 0) {
						for (int i = 0; i < deleteList.length; i++) {
							TSysUserPermPO bo = new TSysUserPermPO();
							bo.setMod_code(modCode);
							bo.setUser_id(new Integer(userId));
							bo.setCopy_code(copyCode);
							bo.setFunc_id(new Integer(deleteList[i]));
							delList.add(bo);
						}
						dao.deleteBatchRow(delList);
					}
				}
			}
		} catch (Exception e) {
			throw new BaseCheckedException("01016006", e);
		}
			
		return res;
	}

	public BaseResponseEvent yhSjsq(BaseRequestEvent reqEvent)
			throws BaseCheckedException {

		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			String userId = (String) req.getAttr("userId");
			String compId = (String) req.getAttr("compId");
			String copyCode = (String) req.getAttr("copyCode");
			String codeDataType = (String) req.getAttr("codeDataType");
			String sourceId = (String) req.getAttr("sourceId");
			List list = req.getTable("list");
			if (list != null && list.size() > 0) {
				int len = list.size();
				List<TSysUserCodePO> updList = new ArrayList<TSysUserCodePO>();
				List<TSysUserCodePO> addList = new ArrayList<TSysUserCodePO>();
				List<TSysUserCodePO> delList = new ArrayList<TSysUserCodePO>();
				for (int i = 0; i < len; i++) {
					TSysUserCodePO bo = (TSysUserCodePO) list.get(i);
					bo.setTable_id(sourceId);
					
					bo.setComp_id(new Integer(compId));
					bo.setUser_id(new Integer(userId));
					if (StringUtils.isNotBlank(bo.getCode())) {
						
						if(bo.getIs_read()==0 && bo.getIs_write()==0){
							delList.add(bo);
						}else{
							updList.add(bo);
						}
					} else {
						bo.setCode(bo.getColumn_code());
						bo.setCopy_code(copyCode);
						
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
			throw new BaseCheckedException("01016007", e);
		}
		return res;
	}
	
	public BaseResponseEvent yhSjsqAll(BaseRequestEvent reqEvent)
	throws BaseCheckedException {

		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			String userId = (String) req.getAttr("userId");
			String compId = (String) req.getAttr("compId");
			String copyCode = (String) req.getAttr("copyCode");
			String codeDataType = (String) req.getAttr("codeDataType");
			String sourceId = (String) req.getAttr("sourceId");
			String rw = (String)req.getAttr("rw");
			
			List list = req.getTable("list");
			if (list != null && list.size() > 0) {
				int len = list.size();
				List<TSysUserCodePO> updList = new ArrayList<TSysUserCodePO>();
				List<TSysUserCodePO> addList = new ArrayList<TSysUserCodePO>();
				for (int i = 0; i < len; i++) {
					TSysUserCodePO bo = (TSysUserCodePO) list.get(i);
					bo.setTable_id(sourceId);
					
					bo.setComp_id(new Integer(compId));
					bo.setUser_id(new Integer(userId));
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

	public BaseResponseEvent getGlyListByGroupId(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		try {
			DataRequestEvent req = (DataRequestEvent) reqEvent;
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();

			List<Object> params = new ArrayList<Object>();
			User user = domainSession.getUser();
			String id = StringUtils.trimToEmpty((String) req.getAttr("coCode"));
			params.add(id);
			params.add(new Integer(user.getUserid()));
			String sqlKey = "SY016yhsqBLH_getGlyListByGroupId";
			DataResponseEvent res = new DataResponseEvent();
			PaginationMetaData metaData = req.getTablePageInfo("list");
			CachedRowSet rs = dao.queryPageToCachedRowSetByKey(sqlKey, params,
					metaData);

			Map<String, String> cacheMap = new HashMap<String, String>();
			cacheMap.put("emp_id", "sys_subj_type");
			res.addCacheInfo("list", cacheMap);
			res.addTable("list", rs,metaData);
			return res;
		} catch (Exception e) {
			throw new BaseCheckedException("01016007", e);
		}
	}

	public BaseResponseEvent initGlyList(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		try {
			DataRequestEvent req = (DataRequestEvent) reqEvent;
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();

			List<Object> params = new ArrayList<Object>();
			User user = domainSession.getUser();
			
			params.add(new Integer(user.getUserid()));
			String sqlKey = "SY016yhsqBLH_initGlyList";
			DataResponseEvent res = new DataResponseEvent();
			PaginationMetaData metaData = req.getTablePageInfo("list");
			CachedRowSet rs = dao.queryPageToCachedRowSetByKey(sqlKey, params,
					metaData);

			Map<String, String> cacheMap = new HashMap<String, String>();
			cacheMap.put("emp_id", "sys_subj_type");
			res.addCacheInfo("list", cacheMap);
			res.addTable("list", rs,metaData);
			return res;
		} catch (Exception e) {
			throw new BaseCheckedException("", e);
		}
	}

	private void initDSCheckList(DataRequestEvent req, DataResponseEvent res)
			throws BaseCheckedException {
		String sqlKey = "SY016yhsqBLH_getDataSourceById";
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		String sourceId = (String) req.getAttr("sourceId");
		String copyCode = (String) req.getAttr("copyCode");
		String compId = (String) req.getAttr("compId");
		String userId = (String) req.getAttr("userId");

		List<Object> params = new ArrayList<Object>(1);
		params.add(sourceId);
		try {
			CachedRowSet crs = dao.queryToCachedRowSetByKey(sqlKey, params);
			List boList = PersistenceUtils
					.rowset2VOList(TSysTablePO.class, crs);
			if (boList != null && boList.size() > 0) {
				TSysTablePO bo = (TSysTablePO) boList.get(0);
				List<Object> pm = new ArrayList<Object>();

				pm.add(new Integer(userId));
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
								" column_value,t.flag,t.table_id,t.comp_id,t.copy_code,t.code,t.user_id,t.acct_year, ")
						.append(
								" case t.is_read when 1 then 1 else 0 end is_read, case t.is_write when 1 then 1 else 0 end is_write from ")
						.append(bo.getTable_id())
						.append(" item ")
						.append(
								" left join (select uc.*,'true' flag from t_sys_user_code uc  where uc.user_id =? and uc.comp_id = ? and uc.table_id=? ");

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
			throw new BaseCheckedException("01016010", e);
		}
	}

}
