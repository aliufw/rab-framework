package com.rab.framework.component.dictcache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.rab.framework.component.console.CommandProcessor;
import com.rab.framework.component.console.ProcessBar;

/**
 * 
 * <P>Title: CacheManagerCommandProcessor</P>
 * <P>Description: </P>
 * <P>����˵�������������������</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-16</P>
 *
 */
public class CacheManagerCommandProcessor extends CommandProcessor{

	private String[] commands = {
			"cacheState",
			"cacheList",
			"cacheShow",
			"startUpdate",
			"startMonitor"
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
			if (commands[0].equalsIgnoreCase(cmd[0])) { //cacheState
				this.cacheState();
			} 
			else if (commands[1].equalsIgnoreCase(cmd[0])) { //cacheList
				this.cacheList();
			} 
			else if (commands[2].equalsIgnoreCase(cmd[0])) { //cacheShow
				this.cacheShow(cmd);
			} 
			else if (commands[3].equalsIgnoreCase(cmd[0])) { //startUpdate
				this.startUpdate();
			} 
			else if (commands[4].equalsIgnoreCase(cmd[0])) { //startMonitor
				this.startMonitor();
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void cacheState() throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("��ǰ����״̬: ");
		ServerCacheManager manager = (ServerCacheManager)ServerCacheManager.getDictCacheManager();
		boolean state = manager.getMonitor().isAlive();
		if(state){
			sb.append("������³�����������!");
		}
		else{
			sb.append("��������߳����в�����!");
		}
		
		sb.append("\r\n");
		
		this.socketOut.write(sb.toString().getBytes());
	}
	
	private void cacheList() throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("��ǰ��������ݱ�: \r\n");
		StringBuffer head = new StringBuffer();
		head.append(format("���",5))
				.append(format("����",15))
				.append(format("�汾��",10))
				.append(format("��������", 10))
				.append(format("��¼����",10))
				.append("\r\n");
		sb.append(repeat("-", len(head.toString()))).append("\r\n");
		sb.append(head);
		sb.append(repeat("-", len(head.toString()))).append("\r\n");

		ServerCacheManager manager = (ServerCacheManager)ServerCacheManager.getDictCacheManager();
		Map<String,CacheTable> codeCachePool = manager.getCodeCachePool();
		
		Iterator<CacheTable> iter = codeCachePool.values().iterator();
		int index = 1;
		while(iter.hasNext()){
			CacheTable ct = (CacheTable)iter.next();
			String tableName = ct.getTableName();
			int version = ct.getVersion();
			int cacheType = ct.getCacheType();
			int rowCount = 0;
			if(cacheType == CacheTable.CACHE_TYPE_MEM){
				rowCount = ct.getCacheData().size();
			}

			String strCacheType = cacheType == CacheTable.CACHE_TYPE_MEM ? "mem" : "db";
			
			sb.append(format("" + index++, 5))
			.append(format(tableName, 15))
			.append(format("" + version, 10))
			.append(format(strCacheType, 10))
			.append(format(""+rowCount ,10))
			.append("\r\n");
		}
		
		this.socketOut.write(sb.toString().getBytes());
	}
	
	private void cacheShow(String[] cmd) throws Exception{
		StringBuffer sb = new StringBuffer();
		
		if(cmd.length != 2){
			this.socketOut.write("�����ʽ����ȷ,���������!\r\n".getBytes());
			return;
		}
		
		ServerCacheManager manager = (ServerCacheManager)ServerCacheManager.getDictCacheManager();
		Map<String,CacheTable> codeCachePool = manager.getCodeCachePool();
		CacheTable ct = (CacheTable)codeCachePool.get(cmd[1]);
		if(ct == null){
			this.socketOut.write("��ѯ�ı�����,�������!\r\n".getBytes());
			return;

		}
		if(ct.getCacheType() == CacheTable.CACHE_TYPE_DB){
			this.socketOut.write("��������ڴ滺��,�������ݿ�ֱ����ʽ,��ֱ�Ӳ�ѯ���ݿ�!\r\n".getBytes());
			return;

		}
		
		String cacheType = ct.getCacheType()==CacheTable.CACHE_TYPE_DB ? "DB" : "MEM";
		sb.append("����    : ").append(cmd[1]).append("\r\n");
		sb.append("�汾��  : ").append(ct.getVersion()).append("\r\n");
		sb.append("��������: ").append(cacheType).append("\r\n");
		List<Map<String,Object>> table = ct.getCacheData();
		
		StringBuffer head = new StringBuffer();
		Map<String,Object> map = table.get(0);
		Iterator<String> iter = map.keySet().iterator();
		head.append(format("���",6));
		List<String> fields = new ArrayList<String>();
		while(iter.hasNext()){
			String fieldName = (String)iter.next();
			fields.add(fieldName);
			head.append(format(fieldName,20));
		}
		head.append("\r\n");
		
		sb.append(repeat("-", len(head.toString()))).append("\r\n");
		sb.append(head);
		sb.append(repeat("-", len(head.toString()))).append("\r\n");
		
		for(int i=0; i<table.size(); i++){
			Map<String,Object> row = table.get(i);
			sb.append(format("" + (i+1),6));
			for(int k=0; k<fields.size(); k++){
				String data = "" + row.get(fields.get(k));
				sb.append(format(data, 20));
			}
			sb.append("\r\n");
		}
		
		this.socketOut.write(sb.toString().getBytes());
	}
	
	private void startUpdate() throws Exception{
		String msg = "��ʼ���»�������";
		socketOut.write(msg.getBytes());
		socketOut.flush();

		ServerCacheManager manager = (ServerCacheManager)ServerCacheManager.getDictCacheManager();
		
		ProcessBar pb = new ProcessBar(socketOut);
		pb.start();
		try {
			manager.update();
		} 
		finally{
			pb.setFlag(false);
		}
		
		msg = "\r\n�������ݽ���!\r\n";
		socketOut.write(msg.getBytes());
		socketOut.flush();
	}
	
	private void startMonitor() throws Exception{
		String msg = "";

		ServerCacheManager manager = (ServerCacheManager)ServerCacheManager.getDictCacheManager();
		
		if(manager.getMonitor().isAlive()){
			msg = "��ǰ����߳�����״̬����,��������!\r\n";
		}
		else{
			manager.startMonitor();
			msg = "\r\n�߳��������!\r\n";
		}
		
		socketOut.write(msg.getBytes());
		socketOut.flush();
	}
	
	private String format(String msg, int length){
		return " " + this.formatString(msg, length);
	}
}
