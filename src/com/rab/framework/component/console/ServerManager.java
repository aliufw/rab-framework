package com.rab.framework.component.console;

import java.net.ServerSocket;
import java.net.Socket;

import com.rab.framework.domain.server.CoreAppServer;


public class ServerManager {
	
	private static ServerManager serverManager = null;
	
	private int port = 0;
	
	private CoreAppServer server;
	
	private ServerManager(){
		
	}

	public static ServerManager getInstance(){
		if(serverManager == null){
			serverManager = new ServerManager();
		}
		
		return serverManager;
	}
	
	public void setServer(CoreAppServer server) {
		this.server = server;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void processCMD(String command[]){
		if(command.length == 0){
			return;
		}
		
		if("dynamicload".equalsIgnoreCase(command[0])){
			if(command.length != 2){
				return;
			}
			String contextName = command[1];
			this.server.reloadContext(contextName);
		}
	}
	
	public void startServerManager(){
		ServerManagerThread thread = new ServerManagerThread();
		thread.setDaemon(true);
		thread.start();
	}
	
	
	public static void main(String[] args) {
		ServerManager sm = new ServerManager();
		sm.startServerManager();

	}

	class ServerManagerThread extends Thread{
		public void run(){
			try {
				ServerSocket ssocket = new ServerSocket(port);
				while(true){
					Socket socket = ssocket.accept();
					if(socket != null){
						ServerManagerProcessor smp = new ServerManagerProcessor(socket);
						smp.start();
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
