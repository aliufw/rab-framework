package com.rab.framework.component.multicast;

/**
 * 
 * <P>Title: MulticastParameters</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-12-3</P>
 *
 */
public class MulticastParameters {
    private MulticastParameters() {

    }

    /**
     * 广播地址
     */
    public final static String multicastAdress = "237.0.0.1";

    /**
     * 广播端口号
     */
    public final static int multicastPort = 5555;

    /**
     * 数据报文长度
     */
    public final static int packetLen = 1024;

    /**
     * 缓存池中数据报文的缓存超时时间, 单位为秒
     */
    public final static long timeout = 10;

}
