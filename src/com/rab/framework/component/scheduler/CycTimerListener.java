package com.rab.framework.component.scheduler;

/**
 * 
 * <P>Title: CycTimerListener</P>
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
public abstract class CycTimerListener extends BaseTimerListener {
    /**
     * 周期
     * 描述 定间隔时间周期性任务 的执行周期
     */
    private int periods = 0;

    /**
     * 监听模式
     * 描述 不定间隔时间周期性任务 的执行时间点
     */
    private ListenerModel model = null;


    public ListenerModel getModel() {
        return model;
    }

    public void setModel(ListenerModel model) {
        this.model = model;
    }

    public int getPeriods() {
        return periods;
    }

    public void setPeriods(int periods) {
        this.periods = periods;
    }
}
