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
 * <P>����˵����</P>
 * <P>���ݹ㲥���Ͷ˽ӿ�, �ṩ�ͻ��˵����ݷ��͹���</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-12-3</P>
 *
 */
public class MulticastMessageSender {
	/**
	 * ��־��¼��
	 */
	private final LogWritter log = LogFactory.getLogger(this.getClass());

    /**
     * ��̬��ʵ��
     */
    private static MulticastMessageSender mm = null;

    /**
     * ˽�й�����
     */
    private MulticastMessageSender() {
    }

    /**
     * ����Sender����ʵ��
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
     * ��������
     *
     * @param data    �����͵�����
     * @param adapter ����������������
     * @throws BaseCheckedException ��������쳣,���׳�VHBaseCheckedException�쳣��������
     */
    public void dataSend(byte[] data, String adapter) throws BaseCheckedException {
        MulticastClientManager mcm = new MulticastClientManager((byte) 0);
        mcm.dataSend(data, adapter);
    }

    /**
     * ��������
     *
     * @param obj     �����͵Ķ���
     * @param adapter
     * @throws BaseCheckedException
     */
    public void dataSend(Serializable obj, String adapter) throws BaseCheckedException {
        MulticastClientManager mcm = new MulticastClientManager((byte) 1);

        try {
            //���ݶ������л�
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            byte[] data = baos.toByteArray();

            //���ݷ���
            mcm.dataSend(data, adapter);
        } 
        catch (Exception e) {
            log.error("����ǰ���ݶ������л����̳����쳣!", e);
            throw new BaseCheckedException("00000855", e);
        }
    }
}
