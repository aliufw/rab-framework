package com.rab.framework.web.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.util.RandomStringUtils;
import com.rab.framework.comm.util.StringUtils;
import com.rab.framework.web.action.vo.data.UploadFileVO;

public class UploadFileServlet extends HttpServlet {

	/**
	 * ���л����
	 */
	private static final long serialVersionUID = 7263150011668509844L;

	/**
	 * ��־��¼��
	 */
	protected static final LogWritter logger = LogFactory.getLogger(UploadFileServlet.class);

	/**
	 * �Ƿ�洢������ʱ�ļ�
	 */
	private boolean isSaveToDiskTmp = false;
	
	/**
	 * �ϴ���������ʱ���Ŀ¼
	 */
	private String tmpDir = null;
	
	/**
	 * �ϴ��ļ���С���ƣ���󲻳���10M�ֽ�
	 */
	private int maxSize = 10;
	
    public void init() throws ServletException {
    	if(isSaveToDiskTmp){
        	this.tmpDir = (String)ApplicationContext.singleton().getValueByKey("uploadfile-tempdir");
    		if(this.tmpDir == null || tmpDir.length()==0){
    			this.tmpDir = ".";
    		}
    		File root = new File(this.tmpDir);
    		if(!root.exists()){
    			root.mkdirs();
    		}
    		logger.debug("tmpDir = " + root.getAbsolutePath());
    		
    		String strMaxSize = (String)ApplicationContext.singleton().getValueByKey("uploadfile-maxsize");
    		if(strMaxSize != null){
    			try {
					maxSize = Integer.parseInt(strMaxSize.trim());
				} catch (NumberFormatException e) {
					logger.error("�ϴ��ļ���С�������ò���������������ȷ�����֣�", e);
				}
    		}
    	}
    }
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		
		String methodflag = req.getParameter("methodflag");
		
		if(methodflag!=null && methodflag.equalsIgnoreCase("delete")){
			fileDelete(req, resp);
		}
		else{
			fileUpload(req, resp);
		}
	}
	
	private void fileUpload(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	    try {
			//Ϊ�����󴴽�һ��DiskFileItemFactory����ͨ��������������ִ�н��������еı���Ŀ��������һ��List�С�
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setSizeMax(maxSize*1024*1024); // ���������û��ϴ��ļ���С,��λ:�ֽ�
			
			List<FileItem> items = upload.parseRequest(req);
			
		    String json = "";
		    Iterator<FileItem> iter = items.iterator(); // ���δ���ÿ���ϴ����ļ�
		    while (iter.hasNext()) {
		        FileItem item = (FileItem) iter.next();// �������������ļ�������б���Ϣ

		        if (!item.isFormField()) {
		        	UploadFileVO uf = this.createUploadFile(item);
		        	
		        	this.save(uf, req);
		        	
		           	String s1 = "����";
		           	String s2 = uf.getFileName();
		           	System.out.println("s1 = " + s1);
		           	System.out.println("s2 = " + s2);
		           	
//		           	s2 = StringUtils.gbkToISO(s2);
		           	s2 = StringUtils.gbkToUTF8(s2); 
//		           	s2 = StringUtils.isoToUTF8(s2);
//		           	s2 = StringUtils.isoToGBK(s2);
//		           	s2 = StringUtils.utftoGBK(s2);
//		           	s2 = StringUtils.utftoISO(s2);
		           	
		           	System.out.println("s2 = " + s2);
		           	
		            logger.debug(uf.getFileName() + "    Size=" + uf.getFileSize());//����ϴ��ļ���Ϣ
		           	json = "data:{" + "name:'"+ s2 + "',size:'" + formatFileSize(item.getSize()) + "',del:'delete'" + ",fileid:'" + uf.getFileId() + "'}";
		        }
		    }
		    	logger.debug("json = " + json);

		    	//json = StringUtils.gbkToISO(json);
		    	//json = StringUtils.utftoISO(json);
		    	//json = StringUtils.utftoGBK(json);
		    	
		    	//json = StringUtils.isoToUTF8(json);
		    	//json = StringUtils.gbkToUTF8(json);
		    	
		    	//json = StringUtils.isoToGBK(json);
		    	
		    	json = "{success:true," + json + "}";
		    	logger.debug("json = " + json);
		    	//System.out.println(json);
		    	resp.getOutputStream().write(json.getBytes());
		    	
//		    resp.getWriter().print(json); 
			
		} catch (Exception e) {
			logger.error("�ļ��ϴ�����ʧ�ܣ�", e);
			String json = "{success:false}";
			resp.getWriter().print(json);
		}
	    
	}
	
	private void fileDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String fileid = req.getParameter("fileid");
		Map<String, UploadFileVO> upfiles = (Map<String, UploadFileVO>)req.getSession().getAttribute(UploadFileVO.tmpCacheName);
		if(upfiles == null){
			upfiles = new HashMap<String, UploadFileVO>();
			req.getSession().setAttribute(UploadFileVO.tmpCacheName, upfiles);
		}
		
		upfiles.remove(fileid);
		
		logger.debug("ɾ������ " + fileid + " �ɹ���");
		
		
		String json = "{success:true}";
		resp.getWriter().print(json);
		
	}

	private UploadFileVO createUploadFile(FileItem item){
		UploadFileVO uf = new UploadFileVO();
		
		uf.setFileContent(item.get());
		uf.setFileSize(item.getSize());
		
		String name = item.getName();//��ȡ�ϴ��ļ���,����·��
        name = name.substring(name.lastIndexOf("\\")+1);//��ȫ·������ȡ�ļ���
        
		uf.setFileName(name);
		
		uf.setFileId("upload_"+ RandomStringUtils.randomAlphabetic(10));
		
		return uf;
	}
	
	private void save(UploadFileVO uf, HttpServletRequest req) throws Exception{
		if(isSaveToDiskTmp){
			//���渽��
			File root = new File(this.tmpDir);
			File file = new File(root, this.createRandom());
			FileOutputStream fo = new FileOutputStream(file);
			fo.write(uf.getFileContent());
			fo.flush();
			fo.close();
		}
		
		//��������Ϣ��¼��session��
		Map<String, UploadFileVO> upfiles = (Map<String, UploadFileVO>)req.getSession().getAttribute(UploadFileVO.tmpCacheName);
		if(upfiles == null){
			upfiles = new HashMap<String, UploadFileVO>();
			req.getSession().setAttribute(UploadFileVO.tmpCacheName, upfiles);
		}

		upfiles.put(uf.getFileId(), uf);
	}
	
	private String createRandom(){
		return RandomStringUtils.randomAlphabetic(10);
	}
	
	private String formatFileSize(long size){
		String s = "" + size;
		
		long tmp = size % 1000;
		
		s = format(tmp);
		
		size = size /1000;
		while(size > 0){
			tmp = size % 1000;
			size = size / 1000;
			if(size ==0){
				s = tmp + "," + s;
			}
			else{
				s = format(tmp) + "," + s;
			}
		}
		
		return s;
	}
	
	private String format(long num){
		String stmp = "";
		if(num  == 0){
			stmp = "000";
		}
		else if(num < 10){
			stmp = "00" + num;
		}
		else if(num < 100){
			stmp = "0" + num;
		}
		else{
			stmp = "" + num;
		}
		
		return stmp;
	}

}
