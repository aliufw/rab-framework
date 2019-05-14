package com.rab.framework.comm.pagination;

import com.rab.framework.comm.dto.vo.BaseValueObject;

public class PaginationMetaData extends BaseValueObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3381569011394798357L;

	/**
	 * ����ģʽ������
	 */
	public static String SORT_MODEL_ASC = "ASC";
	
	/**
	 * ����ģʽ������
	 */
	public static String SORT_MODEL_DESC = "DESC";
	
	/**
	 * �洢ģʽ��session
	 */
	public static String PAGE_MODEL_SESSION = "SESSION";
	
	/**
	 * ��ʼ��ţ���"0"��ʼ
	 */
	int pageIndex = 0;
	
	/** 
	 * ÿҳ��������
	 */
	int rowsPerPage=20;
	
	/**
	 * ��ѯ�����������
	 */
	int totalRowNum;

	/**
	 * ����������ֶ�����
	 */
	String sortFieldName;
	
	String queryParams;
	
	/**
	 * ����˳����
	 */
	String sortFlag;	
	
	/**
	 * ��ҳģʽ���
	 */
	String pageModelFlag;
	
	/**
	 * ��ҳģʽ���
	 */
	String cacheName;
	
	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public int getTotalRowNum() {
		return totalRowNum;
	}

	public void setTotalRowNum(int totalRowNum) {
		this.totalRowNum = totalRowNum;
	}

	public String getSortFieldName() {
		return sortFieldName;
	}

	public void setSortFieldName(String sortFieldName) {
		this.sortFieldName = sortFieldName;
	}

	public String getSortFlag() {
		return sortFlag;
	}

	public void setSortFlag(String sortFlag) {
		this.sortFlag = sortFlag;
	}

	/**
	 * @return the queryParams
	 */
	public String getQueryParams() {
		return queryParams;
	}

	/**
	 * @param queryParams the queryParams to set
	 */
	public void setQueryParams(String queryParams) {
		this.queryParams = queryParams;
	}

	public String getPageModelFlag() {
		return pageModelFlag;
	}

	public void setPageModelFlag(String pageModelFlag) {
		this.pageModelFlag = pageModelFlag;
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}	
}
