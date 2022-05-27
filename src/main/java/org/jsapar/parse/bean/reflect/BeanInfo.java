package org.jsapar.parse.bean.reflect;

import java.util.Map;

public interface BeanInfo {
    static <T> BeanInfo ofClass(Class<T> beanClass) {
        return new BeanInfoReflection(beanClass);
    }

    Map<String, PropertyDescriptor> getPropertyDescriptorsByName();
}
