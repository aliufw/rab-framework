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
 * <P>����˵����</P>
 * <P>���ڴ����ߺ�����,�������ڶ��������ַ������ת������</P>
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
     * ���峣����ʱ���ʽ
     */
    private static String[] dateFormat =
    {
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss.SSS",
        "yyyy-MM-dd",
        "yyyy��MM��dd��HHʱmm��ss��",   
        "yyyy��MM��dd��",
        "yyyy-MM-dd'T'HH:mm:ss" //����ƥ��Ext decode�����Զ��� T �ĸ�ʽ by Zhangbin since 2010-8-25
    };

	public static final String[] dateRegex = {
		"[1|2][0-9][0-9]{2}-[0|1][0-9]-[0-3][0-9] [0-2][0-9]:[0-5][0-9]:[0-5][0-9]",
		"[1|2][0-9][0-9]{2}-[0|1][0-9]-[0-3][0-9] [0-2][0-9]:[0-5][0-9]:[0-5][0-9].[0-9]{1,4}",
		"[1|2][0-9][0-9]{2}-[0|1][0-9]-[0-3][0-9]",
		"[1|2][0-9][0-9]{2}��[0|1][0-9]��[0-3][0-9]�� [0-2][0-9]ʱ[0-5][0-9]��[0-5][0-9]��",
		"[1|2][0-9][0-9]{2}��[0|1][0-9]��[0-3][0-9]��",
		"[1|2][0-9][0-9]{2}-[0|1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]"};
 
    /**
     * �����ڸ�ʽ�� java.util.Calendar ת�� java.sql.Timestamp ��ʽ
     * @param date java.util.Calendar ��ʽ��ʾ������
     * @return     java.sql.Timestamp ��ʽ��ʾ������
     */
    public   static java.sql.Timestamp convUtilCalendarToSqlTimestamp(java.util.Calendar date){
        if(date == null)
            return null;
        else
            return new java.sql.Timestamp(date.getTimeInMillis());
    }

    /**
     * �����ڸ�ʽ�� java.util.Timestamp ת�� java.util.Calendar ��ʽ
     * @param date java.sql.Timestamp ��ʽ��ʾ������
     * @return     java.util.Calendar ��ʽ��ʾ������
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
     * ����һ���ַ������γ�һ��Calendar������Ӧ���ֲ�ͬ�����ڱ�ʾ��
     * @param dateStr �����������ַ�����ע�⣬���ܴ�null��ȥ���������
     * @return ���ؽ������Calendar����
     * <br>
     * <br>������������ִ���ʽ���£�
     * <br>"yyyy-MM-dd HH:mm:ss",
     * <br>"yyyy/MM/dd HH:mm:ss",
     * <br>"yyyy��MM��dd��HHʱmm��ss��",
     * <br>"yyyy-MM-dd",
     * <br>"yyyy/MM/dd",
     * <br>"yy-MM-dd",
     * <br>"yy/MM/dd",
     * <br>"yyyy��MM��dd��",
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
     * ��һ������ת������ʱ���ʽ����ʽ����  2002-08-05 21:25:21
     * @param date  ������ʽ�������ڶ���
     * @return ���ظ�ʽ������ַ���
     * <br>
     * <br>����
     * <br>���ã�
     * <br>Calendar date = new GregorianCalendar();
     * <br>String ret = DateUtils.toDateTimeStr(date);
     * <br>���أ�
     * <br> ret = "2002-12-04 09:13:16";
     */
    public  static String toDateTimeStr(Calendar date){
        if(date == null)
            return null;
        return new SimpleDateFormat(dateFormat[0]).format(date.getTime());
    }

    /**
     * ��һ������ת�����ڸ�ʽ����ʽ����  2002-08-05
     * @param date  ������ʽ�������ڶ���
     * @return ���ظ�ʽ������ַ���
     * <br>
     * <br>����
     * <br>���ã�
     * <br>Calendar date = new GregorianCalendar();
     * <br>String ret = DateUtils.toDateStr(calendar);
     * <br>���أ�
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
     * ����������㣬�����������ڼ������
     * ����������Ϊnull���򷵻���ԶΪ0
     *
     * @param d1 ��ֹ���ڣ���������
     * @param d2 ��ʼ���ڣ�������
     * @return  �������ڼ���������������Ϊ0
     *
     *<br>
     * ����
     *  <br>GregorianCalendar gc = new GregorianCalendar();
     *  <br>GregorianCalendar gc2 = (GregorianCalendar)gc.clone();
     *  <br>gc2.add(gc2.DATE,20);
     ��
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
     * �ڲ�����������ĳ�������е����ڸ�ʽ��������
     * @param dateStr �����������ַ���
     * @param index ���ڸ�ʽ������
     * @return ���ؽ������
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
