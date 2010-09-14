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
import java.util.HashSet;
import java.util.Set;
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
 * Uses a collection of java objects to build a org.jsapar.Document.
 * @author stejon0
 * 
 */
public class JavaBuilder {
    
    protected static Logger logger = Logger.getLogger("org.jsapar");

    private int maxSubLevels = 100;
    /**
     * @param objects
     *            The collection of objects to use when building the document.
     * @return A document containing a collection of lines which represents the list of objects
     *         supplied.
     * @throws JSaParException
     */
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
    public Line buildLine(Object object) throws JSaParException {

        Line line = new Line(object.getClass().getName());
        Set<Object> visited = new HashSet<Object>();
        this.buildLine(line, object, null, visited);
        return line;
    }
    

    @SuppressWarnings("unchecked")
    private void buildLine(Line line, Object object, String prefix, Set<Object> visited) throws JSaParException {

        // First we avoid loops.
        if(visited.contains(object) || visited.size()  >  maxSubLevels)
            return;
        
        Method[] methods = object.getClass().getMethods();
        Object[] logInfo = new Object[] { object.getClass().getName(), null };

        for (Method f : methods) {
            try {
                String sMethodName = f.getName();
                if (f.getParameterTypes().length == 0 && sMethodName.length() > 3
                        && sMethodName.substring(0, 3).equals("get")) {
                    String sAttributeName = makeAttributeName(prefix, sMethodName);
                    logInfo[1] = sAttributeName;
                    @SuppressWarnings("rawtypes")
                    Class returnType = f.getReturnType();

                    if (returnType.isAssignableFrom(Class.class)) {
                        continue;
                    } else if (returnType.isAssignableFrom(String.class)) {
                        String value = (String) f.invoke(object);
                        if (value != null)
                            line.addCell(new StringCell(sAttributeName, value));
                    } else if (returnType.isAssignableFrom(Character.TYPE)
                            || returnType.isAssignableFrom(Character.class)) {
                        line.addCell(new StringCell(sAttributeName, (Character) f.invoke(object)));
                    } else if (returnType.isAssignableFrom(Date.class)) {
                        Date value = (Date) f.invoke(object);
                        if (value != null)
                            line.addCell(new DateCell(sAttributeName, value));
                    } else if (returnType.isAssignableFrom(Calendar.class)) {
                        Calendar value = (Calendar) f.invoke(object);
                        if (value != null)
                            line.addCell(new DateCell(sAttributeName, value.getTime()));
                    } else if (returnType.isAssignableFrom(Integer.TYPE) || returnType.isAssignableFrom(Integer.class)) {
                        line.addCell(new IntegerCell(sAttributeName, (Integer) f.invoke(object)));
                    } else if (returnType.isAssignableFrom(Byte.TYPE) || returnType.isAssignableFrom(Byte.class)) {
                        line.addCell(new IntegerCell(sAttributeName, (Byte) f.invoke(object)));
                    } else if (returnType.isAssignableFrom(Short.TYPE) || returnType.isAssignableFrom(Short.class)) {
                        line.addCell(new IntegerCell(sAttributeName, (Short) f.invoke(object)));
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
                        Object subObject = f.invoke(object);
                        if(subObject == null)
                            continue;
                        // We only want to avoid loops not multiple paths to same object.
                        Set<Object> visitedClone = new HashSet<Object>(visited);
                        visitedClone.add(object);
                        // Recursively add sub classes.
                        this.buildLine(line, subObject, sAttributeName, visitedClone);
                    }
                }
            } catch (IllegalArgumentException e) {
                logger.log(Level.INFO,
                        "Skipped building cell for attribute {1} of class {0} - Illegal argument in getter method.",
                        logInfo);
            } catch (IllegalAccessException e) {
                logger.log(Level.INFO,
                           "Skipped building cell for attribute {1} of class {0} - attibute getter does not have public access.",
                           logInfo);
            } catch (InvocationTargetException e) {
                logger.log(Level.INFO,
                        "Skipped building cell for attribute {1} of class {0} - getter method fails to execute.",
                        logInfo);
            }
        }
    }

    /**
     * Creates the attribute name based on get method name.
     * @param prefix
     * @param sMethodName
     * @return The attribute name that is built from the getter name.
     */
    private String makeAttributeName(String prefix, String sMethodName) {
        StringBuilder sb = new StringBuilder();
        if (prefix != null) {
            sb.append(prefix);
            sb.append('.');
        }
        sb.append(sMethodName.substring(3, 4).toLowerCase());
        sb.append(sMethodName.substring(4));
        return sb.toString();
    }

    /**
     * Sets maximum number of sub-objects that are read while storing a line object.
     * @param maxSubLevels
     */
    public void setMaxSubLevels(int maxSubLevels) {
        this.maxSubLevels = maxSubLevels;
    }

    /**
     * @return The maximum number of sub-objects that are read while storing a line object.
     */
    public int getMaxSubLevels() {
        return maxSubLevels;
    }

}
