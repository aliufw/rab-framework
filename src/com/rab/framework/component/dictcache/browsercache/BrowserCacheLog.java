package com.rab.framework.component.dictcache.browsercache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.GregorianCalendar;

/**
 * 
 * <P>Title: BrowserCacheLog</P>
 * <P>Description: </P>
 * <P>程序说明：日志记录器</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-16</P>
 *
 */
public class BrowserCacheLog {

	private PrintWriter log; 
	
	
	public BrowserCacheLog(String rootdir){
		File file = new File(rootdir,"cachemanager.log");
		try {
			FileOutputStream out = new FileOutputStream(file,true);
			log = new PrintWriter(out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void log(String msg){
		String time = this.getCurrentTime();
		System.out.println(time + " " +msg);
		log.println(time + " " +msg);
		log.flush();
	}
	
	public void log(String msg, Throwable e){
		String time = this.getCurrentTime();
		System.out.println(time + " " + msg + " " + e.getMessage());
		e.printStackTrace();
		
		log.println(time + " " + msg + " " + e.getMessage());
		e.printStackTrace(log);
		log.flush();
	}
	
	   /**-------------------------------------------------------------------------
     * 取当前时间
     */
    private String getCurrentTime(){
        GregorianCalendar gc = new GregorianCalendar();
        String retStr = "";
        retStr += gc.get(GregorianCalendar.YEAR);
        if(gc.get(GregorianCalendar.MONDAY)+1 < 10)
            retStr += "-0" + (gc.get(GregorianCalendar.MONDAY)+1);
        else
            retStr += "-" + (gc.get(GregorianCalendar.MONDAY)+1);

        if(gc.get(GregorianCalendar.DATE) < 10)
            retStr += "-0" + gc.get(GregorianCalendar.DATE);
        else
            retStr += "-" + gc.get(GregorianCalendar.DATE);

        int hour = gc.get(GregorianCalendar.HOUR);
        hour += 12 * gc.get(GregorianCalendar.AM_PM);
        if(hour < 10)
            retStr += " 0" + hour;
        else
            retStr += " " + hour;

        if(gc.get(GregorianCalendar.MINUTE) < 10)
            retStr += ":0" + gc.get(GregorianCalendar.MINUTE);
        else
            retStr += ":" + gc.get(GregorianCalendar.MINUTE);

        if(gc.get(GregorianCalendar.SECOND) < 10)
            retStr += ":0" + gc.get(GregorianCalendar.SECOND);
        else
            retStr += ":" + gc.get(GregorianCalendar.SECOND);

        return retStr;
    }
	
}

