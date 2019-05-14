package com.rab.sys.sysmanager.authority.blh;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.dto.event.DataRequestEvent;
import com.rab.framework.comm.dto.event.DataResponseEvent;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.domain.blh.BaseDomainBLH;
import com.rab.sys.sysmanager.authority.model.TSysDbaCompPO;
/**
 * 
 * @Description£ºµ¥Î»ÊÚÈ¨
 * @Author£ºmanan
 * @Date£º2010-10-12
 */
public class SY013glydwsqBLH extends BaseDomainBLH {
	private static final LogWritter logger = LogFactory
			.getLogger(SY013glydwsqBLH.class);
	
	public BaseResponseEvent add(BaseRequestEvent reqEvent) 
	 throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		try {
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			String userId = StringUtils.trimToEmpty((String)req.getAttr("userId"));
			
			String adds = StringUtils.trimToEmpty((String)req.getAttr("adds"));
			
			if(StringUtils.isNotBlank(adds)){
				String[] insertList = adds.split("_");
				List<TSysDbaCompPO> addList = new ArrayList<TSysDbaCompPO>();
				if(insertList!=null && insertList.length>0){
					for(int i=0;i<insertList.length;i++){
						TSysDbaCompPO bo = new TSysDbaCompPO();
						bo.setDba_id(new Integer(userId));
						bo.setComp_id(new Integer(insertList[i]));
						addList.add(bo);
					}
					dao.insertBatchRow(addList);
				}
			}
			
			String dels = StringUtils.trimToEmpty((String)req.getAttr("dels"));
			if(StringUtils.isNotBlank(dels)){
				String[] deleteList = dels.split("_");
				List<TSysDbaCompPO> delList = new ArrayList<TSysDbaCompPO>();
				if(deleteList!=null && deleteList.length>0){
					for(int i=0;i<deleteList.length;i++){
						TSysDbaCompPO bo = new TSysDbaCompPO();
						bo.setDba_id(new Integer(userId));
						bo.setComp_id(new Integer(deleteList[i]));
						delList.add(bo);
					}
					dao.deleteBatchRow(delList);
				}
			}
			return new DataResponseEvent();
		} catch (Exception e) {
			throw new BaseCheckedException("01013001", e);
		}
		
	}
	
}
