package com.rab.framework.component.multicast;

/**
 * 
 * <P>Title: MulticastParameters</P>
 * <P>Description: </P>
 * <P>����˵����</P>
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
     * �㲥��ַ
     */
    public final static String multicastAdress = "237.0.0.1";

    /**
     * �㲥�˿ں�
     */
    public final static int multicastPort = 5555;

    /**
     * ���ݱ��ĳ���
     */
    public final static int packetLen = 1024;

    /**
     * ����������ݱ��ĵĻ��泬ʱʱ��, ��λΪ��
     */
    public final static long timeout = 10;

}
