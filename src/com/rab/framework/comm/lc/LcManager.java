package com.rab.framework.comm.lc;

/**
 * 
 * <P>Title: ILicenseManager</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-11-3</P>
 *
 */
public interface LcManager {
    /**
     * 获取可以加载的组件的名称
     *
     * @return String[],包含了可以加载的组件的名称
     */
    public String[] getLicenseComponents();

}
