package com.rab.framework.component.dictcache.browsercache;

import java.applet.Applet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * 
 * <P>Title: BrowserCacheManager</P>
 * <P>Description: </P>
 * <P>����˵����������˴������������</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-16</P>
 *
 */
public class BrowserCacheManager extends Applet{ 
	
	/**
	 * ���л����
	 */
	private static final long serialVersionUID = -5425184400036878869L;


	//------------------------------------------------------------------------------ϵͳ��̬����
	/**
	 * �������ݵķָ����� 
	 */
	private static String CACHE_DATA_BOUNDARY = "----------CacheDataBoundary";
	
	
	//------------------------------------------------------------------------------ ϵͳ����
	/**
	 * web��������ַ
	 */
	private String serverip;
	
	/**
	 * web�������˿ں�
	 */
	private int serverport;
	
	/**
	 * web�������й���ͻ��˻������ص�servletURI
	 */
	private String managerServletURI;
	
	/**
	 * ���������ڱ��ش�ŵĸ�·��
	 */
	private String localrootdir = "";
	
	/**
	 * ���������ڱ��ش�ŵĸ�·��
	 */
	private String localdir = "";
	
	/**
	 * �����������
	 */
	private int updateCyc = -1;
	
	/**
	 * ������¼���߳�
	 */
	private Thread monitorThread = null;
	
	
	/**
	 * ������������Ϣ�������˷������˻���������CacheTable���������壬��ֻ�������еĲ�������
	 * CacheTable����
	 */
	private Map<String, BrowserCacheTable> cacheTables = new HashMap<String, BrowserCacheTable>();
	
	/**
	 * ��־��¼����
	 */
	private BrowserCacheLog logger;
	
	
	private DES des = new DES();
	//--------------------------------------------------------------------------------- ϵͳ���Է���
	
	public void setManagerServletURI(String managerServletURI) {
		this.managerServletURI = managerServletURI;
	}

	
	public void setLocalrootdir(String localrootdir) {
		this.localrootdir = localrootdir;
	}

	public void setServerip(String serverip) { 
		this.serverip = serverip;
	}

	public void setServerport(String serverport) {
		this.serverport = Integer.parseInt(serverport);
	}

	public void setUpdateCyc(String updateCyc) {
		this.updateCyc = Integer.parseInt(updateCyc);
	}

	//-------------------------------------------------------------------------------------- ������
	/**
	 * ������
	 *
	 */
	public BrowserCacheManager(){

	}
	
	/**
	 * applet��ʼ������
	 */
	public void init(){
		//�ر�ɳ��
		System.setSecurityManager(null);
	}
	
	//---------------------------------------------------------------------------------- public����
	
	/**
	 * ��ʼ����
	 * 1. �ӱ��ش����м��ػ���İ汾��Ϣ
	 * 2. ��������ͬ������߳�
	 *
	 */
	public void initCacheManager(){
		//����localdir
		////����ʱû����ֵ����ѡ��Ĭ�ϵı���·�� {user.home}
		if(this.localrootdir.equals("")){
			localrootdir = System.getProperty("user.home");
		}
		//��ʼ����־��¼��
		logger = new BrowserCacheLog(this.localdir);
        localdir = localrootdir + File.separator + "codeCache" + File.separator + this.serverip + "_" + this.serverport;

        logger.log("localdir-------->"+localdir);

        File file = new File(localdir);
		if(!file.exists()){
			file.mkdirs();
		}

        File verisonlist = new File(localdir, "versionList.lst");

        logger.log("verisonlist------>"+verisonlist);
        logger.log("verisonlist------>"+verisonlist.getName());

        if(!verisonlist.exists()){
			//����ͬ�������߳�
			startUpdateMonitor();
            logger.log("����ͬ�������߳�!");
			return;
		}
		
		try {
			FileInputStream fis = new FileInputStream(verisonlist);
			byte[] buffer = new byte[1024];
			byte[] data = new byte[0];
			int len = fis.read(buffer);
			while(len > 0){
				byte[] tmp = new byte[data.length + len];
				System.arraycopy(data, 0, tmp, 0, data.length);
				System.arraycopy(buffer, 0, tmp, data.length, len);
				data = tmp;
				len = fis.read(buffer);
			}
			fis.close();
			
			String filecontent = new String(data);
			StringTokenizer st = new StringTokenizer(filecontent, "\r\n");
			while(st.hasMoreElements()){
				String line = (String)st.nextElement();
				int pos = line.indexOf(",");
				String tablename = line.substring(0,pos);
				String version = line.substring(pos + 1);
				
				long filelength = getFileSize(tablename);
				if( filelength == -1){
					continue;
				}
				else if(filelength == 0){
					String fileName = tablename + ".xml";
					fileName = fileName.toUpperCase();
					File f = new File(fileName);
					f.delete();
					continue;
				}
				
            	BrowserCacheTable ct = new BrowserCacheTable();
            	ct.setTableName(tablename);
            	ct.setVersion(Integer.parseInt(version.trim()));
            	
            	this.cacheTables.put(tablename,ct);
            	
            	logger.log("info: ��ʼ������汾��Ϣ: " + tablename + ", \t" + version);
			}

		} catch (Exception e) {
			logger.log("error: ϵͳ��ʼ��ʱ�����쳣�� ", e);
		}
		
		//����ͬ�������߳�
		startUpdateMonitor();
	}
	
