package com.rab.framework.comm.cachesession;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.security.Ticket;
import com.rab.framework.component.console.CommandProcessor;

/**
 * 
 * <P>Title: CacheSessionManagerCommandProcessor</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>登录信息管理命令处理程序</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public class CacheSessionManagerCommandProcessor extends CommandProcessor{

	private final static LogWritter logger = LogFactory.getLogger(CacheSessionManagerCommandProcessor.class);
	
	private String[] commands = {
			"syslist",
			"syslogininfo",
			"sysclear"
			};
	
	
	public void cmdProcess(String cmdLine) {
		cmdLine = cmdLine.trim();
		cmdLine = cmdLine.replaceAll("\t", " ");
		String[] cmd = new String[0];

		StringTokenizer st = new StringTokenizer(cmdLine, " ");
		while (st.hasMoreElements()) {
			String param = (String) st.nextElement();
			String[] tmp = new String[cmd.length + 1];
			System.arraycopy(cmd, 0, tmp, 0, cmd.length);
			tmp[cmd.length] = param;
			cmd = tmp;
		}

		try {
			if (commands[0].equalsIgnoreCase(cmd[0])) { //syslist
				syslist();
			} 
			else if (commands[1].equalsIgnoreCase(cmd[0])) { //syslogininfo
				syslogininfo(cmd);
			} 
			else if (commands[2].equalsIgnoreCase(cmd[0])) { //sysclear
				sysclear();
			} 
						
		} catch (Exception e) {
			logger.error("执行命令出现异常!", e);
		}

	}

	/**
	 * 显示当前登录人信息列表
	 *
	 */
	private void syslist() throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("登录人信息列表: \r\n");
		StringBuffer head = new StringBuffer();
		head.append(format("序号",5))
				.append(format("sessionid",30))
				.append(format("用户编号",15))
				.append(format("用户名", 15))
				.append(format("登录时间",20))
				.append("\r\n");
		sb.append(repeat("-", len(head.toString()))).append("\r\n");
		sb.append(head);
		sb.append(repeat("-", len(head.toString()))).append("\r\n");
		
		List<CacheSession> list = new ArrayList<CacheSession>();
		CacheSessionManagerImpl csm = (CacheSessionManagerImpl)CacheSessionManagerImpl.singleton();
		Map<String,CacheSession> map = csm.getCacheSessionPool();
		Iterator<CacheSession> iter = map.values().iterator();
		while(iter.hasNext()){
			CacheSession cs = (CacheSession)iter.next();
			String sessionid = cs.getUsersessionid();
			Ticket ticket = (Ticket)cs.getValue(sessionid);
			if(ticket == null){
				continue;
			}
			
			list.add(cs);
		}
		
		Collections.sort(list, new SessionInfoSortComparator());
		
		int index = 1;
		for(int i=0; i<list.size(); i++){
			CacheSession cs = (CacheSession)list.get(i);
			String sessionid = cs.getUsersessionid();
			Ticket ticket = (Ticket)cs.getValue(sessionid);
			if(ticket == null){
				continue;
			}

			String usercode = ticket.getUser().getUsercode();
			String username = ticket.getUser().getUsername();
			long lngLogontime = ticket.getLogonTime();
			Calendar logontime = Calendar.getInstance();
			logontime.setTimeInMillis(lngLogontime);
			
			sb.append(format("" + index++, 5))
			.append(format(sessionid, 30))
			.append(format(usercode, 15))
			.append(format(username, 15))
			.append(format(timeFormat(logontime) ,20))
			.append("\r\n");			
		}
		
		this.socketOut.write(sb.toString().getBytes());
	}
	
	/**
	 * 显示指定用户名(userid)的登录信息
	 * @param cmd
	 * @throws Exception
	 */
	private void syslogininfo(String[] cmd) throws Exception{
		StringBuffer sb = new StringBuffer();
		
		if(cmd.length != 2){
			this.socketOut.write("命令格式不正确,请输入用户编号!\r\n".getBytes());
			return;
		}
		
		String argUsercode = cmd[1];
//		System.out.println("argUserid = " + argUserid);
		sb.append("用户: " + argUsercode + " 登录信息列表: \r\n");
		StringBuffer head = new StringBuffer();
		head.append(format("序号",5))
				.append(format("sessionid",30))
				.append(format("用户编号",15))
				.append(format("用户名", 15))
				.append(format("登录时间",20))
				.append("\r\n");
		sb.append(repeat("-", len(head.toString()))).append("\r\n");
		sb.append(head);
		sb.append(repeat("-", len(head.toString()))).append("\r\n");
		
		List<CacheSession> list = new ArrayList<CacheSession>();
		CacheSessionManagerImpl csm = (CacheSessionManagerImpl)CacheSessionManagerImpl.singleton();
		Map<String,CacheSession> map = csm.getCacheSessionPool();
		Iterator<CacheSession> iter = map.values().iterator();
		while(iter.hasNext()){
			CacheSession cs = iter.next();
			String sessionid = cs.getUsersessionid();
			Ticket ticket = (Ticket)cs.getValue(sessionid);
			if(ticket == null || !ticket.getUser().getUsercode().equals(argUsercode)){
				continue;
			}
//			System.out.println("userid = " + ticket.getUser().getUserID());
			
			list.add(cs);
		}
		
		Collections.sort(list, new SessionInfoSortComparator());
		
		int index = 1;
		for(int i=0; i<list.size(); i++){
			CacheSession cs = (CacheSession)list.get(i);
			String sessionid = cs.getUsersessionid();
			Ticket ticket = (Ticket)cs.getValue(sessionid);
			if(ticket == null){
				continue;
			}
			String usercode = ticket.getUser().getUsercode();
			String username = ticket.getUser().getUsername();
			long lngLogontime = ticket.getLogonTime();
			Calendar logontime = Calendar.getInstance();
			logontime.setTimeInMillis(lngLogontime);
			
			sb.append(format("" + index++, 5))
			.append(format(sessionid, 30))
			.append(format(usercode, 15))
			.append(format(username, 15))
			.append(format(timeFormat(logontime) ,20))
			.append("\r\n");			
		}
		
		this.socketOut.write(sb.toString().getBytes());
	}
	
	/**
	 * 清除登录状态信息
	 * @throws Exception
	 */
	private void sysclear() throws Exception{
        Calendar currCalendar = Calendar.getInstance();
        CacheSessionManagerImpl manager = (CacheSessionManagerImpl) CacheSessionManagerImpl.singleton();
        manager.cacheSessionTimeout(currCalendar);
        
        StringBuffer sb = new StringBuffer();
        sb.append("成功清除用户登录信息! \r\n");
        this.socketOut.write(sb.toString().getBytes());
	}

	/**
	 * 时间格式话
	 * @param gc Calendar
	 * @return   yyyy-mm-dd hh24:mm:ss
	 */
    private String timeFormat(Calendar gc){
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
	
	private String format(String msg, int length){
		return " " + this.formatString(msg, length);
	}
	
	class SessionInfoSortComparator implements Comparator<Object> {
	    private final int ASC = 1;
	    private final int DSC = -1;
	    public SessionInfoSortComparator() {
	    }

	    public int compare(Object o1, Object o2) {
	        if(o1 == null || o2 == null){
	            return this.ASC;
	        }

	        CacheSession cs1 = (CacheSession)o1;
	        CacheSession cs2 = (CacheSession)o2;
	        
	        String sessionid1 = cs1.getUsersessionid();
	        String sessionid2 = cs2.getUsersessionid();
	        
	        Ticket ticket1 = (Ticket)cs1.getValue(sessionid1);
	        Ticket ticket2 = (Ticket)cs2.getValue(sessionid2);
	        
	        long time1 = ticket1.getLogonTime();
	        long time2 = ticket2.getLogonTime();
	        
	        if(time1 > time2){
	            return this.ASC;
	        }
	        else{
	            return this.DSC;
	        }
	    }
	
}

}