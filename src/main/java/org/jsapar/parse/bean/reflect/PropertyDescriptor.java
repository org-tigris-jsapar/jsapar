package org.jsapar.parse.bean.reflect;

import java.lang.reflect.Method;

public interface PropertyDescriptor {
    /**
     * @return The getter method or null if no such exists.
     */
    Method getReadMethod();

    /**
     * @return The property name
     */
    String getName();

    /**
     * @return The setter method or null if no such exists.
     */
    Method getWriteMethod();
}
