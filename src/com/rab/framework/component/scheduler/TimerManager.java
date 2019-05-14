package com.rab.framework.component.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;

/**
 * 
 * <P>Title: TimerManager</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P>
 * ʱ���źŹ�����
 * 
 * ��ʱ����ص����������:
 * 1. �����ʱ��(�Ա������ʱ������Ϊ��λ,Ĭ��Ϊÿ��һ��ʱ������)����������:
 *    ������: ��ÿ��������������һ��ʱ�������, ÿ���յ�һ��ʱ������,�ü�������1
 *            ���ﵽָ����ʱ������ʱ, ����������
 * 
 * 2. �������ʱ������������
 *    ������: ��鵱ǰʱ���Ƿ�ͬ��ʱʱ���غ�,�غ��򴥷�������
 *       ǰ��: ��ʱ��С���ҪԶ����ʱ������(��ģ����,ʱ������Ϊ1��, ��ʱ�����СΪ60��)
 * 
 * 3. һ���Զ�ʱ���� 
 *    ������: ��̬ע��,������ɺ�ɾ��

 * </P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public class TimerManager {
	/**
	 * ��־��¼����
	 */
	public static final int HOLIDAY = 2;
    public static final int SUCCESS = 1;
    public static final int FAIL = 0;
    public static final int RUNNING = 3;
    
    private final static LogWritter logger = LogFactory.getLogger(TimerManager.class);

	private String confFile = "timer.xml";
	
    private static TimerManager timerManager = null;

    /**
     * ��������������(ListenerObject)�б�
     */
    private List<ListenerObject> listeners = new ArrayList<ListenerObject>();

    /**
     * ��ǰ�Ѿ��������̳߳�
     */
    private Map<String,Thread> runningThreadPool = new HashMap<String,Thread>();

    /**
     * ������
     */
    private TimerManager() {
    	init();
    }

    private void init(){
    	TimerRegisterConfLoader loader = new TimerRegisterConfLoader();
    	
    	this.listeners = loader.loadConfig(confFile);
    }
    
    /**
     * Singletone ����, ����TimerManager��Ψһʵ��
     *
     * @return
     */
    public static TimerManager Singleton() {
        if (timerManager == null) {
            timerManager = new TimerManager();
        }

        return timerManager;
    }

    /**
     * ��̬ע���첽��������
     */
    public void registerSchedulerTask(SchedulerTaskListener listener) throws SchedulerException {
        Calendar startTime = listener.getStartTime();
        if (startTime.before(Calendar.getInstance())) {
            logger.error("��ʱʱ��С�ڵ�ǰϵͳʱ��, �ܾ�����ע��!");
            throw new SchedulerException("885");
        }
        ListenerObject lo = new ListenerObject();
        lo.setListener(listener);

        listeners.add(lo);
    }

    /**
     * ��ʱ��ʱ�䵽�ﴥ���¼�
     */
    protected void timeout(long currTime) {
        for (int i = 0; i < this.listeners.size(); i++) {
            ListenerObject lo = (ListenerObject) this.listeners.get(i);

            BaseTimerListener listener = lo.getListener();

            if (listener instanceof CycTimerListener) {
                cycListenerProcess(lo, currTime);
            } else if (listener instanceof SchedulerTaskListener) {
                this.schedulerListenerProcess(lo,currTime);
            }
        }
    }

    public int startTask(String taskID){
    	logger.info("�ֶ�������ʱ����, taskid = " + taskID);
    	
    	ListenerObject listenerObj = null;
    	for (int i = 0; i < this.listeners.size(); i++) {
            ListenerObject lo = (ListenerObject) this.listeners.get(i);

            
            if(taskID.equals(lo.getId())){
            	listenerObj = lo;
            	break;
            }
    	}
    	
    	BaseTimerListener listener = listenerObj.getListener();
    	int state = 1;
        if (listener instanceof CycTimerListener ||
    			listener instanceof SchedulerTaskListener) {
    		state = fireCycListenerTimeout(listenerObj, -1l);//what's meaning of 11
        } 
    	logger.info("�ֶ�������ʱ���� " + taskID + " ����!!!!!!!!!!!!!!!!!!!!!");
        return state;

    }
    
    /**
     * ���������Զ�ʱ����
     *
     * @param lo
     * @param currTime
     */
    private void cycListenerProcess(ListenerObject lo, long currTime) {
        CycTimerListener cycListener = (CycTimerListener) lo.getListener();

        if (cycListener.getModel() == null) {
            //������
            int counter = lo.getCounter();
            counter++;
            lo.setCounter(counter);

            if (counter == cycListener.getPeriods()) {
                fireCycListenerTimeout(lo, currTime);
                lo.setCounter(0);
            }
        } else {
            //��ģʽ��ʱ
            ListenerModel model = cycListener.getModel();
            Calendar calendar = Calendar.getInstance();
            //ϵͳ��ǰʱ��
            GregorianCalendar curCalendar = calendarFormat(calendar);

            //�Ƚϵ�ǰʱ��ͬ�����б�ǵ�ʱ���Ƿ����
            //������, ����ζ�ŵ�ǰʱ��������Ѿ���ʼִ��, �����ٴ�����
            if (calendarCompare(lo.getCurCalendarTime(), curCalendar)) {
                return;
            }

            //����Ƿ񵽴�һ���µ�����ִ��ʱ���, �����, �����´����߳�,ִ���µ�����
            if (checkTimeOut(model, curCalendar)) {
                lo.setCurCalendarTime(curCalendar);
                fireCycListenerTimeout(lo, currTime);
            }
        }
    }

    /**
     * ִ������������
     *
     * @param lo
     * @param currTime
     */
    private int fireCycListenerTimeout(ListenerObject lo, long currTime) {
        Class<?> claz = lo.getListener().getClass();

        try {
        	//����Ƿ����˼�������.
        	SchedulerHolidayPolicy holidayPolicy = lo.getHoliday();
        	if(holidayPolicy != null){
        		Calendar thisDate = Calendar.getInstance();
        		
        		//�����ǰ�����趨�ļ���,�򲻼��ʱ����
        		if(holidayPolicy.isHoliday(thisDate)){
        			logger.debug("��ǰ�����趨�ļ���,����" + lo.getId() + "ִֹͣ��!");
        			return HOLIDAY;//2��ʾ����
        		}
        	}
        	
            //���ִ��ָ��������߳��Ƿ�������̬, �����,��ֱ�ӷ���,�������µ������߳�
            Thread thread = (Thread) this.runningThreadPool.get(lo.getId());
            if (thread != null && thread.isAlive()) { //��ǰ�߳���������
                return RUNNING;//1running
            }

            //�����µ��߳�, �����̷߳���ָ�����̳߳���
            BaseTimerListener listener = (BaseTimerListener) claz.newInstance();
            lo.initParams(listener, lo.getParams());
            
            listener.setCurrTime(currTime);
            this.runningThreadPool.put(lo.getId(), listener);

            //���д������߳�,ִ������
            listener.setCurrTime(currTime);
            listener.start();
            return SUCCESS;//success

        } catch (Exception e) {
            String msg = "��ʱ��������ʱʧ��! className = " + claz.getName();
            logger.error(msg, e);
            return FAIL;//fail
        }

    }

    /**
     * ������ִ�еĶ�ʱ����
     *
     * @param lo
     * @param currTime
     */
    private void schedulerListenerProcess(ListenerObject lo, long currTime) {

        SchedulerTaskListener listener = (SchedulerTaskListener) lo.getListener();

        //ϵͳ��ǰʱ��
        GregorianCalendar curCalendar = calendarFormat(Calendar.getInstance());

        //��ǰϵͳʱ��ͬ��ʱʱ��һ��, ����ʼִ��
        if (calendarCompare(listener.getStartTime(), curCalendar)) {
            listener.setCurrTime(currTime);
            listener.start();
            //�������б���ɾ��!
            this.listeners.remove(lo);
        }
    }


    /**
     * �Ƚ��������ڶ����ֵ�Ƿ����
     *
     * @param d1
     * @param d2
     * @return
     */
    private boolean calendarCompare(Calendar d1, Calendar d2) {
        boolean flag = true;
        int[] model = {Calendar.YEAR,
                       Calendar.MONTH,
                       Calendar.DATE,
                       Calendar.HOUR_OF_DAY,
                       Calendar.MINUTE};

        if (d1 == null || d2 == null) {
            return false;
        }

        for (int i = 0; i < model.length; i++) {
            if (d1.get(model[i]) != d2.get(model[i])) {
                flag = false;
                break;
            }
        }

        return flag;
    }


    /**
     * ����Ƿ�ﵽ��ʱʱ��
     *
     * @param model ��ʱģʽ
     * @return
     */
    private boolean checkTimeOut(ListenerModel model, GregorianCalendar curCalendar) {
        boolean flag = true;

        //1. ��
        if (flag && model.getYears().length != 0) {
            int thisYear = curCalendar.get(Calendar.YEAR);
            flag = compare(model.getYears(), thisYear);
        }

        //2. ��
        if (flag && model.getMonths().length != 0) {
            int thisMonth = curCalendar.get(Calendar.MONTH) + 1;
            flag = compare(model.getMonths(), thisMonth);
        }

        //3. ��
        if (flag && model.getDates().length != 0) {
            int thisDate = curCalendar.get(Calendar.DATE);
            flag = compare(model.getDates(), thisDate);
        }

        //4. ʱ
        if (flag && model.getHours().length != 0) {
            int thisHour = curCalendar.get(Calendar.HOUR_OF_DAY);
            flag = compare(model.getHours(), thisHour);
        }

        //5. ʱ
        if (flag && model.getMinutes().length != 0) {
            int thisMinute = curCalendar.get(Calendar.MINUTE);
            flag = compare(model.getMinutes(), thisMinute);
        }

        return flag;
    }

    /**
     * ʱ��Ƚ�
     *
     * @param model
     * @param data
     * @return
     */
    private boolean compare(int[] model, int data) {
        boolean flag = false;

        for (int i = 0; i < model.length; i++) {
            if (data == model[i]) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    protected void setListeners(Vector<ListenerObject> listeners) {
        this.listeners = listeners;
    }

    private GregorianCalendar calendarFormat(Calendar calendar) {
        return new GregorianCalendar(
        		calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));
    }

	public List<ListenerObject> getListeners() {
		return listeners;
	}

	public Map<String,Thread> getRunningThreadPool() {
		return runningThreadPool;
	}
        
}
