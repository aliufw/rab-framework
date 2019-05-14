package com.rab.framework.component.scheduler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;

/**
 * 
 * <P>Title: TimerRegisterConfLoader</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>监听器注册表加载类</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public class TimerRegisterConfLoader {
	/**
	 * 日志记录对象
	 */
	private final static LogWritter logger = LogFactory.getLogger(TimerRegisterConfLoader.class);
	
	private Map<String,SchedulerHolidayPolicy> holidayPolicys = new HashMap<String,SchedulerHolidayPolicy>();
	
	public ArrayList<ListenerObject> loadConfig(String confFileName) {
        ArrayList<ListenerObject> listeners = new ArrayList<ListenerObject>();
        try {
            SAXBuilder builder = new SAXBuilder();
            InputStream in = getClass().getResourceAsStream("/" + confFileName);
            Document doc = builder.build(in);
            Element root = doc.getRootElement();

            //1. 解析休息日定义
            List<?> listHoliday = root.getChildren("holiday");
            for (int i = 0; i < listHoliday.size(); i++) {
            	Element holiday = (Element) listHoliday.get(i);
                String id = holiday.getAttributeValue("id");
                SchedulerHolidayPolicy sh = parseHoliday(holiday);
                
                this.holidayPolicys.put(id,sh);
            }
            
            
            //2. 解析定时任务
            List<?> listTimer = root.getChildren("timer");

            for (int i = 0; i < listTimer.size(); i++) {
                Element timer = (Element) listTimer.get(i);
                String id = timer.getAttributeValue("id");
                String holiday = timer.getAttributeValue("holiday");
                String periods = timer.getAttributeValue("periods");
                String model = timer.getAttributeValue("model");
                String task = timer.getAttributeValue("task");

                try {
                	String className = task;
                	int pos = task.indexOf("?");
                	Map<String, String> params = new HashMap<String, String>();
                	if(pos > 0){
                		className = task.substring(0, pos);
                		String strParams = task.substring(pos+1);
                		StringTokenizer st = new StringTokenizer(strParams, ",");
                		while(st.hasMoreElements()){
                			String param = "" + st.nextElement();
                			if(param.indexOf("=") > 0){
                				String key = param.substring(0, param.indexOf("=")).trim();
                				String value = param.substring(param.indexOf("=")+1).trim();
                				params.put(key, value);
                			}
                		}
                	}
                	
                    CycTimerListener listener = (CycTimerListener) Class.forName(className).newInstance();
                    
                    if (model != null) {
                        ListenerModel listenerModel = createListenerModel(model);
                        this.checkModel(listenerModel);
                        listener.setModel(listenerModel);
                    } else {
                        listener.setPeriods(Integer.parseInt(periods));
                    }

                    ListenerObject lo = new ListenerObject();
                    lo.initParams(listener, params);
                    lo.setId(id);
                    lo.setListener(listener);
                    lo.setParams(params);
                    
                    //设置假日定义
                    if(holiday != null){
                    	SchedulerHolidayPolicy sh = (SchedulerHolidayPolicy)this.holidayPolicys.get(holiday);
                    	if(sh == null){
                    		logger.info("没有找到定时任务 " + id + " 对应的假日设置 " + holiday);
                    	}
                    	else{
                    		lo.setHoliday(sh);
                    	}
                    }
                    
                    listeners.add(lo);
                    logger.debug("定时任务监听器注册: periods=" + periods + ", task=" + task);

                } catch (RuntimeException e1) {
                    logger.error("解析定时任务配置文件时出现异常:", e1);
                    continue; //跳过该定时器监听者的加载过程
                }
            }
        } catch (Exception e) {
            logger.error("定时任务注册监听器配置文件解析错误!", e);
        }

        //将监听者传给时钟管理器
//        TimerManager.Singleton().setListeners(listeners);

        return listeners;
    }


	
    /**
     * 检查监听模式的数据格式是否正确!
     *
     * @throws RuntimeException
     */
    private void checkModel(ListenerModel model) throws RuntimeException {
        boolean[] isAsterisk = {
            model.getYears().length == 0 ? true : false,
            model.getMonths().length == 0 ? true : false,
            model.getDates().length == 0 ? true : false,
            model.getHours().length == 0 ? true : false,
            model.getMinutes().length == 0 ? true : false,
        };

        //1. 最后一位(分钟)必须要有准确值
        if (isAsterisk[4]) {
            throw new RuntimeException("定时模式不正确!");
        }

        //2. * 号前不能有准确值
        for (int i = 3; i >= 0; i--) {
            if (isAsterisk[i]) {
                for (int k = i - 1; k >= 0; k--) {
                    if (!isAsterisk[k]) {
                        throw new RuntimeException("定时模式不正确!");
                    }
                }
            }
        }
    }

    /**
     * 创建监听时间模式对象
     *
     * @return
     */
    private ListenerModel createListenerModel(String strModel) {
        ListenerModel model = new ListenerModel();

        StringTokenizer st = new StringTokenizer(strModel, " ");

        String year = st.nextToken();
        if (!year.equals("*")) {
            model.setYears(createModelData(year));
        }

        String month = st.nextToken();
        if (!month.equals("*")) {
            model.setMonths(createModelData(month));
        }

        String date = st.nextToken();
        if (!date.equals("*")) {
            model.setDates(createModelData(date));
        }

        String hour = st.nextToken();
        if (!hour.equals("*")) {
            model.setHours(createModelData(hour));
        }

        String minute = st.nextToken();
        if (!minute.equals("*")) {
            model.setMinutes(createModelData(minute));
        }

        return model;
    }

    /**
     * 创建模式数据
     *
     * @param strModelData
     * @return
     */
    private int[] createModelData(String strModelData) {
        ArrayList<String> al = new ArrayList<String>();

        StringTokenizer tmp = new StringTokenizer(strModelData, ",");
        while (tmp.hasMoreTokens()) {
            al.add(tmp.nextToken());
        }
        int[] data = new int[al.size()];

        for (int i = 0; i < data.length; i++) {
            data[i] = Integer.parseInt((String) al.get(i));
        }

        return data;
    }

    private SchedulerHolidayPolicy parseHoliday(Element holiday){
    	SchedulerHolidayPolicy shm = new SchedulerHolidayPolicy();
    	
    	List<?> listdate = holiday.getChildren("date"); 
    	for(int i=0; i<listdate.size(); i++){
    		SchedulerHoliday sh = new SchedulerHoliday();
    		
    		Element date = (Element)listdate.get(i);
    		String from = date.getAttributeValue("from");
    		
    		if(from != null){ //时间段
        		String to = date.getAttributeValue("to");

        		StringTokenizer stfrom = new StringTokenizer(from);
        		String syear0 = (String)stfrom.nextElement();
        		String smonth0 = (String)stfrom.nextElement();
        		String sday0 = (String)stfrom.nextElement();
        		SchedulerCalendar sc0 = new SchedulerCalendar();
        		if(!syear0.equalsIgnoreCase("*")){
        			int year0 = Integer.parseInt(syear0);
        			sc0.setYear(year0);
        		}
        		if(!smonth0.equalsIgnoreCase("*")){
        			int month0 = Integer.parseInt(smonth0);
        			sc0.setMonth(month0-1);
        		}
        		if(!sday0.equalsIgnoreCase("*")){
        			int day0 = Integer.parseInt(sday0);
        			sc0.setDay(day0);
        		}

        		StringTokenizer stto = new StringTokenizer(to);
        		String syear1 = (String)stto.nextElement();
        		String smonth1 = (String)stto.nextElement();
        		String sday1 = (String)stto.nextElement();
        		SchedulerCalendar sc1 = new SchedulerCalendar();
        		if(syear1 != null && !syear1.equalsIgnoreCase("*")){
        			int year1 = Integer.parseInt(syear1);
        			sc1.setYear(year1);
        		}
        		
        		if(smonth1 != null && !smonth1.equalsIgnoreCase("*")){
        			int month1 = Integer.parseInt(smonth1);
        			sc1.setMonth(month1-1);
        		}
        		
        		if(sday1 != null && !sday1.equalsIgnoreCase("*")){
        			int day1 = Integer.parseInt(sday1);
        			sc1.setDay(day1);
        		}
        		
        		sh.setModel(SchedulerHoliday.SCHEDULER_CALENDAR_DATE);
        		sh.setDate0(sc0);
        		sh.setDate1(sc1);
    		}
    		else{ //单日期时间
    			String singleday = date.getAttributeValue("singleday");
        		StringTokenizer stsingleday = new StringTokenizer(singleday);
        		String syear = (String)stsingleday.nextElement();
        		String smonth = (String)stsingleday.nextElement();
        		String sday = (String)stsingleday.nextElement();
        		SchedulerCalendar sc = new SchedulerCalendar();
        		if(syear != null && !syear.equalsIgnoreCase("*")){
        			int year1 = Integer.parseInt(syear);
        			sc.setYear(year1);
        		}
        		
        		if(smonth != null && !smonth.equalsIgnoreCase("*")){
        			int month1 = Integer.parseInt(smonth);
        			sc.setMonth(month1-1);
        		}
        		
        		if(sday != null && !sday.equalsIgnoreCase("*")){
        			int day = Integer.parseInt(sday);
        			sc.setDay(day);
        		}
        		
        		sh.setModel(SchedulerHoliday.SCHEDULER_CALENDAR_DATE);
        		sh.setDate0(sc);
        		sh.setDate1(null);

    		}

    		shm.addSchedulerHoliday(sh);
    	}
    	
    	List<?> listweek = holiday.getChildren("week"); 
    	for(int i=0; i<listweek.size(); i++){
    		SchedulerHoliday sh = new SchedulerHoliday();
    		Element week = (Element)listweek.get(i);
    		String sfrom = week.getAttributeValue("from");
    		
    		if(sfrom != null){
    			String sto = week.getAttributeValue("to");
    			int from = Integer.parseInt(sfrom);
        		int to = Integer.parseInt(sto);
        		SchedulerCalendar sc0 = new SchedulerCalendar();
        		SchedulerCalendar sc1 = new SchedulerCalendar();
        		sc0.setWeek(from);
        		sc1.setWeek(to);
        		
        		sh.setModel(SchedulerHoliday.SCHEDULER_CALENDAR_WEEK);
        		sh.setDate0(sc0);
        		sh.setDate1(sc1);
    		}
    		else{
    			String singleday = week.getAttributeValue("singleday");
    			int day = Integer.parseInt(singleday);
    			SchedulerCalendar sc = new SchedulerCalendar();
    			sc.setWeek(day);
    			sh.setModel(SchedulerHoliday.SCHEDULER_CALENDAR_WEEK);
    			sh.setDate0(sc);
        		sh.setDate1(null);
    		}
    		
    		shm.addSchedulerHoliday(sh);
    	}
    	
    	List<?> listwork = holiday.getChildren("work"); 
    	for(int i=0; i<listwork.size(); i++){
    		SchedulerHoliday sh = new SchedulerHoliday();
    		Element work = (Element)listwork.get(i);
    		
			String singleday = work.getAttributeValue("singleday");
    		StringTokenizer stsingleday = new StringTokenizer(singleday);
    		String syear = (String)stsingleday.nextElement();
    		String smonth = (String)stsingleday.nextElement();
    		String sday = (String)stsingleday.nextElement();
    		SchedulerCalendar sc = new SchedulerCalendar();
    		if(syear != null && !syear.equalsIgnoreCase("*")){
    			int year1 = Integer.parseInt(syear);
    			sc.setYear(year1);
    		}
    		
    		if(smonth != null && !smonth.equalsIgnoreCase("*")){
    			int month1 = Integer.parseInt(smonth);
    			sc.setMonth(month1-1);
    		}
    		
    		if(sday != null && !sday.equalsIgnoreCase("*")){
    			int day = Integer.parseInt(sday);
    			sc.setDay(day);
    		}
    		
    		sh.setModel(SchedulerHoliday.SCHEDULER_WORK_DAY);
    		sh.setDate0(sc);
    		sh.setDate1(null);

    		shm.addSchedulerHoliday(sh);
    	}  	
    	return shm;
    }
}
