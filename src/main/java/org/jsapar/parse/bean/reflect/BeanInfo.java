package org.jsapar.parse.bean.reflect;

import java.util.Map;

public interface BeanInfo {
    static BeanInfo ofClass(Class beanClass) {
        return new BeanInfoReflection(beanClass);
    }

    Map<String, PropertyDescriptor> getPropertyDescriptorsByName();
}
