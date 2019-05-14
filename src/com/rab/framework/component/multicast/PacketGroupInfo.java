package com.rab.framework.component.multicast;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * <P>Title: PacketGroupInfo</P>
 * <P>Description: </P>
 * <P>����˵����</P>
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
     * �����Ѿ����յ������ݱ�����Ϣ(���ݶ���: PacketUnit)
     */
    private List<PacketUnit> list;

    /**
     * ���ݱ������ʶ
     */
    private String groupid;

    /**
     * ���󴴽�ʱ��
     */
    private long createTime;

    /**
     * ������
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