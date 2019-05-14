package com.rab.framework.component.multicast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;

/**
 * 
 * <P>Title: MulticastServerManager</P>
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
public class MulticastServerManager {
	/**
	 * 日志记录器
	 */
	private final LogWritter log = LogFactory.getLogger(this.getClass());

    /**
     * 服务器名称标记, 集群中不同server的serverName不相同
     * <p/>
     * 用途: 当MulticastServerManager接收到广播数据时,用该属性判断该信息
     * 是否由当前虚拟机发出.如果是,则一般不予处理.
     * MULTICAST_SERVER_NAME
     */
    protected static String MULTICAST_SERVER_NAME = null;

    //----------------------------------------------------------------------------------- 内部属性


    /**
     * 广播地址
     */
    private String multicastAdress;

    /**
     * 广播端口号
     */
    private int multicastPort;

    /**
     * 数据报文长度
     */
    private int packetLen;

    /**
     * 缓存池中数据报文的缓存超时时间, 单位为秒
     */
    protected long timeout;

    /**
     * 当前MultiServer监听用线程
     */
    private Thread multiServerThread = null;

    /**
     * 当前监控线程
     */
    private Thread cycThread = null;

    /**
     * 数据报文的缓存池
     */
    private Map<String,PacketGroupInfo> pool = new HashMap<String,PacketGroupInfo>();


    //------------------------------------------------------------------------------- 构造器 方法

    /**
     * 构造器
     */
    public MulticastServerManager() {
        init();
    }


    //------------------------------------------------------------------------------- public 方法


    /**
     * 程序入口
     *
     * @param argv
     */
    public static void main(String[] argv) {
        Properties prop = (Properties)ApplicationContext.singleton().getValueByKey("multicast");
    	if(prop != null){
            String state = prop.getProperty("state");
        	if (state != null && state.equalsIgnoreCase("on")) {
                MulticastServerManager msm = new MulticastServerManager();
                msm.start();
            }
    	}
    }


    /**
     * 开始, 启动服务
     */
    public void start() {
        //启动Server线程
        MulticastServer server = new MulticastServer();
        this.multiServerThread = server;
        server.start();
        log.debug("启动MulticastServer线程!");

        //启动生存期监控线程
        LifeCyc lifeCyc = new LifeCyc();
        this.cycThread = lifeCyc;
        lifeCyc.start();
        log.debug("启动MulticastServer生存期监控线程!");
    }

    //------------------------------------------------------------------------------- private 方法


    /**
     * 初始化参数值
     */
    private void init() {
//生成server名字
        MULTICAST_SERVER_NAME = this.createServerName();

        //初始化属性值: multicastAdress, multicastPort
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

        
        //初始化其他参数
        String strpacketlen = prop.getProperty("packetlen");
        String strtimeout = prop.getProperty("strtimeout");

        if (strpacketlen != null) {
            int packetLen = Integer.parseInt(strpacketlen.trim());
            this.packetLen = packetLen;
        } else {
            this.packetLen = MulticastParameters.packetLen;
        }

        if (strtimeout != null) {
            int timeout = Integer.parseInt(strtimeout.trim());
            this.timeout = timeout;
        } else {
            this.timeout = MulticastParameters.timeout;
        }
    }

    /**
     * 在虚拟机加载MulticastServerManager时初始化该属性
     */
    private String createServerName() {
        String name = "";
        for (int i = 0; i < 10; i++) {
            name += (int) (10 * Math.random());
        }
        return name;
    }

    /**
     * 创建监听对象
     *
     * @return MulticastSocket 有效的监听对象
     */
    private MulticastSocket createMulticastSocket() {
        MulticastSocket socket = null;

        try {
            socket = new MulticastSocket(multicastPort);

            InetAddress ia = InetAddress.getByName(multicastAdress);
            socket.joinGroup(ia);

        } catch (UnknownHostException e) {
            log.error("严重异常!!, 创建创建监听对象失败", e);
            exit(-1);
        } catch (IOException e) {
            log.error("严重异常!!, 创建创建监听对象失败", e);
            exit(-1);
        }

        return socket;
    }

    /**
     * 出现了严重异常, 系统将退出运行
     *
     * @param code 退出异常代码
     */
    private void exit(int code) {
        System.exit(code);
    }

    /**
     * 报文处理
     *
     * @param buffer
     * @param buffer
     */
    private void packetHandler(byte[] buffer, int len) {
        //从缓存中提取数据
        byte[] data = new byte[len];
        System.arraycopy(buffer, 0, data, 0, len);
        PacketUnit pu = new PacketUnit();

        //解析报文
        try {
            pu.parseData(data);

        } catch (BaseCheckedException e) {
            log.error("报文数据解析失败!", e);
            return;
        }

//检查报文来源,如果来源于当前JVM, 则不予处理,直接返回
        if (MULTICAST_SERVER_NAME.equals(pu.getServerName())) {
            return;
        }

        //解析报文组的长度
        String groupid = pu.getGroupid();
        int count = pu.getCount();
        if (count == 1) {
            //报文组长度为1, 则数据在传输时没有被拆分, 直接处理即可
            dataHandler(pu);
        } else if (count > 1) {
            //报文组长度大于1, 则数据在传输时没有被拆分, 需要分析当前该报文组是否接收完整,
            //只有在接收完整的情况下, 才进行数据处理, 否则继续等待接收直到接收完整或超时
            PacketGroupInfo pgi = (PacketGroupInfo) this.pool.get(groupid);
            if (pgi == null) {
                pgi = new PacketGroupInfo();
                this.pool.put(groupid, pgi);
            }

            pgi.addPacketUnit(pu);

            if (pgi.getCount() == count) {
                //已经接收到了该报文组的全部报文数据,可以处理了!
                List<PacketUnit> list = pgi.getList();
                dataHandler(list);//处理报文数据
                this.pool.remove(groupid);//从缓存中删除缓存的报文信息
            } else {
                //数据尚未接收完毕, 将已经接收的数据暂时缓存起来,带数据接收完毕后再处理
                ;
            }
        }
    }

    /**
     * 多报文数据处理
     *
     * @param al 报文列表
     */
    private void dataHandler(List<PacketUnit> al) {
        //按照创建时给定的许还标志排序
        Collections.sort(al, new PacketUnitSortComparator());

        //适配器类名称
        String adapterClassName = "";
        byte dataType = 0;

        //计算数据长度
        int totallen = 0;
        for (int i = 0; i < al.size(); i++) {
            PacketUnit pu = (PacketUnit) al.get(i);
            totallen += pu.getData().length;
            adapterClassName = pu.getServerAdapter();
            dataType = pu.getDataType();
        }

        //合并报文数据
        byte[] data = new byte[totallen];
        int pos = 0;
        for (int i = 0; i < al.size(); i++) {
            PacketUnit pu = (PacketUnit) al.get(i);
            byte[] tmp = pu.getData();
            System.arraycopy(tmp, 0, data, pos, tmp.length);
            pos += tmp.length;
        }

        log.debug("Server端适配器类: className = " + adapterClassName);
        log.debug("接收报文: 报文组总长度: list.size() = " + al.size());
        log.debug("接收报文: 报文组数据长度: totallen = " + totallen);

        dataProcess(data, adapterClassName, dataType);
    }

    /**
     * 单报文数据处理
     *
     * @param pu
     */
    private void dataHandler(PacketUnit pu) {
        byte[] data = pu.getData();
        String adapterClassName = pu.getServerAdapter();
        byte dataType = pu.getDataType();

        dataProcess(data, adapterClassName, dataType);
    }

    /**
     * 调用适配器处理报文数据
     *
     * @param data
     * @param adapterClassName
     */
    private void dataProcess(byte[] data, String adapterClassName, byte dataType) {

        try {

            Object obj = null;
            if (dataType == 1) { //对象数据
                BaseMulticastObjectInfoServerAdapter adapter = (BaseMulticastObjectInfoServerAdapter) Class.forName(adapterClassName).newInstance();
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                ObjectInputStream ois = new ObjectInputStream(bais);
                obj = ois.readObject();
                adapter.dataHander(obj);
            } else {
                BaseMulticastFlatInfoServerAdapter adapter = (BaseMulticastFlatInfoServerAdapter) Class.forName(adapterClassName).newInstance();
                adapter.dataHander(data);
            }
        } catch (Exception e) {
            log.error("调用Server端适配器时出现异常!", e);
        }
    }


    //------------------------------------------------------------------------------------ 内部类



    /**
     * 内部类, 用于监听报文广播
     */
    class MulticastServer extends Thread {
        /**
         * 覆盖Thread方法,实现了数据报文的接收和处理
         */
        public void run() {
            //创建监听用socket对象
            MulticastSocket socket = createMulticastSocket();

            //数据接收缓冲区
            byte[] buffer = new byte[packetLen + PacketUnit.HEADER_LENGTH];
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);

            while (true) {
                try {
                    socket.receive(dp); //接收数据报文

                    //取数据
                    byte[] buff = dp.getData();
                    int len = dp.getLength();
                    //处理数据报文
                    packetHandler(buff, len);

                    //监控缓存报文的超时处理控制线程, 如果该线程不可用, 则再创建一个新的线程
                    if (!cycThread.isAlive()) {
                        LifeCyc lifeCyc = new LifeCyc();
                        cycThread = lifeCyc;
                        lifeCyc.start();
                        log.info("缓存报文的超时处理控制线程失效, 重新创建该线程! ");
                    }
                } catch (IOException e) {
                    //如果MulticastSocket对象已经关闭,则再创建一个新的监听对象
                    if (socket.isClosed()) {
                        socket = createMulticastSocket();
                    }
                    log.error("接收广播数据时出现异常!", e);
                }
            }
        }
    }

    /**
     * 内部类, 用于控制报文池中缓存报文的超时处理.
     * 如果报文存在时间超出设定时间, 则清除该报文信息
     */
    class LifeCyc extends Thread {
        public void run() {
            long currTime = System.currentTimeMillis();
            long timeoutMillis = timeout * 1000;

            try {
                while (true) {
                    //检查缓存中数据报文是否超时
                    synchronized (pool) {
                        Iterator<String> iter = pool.keySet().iterator();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            PacketGroupInfo pgi = (PacketGroupInfo) pool.get(key);
                            if (currTime - pgi.getCreateTime() > timeoutMillis) {
                                pool.remove(key);
                                log.info("MulticastServer删除超时的缓存报文信息! key = "
                                        + key);
                            }
                        }
                    }

                    //休眠时间
                    sleep(timeout / 2 * 1000);

                    //监控server线程,如果该线程不可用,则再创建一个新的线程
                    if (!multiServerThread.isAlive()) {
                        MulticastServer server = new MulticastServer();
                        multiServerThread = server;
                        server.start();
                        log.info("监控server线程失效, 重新创建监控server线程! ");
                    }
                }
            } catch (InterruptedException e) {
                log.error("MulticastServer超时报文管理线程运行异常!", e);
            }
        }
    }

}




