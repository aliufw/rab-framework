package com.rab.framework.component.scheduler;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * <P>Title: ListenerObject</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>监听者对象包装类</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */

public class ListenerObject {


    /**
     * 监听者对象标记
     */
    private String id;

    /**
     * 计数器
     */
    private int counter = 0;

    /**
     * 监听者
     */
    private BaseTimerListener listener = null;

    /**
     * 当前任务预设开始时间
     * 当时钟时间打到本属性设置的时间后, 系统激活执行任务
     */
    private Calendar curCalendarTime;

    /**
     * 假日设置, 在假日里不运行设定的周期性定时任务
     */
    private SchedulerHolidayPolicy holiday = null;
    
    private Map<String, String> params = new HashMap<String, String>();
    
	public void initParams(BaseTimerListener listener, Map<String, String> params) throws Exception{
		Iterator<String> iter = params.keySet().iterator();
		Method[] methods = listener.getClass().getMethods();
		while(iter.hasNext()){
			String key = iter.next();
			String value = params.get(key);
			
			String setter = "set" + key.substring(0,1).toUpperCase() + key.substring(1);
			Method method = null;
			for(int i=0; i<methods.length; i++){
				if(methods[i].getName().equals(setter)){
					method = methods[i];
					break;
				}
			}
			if(method == null){
				throw new Exception("任务类的参数名称 " + key + " 定义错误，没有在任务类中找到对应的属性参数定义！");
			}
			
			Type[] pt = method.getParameterTypes();
			String type = "" + pt[0];
			if(type.startsWith("class ")){
				type = type.substring("class ".length());
			}
			
			Object[] paramValue = new Object[1];
			if(type.equals("java.lang.String")){
				paramValue[0] = value;
			}
			else if(type.equals("java.lang.Boolean") || type.equalsIgnoreCase("boolean")){
				if(value.equalsIgnoreCase("false")){
					paramValue[0] = new Boolean(false); 
				}
				else if(value.equalsIgnoreCase("true")){
					paramValue[0] = new Boolean(true); 
				}
				else{
					throw new Exception("任务类的参数 " + key + " 的值赋值错误，应为true或false");
				}
			}
			else if(type.equals("java.lang.Byte") || type.equalsIgnoreCase("byte")){
				paramValue[0] = Byte.parseByte(value);
			}
			else if(type.equals("java.lang.Integer") || type.equalsIgnoreCase("int")){
				paramValue[0] = Integer.parseInt(value);
			}
			else if(type.equals("java.lang.Long") || type.equalsIgnoreCase("long")){
				paramValue[0] = Long.parseLong(value);
			}
			else if(type.equals("java.lang.Double") || type.equalsIgnoreCase("double")){
				paramValue[0] = Double.parseDouble(value);
			}
			else if(type.equals("java.lang.Float")|| type.equalsIgnoreCase("float")){
				paramValue[0] = Float.parseFloat(value);
			}
			else{
				throw new Exception("任务类的参数的数据类型有误，不支持的数据类型！type = " + type);
			}

			method.invoke(listener, paramValue);
		}
	}    
    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public BaseTimerListener getListener() {
        return listener;
    }

    public void setListener(BaseTimerListener listener) {
        this.listener = listener;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Calendar getCurCalendarTime() {
        return curCalendarTime;
    }

    public void setCurCalendarTime(Calendar curCalendarTime) {
        this.curCalendarTime = curCalendarTime;
    }

	public SchedulerHolidayPolicy getHoliday() {
		return holiday;
	}

	public void setHoliday(SchedulerHolidayPolicy holiday) {
		this.holiday = holiday;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}
    
    
}