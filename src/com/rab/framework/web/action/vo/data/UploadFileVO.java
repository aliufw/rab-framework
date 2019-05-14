package com.rab.framework.web.action.vo.data;

public class UploadFileVO implements java.io.Serializable{

	/**
	 * ���л����
	 */
	private static final long serialVersionUID = -3738111774303895559L;
	
	/**
	 * �ϴ�������ʱ��ŵ�session��������
	 */
	public static final String tmpCacheName = "MAP-UPLOADED-FILES";
	/**
	 * �ļ�����
	 */
	private String fileName;

	/**
	 * �ļ���ʾ��������
	 */
	private String fileId;

	/**
	 * �ļ���С
	 */
	private long fileSize;

	/**
	 * �ļ���������
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
