package com.rab.framework.comm.security;

import java.io.Serializable;

/**
 * 
 * <P>Title: ITicket</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P>�û���¼�Ĵ����Ϣ�ӿ�������</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public interface Ticket extends Serializable {
    /**
     * ȡEJBsessionid
     *
     * @return
     */
    public String getUserSessionid();

    /**
     * ȡ��¼�����Ȩƾ֤
     *
     * @return
     */
    public LogonSubject getSubject();

    /**
     * 
     * <p>ȡ��½��Ļ���������Ϣ</p>
     *
     * @return
     */
    public LogonEnvironment getLogonEnv();
    
    /**
     * 
     * <p>��½�����û���������Ϣ</p>
     *
     */
    public void setLogonEnv(LogonEnvironment logonEnv);
    /**
     * �����û���Ϣ
     *
     * @return
     */
    public User getUser();
    
    /**
     * ȡ��¼ʱ��
     * @return
     */
    public long getLogonTime();
    
    /**
     * ���һ�η���ʱ��
     * @return
     */
    public long getLastAccessTime();
}
