/**
 * 
 */
package org.jsapar;

import org.jsapar.error.JSaParException;
import org.jsapar.error.RecordingErrorEventListener;
import org.jsapar.schema.Schema;
import org.jsapar.schema.Xml2SchemaBuilder;

import java.io.*;
import java.util.List;
import java.util.Properties;

/**
 * @author stejon0
 * 
 */
public class ConverterMain {
    private static final String APP_NAME = "jsapar.jar";
    
    private String applicationName = APP_NAME;

    public void run(String[] args) {
        Properties properties;
        try {
            properties = readConfig(args);
        } catch (Exception e) {
            printUsage(e, System.out);
            return;
        }

        try {
            Schema inputSchema = Xml2SchemaBuilder.loadSchemaFromXmlFile(new File(properties.getProperty("in.schema")));
            Schema outputSchema = Xml2SchemaBuilder
                    .loadSchemaFromXmlFile(new File(properties.getProperty("out.schema")));

            String inFileName = properties.getProperty("in.file");
            String inFileEncoding = properties.getProperty("in.file.encoding", null);
            Reader inputFileReader = inFileEncoding == null ? new java.io.FileReader(inFileName)
                    : new InputStreamReader(new FileInputStream(inFileName), inFileEncoding);
            String outFileEncoding = properties.getProperty("out.file.encoding", null);
            String outFileName = properties.getProperty("out.file", inFileName + ".out");
            java.io.Writer writer = (outFileEncoding == null) ? new java.io.FileWriter(outFileName)
                    : new OutputStreamWriter(new FileOutputStream(outFileName), outFileEncoding);

            Converter converter = makeConverter(inputSchema, inputFileReader, outputSchema, writer);
            RecordingErrorEventListener errorEventListener = new RecordingErrorEventListener();
            converter.convert();
            List<JSaParException> parseErrors = errorEventListener.getErrors();

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
     * Prints usage information to supplied output stream.
     * @param e An exception that occurred.
     * @param out The stream to write to.
     */
    protected void printUsage(Exception e, PrintStream out) {
        out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        out.println("");
        out.println("Usage:");
        out.println(" 1. " + getApplicationName() + " <property file name> ");
        out.println(" 2. " + getApplicationName()
                + " -in.schema <input schem name> -out.schema <output schema name>");
        out.println("               -in.file <input file name> [-out.file <output file name>]");
        out.println("               [-in.file.encoding <input file encoding (or system default is used)>] ");
        out.println("               [-out.file.encoding <output file encoding (or system default is used)>] ");
        out.println("");
        out.println("Alternative 1. above reads the arguments from a property file.");
    }

    /**
     * Override to implement other converter behavior.
     * @param inputSchema
     * @param outputSchema
     * @return A new converter.
     */
    protected Converter makeConverter(Schema inputSchema, Reader reader, Schema outputSchema, Writer writer) {
        Converter converter = new Text2TextConverter(inputSchema, reader, outputSchema, writer);
        return converter;
    }

    /**
     * Reads command line arguments into property structure.
     * 
     * @param properties
     *            The properties to be filled with arguments.
     * @param args
     *            The arguments.
     */
    protected void readArgs(Properties properties, String[] args) {
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

    /**
     * @param properties
     * @param key
     *
     */
    protected void checkMandatory(Properties properties, String key)  {
        if (null == properties.getProperty(key))
            throw new IllegalArgumentException("Mandatory argument -" + key + " is missing.");
    }

    /**
     * @param args
     * @return A Properties instance filled with all the configuration.
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected Properties readConfig(String[] args) throws IOException {
        Properties properties = new Properties();
        if (args.length == 1) {
            properties.load(new FileReader(args[0]));
        } else if (args.length > 1) {
            readArgs(properties, args);
        } else {
            throw new IllegalArgumentException("Too few arguments");
        }

        // Check mandatory arguments
        checkMandatory(properties, "in.schema");
        checkMandatory(properties, "out.schema");
        checkMandatory(properties, "in.file");
        return properties;
    }

    
    /**
     * @param args
     */
    public static void main(String[] args) {
        ConverterMain main = new ConverterMain();
        main.run(args);
    }

    /**
     * @param applicationName the applicationName to set
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * @return the applicationName
     */
    public String getApplicationName() {
        return applicationName;
    }

}