	/**
	 * ����ļ�����
	 * 
	 * @param tablename
	 * @return  -1���ļ������ڣ�len���ļ�����
	 */
    private long getFileSize(String tablename){
		//1. �Ӽ�鱾���Ƿ����ָ�����ƵĴ����
		String fileName = tablename + ".xml";
		fileName = fileName.toUpperCase();
		boolean flag = this.fileIsExists(fileName);
    	if(!flag){
    		return -1;
    	}
		
		File file = new File(localdir, fileName);
		return file.length();
    }

	/**
	 * ��ȡ��ǰ����ͬ������߳�״̬
	 * 
	 * @return
	 */
	public boolean getMonitorState(){
		if(this.monitorThread == null){
			return false;
		}
		else{
			return this.monitorThread.isAlive();
		}
		
	}
	
	/**
	 * ������������ͬ������߳�
	 *
	 */
	public void restartUpdateMonitor(){
		if(!this.monitorThread.isAlive()){ 
			startUpdateMonitor();
		}
	}
	
	/**
	 * ȡ��ǰ������������ƺͰ汾��
	 *
	 */
	public String getCurrentCacheList(){
		StringBuffer sb = new StringBuffer();
        Iterator<BrowserCacheTable> iter = this.cacheTables.values().iterator();
        while(iter.hasNext()){
        	BrowserCacheTable tmp = iter.next();
        	String name = tmp.getTableName();
        	int ver = tmp.getVersion();
        	sb.append(name);
        	sb.append(",");
        	sb.append(ver);
        	sb.append("\r\n");
        }
        
        return sb.toString();
	}
	
	/**
	 * �ӱ��ػ�����ȡָ�����ƵĴ�������ַ�����ʽ����
	 * 
	 * @param tableName 
	 * @return
	 */
	public String getCacheTable(String tableName){
		if(tableName == null || tableName.trim().length()==0){
			return null;
		}
		tableName = tableName.trim();
		 
		//1. �Ӽ�鱾���Ƿ����ָ�����ƵĴ����
		String fileName = tableName + ".xml";
		fileName = fileName.toUpperCase();
		boolean flag = this.fileIsExists(fileName);
		
		//2. �ļ������ڣ��ӷ��������أ�
		if(!flag){//
			//����request�ַ���
			String uri = createHttpRequestURI();
			String parameter = "tablenames=" + tableName.toLowerCase() + "&versions=-1";
			boolean success = getDataFromWebServer(uri, parameter);
			if(!success){
				logger.log("error: �ӷ�������ȡ�����ʧ�ܣ� tablename = " + tableName);
				return null; // ����ʧ�ܣ���-��
			}
		}
		
		//3. �ӱ���Ӳ���϶�ȡ�ļ�������
		try {
			byte[] data = new byte[0];
			File file = new File(localdir, fileName);
			FileInputStream in = new FileInputStream(file); 
			byte[] buffer = new byte[10240];
			int len = in.read(buffer);
			while(len > 0){
				byte[] tmp = new byte[data.length + len];
				System.arraycopy(data, 0, tmp, 0, data.length);
				System.arraycopy(buffer, 0, tmp, data.length, len);
				data = tmp;
				
				len = in.read(buffer); 
			}
			try{
				byte[] decryptorByte = des.createDecryptor(data);
				return new String(decryptorByte);
			}catch(Exception e){
				updateCacheTable();
				String tableStirng = readTable(fileName);
				return tableStirng;
			}
			
		} catch (Exception e) {
			logger.log("error: �ӱ��ػ����ļ���ȡ�����ʧ�ܣ� tablename = " + tableName, e);
			return null;
		}
	}
	public String readTable(String fileName){
		try {
			byte[] data = new byte[0];
			File file = new File(localdir, fileName);
			FileInputStream in = new FileInputStream(file); 
			byte[] buffer = new byte[10240];
			int len = in.read(buffer);
			while(len > 0){
				byte[] tmp = new byte[data.length + len];
				System.arraycopy(data, 0, tmp, 0, data.length);
				System.arraycopy(buffer, 0, tmp, data.length, len);
				data = tmp;
				
				len = in.read(buffer); 
			}
			
				byte[] decryptorByte = des.createDecryptor(data);
				return new String(decryptorByte);
			
		} catch (Exception e) {
			logger.log("error: �ӱ��ػ����ļ���ȡ�����ʧ�ܣ� tablename = " + fileName, e);
			return null;
		}
		
	}
	
