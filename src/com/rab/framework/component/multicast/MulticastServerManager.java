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
 * <P>����˵����</P>
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
	 * ��־��¼��
	 */
	private final LogWritter log = LogFactory.getLogger(this.getClass());

    /**
     * ���������Ʊ��, ��Ⱥ�в�ͬserver��serverName����ͬ
     * <p/>
     * ��;: ��MulticastServerManager���յ��㲥����ʱ,�ø������жϸ���Ϣ
     * �Ƿ��ɵ�ǰ���������.�����,��һ�㲻�账��.
     * MULTICAST_SERVER_NAME
     */
    protected static String MULTICAST_SERVER_NAME = null;

    //----------------------------------------------------------------------------------- �ڲ�����


    /**
     * �㲥��ַ
     */
    private String multicastAdress;

    /**
     * �㲥�˿ں�
     */
    private int multicastPort;

    /**
     * ���ݱ��ĳ���
     */
    private int packetLen;

    /**
     * ����������ݱ��ĵĻ��泬ʱʱ��, ��λΪ��
     */
    protected long timeout;

    /**
     * ��ǰMultiServer�������߳�
     */
    private Thread multiServerThread = null;

    /**
     * ��ǰ����߳�
     */
    private Thread cycThread = null;

    /**
     * ���ݱ��ĵĻ����
     */
    private Map<String,PacketGroupInfo> pool = new HashMap<String,PacketGroupInfo>();


    //------------------------------------------------------------------------------- ������ ����

    /**
     * ������
     */
    public MulticastServerManager() {
        init();
    }


    //------------------------------------------------------------------------------- public ����


    /**
     * �������
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
     * ��ʼ, ��������
     */
    public void start() {
        //����Server�߳�
        MulticastServer server = new MulticastServer();
        this.multiServerThread = server;
        server.start();
        log.debug("����MulticastServer�߳�!");

        //���������ڼ���߳�
        LifeCyc lifeCyc = new LifeCyc();
        this.cycThread = lifeCyc;
        lifeCyc.start();
        log.debug("����MulticastServer�����ڼ���߳�!");
    }

    //------------------------------------------------------------------------------- private ����


    /**
     * ��ʼ������ֵ
     */
    private void init() {
//����server����
        MULTICAST_SERVER_NAME = this.createServerName();

        //��ʼ������ֵ: multicastAdress, multicastPort
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

        
        //��ʼ����������
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
     * �����������MulticastServerManagerʱ��ʼ��������
     */
    private String createServerName() {
        String name = "";
        for (int i = 0; i < 10; i++) {
            name += (int) (10 * Math.random());
        }
        return name;
    }

    /**
     * ������������
     *
     * @return MulticastSocket ��Ч�ļ�������
     */
    private MulticastSocket createMulticastSocket() {
        MulticastSocket socket = null;

        try {
            socket = new MulticastSocket(multicastPort);

            InetAddress ia = InetAddress.getByName(multicastAdress);
            socket.joinGroup(ia);

        } catch (UnknownHostException e) {
            log.error("�����쳣!!, ����������������ʧ��", e);
            exit(-1);
        } catch (IOException e) {
            log.error("�����쳣!!, ����������������ʧ��", e);
            exit(-1);
        }

        return socket;
    }

    /**
     * �����������쳣, ϵͳ���˳�����
     *
     * @param code �˳��쳣����
     */
    private void exit(int code) {
        System.exit(code);
    }

    /**
     * ���Ĵ���
     *
     * @param buffer
     * @param buffer
     */
    private void packetHandler(byte[] buffer, int len) {
        //�ӻ�������ȡ����
        byte[] data = new byte[len];
        System.arraycopy(buffer, 0, data, 0, len);
        PacketUnit pu = new PacketUnit();

        //��������
        try {
            pu.parseData(data);

        } catch (BaseCheckedException e) {
            log.error("�������ݽ���ʧ��!", e);
            return;
        }

//��鱨����Դ,�����Դ�ڵ�ǰJVM, ���账��,ֱ�ӷ���
        if (MULTICAST_SERVER_NAME.equals(pu.getServerName())) {
            return;
        }

        //����������ĳ���
        String groupid = pu.getGroupid();
        int count = pu.getCount();
        if (count == 1) {
            //�����鳤��Ϊ1, �������ڴ���ʱû�б����, ֱ�Ӵ�����
            dataHandler(pu);
        } else if (count > 1) {
            //�����鳤�ȴ���1, �������ڴ���ʱû�б����, ��Ҫ������ǰ�ñ������Ƿ��������,
            //ֻ���ڽ��������������, �Ž������ݴ���, ��������ȴ�����ֱ������������ʱ
            PacketGroupInfo pgi = (PacketGroupInfo) this.pool.get(groupid);
            if (pgi == null) {
                pgi = new PacketGroupInfo();
                this.pool.put(groupid, pgi);
            }

            pgi.addPacketUnit(pu);

            if (pgi.getCount() == count) {
                //�Ѿ����յ��˸ñ������ȫ����������,���Դ�����!
                List<PacketUnit> list = pgi.getList();
                dataHandler(list);//����������
                this.pool.remove(groupid);//�ӻ�����ɾ������ı�����Ϣ
            } else {
                //������δ�������, ���Ѿ����յ�������ʱ��������,�����ݽ�����Ϻ��ٴ���
                ;
            }
        }
    }

    /**
     * �౨�����ݴ���
     *
     * @param al �����б�
     */
    private void dataHandler(List<PacketUnit> al) {
        //���մ���ʱ����������־����
        Collections.sort(al, new PacketUnitSortComparator());

        //������������
        String adapterClassName = "";
        byte dataType = 0;

        //�������ݳ���
        int totallen = 0;
        for (int i = 0; i < al.size(); i++) {
            PacketUnit pu = (PacketUnit) al.get(i);
            totallen += pu.getData().length;
            adapterClassName = pu.getServerAdapter();
            dataType = pu.getDataType();
        }

        //�ϲ���������
        byte[] data = new byte[totallen];
        int pos = 0;
        for (int i = 0; i < al.size(); i++) {
            PacketUnit pu = (PacketUnit) al.get(i);
            byte[] tmp = pu.getData();
            System.arraycopy(tmp, 0, data, pos, tmp.length);
            pos += tmp.length;
        }

        log.debug("Server����������: className = " + adapterClassName);
        log.debug("���ձ���: �������ܳ���: list.size() = " + al.size());
        log.debug("���ձ���: ���������ݳ���: totallen = " + totallen);

        dataProcess(data, adapterClassName, dataType);
    }

    /**
     * ���������ݴ���
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
     * ��������������������
     *
     * @param data
     * @param adapterClassName
     */
    private void dataProcess(byte[] data, String adapterClassName, byte dataType) {

        try {

            Object obj = null;
            if (dataType == 1) { //��������
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
            log.error("����Server��������ʱ�����쳣!", e);
        }
    }


    //------------------------------------------------------------------------------------ �ڲ���



    /**
     * �ڲ���, ���ڼ������Ĺ㲥
     */
    class MulticastServer extends Thread {
        /**
         * ����Thread����,ʵ�������ݱ��ĵĽ��պʹ���
         */
        public void run() {
            //����������socket����
            MulticastSocket socket = createMulticastSocket();

            //���ݽ��ջ�����
            byte[] buffer = new byte[packetLen + PacketUnit.HEADER_LENGTH];
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);

            while (true) {
                try {
                    socket.receive(dp); //�������ݱ���

                    //ȡ����
                    byte[] buff = dp.getData();
                    int len = dp.getLength();
                    //�������ݱ���
                    packetHandler(buff, len);

                    //��ػ��汨�ĵĳ�ʱ��������߳�, ������̲߳�����, ���ٴ���һ���µ��߳�
                    if (!cycThread.isAlive()) {
                        LifeCyc lifeCyc = new LifeCyc();
                        cycThread = lifeCyc;
                        lifeCyc.start();
                        log.info("���汨�ĵĳ�ʱ��������߳�ʧЧ, ���´������߳�! ");
                    }
                } catch (IOException e) {
                    //���MulticastSocket�����Ѿ��ر�,���ٴ���һ���µļ�������
                    if (socket.isClosed()) {
                        socket = createMulticastSocket();
                    }
                    log.error("���չ㲥����ʱ�����쳣!", e);
                }
            }
        }
    }

    /**
     * �ڲ���, ���ڿ��Ʊ��ĳ��л��汨�ĵĳ�ʱ����.
     * ������Ĵ���ʱ�䳬���趨ʱ��, ������ñ�����Ϣ
     */
    class LifeCyc extends Thread {
        public void run() {
            long currTime = System.currentTimeMillis();
            long timeoutMillis = timeout * 1000;

            try {
                while (true) {
                    //��黺�������ݱ����Ƿ�ʱ
                    synchronized (pool) {
                        Iterator<String> iter = pool.keySet().iterator();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            PacketGroupInfo pgi = (PacketGroupInfo) pool.get(key);
                            if (currTime - pgi.getCreateTime() > timeoutMillis) {
                                pool.remove(key);
                                log.info("MulticastServerɾ����ʱ�Ļ��汨����Ϣ! key = "
                                        + key);
                            }
                        }
                    }

                    //����ʱ��
                    sleep(timeout / 2 * 1000);

                    //���server�߳�,������̲߳�����,���ٴ���һ���µ��߳�
                    if (!multiServerThread.isAlive()) {
                        MulticastServer server = new MulticastServer();
                        multiServerThread = server;
                        server.start();
                        log.info("���server�߳�ʧЧ, ���´������server�߳�! ");
                    }
                }
            } catch (InterruptedException e) {
                log.error("MulticastServer��ʱ���Ĺ����߳������쳣!", e);
            }
        }
    }

}




