package com.rab.framework.comm.util;

import net.sf.json.JSONObject;

import com.rab.framework.comm.exception.ExceptionInfo;
import com.rab.framework.web.action.vo.ResComponentVO;
import com.rab.framework.web.action.vo.ResExceptionVO;


/**
 * 
 * <P>Title: ExceptionUtils</P>
 * <P>Description: </P>
 * <P>程序说明：处理异常信息</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author jingyang</P>
 * <P>version 1.0</P>
 * <P>2010-7-20</P>
 *
 */
public class ExceptionUtils {
    /**
     * 得到传入参数的具体类型
     *
     * @param e Exception
     * @return String[]
     * 				0:Exception name
     * 				1:Exception cause
     */
	private static String[] parseException(Exception e) {
		if (e == null) {
			return null;
		}
		String[] result = new String[2];
		String rootName = "";
		String rootMsg = "";
		Throwable ex = (Throwable) e;
		while (true) {
			if (ex.getCause() == null) {
				rootMsg = ex.getMessage();
				rootName = ex.getClass().getName();
				break;
			} else if (ex.getCause().getCause() == null) {
				rootMsg = ex.getCause().getMessage();
				rootName = ex.getCause().getClass().getName();
				break;
			} else {
				ex = ex.getCause();
			}
		}
		result[0] = rootName;
		result[1] = rootMsg;

		return result;
	}

	public static String transExceptionInfoToJson(Exception e) {
		String resJson = null;
		ResExceptionVO resExceptionVO = new ResExceptionVO();
		String[] exceptionInfo = parseException(e);
		if (exceptionInfo != null && exceptionInfo.length == 2) {
			resExceptionVO.setExceptionName(exceptionInfo[0]);
			resExceptionVO.setExceptionMsg(exceptionInfo[1]);
		}
		try {
			resJson = JSONObject.fromObject(resExceptionVO).toString();
		} catch (Exception e1) {
			resJson = getFormatExceptionJson(e1);
		}
		return resJson;
	}
	
	public static ResComponentVO transExceptionInfo(Exception e) {
		ResExceptionVO resExceptionVO = new ResExceptionVO();
		String[] exceptionInfo = parseException(e);
		if (exceptionInfo != null && exceptionInfo.length == 2) {
			resExceptionVO.setExceptionName(exceptionInfo[0]);
			resExceptionVO.setExceptionMsg(exceptionInfo[1]);
		}
		return resExceptionVO;
	}

	public static String transExceptionInfoToJson(ExceptionInfo exceptionInfo) {
		String resJson = null;
		ResExceptionVO resExceptionVO = new ResExceptionVO();
		resExceptionVO.setExceptionMsg(exceptionInfo.getExceptionMsg());
		resExceptionVO.setExceptionName(exceptionInfo.getExceptionCode());

		try {
			resJson = JSONObject.fromObject(resExceptionVO).toString();
		} catch (Exception e1) {
			resJson = getFormatExceptionJson(e1);
		}
		return resJson;
	}
	
	public static ResComponentVO transExceptionInfo(ExceptionInfo exceptionInfo) {
		ResExceptionVO resExceptionVO = new ResExceptionVO();
		resExceptionVO.setExceptionMsg(exceptionInfo.getExceptionMsg());
		resExceptionVO.setExceptionName(exceptionInfo.getExceptionCode());
		return resExceptionVO;
	}

	private static String getFormatExceptionJson(Exception e) {
		return "{'exception': true,'data': {},'exceptionName':'格式转换异常','exceptionMsg':"
				+ e.getMessage() + "}";
	}    	
}
