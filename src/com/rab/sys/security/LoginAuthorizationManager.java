package com.rab.sys.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sun.jdbc.rowset.CachedRowSet;

import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.security.BaseAuthorizationManager;
import com.rab.framework.comm.security.FuncRightResource;
import com.rab.framework.comm.security.User;
import com.rab.framework.dao.PersistenceDAO;

public class LoginAuthorizationManager extends BaseAuthorizationManager {
	
	public static final String ADMIN_COMPANY_CODE = "000000";
	public static final String ADMIN_COPY_CODE = "000";
	
	private static final LogWritter logger = LogFactory.getLogger(LoginAuthorizationManager.class);
	/**
	 * ���ݿ�����
	 */
	private PersistenceDAO dao;
	
	public User login(String userid, String password) throws BaseCheckedException {
		String sql = "select user_id,user_code,user_name,password,user_desc,is_dba,sj_id,emp_id";
		sql += " from t_sys_user" ;
		sql += " where is_stop=0 and scbj=0 and user_code=? and password=?";
		logger.debug("��¼�� sql = " + sql);
		
		UserImpl user = new UserImpl();
		try {
			List<Object> params = new ArrayList<Object>();
			params.add(userid);
			params.add(password);
			CachedRowSet rs = dao.queryToCachedRowSetBySQL(sql, params);
//			int empId = 0;
			
			if(rs.next()){ 
				user.setUserid(rs.getInt("user_id"));
				user.setUsercode(rs.getString("user_code"));
				user.setUsername(rs.getString("user_name"));
				user.setUserdesc(rs.getString("user_desc"));
				user.setAdmin(rs.getBoolean("is_dba"));
//				user.setEmpcode(rs.getString("emp_code"));
//				empId = rs.getInt("emp_id");
				if(user.isAdmin() && "0".equals(rs.getString("sj_id"))){
					user.setSuperadmin(true);
				}
			}
			else{
				logger.error("00000504: �û������֤ʧ�ܣ�");
				List<String> tmp = new ArrayList<String>();
				tmp.add(userid);
				tmp.add(password);
				throw new BaseCheckedException("00000504",tmp);
			}

			
			
//			//ȡ��ǰ�û����û�����Ϣ
//			sql = "select group_id from t_sys_user_group where user_id=?";
//			params = new ArrayList<Object>();
//			params.add("" + user.getUserid());
//			rs = dao.queryToCachedRowSetBySQL(sql, params);
//			List<Integer> list = new ArrayList<Integer>();
//			while(rs.next()){
//				int groupId = rs.getInt("group_id");
//				list.add(new Integer(groupId));
//			}
//			int groupId[] = new int[list.size()];
//			for(int i=0; i<list.size(); i++){
//				groupId[i] = list.get(i).intValue();
//			}
//			
//			user.setGroupid(groupId);
			
		}
		catch(BaseCheckedException ex){
			throw ex;
		}
		catch (Exception e) {
			logger.error("00000505: ���û���¼��֤ʱ�����쳣��");
			List<String> tmp = new ArrayList<String>();
			tmp.add(userid);
			tmp.add(password);
			throw new BaseCheckedException("00000505",tmp, e);
		}
		
		return user;
	}


	public Map<String,Map<String,Map<String, List<FuncRightResource>>>> getFuncRight(User user) throws BaseCheckedException {
		//��λ-����-ģ��-������Դ�б�
		Map<String,Map<String,Map<String, List<FuncRightResource>>>> retMap = new HashMap<String,Map<String,Map<String, List<FuncRightResource>>>>();

		try {
			if(((UserImpl)user).isSuperadmin()){
				//��������Ա��������Ȩ��ֱ�ӷ���ϵͳ����Ԫ�Ĺ���Ȩ��
				//�������ݽṹ����λ��000000; ���ף�000; ģ�飺01
				retMap = getFuncRightBySuperAdminUser(user);
			}
			else if(((UserImpl)user).isAdmin()){
				//һ�����Ա��������Ȩ��ϵͳ����Ԫ����Ȩ��
				//�������ݽṹ����λ��000000; ���ף�000; ģ�飺01
				retMap = getFuncRightByAdminUser(user);
			}
			else{ 
				//��ͨ�û���������Ȩ��ҵ��ģ�鹦��Ȩ��
				retMap = getFuncRightByGeneralUser(user);
			}
		} 
		catch(BaseCheckedException ex){
			throw ex;
		}
		catch (Exception e) {
			logger.error("00000506: ȡ��¼�û� " + user.getUsercode() + " �Ĺ���Ȩ��ʱ�����쳣��");
			List<String> tmp = new ArrayList<String>();
			tmp.add("" + user.getUsercode());
			throw new BaseCheckedException("00000506",tmp, e);
		}
		finally{
		}
		
		return retMap;
		
	}
	