	/**
	 * ��web�������еĻ�����Ϣͬ��
	 *
	 */
	public void updateCacheTable(){
		//1. ȡ���ػ�������б�
		Iterator<BrowserCacheTable> iter = this.cacheTables.values().iterator();
		String tablenames = "";
		String versions = "";
		while(iter.hasNext()){
			BrowserCacheTable ct = iter.next();
			String tablename = ct.getTableName();
			int version = ct.getVersion();
			
			tablenames += tablename + ",";
			versions   += version   + ",";
		}
		
		//����request�ַ���
		String uri = createHttpRequestURI();
		String parameter = "tablenames=" + tablenames + "&versions=" + versions;

		logger.log("uri---------->"+uri);
		logger.log("parameter---------->"+parameter);
        getDataFromWebServer(uri, parameter);

	}
	
	
	//---------------------------------------------------------------------------------- private����
	/**
	 *  ��web����������ָ��������
	 *  
	 * @param uri 
	 * @param parameter 
	 * @return
	 */
	private boolean getDataFromWebServer(String uri, String parameter){
		try {
			Socket socket = new Socket(serverip, serverport);
			OutputStream out = socket.getOutputStream();
		
			String request = createHttpRequest(uri,parameter);
			out.write(request.getBytes());

			InputStream is = socket.getInputStream();
			
			//����ͷ��Ϣ
			byte[] head = this.getHttpResponseHead(is);
			int pos = this.KMPIndex(head, "\r\n".getBytes(), 0);
			String firstLine = new String(head,0,pos);
            logger.log("firstLine------------->"+firstLine);
            if(!firstLine.endsWith("200 OK")){
				throw new Exception("�����������쳣��������ϢΪ�� " + firstLine);
			}

			//ȡ���ݳ���
			byte[] mode = "Content-Length:".getBytes();
			int pos0 = this.KMPIndex(head, mode, 0);
			int pos1 = this.KMPIndex(head,"\r\n".getBytes(), pos0);
			String strLength = new String(head, pos0 + mode.length, pos1-pos0-mode.length).trim();
			int dataLength = Integer.parseInt(strLength);
			if(dataLength == 0){
				logger.log("info: �������£� ��������û����Ҫ���µ����ݣ�");
				return true;
			}
			
			//ȡ�ļ�����
			byte[] buffer = new byte[10240];
			byte[] data = new byte[0]; //�ļ���Ӧ�����ݻ�����
			int len = is.read(buffer);
			int lengthCounter = 0;
			lengthCounter = len;
			while(len > 0){
				int posB = 0;
				int posE = KMPIndex(buffer, CACHE_DATA_BOUNDARY.getBytes(), posB);
				while(posE >= 0){
					//�Ѵӵ�ǰ��ʼ�㵽posE�����ݷ���
					byte[] tmp = new byte[data.length + posE-posB];
					System.arraycopy(data, 0, tmp, 0, data.length);
					System.arraycopy(buffer, posB, tmp, data.length, posE-posB);
					data = tmp;
					
					//�ļ����ս�����������ļ��� 
					saveCacheTable(data);
					
					//����ļ����ݻ�����
					data = new byte[0];
					
					//��λ��һ���ļ������ݶ�
					posB = posE + CACHE_DATA_BOUNDARY.getBytes().length;
					posE = KMPIndex(buffer, CACHE_DATA_BOUNDARY.getBytes(), posB);
				}
				
				//û���ҵ�CACHE_DATA_BOUNDARY���򽫵�ǰbuffer�е����ݳ�ȥ�߽糤�Ⱥ�����ļ����ݻ�����
				if(len-posB > CACHE_DATA_BOUNDARY.getBytes().length){
					posE = len - CACHE_DATA_BOUNDARY.getBytes().length;
					byte[] tmp = new byte[data.length + posE-posB];
					System.arraycopy(data, 0, tmp, 0, data.length);
					System.arraycopy(buffer, posB, tmp, data.length, posE-posB);
					data = tmp;
					
					posB = posE;
				}
				
				for(int i=0; i<len-posB; i++){
					buffer[i] = buffer[posB+i];
				}
				
				int tmp = len-posB;
				
				if(lengthCounter == dataLength){
					break;
				}

				len = is.read(buffer, len-posB, buffer.length-tmp);
				lengthCounter += len;
				len += tmp;
			}
			return true;
		} 
		catch (Exception e) {
			logger.log("error: ���ʷ�����ʧ�ܣ� uri=" + uri + ", parameter=" + parameter, e);
			
			return false;
		}
	}

