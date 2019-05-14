package com.rab.framework.comm.security;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 
 * <P>Title: MenuNode</P>
 * <P>Description: </P>
 * <P>程序说明：菜单导航的节点</P>
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
	 * 序列化编号
	 */
	private static final long serialVersionUID = -4283015971324570262L;

	/**
	 * 菜单节点类型：目录节点
	 */
	public static int MENU_NODE_TYPE_DIR = 0;
	
	/**
	 * 菜单节点类型：功能权限
	 */
	public static int MENU_NODE_TYPE_MENU = 1;
	
	/**
	 * 菜单节点类型：按钮权限
	 */
	public static int MENU_NODE_TYPE_BUTTON = 2;
	
	/**
	 * 功能模块
	 */
	private String modCode;
	
	/**
	 * 节点编号
	 */
	private String funcId;
	
	/**
	 * 父节点编号
	 */
	private String parentId;
	
	/**
	 * 节点排序编号
	 */
	private int sortId;
	
	/**
	 * 节点权限名称
	 */
	private String permName;
	
	/**
	 * 节点权限标识
	 */
	private String permId;
	
	/**
	 * <P>节点类型</P>
	 */
	private int funcType;
	
	/**
	 * 菜单节点的导航URI，当节点类型为MENU_NODE_TYPE_DIR时，本属性无效
	 * 
	 */
	private String funcUri;
	
//	/**
//	 * 帐套编码
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
	 * 返回类的基本信息, 包括类的名称和类的基本属性值信息
	 * @return
	 */
	public String toString() {
		Method[] methods = this.getClass().getMethods();
		StringBuffer ret = new StringBuffer(super.toString());
		ret.append(" = {\r\n");
		for (int i = 0; i < methods.length; i++) {
			try {
				String methodName = methods[i].getName();
				if (methodName.startsWith("get")) { // 只处理get方法

					// 过滤掉getClass和有参数的方法
					Class<?> declarClass = methods[i].getDeclaringClass();
					Constructor<?>[] constructor = declarClass.getConstructors();
					if (constructor.length == 0 || declarClass.isInterface()) {
						continue; // 如果是接口或抽象类的话则自动跳过
					}

					// 过滤掉getClass和有参数的方法
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
