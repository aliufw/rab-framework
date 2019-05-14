/**
 * 作者：刘付伟
 *
 * 功能：扩展文件上传组件，以实现上传附件的发送
 * 实现方法：扩展接口javax.activation.DataSource
 * 相关包：mail.jar,activation.jar
 */
package com.rab.framework.web.upload.smartupload;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;


public class UploadDataSource
    implements DataSource
{

    public UploadDataSource(SmartUpload upload)
    {
        this.upload = upload;
        file = upload.getFiles().getFile(0);
    }

    public InputStream getInputStream()
        throws IOException
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(upload.m_binArray, file.getStartData(), file.getSize());
        return bais;
    }

    public OutputStream getOutputStream()
        throws IOException
    {
        FileOutputStream out = new FileOutputStream(String.valueOf(String.valueOf(file.getFileName())));
        return out;
    }

    public String getContentType()
    {
        return file.getContentType();
    }

    public String getName()
    {
        return file.getFileName();
    }

    public int getFileSize()
    {
        return file.getSize();
    }

    UploadFile file;
    SmartUpload upload;
}
