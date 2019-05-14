package com.rab.framework.dao;

/**
 * 
 * <P>Title: StoredProcParamObj</P>
 * <P>Description: </P>
 * <P>程序说明：用来封装存储过程的参数:</P>
 * <P>        index: 参数的顺序。</P>
 * <P>        value:参数值，如果参数是IN或者INOUT参数类型时，要输入这个参数的值。</P>
 * <P>        paramType:参数类型。这些类型只可能为IN, OUT, INOUT三种。</P>
 * <P>        dataType:数据类型，当参数为OUT或者INOUT参数类型时，一定要定义它的返</P>
 * <P>                 回值的数据类型，这种数据类型是java.sql.Types中的一种。</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-8-19</P>
 *
 */
public class StoredProcParamObj {
	   /**
     * 参数的顺序。
     */
    private int index;
    /**
     * 参数值，如果参数是IN或者INOUT参数类型时，要输入这个参数的值。
     */
    private Object value;
    /**
     * 参数类型。这些类型只可能为IN, OUT, INOUT三种。
     */
    private String paramType;
    /*
     *数据类型，当参数为OUT或者INOUT参数类型时，一定要定义它的返回值的数
     *据类型，这种数据类型是java.sql.Types中的一种。
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
