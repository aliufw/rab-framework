package com.rab.framework.component.scheduler;

/**
 * 
 * <P>Title: SchedulerHoliday</P>
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
public class SchedulerHoliday {
	/**
	 * 按照日期定义的假期
	 */
	public static int SCHEDULER_CALENDAR_DATE = 0;

	/**
	 * 按照星期定义的假期
	 */
	public static int SCHEDULER_CALENDAR_WEEK = 1;

	/**
	 * 在上述定义的假期描述中,某些日期为工作日,即非假期
	 */
	public static int SCHEDULER_WORK_DAY = 2;
	
	/**
	 * 假期开始时间, 如果date1=null,则就是指单日期时间点,即单日期假日
	 */
	SchedulerCalendar date0;

	/**
	 * 假期结束时间
	 */
	SchedulerCalendar date1;

	/**
	 * 定义模式, 日期或星期
	 */
	int model;

	
	public SchedulerCalendar getDate0() {
		return date0;
	}

	public void setDate0(SchedulerCalendar date0) {
		this.date0 = date0;
	}

	public SchedulerCalendar getDate1() {
		return date1;
	}

	public void setDate1(SchedulerCalendar date1) {
		this.date1 = date1;
	}

	public int getModel() {
		return model;
	}

	public void setModel(int model) {
		this.model = model;
	}
	
	private String formatdate(int data){
		if(data < -0){
			return "*";
		}
		else{
			return "" + data;
		}
	}
	
	public String toString(){
		String tostr = "";
		tostr += "假日定义: ";
		if(this.model ==SCHEDULER_CALENDAR_DATE){
			
			tostr += "类型: SCHEDULER_CALENDAR_DATE";
			if(date1 == null){
				tostr += " 单日假期: " + formatdate(date0.getYear()) + "-" + formatdate(date0.getMonth()+1) + "-" + formatdate(date0.getDay());
			}
			else{
				tostr += " 多日假期: from " + formatdate(date0.getYear()) + "-" + formatdate(date0.getMonth()+1) + "-" + formatdate(date0.getDay());
				tostr += " to " + formatdate(date1.getYear()) + "-" + formatdate(date1.getMonth()+1) + "-" + formatdate(date1.getDay());
			}
		}
		else if(this.model == SCHEDULER_CALENDAR_WEEK){
			tostr += "类型: SCHEDULER_CALENDAR_WEEK";
			if(date1 == null){
				tostr += " 单日假期: 周" + date0.getWeek();
			}
			else{
				tostr += " 多日假期: from 周" + date0.getWeek() + " to 周" + date1.getWeek();
			}
		}
		else if(this.model == SCHEDULER_WORK_DAY){
			tostr += "类型: SCHEDULER_WORK_DAY";
			tostr += " 假日排除: " + formatdate(date0.getYear()) + "-" + formatdate(date0.getMonth()+1) + "-" + formatdate(date0.getDay());
		}
		
		return tostr;
	}
}