	private Map<String,Map<String,Map<String, List<FuncRightResource>>>> getFuncRightBySuperAdminUser(User user) throws Exception {
		//��λ-����-ģ��-������Դ�б�
		Map<String,Map<String,Map<String, List<FuncRightResource>>>> retMap = new HashMap<String,Map<String,Map<String, List<FuncRightResource>>>>();
		Map<String,Map<String, List<FuncRightResource>>> map = new HashMap<String,Map<String, List<FuncRightResource>>>();

		String sql = "select '" + ADMIN_COPY_CODE + "' as copy_code, mod_code, func_id, perm_name,perm_id, func_type, func_uri, parent_id, sortid"; 
		sql += " from t_sys_perm ";
		sql += " where mod_code='01'  and scbj=0 order by func_id";	
		
		logger.debug("ȡ����Ȩ��(��������Ա)�� sql = " + sql);
		CachedRowSet rs = dao.queryToCachedRowSetBySQL(sql, null);
		this.createFuncResources(rs, map);
		
		//������Ҷ�ӽڵ���м�ڵ�
		Map<String, List<FuncRightResource>> mapCopy = map.get(ADMIN_COPY_CODE);
		List<FuncRightResource> list = mapCopy.get("01");
		list = nodeFilter(list);
		mapCopy.put("01", list);
		map.put(ADMIN_COPY_CODE, mapCopy);
		
		
		retMap.put(ADMIN_COMPANY_CODE, map);
		
		return retMap;
	}

