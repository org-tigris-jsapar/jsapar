package org.jsapar.parse.bean;

import org.jsapar.Bean2TextConverter;
import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.model.*;
import org.jsapar.parse.AbstractParseTask;
import org.jsapar.parse.CellParseException;
import org.jsapar.parse.LineParsedEvent;
import org.jsapar.parse.ParseTask;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Uses a collection of java bean objects to build {@link LineParsedEvent}. The {@link Line#lineType} of each line will be
 * the name of the class denoted by {@link Class#getName()}. Each bean property that have a getter method will result in
 * a cell with the bean property name The {@link Cell#name} of each cell will be the name of the bean property, e.g. if
 * the bean has a method declared as {@code public int getNumber()}, it will result in a cell with the name "number" of
 * type {@link CellType}.INTEGER.
 *
 * If you use these rules you can write a {@link org.jsapar.schema.Schema} that converts a bean to a different type of output.
 * @see Bean2TextConverter
 */
public class BeanParseTask<T> extends AbstractParseTask implements ParseTask {

    private final BeanParseConfig config;

    private Iterator<? extends T> iterator;

    public BeanParseTask(Iterator<? extends T> iterator, BeanParseConfig config) {
        this.iterator = iterator;
        this.config = config;
    }


    /**
     * Starts parsing of an iterated series of beans. The result will be line parsed events where each
     * line hav
     */
    @Override
    public void execute() throws IOException {
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
     * @param lineNumber The number of the line being parsed. Numbering starts from 1.
     * @return A Line object containing cells according to the getter method of the supplied object.
     *
     */
    Line parseBean(Object object, ErrorEventListener errorListener, long lineNumber)  {

        Line line = new Line(object.getClass().getName());
        line.setLineNumber(lineNumber);
        Set<Object> visited = new HashSet<>();
        this.parseBean(line, object, null, visited, errorListener);
        return line;
    }
    

    @SuppressWarnings("unchecked")
    private void parseBean(Line line, Object object, String prefix, Set<Object> visited, ErrorEventListener errorListener)  {

        // First we avoid loops.
        if(visited.contains(object) || visited.size()  >  config.getMaxSubLevels())
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
                handleCellError(errorListener, sAttributeName, object, line, "Illegal argument in getter method.");
            } catch (IllegalAccessException e) {
                handleCellError(errorListener, sAttributeName, object, line, "Attribute getter does not have public access.");
            } catch (InvocationTargetException e) {
                handleCellError(errorListener, sAttributeName, object, line, "Getter method fails to execute.");
            }
        }
    }

    private void handleCellError(ErrorEventListener errorListener,
                                 String sAttributeName,
                                 Object object,
                                 Line line,
                                 String message) {
        CellParseException error = new CellParseException(sAttributeName, "", null,
                "Unable to build cell for attribute " + sAttributeName + " of class " + object.getClass().getName()
                        + " - " + message);
        line.addCellError(error);
        errorListener.errorEvent(new ErrorEvent(this, error));
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

}
