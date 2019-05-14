package com.rab.framework.comm.security;

import java.io.Serializable;

/**
 * 
 * <P>Title: ITicket</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>用户登录的存根信息接口描述类</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public interface Ticket extends Serializable {
    /**
     * 取EJBsessionid
     *
     * @return
     */
    public String getUserSessionid();

    /**
     * 取登录后的授权凭证
     *
     * @return
     */
    public LogonSubject getSubject();

    /**
     * 
     * <p>取登陆后的环境变量信息</p>
     *
     * @return
     */
    public LogonEnvironment getLogonEnv();
    
    /**
     * 
     * <p>登陆后设置环境变量信息</p>
     *
     */
    public void setLogonEnv(LogonEnvironment logonEnv);
    /**
     * 返回用户信息
     *
     * @return
     */
    public User getUser();
    
    /**
     * 取登录时间
     * @return
     */
    public long getLogonTime();
    
    /**
     * 最后一次访问时间
     * @return
     */
    public long getLastAccessTime();
}
