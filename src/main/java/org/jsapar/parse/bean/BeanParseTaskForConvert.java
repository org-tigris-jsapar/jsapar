package org.jsapar.parse.bean;

import org.jsapar.error.ErrorEventListener;
import org.jsapar.model.Line;
import org.jsapar.parse.AbstractParseTask;
import org.jsapar.parse.LineParsedEvent;
import org.jsapar.parse.ParseTask;

import java.io.IOException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Specially designed {@link ParseTask} implementation to be used for converting.
 * Same as {@link BeanParseTask} except that this implementation does not supply complete lines when parsing. Instead it
 * supplies {@link BeanBackedLine} instances that creates cell instances on-the-fly from the bean when called upon. This
 * means that it will not create unnecessary large line instances when converting only because the bean had a lot of
 * getter methods. Instead it will create only those cells needed for the convert task.
 * @param <T>
 */
public class BeanParseTaskForConvert<T> extends AbstractParseTask implements ParseTask {

    private Stream<? extends T> stream;

    public BeanParseTaskForConvert(Stream<? extends T> stream) {
        this.stream = stream;
    }

    public BeanParseTaskForConvert(Iterator<? extends T> iterator) {
        this.stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
    }

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
     * Creates a line that is backed by supplied bean. Cells will be created on-the-fly when requested from the line.
     * Each cell in the line will
     * be named according to the java bean attribute name. This means that if there is a member
     * method called <tt>getStreetAddress()</tt>, the name of the cell will be
     * <tt>streetAddress</tt>.
     *
     * @param object     The object.
     * @param lineNumber The number of the line being parsed. Numbering starts from 1.
     * @return A Line object containing cells according to the getter method of the supplied object.
     */
    Line parseBean(T object, ErrorEventListener errorListener, long lineNumber) {
        Line line = new BeanBackedLine<>(object, errorListener);
        line.setLineNumber(lineNumber);
        return line;
    }

}
