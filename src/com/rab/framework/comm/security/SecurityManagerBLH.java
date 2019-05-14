package com.rab.framework.comm.security;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import sun.jdbc.rowset.CachedRowSet;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.dao.PersistenceDAO;
import com.rab.framework.domain.blh.BaseDomainBLH;

/**
 * 
 * <P>Title: SecurityManagerBLH</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-10-9</P>
 *
 */
public class SecurityManagerBLH extends BaseDomainBLH {
	
	public BaseResponseEvent getFuncPrincipals(BaseRequestEvent reqEvent) throws BaseCheckedException {
		PersistenceDAO dao = this.domainSession.getPersistenceDAO();
		
		SecurityManagerResponseEvent resp = new SecurityManagerResponseEvent();
		
		String sql = "select mod_code, func_id, perm_name, perm_id, func_type, func_uri, parent_id, sortid "; 
		sql += "from  t_sys_perm "; 
		sql += "where scbj=0 order by func_id ";
		logger.debug("取系统的功能权限定义集合： sql = " + sql);
		CachedRowSet rs = dao.queryToCachedRowSetBySQL(sql, null);
		
		Map<String,FuncRightPrincipal> funcPrincipals =  new HashMap<String,FuncRightPrincipal>();
		try {
			while(rs.next()){
				String modCode 		= rs.getString("mod_code");
							
				String funcId		= rs.getString("func_id");
				String permName		= rs.getString("perm_name");
				String permId		= rs.getString("perm_id");
				int funcType		= rs.getInt("func_type");
				String funcUri		= rs.getString("func_uri");
				String parentId		= rs.getString("parent_id");
				int sortId 		    = rs.getInt("sortid");
				

				FuncRightResource frr = new FuncRightResource();
				
				frr.setModCode(modCode);
				frr.setFuncId(funcId);
				frr.setPermName(permName);
				frr.setPermId(permId);
				frr.setFuncType(funcType);
				frr.setFuncUri(funcUri);
				frr.setParentId(parentId);
				frr.setSortId(sortId);
				
				
				FuncRightPrincipal principal = new FuncRightPrincipal();
				principal.setRightResource(frr);
				
				funcPrincipals.put(principal.getPrincipalId(), principal);
				
				resp.setFuncPrincipals(funcPrincipals);
			}
		} catch (SQLException e) {
			logger.error("00000507: 初始化系统的功能权限定义集合时出现异常！");
			throw new BaseCheckedException("00000507", e);
		}
		
		return resp;
	}
	
}
