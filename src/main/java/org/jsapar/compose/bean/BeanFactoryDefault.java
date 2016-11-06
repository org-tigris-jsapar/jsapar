package org.jsapar.compose.bean;

import org.jsapar.model.Line;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeanFactoryDefault<T> implements BeanFactory<T> {
    private static final String GET_PREFIX = "get";
    private static final String SET_PREFIX = "set";

    public BeanFactoryDefault() {
    }

    /**
     * This implementation creates the bean by using Class.forName method on the line type.
     * @see BeanFactory#createBean(Line)
     */
    @SuppressWarnings("unchecked")
    @Override
    public T createBean(Line line) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException {
        Class<?> c = Class.forName(line.getLineType());
        return (T) c.newInstance();
    }

    /**
     * This implementation uses reflection methods to assign correct object.
     * @see BeanFactory#findOrCreateChildBean(java.lang.Object, java.lang.String)
     */
    @Override
    public Object findOrCreateChildBean(Object parentBean, String childBeanName) throws InstantiationException,
            IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException,
            InvocationTargetException {
        String getterMethodName = createGetMethodName(childBeanName);
        Method getterMethod = parentBean.getClass().getMethod(getterMethodName);
        Object childBean = getterMethod.invoke(parentBean);
        if (childBean == null) {
            // If there was no object we have to create it..
            Class<?> nextClass = getterMethod.getReturnType();
            childBean = nextClass.newInstance();
            // And assign it by using the setter.
            String setterMethodName = createSetMethodName(childBeanName);
            parentBean.getClass().getMethod(setterMethodName, nextClass).invoke(parentBean, childBean);
        }
        return childBean;
    }

    /**
     * Creates a set method name based on attribute name.
     * @param sAttributeName The attribute name
     * @return The set method that corresponds to this attribute.
     */
    private String createSetMethodName(String sAttributeName) {
        return createBeanMethodName(SET_PREFIX, sAttributeName);
    }

    /**
     * Creates a get method based on attribute name.
     * @param sAttributeName The attribute name
     * @return The get method that corresponds to this attribute.
     */
    private String createGetMethodName(String sAttributeName) {
        return createBeanMethodName(GET_PREFIX, sAttributeName);
    }

    /**
     * Creates an access method based on attribute
     * @param prefix The access method prefix.
     * @param sAttributeName The attribute name.
     * @return The setter or setter method that corresponds to this attribute.
     */
    private String createBeanMethodName(String prefix, String sAttributeName) {
        return prefix + sAttributeName.substring(0, 1).toUpperCase() + sAttributeName.substring(1);
    }

}
