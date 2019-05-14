package com.rab.framework.comm.lc;

import java.lang.reflect.Method;
import java.util.Calendar;

/**
 * 
 * <P>Title: AuthorizationItem</P>
 * <P>Description: </P>
 * <P>程序说明：License描述对象,对应于license文件中的license标签</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2011-1-12</P>
 *
 */
public class AuthorizationItem {
	/**
	 * 组件名称
	 */
	private String componentName;

	/**
	 * 被授权者
	 */
	private String licensee;

	/**
	 * 组件超时日期
	 */
	private Calendar expiration;

	/**
	 * 硬件码
	 */
	private String hardid;

	/**
	 * 加密摘要信息
	 */
	private String signature;

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getLicensee() {
		return licensee;
	}

	public void setLicensee(String licensee) {
		this.licensee = licensee;
	}

	public Calendar getExpiration() {
		return expiration;
	}

	public void setExpiration(Calendar expiration) {
		this.expiration = expiration;
	}

	public String getHardid() {
		return hardid;
	}

	public void setHardid(String hardid) {
		this.hardid = hardid;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String toString() {
		Method[] methods = this.getClass().getMethods();
		StringBuffer ret = new StringBuffer(super.toString());
		ret.append(" = {\r\n");
		try {
			for (int i = 0; i < methods.length; i++) {
				String methodName = methods[i].getName();
				if (methodName.startsWith("get")) { // 只处理get方法
					// 过滤掉getClass和有参数的方法
					if (methodName.equals("getClass")
							|| methods[i].getParameterTypes().length > 0) {
						continue;
					}

					String tmp = methodName.trim().substring(3);
					tmp = tmp.substring(0, 1).toLowerCase() + tmp.substring(1);
					Object[] obj = new Object[1];
					obj[0] = null;
					ret.append("\t").append(tmp).append(" = ").append(
							methods[i].invoke(this, obj)).append("\r\n");
				}
			}
		} catch (Exception ex) {
			ret.append("******************* 异常! **********************");
		}

		ret.append("}\r\n");
		return ret.toString();
	}

}
