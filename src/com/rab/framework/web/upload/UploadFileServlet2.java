package com.rab.framework.web.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.util.RandomStringUtils;
import com.rab.framework.comm.util.StringUtils;
import com.rab.framework.web.action.vo.data.UploadFileVO;
import com.rab.framework.web.upload.smartupload.SmartUpload;
import com.rab.framework.web.upload.smartupload.UploadFile;
import com.rab.framework.web.upload.smartupload.UploadFiles;

public class UploadFileServlet2 extends HttpServlet {

	/**
	 * 序列化编号
	 */
	private static final long serialVersionUID = 7263150011668509844L;

	/**
	 * 日志记录器
	 */
	protected static final LogWritter logger = LogFactory.getLogger(UploadFileServlet2.class);

	/**
	 * 是否存储磁盘临时文件
	 */
	private boolean isSaveToDiskTmp = false;
	
	/**
	 * 上传附件的临时存放目录
	 */
	private String tmpDir = null;
	
	/**
	 * 上传文件大小限制，最大不超过10M字节
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
					logger.error("上传文件大小限制配置参数有误，请配置正确的数字！", e);
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
	    String json = "";
		
	    try {
	    	SmartUpload su = new SmartUpload();
	    	su.initialize(req, resp, maxSize*1024*1024);
	    	su.upload();
	    	
	    	UploadFiles files = su.getFiles();
	    	for(int i=0; i<files.getCount(); i++){
	    		UploadFile upfile =files.getFile(i);
	    		
	    		UploadFileVO uf = new UploadFileVO();
	    		
	    		uf.setFileSize(upfile.getSize());
	    		uf.setFileName(upfile.getFileName());
	    		byte[] data = new byte[upfile.getSize()];
        		for(int k=0; k<upfile.getSize(); k++){
    				data[k] = upfile.getBinaryData(k);
    			}
	    		uf.setFileId("upload_"+ RandomStringUtils.randomAlphabetic(10));
	    		
	            logger.debug(uf.getFileName() + "    Size=" + uf.getFileSize());//输出上传文件信息
	            
	            String s2 = StringUtils.gbkToUTF8(uf.getFileName()); 
	            
	           	json = "data:{" + "name:'"+ s2 + "',size:'" + formatFileSize(uf.getFileSize()) + "',del:'delete'" + ",fileid:'" + uf.getFileId() + "'}";

	    	}
	    	json = "{success:true," + json + "}";
	    	logger.debug("json = " + json);
	    	resp.getOutputStream().write(json.getBytes());
			
		} catch (Exception e) {
			logger.error("文件上传处理失败！", e);
			json = "{success:false}";
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
		
		logger.debug("删除附件 " + fileid + " 成功！");
		
		
		String json = "{success:true}";
		resp.getWriter().print(json);
		
	}

	private UploadFileVO createUploadFile(FileItem item){
		UploadFileVO uf = new UploadFileVO();
		
		uf.setFileContent(item.get());
		uf.setFileSize(item.getSize());
		
		String name = item.getName();//获取上传文件名,包括路径
        name = name.substring(name.lastIndexOf("\\")+1);//从全路径中提取文件名
        
		uf.setFileName(name);
		
		uf.setFileId("upload_"+ RandomStringUtils.randomAlphabetic(10));
		
		return uf;
	}
	
	private void save(UploadFileVO uf, HttpServletRequest req) throws Exception{
		if(isSaveToDiskTmp){
			//保存附件
			File root = new File(this.tmpDir);
			File file = new File(root, this.createRandom());
			FileOutputStream fo = new FileOutputStream(file);
			fo.write(uf.getFileContent());
			fo.flush();
			fo.close();
		}
		
		//将附件信息记录到session中
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
