package com.rab.framework.comm.security;

import java.util.Comparator;

/**
 * 
 * <P>Title: MenuNodeComparator</P>
 * <P>Description: </P>
 * <P>³ÌÐòËµÃ÷£º</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-12-2</P>
 *
 */
public class MenuNodeComparator implements Comparator<Object> {
	public MenuNodeComparator() {
	}

	public int compare(Object o1, Object o2) {
		FuncRightResource m1 = (FuncRightResource) o1;
		FuncRightResource m2 = (FuncRightResource) o2;
		if (m1.getSortId() < m2.getSortId()) {
			return -1;
		}
		else {
			return 1;
		}
	}
}
