package com.rab.framework.comm.util;


/**
 * 
 * <P>Title: RefUtils</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-16</P>
 *
 */
public class RefUtils {

    private RefUtils() {
    }

    /**
     * 得到传入参数的具体类型
     *
     * @param orb：Object
     * @return 参数的具体类型
     */
    public static String getDataType(Object obj) {
        if (obj == null) {
            return null;
        }

        String type = obj.getClass().getName();

        int pos = type.lastIndexOf(".");
        if (pos >= 0) {
            type = type.substring(pos + 1);
        }

        return type;

    }

}
