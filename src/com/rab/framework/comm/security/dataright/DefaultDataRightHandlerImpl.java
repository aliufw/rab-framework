package com.rab.framework.comm.security.dataright;

import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.security.User;
import com.rab.framework.dao.PersistenceDAO;

/**
 * 
 * <P>Title: DefaultDataRightHandlerImpl</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-10-11</P>
 *
 */
public class DefaultDataRightHandlerImpl implements DataRightHandler{

	/**
	 * 
	 * <p>��ȡ����Ȩ�޿���SQL�ַ���</p>
	 *
	 * @param compCode        ��ǰ�Ĺ���������������λ
	 * @param copyCode        ��ǰ�Ĺ�����������������
	 * @param user            ��ǰ�û�
	 * @param tableId         �ṩ���ݷ�Χ��ʶ���ֵ������
	 * @param codeField       �����ؼ������ֶε��ֵ���ֶ�����
	 * @param bizTable        �����˵�ҵ�����ݱ�����
	 * @param PersistenceDAO  �־ò���ʶ���
	 * 
	 * @return SQL��ѯ�����ַ���
	 */
	public String createDataRightFilter(
			String compCode,
			String copyCode,
			User user,
			String tableId,
			String codeField,
			String bizTable,
			PersistenceDAO dao) throws BaseCheckedException{
		
		String retStr = "";
		
		retStr = " " + bizTable + "." + codeField + " in (";
		
		//���û���Ȩ
		retStr += " select code ";
		retStr += " from t_sys_user_code a, t_sys_company b ";
		retStr += " where a.comp_id=b.comp_id ";
		retStr += " and b.comp_code='" + compCode + "'";
		retStr += " and a.copy_code='" + copyCode + "'";
		retStr += " and a.table_id='" + tableId + "'";
		retStr += " and a.user_id=" + user.getUserid() + "";
		
		retStr += " union ";
		
		//���û�����Ȩ
		retStr += " select a.code ";
		retStr += " from t_sys_group_code a, t_sys_user_group b, t_sys_company c ";
		retStr += " where a.comp_id=c.comp_id ";
		retStr += " and c.comp_code='" + compCode + "'";
		retStr += " and a.copy_code='" + copyCode + "'";
		retStr += " and a.table_id='" + tableId + "'";
		retStr += " and a.group_id=b.group_id ";
		retStr += " and b.user_id=" + user.getUserid() + "";

		retStr += " )";
		
		
		
		return retStr;

	}
}
