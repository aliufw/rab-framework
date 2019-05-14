package com.rab.framework.component.multicast;

import java.util.Comparator;

/**
 * 
 * <P>Title: PacketUnitSortComparator</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P>PacketUnit ����������</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-12-3</P>
 *
 */
public class PacketUnitSortComparator implements Comparator<Object> {
    private final int ASC = 1; //����
    private final int DSC = -1;//����

    public int compare(Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            return this.ASC;
        }

        PacketUnit pu1 = (PacketUnit) o1;
        PacketUnit pu2 = (PacketUnit) o2;

        if (pu1.getIndex() >= pu2.getIndex()) {
            return this.ASC;
        } else {
            return this.DSC;
        }
    }
}