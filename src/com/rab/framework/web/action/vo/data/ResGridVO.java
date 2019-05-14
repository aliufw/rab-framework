package com.rab.framework.web.action.vo.data;

import com.rab.framework.comm.pagination.PaginationMetaData;

public class ResGridVO extends DataVO {
	private static final long serialVersionUID = -7807441951255156254L;
	private ResTDSVO[] trs;
	private PaginationMetaData pageInfo ;
	/**
	 * @return the trs
	 */
	public ResTDSVO[] getTrs() {
		return trs;
	}

	/**
	 * @param trs
	 *            the trs to set
	 */
	public void setTrs(ResTDSVO[] trs) {
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
