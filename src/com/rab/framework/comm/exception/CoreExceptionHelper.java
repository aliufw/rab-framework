package com.rab.framework.comm.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rab.framework.comm.appcontext.ApplicationContext;
import com.rab.framework.comm.util.StringUtils;

/**
 * 
 * <P>Title: VHExceptionHelper</P>
 * <P>Description: </P>
 * <P>����˵�����쳣���������</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class CoreExceptionHelper implements Serializable {

	private static final long serialVersionUID = -8420003610577985529L;
	
    /**
     * �����ļ����쳣���������ʼ��־
     */
    public static final String KEY_FLAG_START = "{";

    /**
     * �����ļ����쳣�������������־
     */
    public static final String KEY_FLAG_END = "}";

	/**
	 * ������쳣��
	 */
	String code;

	/**
	 * ����������Ӧ�Ĳ��������ڴ������ļ��ж�ȡ����ʱ����̬�滻���������
	 */
	List<String> params = new ArrayList<String>();

	static final String FILETEMPLET_NAME = "exception";

	private String exceptionStackInfo = "";

	private String exceptionContent = null;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void addParam(String param) {
		this.params.add(param);
	}

	public void addParam(List<String> param) {
		this.params.addAll(param);
	}

	public List<String> getParam() {
		return this.params;
	}

	public void cleanParam() {
		this.params.clear();
	}

	public String getContent() {
		if (this.exceptionContent != null) {
			return this.exceptionContent;
		}

		String original = ApplicationContext.singleton().getExceptionMsg(this.code);
		exceptionContent = makeContent(original, this.params);

		return exceptionContent;
	}
	/**
	 * ���쳣�����ļ����ֵı����滻Ϊ�����ֵ
	 * 
	 * @param original
	 *            String ԭʼ�������ļ��ж�����ַ���
	 * @param param
	 *            ArrayList �����滻��ʼ����ֹ��־����ǵĲ����ض�
	 * @return String ���յ��ַ���
	 */
	private String makeContent(String original, List<String> param) {
		Map<String,Integer> flagMap = new HashMap<String,Integer>();

		if(original == null){
			original = "";
		}
		int iStart = 0;
		// �����ַ��������еı��
		while ((iStart = original.indexOf(KEY_FLAG_START, iStart + 1)) != -1) {
			int iEnd = original.indexOf(KEY_FLAG_END, iStart);
			char aChar = original.charAt(iStart);
			if (aChar != '\\') {
				String resCode = original.substring(iStart + 1, iEnd);
				flagMap.put("s" + resCode, new Integer(iStart));
				flagMap.put("e" + resCode, new Integer(iEnd));
			}
		}

		// �ø����������滻��Ƿ�
		for (int i = param.size() - 1; i >= 0; i--) {
			String resValue = (String) param.get(i);
			if (flagMap.get("s" + (i + 1)) != null) {
				int i_Start = ((Integer) flagMap.get("s" + (i + 1))).intValue();
				int i_End = ((Integer) flagMap.get("e" + (i + 1))).intValue();
				original = StringUtils.replaceByPos(original, i_Start, i_End,
						resValue);
			}
		}
		
		return original;
	}
	
	
	public void parseExceptionStackInfo(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		if (ex == null) {
			return;
		}

		sb.append(ex.getClass().getName()).append(":").append(ex.getMessage()).append("\r\n");
		StackTraceElement[] stack = ex.getStackTrace();
		for (int i = 0; i < stack.length; i++) {
			sb.append("\t").append(stack[i]).append("\r\n");
		}

		this.exceptionStackInfo = sb.toString();
	}

	public String getExceptionStackInfo() {
		return this.exceptionStackInfo;
	}

}