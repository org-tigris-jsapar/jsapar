package org.jsapar;

import org.jsapar.error.JSaParException;
import org.jsapar.error.MaxErrorsExceededException;
import org.jsapar.error.ThresholdCollectingErrorConsumer;
import org.jsapar.model.CellType;
import org.jsapar.model.Document;
import org.jsapar.model.LineUtils;
import org.jsapar.parse.CellParseException;
import org.jsapar.parse.DocumentBuilderLineConsumer;
import org.jsapar.parse.CollectingConsumer;
import org.jsapar.parse.text.TextParseTask;
import org.jsapar.schema.FixedWidthSchema;
import org.jsapar.schema.FixedWidthSchemaCell;
import org.jsapar.schema.FixedWidthSchemaLine;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class TextParseTaskTest {

    @Test
    public void testBuild_fixed_oneLine() throws IOException {
        String toParse = "JonasStenberg";
        FixedWidthSchema schema = makePersonNamesSchema();
        Document doc = build(toParse, schema);

        assertEquals(1, doc.size());
        assertEquals("Jonas", LineUtils.getStringCellValue(doc.getLine(0), "First name"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(doc.getLine(0), "Last name"));
    }

    public FixedWidthSchema makePersonNamesSchema() {
        return FixedWidthSchema.builder()
                    .withLine("Person", line->line
                            .withCell("First name", 5)
                            .withCell("Last name", 8)
                    ).build();
    }

    @Test(expected=CellParseException.class)
    public void testBuild_error_throws() throws IOException {
        String toParse = "JonasAAA";
        FixedWidthSchema schema = makePersonShoeSchema();

        Document doc = build(toParse, schema);
    }

    @Test
    public void testBuild_error_list() throws IOException {
        String toParse = "JonasAAA";
        FixedWidthSchema schema = makePersonShoeSchema();

        Reader reader = new StringReader(toParse);
        List<JSaParException> parseErrors = new ArrayList<>();
        TextParseTask parser = new TextParseTask(schema, reader);
        DocumentBuilderLineConsumer listener = new DocumentBuilderLineConsumer();
        parser.setLineConsumer(listener);
        parser.setErrorConsumer(new CollectingConsumer<>(parseErrors));
        parser.execute();
        Document doc = listener.getDocument();
        Assert.assertEquals(1, parseErrors.size());
        Assert.assertEquals("Shoe size", ((CellParseException)parseErrors.get(0)).getCellName());
        Assert.assertEquals(1, ((CellParseException)parseErrors.get(0)).getLineNumber());
    }

    public FixedWidthSchema makePersonShoeSchema() {
        return FixedWidthSchema.builder()
                    .withLine("Person", line->line
                            .withCell("First name", 5)
                            .withCell("Shoe size", 8, c->c.withType(CellType.INTEGER))
                    ).build();
    }

    @Test(expected=MaxErrorsExceededException.class)
    public void testBuild_error_list_max() throws IOException {
        String toParse = "JonasAAA";
        FixedWidthSchema schema = makePersonShoeSchema();

        Reader reader = new StringReader(toParse);
        TextParseTask parser = new TextParseTask(schema, reader);
        DocumentBuilderLineConsumer listener = new DocumentBuilderLineConsumer();
        parser.setLineConsumer(listener);
        parser.setErrorConsumer(new ThresholdCollectingErrorConsumer(0));
        parser.execute();
        Document doc = listener.getDocument();
    }
    
    
    @Test
    public void testBuild_fixed_twoLines() throws IOException {
        String toParse = "JonasStenberg" + System.getProperty("line.separator") + "LinusStenberg";
        FixedWidthSchema schema = makePersonNamesSchema();

        Document doc = build(toParse, schema);

        assertEquals(2, doc.size());
        assertEquals("Jonas", LineUtils.getStringCellValue(doc.getLine(0), "First name"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(doc.getLine(0), "Last name"));

        assertEquals("Linus", LineUtils.getStringCellValue(doc.getLine(1), "First name"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(doc.getLine(1), "Last name"));
    }

    private Document build(String toParse, FixedWidthSchema schema) throws IOException {
        Reader reader = new StringReader(toParse);
        TextParseTask parser = new TextParseTask(schema, reader);
        DocumentBuilderLineConsumer listener = new DocumentBuilderLineConsumer();
        parser.setLineConsumer(listener);
        parser.execute();
        parser.close();
        return listener.getDocument();
    }

}
