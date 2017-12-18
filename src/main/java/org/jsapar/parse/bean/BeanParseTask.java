package org.jsapar.parse.bean;

import org.jsapar.Bean2TextConverter;
import org.jsapar.error.ErrorEvent;
import org.jsapar.error.ErrorEventListener;
import org.jsapar.error.JSaParException;
import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.model.Line;
import org.jsapar.parse.AbstractParseTask;
import org.jsapar.parse.CellParseException;
import org.jsapar.parse.LineParsedEvent;
import org.jsapar.parse.ParseTask;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Uses a collection of java bean objects to build {@link LineParsedEvent}. The {@link Line#lineType} of each line will be
 * the name of the class denoted by {@link Class#getName()}. Each bean property that have a getter method will result in
 * a cell with the bean property name The {@link Cell#name} of each cell will be the name of the bean property, e.g. if
 * the bean has a method declared as {@code public int getNumber()}, it will result in a cell with the name "number" of
 * type {@link CellType}.INTEGER.
 * <p>
 * If you use these rules you can write a {@link org.jsapar.schema.Schema} that converts a bean to a different type of output.
 *
 * @see Bean2TextConverter
 */
public class BeanParseTask<T> extends AbstractParseTask implements ParseTask {

    private final BeanParseConfig config;

    private Stream<? extends T> stream;

    public BeanParseTask(Stream<? extends T> stream, BeanParseConfig config) {
        this.stream = stream;
        this.config = config;
    }

    public BeanParseTask(Iterator<? extends T> iterator, BeanParseConfig config) {
        this.stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
        this.config = config;
    }


    /**
     * Starts parsing of an iterated series of beans. The result will be line parsed events where each
     * line hav
     */
    @Override
    public long execute() {
        AtomicLong count = new AtomicLong(1);
        stream.forEach(bean ->
                lineParsedEvent(new LineParsedEvent(
                        this,
                        parseBean(bean, this, count.incrementAndGet()))));
        return count.get();
    }

    /**
     * Builds a line object according to the getter fields of the object. Each cell in the line will
     * be named according to the java bean attribute name. This means that if there is a member
     * method called <tt>getStreetAddress()</tt>, the name of the cell will be
     * <tt>streetAddress</tt>.
     *
     * @param object     The object.
     * @param lineNumber The number of the line being parsed. Numbering starts from 1.
     * @return A Line object containing cells according to the getter method of the supplied object.
     */
    Line parseBean(Object object, ErrorEventListener errorListener, long lineNumber) {

        Line line = new Line(object.getClass().getName());
        line.setLineNumber(lineNumber);
        Set<Object> visited = new HashSet<>();
        this.parseBean(line, object, null, visited, errorListener);
        return line;
    }


    @SuppressWarnings("unchecked")
    private void parseBean(Line line, Object object, String prefix, Set<Object> visited, ErrorEventListener errorListener) {

        // First we avoid loops.
        if (visited.contains(object) || visited.size() > config.getMaxSubLevels())
            return;
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
                Method f = pd.getReadMethod();
                String sAttributeName = pd.getName();
                if (f == null || "class".equals(sAttributeName))
                    continue;
                try {
                    sAttributeName = makeAttributeName(prefix, sAttributeName);
                    Optional<Cell> oCell = Bean2Cell.makeCell(object, f, sAttributeName);
                    oCell.ifPresent(line::addCell);
                    if (!oCell.isPresent()) {
                        Object subObject = f.invoke(object);
                        if (subObject == null)
                            continue;
                        // We only want to avoid loops not multiple paths to same object.
                        Set<Object> visitedClone = new HashSet<>(visited);
                        visitedClone.add(object);
                        // Recursively add sub classes.
                        this.parseBean(line, subObject, sAttributeName, visitedClone, errorListener);
                    }
                } catch (IllegalArgumentException e) {
                    handleCellError(errorListener, sAttributeName, object, line, "Illegal argument in getter method.");
                } catch (IllegalAccessException e) {
                    handleCellError(errorListener, sAttributeName, object, line, "Attribute getter does not have public access.");
                } catch (InvocationTargetException e) {
                    handleCellError(errorListener, sAttributeName, object, line, "Getter method fails to execute.");
                }
            }
        } catch (IntrospectionException e) {
            throw new JSaParException("Unable to parse bean", e);
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
     *
     * @param prefix   A prefix that will be appended before the attribute name.
     * @param property The attribute name.
     * @return The attribute name that is built from the getter name.
     */
    private String makeAttributeName(String prefix, String property) {
        StringBuilder sb = new StringBuilder();
        if (prefix != null) {
            sb.append(prefix);
            sb.append('.');
        }
        sb.append(property);
        return sb.toString();
    }

}
