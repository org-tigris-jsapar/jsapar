package org.jsapar.compose.csv.quote;

import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

/**
 */
public class AlwaysQuoteTest {
    @Test
    public void writeQuoted_maxLength() throws Exception {
        AlwaysQuote instance = new AlwaysQuote( '/', 10, false);

        StringWriter w = new StringWriter();
        instance.writeValue(w, "hej");
        assertEquals("/hej/", w.toString());


        w = new StringWriter();
        instance.writeValue(w, "hej;san123456");
        assertEquals("/hej;san1/", w.toString());
    }

    @Test
    public void writeQuoted_atomic() throws Exception {
        AlwaysQuote instance = new AlwaysQuote( '/', -1, false);

        StringWriter w = new StringWriter();
        instance.writeValue(w, "hej");
        assertEquals("/hej/", w.toString());


        w = new StringWriter();
        instance.writeValue(w, "hej;san123456");
        assertEquals("/hej;san123456/", w.toString());
    }

    @Test
    public void writeValueQuotedRFC() throws Exception {
        Quoter instance = new AlwaysQuote( '/', -1, true);

        StringWriter w = new StringWriter();
        instance.writeValue(w, "hej");
        assertEquals("/hej/", w.toString());


        w = new StringWriter();
        instance.writeValue(w, "hej/san/123456");
        assertEquals("/hej//san//123456/", w.toString());
    }

    @Test
    public void writeValueQuotedRFC_maxLength() throws Exception {
        Quoter instance = new AlwaysQuote( '/',12, true);

        StringWriter w = new StringWriter();
        instance.writeValue(w, "hej");
        assertEquals("/hej/", w.toString());


        w = new StringWriter();
        instance.writeValue(w, "hej/san/123456");
        assertEquals("/hej//san///", w.toString());

        w = new StringWriter();
        instance.writeValue(w, "hej/sans/123456");
        assertEquals("/hej//sans/", w.toString());

    }

}