package org.jsapar.parse.bean.reflect;

import org.jsapar.error.BeanException;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

class BeanInfoBeans implements BeanInfo {
    private final Map<String, PropertyDescriptor> propertyDescriptorsByName;

    BeanInfoBeans(Class c) {
        try {
            java.beans.BeanInfo beanInfo = Introspector.getBeanInfo(c);
            this.propertyDescriptorsByName = Arrays.stream(beanInfo.getPropertyDescriptors()).collect(Collectors
                    .toMap(java.beans.PropertyDescriptor::getName,
                            pd -> new PropertyDescriptorSimple(pd.getName(), pd.getReadMethod(), pd.getWriteMethod())));
        } catch (IntrospectionException e) {
            throw new BeanException("Unable to determine bean info from class " + c, e);
        }
    }

    @Override
    public Map<String, PropertyDescriptor> getPropertyDescriptorsByName() {
        return propertyDescriptorsByName;
    }
}
