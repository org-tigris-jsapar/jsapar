package org.jsapar.parse.xml;

import org.jsapar.error.JSaParException;
import org.jsapar.model.*;
import org.jsapar.parse.cell.CellParser;
import org.jsapar.schema.SchemaCellFormat;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.GregorianCalendar;
import java.util.Spliterator;
import java.util.function.Consumer;

public class XmlParseSpliterator implements Spliterator<Line> {
    private final XMLStreamReader streamReader;
    private long currentLineNumber = 1;

    public XmlParseSpliterator(Reader reader) {
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            streamReader = inputFactory.createXMLStreamReader(reader);
        } catch (XMLStreamException e) {
            throw new JSaParException("Failed to initialize xml stream reader", e);
        }
    }

    @Override
    public boolean tryAdvance(Consumer<? super Line> action) {
        try {
            Line currentLine = null;
            Cell<?> currentCell = null;
            while (streamReader.hasNext()) {
                int type = streamReader.next();
                switch (type) {
                    case XMLStreamReader.START_ELEMENT:
                        switch (streamReader.getName().getLocalPart()) {
                            case "line":
                                currentLine = new Line(streamReader.getAttributeValue(null, "linetype"), 16, currentLineNumber++);
                                break;
                            case "cell":
                                currentCell = handleCellStart();
                                // After getting element text we might already be at the end.
                                if(streamReader.isEndElement()){
                                    currentCell = handleCellEnd(currentLine, currentCell);
                                }
                                break;
                        }
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        switch (streamReader.getName().getLocalPart()) {
                            case "line":
                                if (currentLine != null) {
                                    action.accept(currentLine);
                                    return true;
                                }
                                break;
                            case "cell":
                                currentCell = handleCellEnd(currentLine, currentCell);
                                break;
                        }
                        break;
                }
            }
        } catch (XMLStreamException | ParseException | DatatypeConfigurationException e) {
            throw new JSaParException("Error while parsing xml", e);
        }
        return false;
    }

    private Cell<?> handleCellEnd(Line currentLine, Cell<?> currentCell) {
        if (currentCell != null && currentLine != null) {
            currentLine.addCell(currentCell);
            return null;
        }
        return currentCell;
    }

    private Cell<?> handleCellStart() throws XMLStreamException, ParseException, DatatypeConfigurationException {
        String cellName = streamReader.getAttributeValue(null, "name");
        CellType cellType = makeCellType(streamReader.getAttributeValue(null, "type"));
        String cellContent = streamReader.getElementText().trim();
        return makeCell(cellName, cellType, cellContent);
    }

    private Cell<?> makeCell(String cellName, CellType cellType, String cellContent) throws ParseException, DatatypeConfigurationException {
        if(cellContent.isEmpty())
            return new EmptyCell(cellName, cellType);

        if (cellType == CellType.DATE) {
            XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(cellContent);
            GregorianCalendar calendar = xmlCalendar.toGregorianCalendar();
            return new DateCell(cellName, calendar.getTime());
        }
        return CellParser.makeCell(cellType, cellName, cellContent, SchemaCellFormat.defaultLocale);
    }

    private CellType makeCellType(String sXmlCellType) {
        if (sXmlCellType == null)
            return CellType.STRING;
        return Enum.valueOf(CellType.class, sXmlCellType.toUpperCase());
    }


    @Override
    public Spliterator<Line> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return IMMUTABLE | ORDERED | NONNULL;
    }
}
