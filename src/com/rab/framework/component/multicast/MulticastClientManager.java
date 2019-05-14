package com.rab.framework.component.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;

/**
 * 
 * <P>Title: MulitcastClientManager</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P>���ݹ㲥���Ϳͻ���, ���ݴ������ݳ��Ⱥ�ϵͳ���İ���С����, �����ݲ��Ϊָ�����ȵ����ݶ�,
 * �ֱ��װΪ��Ӧ�����ݰ�,Ȼ���ٷ���</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-12-3</P>
 *
 */
public class MulticastClientManager {
	/**
	 * ��־��¼��
	 */
	private final LogWritter log = LogFactory.getLogger(this.getClass());

    /**
     * ��־�������ݵ��������Ը�ʽ����
     * dataType = 1; Object����, ���������л�����, �ڽ��ն˿����Զ���ɷ����л����ع�����
     * dataType = 0; �ṹ������, ��ƽ�滯����, �ڽ��ն���Ҫ�ֹ�������ݵĽ�������
     */
    private byte dataType = 0;

    /**
     * ���ݱ��ĳ���
     */
    private int packetLen;

    /**
     * �㲥��ַ
     */
    private String multicastAdress;

    /**
     * �㲥�˿ں�
     */
    private int multicastPort;

    /**
     * ������
     *
     * @param dataType ��־�������ݵ��������Ը�ʽ����
     */
    protected MulticastClientManager(byte dataType) {
        this.dataType = dataType;
        //��ʼ������
        init();
    }


    //-------------------------------------------------------------------------------- protected����

    /**
     * ��������, Ϊ����ı�̽ӿ�
     * <p/>
     * �ýӿڲ�ֱ�ӶԿ�����Աʹ��, ������Ա����ʹ�ð�װ��Ľӿ�:
     * MulitcastMessageSender.dataSend(byte[] data, String adapter)
     *
     * @param data    �����͵�����(byte����)
     * @param adapter �������˽���������������
     * @throws BaseCheckedException ����ʧ��: �׳�VHBaseCheckedException�쳣
     */
    protected void dataSend(byte[] data, String adapter) throws BaseCheckedException {
        List<PacketUnit> list = packetPartition(data, adapter);
        send(list);
    }

    //-------------------------------------------------------------------------------- private ����

    /**
     * ��ʼ������ֵ
     */
    private void init() {
    	Properties prop = (Properties)ApplicationContext.singleton().getValueByKey("multicast");
    	String multicastAdress = prop.getProperty("multicastadress");
        String multicastPort = prop.getProperty("multicastport");

        if (multicastAdress != null) { 
            this.multicastAdress = multicastAdress;
        } else {
            this.multicastAdress = MulticastParameters.multicastAdress;
        }

        if (multicastPort != null) {
            int port = Integer.parseInt(multicastPort.trim());
            this.multicastPort = port;
        } else {
            this.multicastPort = MulticastParameters.multicastPort;
        }

        String strpacketlen = prop.getProperty("packetlen");
        if (strpacketlen != null) {
            int packetLen = Integer.parseInt(strpacketlen.trim());
            this.packetLen = packetLen;
        } else {
            this.packetLen = MulticastParameters.packetLen;
        }
    }

    /**
     * ����ϵͳ�ı��ĳ��Ȳ����趨�ָ�����, Ϊ�������Ƴ��ȵı�����׼��
     *
     * @param data    �����͵�����
     * @param adapter ��������������������
     * @return �����ָ������(PacketUnit����)������
     */
    private List<PacketUnit> packetPartition(byte[] data, String adapter) {
        List<PacketUnit> al = new ArrayList<PacketUnit>();
        int currPos = 0;
        //���ʶ
        String groupid = this.createPacketGroupid();
        //��������Ԫ������
        int count = data.length / packetLen;
        if (data.length % packetLen > 0) {
            count++;
        }

        //����
        byte[] dataDiv = null;

        while (data.length - currPos > 0) {
            if (data.length - currPos > packetLen) {
                dataDiv = new byte[packetLen];
            } else {
                dataDiv = new byte[data.length - currPos];
            }
            System.arraycopy(data, currPos, dataDiv, 0, dataDiv.length);

            PacketUnit pu = new PacketUnit();
            pu.setDataType(this.dataType);
            pu.setGroupid(groupid);
            pu.setIndex(al.size());
            pu.setCount(count);
            pu.setData(dataDiv);
            pu.setServerAdapter(adapter);
            pu.setServerName(MulticastServerManager.MULTICAST_SERVER_NAME);

            al.add(pu);

            currPos += dataDiv.length;
        }

        return al;
    }

    /**
     * ��������
     * <p/>
     * ���ָ��걸�������Թ㲥��ʽ���͸�������
     *
     * @param al PacketUnit�����б�
     * @throws BaseCheckedException ��������쳣,���׳�VHBaseCheckedException�쳣��������
     */
    private void send(List<PacketUnit> al) throws BaseCheckedException {
        try {
            long count = 0;

            for (int i = 0; i < al.size(); i++) {
                PacketUnit pu = (PacketUnit) al.get(i);
                byte[] data = pu.getPacketUnitBytes();
                DatagramPacket dp = new DatagramPacket(data, data.length);
                MulticastSocket ms = new MulticastSocket();
                InetAddress ia = InetAddress.getByName(multicastAdress);
                ms.connect(ia, multicastPort);
                ms.send(dp);
                ms.close();

                count += data.length;
            }
//			if(true) {
//				System.out.println("�׳��쳣!");
//				throw new RuntimeException("***************");
//			}
            log.debug("���ͱ���: ������[" + al.size() + "], ���ݳ���[" + count + "]");
        } catch (UnknownHostException e) {
            log.error("��Ⱥ�㲥����ʱ�����쳣, ���ҷ�������ַʧ�ܣ���ȷ�Ϸ�������ַ������ȷ!", e);
            throw new BaseCheckedException("00000850", e);
        } catch (IOException e) {
            log.error("��Ⱥ�㲥����ʱ�����쳣, ���ݴ���IO����", e);
            throw new BaseCheckedException("00000851", e);
        }
    }


    /**
     * ��������, ���ڽ��ն˱��ĵķ������
     *
     * @return ����ַ���
     */
    private String createPacketGroupid() {
        String s = "";
        for (int i = 0; i < 10; i++) {
            s += (int) (10 * Math.random());
        }
        return s;
    }
}
