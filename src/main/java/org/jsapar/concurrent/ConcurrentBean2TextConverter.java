package org.jsapar.concurrent;

import org.jsapar.Bean2TextConverter;
import org.jsapar.convert.AbstractConverter;
import org.jsapar.parse.bean.BeanMap;
import org.jsapar.schema.Schema;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * A multi threaded version of {@link org.jsapar.Bean2TextConverter} where the composer is started in a separate worker
 * thread.
 * See {@link AbstractConverter} for details about error handling and manipulating data.
 * <p>
 * As a rule of thumb while working with normal files on disc, don't use this concurrent version unless your input
 * normally exceeds at least 1MB of data, as the overhead of starting
 * a new thread and synchronizing threads are otherwise greater than the gain by the concurrency.
 * @param <T> The base class for the beans to convert.
 */
public class ConcurrentBean2TextConverter<T> extends Bean2TextConverter<T>{
    private ConcurrentConvertTaskFactory convertTaskFactory = new ConcurrentConvertTaskFactory();

    public ConcurrentBean2TextConverter(Schema composerSchema) throws IntrospectionException, ClassNotFoundException {
        super(composerSchema);
    }

    public ConcurrentBean2TextConverter(Schema composerSchema, BeanMap beanMap) {
        super(composerSchema, beanMap);
    }

    @Override
    public void convert(Stream<? extends T> stream, Writer writer) throws IOException {
        execute(convertTaskFactory.makeConvertTask(makeParseTask(stream), makeComposer(writer)));
    }

    @Override
    public void convert(Iterator<? extends T> iterator, Writer writer) throws IOException {
        execute(convertTaskFactory.makeConvertTask(makeParseTask(iterator), makeComposer(writer)));
    }

    /**
     * Each registered onStart runnable will be called in the same order that they were registered by consumer thread
     * when it starts up but before it starts handling any event. Use this in order to
     * implement initialization needed for the new
     * thread.
     *
     * @param onStart The runnable that will be called by consumer thread when starting up.
     */
    public void registerOnStart(Runnable onStart) {
        this.convertTaskFactory.registerOnStart(onStart);
    }

    /**
     * Each registered onStop runnable will be called in the same order that they were registered by consumer
     * thread just before it dies. Use this in order to
     * implement resource deallocation etc. These handlers are called also when the thread is terminated with an exception so
     * be aware that you may end up here also when a serious error has occurred.
     *
     * @param onStop The runnable that will be called by consumer thread when stopping.
     */
    public void registerOnStop(Runnable onStop) {
        this.convertTaskFactory.registerOnStop(onStop);
    }
}
