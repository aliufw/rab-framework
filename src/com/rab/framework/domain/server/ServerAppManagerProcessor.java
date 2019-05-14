package com.rab.framework.domain.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import com.rab.framework.comm.exception.BaseCheckedException;
import com.rab.framework.component.console.CommandProcessor;

/**
 * 
 * <P>Title: ServerManagerProcessor</P>
 * <P>Description: </P>
 * <P>程序说明：监控管理处理器</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class ServerAppManagerProcessor extends CommandProcessor {
	private Socket socket = null;

	private CoreAppServer server;

	private String[] commands = { "reload", "listapp", "help", "quit", "exit" };

	public ServerAppManagerProcessor(){
		try {
			this.server = CoreAppServer.getInstance();
		} catch (BaseCheckedException e) {
			e.printStackTrace();
		}
	}
	
//	public ServerAppManagerProcessor(Socket socket, VHAppServer server) {
//		this.socket = socket;
//		this.server = server;
//	}

//	public void run() {
//		String cmdSeparator = "\r\n";
//		String cmdPrompt = "\r\nvh-console> ";
//		String msg = "欢迎使用 系统 监控台！\r\n";
//		try {
//			OutputStream os = socket.getOutputStream();
//			os.write(msg.getBytes());
//			os.write(cmdPrompt.getBytes());
//			os.flush();
//
//			InputStream is = socket.getInputStream();
//			String cmdBuffer = "";
//			byte[] buffer = new byte[100];
//			int len = is.read(buffer);
//			while (len > 0) {
//				String cmdLine = new String(buffer, 0, len);
//				cmdBuffer += cmdLine;
//				if (cmdBuffer.indexOf(cmdSeparator) > 0) {
//					cmdProcess(cmdBuffer, os); //调用命令处理
//					cmdBuffer = ""; //清空命令缓存
//
//					os.write(cmdPrompt.getBytes()); //输出提示符
//					os.flush();
//				}
//				len = is.read(buffer);
//			}
//			os.close();
//		} catch (Exception e) {
//			//e.printStackTrace();
//		}
//	}

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
			
			if (commands[0].equalsIgnoreCase(cmd[0])) { //reload
				cmdReload(cmd, socketOut);
			} 
			else if (commands[1].equalsIgnoreCase(cmd[0])) { //listapp
				cmdListapp(cmd, socketOut);
			} 
			else if (commands[2].equalsIgnoreCase(cmd[0])) { //help
				cmdHelp(socketOut);
			} 
			else if (commands[3].equalsIgnoreCase(cmd[0])
					|| commands[4].equalsIgnoreCase(cmd[0])) { //quit or exit
				cmdQuit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void cmdReload(String[] cmd, OutputStream socketOut) throws Exception{
		String msg = "";
		if(cmd.length < 2){
			msg = "命令格式不正确,请指明需要reload的模块名称!\r\n";
			socketOut.write(msg.getBytes());
			return;
		}
		
		Map<String, CoreAppContext> map = server.getContexts();
		if(map == null)
			map = new HashMap<String, CoreAppContext>();
		for(int i=1; i<cmd.length; i++){
			Iterator<String> iter = map.keySet().iterator();
			boolean flag = false;
			while(iter.hasNext()){
				String contextName = (String)iter.next();
				if(cmd[i].equals(contextName)){
					flag = true;
					break;
				}
			}
			
			if(!flag){
				msg = "模块 " + cmd[i] + " 不存在,请检查名称是否正确! \r\n";
				socketOut.write(msg.getBytes());
				socketOut.flush();
				continue;
			}
			
			msg = "模块 " + cmd[i] + " 开始重新加载 ... ... \r\n";
			socketOut.write(msg.getBytes());
			socketOut.flush();
			
			ProcessBar pb = new ProcessBar(socketOut);
			pb.start();
			boolean ret = server.reloadContext(cmd[i]); //重加载!
			pb.setFlag(false);
			
			if(ret){
				msg = "模块 " + cmd[i] + " 加载成功! \r\n";
			}
			else{
				msg = "模块 " + cmd[i] + " 加载失败! \r\n";
			}
			socketOut.write(msg.getBytes());
			socketOut.flush();
		}
		
	}
	
	private void cmdListapp(String[] cmd, OutputStream socketOut) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("当前加载的应用模块列表:\r\n");
		sb.append("\tContextName\r\n");
		sb.append("\t-------------------\r\n");
		
		Map<String, CoreAppContext> map = server.getContexts();
		if(map == null)
			map = new HashMap<String, CoreAppContext>();
		Iterator<String> iter = map.keySet().iterator();
		while(iter.hasNext()){
			String contextName = (String)iter.next();
			sb.append("\t").append(contextName).append("\r\n");
		}
		socketOut.write(sb.toString().getBytes());
	}
	
	private void cmdHelp(OutputStream socketOut) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("命令列表:\r\n");
		for(int i=0; i<commands.length; i++){
			sb.append("\t").append(i+1).append("\t").append(commands[i]).append("\r\n");
		}
		socketOut.write(sb.toString().getBytes());

	}
	
	private void cmdQuit(){
		try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class ProcessBar extends Thread{
		OutputStream out;
		boolean flag = true;
		ProcessBar(OutputStream out){
			this.out = out;
		}
		public void run(){
			try {
				while(flag){
					out.write(".".getBytes());
					sleep(1000);
				}
			} 
			catch (Exception e) {
			}
		}
		public void setFlag(boolean flag) {
			this.flag = flag;
		}
		
	}
	
}
