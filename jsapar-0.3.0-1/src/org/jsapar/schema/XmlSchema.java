package org.jsapar.schema;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jsapar.Cell;
import org.jsapar.Document;
import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.input.CellParseError;
import org.jsapar.input.ParseException;
import org.jsapar.input.ParsingEventListener;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


public class XmlSchema extends Schema {

    public XmlSchema() {
	// TODO Auto-generated constructor stub
    }

    @Override
    public void parse(Reader reader, ParsingEventListener listener)
	    throws IOException, JSaParException {

	try {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document document = new Document();

	    org.xml.sax.InputSource is = new org.xml.sax.InputSource(reader);
	    org.w3c.dom.Document xmlDocument = builder.parse(is);

	    org.w3c.dom.Element root = xmlDocument.getDocumentElement();
	    String sName = root.getNodeName();
	    if (sName == null || !sName.equals("document"))
		throw new ParseException("Missing root node <document>");

	    org.w3c.dom.NodeList nodes = root.getChildNodes();
	    for (int i = 0; i < nodes.getLength(); i++) {
		org.w3c.dom.Node child = nodes.item(i);
		if (child instanceof org.w3c.dom.Element) {
		    String sElementName = child.getNodeName();
		    if (sName == null || !sElementName.equals("line"))
			throw new ParseException("Invalid element found: <" + sElementName
				+ ">, when expecting <line>");
		    Line line = this.parseLine((org.w3c.dom.Element) child, listener);
		    if (line == null)
			break;
		    else
			document.addLine(line);
		}
	    }

	    // TODO Auto-generated method stub
	} catch (ParserConfigurationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (SAXException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /**
     * @param xmlLine
     * @param parseErrors
     * @return
     * @throws ParseException 
     */
    private Line parseLine(org.w3c.dom.Element xmlLine, ParsingEventListener listener) throws ParseException {
	Line line = new Line();
	
	String sLineType = xmlLine.getAttribute("linetype");
	line.setLineType(sLineType);
	
	org.w3c.dom.NodeList nodes = xmlLine.getChildNodes();
	
	for (int i = 0; i < nodes.getLength(); i++) {
	    org.w3c.dom.Node child = nodes.item(i);
	    if (child instanceof org.w3c.dom.Element) {
		String sName = child.getNodeName();
		if (sName == null || !sName.equals("cell"))
		    throw new ParseException("Invalid element found: <" + sName
			    + ">, when expecting <cell>");
		Cell cell = this.parseCell((org.w3c.dom.Element) child, listener);
		if (line == null)
		    break;
		else
		    line.addCell(cell);
	    }
	}
	return line;
    }

    /**
     * @param child
     * @param parseErrors
     * @return
     * @throws ParseException 
     */
    private Cell parseCell(Element xmlCell, ParsingEventListener listener) throws ParseException {
//	String sType = xmlCell.getAttribute("type");
	Cell cell = null;
	
	org.w3c.dom.NodeList nodes = xmlCell.getChildNodes();
	
	for (int i = 0; i < nodes.getLength(); i++) {
	    org.w3c.dom.Node child = nodes.item(i);
	    if (child instanceof org.w3c.dom.Element) {
		String sName = child.getNodeName();
		if(sName.equals("name")){
		    
		}
		else if(sName.equals("value")){
		    
		}
		else{
		    
		}
		if (sName == null || !sName.equals("cell"))
		    throw new ParseException("Invalid element found: <" + sName
			    + ">, when expecting <cell>");
		cell = this.parseCell((org.w3c.dom.Element) child, listener);
		if (cell == null)
		    break;
	    }
	}

	return cell;
    }
    
    @Override
    public void outputBefore(Writer writer)
	    throws IOException, JSaParException {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void output(Document document, Writer writer) throws IOException,
	    JSaParException {
	// TODO Auto-generated method stub

    }

    @Override
    public void outputAfter(Writer writer)
	    throws IOException, JSaParException {
	// TODO Auto-generated method stub
	
    }


    @Override
    public List getSchemaLines() {
	// TODO Auto-generated method stub
	return null;
    }


}
