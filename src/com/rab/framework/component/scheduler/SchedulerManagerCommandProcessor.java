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
 * <P>����˵����</P>
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
	 * �����б�:
	 * tasklist: ��ǰ���صĶ�ʱ�����б�
	 * taskinfo: ��ʾָ���������ϸ��Ϣ
	 * taskstart: ����ָ��������
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
	 * ��ʾ��ǰ���ص������б�
	 * @throws Exception
	 */
	private void tasklist() throws Exception{
		//��ʾ��: ���, id, ����״̬(on/off) 
		TimerManager tm = TimerManager.Singleton();
		List<ListenerObject> listeners = tm.getListeners();
		Map<String,Thread> runningThreadPool = tm.getRunningThreadPool();
		
		StringBuffer sb = new StringBuffer();
		sb.append("��ʱ������Ϣ: \r\n");
		StringBuffer head = new StringBuffer();
		head.append(formatString("���",5))
				.append(formatString("��������",30))
				.append(formatString("����״̬",10))
				.append(formatString("������",30))
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
            if (thread != null && thread.isAlive()) { //��ǰ�߳���������
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
	 * ��ʾָ�����ƵĶ�ʱ������Ϣ
	 * @param cmd
	 * @throws Exception
	 */
	private void taskinfo(String[] cmd) throws Exception{
		StringBuffer sb = new StringBuffer();
		
		if(cmd.length != 2){
			this.socketOut.write("�����ʽ����ȷ,��������ȷ�Ķ�ʱ��������!\r\n".getBytes());
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
			this.socketOut.write("��ʱ�������Ʋ���ȷ,��������ȷ�Ķ�ʱ��������!\r\n".getBytes());
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
			//��������
			sb.append("\r\n");
			sb.append("��ʱ��������: " + lo.getId() + "\r\n");
			sb.append("��ʱ��������: ��������\r\n");
			sb.append("    ��������: " + cycListener.getPeriods() + " ����\r\n");
			sb.append("��ʱ�������: " + cycListener.getClass().getName() + "\r\n");
		}
		else{
			//����������
			sb.append("\r\n");
			sb.append("��ʱ��������: " + lo.getId() + "\r\n");
			sb.append("��ʱ��������: ����������\r\n");
			sb.append("����ģʽ: " + cycListener.getModel().toString() + "\r\n");
		}
		
		if(lo.getHoliday() != null){
			sb.append("\r\n");
			sb.append("��ʱ�������\r\n");
			sb.append("----------------------------------------------------\r\n");
			sb.append(lo.getHoliday().toString());
		}
		
		return sb;
	}
	
	/**
	 * ����ִ������
	 * 
	 * @param lo
	 * @return
	 */
	private StringBuffer schedulerListenerProcess(ListenerObject lo){
		StringBuffer sb = new StringBuffer();
		SchedulerTaskListener listener = (SchedulerTaskListener) lo.getListener();

		sb.append("\r\n");
		sb.append("��ʱ��������: " + lo.getId() + "\r\n");
		sb.append("��ʱ��������: ����ִ������\r\n");
		sb.append("����ʼʱ��: " + DateUtils.toDateTimeStr(listener.getStartTime()) + "\r\n");
		sb.append("��ʱ�������: " + listener.getClass().getName() + "\r\n");
		return sb;
	}
	
	/**
	 * ����ָ��������
	 * 
	 * @param cmd
	 * @throws Exception
	 */
	private void taskstart(String[] cmd) throws Exception{
		if(cmd.length != 2){
			this.socketOut.write("�����ʽ����ȷ,��������ȷ�Ķ�ʱ��������!\r\n".getBytes());
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
			this.socketOut.write("��ʱ�������Ʋ���ȷ,��������ȷ�Ķ�ʱ��������!\r\n".getBytes());
			return;
		}

		//������ʱ����
		tm.startTask(argTaskId);
		
		this.socketOut.write(("\r\n�ֹ�������ʱ���� " + argTaskId + " !!!\r\n\r\n").getBytes());
	}
}
