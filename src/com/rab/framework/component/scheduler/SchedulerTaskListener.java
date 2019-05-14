package com.rab.framework.component.scheduler;

import java.util.Calendar;

/**
 * 
 * <P>Title: SchedulerTaskListener</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */

public abstract class SchedulerTaskListener extends BaseTimerListener {
    /**
     * 任务开始时间
     */
    private Calendar startTime;


    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }
}
