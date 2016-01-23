package org.jsapar.compose;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jsapar.model.Line;

public class BeanFactoryDefault implements BeanFactory {
    private static final String GET_PREFIX = "get";
    private static final String SET_PREFIX = "set";

    public BeanFactoryDefault() {
    }

    /**
     * This implementation creates the bean by using Class.forName method on the line type.
     * @see BeanFactory#createBean(Line)
     */
    @Override
    public Object createBean(Line line) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<?> c = Class.forName(line.getLineType());
        return c.newInstance();
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
     * @param sAttributeName
     * @return The set method that corresponds to this attribute.
     */
    private String createSetMethodName(String sAttributeName) {
        return createBeanMethodName(SET_PREFIX, sAttributeName);
    }

    /**
     * @param sAttributeName
     * @return The get method that corresponds to this attribute.
     */
    private String createGetMethodName(String sAttributeName) {
        return createBeanMethodName(GET_PREFIX, sAttributeName);
    }

    /**
     * @param prefix
     * @param sAttributeName
     * @return The setter or setter method that corresponds to this attribute.
     */
    private String createBeanMethodName(String prefix, String sAttributeName) {
        return prefix + sAttributeName.substring(0, 1).toUpperCase() + sAttributeName.substring(1);
    }

}
