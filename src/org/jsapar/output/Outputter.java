/** 
 * Copyright: Jonas Stenberg
 */
package org.jsapar.output;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsapar.Cell;
import org.jsapar.Document;
import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.schema.CsvSchemaLine;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaLine;

/**
 * This class contains methods for transforming a Document into an output.
 * E.g. if you want to write the Document to a file you should use a
 * {@link java.io.FileWriter} together with a Schema and call the
 * {@link #output(Document, Schema, java.io.Writer)} method.
 * 
 * @author Jonas Stenberg
 * 
 */
public class Outputter {

    protected static Logger logger = Logger.getLogger("org.jsapar");

    /**
     * Writes the document to a {@link java.io.Writer} according to the supplied
     * schema.
     * 
     * @param document
     * @param schema
     * @param writer
     * @throws JSaParException
     */
    public void output(Document document, Schema schema, java.io.Writer writer)
	    throws JSaParException {
	try {
	    schema.outputBefore(writer);
	    schema.output(document, writer);
	    schema.outputAfter(writer);
	} catch (IOException e) {
	    throw new OutputException("Failed to write to buffert.", e);
	}
    }

    /**
     * Writes the single line to a {@link java.io.Writer} according to the
     * supplied schema line.
     * 
     * @param line
     * @param schemaLine
     * @param writer
     * @throws JSaParException
     */
    public void output(Line line, SchemaLine schemaLine, java.io.Writer writer)
	    throws JSaParException {
	try {
	    schemaLine.output(line, writer);
	} catch (IOException e) {
	    throw new OutputException("Failed to write to buffert.", e);
	}
    }

    /**
     * Writes the header line if the first line is schema.
     * 
     * @param schemaLine
     * @param writer
     * @throws JSaParException
     */
    public void outputCsvHeaderLine(CsvSchemaLine schemaLine,
	    java.io.Writer writer) throws JSaParException {
	if (!schemaLine.isFirstLineAsSchema())
	    throw new JSaParException(
		    "The schema line is not of type where first line is schema.");

	try {
	    schemaLine.outputHeaderLine(writer);
	} catch (IOException e) {
	    throw new OutputException("Failed to write to buffert.", e);
	}
    }

    /**
     * Creates a list of java objects. For this method to work, the lineType
     * attribute of each line have to contain the full class name of the class
     * to create for each line. Also the set method for each attribute have to
     * match exactly to the name of each cell.
     * 
     * @param document
     * @return A list of Java objects.
     */
    @SuppressWarnings("unchecked")
    public java.util.List createJavaObjects(Document document) {
	java.util.List objects = new java.util.ArrayList(document
		.getNumberOfLines());
	java.util.Iterator<Line> lineIter = document.getLineIterator();
	while (lineIter.hasNext()) {
	    try {
		Line line = lineIter.next();
		Class c = Class.forName(line.getLineType());
		Object o = this.createObject(c, line);
		objects.add(o);
	    } catch (ClassNotFoundException e) {
		logger.info("Skipped creating object - " + e);
	    } catch (InstantiationException e) {
		logger.info("Skipped creating object - " + e);
	    } catch (IllegalAccessException e) {
		logger.info("Skipped creating object - " + e);
	    }
	}
	return objects;
    }

    /**
     * @param c
     *            The class to use when creating the object to return.
     * @param line
     * @return An object of the supplied class (c) with attibutes set by the
     *         supplied line.
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unchecked")
    private Object createObject(Class c, Line line)
	    throws InstantiationException, IllegalAccessException {
	Object o = c.newInstance();
	Method[] methods = o.getClass().getMethods();
	Object[] logInfo = new Object[] { c.getName(), null, null };

	java.util.Iterator<Cell> cellIter = line.getCellIterator();
	while (cellIter.hasNext()) {
	    try {
		Cell cell = cellIter.next();
		if (cell.getName() == null)
		    continue;
		String sSetMethodName = "set" + cell.getName();
		logInfo[1] = sSetMethodName;
		boolean isSet = false;
		for (Method f : methods) {
		    if (f.getName().equals(sSetMethodName)) {
			f.invoke(o, cell.getValue());
			isSet = true;
			logger.finest("Assigned cell by calling {1} of {0}");
			break;
		    }
		}
		if (!isSet) {
		    logger
			    .log(
				    Level.INFO,
				    "Skipped assigning cell - No method called {1}() found in class {0}",
				    logInfo);
		}
	    } catch (SecurityException e) {
		logInfo[2] = e;
		logger
			.log(
				Level.INFO,
				"Skipped assigning cell - The method {1}() in class {0} does not have public access. - {2}",
				logInfo);
	    } catch (IllegalArgumentException e) {
		logInfo[2] = e;
		logger
			.log(
				Level.INFO,
				"Skipped assigning cell - The method {1}() in class {0} does accept correct type. - {2}",
				logInfo);
	    } catch (IllegalAccessException e) {
		logInfo[2] = e;
		logger
			.log(
				Level.INFO,
				"Skipped assigning cell - The method {1}() in class {0} does not have correct access. - {2}",
				logInfo);
	    } catch (InvocationTargetException e) {
		logInfo[2] = e;
		logger
			.log(
				Level.INFO,
				"Skipped assigning cell - The method {1}() in class {0} fails to execute. - {2}",
				logInfo);
	    }
	}
	return o;
    }
}
