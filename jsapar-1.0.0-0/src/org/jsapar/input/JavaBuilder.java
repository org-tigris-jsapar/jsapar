/**
 * 
 */
package org.jsapar.input;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsapar.BigDecimalCell;
import org.jsapar.BooleanCell;
import org.jsapar.DateCell;
import org.jsapar.Document;
import org.jsapar.FloatCell;
import org.jsapar.IntegerCell;
import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.StringCell;

/**
 * @author stejon0
 * 
 */
public class JavaBuilder {

    protected static Logger logger = Logger.getLogger("org.jsapar");

    /**
     * @param <T>
     *            Type of the object of which the document will be built.
     * @param objects
     *            The collection of objects to use when building the document.
     * @return A document containing a collection of lines which represents the list of objects
     *         supplied.
     * @throws JSaParException
     */
    @SuppressWarnings("unchecked")
    public Document build(Collection objects) throws JSaParException {
        Document doc = new Document();
        for (Object object : objects) {
            doc.addLine(buildLine(object));
        }
        return doc;
    }

    /**
     * Builds a line object according to the getter fields of the object. Each cell in the line will
     * be named according to the java bean attribute name. This means that if there is a member
     * method called <tt>getStreetAddress()</tt>, the name of the cell will be
     * <tt>streetAddress</tt>.
     * 
     * @param object
     *            The object.
     * @return A Line object containing cells according to the getter method of the supplied object.
     * @throws JSaParException
     */
    @SuppressWarnings("unchecked")
    public Line buildLine(Object object) throws JSaParException {

        Method[] methods = object.getClass().getMethods();
        Object[] logInfo = new Object[] { object.getClass().getName(), null };

        Line line = new Line(object.getClass().getName());

        for (Method f : methods) {
            try {
                String sMethodName = f.getName();
                if (f.getParameterTypes().length == 0 && sMethodName.length() > 3
                        && sMethodName.substring(0, 3).equals("get")) {
                    String sAttributeName = f.getName().substring(3, 4).toLowerCase();
                    sAttributeName += f.getName().substring(4, sMethodName.length());
                    logInfo[1] = sAttributeName;
                    Class returnType = f.getReturnType();

                    if (returnType.isAssignableFrom(Class.class)) {
                        continue;
                    } else if (returnType.isAssignableFrom(String.class)) {
                        line.addCell(new StringCell(sAttributeName, (String) f.invoke(object)));
                    } else if (returnType.isAssignableFrom(Character.TYPE)
                            || returnType.isAssignableFrom(Character.class)) {
                        line.addCell(new StringCell(sAttributeName, (Character) f.invoke(object)));
                    } else if (returnType.isAssignableFrom(Date.class)) {
                        line.addCell(new DateCell(sAttributeName, (Date) f.invoke(object)));
                    } else if (returnType.isAssignableFrom(Calendar.class)) {
                        line.addCell(new DateCell(sAttributeName, ((Calendar) f.invoke(object)).getTime()));
                    } else if (returnType.isAssignableFrom(Integer.TYPE) || returnType.isAssignableFrom(Integer.class)
                            || returnType.isAssignableFrom(Short.TYPE) || returnType.isAssignableFrom(Short.class)
                            || returnType.isAssignableFrom(Byte.TYPE) || returnType.isAssignableFrom(Byte.class)) {
                        line.addCell(new IntegerCell(sAttributeName, (Integer) f.invoke(object)));
                    } else if (returnType.isAssignableFrom(Long.TYPE) || returnType.isAssignableFrom(Long.class)) {
                        line.addCell(new IntegerCell(sAttributeName, (Long) f.invoke(object)));
                    } else if (returnType.isAssignableFrom(Boolean.TYPE) || returnType.isAssignableFrom(Boolean.class)) {
                        line.addCell(new BooleanCell(sAttributeName, (Boolean) f.invoke(object)));
                    } else if (returnType.isAssignableFrom(Float.TYPE) || returnType.isAssignableFrom(Float.class)) {
                        line.addCell(new FloatCell(sAttributeName, (Float) f.invoke(object)));
                    } else if (returnType.isAssignableFrom(Double.TYPE) || returnType.isAssignableFrom(Double.class)) {
                        line.addCell(new FloatCell(sAttributeName, (Double) f.invoke(object)));
                    } else if (returnType.isAssignableFrom(BigDecimal.class)) {
                        line.addCell(new BigDecimalCell(sAttributeName, (BigDecimal) f.invoke(object)));
                    } else if (returnType.isAssignableFrom(BigInteger.class)) {
                        line.addCell(new BigDecimalCell(sAttributeName, (BigInteger) f.invoke(object)));
                    } else {
                        logger.log(Level.FINE, "Skipped building cell - No the type " + returnType
                                + " of attribute {1} in class {0} is not supported for assigning.", logInfo);
                    }
                }
            } catch (IllegalArgumentException e) {
                logger.log(Level.INFO,
                        "Skipped building cell for attribute {1} of class {0} - Illegal argument in getter method.",
                        logInfo);
            } catch (IllegalAccessException e) {
                logger
                        .log(
                                Level.INFO,
                                "Skipped building cell for attribute {1} of class {0} - attibute getter does not have public access.",
                                logInfo);
            } catch (InvocationTargetException e) {
                logger.log(Level.INFO,
                        "Skipped building cell for attribute {1} of class {0} - getter method fails to execute.",
                        logInfo);
            }
        }
        return line;
    }
    /*
     * }
     * 
     * while (cellIter.hasNext()) { try { Cell cell = cellIter.next(); if (cell.getName() == null)
     * continue; String sName = cell.getName(); if (sName != null && sName.length() > 0) { String
     * sSetMethodName = "set" + sName.substring(0, 1).toUpperCase() + sName.substring(1,
     * sName.length()); logInfo[1] = sSetMethodName; boolean isSet = false; for (Method f : methods)
     * { if (f.getName().equals(sSetMethodName)) { f.invoke(objectToAssign, cell.getValue()); isSet
     * = true; logger.finest("Assigned cell by calling {1} of {0}"); break; } } if (!isSet) {
     * logger.log(Level.INFO, "Skipped assigning cell - No method called {1}() found in class {0}",
     * logInfo); } } } catch (SecurityException e) { logInfo[2] = e; logger.log(Level.INFO,
     * "Skipped assigning cell - The method {1}() in class {0} does not have public access. - {2}",
     * logInfo); } catch (IllegalArgumentException e) { logInfo[2] = e; logger.log(Level.INFO,
     * "Skipped assigning cell - The method {1}() in class {0} does accept correct type. - {2}",
     * logInfo); } catch (IllegalAccessException e) { logInfo[2] = e; logger.log(Level.INFO,
     * "Skipped assigning cell - The method {1}() in class {0} does not have correct access. - {2}",
     * logInfo); } catch (InvocationTargetException e) { logInfo[2] = e; logger.log(Level.INFO,
     * "Skipped assigning cell - The method {1}() in class {0} fails to execute. - {2}", logInfo); }
     * } return objectToAssign; }
     */
}
