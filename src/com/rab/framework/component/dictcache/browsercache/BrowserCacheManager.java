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
 * <P>程序说明：浏览器端代码表缓存管理程序</P>
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
	 * 序列化标记
	 */
	private static final long serialVersionUID = -5425184400036878869L;


	//------------------------------------------------------------------------------系统静态变量
	/**
	 * 返回数据的分隔符号 
	 */
	private static String CACHE_DATA_BOUNDARY = "----------CacheDataBoundary";
	
	
	//------------------------------------------------------------------------------ 系统属性
	/**
	 * web服务器地址
	 */
	private String serverip;
	
	/**
	 * web服务器端口号
	 */
	private int serverport;
	
	/**
	 * web服务器中管理客户端缓存下载的servletURI
	 */
	private String managerServletURI;
	
	/**
	 * 缓存数据在本地存放的根路径
	 */
	private String localrootdir = "";
	
	/**
	 * 缓存数据在本地存放的根路径
	 */
	private String localdir = "";
	
	/**
	 * 缓存更新周期
	 */
	private int updateCyc = -1;
	
	/**
	 * 缓存更新监控线程
	 */
	private Thread monitorThread = null;
	
	
	/**
	 * 缓存表的描述信息，借用了服务器端缓存表的类型CacheTable的描述定义，但只用了其中的部分属性
	 * CacheTable集合
	 */
	private Map<String, BrowserCacheTable> cacheTables = new HashMap<String, BrowserCacheTable>();
	
	/**
	 * 日志记录对象
	 */
	private BrowserCacheLog logger;
	
	
	private DES des = new DES();
	//--------------------------------------------------------------------------------- 系统属性方法
	
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

	//-------------------------------------------------------------------------------------- 构造器
	/**
	 * 构造器
	 *
	 */
	public BrowserCacheManager(){

	}
	
	/**
	 * applet初始化函数
	 */
	public void init(){
		//关闭沙箱
		System.setSecurityManager(null);
	}
	
	//---------------------------------------------------------------------------------- public方法
	
	/**
	 * 初始化，
	 * 1. 从本地磁盘中加载缓存的版本信息
	 * 2. 启动缓存同步监控线程
	 *
	 */
	public void initCacheManager(){
		//设置localdir
		////运行时没有设值，则选用默认的保存路径 {user.home}
		if(this.localrootdir.equals("")){
			localrootdir = System.getProperty("user.home");
		}
		//初始化日志记录器
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
			//启动同步更新线程
			startUpdateMonitor();
            logger.log("启动同步更新线程!");
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
            	
            	logger.log("info: 初始化缓存版本信息: " + tablename + ", \t" + version);
			}

		} catch (Exception e) {
			logger.log("error: 系统初始化时出现异常： ", e);
		}
		
		//启动同步更新线程
		startUpdateMonitor();
	}
	
	/**
	 * 检查文件长度
	 * 
	 * @param tablename
	 * @return  -1：文件不存在，len：文件长度
	 */
    private long getFileSize(String tablename){
		//1. 从检查本地是否存在指定名称的代码表
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
	 * 读取当前缓存同步监控线程状态
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
	 * 重新启动缓存同步监控线程
	 *
	 */
	public void restartUpdateMonitor(){
		if(!this.monitorThread.isAlive()){ 
			startUpdateMonitor();
		}
	}
	
	/**
	 * 取当前缓存代码表的名称和版本号
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
	 * 从本地缓存中取指定名称的代码表，以字符串形式返回
	 * 
	 * @param tableName 
	 * @return
	 */
	public String getCacheTable(String tableName){
		if(tableName == null || tableName.trim().length()==0){
			return null;
		}
		tableName = tableName.trim();
		 
		//1. 从检查本地是否存在指定名称的代码表
		String fileName = tableName + ".xml";
		fileName = fileName.toUpperCase();
		boolean flag = this.fileIsExists(fileName);
		
		//2. 文件不存在，从服务器下载！
		if(!flag){//
			//构建request字符串
			String uri = createHttpRequestURI();
			String parameter = "tablenames=" + tableName.toLowerCase() + "&versions=-1";
			boolean success = getDataFromWebServer(uri, parameter);
			if(!success){
				logger.log("error: 从服务器读取代码表失败！ tablename = " + tableName);
				return null; // 下载失败！：-（
			}
		}
		
		//3. 从本地硬盘上读取文件并返回
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
			logger.log("error: 从本地缓存文件读取代码表失败！ tablename = " + tableName, e);
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
			logger.log("error: 从本地缓存文件读取代码表失败！ tablename = " + fileName, e);
			return null;
		}
		
	}
	
	/**
	 * 和web服务器中的缓存信息同步
	 *
	 */
	public void updateCacheTable(){
		//1. 取本地缓存表名列表
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
		
		//构建request字符串
		String uri = createHttpRequestURI();
		String parameter = "tablenames=" + tablenames + "&versions=" + versions;

		logger.log("uri---------->"+uri);
		logger.log("parameter---------->"+parameter);
        getDataFromWebServer(uri, parameter);

	}
	
	
	//---------------------------------------------------------------------------------- private方法
	/**
	 *  从web服务器下载指定的数据
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
			
			//过滤头信息
			byte[] head = this.getHttpResponseHead(is);
			int pos = this.KMPIndex(head, "\r\n".getBytes(), 0);
			String firstLine = new String(head,0,pos);
            logger.log("firstLine------------->"+firstLine);
            if(!firstLine.endsWith("200 OK")){
				throw new Exception("服务器返回异常，描述信息为： " + firstLine);
			}

			//取数据长度
			byte[] mode = "Content-Length:".getBytes();
			int pos0 = this.KMPIndex(head, mode, 0);
			int pos1 = this.KMPIndex(head,"\r\n".getBytes(), pos0);
			String strLength = new String(head, pos0 + mode.length, pos1-pos0-mode.length).trim();
			int dataLength = Integer.parseInt(strLength);
			if(dataLength == 0){
				logger.log("info: 代码表更新： 服务器端没有需要更新的数据！");
				return true;
			}
			
			//取文件数据
			byte[] buffer = new byte[10240];
			byte[] data = new byte[0]; //文件对应的数据缓冲区
			int len = is.read(buffer);
			int lengthCounter = 0;
			lengthCounter = len;
			while(len > 0){
				int posB = 0;
				int posE = KMPIndex(buffer, CACHE_DATA_BOUNDARY.getBytes(), posB);
				while(posE >= 0){
					//把从当前起始点到posE的数据放入
					byte[] tmp = new byte[data.length + posE-posB];
					System.arraycopy(data, 0, tmp, 0, data.length);
					System.arraycopy(buffer, posB, tmp, data.length, posE-posB);
					data = tmp;
					
					//文件接收结束，输出该文件！ 
					saveCacheTable(data);
					
					//清空文件数据缓冲区
					data = new byte[0];
					
					//定位下一个文件的数据段
					posB = posE + CACHE_DATA_BOUNDARY.getBytes().length;
					posE = KMPIndex(buffer, CACHE_DATA_BOUNDARY.getBytes(), posB);
				}
				
				//没有找到CACHE_DATA_BOUNDARY，则将当前buffer中的数据除去边界长度后放入文件数据缓冲区
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
			logger.log("error: 访问服务器失败！ uri=" + uri + ", parameter=" + parameter, e);
			
			return false;
		}
	}

	/**
	 * 保存从服务器端下载的数据
	 * 
	 * @param data 
	 */
	private void saveCacheTable(byte[] data){
		//1. 取表名
		int pointer =0;
		String tablename = new String(data, pointer, 20);
		tablename = tablename.trim();
		
		//2. 取版本号
		pointer += 20;
		String version = new String(data, pointer, 5);
		version = version.trim();
		int ver = Integer.parseInt(version);
		
		//3. 取文件数据
		pointer += 5;
		
		//4. 写文件
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
	 * 更新版本信息列表
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
		
		logger.log("info: 下载新的缓存文件： tablename=" + tablename + ", version=" + version);
		
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
			logger.log("error: 保存版本列表信息到文件 versionList.lst 失败！", e);
		}
	}
	
	/**
	 * 创建HTTP请求头信息
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
	 * 取HTTP返回数据的头信息
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
					//TODO: 网络出现异常！
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
	 * 创建HTTP请求的URI字符串
	 * 
	 * @return
	 */
	private String createHttpRequestURI(){
		String uri = "http://" + this.serverip + ":" + this.serverport + this.managerServletURI;
		
		return uri;
	}
	
	/** 
	 * 判断指定的文件是否存在
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
	 * KMP模式匹配算法
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
	 * KMP模式匹配算法的next模式算子
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
	 * 启动缓存同步监控线程
	 *
	 */
	private void startUpdateMonitor(){
		this.monitorThread = new UpdateMonitor();
		this.monitorThread.setDaemon(true);
		this.monitorThread.start();
		
	}

	/**
	 * 缓存同步监控线程
	 * 
	 * @author lfw
	 *
	 */
	class UpdateMonitor extends Thread{
		public void run(){
			if(updateCyc == -1){
				logger.log("info: 自动更新周期没有设置，系统采用默认的更新周期： cyc=60 分钟");
				updateCyc = 60; //分钟
			}

			while(true){
				try {
					//double rnd = Math.random();
					long cyc = (long)(updateCyc * 60 * 1000 * 5);
					sleep(cyc); 
				} catch (InterruptedException e) {
					logger.log("error: 代码表缓存管理同步线程出现异常！", e);
				}
				
				updateCacheTable();
			}
		}
	}

}





















