	private Map<String,Map<String,Map<String, List<FuncRightResource>>>> getFuncRightByAdminUser(User user) throws Exception {
		//��λ-����-ģ��-������Դ�б�
		Map<String,Map<String,Map<String, List<FuncRightResource>>>> retMap = new HashMap<String,Map<String,Map<String, List<FuncRightResource>>>>();
		Map<String,Map<String, List<FuncRightResource>>> map = new HashMap<String,Map<String, List<FuncRightResource>>>();
		List<Object> params = new ArrayList<Object>();
		params.add(new Integer(user.getUserid()));

		String sql = "select '" + ADMIN_COPY_CODE + "' as copy_code, a.mod_code, a.func_id, b.perm_name,b.perm_id, b.func_type, b.func_uri, b.parent_id, b.sortid "; 
		sql += " from t_sys_user_perm a, t_sys_perm b ";
		sql += " where a.func_id = b.func_id and a.user_id=? and a.mod_code='01' and b.scbj=0 order by a.func_id";
		
		logger.debug("ȡ����Ȩ��(��ͨ����Ա�����û���Ȩ)�� sql = " + sql);
		CachedRowSet rs = dao.queryToCachedRowSetBySQL(sql, params);
		this.createFuncResources(rs, map);
		
		String sql2 = "select '" + ADMIN_COPY_CODE + "' as copy_code, a.mod_code, a.func_id, b.perm_name,b.perm_id, b.func_type, b.func_uri, b.parent_id, b.sortid"; 
		sql2 += " from t_sys_group_perm a, t_sys_perm b, t_sys_group c, t_sys_user_group d";
		sql2 += " where a.func_id   =b.func_id  and a.group_id  =c.group_id  and d.group_id =c.group_id and a.mod_code='01' and d.user_id=?  and b.scbj=0 order by a.func_id";
		logger.debug("ȡ����Ȩ��(��ͨ����Ա�����û�����Ȩ)�� sql2 = " + sql2);
		CachedRowSet rs2 = dao.queryToCachedRowSetBySQL(sql2, params);
		this.createFuncResources(rs2, map);

		//������Ҷ�ӽڵ���м�ڵ�
		Map<String, List<FuncRightResource>> mapCopy = map.get(ADMIN_COPY_CODE);
		if(mapCopy != null && mapCopy.containsKey("01")){
			List<FuncRightResource> list = mapCopy.get("01");
			list = nodeFilter(list);
			mapCopy.put("01", list);
		}
		
		map.put(ADMIN_COPY_CODE, mapCopy);

		retMap.put(ADMIN_COMPANY_CODE, map);
		
		return retMap;
	}

	
	private Map<String,Map<String,Map<String, List<FuncRightResource>>>> getFuncRightByGeneralUser(User user) throws Exception {
		//��λ-����-ģ��-������Դ�б�
		Map<String,Map<String,Map<String, List<FuncRightResource>>>> retMap = new HashMap<String,Map<String,Map<String, List<FuncRightResource>>>>();
		Map<String,Map<String, List<FuncRightResource>>> map = new HashMap<String,Map<String, List<FuncRightResource>>>();
		List<Object> params = new ArrayList<Object>();
		params.add(new Integer(user.getUserid()));

		String sql = "select a.copy_code, a.mod_code, a.func_id, b.perm_name,b.perm_id, b.func_type, b.func_uri, b.parent_id, b.sortid "; 
		sql += " from t_sys_user_perm a, t_sys_perm b ";
		sql += " where a.func_id = b.func_id and a.user_id=? and b.scbj=0 order by a.func_id";
		
		logger.debug("ȡ����Ȩ��(���û���Ȩ)�� sql = " + sql);
		CachedRowSet rs = dao.queryToCachedRowSetBySQL(sql, params);
		this.createFuncResources(rs, map);
		
		String sql2 = "select a.copy_code, a.mod_code, a.func_id, b.perm_name,b.perm_id, b.func_type, b.func_uri, b.parent_id, b.sortid"; 
		sql2 += " from t_sys_group_perm a, t_sys_perm b, t_sys_group c, t_sys_user_group d";
		sql2 += " where a.func_id   =b.func_id  and a.group_id  =c.group_id  and d.group_id =c.group_id  and d.user_id=?  and b.scbj=0 order by a.func_id";
		logger.debug("ȡ����Ȩ��(���û�����Ȩ)�� sql2 = " + sql2);
		CachedRowSet rs2 = dao.queryToCachedRowSetBySQL(sql2, params);
		this.createFuncResources(rs2, map);

		//ö������
		Iterator<Map<String, List<FuncRightResource>>> iterCopy = map.values().iterator();
		while(iterCopy.hasNext()){
			//��������ö��ģ��
			Map<String, List<FuncRightResource>> mapCopy = iterCopy.next();
			Iterator<String> iterModCode =  mapCopy.keySet().iterator();
			while(iterModCode.hasNext()){
				String modCode = iterModCode.next();
				List<FuncRightResource> list = mapCopy.get(modCode);
				list = nodeFilter(list);
				mapCopy.put(modCode, list);
			}
		}
		
		//ȡ�������׵Ĺ�����λ

		if(!map.isEmpty()){
			Iterator<String> iterCopyCode = map.keySet().iterator();
			String values = "";
			while(iterCopyCode.hasNext()){
				values += "'" + iterCopyCode.next() + "',";
			}
			values = values.substring(0,values.length()-1);
			
			String sql3 = "select  b.comp_code,a.copy_code from t_sys_copy a, t_sys_company b where a.comp_id=b.comp_id and copy_code in (" + values + ")";
			logger.debug("ȡ��Ȩ������صĵ�λ��Ϣ�� sql3 = " + sql3);
			CachedRowSet rs3 = dao.queryToCachedRowSetBySQL(sql3, null);
			while(rs3.next()){
				String copyCode = rs3.getString("copy_code");
				String comp_code = rs3.getString("comp_code");
				
				//��λ
				Map<String,Map<String, List<FuncRightResource>>> mapComp = retMap.get(comp_code);
				if(mapComp == null){
					mapComp = new HashMap<String,Map<String, List<FuncRightResource>>>();
					retMap.put(comp_code, mapComp);
				}
				
				mapComp.put(copyCode, map.get(copyCode));
			}
		}
		
		return retMap;
	}
	
