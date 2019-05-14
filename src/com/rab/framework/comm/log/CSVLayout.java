package com.rab.framework.comm.log;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * 
 * <P>Title: CSVLayout</P>
 * <P>Description: </P>
 * <P>����˵����</P>
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
	   * ���ַ������и�ʽ��������û��������Ϊ�ַ����ڴ�����֮ǰ�Ѿ����˴���
	   * @param event
	   * @return ��ʽ������ַ���
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