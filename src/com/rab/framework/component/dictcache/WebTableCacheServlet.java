package com.rab.framework.component.dictcache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;

/**
 * 
 * <P>Title: WebCodeCacheServlet</P>
 * <P>Description: </P>
 * <P>����˵�������������web�˴������</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-16</P>
 *
 */
public class WebTableCacheServlet extends HttpServlet {
	/**
	 * ���л����
	 */
	private static final long serialVersionUID = 7111829713673537104L;
	
	private static String CACHE_DATA_BOUNDARY = "----------CacheDataBoundary";
    /**
     * ��־��¼����
     */
	protected static final LogWritter log = LogFactory.getLogger(WebTableCacheServlet.class);

    public void init() throws ServletException {
    	String className = ServerCacheManager.class.getName();
    	try {
    		log.debug("��ʼ����web�˻������� .................................");
			Class<?> claz = Class.forName(className);
			Class<?>[] paramTypes = {(new String[0].getClass())};
			Method method = claz.getMethod("main", paramTypes);
			Object[] param = {null};
			method.invoke(null, param);

    		log.debug("web�˻������ݼ������!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			
		} catch (Exception e) {
			log.error("web�˻�����س����쳣��", e);
		} 
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws
    ServletException, IOException {
    	doGet(request, response);
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws
    ServletException, IOException {
        log.debug("��WEB���������ػ�������");
        String method = request.getParameter("method");
        log.debug("method = " + method);
        if("get_web_cache".equalsIgnoreCase(method)){
    		this.getWebCache(request, response);
    		return ;
    	}
    	log.debug("��WEB�������ķ������ļ��м��� ...................");
    	String tablenames = request.getParameter("tablenames");
    	String versions = request.getParameter("versions");
    	
    	ServletOutputStream out = response.getOutputStream();
    	response.setContentType("text/plan");
        response.setHeader("Content-Disposition","data.txt");
        
    	ServerCacheManager manager = (ServerCacheManager)ServerCacheManager.getDictCacheManager();
    	Map<String,CacheTable> pool = manager.getCodeCachePool();
        StringTokenizer stTable  = new StringTokenizer(tablenames,",");
        StringTokenizer stVersion = new StringTokenizer(versions,",");
        long datalength = 0;
        while(stTable.hasMoreElements()){
    		String tablename = "" + stTable.nextElement();
    		String version   = "" + stVersion.nextElement();
    		
        	ServerCacheManager scm = (ServerCacheManager)ServerCacheManager.getDictCacheService();
        	if(!scm.isUseMemoryCache()){
    			scm.getDataFromServer(tablename, null);
    		}
    		
    		int ver = Integer.parseInt(version);
    		CacheTable cacheTable = (CacheTable)pool.get(tablename.toUpperCase());
    		
    		if(ver >= cacheTable.getVersion()){
    			continue;
    		}
    		
    		long filesize = getFileSize(tablename);
    		datalength += 20;  //����
    		datalength += 5;   //�汾��Ϣ����
    		datalength += filesize; //���ݱ��峤��
    		datalength += CACHE_DATA_BOUNDARY.length(); //�߽��ַ�������
        }
        
        response.setHeader("Content-Length", "" + datalength);
        
        stTable   = new StringTokenizer(tablenames,",");
    	stVersion = new StringTokenizer(versions,",");
    	
    	while(stTable.hasMoreElements()){
    		String tablename = "" + stTable.nextElement();
    		String version   = "" + stVersion.nextElement();
    		int ver = Integer.parseInt(version);
    		
    		CacheTable cacheTable = (CacheTable)pool.get(tablename.toUpperCase());
    		
    		if(ver >= cacheTable.getVersion()){
    			continue;
    		}

    		byte[] bname = new byte[20];
    		for(int i=0; i<bname.length; i++){
    			bname[i] = ' ';
    		}
    		byte[] tmp = tablename.toUpperCase().getBytes();
    		System.arraycopy(tmp, 0, bname, 0, tmp.length);
    		
    		byte[] data = this.getdata(tablename);
    		byte[] bver = this.getversion(tablename);
    		
    		out.write(bname);
            out.write(bver);
            out.write(data);
            out.write(CACHE_DATA_BOUNDARY.getBytes());
    	}

        
        out.flush();
    	out.close();
    } 
    
    private void getWebCache(HttpServletRequest request, HttpServletResponse response) 
    	throws ServletException, IOException {
    	
    	String tablename = request.getParameter("tablename");
    	log.debug("�����ֱ�Ӵӷ������˻�ȡ��������ļ��� tablename = " + tablename + ".xml");
    	
    	ServletOutputStream out = response.getOutputStream();
    	response.setContentType("text/plan");
        response.setHeader("Content-Disposition", tablename + ".xml");

    	ServerCacheManager scm = (ServerCacheManager)ServerCacheManager.getDictCacheService();
    	if(!scm.isUseMemoryCache()){
			scm.getDataFromServer(tablename, null);
		}

    	long filesize = getFileSize(tablename);
    	
    	response.setHeader("Content-Length", "" + filesize);
    	
    	byte[] data = this.getdata(tablename);
        out.write(data);
        out.flush();
    	out.close();
    }
    
    private byte[] getdata(String tablename){
		try {
			byte[] data = new byte[0];
	    	Properties props = (Properties)ApplicationContext.singleton().getValueByKey("codecache");
			String strLocalXMLCacheDir = props.getProperty("local-xml-cache-dir");
			
			File file = new File(strLocalXMLCacheDir, tablename.toUpperCase() + ".xml");
			log.debug("��ȡ���ػ����ļ��� " + file.getAbsolutePath());
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
			return data;
			
		} catch (Exception e) {
			log.error("��ȡ�������˱��ػ��������ļ�ʱ�����쳣��",e);
			return new byte[0];
		}
    }
    
    private long getFileSize(String tablename){
    	Properties props = (Properties)ApplicationContext.singleton().getValueByKey("codecache");
		String strLocalXMLCacheDir =  props.getProperty("local-xml-cache-dir");
		
		File file = new File(strLocalXMLCacheDir, tablename.toUpperCase() + ".xml");
		return file.length();
    }
    
    private byte[] getversion(String tablename){
    	ServerCacheManager manager = (ServerCacheManager)ServerCacheManager.getDictCacheManager();
    	Map<String,CacheTable> pool = manager.getCodeCachePool();
    	CacheTable cacheTable = (CacheTable)pool.get(tablename.toUpperCase());
    	
    	byte[] ver = new byte[5];
    	for(int i=0; i<ver.length; i++){
    		ver[i] = ' ';
    	}
    	
    	String version = "-1";
    	if(cacheTable != null){
    		version = "" + cacheTable.getVersion();
    	}
    	byte[] bversion = version.getBytes();
    	System.arraycopy(bversion, 0, ver, 0, bversion.length);
    	
    	return ver;
    }
}

