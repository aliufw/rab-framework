package com.rab.framework.component.scheduler;

/**
 * 
 * <P>Title: CycTimerListener</P>
 * <P>Description: </P>
 * <P>����˵����</P>
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
     * ����
     * ���� �����ʱ������������ ��ִ������
     */
    private int periods = 0;

    /**
     * ����ģʽ
     * ���� �������ʱ������������ ��ִ��ʱ���
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
