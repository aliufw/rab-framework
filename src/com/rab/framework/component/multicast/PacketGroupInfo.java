package com.rab.framework.component.multicast;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * <P>Title: PacketGroupInfo</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-12-3</P>
 *
 */
public class PacketGroupInfo {
    /**
     * 缓存已经接收到的数据报文信息(数据对象: PacketUnit)
     */
    private List<PacketUnit> list;

    /**
     * 数据报文组标识
     */
    private String groupid;

    /**
     * 对象创建时间
     */
    private long createTime;

    /**
     * 构造器
     */
    public PacketGroupInfo() {
        this.list = new ArrayList<PacketUnit>();
        this.createTime = System.currentTimeMillis();
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public List<PacketUnit> getList() {
        return list;
    }

    public void addPacketUnit(PacketUnit pu) {
        this.list.add(pu);
    }

    public int getCount() {
        return list.size();
    }
}