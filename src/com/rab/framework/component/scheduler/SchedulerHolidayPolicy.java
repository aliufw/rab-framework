package com.rab.framework.component.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 
 * <P>Title: SchedulerHolidayPolicy</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>标记休息日信息</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */

public class SchedulerHolidayPolicy {
	/**
	 * 休息日列表
	 */
	private List<SchedulerHoliday> date = new ArrayList<SchedulerHoliday>();

	
	/**
	 * 添加休息日
	 * @param calendar
	 */
	public void addSchedulerHoliday(SchedulerHoliday date){
		this.date.add(date);
	}
	
	/**
	 * 删除休息日
	 * 
	 * @param calendar
	 */
	public void removeSchedulerHoliday(SchedulerHoliday date){
		this.date.remove(date);
	}

	/**
	 * 判断指定日期是否休息日
	 * 
	 * @param calendar
	 * @return
	 */
	public boolean isHoliday(Calendar date){
		boolean flag = false;
		for(int i=0; i<this.date.size(); i++){
			SchedulerHoliday sh = (SchedulerHoliday)this.date.get(i);
			
			boolean isHoliday = checkHoliday(sh, date);
			
			if(isHoliday){
				if(!checkWorkday(date)){ //不是假期,是工作日
					flag = true;
				}
				else{
					flag = false;
				}
				break;
			}
		}
		
		return flag;
	}

	private boolean checkWorkday(Calendar date){
		for(int i=0; i<this.date.size(); i++){
			SchedulerHoliday sh = (SchedulerHoliday)this.date.get(i);
			if(sh.getModel() == SchedulerHoliday.SCHEDULER_WORK_DAY){
				Calendar cc = (Calendar)date.clone();
				Calendar c0 = (Calendar)cc.clone();
				
				cc.clear();
				c0.clear();

				SchedulerCalendar sc = sh.getDate0();
				if(sc.getYear() != -1){
					c0.set(Calendar.YEAR, sc.getYear());
					cc.set(Calendar.YEAR, date.get(Calendar.YEAR));
				}
				if(sc.getMonth() != -1){
					c0.set(Calendar.MONTH, sc.getMonth());
					cc.set(Calendar.MONTH, date.get(Calendar.MONTH));
				}
				if(sc.getDay() != -1){
					c0.set(Calendar.DATE, sc.getDay());
					cc.set(Calendar.DATE, date.get(Calendar.DATE));
				}
				
				if(cc.equals(c0)){
					return true;
				}
			}
		}
			
		return false;
	}

	private boolean checkHoliday(SchedulerHoliday schedulerHoliday, Calendar date){
		if(schedulerHoliday.getModel() == SchedulerHoliday.SCHEDULER_CALENDAR_WEEK){
			if(schedulerHoliday.getDate1() == null){
				if(schedulerHoliday.getDate0().getWeek() == date.get(Calendar.DAY_OF_WEEK)){
					return true;
				}
			}
			else{
				int week0 = schedulerHoliday.getDate0().getWeek();
				int week1 = schedulerHoliday.getDate1().getWeek();
				if(date.get(Calendar.DAY_OF_WEEK) >= week0 
						&& date.get(Calendar.DAY_OF_WEEK) <=week1){
					return true;
				}
			}
		}
		else if(schedulerHoliday.getModel() == SchedulerHoliday.SCHEDULER_CALENDAR_DATE){
			if(schedulerHoliday.getDate1() == null){
				Calendar cc = (Calendar)date.clone();
				Calendar c0 = (Calendar)cc.clone();
				
				cc.clear();
				c0.clear();

				SchedulerCalendar sc = schedulerHoliday.getDate0();
				if(sc.getYear() != -1){
					c0.set(Calendar.YEAR, sc.getYear());
					cc.set(Calendar.YEAR, date.get(Calendar.YEAR));
				}
				if(sc.getMonth() != -1){
					c0.set(Calendar.MONTH, sc.getMonth());
					cc.set(Calendar.MONTH, date.get(Calendar.MONTH));
				}
				if(sc.getDay() != -1){
					c0.set(Calendar.DATE, sc.getDay());
					cc.set(Calendar.DATE, date.get(Calendar.DATE));
				}
				
				if(cc.equals(c0)){
					return true;
				}
			}
			else{
				Calendar cc = (Calendar)date.clone();
				Calendar c0 = (Calendar)cc.clone();
				Calendar c1 = (Calendar)cc.clone();
				
				cc.clear();
				c0.clear();
				c1.clear();

				SchedulerCalendar sc0 = schedulerHoliday.getDate0();
				SchedulerCalendar sc1 = schedulerHoliday.getDate1();
				
				if(sc0.getYear() != -1){
					c0.set(Calendar.YEAR, sc0.getYear());
					c1.set(Calendar.YEAR, sc1.getYear());
					cc.set(Calendar.YEAR, date.get(Calendar.YEAR));
				}
				if(sc0.getMonth() != -1){
					c0.set(Calendar.MONTH, sc0.getMonth());
					c1.set(Calendar.MONTH, sc1.getMonth());
					cc.set(Calendar.MONTH, date.get(Calendar.MONTH));
				}
				if(sc0.getDay() != -1){
					c0.set(Calendar.DATE, sc0.getDay());
					c1.set(Calendar.DATE, sc1.getDay());
					cc.set(Calendar.DATE, date.get(Calendar.DATE));
				}
				
				if((cc.after(c0) && cc.before(c1)) || (cc.equals(c0) || (cc.equals(c1)))){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		for(int i=0; i<this.date.size(); i++){
			sb.append("" + (i+1));
			sb.append(" " + this.date.get(i));
			sb.append("\r\n");
		}
		
		return sb.toString();
	}
}