	private void createFuncResources(CachedRowSet rs, Map<String,Map<String, List<FuncRightResource>>> map) throws Exception {
		//select a.copy_code, a.mod_code, a.func_id, b.perm_name,b.perm_id, b.func_type, b.func_uri, b.parent_id, b.sortid 
		while(rs.next()){
			String copyCode		= rs.getString("copy_code");
			String modCode 		= rs.getString("mod_code");
			
			Map<String, List<FuncRightResource>> mapCopy = map.get(copyCode); //��������
			if(mapCopy == null){
				mapCopy = new HashMap<String, List<FuncRightResource>>();
				map.put(copyCode, mapCopy);
			}
						
			List<FuncRightResource> listMod = mapCopy.get(modCode); //�������£�����ģ��
			if(listMod == null){
				listMod = new ArrayList<FuncRightResource>();
				mapCopy.put(modCode, listMod);
			}
						
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
			
			boolean flag = true;
			for(FuncRightResource tmp : listMod){
				if(tmp.getFuncId().equals(funcId)){
					flag = false;
					break;
				}
			}
			
			if(flag){
				listMod.add(frr);
			}
		}
		
	}
	
	/**
	 * 
	 * <p>���˵���Щû���ӽڵ���м�ڵ�</p>
	 *
	 * @param srcList ԭʼ�ڵ㼯��
	 * 
	 * @return  ���˺�Ľڵ㼯��
	 */
	private List<FuncRightResource> nodeFilter(List<FuncRightResource> srcList){
		//���ؽڵ㼯��
		List<FuncRightResource> retList = new ArrayList<FuncRightResource>();
		
		//Ҷ�ӽڵ㼯��
		List<FuncRightResource> leaves = new  ArrayList<FuncRightResource>();
		for(int i=srcList.size()-1; i>=0; i--){
			FuncRightResource node = srcList.get(i);
			if(node.getFuncType() > 0){ //�ж���Ҷ�ӽڵ㣬�����������ͣ���ť��������ڣ����ͷֱ���1��2
				leaves.add(node);
				retList.add(node);
				srcList.remove(node); //��Դ�ڵ��б�������ѳ�ȡ�Ľڵ�
			}
			
		}
		
		for(FuncRightResource node : leaves){
			String parentid = node.getParentId();
			FuncRightResource parentNode = getResourceById(srcList, parentid);
			if(parentNode == null){
				continue;
			}
			retList.add(parentNode);
			
			while(!parentNode.getFuncId().equals("0")){
				parentid = parentNode.getParentId(); //�߼����󣿣�lfw 20101010
				parentNode = getResourceById(srcList, parentid);
				if(parentNode == null){
					break;
				}
				retList.add(parentNode);
			}
		}
		
		return retList;
	}
	
	/**
	 * <p>���ݱ�ţ��Ӹ����Ľڵ㼯���в��ҽڵ�</p>
	 *
	 * @param srcList ԭʼ�ڵ㼯��
	 * @param id      �����Ľڵ���
	 * 
	 * @return        
	 */
	private FuncRightResource getResourceById(List<FuncRightResource> srcList,String id){
		FuncRightResource resource = null;
		
		for(int i=srcList.size()-1; i>=0; i--){
			FuncRightResource node = srcList.get(i);
			if(node.getFuncId().equals(id)){
				resource = node;
				srcList.remove(node);
				break;
			}
		}
		
		return resource;
	}
	
	
	public void setDao(PersistenceDAO dao) {
		this.dao = dao;
	}
}
