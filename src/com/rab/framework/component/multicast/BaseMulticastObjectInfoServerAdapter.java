package com.rab.framework.component.multicast;

/**
 * 
 * <P>Title: BaseMulticastObjectInfoServerAdapter</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P>������������������, ���Դ�����յ�������, ʵ�ִ����ݴ���ģ�鵽Ӧ��ģ�������ת���ӿ�</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-12-3</P>
 *
 */
public interface BaseMulticastObjectInfoServerAdapter {
    /**
     * ������յ�������
     * <p/>
     * ������Ա���Լ̳в�ʵ�ָ÷���, ������ʵ�����ݵĸ�ʽת���Լ�����ҵ����
     *
     * @param obj ��������Ϊͨ���㲥��ʽ���յ�������, ��װ��ʽΪObject
     */
    public void dataHander(Object obj);
}
