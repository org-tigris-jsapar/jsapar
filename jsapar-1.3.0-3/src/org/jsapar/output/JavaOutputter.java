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

    /**
     * Creates a list of java objects. For this method to work, the lineType attribute of each line
     * have to contain the full class name of the class to create for each line. Also the set method
     * for each attribute have to match exactly to the name of each cell.
     * 
     * @param document
     * @return A list of Java objects.
     */
    @SuppressWarnings("unchecked")
    public java.util.List createJavaObjects(Document document, List<CellParseError> parseErrors) {
        java.util.List objects = new java.util.ArrayList(document.getNumberOfLines());
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
                parseErrors.add(new CellParseError("", "", null,
                        "Class not found. Skipped creating object - " + e));
            } catch (Throwable e) {
                parseErrors.add(new CellParseError(0, "", "", null,
                        "Skipped creating object - " + e.getMessage(), e));
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
    @SuppressWarnings("unchecked")
    public Object createObject(Line line, List<CellParseError> parseErrors) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        Class c = Class.forName(line.getLineType());
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
            if (sName == null || sName.length() == 0)
                continue;

            String sSetMethodName = "set" + sName.substring(0, 1).toUpperCase() + sName.substring(1, sName.length());
            boolean success;
            try {
                success = assignParameterBySignature(objectToAssign, sSetMethodName, cell);
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
        return objectToAssign;
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
    @SuppressWarnings("unchecked")
    private <T> boolean assignParameterBySignature(T objectToAssign, String sSetMethodName, Cell cell)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        try {
            Class type = cell.getValue().getClass();
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
    @SuppressWarnings("unchecked")
    private <T> boolean assignParameterByName(T objectToAssign,
                                              String sSetMethodName,
                                              Cell cell,
                                              List<CellParseError> parseErrors) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {

        try {
            Method[] methods = objectToAssign.getClass().getMethods();
            for (Method f : methods) {
                Class[] paramTypes = f.getParameterTypes();
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
