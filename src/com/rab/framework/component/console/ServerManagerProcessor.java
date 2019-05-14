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

	private String msg = "��ӭʹ��ϵͳ���̨��\r\n";

	private Socket socket = null;

	private String[] localCmds = { "help", "quit", "exit" };

	private String[] subCmdProcessor = {
			"����������|" + ServerAppManagerProcessor.class.getName() + "|reload|listapp",
			"����������|" + CacheManagerCommandProcessor.class.getName() + "|cacheState|cacheList|cacheShow|startUpdate|startMonitor",
			"��¼��Ϣ����|" + CacheSessionManagerCommandProcessor.class.getName() + "|syslist|syslogininfo|sysclear",
			"��ʱ�������|" + SchedulerManagerCommandProcessor.class.getName() + "|tasklist|taskinfo|taskstart"
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
			// is.read(new byte[1024]); // �������������Ϣ

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
					cmdProcess(cmd, os); // ���������
					// System.out.println("cmd = " + cmd);
					cmdBuffer = new byte[0]; // ��������
					os.write(cmdPrompt.getBytes()); // �����ʾ��
					os.flush();
				} else if (isCmdComplete(cmdBuffer) && cmdBuffer.length == 2) {
					cmdBuffer = new byte[0]; // ��������
					os.write(cmdPrompt.getBytes()); // �����ʾ��
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
			if (processor != null) { // ������Ӧ����������
				CommandProcessor cp = (CommandProcessor) Class.forName(
						processor).newInstance();
				cp.setSocketOut(socketOut);
				cp.cmdProcess(cmdLine);
			}
			// Ĭ�ϵ�,ִ�б����������
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
		sb.append("�����б�:\r\n");
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
					cmd = cmd + "             ���¼���ģ�飬����Ϊģ������";
				if (cmd.equalsIgnoreCase("listapp"))
					cmd = cmd + "            ��ʾ��ǰ�����ص�Ӧ��ģ���б��޲���";
				if (cmd.equalsIgnoreCase("cacheState"))
					cmd = cmd + "         ��ʾ��ǰ����״̬���޲���";
				if (cmd.equalsIgnoreCase("cacheList"))
					cmd = cmd + "          ��ʾ��ǰ�������ݱ��б��޲���";
				if (cmd.equalsIgnoreCase("cacheShow"))
					cmd = cmd + "          ��ѯĳ�Ż������Ϣ������Ϊ�������";
				if (cmd.equalsIgnoreCase("startUpdate"))
					cmd = cmd + "        ���»������ݣ��޲���";
				if (cmd.equalsIgnoreCase("startMonitor"))
					cmd = cmd + "       ����ͬ������̣߳��޲���";
				if (cmd.equalsIgnoreCase("syslist"))
					cmd = cmd + "            ��ʾ��ǰ���е�½ϵͳ�е��û���Ϣ���޲���";
				if (cmd.equalsIgnoreCase("syslogininfo"))
					cmd = cmd + "       ��ʾĳ�������û�����Ϣ������Ϊ�û����";
				else if (cmd.equalsIgnoreCase("sysclear"))
					cmd = cmd + "           ������е�½�û���Ϣ���޲���";
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
