package com.rab.framework.comm.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;

/**
 * 
 * <P>Title: FileUtils</P>
 * <P>Description: </P>
 * <P>程序说明：文件处理工具包</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class FileUtils {
	
	private final static LogWritter logger = LogFactory.getLogger(FileUtils.class);
	
    private String loadFilename;

    public FileUtils() {
    }

    public FileUtils(String loadFilename) {
        this.loadFilename = loadFilename;
    }

    public String getAbsolutePath(String loadFilename) {
        ClassLoader loader = this.getClass().getClassLoader();
        URL url = loader.getResource(loadFilename);
        return url.getPath();

    }

    public URL getFileURL(){
    	ClassLoader loader = this.getClass().getClassLoader();
        URL url = loader.getResource(loadFilename);
        return url;
    }
    
    public String getAbsolutePath() {
        return getFileURL().getPath();
    }

    public InputStream getInputStream() {
        ClassLoader loader = this.getClass().getClassLoader();
        URL url = loader.getResource(loadFilename);
        try {
        	if(url!=null)
            return url.openStream();
        } catch (IOException ex) {
            logger.error("打开配置文件错误，文件名：" + this.loadFilename, ex);
        }
        return null;

    }

}
