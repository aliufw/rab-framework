package com.rab.framework.comm.cachesession;


import java.util.Calendar;

import com.rab.framework.component.scheduler.CycTimerListener;
import com.rab.framework.component.scheduler.SchedulerException;

/**
 * 
 * <P>Title: CacheSessionTimeoutListener</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>缓存超时处理</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public class CacheSessionTimeoutListener extends CycTimerListener {

    public void execute() throws SchedulerException {
        Calendar currCalendar = Calendar.getInstance();
        CacheSessionManagerImpl manager = (CacheSessionManagerImpl) CacheSessionManagerImpl.singleton();
        manager.cacheSessionTimeout(currCalendar);
    }

}
