package com.rab.framework.comm.log;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * 
 * <P>Title: CSVLayout</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-10-30</P>
 *
 */
public class CSVLayout extends Layout {

	  StringBuffer sbuf = new StringBuffer(1024);

	  public CSVLayout() {
	  }
	  public void activateOptions() {
	  }
	  /**
	   * 对字符串进行格式化，这里没做处理，因为字符串在传进来之前已经做了处理。
	   * @param event
	   * @return 格式化后的字符串
	   */
	  public String format(LoggingEvent event) {
	    sbuf.setLength(0);
	    sbuf.append(event.getRenderedMessage());
	    sbuf.append(LINE_SEP);
	    return sbuf.toString();
	  }
	  public boolean ignoresThrowable() {
	    return true;
	  }
	}