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
 * <P>程序说明：</P>
 * <P>数据广播发送客户端, 根据传送数据长度和系统报文包大小限制, 将数据拆分为指定长度的数据段,
 * 分别包装为对应的数据包,然后再发送</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-12-3</P>
 *
 */
public class MulticastClientManager {
	/**
	 * 日志记录器
	 */
	private final LogWritter log = LogFactory.getLogger(this.getClass());

    /**
     * 标志发送数据的数据属性格式类型
     * dataType = 1; Object数据, 将对象序列化后传输, 在接收端可以自动完成反序列化的重构工作
     * dataType = 0; 结构化数据, 即平面化数据, 在接收端需要手工完成数据的解析工作
     */
    private byte dataType = 0;

    /**
     * 数据报文长度
     */
    private int packetLen;

    /**
     * 广播地址
     */
    private String multicastAdress;

    /**
     * 广播端口号
     */
    private int multicastPort;

    /**
     * 构造器
     *
     * @param dataType 标志发送数据的数据属性格式类型
     */
    protected MulticastClientManager(byte dataType) {
        this.dataType = dataType;
        //初始化参数
        init();
    }


    //-------------------------------------------------------------------------------- protected方法

    /**
     * 发送数据, 为对外的编程接口
     * <p/>
     * 该接口不直接对开发人员使用, 开发人员可以使用包装后的接口:
     * MulitcastMessageSender.dataSend(byte[] data, String adapter)
     *
     * @param data    待发送的数据(byte数组)
     * @param adapter 服务器端解析适配器类名称
     * @throws BaseCheckedException 发送失败: 抛出VHBaseCheckedException异常
     */
    protected void dataSend(byte[] data, String adapter) throws BaseCheckedException {
        List<PacketUnit> list = packetPartition(data, adapter);
        send(list);
    }

    //-------------------------------------------------------------------------------- private 方法

    /**
     * 初始化参数值
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
     * 根据系统的报文长度参数设定分割数据, 为生成限制长度的报文做准备
     *
     * @param data    待发送的数据
     * @param adapter 服务器端适配器类名称
     * @return 包含分割后数据(PacketUnit对象)的数组
     */
    private List<PacketUnit> packetPartition(byte[] data, String adapter) {
        List<PacketUnit> al = new ArrayList<PacketUnit>();
        int currPos = 0;
        //组标识
        String groupid = this.createPacketGroupid();
        //组中数据元素数量
        int count = data.length / packetLen;
        if (data.length % packetLen > 0) {
            count++;
        }

        //数据
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
     * 发送数据
     * <p/>
     * 将分割完备的数据以广播方式发送给接收者
     *
     * @param al PacketUnit对象列表
     * @throws BaseCheckedException 如果发送异常,则抛出VHBaseCheckedException异常描述对象
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
//				System.out.println("抛出异常!");
//				throw new RuntimeException("***************");
//			}
            log.debug("发送报文: 报文数[" + al.size() + "], 数据长度[" + count + "]");
        } catch (UnknownHostException e) {
            log.error("集群广播数据时发送异常, 查找服务器地址失败，请确认服务器地址配置正确!", e);
            throw new BaseCheckedException("00000850", e);
        } catch (IOException e) {
            log.error("集群广播数据时发送异常, 数据传输IO错误", e);
            throw new BaseCheckedException("00000851", e);
        }
    }


    /**
     * 创建组标记, 用于接收端报文的分组管理
     *
     * @return 标记字符串
     */
    private String createPacketGroupid() {
        String s = "";
        for (int i = 0; i < 10; i++) {
            s += (int) (10 * Math.random());
        }
        return s;
    }
}
