package com.rab.sys.comm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sun.jdbc.rowset.CachedRowSet;

import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.dao.PersistenceDAO;

/**
 * 
 * <P>Title: SysInfoService</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-11-9</P>
 *
 */
public class SysInfoService {
	/**
	 * ��־��¼��
	 */
	protected static final LogWritter logger = LogFactory.getLogger(SysInfoService.class);
	
	/**
	 * <p>�����û�ID����ȡ�û����ڲ��ŵ�ID</p>
	 *
	 * @param userid    �û�ID
	 * @param dao       �־ò���ʽӿ�
	 * @return
	 * @throws BaseCheckedException
	 */
	public static int getDeptIdByUserId(int userid, PersistenceDAO dao) throws BaseCheckedException {
		int deptid = -1;
		
		String sql = "select a.dept_id from t_sys_dept a, t_sys_emp b, t_sys_user c where a.dept_id=b.dept_id and b.emp_id=c.emp_id and c.user_id=? ";
		List<Object> params = new ArrayList<Object>();
		params.add(new Integer(userid));
		CachedRowSet rowset = dao.queryToCachedRowSetBySQL(sql, params);
		
		if(rowset.size() == 0){
			logger.error("00000510=�����û�ID(" + userid + ")��ȡ����IDʱ�����쳣");
			List<String> p = new ArrayList<String>();
			p.add("" + userid);
			throw new BaseCheckedException("00000510", p);
		}
		
		try {
			if(rowset.next()){
				deptid = rowset.getInt("dept_id");
			}
		} catch (SQLException e) {
			logger.error("00000510=�����û�ID(" + userid + ")��ȡ����IDʱ�����쳣");
			List<String> p = new ArrayList<String>();
			p.add("" + userid);
			throw new BaseCheckedException("00000510", p, e);
		}
		
		return deptid;
	}
}
