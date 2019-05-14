package com.rab.sys.sysmanager.authority.blh;

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
import com.rab.framework.comm.pagination.PaginationMetaData;
import com.rab.framework.comm.security.User;
import com.rab.framework.comm.util.StringUtils;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.domain.blh.BaseDomainBLH;
import com.rab.sys.sysmanager.authority.model.TSysGroupPO;

/**
 * 
 * @Description：用户管理
 * @Author：manan
 * @Date：2010-10-12
 */
public class SY011yhzBLH extends BaseDomainBLH {
	private static final LogWritter logger = LogFactory
			.getLogger(SY012yhBLH.class);

	public BaseResponseEvent init(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;

		DataResponseEvent res = new DataResponseEvent();

		res.setPage("/page/sysmanager/authority/sy011_yhzxx_main.jsp");
		
		return res;
	}

	public BaseResponseEvent addInit(BaseRequestEvent reqEvent)
			throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;

		DataResponseEvent res = new DataResponseEvent();

		res.setPage("/page/sysmanager/authority/sy011_yhzxx_add.jsp");

		return res;
	}
	public BaseResponseEvent add(BaseRequestEvent reqEvent) 
	 throws BaseCheckedException {
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		try{
			TSysGroupPO bo = (TSysGroupPO) req.getForm("form");
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			List<Object> params = new ArrayList<Object>();
			params.add(bo.getGroup_code());
			params.add(bo.getGroup_name());
			CachedRowSet rs = dao.queryToCachedRowSetByKey("SY011yhzBLH_add", params);
			if(rs.next()){
			  String tempCode = rs.getString("group_code");	
			  String tempName = rs.getString("group_name");
			  if(tempCode.equals(bo.getGroup_code())){
				  throw new BaseCheckedException("01011005");
			  }else if(tempName.equals(bo.getGroup_name())){
				  throw new BaseCheckedException("01011006");
			  }
					
			}
		}catch(Exception e){
			if(e instanceof BaseCheckedException){
				throw (BaseCheckedException)e;
			}else{
				throw new BaseCheckedException("", e);
			}
		}
		try {
			TSysGroupPO bo = (TSysGroupPO) req.getForm("form");
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			long sq =  dao.getSequence("SQ_SYS_GROUP");
			Integer s = new Integer((int)sq);
			bo.setGroup_id(s);
			User user = domainSession.getUser();
			bo.setDba_id(user.getUserid());
			dao.insertSingleRow(bo);
			DataResponseEvent res = new DataResponseEvent();
            res.addAttr("group_id", String.valueOf(s));
            return res;
		} catch (Exception e) {
			throw new BaseCheckedException("01011001", e);
		}
	}

	public BaseResponseEvent delete(BaseRequestEvent reqEvent) throws Exception {
		try {
			DataRequestEvent req = (DataRequestEvent) reqEvent;
			PersistenceDAO dao = this.domainSession.getPersistenceDAO();
			List list = req.getTable("list",TSysGroupPO.class);
			dao.deleteBatchRow(list);
			DataResponseEvent res = new DataResponseEvent();
			return res;
		} catch (Exception e) {
			throw new BaseCheckedException("01011002", e);
		}
	}

	public BaseResponseEvent update(BaseRequestEvent reqEvent)
			throws BaseCheckedException {

		DataRequestEvent req = (DataRequestEvent) reqEvent;
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		try{
			TSysGroupPO bo = (TSysGroupPO) req.getForm("form");
			List<Object> params = new ArrayList<Object>();
			params.add(bo.getGroup_code());
			params.add(bo.getGroup_name());
			params.add(bo.getGroup_id());
			CachedRowSet rs = dao.queryToCachedRowSetByKey("SY011yhzBLH_update", params);
			if(rs.next()){
				  String tempCode = rs.getString("group_code");	
				  String tempName = rs.getString("group_name");
				  if(tempCode.equals(bo.getGroup_code())){
					  throw new BaseCheckedException("01011005");
				  }else if(tempName.equals(bo.getGroup_name())){
					  throw new BaseCheckedException("01011006");
				  }
			}
		}catch(Exception e){
			if(e instanceof BaseCheckedException){
				throw (BaseCheckedException)e;
			}else{
				throw new BaseCheckedException("", e);
			}
		}
		try {
			TSysGroupPO bo = (TSysGroupPO) req.getForm("form");
			dao.updateSingleRow(bo);
		} catch (Exception e) {
			throw new BaseCheckedException("01011003", e);
		}
		return new DataResponseEvent();
	}

	public BaseResponseEvent query(BaseRequestEvent reqEvent) 
	 throws BaseCheckedException {
	   try{
		DataRequestEvent req = (DataRequestEvent) reqEvent;
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		TSysGroupPO bo=null;
		bo = (TSysGroupPO) req.getForm("form");	
		List<Object> params = new ArrayList<Object>();
		params.add("%" + StringUtils.trimToEmpty(bo.getGroup_code()) + "%");
		params.add("%" + StringUtils.trimToEmpty(bo.getGroup_name()) + "%");
		User user = domainSession.getUser();
		params.add(new Integer(user.getUserid()));
		PaginationMetaData metaData = req.getTablePageInfo("list");
		DataResponseEvent res = new DataResponseEvent();
		CachedRowSet rs = dao.queryPageToCachedRowSetByKey("SY011yhzBLH_query", params,metaData);
		res.addTable("list", rs,metaData);
		return res;
	   }catch(Exception e){
		   throw new BaseCheckedException("01011004", e);
     }
	}
}
