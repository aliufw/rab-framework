package com.rab.framework.component.multicast;

/**
 * 
 * 
 * <P>Title: BaseMulticastFlatInfoServerAdapter</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P>服务器端适配器基类, 用以处理接收到的数据, 实现从数据传输模块到应用模块的数据转换接口</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-12-3</P>
 *
 */

public interface BaseMulticastFlatInfoServerAdapter {
    /**
     * 处理接收到的数据
     * <p/>
     * 开发人员可以继承并实现该方法, 在其中实现数据的格式转换以及其他业务功能
     *
     * @param data 输入数据为通过广播方式接收到的数据, 封装方式为字节数组
     */
    public void dataHander(byte[] data);
}
