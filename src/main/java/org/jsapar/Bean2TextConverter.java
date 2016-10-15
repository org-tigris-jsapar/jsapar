package org.jsapar;

import org.jsapar.schema.Schema;

import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

/**
 * Converts from beans to text output.
 * The Generic type T should be set to a common base class of all the expected beans. Use Object as
 * base class if there is no common base class for all beans.
 * Created by stejon0 on 2016-10-15.
 */
public class Bean2TextConverter<T> extends Converter {

    public Bean2TextConverter(BeanParser parser, TextComposer composer) {
        super(parser, composer);
    }

    public Bean2TextConverter(Iterator<? extends T> iterator, Schema outputSchema, Writer writer) {
        super(new BeanParser<>(iterator), new TextComposer(outputSchema, writer));
    }

    public Bean2TextConverter(Collection<? extends T> collection, Schema outputSchema, Writer writer) {
        super(new BeanParser<>(collection), new TextComposer(outputSchema, writer));
    }

}
