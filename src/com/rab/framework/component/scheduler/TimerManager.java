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
 * <P>程序说明：</P>
 * <P>
 * 时钟信号管理器
 * 
 * 和时间相关的任务分三种:
 * 1. 定间隔时间(以本组件的时钟脉冲为单位,默认为每秒一个时钟脉冲)周期性任务:
 *    处理方法: 对每个任务添对象添加一个时间计数器, 每接收到一个时钟脉冲,该计数器加1
 *            当达到指定的时钟周期时, 触发该任务
 * 
 * 2. 不定间隔时间周期性任务
 *    处理方法: 检查当前时间是否同定时时间重合,重合则触发该任务
 *       前提: 定时最小间隔要远大于时钟脉冲(本模块中,时钟脉冲为1秒, 定时间隔最小为60秒)
 * 
 * 3. 一次性定时任务 
 *    处理方法: 动态注册,任务完成后删除

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
	 * 日志记录对象
	 */
	public static final int HOLIDAY = 2;
    public static final int SUCCESS = 1;
    public static final int FAIL = 0;
    public static final int RUNNING = 3;
    
    private final static LogWritter logger = LogFactory.getLogger(TimerManager.class);

	private String confFile = "timer.xml";
	
    private static TimerManager timerManager = null;

    /**
     * 监听者描述对象(ListenerObject)列表
     */
    private List<ListenerObject> listeners = new ArrayList<ListenerObject>();

    /**
     * 当前已经启动的线程池
     */
    private Map<String,Thread> runningThreadPool = new HashMap<String,Thread>();

    /**
     * 构造器
     */
    private TimerManager() {
    	init();
    }

    private void init(){
    	TimerRegisterConfLoader loader = new TimerRegisterConfLoader();
    	
    	this.listeners = loader.loadConfig(confFile);
    }
    
    /**
     * Singletone 方法, 返回TimerManager的唯一实例
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
     * 动态注册异步调度任务
     */
    public void registerSchedulerTask(SchedulerTaskListener listener) throws SchedulerException {
        Calendar startTime = listener.getStartTime();
        if (startTime.before(Calendar.getInstance())) {
            logger.error("定时时间小于当前系统时间, 拒绝接受注册!");
            throw new SchedulerException("885");
        }
        ListenerObject lo = new ListenerObject();
        lo.setListener(listener);

        listeners.add(lo);
    }

    /**
     * 计时器时间到达触发事件
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
    	logger.info("手动启动定时任务, taskid = " + taskID);
    	
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
    	logger.info("手动启动定时任务 " + taskID + " 结束!!!!!!!!!!!!!!!!!!!!!");
        return state;

    }
    
    /**
     * 处理周期性定时任务
     *
     * @param lo
     * @param currTime
     */
    private void cycListenerProcess(ListenerObject lo, long currTime) {
        CycTimerListener cycListener = (CycTimerListener) lo.getListener();

        if (cycListener.getModel() == null) {
            //按周期
            int counter = lo.getCounter();
            counter++;
            lo.setCounter(counter);

            if (counter == cycListener.getPeriods()) {
                fireCycListenerTimeout(lo, currTime);
                lo.setCounter(0);
            }
        } else {
            //按模式定时
            ListenerModel model = cycListener.getModel();
            Calendar calendar = Calendar.getInstance();
            //系统当前时间
            GregorianCalendar curCalendar = calendarFormat(calendar);

            //比较当前时间同任务中标记的时间是否相等
            //如果相等, 则意味着当前时间的任务已经开始执行, 不必再次运行
            if (calendarCompare(lo.getCurCalendarTime(), curCalendar)) {
                return;
            }

            //检查是否到达一个新的任务执行时间点, 如果是, 则重新创建线程,执行新的任务
            if (checkTimeOut(model, curCalendar)) {
                lo.setCurCalendarTime(curCalendar);
                fireCycListenerTimeout(lo, currTime);
            }
        }
    }

    /**
     * 执行周期性事务
     *
     * @param lo
     * @param currTime
     */
    private int fireCycListenerTimeout(ListenerObject lo, long currTime) {
        Class<?> claz = lo.getListener().getClass();

        try {
        	//检查是否做了假日设置.
        	SchedulerHolidayPolicy holidayPolicy = lo.getHoliday();
        	if(holidayPolicy != null){
        		Calendar thisDate = Calendar.getInstance();
        		
        		//如果当前处于设定的假日,则不激活定时任务
        		if(holidayPolicy.isHoliday(thisDate)){
        			logger.debug("当前处于设定的假期,任务" + lo.getId() + "停止执行!");
        			return HOLIDAY;//2表示假期
        		}
        	}
        	
            //检查执行指定任务的线程是否处于运行态, 如果是,则直接返回,不激发新的运行线程
            Thread thread = (Thread) this.runningThreadPool.get(lo.getId());
            if (thread != null && thread.isAlive()) { //当前线程正在运行
                return RUNNING;//1running
            }

            //创建新的线程, 并将线程放在指定的线程池中
            BaseTimerListener listener = (BaseTimerListener) claz.newInstance();
            lo.initParams(listener, lo.getParams());
            
            listener.setCurrTime(currTime);
            this.runningThreadPool.put(lo.getId(), listener);

            //运行创建的线程,执行任务
            listener.setCurrTime(currTime);
            listener.start();
            return SUCCESS;//success

        } catch (Exception e) {
            String msg = "定时任务启动时失败! className = " + claz.getName();
            logger.error(msg, e);
            return FAIL;//fail
        }

    }

    /**
     * 处理单次执行的定时任务
     *
     * @param lo
     * @param currTime
     */
    private void schedulerListenerProcess(ListenerObject lo, long currTime) {

        SchedulerTaskListener listener = (SchedulerTaskListener) lo.getListener();

        //系统当前时间
        GregorianCalendar curCalendar = calendarFormat(Calendar.getInstance());

        //当前系统时间同定时时间一致, 程序开始执行
        if (calendarCompare(listener.getStartTime(), curCalendar)) {
            listener.setCurrTime(currTime);
            listener.start();
            //从任务列表中删除!
            this.listeners.remove(lo);
        }
    }


    /**
     * 比较两个日期对象的值是否相等
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
     * 检查是否达到定时时间
     *
     * @param model 定时模式
     * @return
     */
    private boolean checkTimeOut(ListenerModel model, GregorianCalendar curCalendar) {
        boolean flag = true;

        //1. 年
        if (flag && model.getYears().length != 0) {
            int thisYear = curCalendar.get(Calendar.YEAR);
            flag = compare(model.getYears(), thisYear);
        }

        //2. 月
        if (flag && model.getMonths().length != 0) {
            int thisMonth = curCalendar.get(Calendar.MONTH) + 1;
            flag = compare(model.getMonths(), thisMonth);
        }

        //3. 日
        if (flag && model.getDates().length != 0) {
            int thisDate = curCalendar.get(Calendar.DATE);
            flag = compare(model.getDates(), thisDate);
        }

        //4. 时
        if (flag && model.getHours().length != 0) {
            int thisHour = curCalendar.get(Calendar.HOUR_OF_DAY);
            flag = compare(model.getHours(), thisHour);
        }

        //5. 时
        if (flag && model.getMinutes().length != 0) {
            int thisMinute = curCalendar.get(Calendar.MINUTE);
            flag = compare(model.getMinutes(), thisMinute);
        }

        return flag;
    }

    /**
     * 时间比较
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
