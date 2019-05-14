package com.rab.framework.comm.security;

import javax.servlet.ServletRequest;

import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.dao.PersistenceDAO;

/**
 * 
 * <P>Title: SecurityManager</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-10-9</P>
 *
 */
public interface SecurityManager {
	
	/**
	 * <p>����û�����URI�Ƿ��ǺϷ�����Ȩ����</p>
	 *
	 * @param uri  ������URI
	 * @param request  ��ǰ��ServletRequest����
	 * 
	 * @return  true - �з���Ȩ�ޣ� false - �޷���Ȩ��
	 */
	public boolean securityURICheck(String uri, ServletRequest request);
	
	/**
	 * <p>��鵱ǰ�û��Ƿ�ӵ��ָ������Ȩ��</p>
	 *
	 * @param permid  ����Ȩ�ޱ�ʶ
	 * @param request  ��ǰ��ServletRequest����
	 * @return  true - �з���Ȩ�ޣ� false - �޷���Ȩ��
	 */
	public boolean securityPermidCheck(String permid, ServletRequest request);
	
	
	/**
	 * 
	 * <p>��ȡ����Ȩ�޿���SQL�ַ���</p>
	 *
	 * @param compCode        ��ǰ�Ĺ���������������λ
	 * @param copyCode        ��ǰ�Ĺ�����������������
	 * @param user            ��ǰ�û�
	 * @param tableId         �ṩ���ݷ�Χ��ʶ���ֵ������
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
			String bizTable,
			PersistenceDAO dao) throws BaseCheckedException;

}
