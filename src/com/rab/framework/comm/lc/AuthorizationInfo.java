package com.rab.framework.comm.lc;

import java.util.Map;

/**
 * 
 * <P>Title: AuthorizationInfo</P>
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
public class AuthorizationInfo {
    /**
     * 格式
     */
    private String format;

    /**
     * 产品名称
     */
    private String product;

    /**
     * 版本
     */
    private String release;

    /**
     * 授权信息
     */
    private Map<String, AuthorizationItem> licenseObjects;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public Map<String, AuthorizationItem> getLicenseObjects() {
        return licenseObjects;
    }

    public void setLicenseObjects(Map<String, AuthorizationItem> licenseObjects) {
        this.licenseObjects = licenseObjects;
    }
}