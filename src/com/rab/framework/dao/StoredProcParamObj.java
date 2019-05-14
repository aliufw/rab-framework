package com.rab.framework.dao;

/**
 * 
 * <P>Title: StoredProcParamObj</P>
 * <P>Description: </P>
 * <P>����˵����������װ�洢���̵Ĳ���:</P>
 * <P>        index: ������˳��</P>
 * <P>        value:����ֵ�����������IN����INOUT��������ʱ��Ҫ�������������ֵ��</P>
 * <P>        paramType:�������͡���Щ����ֻ����ΪIN, OUT, INOUT���֡�</P>
 * <P>        dataType:�������ͣ�������ΪOUT����INOUT��������ʱ��һ��Ҫ�������ķ�</P>
 * <P>                 ��ֵ���������ͣ���������������java.sql.Types�е�һ�֡�</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-8-19</P>
 *
 */
public class StoredProcParamObj {
	   /**
     * ������˳��
     */
    private int index;
    /**
     * ����ֵ�����������IN����INOUT��������ʱ��Ҫ�������������ֵ��
     */
    private Object value;
    /**
     * �������͡���Щ����ֻ����ΪIN, OUT, INOUT���֡�
     */
    private String paramType;
    /*
     *�������ͣ�������ΪOUT����INOUT��������ʱ��һ��Ҫ�������ķ���ֵ����
     *�����ͣ���������������java.sql.Types�е�һ�֡�
     */
    private int dataType;

    public static final String IN = "In" ;
    public static final String OUT = "Out" ;
    public static final String INOUT = "InAndOut" ;

    public StoredProcParamObj() {
    }

    public StoredProcParamObj(int index,Object value,
                              String paramType,int dataType){
        this.index = index ;
        this.value = value ;
        this.paramType = paramType ;
        this.dataType = dataType ;
    }

    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    public String getParamType() {
        return paramType;
    }
    public void setParamType(String paramType) {
        this.paramType = paramType;
    }
    public int getDataType() {
        return dataType;
    }
    public void setDataType(int dataType) {
        this.dataType = dataType;
    }
}
