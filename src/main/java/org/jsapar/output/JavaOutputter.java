package org.jsapar.output;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.jsapar.Cell;
import org.jsapar.Document;
import org.jsapar.Line;
import org.jsapar.input.CellParseError;

/**
 * Uses Java reflection to convert the Document structure into POJO objects.
 * 
 * @author stejon0
 * 
 */
public class JavaOutputter {

    private static final String GET_PREFIX = "get";
    private static final String SET_PREFIX = "set";

    /**
     * Creates a list of java objects. For this method to work, the lineType attribute of each line
     * have to contain the full class name of the class to create for each line. Also the set method
     * for each attribute have to match exactly to the name of each cell.
     * 
     * @param document
     * @return A list of Java objects.
     */
    @SuppressWarnings("rawtypes")
    public java.util.List createJavaObjects(Document document, List<CellParseError> parseErrors) {
        java.util.List<Object> objects = new java.util.ArrayList<Object>(document.getNumberOfLines());
        java.util.Iterator<Line> lineIter = document.getLineIterator();
        while (lineIter.hasNext()) {
            Line line = lineIter.next();
            try {
                Object o = this.createObject(line, parseErrors);
                objects.add(o);
            } catch (InstantiationException e) {
                parseErrors.add(new CellParseError("", "", null,
                        "Failed to instantiate object. Skipped creating object - " + e));
            } catch (IllegalAccessException e) {
                parseErrors.add(new CellParseError("", "", null,
                        "Failed to call set method. Skipped creating object - " + e));
            } catch (ClassNotFoundException e) {
                parseErrors.add(new CellParseError("", "", null, "Class not found. Skipped creating object - " + e));
            } catch (Throwable e) {
                parseErrors.add(new CellParseError(0, "", "", null, "Skipped creating object - " + e.getMessage(), e));
            }
        }
        return objects;
    }

    /**
     * @param line
     * @return An object of the class, denoted by the lineType of the line, with attributes set by
     *         the supplied line.
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public Object createObject(Line line, List<CellParseError> parseErrors) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        Class<?> c = Class.forName(line.getLineType());
        Object o = c.newInstance();
        return assign(line, o, parseErrors);
    }

    /**
     * Assigns the cells of a line as attributes to an object.
     * 
     * @param <T>
     *            The type of the object to assign
     * @param line
     *            The line to get parameters from.
     * @param objectToAssign
     *            The object to assign cell attributes to. The object will be modified.
     * @return The object that was assigned. The same object that was supplied as parameter.
     */
    public <T> T assign(Line line, T objectToAssign, List<CellParseError> parseErrors) {

        java.util.Iterator<Cell> cellIter = line.getCellIterator();
        while (cellIter.hasNext()) {
            Cell cell = cellIter.next();
            String sName = cell.getName();
            if (sName == null || sName.isEmpty())
                continue;

            doAssign(cell, sName, objectToAssign, parseErrors);
        }
        return objectToAssign;
    }

    /**
     * Assign supplied cell value to supplied object.
     * 
     * @param <T>
     * @param cell
     * @param sName
     * @param objectToAssign
     * @param parseErrors
     */
    private void doAssign(Cell cell, String sName, Object objectToAssign, List<CellParseError> parseErrors) {
        try {
            String[] nameLevels = sName.split("\\.");
            Object currentObject = objectToAssign;
            for (int i = 0; i + 1 < nameLevels.length; i++) {
                // First invoke the getter method.
                String getterMethodName = createGetMethodName(nameLevels[i]);
                Method getterMethod = currentObject.getClass().getMethod(getterMethodName);
                Object nextObject = getterMethod.invoke(currentObject);
                if (nextObject == null) {
                    // If there was no object we have to create it..
                    Class<?> nextClass = getterMethod.getReturnType();
                    try {
                        nextObject = nextClass.newInstance();
                    } catch (InstantiationException e) {
                        parseErrors.add(new CellParseError(cell.getName(), cell.getStringValue(), null,
                                "Skipped assigning cell - Failed to execute default constructor for class"
                                        + nextClass.getName() + " - " + e));
                        return;
                    }
                    // And assign it by using the setter.
                    String setterMethodName = createSetMethodName(nameLevels[i]);
                    currentObject.getClass().getMethod(setterMethodName, nextClass).invoke(currentObject, nextObject);
                }
                // Continue looping to next object.
                currentObject = nextObject;
            }
            sName = nameLevels[nameLevels.length - 1];
            assignAttribute(cell, sName, currentObject, parseErrors);
        } catch (InvocationTargetException e) {
            parseErrors.add(new CellParseError(cell.getName(), cell.getStringValue(), null,
                    "Skipped assigning cell - Failed to execute getter or setter method in class "
                            + objectToAssign.getClass().getName() + " - " + e));
        } catch (IllegalArgumentException e) {
            parseErrors.add(new CellParseError(cell.getName(), cell.getStringValue(), null,
                    "Skipped assigning cell - Failed to execute getter or setter method in class "
                            + objectToAssign.getClass().getName() + " - " + e));
        } catch (IllegalAccessException e) {
            parseErrors.add(new CellParseError(cell.getName(), cell.getStringValue(), null,
                    "Skipped assigning cell - Failed to access getter or setter method in class "
                            + objectToAssign.getClass().getName() + " - " + e));
        } catch (NoSuchMethodException e) {
            parseErrors.add(new CellParseError(cell.getName(), cell.getStringValue(), null,
                    "Skipped assigning cell - The missing getter or setter method in class "
                            + objectToAssign.getClass().getName() + " or sub class - " + e));
        }
    }

