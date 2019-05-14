package com.rab.framework.component.scheduler;

import java.util.Properties;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;

/**
 * 
 * <P>Title: TimerClock</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>时钟程序</P>
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
     * 计时单位: 秒
     */
    private int unit = 10;

    /**
     * 时间计数器
     */
    private long timerCounter = 0L;

    /**
     * 计时器线程
     */
    private Thread timerThread = null;

    /**
     * 监控线程
     */
    private Thread monitorThread = null;

    /**
     * 构造器
     */
    public TimerClock() {

    }

    /**
     * 启动计数器
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
     * 定时器时间到, 触发时间事件
     */
    private void timeout() {
        timerCounter++;
        TimerManager.Singleton().timeout(this.timerCounter);
    }

    /**
     * 程序启动入口
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
     * 内部类, 定时器时钟
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
                    logger.error("定时器时钟线程出现异常!", e);
                }
            }
        }
    }

    /**
     * 内部类, 监控线程
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
                    logger.error("监控线程出现异常!", e);
                }
            }
        }
    }
}
