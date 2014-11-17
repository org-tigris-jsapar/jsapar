package org.jsapar.output;

import java.lang.reflect.InvocationTargetException;

import org.jsapar.Line;

public interface JavaBeanFactory {

    /**
     * @param lineType
     * @param line
     * @return A new instance of a java bean created for the supplied line.
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public Object createBean(Line line) throws ClassNotFoundException, InstantiationException, IllegalAccessException;

    /**
     * @param parentBean
     * @param childBeanName
     *            A child bean name. Dot notation can be used in a cell name to access sub-levels. This method will be
     *            called once for each level with the previous level as parentBean.
     * @return The child bean based on the supplied childBeanName. If no bean is assigned yet, a new instance is created
     *         and assigned by this method.
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public Object findOrCreateChildBean(Object parentBean, String childBeanName) throws InstantiationException,
            IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException,
            InvocationTargetException;
}
