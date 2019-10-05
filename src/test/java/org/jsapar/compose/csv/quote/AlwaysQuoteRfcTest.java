package org.jsapar.compose.csv.quote;

import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.*;

public class AlwaysQuoteRfcTest {

    @Test
    public void writeValueQuoted() throws Exception {
        Quoter instance = new AlwaysQuoteRfc( '/','%', -1);

        StringWriter w = new StringWriter();
        instance.writeValue(w, "hej");
        assertEquals("/hej/", w.toString());


        w = new StringWriter();
        instance.writeValue(w, "hej/san/123456");
        assertEquals("/hej%/san%/123456/", w.toString());
    }
}