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
 * <P>程序说明：</P>
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
	 * 日志记录器
	 */
	private final LogWritter log = LogFactory.getLogger(this.getClass());

    /**
     * 报文头信息长度
     */
    public static final int HEADER_LENGTH = 140;

    /**
     * 标志发送数据的数据属性格式类型
     * dataType = 1; Object数据, 将对象序列化后传输, 在接收端可以自动完成反序列化的重构工作
     * dataType = 0; 结构化数据, 即平面化数据, 在接收端需要手工完成数据的解析工作
     */
    private byte dataType;

    /**
     * Server端的处理逻辑(适配器类名称)
     */
    private String serverAdapter = "";

    /**
     * 数据组标识, 固定长度 10 byte
     */
    private String groupid;

    /**
     * 数据组中包含PacketUnit的数量
     */
    private int count;

    /**
     * 在组内的顺序号
     */
    private int index;

    /**
     * 数据
     */
    private byte[] data;

    /**
     * 服务器名称标记, 集群中不同server的serverName不相同,
     * 该属性值由当前虚拟机中的MulticastServerManager实例获取
     * <p/>
     * 用途: 当MulticastServerManager接收到广播数据时,用该属性判断该信息
     * 是否由当前虚拟机发出.如果是,则一般不予处理.
     */
    private String serverName;

    /**
     * 生成报文数据包
     * 头长度： 130 Bytes
     * 01-01 ----------- 1  Byte,  标志发送数据的数据属性格式类型
     * 02-11 ----------- 10 Bytes, 报文组标记
     * 12-15 ----------- 4  Bytes, 报文组长度
     * 16-19 ----------- 4  Bytes, 报文在组中的顺序号
     * * 20-29 ----------- 10  Bytes, 当前server的serverName
     * 30-33 ----------- 4  Bytes, 数据区长度
     * 34-34 ----------- 1  Byte,  server端适配器类名称长度
     * 35-?  ----------- ?  Bytes, server端适配器类名称
     * <p/>
     * 141-end ---------    数据区
     *
     * @return
     */
    public byte[] getPacketUnitBytes() {
        byte[] bheader = new byte[HEADER_LENGTH];

        int pos = 0;

        //标志发送数据的数据属性格式类型
        bheader[0] = this.dataType;
        pos++;

        //报文组标记
        System.arraycopy(this.groupid.getBytes(), 0, bheader, pos, 10);
        pos += 10;

        //报文组长度
        System.arraycopy(MulticastUtils.int2bytes(this.count), 0, bheader, pos, 4);
        pos += 4;

        //报文在组中的顺序号
        System.arraycopy(MulticastUtils.int2bytes(this.index), 0, bheader, pos, 4);
        pos += 4;

        //当前server的serverName
        if(this.serverName == null){
        	this.serverName = "";
        }
        System.arraycopy(this.serverName.getBytes(), 0, bheader, pos, this.serverName.getBytes().length);
        pos += 10;

        //数据区长度
        System.arraycopy(MulticastUtils.int2bytes(this.data.length), 0, bheader, pos, 4);
        pos += 4;

        //server端适配器类描述
        byte[] bserverAdapter = this.serverAdapter.getBytes();
        byte classNameLen = (byte) bserverAdapter.length;
        bheader[pos] = classNameLen;
        pos++;
        System.arraycopy(bserverAdapter, 0, bheader, pos,
                bserverAdapter.length);
        pos += bserverAdapter.length;

        //生成总信息
        byte[] buffer = new byte[this.data.length + HEADER_LENGTH];
        pos = 0;

        //报文中的头信息区
        System.arraycopy(bheader, 0, buffer, 0, bheader.length);
        pos += bheader.length;

        //报文中的数据区
        System.arraycopy(this.data, 0, buffer, pos, this.data.length);

        return buffer;
    }

    /**
     * 解析数据报文
     *
     * @param packetData
     */
    public void parseData(byte[] packetData) throws BaseCheckedException {
        int pos = 0;

        //头信息
        if (packetData.length < HEADER_LENGTH) {
        	String msg = "报文数据格式错误, 报文头长度小于规定报文头的标识长度(" + HEADER_LENGTH + ")!";
            log.error(msg);
            List<String> params = new ArrayList<String>();
            params.add("" + HEADER_LENGTH);
            throw new BaseCheckedException("00000852", params);
        }

        //标志发送数据的数据属性格式类型
        this.dataType = packetData[0];
        pos++;

        //报文组标记
        byte[] bgroupid = new byte[10];
        System.arraycopy(packetData, pos, bgroupid, 0, 10);
        this.groupid = new String(bgroupid);
        pos += 10;

        //报文组长度
        byte[] bcount = new byte[4];
        System.arraycopy(packetData, pos, bcount, 0, 4);
        this.count = MulticastUtils.bytes2int(bcount);
        pos += 4;

        //报文在组中的顺序号
        byte[] bindex = new byte[4];
        System.arraycopy(packetData, pos, bindex, 0, 4);
        this.index = MulticastUtils.bytes2int(bindex);
        pos += 4;

//当前server的serverName
        byte[] bserverName = new byte[10];
        System.arraycopy(packetData, pos, bserverName, 0, 10);
        this.serverName = new String(bserverName);
        pos += 10;

        //数据区长度
        byte[] bdataLength = new byte[4];
        System.arraycopy(packetData, pos, bdataLength, 0, 4);
        int dataLength = MulticastUtils.bytes2int(bdataLength);
        pos += 4;

        //server端适配器类信息
        int classNameLen = (packetData[pos] + 256) % 256;
        pos++;
        byte[] bserverAdapter = new byte[classNameLen];
        System.arraycopy(packetData, pos, bserverAdapter, 0, classNameLen);
        this.serverAdapter = new String(bserverAdapter);
        pos += classNameLen;

        if (dataLength != packetData.length - HEADER_LENGTH) { //数据长度不对!
            String msg = "报文数据格式错误, 报文中标记的数据长度(" + dataLength
                    + ")与报文中的实际数据区长度(" + (packetData.length - HEADER_LENGTH)
                    + ")不一致!";
            log.error(msg);
            List<String> params = new ArrayList<String>();
            params.add("" + dataLength);
            params.add("" + (packetData.length - HEADER_LENGTH));
            throw new BaseCheckedException("00000853", params);
        }
        
        //数据信息
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