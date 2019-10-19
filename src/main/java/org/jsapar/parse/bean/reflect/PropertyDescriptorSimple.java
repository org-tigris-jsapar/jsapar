package org.jsapar.parse.bean.reflect;

import java.lang.reflect.Method;

final class PropertyDescriptorSimple implements PropertyDescriptor {
    private final String name;
    private final Method readMethod;
    private final Method writeMethod;

    PropertyDescriptorSimple(String name, Method readMethod, Method writeMethod) {
        this.name = name;
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
    }

    @Override
    public Method getReadMethod() {
        return readMethod;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Method getWriteMethod() {
        return writeMethod;
    }
}
