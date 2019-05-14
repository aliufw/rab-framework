package com.rab.framework.component.scheduler;

import java.util.List;

import com.rab.framework.comm.exception.BaseCheckedException;

/**
 * 
 * <P>Title: SchedulerException</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>监听组件异常类</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-2</P>
 *
 */
public class SchedulerException extends BaseCheckedException {

	private static final long serialVersionUID = 8225356947589160506L;

	/**
     * @param code
     */
    public SchedulerException(String code) {
        super(code);
    }

    /**
     * @param code
     * @param ex
     */
    public SchedulerException(String code, Throwable ex) {
        super(code, ex);
    }

    /**
     * @param code
     * @param params
     */
    public SchedulerException(String code, List<String> params) {
        super(code, params);
    }

    /**
     * @param code
     * @param params
     * @param ex
     */
    public SchedulerException(String code, List<String> params, Throwable ex) {
        super(code, params, ex);
    }
}
