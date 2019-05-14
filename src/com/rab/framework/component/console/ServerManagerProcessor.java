package com.rab.framework.component.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

import com.rab.framework.comm.cachesession.CacheSessionManagerCommandProcessor;
import com.rab.framework.component.dictcache.CacheManagerCommandProcessor;
import com.rab.framework.component.scheduler.SchedulerManagerCommandProcessor;
import com.rab.framework.domain.server.ServerAppManagerProcessor;

public class ServerManagerProcessor extends Thread {
	private String cmdSeparator = "\r\n";

	private String cmdPrompt = "\r\nvh-console> ";

	private String msg = "欢迎使用系统监控台！\r\n";

	private Socket socket = null;

	private String[] localCmds = { "help", "quit", "exit" };

	private String[] subCmdProcessor = {
			"组件部署管理|" + ServerAppManagerProcessor.class.getName() + "|reload|listapp",
			"代码表缓存管理|" + CacheManagerCommandProcessor.class.getName() + "|cacheState|cacheList|cacheShow|startUpdate|startMonitor",
			"登录信息管理|" + CacheSessionManagerCommandProcessor.class.getName() + "|syslist|syslogininfo|sysclear",
			"定时任务管理|" + SchedulerManagerCommandProcessor.class.getName() + "|tasklist|taskinfo|taskstart"
	};

	public ServerManagerProcessor(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			OutputStream os = socket.getOutputStream();
			os.write(msg.getBytes());
			os.write(cmdPrompt.getBytes());
			os.flush();

			InputStream is = socket.getInputStream();
			// is.read(new byte[1024]); // 清除“垃圾”信息

			byte[] cmdBuffer = new byte[0];
			while (is.available() >= 0) {
				byte b = (byte) is.read();
//				System.out.println(b + "\t" + (char) b);
				if (b == 8) {
					int len = cmdBuffer.length - 1;
					if (len <= 0) {
						cmdBuffer = new byte[0];
					} else {
						byte[] tmp = new byte[cmdBuffer.length - 1];
						System.arraycopy(cmdBuffer, 0, tmp, 0,
								cmdBuffer.length - 1);
						cmdBuffer = tmp;
					}
				} else {
					byte[] tmp = new byte[cmdBuffer.length + 1];
					System.arraycopy(cmdBuffer, 0, tmp, 0, cmdBuffer.length);
					tmp[cmdBuffer.length] = b;
					cmdBuffer = tmp;
				}

				if (isCmdComplete(cmdBuffer) && cmdBuffer.length > 2) {
					String cmd = new String(cmdBuffer, 0, cmdBuffer.length - 2);
					cmdProcess(cmd, os); // 调用命令处理
					// System.out.println("cmd = " + cmd);
					cmdBuffer = new byte[0]; // 清空命令缓存
					os.write(cmdPrompt.getBytes()); // 输出提示符
					os.flush();
				} else if (isCmdComplete(cmdBuffer) && cmdBuffer.length == 2) {
					cmdBuffer = new byte[0]; // 清空命令缓存
					os.write(cmdPrompt.getBytes()); // 输出提示符
					os.flush();
				}
				// System.out.println(b + "\t" + (char)b);
			}
			os.close();
		} catch (Exception e) {
			if (!e.getMessage().equals("Socket closed")) {
				e.printStackTrace();
			}
		}
	}

	private boolean isCmdComplete(byte[] cmdBuffer) {
		boolean flag = true;

		byte[] endsep = this.cmdSeparator.getBytes();
		int pointer = cmdBuffer.length - endsep.length;
		if (pointer < 0) {
			return false;
		}
		for (int i = 0; i < endsep.length; i++) {
			if (cmdBuffer[pointer + i] != endsep[i]) {
				flag = false;
				break;
			}
		}

		return flag;
	}

	private void cmdProcess(String cmdLine, OutputStream socketOut) {
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
			//
			String processor = this.getCommandProcessor(cmd[0]);
			if (processor != null) { // 进入相应的命令处理程序
				CommandProcessor cp = (CommandProcessor) Class.forName(
						processor).newInstance();
				cp.setSocketOut(socketOut);
				cp.cmdProcess(cmdLine);
			}
			// 默认的,执行本地命令程序
			else if (localCmds[0].equalsIgnoreCase(cmd[0])) { // help
				cmdHelp(socketOut);
			} else if (localCmds[1].equalsIgnoreCase(cmd[0])
					|| localCmds[2].equalsIgnoreCase(cmd[0])) { // quit or exit
				cmdQuit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getCommandProcessor(String cmd) {
		for (int i = 0; i < this.subCmdProcessor.length; i++) {
			int posB = this.subCmdProcessor[i].indexOf("|");
			int posE = this.subCmdProcessor[i].indexOf("|", posB + 1);
			String className = this.subCmdProcessor[i]
					.substring(posB + 1, posE);
			String cmds = this.subCmdProcessor[i].substring(posE + 1);

			StringTokenizer st = new StringTokenizer(cmds, "|");
			while (st.hasMoreElements()) {
				String s = (String) st.nextElement();
				if (s.equalsIgnoreCase(cmd)) {
					return className;
				}
			}
		}
		return null;
	}

	private void cmdHelp(OutputStream socketOut) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("命令列表:\r\n");
		int index = 0;
		for (int i = 0; i < localCmds.length; i++) {
			sb.append("\t").append(index++).append("\t").append(localCmds[i])
					.append("\r\n");
		}

		for (int i = 0; i < this.subCmdProcessor.length; i++) {
			int posB = this.subCmdProcessor[i].indexOf("|");
			int posE = this.subCmdProcessor[i].indexOf("|", posB + 1);
			String cmdProcessorName = this.subCmdProcessor[i]
					.substring(0, posB);
			String cmds = this.subCmdProcessor[i].substring(posE + 1);

			StringTokenizer st = new StringTokenizer(cmds, "|");

			sb.append("\t    ").append(cmdProcessorName).append(": ").append(
					"\r\n");
			while (st.hasMoreElements()) {
				String cmd = (String) st.nextElement();
				if (cmd.equalsIgnoreCase("reload"))
					cmd = cmd + "             重新加载模块，参数为模块名称";
				if (cmd.equalsIgnoreCase("listapp"))
					cmd = cmd + "            显示当前所加载的应该模块列表，无参数";
				if (cmd.equalsIgnoreCase("cacheState"))
					cmd = cmd + "         显示当前缓存状态，无参数";
				if (cmd.equalsIgnoreCase("cacheList"))
					cmd = cmd + "          显示当前缓存数据表列表，无参数";
				if (cmd.equalsIgnoreCase("cacheShow"))
					cmd = cmd + "          查询某张缓存表信息，参数为缓存表名";
				if (cmd.equalsIgnoreCase("startUpdate"))
					cmd = cmd + "        更新缓存数据，无参数";
				if (cmd.equalsIgnoreCase("startMonitor"))
					cmd = cmd + "       启动同步监控线程，无参数";
				if (cmd.equalsIgnoreCase("syslist"))
					cmd = cmd + "            显示当前所有登陆系统中的用户信息，无参数";
				if (cmd.equalsIgnoreCase("syslogininfo"))
					cmd = cmd + "       显示某个具体用户的信息，参数为用户编号";
				else if (cmd.equalsIgnoreCase("sysclear"))
					cmd = cmd + "           清除所有登陆用户信息，无参数";
				sb.append("\t").append(index++).append("\t").append(cmd)
						.append("\r\n");
			}
		}

		socketOut.write(sb.toString().getBytes());

	}

	private void cmdQuit() {
		try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
