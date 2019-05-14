/**
 * <p>Title: </p>
 * <p>Description:  * Miscellaneous object utility methods. Mainly for internal use
 * within the framework; consider Jakarta's Commons Lang for a more
 * comprehensive suite of object utilities.</p>
 *
 * <p>Copyright: Copyright (c) 2004 中软网络??术股份有限公??</p>
 * <p>Company: 应用产品研发中心</p>
 * @author wwq
 * @version 1.0
 */

package com.rab.framework.comm.util;


public abstract class ObjectUtils {

    /**
     * Determine if the given Objects are equal, returning true if both
     * are null respectively false if only one is null.
     * @param o1 first Object to compare
     * @param o2 second Object to compare
     * @return whether the given Objects are equal
     */
    public static boolean nullSafeEquals1(Object o1, Object o2) {
        return (o1 == o2 || (o1 != null && o1.equals(o2)));
    }

}