package com.rab.framework.component.scheduler;

import java.util.EventListener;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;


/**
 * 
 * <P>Title: BaseTimerListener</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P>�������ӿ�</P>
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
     * ��ʱ����ǰʱ������
     */
    protected long currTime;


    /**
     * ����ʱʱ�䵽���, ϵͳ����һ���µ��߳�����ɶ�ʱ�����ִ��
     */
    public void run() {
        try {
            execute();
        } catch (SchedulerException e) {
            String msg = "����ע����¼�ʱ��������!";
            msg += "className= " + this.getClass().getName();
            logger.error(msg, e);
        }
    }

    /**
     * ��ʱ����������
     *
     * @param currTime ��ǰ��ʱ��ʱ��
     * @throws SchedulerException �����쳣,�׳�CSSTimerException�쳣����
     */
    public abstract void execute() throws SchedulerException;

    protected void setCurrTime(long currTime) {
        this.currTime = currTime;
    }
}
