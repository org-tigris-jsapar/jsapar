package org.jsapar.parse.fixed;

import org.jsapar.error.ExceptionErrorConsumer;
import org.jsapar.error.JSaParException;
import org.jsapar.error.ValidationAction;
import org.jsapar.model.CellType;
import org.jsapar.model.Line;
import org.jsapar.model.LineUtils;
import org.jsapar.parse.CellParseException;
import org.jsapar.parse.LineParseException;
import org.jsapar.text.TextParseConfig;
import org.jsapar.schema.FixedWidthSchemaLine;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

public class FixedWidthLineParserTest {
    
    boolean foundError = false;

    @Before
    public void setUp() {
        foundError = false;
    }

    private ReadBuffer makeReadBuffer(String toParse){
        return new ReadBuffer("", new StringReader(toParse), 100, 100);
    }

    @Test
    public void testParse() throws IOException, JSaParException {
        String toParse = "JonasStenbergSpiselvagen 19141 59Huddinge";
        FixedWidthSchemaLine schemaLine =FixedWidthSchemaLine.builder("Person")
                .withOccurs(1)
                .withCell("First name", 5)
                .withCell("Last name", 8)
                .withCell("Street address", 14)
                .withCell("Zip code", 6)
                .withCell("City", 8)
                .build();

        TextParseConfig config = new TextParseConfig();
        FixedWidthLineParser parser = new FixedWidthLineParser(schemaLine, config);
        Line line = parser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer() );
        assertNotNull(line);
        assertEquals("Jonas", LineUtils.getStringCellValue(line, "First name"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(line, "Last name"));
        assertEquals("Huddinge", LineUtils.getStringCellValue(line,"City"));
    }

    @Test
    public void testParse_hangul() throws IOException, JSaParException {
        String toParse = "서화가람큰길19141 59강남";
        FixedWidthSchemaLine schemaLine =FixedWidthSchemaLine.builder("Person")
                .withOccurs(1)
                .withCell("First name", 2)
                .withCell("Last name", 2)
                .withCell("Street address", 4)
                .withCell("Zip code", 6)
                .withCell("City", 2)
                .build();

        TextParseConfig config = new TextParseConfig();
        FixedWidthLineParser parser = new FixedWidthLineParser(schemaLine, config);
        Line line = parser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer() );
        assertNotNull(line);
        assertEquals("서화", LineUtils.getStringCellValue(line, "First name"));
        assertEquals("가람", LineUtils.getStringCellValue(line, "Last name"));
        assertEquals("강남", LineUtils.getStringCellValue(line,"City"));
    }

    @Test
    public void testParse_simplified_chinese() throws IOException, JSaParException {
        String toParse = "强尼加拉姆主要街道19141 59北京";
        FixedWidthSchemaLine schemaLine =FixedWidthSchemaLine.builder("Person")
                .withOccurs(1)
                .withCell("First name", 2)
                .withCell("Last name", 4)
                .withCell("Street address", 5)
                .withCell("Zip code", 6)
                .withCell("City", 2)
                .build();

        TextParseConfig config = new TextParseConfig();
        FixedWidthLineParser parser = new FixedWidthLineParser(schemaLine, config);
        Line line = parser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer() );
        assertNotNull(line);
        assertEquals("强尼", LineUtils.getStringCellValue(line, "First name"));
        assertEquals("加拉姆主", LineUtils.getStringCellValue(line, "Last name"));
        assertEquals("北京", LineUtils.getStringCellValue(line,"City"));
    }


    @Test
    public void testParse_defaultLast() throws IOException, JSaParException {
        String toParse = "JonasStenberg";
        FixedWidthSchemaLine schemaLine =FixedWidthSchemaLine.builder("Person")
                .withOccurs(1)
                .withCell("First name", 5)
                .withCell("Last name", 8)
                .withCell("City", 8, c->c.withDefaultValue("Falun"))
                .build();

        TextParseConfig config = new TextParseConfig();
        FixedWidthLineParser parser = new FixedWidthLineParser(schemaLine, config);
        Line line = parser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer() );
        assertNotNull(line);
        assertEquals("Jonas", LineUtils.getStringCellValue(line, "First name"));
        assertEquals("Stenberg", LineUtils.getStringCellValue(line, "Last name"));
        assertEquals("Falun", LineUtils.getStringCellValue(line,"City"));
    }

    @Test(expected = LineParseException.class)
    public void testParse_insufficient() throws IOException, JSaParException {
        String toParse = "JonasStenberg";
        FixedWidthSchemaLine schemaLine =FixedWidthSchemaLine.builder("Person")
                .withOccurs(1)
                .withCell("First name", 5)
                .withCell("Last name", 8)
                .withCell("City", 8)
                .build();

        TextParseConfig config = new TextParseConfig();
        config.setOnLineInsufficient(ValidationAction.EXCEPTION);
        FixedWidthLineParser parser = new FixedWidthLineParser(schemaLine, config);
        parser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer() );
        fail("Exception is expected");
    }

    @Test
    public void testParse_default_and_mandatory()
            throws IOException, JSaParException {
        String toParse = "JonasStenberg";
        FixedWidthSchemaLine schemaLine =FixedWidthSchemaLine.builder("Person")
                .withOccurs(1)
                .withCell("First name", 5)
                .withCell("Last name", 8)
                .withCell("City", 8, c->c.withDefaultValue("Falun").withMandatory(true))
                .build();

        TextParseConfig config = new TextParseConfig();
        FixedWidthLineParser parser = new FixedWidthLineParser(schemaLine, config);
        Line line = parser.parse(makeReadBuffer(toParse), error -> {
            CellParseException e = (CellParseException) error;
            assertEquals("City", e.getCellName());
            foundError=true;
        });

        assertNotNull(line);
        assertTrue(foundError);
    }

    @Test
    public void testParse_mandatory()
            throws IOException, JSaParException {
        String toParse = "JonasStenberg";
        FixedWidthSchemaLine schemaLine =FixedWidthSchemaLine.builder("Person")
                .withOccurs(1)
                .withCell("First name", 5)
                .withCell("Last name", 8)
                .withCell("City", 8, c->c.withMandatory(true))
                .build();

        TextParseConfig config = new TextParseConfig();
        FixedWidthLineParser parser = new FixedWidthLineParser(schemaLine, config);
        Line line = parser.parse(makeReadBuffer(toParse), error -> {
            CellParseException e = (CellParseException) error;
            assertEquals("City", e.getCellName());
            foundError=true;
        });

        assertNotNull(line);
        assertTrue(foundError);
    }

    @Test(expected = CellParseException.class)
    public void testParse_parseError() throws IOException, JSaParException {
        String toParse = "JonasStenbergFortyone";
        FixedWidthSchemaLine schemaLine =FixedWidthSchemaLine.builder("Person")
                .withOccurs(1)
                .withCell("First name", 5)
                .withCell("Last name", 8)
                .withCell("Shoe size", 8, c->c.withType(CellType.INTEGER))
                .build();

        TextParseConfig config = new TextParseConfig();
        FixedWidthLineParser parser = new FixedWidthLineParser(schemaLine, config);
        Line line = parser.parse(makeReadBuffer(toParse), new ExceptionErrorConsumer() );
        assertNotNull(line);
    }
    
    
}
