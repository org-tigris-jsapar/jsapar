package org.jsapar.compose.csv.quote;

import org.jsapar.schema.QuoteSyntax;
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

/**
 */
public class AlwaysQuoteTest {
    @Test
    public void writeQuoted_maxLength() throws Exception {
        AlwaysQuote instance = new AlwaysQuote( '/', 10, QuoteSyntax.FIRST_LAST);

        StringWriter w = new StringWriter();
        instance.writeValue(w, "hej");
        assertEquals("/hej/", w.toString());


        w = new StringWriter();
        instance.writeValue(w, "hej;san123456");
        assertEquals("/hej;san1/", w.toString());
    }

    @Test
    public void writeQuoted_atomic() throws Exception {
        AlwaysQuote instance = new AlwaysQuote( '/', -1, QuoteSyntax.FIRST_LAST);

        StringWriter w = new StringWriter();
        instance.writeValue(w, "hej");
        assertEquals("/hej/", w.toString());


        w = new StringWriter();
        instance.writeValue(w, "hej;san123456");
        assertEquals("/hej;san123456/", w.toString());
    }

    @Test
    public void writeValueQuotedRFC() throws Exception {
        Quoter instance = new AlwaysQuote( '/', -1, QuoteSyntax.RFC4180);

        StringWriter w = new StringWriter();
        instance.writeValue(w, "hej");
        assertEquals("/hej/", w.toString());


        w = new StringWriter();
        instance.writeValue(w, "hej/san/123456");
        assertEquals("/hej//san//123456/", w.toString());

        w = new StringWriter();
        instance.writeValue(w, "/hej//san/123456/");
        assertEquals("///hej////san//123456///", w.toString());

    }

    @Test
    public void writeValueQuotedRFC_maxLength() throws Exception {
        Quoter instance = new AlwaysQuote( '/',12, QuoteSyntax.RFC4180);

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