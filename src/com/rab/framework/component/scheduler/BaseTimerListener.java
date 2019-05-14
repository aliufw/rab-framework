package com.rab.framework.component.scheduler;

import java.util.EventListener;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;


/**
 * 
 * <P>Title: BaseTimerListener</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>监听器接口</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public abstract class BaseTimerListener extends Thread implements EventListener {
	
	private final static LogWritter logger = LogFactory.getLogger(BaseTimerListener.class);
	
    /**
     * 定时器当前时间属性
     */
    protected long currTime;


    /**
     * 当定时时间到达后, 系统启动一个新的线程来完成定时任务的执行
     */
    public void run() {
        try {
            execute();
        } catch (SchedulerException e) {
            String msg = "触发注册的事件时发生错误!";
            msg += "className= " + this.getClass().getName();
            logger.error(msg, e);
        }
    }

    /**
     * 计时器触发方法
     *
     * @param currTime 当前计时器时间
     * @throws SchedulerException 出现异常,抛出CSSTimerException异常对象
     */
    public abstract void execute() throws SchedulerException;

    protected void setCurrTime(long currTime) {
        this.currTime = currTime;
    }
}
