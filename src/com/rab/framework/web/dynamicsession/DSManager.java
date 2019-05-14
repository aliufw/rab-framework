package com.rab.framework.web.dynamicsession;

import java.util.Map;

/**
 * 
 * <P>Title: DSManager</P>
 * <P>Description: </P>
 * <P>³ÌÐòËµÃ÷£º</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-12-3</P>
 *
 */
public class DSManager {
	public Map<String, MemorySession> getDataCachePool(){
		return DynamicSessionManager.singleton().getDataCachePool();
		
	}
}
