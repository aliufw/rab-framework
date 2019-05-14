package com.rab.framework.comm.lc;

/**
 * 
 * <P>Title: Counter</P>
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
public class Counter {
    /**
     * 组件名称
     */
    private String licenseObjectName;

    /**
     * 最大允许并发数
     */
    private int counterMax;

    /**
     * 当前并发数指针
     */
    private int counter;

    public String getLicenseObjectName() {
        return licenseObjectName;
    }

    public void setLicenseObjectName(String licenseObjectName) {
        this.licenseObjectName = licenseObjectName;
    }

    public int getCounterMax() {
        return counterMax;
    }

    public void setCounterMax(int counterMax) {
        this.counterMax = counterMax;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}
