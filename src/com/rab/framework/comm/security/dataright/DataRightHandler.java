package com.rab.framework.comm.security.dataright;

import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.security.User;
import com.rab.framework.dao.PersistenceDAO;

/**
 * 
 * <P>Title: DataRightHandler</P>
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
public interface DataRightHandler {

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
			PersistenceDAO dao) throws BaseCheckedException;
}
