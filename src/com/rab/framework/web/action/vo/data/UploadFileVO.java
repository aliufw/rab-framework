package com.rab.framework.web.action.vo.data;

public class UploadFileVO implements java.io.Serializable{

	/**
	 * 序列化编号
	 */
	private static final long serialVersionUID = -3738111774303895559L;
	
	/**
	 * 上传附件临时存放的session变量名称
	 */
	public static final String tmpCacheName = "MAP-UPLOADED-FILES";
	/**
	 * 文件名称
	 */
	private String fileName;

	/**
	 * 文件标示，计算用
	 */
	private String fileId;

	/**
	 * 文件大小
	 */
	private long fileSize;

	/**
	 * 文件内容数据
	 */
	private byte[] fileContent;

	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
}
