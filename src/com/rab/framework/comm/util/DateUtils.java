package com.rab.framework.comm.util;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 
 * <P>Title: DateUtils</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>日期处理工具函数包,包括日期对象、日期字符串相关转换函数</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public class DateUtils
{
    /**
     * 定义常见的时间格式
     */
    private static String[] dateFormat =
    {
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss.SSS",
        "yyyy-MM-dd",
        "yyyy年MM月dd日HH时mm分ss秒",   
        "yyyy年MM月dd日",
        "yyyy-MM-dd'T'HH:mm:ss" //用于匹配Ext decode日期自动加 T 的格式 by Zhangbin since 2010-8-25
    };

	public static final String[] dateRegex = {
		"[1|2][0-9][0-9]{2}-[0|1][0-9]-[0-3][0-9] [0-2][0-9]:[0-5][0-9]:[0-5][0-9]",
		"[1|2][0-9][0-9]{2}-[0|1][0-9]-[0-3][0-9] [0-2][0-9]:[0-5][0-9]:[0-5][0-9].[0-9]{1,4}",
		"[1|2][0-9][0-9]{2}-[0|1][0-9]-[0-3][0-9]",
		"[1|2][0-9][0-9]{2}年[0|1][0-9]月[0-3][0-9]日 [0-2][0-9]时[0-5][0-9]分[0-5][0-9]秒",
		"[1|2][0-9][0-9]{2}年[0|1][0-9]月[0-3][0-9]日",
		"[1|2][0-9][0-9]{2}-[0|1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]"};
 
    /**
     * 将日期格式从 java.util.Calendar 转到 java.sql.Timestamp 格式
     * @param date java.util.Calendar 格式表示的日期
     * @return     java.sql.Timestamp 格式表示的日期
     */
    public   static java.sql.Timestamp convUtilCalendarToSqlTimestamp(java.util.Calendar date){
        if(date == null)
            return null;
        else
            return new java.sql.Timestamp(date.getTimeInMillis());
    }

    /**
     * 将日期格式从 java.util.Timestamp 转到 java.util.Calendar 格式
     * @param date java.sql.Timestamp 格式表示的日期
     * @return     java.util.Calendar 格式表示的日期
     */
    public   static Calendar convSqlTimestampToUtilCalendar(java.sql.Timestamp date){
        if(date == null)
            return null;
        else{
            java.util.GregorianCalendar gc = new java.util.GregorianCalendar();
            gc.setTimeInMillis(date.getTime());
            return gc;
        }
    }

    /**
     * 解析一个字符串，形成一个Calendar对象，适应各种不同的日期表示法
     * @param dateStr 期望解析的字符串，注意，不能传null进去，否则出错
     * @return 返回解析后的Calendar对象
     * <br>
     * <br>可输入的日期字串格式如下：
     * <br>"yyyy-MM-dd HH:mm:ss",
     * <br>"yyyy/MM/dd HH:mm:ss",
     * <br>"yyyy年MM月dd日HH时mm分ss秒",
     * <br>"yyyy-MM-dd",
     * <br>"yyyy/MM/dd",
     * <br>"yy-MM-dd",
     * <br>"yy/MM/dd",
     * <br>"yyyy年MM月dd日",
     * <br>"HH:mm:ss",
     * <br>"yyyyMMddHHmmss",
     * <br>"yyyyMMdd",
     * <br>"yyyy.MM.dd",
     * <br>"yy.MM.dd"
     */
    public  static Calendar parseDate(String dateStr){
        if(dateStr == null || dateStr.trim().length()==0)
            return null;

        Date result = parseDate(dateStr,0);
        Calendar cal = Calendar.getInstance();
        cal.setTime(result);

        return cal;
    }

    /**
     * 将一个日期转成日期时间格式，格式这样  2002-08-05 21:25:21
     * @param date  期望格式化的日期对象
     * @return 返回格式化后的字符串
     * <br>
     * <br>例：
     * <br>调用：
     * <br>Calendar date = new GregorianCalendar();
     * <br>String ret = DateUtils.toDateTimeStr(date);
     * <br>返回：
     * <br> ret = "2002-12-04 09:13:16";
     */
    public  static String toDateTimeStr(Calendar date){
        if(date == null)
            return null;
        return new SimpleDateFormat(dateFormat[0]).format(date.getTime());
    }

    /**
     * 将一个日期转成日期格式，格式这样  2002-08-05
     * @param date  期望格式化的日期对象
     * @return 返回格式化后的字符串
     * <br>
     * <br>例：
     * <br>调用：
     * <br>Calendar date = new GregorianCalendar();
     * <br>String ret = DateUtils.toDateStr(calendar);
     * <br>返回：
     * <br>ret = "2002-12-04";
     */
    public  static String toDateStr(Calendar date){
        if(date == null)
            return null;
        return new SimpleDateFormat(dateFormat[2]).format(date.getTime());
    }

    public  static String toDateStr(Date date ,int index){
        if(date == null)
            return null;
        return new SimpleDateFormat(dateFormat[index]).format(date);
    }

    /**
     * 日期相减运算，返回两个日期间隔天数
     * 如果输入参数为null；则返回永远为0
     *
     * @param d1 终止日期（被减数）
     * @param d2 起始日期（减数）
     * @return  两个日期间隔天数，不足天计为0
     *
     *<br>
     * 例：
     *  <br>GregorianCalendar gc = new GregorianCalendar();
     *  <br>GregorianCalendar gc2 = (GregorianCalendar)gc.clone();
     *  <br>gc2.add(gc2.DATE,20);
     ×
     *  <br>System.out.println(DateUtils.calendarMinus(gc2,gc));
     *
     *  <br>ret = 20
     */
//    public  static int calendarMinus(Calendar d1,Calendar d2){
//        int date = 0;
//        if(d1 == null || d2 == null){
//            return 0;
//        }
//
//        return (int)((d1.getTimeInMillis() - d2.getTimeInMillis())/(3600*24*1000));
//    }

//    public  static int calendarMinus(Calendar d1,Calendar d2){
//       int date = 0;
//       if(d1 == null || d2 == null){
//           return 0;
//       }
//       long t = d1.getTimeInMillis() - d2.getTimeInMillis();
//       long ret = (t / (3600*24*1000));
//       if(t % (3600*24*1000) > 0){
//           ret ++;
//       }
//       return (int)ret;
//        return (int)((d1.getTimeInMillis() - d2.getTimeInMillis())/(3600*24*1000));
//   }

   public  static int calendarMinus(Calendar d1,Calendar d2){
        if(d1 == null || d2 == null){
            return 0;
        }

        d1.set(Calendar.HOUR_OF_DAY,0);
        d1.set(Calendar.MINUTE,0);
        d1.set(Calendar.SECOND,0);

        d2.set(Calendar.HOUR_OF_DAY,0);
        d2.set(Calendar.MINUTE,0);
        d2.set(Calendar.SECOND,0);

        long t1 = d1.getTimeInMillis();
        long t2 = d2.getTimeInMillis();
           long daylong = 3600*24*1000;
        t1 = t1 - t1 % (daylong);
        t2 = t2 - t2 % (daylong);

        long t = t1 - t2;
        int value = (int)(t / (daylong));


        return value;
    }


    /**
     * 内部方法，根据某个索引中的日期格式解析日期
     * @param dateStr 期望解析的字符串
     * @param index 日期格式的索引
     * @return 返回解析结果
     */
    public  static Date parseDate(String dateStr,int index){
        DateFormat df = null;
        try{
            df = new SimpleDateFormat(dateFormat[index]);

            return df.parse(dateStr);
        }catch(ParseException pe){
            return parseDate(dateStr,index+1);
        }catch(ArrayIndexOutOfBoundsException aioe){
             return null;
        }
    }
	public static java.sql.Date parseToDate(String dateStr) throws ParseException {
		java.sql.Date date =null;
		for (int i = 0; i < dateRegex.length; i++) {
			if (dateStr.matches(dateRegex[i])) {
				SimpleDateFormat sdf = new SimpleDateFormat(dateFormat[i]);
				try {
					date = new java.sql.Date(sdf.parse(dateStr).getTime());
					break;
				} catch (ParseException e) {
					throw e;
				}
			}
		}
		return date;
	}
	
    public  static String toDateStr(java.sql.Date date){
        if(date == null)
            return null;
        return new SimpleDateFormat(dateFormat[2]).format(date);
    }

	public static java.sql.Timestamp parseToTimestamp(String dateStr) throws Exception{
		return new java.sql.Timestamp(parseToDate(dateStr).getTime());
	}
}
