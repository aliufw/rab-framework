package com.rab.framework.component.multicast;

import java.util.ArrayList;
import java.util.List;

import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;


/**
 * 
 * <P>Title: PacketUnit</P>
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
public class PacketUnit {
	/**
	 * ��־��¼��
	 */
	private final LogWritter log = LogFactory.getLogger(this.getClass());

    /**
     * ����ͷ��Ϣ����
     */
    public static final int HEADER_LENGTH = 140;

    /**
     * ��־�������ݵ��������Ը�ʽ����
     * dataType = 1; Object����, ���������л�����, �ڽ��ն˿����Զ���ɷ����л����ع�����
     * dataType = 0; �ṹ������, ��ƽ�滯����, �ڽ��ն���Ҫ�ֹ�������ݵĽ�������
     */
    private byte dataType;

    /**
     * Server�˵Ĵ����߼�(������������)
     */
    private String serverAdapter = "";

    /**
     * �������ʶ, �̶����� 10 byte
     */
    private String groupid;

    /**
     * �������а���PacketUnit������
     */
    private int count;

    /**
     * �����ڵ�˳���
     */
    private int index;

    /**
     * ����
     */
    private byte[] data;

    /**
     * ���������Ʊ��, ��Ⱥ�в�ͬserver��serverName����ͬ,
     * ������ֵ�ɵ�ǰ������е�MulticastServerManagerʵ����ȡ
     * <p/>
     * ��;: ��MulticastServerManager���յ��㲥����ʱ,�ø������жϸ���Ϣ
     * �Ƿ��ɵ�ǰ���������.�����,��һ�㲻�账��.
     */
    private String serverName;

    /**
     * ���ɱ������ݰ�
     * ͷ���ȣ� 130 Bytes
     * 01-01 ----------- 1  Byte,  ��־�������ݵ��������Ը�ʽ����
     * 02-11 ----------- 10 Bytes, ��������
     * 12-15 ----------- 4  Bytes, �����鳤��
     * 16-19 ----------- 4  Bytes, ���������е�˳���
     * * 20-29 ----------- 10  Bytes, ��ǰserver��serverName
     * 30-33 ----------- 4  Bytes, ����������
     * 34-34 ----------- 1  Byte,  server�������������Ƴ���
     * 35-?  ----------- ?  Bytes, server��������������
     * <p/>
     * 141-end ---------    ������
     *
     * @return
     */
    public byte[] getPacketUnitBytes() {
        byte[] bheader = new byte[HEADER_LENGTH];

        int pos = 0;

        //��־�������ݵ��������Ը�ʽ����
        bheader[0] = this.dataType;
        pos++;

        //��������
        System.arraycopy(this.groupid.getBytes(), 0, bheader, pos, 10);
        pos += 10;

        //�����鳤��
        System.arraycopy(MulticastUtils.int2bytes(this.count), 0, bheader, pos, 4);
        pos += 4;

        //���������е�˳���
        System.arraycopy(MulticastUtils.int2bytes(this.index), 0, bheader, pos, 4);
        pos += 4;

        //��ǰserver��serverName
        if(this.serverName == null){
        	this.serverName = "";
        }
        System.arraycopy(this.serverName.getBytes(), 0, bheader, pos, this.serverName.getBytes().length);
        pos += 10;

        //����������
        System.arraycopy(MulticastUtils.int2bytes(this.data.length), 0, bheader, pos, 4);
        pos += 4;

        //server��������������
        byte[] bserverAdapter = this.serverAdapter.getBytes();
        byte classNameLen = (byte) bserverAdapter.length;
        bheader[pos] = classNameLen;
        pos++;
        System.arraycopy(bserverAdapter, 0, bheader, pos,
                bserverAdapter.length);
        pos += bserverAdapter.length;

        //��������Ϣ
        byte[] buffer = new byte[this.data.length + HEADER_LENGTH];
        pos = 0;

        //�����е�ͷ��Ϣ��
        System.arraycopy(bheader, 0, buffer, 0, bheader.length);
        pos += bheader.length;

        //�����е�������
        System.arraycopy(this.data, 0, buffer, pos, this.data.length);

        return buffer;
    }

    /**
     * �������ݱ���
     *
     * @param packetData
     */
    public void parseData(byte[] packetData) throws BaseCheckedException {
        int pos = 0;

        //ͷ��Ϣ
        if (packetData.length < HEADER_LENGTH) {
        	String msg = "�������ݸ�ʽ����, ����ͷ����С�ڹ涨����ͷ�ı�ʶ����(" + HEADER_LENGTH + ")!";
            log.error(msg);
            List<String> params = new ArrayList<String>();
            params.add("" + HEADER_LENGTH);
            throw new BaseCheckedException("00000852", params);
        }

        //��־�������ݵ��������Ը�ʽ����
        this.dataType = packetData[0];
        pos++;

        //��������
        byte[] bgroupid = new byte[10];
        System.arraycopy(packetData, pos, bgroupid, 0, 10);
        this.groupid = new String(bgroupid);
        pos += 10;

        //�����鳤��
        byte[] bcount = new byte[4];
        System.arraycopy(packetData, pos, bcount, 0, 4);
        this.count = MulticastUtils.bytes2int(bcount);
        pos += 4;

        //���������е�˳���
        byte[] bindex = new byte[4];
        System.arraycopy(packetData, pos, bindex, 0, 4);
        this.index = MulticastUtils.bytes2int(bindex);
        pos += 4;

//��ǰserver��serverName
        byte[] bserverName = new byte[10];
        System.arraycopy(packetData, pos, bserverName, 0, 10);
        this.serverName = new String(bserverName);
        pos += 10;

        //����������
        byte[] bdataLength = new byte[4];
        System.arraycopy(packetData, pos, bdataLength, 0, 4);
        int dataLength = MulticastUtils.bytes2int(bdataLength);
        pos += 4;

        //server������������Ϣ
        int classNameLen = (packetData[pos] + 256) % 256;
        pos++;
        byte[] bserverAdapter = new byte[classNameLen];
        System.arraycopy(packetData, pos, bserverAdapter, 0, classNameLen);
        this.serverAdapter = new String(bserverAdapter);
        pos += classNameLen;

        if (dataLength != packetData.length - HEADER_LENGTH) { //���ݳ��Ȳ���!
            String msg = "�������ݸ�ʽ����, �����б�ǵ����ݳ���(" + dataLength
                    + ")�뱨���е�ʵ������������(" + (packetData.length - HEADER_LENGTH)
                    + ")��һ��!";
            log.error(msg);
            List<String> params = new ArrayList<String>();
            params.add("" + dataLength);
            params.add("" + (packetData.length - HEADER_LENGTH));
            throw new BaseCheckedException("00000853", params);
        }
        
        //������Ϣ
        byte[] bdata = new byte[packetData.length - HEADER_LENGTH];
        System.arraycopy(packetData, HEADER_LENGTH, bdata, 0, bdata.length);
        this.data = bdata;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getServerAdapter() {
        return serverAdapter;
    }

    public void setServerAdapter(String serverAdapter) {
        this.serverAdapter = serverAdapter;
    }

    public byte getDataType() {
        return dataType;
    }

    public void setDataType(byte dataType) {
        this.dataType = dataType;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}