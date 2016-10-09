package org.jsapar.compose.bean;

import org.jsapar.Composer;
import org.jsapar.compose.ComposeError;
import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.ErrorEventSource;
import org.jsapar.model.Cell;
import org.jsapar.model.Document;
import org.jsapar.model.Line;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stejon0 on 2016-10-09.
 */
public class BeanComposer implements Composer, BeanComposedEventListener, ErrorEventListener {
    private static final String SET_PREFIX = "set";

    private List<BeanComposedEventListener> composedEventListeners = new ArrayList<>();
    private ErrorEventSource                errorEventSource       = new ErrorEventSource();
    private BeanFactory                     beanFactory            = new BeanFactoryDefault();
    private Map<String, String>             setMethodNameCache     = new HashMap<>();

    public BeanComposer() {
    }

    public BeanComposer(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void compose(Document document) throws IOException {
    }

    @Override
    public boolean composeLine(Line line) throws IOException {
        Object bean = null;
        try {
            bean = beanFactory.createBean(line);
            assign(line, bean);
        } catch (InstantiationException e) {
            generateErrorEvent(line, "Failed to instantiate object. Skipped creating bean", e);
        } catch (IllegalAccessException e) {
            generateErrorEvent(line, "Failed to call set method. Skipped creating bean", e);
        } catch (ClassNotFoundException e) {
            generateErrorEvent(line, "Class not found. Skipped creating bean", e);
        } catch (Throwable e) {
            generateErrorEvent(line, "Unexpected error. Skipped creating bean", e);
        }
        beanComposedEvent(new BeanComposedEvent(this, bean, line.getLineNumber()));
        return true;
    }

    private void generateErrorEvent(Line line, String message, Throwable t) {
        errorEvent(new ErrorEvent(this, new ComposeError(message, line, t)));
    }

    private void generateErrorEvent(Cell cell, String message, Throwable t) {
        errorEvent(new ErrorEvent(this, new ComposeError(message + " while handling cell " + cell, t)));
    }

    private void generateErrorEvent(Cell cell, String message) {
        errorEvent(new ErrorEvent(this, new ComposeError(message + " while handling cell " + cell)));
    }

    public void addComposedEventListener(BeanComposedEventListener eventListener) {
        composedEventListeners.add(eventListener);
    }

    @Override
    public void addErrorEventListener(ErrorEventListener errorEventListener) {
        errorEventSource.addEventListener(errorEventListener);
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void beanComposedEvent(BeanComposedEvent event) {
        for (BeanComposedEventListener composedEventlistener : composedEventListeners) {
            composedEventlistener.beanComposedEvent(event);
        }
    }

    @Override
    public void errorEvent(ErrorEvent event) {
        errorEventSource.errorEvent(event);
    }

    /**
     * Assigns the cells of a line as attributes to an object.
     *
     * @param <T>            The type of the object to assign
     * @param line           The line to get parameters from.
     * @param objectToAssign The object to assign cell attributes to. The object will be modified.
     * @return The object that was assigned. The same object that was supplied as parameter.
     */
    public <T> T assign(Line line, T objectToAssign) {

        java.util.Iterator<Cell> cellIter = line.getCellIterator();
        while (cellIter.hasNext()) {
            Cell cell = cellIter.next();
            String sName = cell.getName();
            if (sName == null || sName.isEmpty())
                continue;

            assignCellToField(cell, sName, objectToAssign);
        }
        return objectToAssign;
    }

    /**
     * Assign supplied cell value to supplied object.
     *
     * @param cell
     * @param sName
     * @param objectToAssign
     */
    private void assignCellToField(Cell cell, String sName, Object objectToAssign) {
        try {
            String[] nameLevels = sName.split("\\.");
            Object currentObject = objectToAssign;
            for (int i = 0; i + 1 < nameLevels.length; i++) {
                try {
                    // Continue looping to next object.
                    currentObject = beanFactory.findOrCreateChildBean(currentObject, nameLevels[i]);
                } catch (InstantiationException e) {
                    this.generateErrorEvent(cell,
                            "Skipped assigning cell - Failed to execute default constructor for class accessed by "
                                    + nameLevels[i], e);
                    return;
                }
            }
            sName = nameLevels[nameLevels.length - 1];
            assignAttribute(cell, sName, currentObject);
        } catch (InvocationTargetException | IllegalArgumentException e) {
            this.generateErrorEvent(cell,
                    "Skipped assigning cell - Failed to execute getter or setter method in class " + objectToAssign
                            .getClass().getName(), e);
        } catch (IllegalAccessException e) {
            this.generateErrorEvent(cell,
                    "Skipped assigning cell - Failed to access getter or setter method in class " + objectToAssign
                            .getClass().getName(), e);
        } catch (NoSuchMethodException e) {
            this.generateErrorEvent(cell,
                    "Skipped assigning cell - Missing getter or setter method in class " + objectToAssign.getClass()
                            .getName() + " or a sub class", e);
        }
    }

    /**
     * @param cell
     * @param sName
     * @param objectToAssign
     */
    private void assignAttribute(Cell cell, String sName, Object objectToAssign) {
        if (cell.isEmpty())
            return;

        String sSetMethodName = createSetMethodName(sName);
        try {
            boolean success = assignParameterBySignature(objectToAssign, sSetMethodName, cell);
            if (!success) // Try again but use the name and try to cast.
                assignParameterByName(objectToAssign, sSetMethodName, cell);
        } catch (IllegalArgumentException e) {
            this.generateErrorEvent(cell,
                    "Skipped assigning cell - The method " + sSetMethodName + "() in class " + objectToAssign.getClass()
                            .getName() + " does not accept correct type", e);
        } catch (IllegalAccessException e) {
            this.generateErrorEvent(cell,
                    "Skipped assigning cell - The method " + sSetMethodName + "() in class " + objectToAssign.getClass()
                            .getName() + " does not have correct access", e);
        } catch (InvocationTargetException e) {
            this.generateErrorEvent(cell,
                    "Skipped assigning cell - The method " + sSetMethodName + "() in class " + objectToAssign.getClass()
                            .getName() + " failed to execute", e);
        }
    }

    /**
     * @param sAttributeName
     * @return The set method that corresponds to this attribute.
     */
    private String createSetMethodName(String sAttributeName) {
        String methodName = this.setMethodNameCache.get(sAttributeName);
        if (methodName == null) {
            methodName = createBeanMethodName(SET_PREFIX, sAttributeName);
            this.setMethodNameCache.put(sAttributeName, methodName);
        }
        return methodName;

    }

    /**
     * @param prefix
     * @param sAttributeName
     * @return The setter or setter method that corresponds to this attribute.
     */
    private String createBeanMethodName(String prefix, String sAttributeName) {
        return prefix + sAttributeName.substring(0, 1).toUpperCase() + sAttributeName.substring(1);
    }

    /**
     * Assigns the cells of a line as attributes to an object.
     *
     * @param <T>            The type of the object to assign
     * @param cell           The cell to get the parameter from.
     * @param objectToAssign The object to assign cell attributes to. The object will be modified.
     * @return True if the parameter was assigned to the object, false otherwise.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private <T> boolean assignParameterBySignature(T objectToAssign, String sSetMethodName, Cell cell)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        if (cell.getValue() == null)
            return false;
        try {
            Class<?> type = cell.getValue().getClass();
            Method f = objectToAssign.getClass().getMethod(sSetMethodName, type);
            f.invoke(objectToAssign, cell.getValue());
            return true;
        } catch (NoSuchMethodException e) {
            // We don't care here since we will try again if this method fails.
        }
        return false;
    }

    /**
     * Assigns the cells of a line as attributes to an object.
     *
     * @param <T>            The type of the object to assign
     * @param cell           The cell to get the parameter from.
     * @param objectToAssign The object to assign cell attributes to. The object will be modified.
     * @return True if the parameter was assigned to the object, false otherwise.
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private <T> boolean assignParameterByName(T objectToAssign, String sSetMethodName, Cell cell)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        try {
            Method[] methods = objectToAssign.getClass().getMethods();
            for (Method f : methods) {
                Class<?>[] paramTypes = f.getParameterTypes();
                if (paramTypes.length != 1 || !f.getName().equals(sSetMethodName))
                    continue;

                Object value = cell.getValue();
                // Casts between simple types does not work automatically
                if (paramTypes[0] == Integer.TYPE && value instanceof Number)
                    f.invoke(objectToAssign, ((Number) value).intValue());
                else if (paramTypes[0] == Short.TYPE && value instanceof Number)
                    f.invoke(objectToAssign, ((Number) value).shortValue());
                else if (paramTypes[0] == Byte.TYPE && value instanceof Number)
                    f.invoke(objectToAssign, ((Number) value).byteValue());
                else if (paramTypes[0] == Float.TYPE && value instanceof Number)
                    f.invoke(objectToAssign, ((Number) value).floatValue());
                    // Will squeeze in first character of any datatype's string representation.
                else if (paramTypes[0] == Character.TYPE) {
                    if (value instanceof Character) {
                        f.invoke(objectToAssign, (Character) value);
                    } else {
                        String sValue = value.toString();
                        if (!sValue.isEmpty())
                            f.invoke(objectToAssign, sValue.charAt(0));
                    }
                } else if (Enum.class.isAssignableFrom(paramTypes[0]) && value instanceof String) {
                    f.invoke(objectToAssign, Enum.valueOf((Class<Enum>) paramTypes[0], String.valueOf(value)));
                } else {
                    try {
                        f.invoke(objectToAssign, value);
                    } catch (IllegalArgumentException e) {
                        // There may be more methods that fits the name.
                        continue;
                    }
                }
                return true;
            }
            this.generateErrorEvent(cell,
                    "Skipped assigning cell - No method called " + sSetMethodName + "() found in class "
                            + objectToAssign.getClass().getName() + " that fits the cell ");
        } catch (SecurityException e) {
            this.generateErrorEvent(cell,
                    "Skipped assigning cell - The method " + sSetMethodName + "() in class " + objectToAssign.getClass()
                            .getName() + " does not have public access", e);
        }
        return false;
    }

}
