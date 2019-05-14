package com.rab.framework.comm.pagination;

import com.rab.framework.comm.dto.vo.BaseValueObject;

public class PaginationMetaData extends BaseValueObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3381569011394798357L;

	/**
	 * 排序模式：升序
	 */
	public static String SORT_MODEL_ASC = "ASC";
	
	/**
	 * 排序模式：降序
	 */
	public static String SORT_MODEL_DESC = "DESC";
	
	/**
	 * 存储模式：session
	 */
	public static String PAGE_MODEL_SESSION = "SESSION";
	
	/**
	 * 初始序号，从"0"开始
	 */
	int pageIndex = 0;
	
	/** 
	 * 每页数据行数
	 */
	int rowsPerPage=20;
	
	/**
	 * 查询结果集总行数
	 */
	int totalRowNum;

	/**
	 * 用于排序的字段名称
	 */
	String sortFieldName;
	
	String queryParams;
	
	/**
	 * 排序顺序标记
	 */
	String sortFlag;	
	
	/**
	 * 分页模式标记
	 */
	String pageModelFlag;
	
	/**
	 * 分页模式标记
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
