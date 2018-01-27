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
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
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

    private final BeanMap beanMap;

    private Stream<? extends T> stream;

    public BeanParseTask(Stream<? extends T> stream, BeanMap beanMap) {
        this.stream = stream;
        this.beanMap = beanMap;
    }

    public BeanParseTask(Iterator<? extends T> iterator, BeanMap beanMap) {
        this.stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
        this.beanMap = beanMap;
    }


    /**
     * Starts parsing of an iterated series of beans. The result will be line parsed events where each
     * line hav
     */
    @Override
    public long execute() {
        AtomicLong count = new AtomicLong(1);
        stream.forEach(bean ->
                parseBean(bean, this, count.incrementAndGet()).ifPresent(line ->
                        lineParsedEvent(new LineParsedEvent(
                                this,
                                line))));
        return count.get();
    }

    /**
     * Builds a line bean according to the getter fields of the bean. Each cell in the line will
     * be named according to the java bean attribute name. This means that if there is a member
     * method called <tt>getStreetAddress()</tt>, the name of the cell will be
     * <tt>streetAddress</tt>.
     *
     * @param bean     The bean.
     * @param lineNumber The number of the line being parsed. Numbering starts from 1.
     * @return A Line bean containing cells according to the getter method of the supplied bean.
     */
    Optional<Line> parseBean(T bean, ErrorEventListener errorListener, long lineNumber) {
        return beanMap.getBean2Line(bean.getClass()).map(bean2Line -> {
            Line line = new Line(bean2Line.getLineType());
            line.setLineNumber(lineNumber);
            this.parseBean(line, bean, bean2Line, errorListener);
            return line;
        });
    }


    @SuppressWarnings("unchecked")
    private void parseBean(Line line, Object object, Bean2Line bean2Line, ErrorEventListener errorListener) {

        for (Bean2Cell bean2Cell : bean2Line.getBean2Cells()) {
            PropertyDescriptor pd = bean2Cell.getPropertyDescriptor();

            try {
                Bean2Line children = bean2Cell.getChildren();
                if (children != null) {
                    Object subObject = pd.getReadMethod().invoke(object);
                    if (subObject == null)
                        continue;
                    // Recursively add sub classes.
                    this.parseBean(line, subObject, children, errorListener);
                } else
                    bean2Cell.makeCell(object).ifPresent(line::addCell);
            } catch (IllegalArgumentException e) {
                handleCellError(errorListener, bean2Cell.getCellName(), object, line, "Illegal argument in getter method.");
            } catch (IllegalAccessException e) {
                handleCellError(errorListener, bean2Cell.getCellName(), object, line, "Attribute getter does not have public access.");
            } catch (InvocationTargetException e) {
                handleCellError(errorListener, bean2Cell.getCellName(), object, line, "Getter method fails to execute.");
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
