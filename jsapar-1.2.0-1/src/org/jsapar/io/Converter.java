package org.jsapar.io;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.input.CellParseError;
import org.jsapar.input.LineErrorEvent;
import org.jsapar.input.LineParsedEvent;
import org.jsapar.input.ParseSchema;
import org.jsapar.input.ParsingEventListener;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaException;

/**
 * Reads buffer using an input schema and writes to another buffer using an output schema. By adding
 * a LineManipulator you are able to make modifications of each line before it is written to the
 * output. The method manipulate() of all added LineManipulators are called for each line that are
 * parsed successfully.
 * 
 * @author stejon0
 * 
 */
public class Converter {

    private List<LineManipulator> manipulators = new java.util.LinkedList<LineManipulator>();
    private ParseSchema inputSchema;
    private Schema outputSchema;

    /**
     * Creates a Converter object with the specified schemas.
     * 
     * @param inputSchema
     * @param outputSchema
     */
    public Converter(ParseSchema inputSchema, Schema outputSchema) {
        this.inputSchema = inputSchema;
        this.outputSchema = outputSchema;
    }

    /**
     * Adds LineManipulator to this converter. All present line manipulators are executed for each
     * line.
     * 
     * @param manipulator
     */
    public void addLineManipulator(LineManipulator manipulator) {
        manipulators.add(manipulator);
    }

    /**
     * @param reader
     * @param writer
     * @return A list of CellParseErrors or an empty list if there were no errors.
     * @throws IOException
     * @throws JSaParException
     */
    public java.util.List<CellParseError> convert(java.io.Reader reader, java.io.Writer writer) throws IOException,
            JSaParException {

        DocumentWriter outputter = new DocumentWriter(writer);
        // TODO Create a DocumentWriter that supports line type by control cell.

        return doConvert(reader, writer, outputter);

    }

    /**
     * @param reader
     * @param writer
     * @param outputter
     * @return
     * @throws IOException
     * @throws JSaParException
     */
    protected java.util.List<CellParseError> doConvert(java.io.Reader reader,
                                                       java.io.Writer writer,
                                                       DocumentWriter outputter) throws IOException, JSaParException {
        outputSchema.outputBefore(writer);
        inputSchema.parse(reader, outputter);
        outputSchema.outputAfter(writer);
        return outputter.getParseErrors();
    }

    /**
     * Internal class for handling output of one line at a time while receiving parsing events.
     * 
     * @author stejon0
     * 
     */
    protected class DocumentWriter implements ParsingEventListener {
        private List<CellParseError> parseErrors = new LinkedList<CellParseError>();
        private java.io.Writer writer;

        public DocumentWriter(Writer writer) throws JSaParException {
            this.writer = writer;
        }

        @Override
        public void lineErrorEvent(LineErrorEvent event) {
            parseErrors.add(event.getCellParseError());
        }

        @Override
        public void lineParsedEvent(LineParsedEvent event) throws JSaParException {
            try {
                Line line = event.getLine();
                for (LineManipulator manipulator : manipulators) {
                    manipulator.manipulate(line);
                }
                outputSchema.outputLine(line, event.getLineNumber(), this.writer);
            } catch (IOException e) {
                throw new JSaParException("Failed to write to writer", e);
            }
        }

        /**
         * @return the parseErrors
         */
        public List<CellParseError> getParseErrors() {
            return parseErrors;
        }

        /**
         * @return the writer
         */
        protected java.io.Writer getWriter() {
            return writer;
        }


    }

    /**
     * @return the inputSchema
     */
    public ParseSchema getInputSchema() {
        return inputSchema;
    }

    /**
     * @param inputSchema
     *            the inputSchema to set
     */
    public void setInputSchema(ParseSchema inputSchema) {
        this.inputSchema = inputSchema;
    }

    /**
     * @return the outputSchema
     */
    public Schema getOutputSchema() {
        return outputSchema;
    }

    /**
     * @param outputSchema
     *            the outputSchema to set
     */
    public void setOutputSchema(Schema outputSchema) {
        this.outputSchema = outputSchema;
    }

    /**
     * Reads command line arguments into property structure.
     * 
     * @param properties
     *            The properties to be filled with arguments.
     * @param args
     *            The arguments.
     * @return
     */
    private static void readArgs(Properties properties, String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.length() > 1 && arg.charAt(0) == '-') {
                if (args.length > i + 1) {
                    properties.setProperty(arg.substring(1, arg.length()), args[i + 1]);
                    i++; // Skip next.
                }
            }
        }
    }

    private static void checkMandatory(Properties properties, String key) throws JSaParException {
        if (null == properties.getProperty(key))
            throw new JSaParException("Mandatory argument -" + key + " is missing.");
    }

    /**
     * @param args
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @throws JSaParException
     */
    private static Properties readConfig(String[] args) throws FileNotFoundException, IOException, JSaParException {
        Properties properties = new Properties();
        if (args.length == 1) {
            properties.load(new FileReader(args[0]));
        } else if (args.length > 1) {
            readArgs(properties, args);
        } else {
            throw new JSaParException("Too few arguments");
        }

        // Check mandatory arguments
        checkMandatory(properties, "in.schema");
        checkMandatory(properties, "out.schema");
        checkMandatory(properties, "in.file");
        return properties;
    }

    /**
     * @param fileName
     * @return
     * @throws SchemaException
     * @throws IOException
     */
    private static Schema loadSchemaFromXmlFile(String fileName) throws SchemaException, IOException {
        Reader schemaReader = new FileReader(fileName);
        org.jsapar.schema.Xml2SchemaBuilder builder = new org.jsapar.schema.Xml2SchemaBuilder();
        Schema schema = builder.build(schemaReader);
        schemaReader.close();
        return schema;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        final String applicationName = "jsapar.jar";
        Properties properties;
        try {
            properties = readConfig(args);
        } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            System.out.println("");
            System.out.println("Usage:");
            System.out.println(" 1. " + applicationName + " <property file name> ");
            System.out.println(" 2. " + applicationName
                    + " -in.schema <input schem name> -out.schema <output schema name>");
            System.out.println("               -in.file <input file name> [-out.file <output file name>]");
            System.out.println("");
            System.out.println("Alternative 1. above reads the arguments from a property file.");
            return;
        }

        try {
            Schema inputSchema = loadSchemaFromXmlFile(properties.getProperty("in.schema"));
            Schema outputSchema = loadSchemaFromXmlFile(properties.getProperty("out.schema"));

            String inFileName = properties.getProperty("in.file");
            Reader inputFileReader = new java.io.FileReader(inFileName);
            java.io.Writer writer = new java.io.FileWriter(properties.getProperty("out.file", inFileName + ".out"));

            Converter converter = new Converter(inputSchema, outputSchema);
            java.util.List<CellParseError> parseErrors = converter.convert(inputFileReader, writer);

            if (parseErrors.size() > 0)
                System.out.println("===> Found errors while converting file " + inFileName + ": "
                        + System.getProperty("line.separator") + parseErrors);
            else
                System.out.println("Successfully converted file " + inFileName);

            inputFileReader.close();
            writer.close();
        } catch (Throwable t) {
            System.err.println("Failed to convert file.");
            t.printStackTrace(System.err);
        }
    }

    /**
     * @return the manipulators
     */
    protected List<LineManipulator> getManipulators() {
        return manipulators;
    }

}
