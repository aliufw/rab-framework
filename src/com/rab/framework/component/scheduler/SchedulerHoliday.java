package com.rab.framework.component.scheduler;

/**
 * 
 * <P>Title: SchedulerHoliday</P>
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
public class SchedulerHoliday {
	/**
	 * �������ڶ���ļ���
	 */
	public static int SCHEDULER_CALENDAR_DATE = 0;

	/**
	 * �������ڶ���ļ���
	 */
	public static int SCHEDULER_CALENDAR_WEEK = 1;

	/**
	 * ����������ļ���������,ĳЩ����Ϊ������,���Ǽ���
	 */
	public static int SCHEDULER_WORK_DAY = 2;
	
	/**
	 * ���ڿ�ʼʱ��, ���date1=null,�����ָ������ʱ���,�������ڼ���
	 */
	SchedulerCalendar date0;

	/**
	 * ���ڽ���ʱ��
	 */
	SchedulerCalendar date1;

	/**
	 * ����ģʽ, ���ڻ�����
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
		tostr += "���ն���: ";
		if(this.model ==SCHEDULER_CALENDAR_DATE){
			
			tostr += "����: SCHEDULER_CALENDAR_DATE";
			if(date1 == null){
				tostr += " ���ռ���: " + formatdate(date0.getYear()) + "-" + formatdate(date0.getMonth()+1) + "-" + formatdate(date0.getDay());
			}
			else{
				tostr += " ���ռ���: from " + formatdate(date0.getYear()) + "-" + formatdate(date0.getMonth()+1) + "-" + formatdate(date0.getDay());
				tostr += " to " + formatdate(date1.getYear()) + "-" + formatdate(date1.getMonth()+1) + "-" + formatdate(date1.getDay());
			}
		}
		else if(this.model == SCHEDULER_CALENDAR_WEEK){
			tostr += "����: SCHEDULER_CALENDAR_WEEK";
			if(date1 == null){
				tostr += " ���ռ���: ��" + date0.getWeek();
			}
			else{
				tostr += " ���ռ���: from ��" + date0.getWeek() + " to ��" + date1.getWeek();
			}
		}
		else if(this.model == SCHEDULER_WORK_DAY){
			tostr += "����: SCHEDULER_WORK_DAY";
			tostr += " �����ų�: " + formatdate(date0.getYear()) + "-" + formatdate(date0.getMonth()+1) + "-" + formatdate(date0.getDay());
		}
		
		return tostr;
	}
}
