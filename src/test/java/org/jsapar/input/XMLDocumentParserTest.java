package org.jsapar.input;

import static org.junit.Assert.assertEquals;

import org.jsapar.Parser;
import org.jsapar.model.Document;
import org.jsapar.model.IntegerCell;
import org.jsapar.JSaParException;
import org.jsapar.schema.XmlSchema;
import org.junit.Before;
import org.junit.Test;

public class XMLDocumentParserTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public final void testBuild() throws JSaParException {
	String sXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+ "<document  xmlns=\"http://jsapar.tigris.org/XMLDocumentFormat/1.0\" >"
		+ "<line linetype=\"Person\">"
		+ "<cell name=\"FirstName\" type=\"string\">Hans</cell>"
		+ "<cell name=\"LastName\" type=\"string\">Hugge</cell>"
		+ "<cell name=\"ShoeSize\" type=\"integer\">48</cell>"
		+ "<cell name=\"LastSeen\" type=\"date\">2007-12-03T12:48:00</cell>"
		+ "</line></document>";

	java.io.Reader reader=new java.io.StringReader(sXml);
	ParseSchema schema = new XmlSchema();
	Parser docBuilder = new Parser(schema);
	java.util.List<CellParseError> parseErrors = new java.util.LinkedList<CellParseError>();
	Document document = docBuilder.build(reader, parseErrors);

	// System.out.println("Errors: " + parseErrors.toString());

	assertEquals(1, document.getNumberOfLines());
	assertEquals("Hans", document.getLine(0).getCell("FirstName")
		.getStringValue());
	assertEquals("Hugge", document.getLine(0).getCell("LastName")
		.getStringValue());
	assertEquals(48,
		((IntegerCell) document.getLine(0).getCell("ShoeSize"))
			.getNumberValue().intValue());
    }

}