    /**
     * @param <T>
     * @param cell
     * @param sName
     * @param objectToAssign
     * @param parseErrors
     * @return
     */
    private void assignAttribute(Cell cell, String sName, Object objectToAssign, List<CellParseError> parseErrors) {
        String sSetMethodName = createSetMethodName(sName);
        try {
            boolean success = assignParameterBySignature(objectToAssign, sSetMethodName, cell);
            if (!success) // Try again but use the name and try to cast.
                assignParameterByName(objectToAssign, sSetMethodName, cell, parseErrors);
        } catch (IllegalArgumentException e) {
            parseErrors.add(new CellParseError(cell.getName(), cell.getStringValue(), null,
                    "Skipped assigning cell - The method " + sSetMethodName + "() in class "
                            + objectToAssign.getClass().getName() + " does not accept correct type - " + e));
        } catch (IllegalAccessException e) {
            parseErrors.add(new CellParseError(cell.getName(), cell.getStringValue(), null,
                    "Skipped assigning cell - The method " + sSetMethodName + "() in class "
                            + objectToAssign.getClass().getName() + " does not have correct access - " + e));
        } catch (InvocationTargetException e) {
            parseErrors.add(new CellParseError(cell.getName(), cell.getStringValue(), null,
                    "Skipped assigning cell - The method " + sSetMethodName + "() in class "
                            + objectToAssign.getClass().getName() + " failed to execute - " + e));
        }
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

    /**
     * Assigns the cells of a line as attributes to an object.
     * 
     * @param <T>
     *            The type of the object to assign
     * @param cell
     *            The cell to get the parameter from.
     * @param objectToAssign
     *            The object to assign cell attributes to. The object will be modified.
     * @return True if the parameter was assigned to the object, false otherwise.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private <T> boolean assignParameterBySignature(T objectToAssign, String sSetMethodName, Cell cell)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        if(cell.getValue() == null)
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
     * @param <T>
     *            The type of the object to assign
     * @param cell
     *            The cell to get the parameter from.
     * @param objectToAssign
     *            The object to assign cell attributes to. The object will be modified.
     * @return True if the parameter was assigned to the object, false otherwise.
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private <T> boolean assignParameterByName(T objectToAssign,
                                              String sSetMethodName,
                                              Cell cell,
                                              List<CellParseError> parseErrors) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {

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
                        f.invoke(objectToAssign, ((Character) value).charValue());
                    } else {
                        String sValue = value.toString();
                        if (!sValue.isEmpty())
                            f.invoke(objectToAssign, sValue.charAt(0));
                    }
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
            parseErrors.add(new CellParseError(cell.getName(), cell.getStringValue(), null,
                    "Skipped assigning cell - No method called " + sSetMethodName + "() found in class "
                            + objectToAssign.getClass().getName() + " that fits the cell " + cell));
        } catch (SecurityException e) {
            parseErrors.add(new CellParseError(cell.getName(), cell.getStringValue(), null,
                    "Skipped assigning cell - The method " + sSetMethodName + "() in class "
                            + objectToAssign.getClass().getName() + " does not have public access - " + e));
        }
        return false;
    }

}
