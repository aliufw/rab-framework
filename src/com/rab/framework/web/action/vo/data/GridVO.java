package com.rab.framework.web.action.vo.data;

import com.rab.framework.comm.pagination.PaginationMetaData;

public class GridVO extends DataVO {
	private static final long serialVersionUID = -1904092094585917606L;
	private String beanName;
	private PaginationMetaData pageInfo;
	private TDSVO[] trs;

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public TDSVO[] getTrs() {
		return trs;
	}

	public void setTrs(TDSVO[] trs) {
		this.trs = trs;
	}

	/**
	 * @return the pageInfo
	 */
	public PaginationMetaData getPageInfo() {
		return pageInfo;
	}

	/**
	 * @param pageInfo
	 *            the pageInfo to set
	 */
	public void setPageInfo(PaginationMetaData pageInfo) {
		this.pageInfo = pageInfo;
	}

}
