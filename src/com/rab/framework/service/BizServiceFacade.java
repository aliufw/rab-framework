package com.rab.framework.service;

import com.rab.framework.comm.dto.event.BaseRequestEvent;
import com.rab.framework.comm.dto.event.BaseResponseEvent;

/**
 * 
 * <P>Title: IBizServiceFacade</P>
 * <P>Description: </P>
 * <P>����˵����ҵ�������װ�ӿ�</P>
 * <P>���ӿڶ�����ҵ���Ľ��뷽ʽ����ʵ��ʱ���ɸ��ݾ����Ӧ�üܹ����󣬷ֱ���費ͬ��ʵ��</P>
 * <P>���磺</P>
 * <li>EJB�ӿڣ�����EJB������װҵ���������������񻯡��ֲ�ʽӦ�û���</li>
 * <li>���ؽӿڣ�����javaBean������װҵ�����������ڹ�ģ��С�ġ����л����𻷾�</li>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-8-9</P>
 *
 */
public interface BizServiceFacade {
	/**
	 * ����ӿ�
	 * 
	 * @param reqEvent
	 * @return
	 * @throws Exception
	 */
	public BaseResponseEvent invoke(BaseRequestEvent reqEvent) throws Exception ;
}
