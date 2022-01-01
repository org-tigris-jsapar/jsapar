package org.jsapar.parse.xml;

import org.jsapar.model.Line;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.util.Spliterator;
import java.util.function.Consumer;

public class XmlParseSplitterator implements Spliterator<Line> {
    private final XMLStreamReader streamReader;

    public XmlParseSplitterator(Reader reader) throws XMLStreamException {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        streamReader = inputFactory.createXMLStreamReader(reader);
    }

    @Override
    public boolean tryAdvance(Consumer<? super Line> action) {
        return false;
    }

    @Override
    public Spliterator<Line> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return IMMUTABLE|ORDERED|NONNULL;
    }
}
