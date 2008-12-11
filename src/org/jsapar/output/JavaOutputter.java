package org.jsapar.output;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsapar.Cell;
import org.jsapar.Document;
import org.jsapar.Line;

/**
 * Uses Java reflection to convert the Document structure into POJO objects.
 * 
 * @author stejon0
 * 
 */
public class JavaOutputter {
    protected static Logger logger = Logger.getLogger("org.jsapar");

    /**
     * Creates a list of java objects. For this method to work, the lineType attribute of each line
     * have to contain the full class name of the class to create for each line. Also the set method
     * for each attribute have to match exactly to the name of each cell.
     * 
     * @param document
     * @return A list of Java objects.
     */
    @SuppressWarnings("unchecked")
    public java.util.List createJavaObjects(Document document) {
	java.util.List objects = new java.util.ArrayList(document.getNumberOfLines());
	java.util.Iterator<Line> lineIter = document.getLineIterator();
	while (lineIter.hasNext()) {
	    try {
		Line line = lineIter.next();
		Object o = this.createObject(line);
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
     * @return An object of the supplied class (c) with attibutes set by the supplied line.
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public Object createObject(Line line) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
	Class c = Class.forName(line.getLineType());
	Object o = c.newInstance();
	return assign(line, o);
    }

    /**
     * Assigns the cells of a line as attributes to an object.
     * 
     * @param <T>
     *            The type of the object to assign
     * @param line
     *            The line to get parameters from.
     * @param objectToAssign
     *            The object to assign cell attributes to. The object will be modified.
     * @return The object that was assigned. The same object that was supplied as parameter.
     */
    public <T> T assign(Line line, T objectToAssign) {

	Method[] methods = objectToAssign.getClass().getMethods();
	Object[] logInfo = new Object[] { objectToAssign.getClass().getName(), null, null };

	java.util.Iterator<Cell> cellIter = line.getCellIterator();
	while (cellIter.hasNext()) {
	    try {
		Cell cell = cellIter.next();
		if (cell.getName() == null)
		    continue;
		String sName = cell.getName();
		if (sName != null && sName.length() > 0) {
		    String sSetMethodName = "set" + sName.substring(0, 1).toUpperCase()
			    + sName.substring(1, sName.length());
		    logInfo[1] = sSetMethodName;
		    boolean isSet = false;
		    for (Method f : methods) {
			if (f.getName().equals(sSetMethodName)) {
			    f.invoke(objectToAssign, cell.getValue());
			    isSet = true;
			    logger.finest("Assigned cell by calling {1} of {0}");
			    break;
			}
		    }
		    if (!isSet) {
			logger.log(Level.INFO, "Skipped assigning cell - No method called {1}() found in class {0}",
				logInfo);
		    }
		}
	    } catch (SecurityException e) {
		logInfo[2] = e;
		logger.log(Level.INFO,
			"Skipped assigning cell - The method {1}() in class {0} does not have public access. - {2}",
			logInfo);
	    } catch (IllegalArgumentException e) {
		logInfo[2] = e;
		logger.log(Level.INFO,
			"Skipped assigning cell - The method {1}() in class {0} does accept correct type. - {2}",
			logInfo);
	    } catch (IllegalAccessException e) {
		logInfo[2] = e;
		logger.log(Level.INFO,
			"Skipped assigning cell - The method {1}() in class {0} does not have correct access. - {2}",
			logInfo);
	    } catch (InvocationTargetException e) {
		logInfo[2] = e;
		logger.log(Level.INFO,
			"Skipped assigning cell - The method {1}() in class {0} fails to execute. - {2}", logInfo);
	    }
	}
	return objectToAssign;
    }

}
