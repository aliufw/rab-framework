package com.rab.framework.component.multicast;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;

/**
 * 
 * <P>Title: MulitcastMessageSender</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>数据广播发送端接口, 提供客户端的数据发送功能</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-12-3</P>
 *
 */
public class MulticastMessageSender {
	/**
	 * 日志记录器
	 */
	private final LogWritter log = LogFactory.getLogger(this.getClass());

    /**
     * 静态类实例
     */
    private static MulticastMessageSender mm = null;

    /**
     * 私有构造器
     */
    private MulticastMessageSender() {
    }

    /**
     * 返回Sender对象实例
     *
     * @return
     */
    public static MulticastMessageSender singleton() {
        if (mm == null) {
            mm = new MulticastMessageSender();
        }
        return mm;
    }

    /**
     * 发送数据
     *
     * @param data    待发送的数据
     * @param adapter 服务器端适配器类
     * @throws BaseCheckedException 如果出现异常,则抛出VHBaseCheckedException异常描述对象
     */
    public void dataSend(byte[] data, String adapter) throws BaseCheckedException {
        MulticastClientManager mcm = new MulticastClientManager((byte) 0);
        mcm.dataSend(data, adapter);
    }

    /**
     * 发送数据
     *
     * @param obj     待发送的对象
     * @param adapter
     * @throws BaseCheckedException
     */
    public void dataSend(Serializable obj, String adapter) throws BaseCheckedException {
        MulticastClientManager mcm = new MulticastClientManager((byte) 1);

        try {
            //数据对象序列化
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            byte[] data = baos.toByteArray();

            //数据发送
            mcm.dataSend(data, adapter);
        } 
        catch (Exception e) {
            log.error("发送前数据对象序列化过程出现异常!", e);
            throw new BaseCheckedException("00000855", e);
        }
    }
}
