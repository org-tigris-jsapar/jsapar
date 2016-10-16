package org.jsapar;

import org.jsapar.compose.bean.BeanComposedEventListener;
import org.jsapar.compose.bean.BeanComposer;
import org.jsapar.schema.Schema;

import java.io.Reader;

/**
 * Created by stejon0 on 2016-02-13.
 */
public class Text2BeanConverter<T> extends Converter{

    public Text2BeanConverter(Schema inputSchema, Reader reader) {
        super(new TextParser(inputSchema, reader), new BeanComposer());
    }

    public Text2BeanConverter(Schema inputSchema, Reader reader, BeanComposer<T> composer) {
        super(new TextParser(inputSchema, reader), composer);
    }

    public void addComposedEventListener(BeanComposedEventListener<T> eventListener) {
        ((BeanComposer<T>)getComposer()).addComposedEventListener(eventListener);
    }


}
