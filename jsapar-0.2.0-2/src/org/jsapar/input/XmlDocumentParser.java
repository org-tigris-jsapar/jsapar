package org.jsapar.input;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.xml.XMLConstants;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.jsapar.Cell;
import org.jsapar.DateCell;
import org.jsapar.Document;
import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.Xml2SchemaBuilder;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class XmlDocumentParser implements org.jsapar.input.Parser {
    // private final static String SCHEMA_LANGUAGE =
    // "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    public Document build(Reader reader, List<CellParseError> parseErrors)
	    throws IOException, JSaParException {

	String schemaFileName = "/xml/schema/XMLDocumentFormat.xsd";
	InputStream schemaStream = Xml2SchemaBuilder.class
		.getResourceAsStream(schemaFileName);

	if (schemaStream == null)
	    throw new JSaParException("Could not find schema file: "
		    + schemaFileName);

	try {
	    SchemaFactory schemaFactory = SchemaFactory
		    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	    StreamSource ss = new StreamSource(schemaStream);
	    Schema schema = schemaFactory.newSchema(ss);

	    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	    parserFactory.setNamespaceAware(true);
	    parserFactory.setSchema(schema);

	    // factory.set
	    SAXParser parser = parserFactory.newSAXParser();
	    org.xml.sax.InputSource is = new org.xml.sax.InputSource(reader);
	    if (parseErrors == null)
		parseErrors = new java.util.LinkedList<CellParseError>();
	    JsaparSAXHandler handler = new JsaparSAXHandler(parseErrors);
	    parser.parse(is, handler);
	    return handler.getCurrentDocument();

	    // TODO Auto-generated method stub
	} catch (ParserConfigurationException e) {
	    throw new JSaParException("XML parsing error.", e);
	} catch (SAXException e) {
	    throw new JSaParException("XML parsing error.", e);
	}
    }

    private Cell.CellType makeCellType(String sXmlCellType) {
	return (Cell.CellType) Enum.valueOf(Cell.CellType.class, sXmlCellType
		.toUpperCase());
    }

    private class JsaparSAXHandler extends org.xml.sax.helpers.DefaultHandler {
	private Document currentDocument;
	private Line currentLine;
	private Cell.CellType currentCellType;
	private String currentCellName;
	private Cell currentCell;
	private boolean cellStarted = false;
	private List<CellParseError> parseErrors;

	public JsaparSAXHandler(List<CellParseError> parseErrors) {
	    this.parseErrors = parseErrors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String name)
		throws SAXException {
	    if (localName.equals("cell")) {
		this.currentLine.addCell(this.currentCell);
		this.currentCell = null;
		this.cellStarted = false;
	    } else if (localName.equals("line")) {
		this.currentDocument.addLine(this.currentLine);
		this.currentLine = null;
	    }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String name,
		Attributes attributes) throws SAXException {

	    if (localName.equals("cell")) {
		cellStarted = true;
		for (int i = 0; i < attributes.getLength(); i++) {
		    if (attributes.getLocalName(i).equals("name"))
			this.currentCellName = attributes.getValue(i);
		    else if (attributes.getLocalName(i).equals("type"))
			this.currentCellType = makeCellType(attributes
				.getValue(i));
		}
		if (this.currentCellType == null)
		    this.currentCellType = Cell.CellType.STRING;
	    } else if (localName.equals("line")) {
		this.currentLine = new Line();
	    } else if (localName.equals("document")) {
		this.currentDocument = new Document();
	    }
	}

	/**
	 * @return the currentDocument
	 */
	public Document getCurrentDocument() {
	    return currentDocument;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length)
		throws SAXException {
	    if (this.cellStarted) {
		String sValue = new String(ch, start, length);
		try {
		    if (this.currentCellType == Cell.CellType.DATE) {
			this.makeDateCell(sValue);
		    } else {
			this.currentCell = SchemaCell.makeCell(
				this.currentCellType, this.currentCellName,
				sValue, Locale.getDefault());
		    }
		    this.currentCellType = null;
		    this.currentCellName = null;
		} catch (Exception e) {
		    throw new SAXException("Failed to parse cell value", e);
		}
	    }
	}

	/**
	 * Creates a date cell from a date string value.
	 * 
	 * @param value
	 * @throws DatatypeConfigurationException
	 */
	private void makeDateCell(String value)
		throws DatatypeConfigurationException {
	    XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance()
		    .newXMLGregorianCalendar(value);
	    GregorianCalendar calendar = xmlCalendar.toGregorianCalendar();
	    this.currentCell = new DateCell(this.currentCellName, calendar
		    .getTime());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.xml.sax.helpers.DefaultHandler#error(org.xml.sax.SAXParseException
	 * )
	 */
	@Override
	public void error(SAXParseException e) throws SAXException {
	    throw e;
	    // int nLine = (this.currentDocument != null) ?
	    // this.currentDocument.getNumberOfLines()+1 : 0;
	    // this.parseErrors.add(new CellParseError(nLine,
	    // this.currentCellName, null, null, e.getMessage()));
	    // e.printStackTrace();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.xml.sax.helpers.DefaultHandler#fatalError(org.xml.sax.
	 * SAXParseException)
	 */
	@Override
	public void fatalError(SAXParseException e) throws SAXException {
	    throw e;
	    // int nLine = (this.currentDocument != null) ?
	    // this.currentDocument.getNumberOfLines()+1 : 0;
	    // this.parseErrors.add(new CellParseError(nLine,
	    // this.currentCellName, null, null, e.getMessage()));
	    // e.printStackTrace();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.xml.sax.helpers.DefaultHandler#warning(org.xml.sax.SAXParseException
	 * )
	 */
	@Override
	public void warning(SAXParseException e) throws SAXException {
	    throw e;
	    // int nLine = (this.currentDocument != null) ?
	    // this.currentDocument.getNumberOfLines()+1 : 0;
	    // this.parseErrors.add(new CellParseError(nLine,
	    // this.currentCellName, null, null, e.getMessage()));
	    // e.printStackTrace();
	}

    }

}
