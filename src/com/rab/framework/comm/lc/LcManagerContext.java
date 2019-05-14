package com.rab.framework.comm.lc;

/**
 * 
 * 
 * <P>Title: LicenseManagerContext</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P> 
 * LicenseManager �Ĺ�����,ʵ�������¹���:
 * 1. LicenseManager�ĳ�ʼ��
 * 2. LicenseManager��Ӧ�ýӿڹ���</P>
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
     * LicenseManager ʵ��
     * ϵͳ��ֻ��һ�� LicenseManager ʵ��
     */
    private LcManagerImpl instance = null;

    /**
     * LicenseManagerContext ʵ������,
     * ��֤��ϵͳ��ֻ��Ψһ�� LicenseManagerContext ʵ��
     */
    private static LcManagerContext context = null;



    //------------------------------------------------------------------- ������


    /**
     * ˽�й�����
     */
    private LcManagerContext() {
        instance = new LcManagerImpl();
        instance.init();
    }



    //------------------------------------------------------------------- public

    /**
     * ��̬����, ����ģʽ�ӿ�,���� LicenseManagerContext ʵ��
     */
    public static LcManagerContext singleton() {
        if (context == null) {
            context = new LcManagerContext();
        }
        return context;
    }

    /**
     * ���� LicenseManager ʵ��
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
