/**
 * 
 */
package org.jsapar;

import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.model.*;
import org.jsapar.parse.AbstractParser;
import org.jsapar.parse.CellParseException;
import org.jsapar.parse.LineParsedEvent;
import org.jsapar.parse.Parser;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Uses a collection of java objects to build a org.jsapar.model.Document.
 * @author stejon0
 * 
 */
public class BeanParser<T> extends  AbstractParser implements Parser{
    

    private int maxSubLevels = 100;
    private Iterator<? extends T> iterator;

    public BeanParser(Iterator<? extends T> iterator) {
        this.iterator = iterator;
    }

    public BeanParser(Collection<? extends T> objects) {
        this.iterator = objects.iterator();
    }

    /**
     *
     */
    @Override
    public void parse() throws IOException {
        long count = 0;
        while(iterator.hasNext()){
            count++;
            lineParsedEvent( new LineParsedEvent(this, parseBean(iterator.next(), this, count)) );
        }
    }

    /**
     * Builds a line object according to the getter fields of the object. Each cell in the line will
     * be named according to the java bean attribute name. This means that if there is a member
     * method called <tt>getStreetAddress()</tt>, the name of the cell will be
     * <tt>streetAddress</tt>.
     * 
     * @param object
     *            The object.
     * @param lineNumber
     * @return A Line object containing cells according to the getter method of the supplied object.
     *
     */
    public Line parseBean(Object object, ErrorEventListener errorListener, long lineNumber)  {

        Line line = new Line(object.getClass().getName());
        line.setLineNumber(lineNumber);
        Set<Object> visited = new HashSet<>();
        this.parseBean(line, object, null, visited, errorListener);
        return line;
    }
    

    @SuppressWarnings("unchecked")
    private void parseBean(Line line, Object object, String prefix, Set<Object> visited, ErrorEventListener errorListener)  {

        // First we avoid loops.
        if(visited.contains(object) || visited.size()  >  maxSubLevels)
            return;
        
        Method[] methods = object.getClass().getMethods();

        for (Method f : methods) {
            String sAttributeName="?";
            try {
                String sMethodName = f.getName();
                if (f.getParameterTypes().length == 0 && sMethodName.length() > 3
                        && sMethodName.substring(0, 3).equals("get")) {
                    sAttributeName = makeAttributeName(prefix, sMethodName);
                    @SuppressWarnings("rawtypes")
                    Class returnType = f.getReturnType();

                    if (returnType.isAssignableFrom(Class.class)) {
                        //noinspection UnnecessaryContinue
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
                        Set<Object> visitedClone = new HashSet<>(visited);
                        visitedClone.add(object);
                        // Recursively add sub classes.
                        this.parseBean(line, subObject, sAttributeName, visitedClone, errorListener);
                    }
                }
            } catch (IllegalArgumentException e) {
                errorListener.errorEvent(new ErrorEvent(this, new CellParseException(sAttributeName, "", null, "Skipped building cell for attribute "+sAttributeName+" of class "+ object.getClass().getName()+" - Illegal argument in getter method.")));
            } catch (IllegalAccessException e) {
                errorListener.errorEvent(new ErrorEvent(this, new CellParseException(sAttributeName, "", null, "Skipped building cell for attribute "+sAttributeName+" of class "+ object.getClass().getName()+" - attribute getter does not have public access.")));
            } catch (InvocationTargetException e) {
                errorListener.errorEvent(new ErrorEvent(this, new CellParseException(sAttributeName, "", null, "Skipped building cell for attribute "+sAttributeName+" of class "+ object.getClass().getName()+" - getter method fails to execute.")));
            }
        }
    }

    /**
     * Creates the attribute name based on get method name.
     * @param prefix A prefix that will be appended before the attribute name.
     * @param sMethodName The method that is used to construct the attribute name.
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
     * @param maxSubLevels Maximum number of sub-objects that are read while storing a line object
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
