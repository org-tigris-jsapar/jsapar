/** 
 * Copyrigth: Jonas Stenberg
 */
package org.jsapar.schema;

import static org.junit.Assert.assertEquals;

import org.jsapar.Cell.CellType;
import org.junit.Test;


/**
 * @author Jonas
 * 
 */
public class Xml2SchemaBuilderTest {

	/**
	 * Test method for
	 * {@link org.jsapar.schema.Xml2SchemaBuilder#build(java.io.Reader)}
	 * .
	 * 
	 * @throws SchemaException
	 */
	@Test
	public final void testBuild_FixedLength() throws SchemaException {

		String sXmlSchema = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		    		+ "<schema  xmlns=\"http://jsapar.tigris.org/JSaParSchema\" >"
		    		+ "<fixedwidthschema lineseparator=\"&#13;&#10;\"><line occurs=\"*\">"
				+ "<cell name=\"First name\" length=\"5\"/>"
				+ "<cell name=\"Last name\" length=\"8\"/>"
				+ "<cell name=\"Shoe size\" length=\"8\"><format type=\"integer\"/></cell>"
				+ "</line></fixedwidthschema></schema>";

		Xml2SchemaBuilder builder = new Xml2SchemaBuilder();
		java.io.Reader reader = new java.io.StringReader(sXmlSchema);
		Schema schema = builder.build(reader);
		FixedWidthSchema fwSchema = (FixedWidthSchema) schema;

		assertEquals("\r\n", fwSchema.getLineSeparator());

		assertEquals("First name", fwSchema.getFixedWidthSchemaLines().get(0)
				.getSchemaCells().get(0).getName());
		assertEquals("Last name", fwSchema.getFixedWidthSchemaLines().get(0)
				.getSchemaCells().get(1).getName());

		assertEquals(5, fwSchema.getFixedWidthSchemaLines().get(0).getSchemaCells()
				.get(0).getLength());
		assertEquals(8, fwSchema.getFixedWidthSchemaLines().get(0).getSchemaCells()
				.get(1).getLength());

		assertEquals(CellType.INTEGER, fwSchema.getFixedWidthSchemaLines().get(0)
				.getSchemaCells().get(2).getCellFormat().getCellType());

	}

	@Test
	public final void testBuild_Csv() throws SchemaException {

		String sXmlSchema = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		    		+ "<schema  xmlns=\"http://jsapar.tigris.org/JSaParSchema\" >"
		    		+ "<csvschema><line occurs=\"4\">"
				+ "<cell name=\"First name\"/>" + "<cell name=\"Last name\"/>"
				+ "</line></csvschema></schema>";

		Xml2SchemaBuilder builder = new Xml2SchemaBuilder();
		java.io.Reader reader = new java.io.StringReader(sXmlSchema);
		Schema schema = builder.build(reader);
		CsvSchema csvSchema = (CsvSchema) schema;

		assertEquals("First name", csvSchema.getCsvSchemaLines().get(0)
				.getSchemaCells().get(0).getName());
		assertEquals("Last name", csvSchema.getCsvSchemaLines().get(0)
				.getSchemaCells().get(1).getName());

	}

	@Test
	public final void testBuild_Csv_firstlineasschema() throws SchemaException {

		String sXmlSchema = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		    		+ "<schema  xmlns=\"http://jsapar.tigris.org/JSaParSchema\" >"
	    			+ "<csvschema><line occurs=\"4\" firstlineasschema=\"true\" >"
				+ "</line></csvschema></schema>";

		Xml2SchemaBuilder builder = new Xml2SchemaBuilder();
		java.io.Reader reader = new java.io.StringReader(sXmlSchema);
		Schema schema = builder.build(reader);
		CsvSchema csvSchema = (CsvSchema) schema;

		assertEquals(true, csvSchema.getCsvSchemaLines().get(0)
				.isFirstLineAsSchema());

	}

	@Test(expected=SchemaException.class)
	public final void testBuild_Csv_firstlineasschema_error() throws SchemaException {

	    	//"yes" is not a valid boolean value.
		String sXmlSchema = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
		    		+ "<schema  xmlns=\"http://jsapar.tigris.org/JSaParSchema\" >\n"
	    			+ "<csvschema><line occurs=\"4\" firstlineasschema=\"yes\" >\n"
				+ "</line></csvschema></schema>";

		Xml2SchemaBuilder builder = new Xml2SchemaBuilder();
		java.io.Reader reader = new java.io.StringReader(sXmlSchema);
		Schema schema = builder.build(reader);
	}
	
}
