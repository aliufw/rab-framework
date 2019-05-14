package com.rab.framework.component.scheduler;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.rab.framework.comm.util.DateUtils;
import com.rab.framework.component.console.CommandProcessor;

/**
 * 
 * <P>Title: SchedulerManagerCommandProcessor</P>
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
public class SchedulerManagerCommandProcessor extends CommandProcessor{
	
	/**
	 * 命令列表:
	 * tasklist: 当前加载的定时任务列表
	 * taskinfo: 显示指定任务的详细信息
	 * taskstart: 启动指定的任务
	 */
	private String[] commands = {
			"tasklist",      
			"taskinfo",
			"taskstart"
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
			if (commands[0].equalsIgnoreCase(cmd[0])) { //tasklist
				tasklist();
			} 
			else if (commands[1].equalsIgnoreCase(cmd[0])) { //taskinfo
				taskinfo(cmd);
			} 
			else if (commands[2].equalsIgnoreCase(cmd[0])) { //taskstart
				taskstart(cmd);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示当前加载的任务列表
	 * @throws Exception
	 */
	private void tasklist() throws Exception{
		//显示列: 序号, id, 运行状态(on/off) 
		TimerManager tm = TimerManager.Singleton();
		List<ListenerObject> listeners = tm.getListeners();
		Map<String,Thread> runningThreadPool = tm.getRunningThreadPool();
		
		StringBuffer sb = new StringBuffer();
		sb.append("定时任务信息: \r\n");
		StringBuffer head = new StringBuffer();
		head.append(formatString("序号",5))
				.append(formatString("任务名称",30))
				.append(formatString("任务状态",10))
				.append(formatString("任务类",30))
				.append("\r\n");
		sb.append(repeat("-", len(head.toString()))).append("\r\n");
		sb.append(head);
		sb.append(repeat("-", len(head.toString()))).append("\r\n");
		
		int count = 1;
		for(int i=0; i<listeners.size(); i++){
			ListenerObject lo = listeners.get(i);
			BaseTimerListener listener = lo.getListener();
			String id = lo.getId();
			String className = listener.getClass().getName();
			
			String state = "WAITING";
			Thread thread = (Thread) runningThreadPool.get(lo.getId());
            if (thread != null && thread.isAlive()) { //当前线程正在运行
            	state = "RUNNING";
            }
			
			sb.append(formatString("" + count++, 5))
				.append(formatString(id, 30))
				.append(formatString(state, 10))
				.append(formatString(className, className.length()))
				.append("\r\n");			
		}
		
		this.socketOut.write(sb.toString().getBytes());
	}
	
	/**
	 * 显示指定名称的定时任务信息
	 * @param cmd
	 * @throws Exception
	 */
	private void taskinfo(String[] cmd) throws Exception{
		StringBuffer sb = new StringBuffer();
		
		if(cmd.length != 2){
			this.socketOut.write("命令格式不正确,请输入正确的定时任务名称!\r\n".getBytes());
			return;
		}

		String argTaskId = cmd[1];
		TimerManager tm = TimerManager.Singleton();
		List<ListenerObject> listeners = tm.getListeners();
		ListenerObject lo = null;
		for(int i=0; i<listeners.size(); i++){
			ListenerObject tmp = (ListenerObject) listeners.get(i);
			if(tmp.getId().equals(argTaskId)){
				lo = tmp;
				break;
			}
		}
		
		if(lo == null){
			this.socketOut.write("定时任务名称不正确,请输入正确的定时任务名称!\r\n".getBytes());
			return;
		}
		
		BaseTimerListener listener = lo.getListener();
		if (listener instanceof CycTimerListener) {
            StringBuffer info = cycListenerProcess(lo);
            sb.append(info);
        } else if (listener instanceof SchedulerTaskListener) {
            schedulerListenerProcess(lo);
        }
		
		sb.append("\r\n");
		
		this.socketOut.write(sb.toString().getBytes());
	}
	
	/**
	 * 
	 * @param lo
	 * @return
	 */
	private StringBuffer cycListenerProcess(ListenerObject lo){
		StringBuffer sb = new StringBuffer();
		
		CycTimerListener cycListener = (CycTimerListener) lo.getListener();
		if (cycListener.getModel() == null) {
			//定长周期
			sb.append("\r\n");
			sb.append("定时任务名称: " + lo.getId() + "\r\n");
			sb.append("定时任务类型: 定长周期\r\n");
			sb.append("    运行周期: " + cycListener.getPeriods() + " 秒钟\r\n");
			sb.append("定时任务程序: " + cycListener.getClass().getName() + "\r\n");
		}
		else{
			//不定长周期
			sb.append("\r\n");
			sb.append("定时任务名称: " + lo.getId() + "\r\n");
			sb.append("定时任务类型: 不定长周期\r\n");
			sb.append("运行模式: " + cycListener.getModel().toString() + "\r\n");
		}
		
		if(lo.getHoliday() != null){
			sb.append("\r\n");
			sb.append("定时任务假日\r\n");
			sb.append("----------------------------------------------------\r\n");
			sb.append(lo.getHoliday().toString());
		}
		
		return sb;
	}
	
	/**
	 * 单次执行任务
	 * 
	 * @param lo
	 * @return
	 */
	private StringBuffer schedulerListenerProcess(ListenerObject lo){
		StringBuffer sb = new StringBuffer();
		SchedulerTaskListener listener = (SchedulerTaskListener) lo.getListener();

		sb.append("\r\n");
		sb.append("定时任务名称: " + lo.getId() + "\r\n");
		sb.append("定时任务类型: 单次执行任务\r\n");
		sb.append("任务开始时间: " + DateUtils.toDateTimeStr(listener.getStartTime()) + "\r\n");
		sb.append("定时任务程序: " + listener.getClass().getName() + "\r\n");
		return sb;
	}
	
	/**
	 * 启动指定的任务
	 * 
	 * @param cmd
	 * @throws Exception
	 */
	private void taskstart(String[] cmd) throws Exception{
		if(cmd.length != 2){
			this.socketOut.write("命令格式不正确,请输入正确的定时任务名称!\r\n".getBytes());
			return;
		}

		String argTaskId = cmd[1];
		TimerManager tm = TimerManager.Singleton();
		List<ListenerObject> listeners = tm.getListeners();
		ListenerObject lo = null;
		for(int i=0; i<listeners.size(); i++){
			ListenerObject tmp = (ListenerObject) listeners.get(i);
			if(tmp.getId().equals(argTaskId)){
				lo = tmp;
				break;
			}
		}
		
		if(lo == null){
			this.socketOut.write("定时任务名称不正确,请输入正确的定时任务名称!\r\n".getBytes());
			return;
		}

		//启动定时任务
		tm.startTask(argTaskId);
		
		this.socketOut.write(("\r\n手工启动定时任务 " + argTaskId + " !!!\r\n\r\n").getBytes());
	}
}
