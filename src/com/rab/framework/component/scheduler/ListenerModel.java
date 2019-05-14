package com.rab.framework.component.scheduler;

/**
 * 
 * <P>Title: ListenerModel</P>
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
public class ListenerModel {
    /**
     * 年
     */
    private int[] years = new int[0];

    /**
     * 月
     */
    private int[] months = new int[0];

    /**
     * 日
     */
    private int[] dates = new int[0];

    /**
     * 时
     */
    private int[] hours = new int[0];

    /**
     * 分
     */
    private int[] minutes = new int[0];

    public int[] getDates() {
        return dates;
    }

    public void setDates(int[] dates) {
        this.dates = dates;
    }

    public int[] getHours() {
        return hours;
    }

    public void setHours(int[] hours) {
        this.hours = hours;
    }

    public int[] getMinutes() {
        return minutes;
    }

    public void setMinutes(int[] minutes) {
        this.minutes = minutes;
    }

    public int[] getMonths() {
        return months;
    }

    public void setMonths(int[] months) {
        this.months = months;
    }

    public int[] getYears() {
        return years;
    }

    public void setYears(int[] years) {
        this.years = years;
    }
    
    private StringBuffer dataconv(int[] data){
    	StringBuffer sb = new StringBuffer();
    	if(data.length == 0){
    		sb.append("*");
    	}
    	else{
    		for(int i=0; i<data.length-1; i++){
    			sb.append("" + data[i] + ",");
    		}
    		sb.append(data[data.length-1]);
    	}
    	
    	return sb;
    }
    
    public String toString(){
    	StringBuffer sb = new StringBuffer();
    	
    	sb.append(" " + dataconv(this.years));
    	sb.append(" " + dataconv(this.months));
    	sb.append(" " + dataconv(this.dates));
    	sb.append(" " + dataconv(this.hours));
    	sb.append(" " + dataconv(this.minutes));
    	
    	return sb.toString();
    }
}