package org.jsapar;

import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.model.Line;
import org.jsapar.parse.AbstractParser;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.LineParsedEvent;
import org.jsapar.parse.bean.BeanParseConfig;
import org.jsapar.parse.bean.BeanParseTask;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Uses a collection of java bean objects to build {@link LineParsedEvent}. The {@link Line#lineType} of each line will be
 * the name of the class denoted by {@link Class#getName()}. Each bean property that have a getter method will result in
 * a cell with the bean property name The {@link Cell#name} of each cell will be the name of the bean property, e.g. if
 * the bean has a method declared as {@code public int getNumber()}, it will result in a cell with the name "number" of
 * type {@link CellType}.INTEGER.
 *
 * If you use these rules you can write a {@link org.jsapar.schema.Schema} that converts a bean to a different type of output.
 *
 * The default error handling is to throw an exception upon the first error that occurs. You can however change that
 * behavior by adding an {@link org.jsapar.error.ErrorEventListener}. There are several implementations to choose from such as
 * {@link org.jsapar.error.RecordingErrorEventListener} or
 * {@link org.jsapar.error.ThresholdRecordingErrorEventListener}, or you may implement your own..
 *
 * @see Bean2TextConverter
 */
public class BeanParser<T> extends AbstractParser {
    private BeanParseConfig parseConfig = new BeanParseConfig();

    /**
     * Parses beans supplied by the iterator and produces {@link LineParsedEvent} to the provided lineEventListener for
     * each bean within the iterator.
     * @param iterator The iterator to get beans from.
     * @param lineEventListener The listener that will receive events for each parsed bean.
     */
    public void parse(Iterator<? extends T> iterator, LineEventListener lineEventListener)  {
        BeanParseTask<T> parseTask = new BeanParseTask<>(iterator, parseConfig);
        try {
            execute(parseTask, lineEventListener);
        } catch (IOException e) {
            throw new AssertionError("Parsing beans should never throw IOException", e);
        }
    }

    /**
     * Parses beans supplied within the collection and produces {@link LineParsedEvent} to the provided lineEventListener for
     * each bean within the iterator.
     * @param collection The collection to get beans from.
     * @param lineEventListener The listener that will receive events for each parsed bean.
     */
    public void parse(Collection<? extends T> collection, LineEventListener lineEventListener) {
        parse(collection.iterator(), lineEventListener);
    }

    /**
     * @return A config object holding configuration for this parser.
     */
    @SuppressWarnings("WeakerAccess")
    public BeanParseConfig getParseConfig() {
        return parseConfig;
    }

    /**
     * Sets new configuration to this bean parser.
     * @param parseConfig The new configuration instance to use.
     */
    @SuppressWarnings("WeakerAccess")
    public void setParseConfig(BeanParseConfig parseConfig) {
        this.parseConfig = parseConfig;
    }
}
