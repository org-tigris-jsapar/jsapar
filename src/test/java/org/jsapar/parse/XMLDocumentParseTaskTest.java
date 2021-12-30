package org.jsapar.parse;

import org.jsapar.model.Document;
import org.jsapar.model.IntegerCell;
import org.jsapar.model.Line;
import org.jsapar.parse.xml.XmlParseTask;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

public class XMLDocumentParseTaskTest {

    @Test
    public final void testBuild() throws IOException {
        String sXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<document  xmlns=\"http://jsapar.tigris.org/XMLDocumentFormat/2.0\" >" + "<line linetype=\"Person\">"
                + "<cell name=\"FirstName\" type=\"string\">Hans</cell>"
                + "<cell name=\"LastName\" type=\"string\">Hugge</cell>"
                + "<cell name=\"ShoeSize\" type=\"integer\">48</cell>"
                + "<cell name=\"LastSeen\" type=\"date\">2007-12-03T12:48:00</cell>" + "</line></document>";

        java.io.Reader reader = new java.io.StringReader(sXml);
        ParseTask parseTask = new XmlParseTask(reader);
        CollectingConsumer<Line> listener = new CollectingConsumer<>();
        parseTask.setLineConsumer(listener);
        parseTask.execute();
        List<Line> lines = listener.getCollected();

        // System.out.println("Errors: " + parseErrors.toString());

        assertEquals(1, lines.size());
        assertEquals("Hans", lines.get(0).getCell("FirstName").orElseThrow(() -> new AssertionError("Should be set")).getStringValue());
        assertEquals("Hugge", lines.get(0).getCell("LastName").orElseThrow(() -> new AssertionError("Should be set")).getStringValue());
        assertEquals(48, ((IntegerCell) lines.get(0).getCell("ShoeSize").orElseThrow(() -> new AssertionError("Should be set"))).getValue().intValue());
    }

}
