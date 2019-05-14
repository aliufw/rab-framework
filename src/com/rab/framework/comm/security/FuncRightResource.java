package com.rab.framework.comm.security;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 
 * <P>Title: MenuNode</P>
 * <P>Description: </P>
 * <P>����˵�����˵������Ľڵ�</P>
 * <P></P> 
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P> 
 * <P>version 1.0</P>
 * <P>2010-8-11</P>
 *
 */
public class FuncRightResource implements Serializable {
	/**
	 * ���л����
	 */
	private static final long serialVersionUID = -4283015971324570262L;

	/**
	 * �˵��ڵ����ͣ�Ŀ¼�ڵ�
	 */
	public static int MENU_NODE_TYPE_DIR = 0;
	
	/**
	 * �˵��ڵ����ͣ�����Ȩ��
	 */
	public static int MENU_NODE_TYPE_MENU = 1;
	
	/**
	 * �˵��ڵ����ͣ���ťȨ��
	 */
	public static int MENU_NODE_TYPE_BUTTON = 2;
	
	/**
	 * ����ģ��
	 */
	private String modCode;
	
	/**
	 * �ڵ���
	 */
	private String funcId;
	
	/**
	 * ���ڵ���
	 */
	private String parentId;
	
	/**
	 * �ڵ�������
	 */
	private int sortId;
	
	/**
	 * �ڵ�Ȩ������
	 */
	private String permName;
	
	/**
	 * �ڵ�Ȩ�ޱ�ʶ
	 */
	private String permId;
	
	/**
	 * <P>�ڵ�����</P>
	 */
	private int funcType;
	
	/**
	 * �˵��ڵ�ĵ���URI�����ڵ�����ΪMENU_NODE_TYPE_DIRʱ����������Ч
	 * 
	 */
	private String funcUri;
	
//	/**
//	 * ���ױ���
//	 */
//	private String copyCode;
//	
	
	public String getModCode() {
		return modCode;
	}



	public void setModCode(String modCode) {
		this.modCode = modCode;
	}



	public String getFuncId() {
		return funcId;
	}



	public void setFuncId(String funcId) {
		this.funcId = funcId;
	}



	public String getParentId() {
		return parentId;
	}



	public void setParentId(String parentId) {
		this.parentId = parentId;
	}



	public int getSortId() {
		return sortId;
	}



	public void setSortId(int sortId) {
		this.sortId = sortId;
	}



	public String getPermName() {
		return permName;
	}



	public void setPermName(String permName) {
		this.permName = permName;
	}



	public String getPermId() {
		return permId;
	}



	public void setPermId(String permId) {
		this.permId = permId;
	}



	public int getFuncType() {
		return funcType;
	}



	public void setFuncType(int funcType) {
		this.funcType = funcType;
	}

	public String getFuncUri() {
		return funcUri;
	}


	public void setFuncUri(String funcUri) {
		this.funcUri = funcUri;
	}


//	public String getCopyCode() {
//		return copyCode;
//	}
//
//
//
//	public void setCopyCode(String copyCode) {
//		this.copyCode = copyCode;
//	}



	/**
	 * ������Ļ�����Ϣ, ����������ƺ���Ļ�������ֵ��Ϣ
	 * @return
	 */
	public String toString() {
		Method[] methods = this.getClass().getMethods();
		StringBuffer ret = new StringBuffer(super.toString());
		ret.append(" = {\r\n");
		for (int i = 0; i < methods.length; i++) {
			try {
				String methodName = methods[i].getName();
				if (methodName.startsWith("get")) { // ֻ����get����

					// ���˵�getClass���в����ķ���
					Class<?> declarClass = methods[i].getDeclaringClass();
					Constructor<?>[] constructor = declarClass.getConstructors();
					if (constructor.length == 0 || declarClass.isInterface()) {
						continue; // ����ǽӿڻ������Ļ����Զ�����
					}

					// ���˵�getClass���в����ķ���
					if (methodName.equals("getClass")
							|| methods[i].getParameterTypes().length > 0) {
						continue;
					}

					String tmp = methodName.trim().substring(3);
					tmp = tmp.substring(0, 1).toLowerCase() + tmp.substring(1);
					ret.append("\t").append(tmp).append(" = ").append(
							methods[i].invoke(this, (Object[])null)).append("\r\n");
				}
			} catch (Exception ex) {
				continue;
			}
		}
		ret.append("}\r\n");
		return ret.toString();
	}
}
