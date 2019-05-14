package com.rab.framework.component.scheduler;

import java.util.Properties;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;

/**
 * 
 * <P>Title: TimerClock</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P>ʱ�ӳ���</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public class TimerClock {
	
	private final static LogWritter logger = LogFactory.getLogger(TimerClock.class);
	
    /**
     * ��ʱ��λ: ��
     */
    private int unit = 10;

    /**
     * ʱ�������
     */
    private long timerCounter = 0L;

    /**
     * ��ʱ���߳�
     */
    private Thread timerThread = null;

    /**
     * ����߳�
     */
    private Thread monitorThread = null;

    /**
     * ������
     */
    public TimerClock() {

    }

    /**
     * ����������
     */
    public void start() {
        Timer timer = new Timer();
        this.timerThread = timer;
//        this.timerThread.setDaemon(true);
        timer.start();

        Monitor monitor = new Monitor();
        this.monitorThread = monitor;
        this.monitorThread.setDaemon(true);
        monitor.start();
    }


    /**
     * ��ʱ��ʱ�䵽, ����ʱ���¼�
     */
    private void timeout() {
        timerCounter++;
        TimerManager.Singleton().timeout(this.timerCounter);
    }

    /**
     * �����������
     *
     * @param argv
     */
    public static void main(String[] argv) {
        Properties prop = (Properties)ApplicationContext.singleton().getValueByKey("scheduler");
    	if(prop != null){
            String state = prop.getProperty("state");
        	if (state != null && state.equalsIgnoreCase("on")) {
                TimerClock timer = new TimerClock();
                timer.start();
            }
    	}

    }

    /**
     * �ڲ���, ��ʱ��ʱ��
     */
    class Timer extends Thread {
        public void run() {
            while (true) {
                try {
                    timeout();
                    sleep(unit * 1000);

                    if (!monitorThread.isAlive()) {
                        Monitor monitor = new Monitor();
                        monitorThread = monitor;
                        monitor.start();
                    }
                } catch (InterruptedException e) {
                    logger.error("��ʱ��ʱ���̳߳����쳣!", e);
                }
            }
        }
    }

    /**
     * �ڲ���, ����߳�
     */
    class Monitor extends Thread {
        public void run() {
            while (true) {
                try {
                    sleep(unit * 10 * 1000);
                    if (!timerThread.isAlive()) {
                        Timer timer = new Timer();
                        timerThread = timer;
                        timer.start();
                    }
                } catch (InterruptedException e) {
                    logger.error("����̳߳����쳣!", e);
                }
            }
        }
    }
}