	/**
	 * ����ӷ����������ص�����
	 * 
	 * @param data 
	 */
	private void saveCacheTable(byte[] data){
		//1. ȡ����
		int pointer =0;
		String tablename = new String(data, pointer, 20);
		tablename = tablename.trim();
		
		//2. ȡ�汾��
		pointer += 20;
		String version = new String(data, pointer, 5);
		version = version.trim();
		int ver = Integer.parseInt(version);
		
		//3. ȡ�ļ�����
		pointer += 5;
		
		//4. д�ļ�
		try {
			File file = new File(this.localdir, tablename + ".xml");
			FileOutputStream fout = new FileOutputStream(file);
		     
		//	fout.write(data, pointer, data.length-pointer);
		    String dataStr = new String(data, pointer,data.length-pointer,"UTF-8");
		    
			byte[] dataEncryptor = des.createEncryptor(dataStr);
			
			fout.write(dataEncryptor);
			fout.flush();
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 

		upversionlist(tablename, ver);
		
	}
	
	/**
	 * ���°汾��Ϣ�б�
	 * 
	 * @param tablename
	 * @param version
	 */
	private void upversionlist(String tablename, int version){
		BrowserCacheTable ct = (BrowserCacheTable)this.cacheTables.get(tablename);
		
		if(ct == null){
			ct = new BrowserCacheTable();
			ct.setTableName(tablename);
			this.cacheTables.put(tablename, ct);
		}
		ct.setVersion(version);
		
		logger.log("info: �����µĻ����ļ��� tablename=" + tablename + ", version=" + version);
		
		StringBuffer sb = new StringBuffer();
        Iterator<BrowserCacheTable> iter = this.cacheTables.values().iterator();
        while(iter.hasNext()){
        	BrowserCacheTable tmp = iter.next();
        	String name = tmp.getTableName();
        	int ver = tmp.getVersion();
        	sb.append(name);
        	sb.append(",");
        	sb.append(ver);
        	sb.append("\r\n");
        }

        try {
			File versionlist = new File(localdir, "versionList.lst");
			FileOutputStream out = new FileOutputStream(versionlist);
			out.write(sb.toString().getBytes());
			out.flush();
			out.close();
			
		} catch (Exception e) {
			logger.log("error: ����汾�б���Ϣ���ļ� versionList.lst ʧ�ܣ�", e);
		}
	}
	
	/**
	 * ����HTTP����ͷ��Ϣ
	 * 
	 * @param uri 
	 * @param parameter 
	 * 
	 * @return 
	 */
	private String createHttpRequest(String uri, String parameter){
		parameter = parameter.replaceAll(",", "%2C");
		
		String request = "";
		request += "POST " + uri + " HTTP/1.1\r\n";
		request += "Accept: */*\r\n";
		request += "Content-Type: application/x-www-form-urlencoded\r\n";
		request += "User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)\r\n";
		request += "Host: " + serverip + ":" + serverport + "\r\n";
		request += "Pragma: no-cache\r\n";
		request += "Content-Length:" + parameter.getBytes().length + "\r\n";
		request += "Cache-Control: no-cache\r\n";
		request += "\r\n";
		request += parameter;

		return request;
	}
	
	/**
	 * ȡHTTP�������ݵ�ͷ��Ϣ
	 * 
	 * @param is
	 * @return
	 * @throws Exception
	 */
	private byte[] getHttpResponseHead(InputStream is) throws Exception{
		byte[] headbuffer = new byte[10240];
		byte b=(byte)is.read();
		int pointer = 0;
		
		while(true){
			headbuffer[pointer] = b;
			pointer ++;
			if(b=='\r'){
				byte[] tmp = new byte[3];
				int len = is.read(tmp);
				if(len <3){
					//TODO: ��������쳣��
				}
				if(tmp[0] == '\n' && tmp[1]=='\r' && tmp[2]=='\n'){
					headbuffer[pointer] = tmp[0];
					pointer ++;
					break;
				}
				else{
					System.arraycopy(tmp, 0, headbuffer, pointer, 3);
					pointer +=3;

				}
			}
			
			b=(byte)is.read();
		}
		byte[] head = new byte[pointer];
		System.arraycopy(headbuffer, 0, head, 0, pointer);
		
		return head;
	}
	
	/**
	 * ����HTTP�����URI�ַ���
	 * 
	 * @return
	 */
	private String createHttpRequestURI(){
		String uri = "http://" + this.serverip + ":" + this.serverport + this.managerServletURI;
		
		return uri;
	}
	
	/** 
	 * �ж�ָ�����ļ��Ƿ����
	 * 
	 * @param fileName
	 * @return
	 */
	private boolean fileIsExists(String fileName){
		File file = new File(localdir, fileName);
		if(file.exists()){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * KMPģʽƥ���㷨
	 * 
	 * @param data
	 * @param mode
	 * @param startpos
	 * @return
	 */
	private int KMPIndex(byte[] data, byte[] mode, int startpos){
		int i = startpos;
		int j = 0;
		
		int[] next = getNext(mode);
		
		while(i<data.length && j<mode.length){
			if(j==-1 || data[i]==mode[j]){
				i++;
				j++;
			}
			else{
				j=next[j];
			}
		}
		
		if(j>=mode.length){
			return i-mode.length;
		}
		else{
			return -1;
		}
	}

	/**
	 * KMPģʽƥ���㷨��nextģʽ����
	 * 
	 * @param mode
	 * @return
	 */
	private int[] getNext(byte[] mode){
		int[] next = new int[mode.length];
		
		int len = mode.length;
		int i=0;
		int k=-1;
		next[0] = -1;
		while(i<len-1){
			if(k==-1 || mode[i]==mode[k]){
				i++;
				k++;
				next[i]=k;
			}
			else{
				k=next[k];
			}
		}
		
		return next;
	}
	
	/**
	 * ��������ͬ������߳�
	 *
	 */
	private void startUpdateMonitor(){
		this.monitorThread = new UpdateMonitor();
		this.monitorThread.setDaemon(true);
		this.monitorThread.start();
		
	}

	/**
	 * ����ͬ������߳�
	 * 
	 * @author lfw
	 *
	 */
	class UpdateMonitor extends Thread{
		public void run(){
			if(updateCyc == -1){
				logger.log("info: �Զ���������û�����ã�ϵͳ����Ĭ�ϵĸ������ڣ� cyc=60 ����");
				updateCyc = 60; //����
			}

			while(true){
				try {
					//double rnd = Math.random();
					long cyc = (long)(updateCyc * 60 * 1000 * 5);
					sleep(cyc); 
				} catch (InterruptedException e) {
					logger.log("error: ����������ͬ���̳߳����쳣��", e);
				}
				
				updateCacheTable();
			}
		}
	}

}





















































