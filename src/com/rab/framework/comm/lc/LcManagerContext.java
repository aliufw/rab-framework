package com.rab.framework.comm.lc;

/**
 * 
 * 
 * <P>Title: LicenseManagerContext</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P> 
 * LicenseManager 的管理类,实现了如下功能:
 * 1. LicenseManager的初始化
 * 2. LicenseManager的应用接口管理</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-11-3</P>
 *
 */
public class LcManagerContext {

    //------------------------------------------------------------------- fields
    /**
     * LicenseManager 实例
     * 系统中只有一个 LicenseManager 实例
     */
    private LcManagerImpl instance = null;

    /**
     * LicenseManagerContext 实例变量,
     * 保证了系统中只有唯一的 LicenseManagerContext 实例
     */
    private static LcManagerContext context = null;



    //------------------------------------------------------------------- 构造器


    /**
     * 私有构造器
     */
    private LcManagerContext() {
        instance = new LcManagerImpl();
        instance.init();
    }



    //------------------------------------------------------------------- public

    /**
     * 静态方法, 单例模式接口,返回 LicenseManagerContext 实例
     */
    public static LcManagerContext singleton() {
        if (context == null) {
            context = new LcManagerContext();
        }
        return context;
    }

    /**
     * 返回 LicenseManager 实例
     *
     * @return
     */
    public LcManager getInstance() {
        return this.instance;
    }

    public static void main(String[] a) {
        LcManager ilm = LcManagerContext.singleton().getInstance();
        String[] s = ilm.getLicenseComponents();
        for (int i = 0; i < s.length; i++)
            System.out.println(s[i]);
    }
}
