package com.rab.sys.sysmanager.authority.blh;

import java.util.ArrayList;
import java.util.List;

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
import com.rab.framework.domain.blh.BaseDomainBLH;
import com.rab.sys.sysmanager.authority.model.TSysModPermPO;

/**
 * @Description：管理员模块授权
 * @Author：ZhangBin
 * @Date：2010-10-18
 */
public class SY014glyMkSqBLH extends BaseDomainBLH {
	
	public BaseResponseEvent init(BaseRequestEvent reqEvent) throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		try {
			
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			User user = this.domainSession.getUser();
			
			List<Object> params = new ArrayList<Object>();
			String sqlKey = "SY014glyMkSqBLH_init_list";
			String userId = (String)req.getAttr("userId");
			if(user.isSuperadmin()){
				sqlKey = "SY014glyMkSqBLH_init_all";
				params.add(new Integer(userId));
				params.add(new Integer(userId));
			}else{
				params.add(new Integer(userId));
				params.add(new Integer(user.getUserid()));
				params.add(new Integer(user.getUserid()));
				params.add(new Integer(userId));
			}
			CachedRowSet crs = dao.queryToCachedRowSetByKey(sqlKey, params);
			res.addTable("list", crs);
			
		}catch(Exception e){
			throw new BaseCheckedException("", e);
		}
		return res;
	}
	
	/**
	 * 模块授权
	 * @param reqEvent
	 * @return
	 * @throws BaseCheckedException
	 */
	public BaseResponseEvent mksq(BaseRequestEvent reqEvent) throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		DataResponseEvent res = new DataResponseEvent();
		
		try {
			
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			String userId = StringUtils.trimToEmpty((String)req.getAttr("userId"));
			String adds = StringUtils.trimToEmpty((String)req.getAttr("adds"));
			
			if(StringUtils.isNotBlank(adds)){
				String[] insertList = adds.split("_");
				List<TSysModPermPO> addList = new ArrayList<TSysModPermPO>();
				if(insertList!=null && insertList.length>0){
					for(int i=0;i<insertList.length;i++){
						TSysModPermPO bo = new TSysModPermPO();
						bo.setMod_code(insertList[i]);
						bo.setUser_id(new Integer(userId));
						addList.add(bo);
					}
					dao.insertBatchRow(addList);
				}
			}
			
			String dels = StringUtils.trimToEmpty((String)req.getAttr("dels"));
			if(StringUtils.isNotBlank(dels)){
				String[] deleteList = dels.split("_");
				List<TSysModPermPO> delList = new ArrayList<TSysModPermPO>();
				if(deleteList!=null && deleteList.length>0){
					for(int i=0;i<deleteList.length;i++){
						TSysModPermPO bo = new TSysModPermPO();
						bo.setMod_code(deleteList[i]);
						bo.setUser_id(new Integer(userId));
						delList.add(bo);
					}
					dao.deleteBatchRow(delList);
				}
			}
		}catch(Exception e){
			throw new BaseCheckedException("", e);
		}
		return res;
	}
}
