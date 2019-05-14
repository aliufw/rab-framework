package com.rab.framework.component.multicast;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * 
 * <P>Title: MulitcastUtils</P>
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
public class MulticastUtils {
    /**
     * 将类对象转换为平面结构数据
     *
     * @param vo
     * @return
     */
    public static StringBuffer ObjectToFlat(Object vo) {
        StringBuffer sb = new StringBuffer();
        if (vo == null) {
            return sb;
        }
        sb.append("CLASSNAME:").append(vo.getClass().getName()).append("\r\n");
        Method[] methods = vo.getClass().getMethods();
        try {
            for (int i = 0; i < methods.length; i++) {
                String methodName = methods[i].getName();
                if (methodName.startsWith("get") || methodName.startsWith("is")) { //只处理get方法
                    //过滤掉getClass和有参数的方法
                    if (methodName.equals("getClass")
                            || methods[i].getParameterTypes().length > 0) {
                        continue;
                    }
                    String method = null;
                    if (methodName.startsWith("is")) {
                        method = methodName.trim().substring(2);
                    } else {
                        method = methodName.trim().substring(3);
                    }
                    method = method.substring(0, 1).toLowerCase() + method.substring(1);
                    Object[] obj2 = new Object[1];
    				obj2[0] = null;
                    sb.append(method).append(":").append(methods[i].invoke(vo, obj2)).append("\r\n");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }

    /**
     * 将平面结构数据转换为类对象
     *
     * @param sb
     * @return
     */
    public static Object FlatToObject(StringBuffer sb) {
        Object obj = null;
        Properties props = new Properties();
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(sb.toString().getBytes());
            props.load(in);
            String className = props.getProperty("CLASSNAME");
            obj = Class.forName(className).newInstance();

            Method[] methods = obj.getClass().getMethods();
            for (int i = 0; i < methods.length; i++) {
                String methodName = methods[i].getName();

                String attribName = null;
                String dataTypeName = null;
                if (methodName.startsWith("get")) {
                    if (methodName.equals("getClass")
                            || methods[i].getParameterTypes().length > 0) {
                        continue;
                    }
                    attribName = methodName.substring(3);
                    dataTypeName = methods[i].getReturnType().getName();
                } else if (methodName.startsWith("is")) {
                    attribName = methodName.substring(2);
                    dataTypeName = methods[i].getReturnType().getName();
                } else {
                    continue;
                }

                Method setMethod = null;
                Class<?>[] paraType = new Class[1];
                Object[] param = new Object[1];
                attribName = attribName.substring(0, 1).toLowerCase() + attribName.substring(1);
                String setMethodName = "set" + attribName.substring(0, 1).toUpperCase() + attribName.substring(1);
                if (dataTypeName.equals("java.lang.String")) {
                    paraType[0] = String.class;
                    setMethod = obj.getClass().getMethod(setMethodName, paraType);
                    param[0] = props.getProperty(attribName);
                } else if (dataTypeName.equals("long")) {
                    paraType[0] = long.class;
                    setMethod = obj.getClass().getMethod(setMethodName, paraType);
                    param[0] = new Long(props.getProperty(attribName));
                } else if (dataTypeName.equals("int")) {
                    paraType[0] = int.class;
                    setMethod = obj.getClass().getMethod(setMethodName, paraType);
                    param[0] = new Integer(props.getProperty(attribName));
                } else if (dataTypeName.equals("byte")) {
                    paraType[0] = byte.class;
                    setMethod = obj.getClass().getMethod(setMethodName, paraType);
                    param[0] = new Byte(props.getProperty(attribName));
                } else if (dataTypeName.equals("boolean")) {
                    boolean data = props.getProperty(attribName).equals("true") ? true : false;
                    paraType[0] = boolean.class;
                    setMethod = obj.getClass().getMethod(setMethodName, paraType);
                    param[0] = new Boolean(data);
                }

                //设置对象属性值
                setMethod.invoke(obj, param);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return obj;
    }

    /**
     * 将整形数据转换为byte数组
     *
     * @param d
     * @return
     */
    public static byte[] int2bytes(int d) {
        byte[] buffer = new byte[4];

        for (int i = buffer.length - 1; i >= 0; i--) {
            buffer[i] = (byte) (d % 256);
            d = d / 256;
        }

        return buffer;
    }

    /**
     * 将以数组形式保存的整型数据恢复到整型变量中
     *
     * @param b
     * @return
     */
    public static int bytes2int(byte[] b) {
        int d = 0;

        for (int i = b.length - 1; i >= 0; i--) {
            int byteData = ((int) b[i] + 256) % 256;
            d += ((int) Math.pow(256, (b.length - i - 1))) * byteData;
        }

        return d;
    }

}
