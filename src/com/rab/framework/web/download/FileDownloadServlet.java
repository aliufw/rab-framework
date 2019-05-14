package com.rab.framework.web.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileDownloadServlet extends HttpServlet {
	
	/**
	 * 序列化编号
	 */
	private static final long serialVersionUID = 6317276729477092677L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws
	    ServletException, IOException {
		ServletOutputStream out = response.getOutputStream();
		FileInputStream in = null;
		String uri = request.getParameter("uri"); //附件名称路径
		String fileName = uri;
	    try {
	    	if(uri == null || uri.trim().length() ==0){
	    		return;
	    	}
	    	
	    	if(uri.lastIndexOf("/") > 0){
	    		fileName = uri.substring(uri.lastIndexOf("/")+1);
	    	}
	    	String realPath = request.getRealPath(uri);
	    	File file = new File(realPath);
	    	
	    	if(!file.exists()){
	    		response.sendRedirect("downloaderror.jsp");
	    		return;
	    	}

	    	fileName = charSetConvert(fileName, "GBK", "iso-8859-1");
	    	
	        response.setContentType("application/octet-stream");
	        response.setHeader("Content-Disposition","filename=" + fileName);
	        response.setHeader("Content-Length","" + file.length());
	        
	        in = new FileInputStream(file);
	        byte[] buffer = new byte[10240];
	        int len = in.read(buffer);
	        while(len > 0){
	        	out.write(buffer, 0, len);
	        	len = in.read(buffer);
	        }
			out.flush();
			out.close();
	    }
	    catch (Exception ex) {
	        System.out.println("文件下载时出现异常! 文件名 = " + uri);
	        ex.printStackTrace();
	        response.sendRedirect("downloaderror.jsp");
	    }
	    finally{
	    	if(in != null){
	    		in.close();
	    	}
	    }
	}
	
	private String charSetConvert(String src, String fromCharSet, String toCharSet) {
		if (src == null) {
			return src;
		}
		try {
			return new String(src.getBytes(fromCharSet), toCharSet);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
