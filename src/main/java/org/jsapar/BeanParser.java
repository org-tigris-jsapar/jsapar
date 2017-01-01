package org.jsapar;

import org.jsapar.parse.AbstractParser;
import org.jsapar.parse.LineEventListener;
import org.jsapar.parse.bean.BeanParseConfig;
import org.jsapar.parse.bean.BeanParseTask;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by stejon0 on 2017-01-01.
 */
public class BeanParser<T> extends AbstractParser {
    private BeanParseConfig parseConfig = new BeanParseConfig();


    public void parse(Iterator<? extends T> iterator, LineEventListener lineEventListener) throws IOException {
        BeanParseTask<T> parseTask = new BeanParseTask<>(iterator, parseConfig);
        execute(parseTask, lineEventListener);
    }

    public void parse(Collection<? extends T> collection, LineEventListener lineEventListener) throws IOException {
        parse(collection.iterator(), lineEventListener);
    }

    public BeanParseConfig getParseConfig() {
        return parseConfig;
    }

    public void setParseConfig(BeanParseConfig parseConfig) {
        this.parseConfig = parseConfig;
    }
}
