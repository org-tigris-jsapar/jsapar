package org.jsapar;

import org.jsapar.error.JSaParException;
import org.jsapar.parse.CollectingConsumer;
import org.jsapar.schema.Schema;
import org.jsapar.schema.Xml2SchemaBuilder;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

/**
 * Command line utility that either:
 * 1. Converts one file to another using provided input and output schemas.
 * 2. Transforms one file that is parsed using provided input schema into some output using XSLT.
 * <p>
 * Usage:
 * <pre>{@code
1. Read all properties from a file:
jsapar.jar <property file name>

2. Convert from one text file to another using different input and output
schemas:
jsapar.jar -in.schema <input schema path> -out.schema <output schema path>
-in.file <input file name> [-out.file <output file name>]
[-in.file.encoding  <input file encoding (or system default is used)>]
[-out.file.encoding <output file encoding (or system default is used)>]

3. Transform a text file into xml, html or any other format using XSLT:
jsapar.jar -in.schema <input schema path> -xslt.file <xslt file path>
-in.file <input file name> [-out.file <output file name>]
[-in.file.encoding  <input file encoding (or system default is used)>]
[-out.file.encoding <output file encoding (or system default is used)>]
[-xslt.encoding     <xslt file encoding (or system default is used)>]
[-xslt.method       <xslt method to use. (xml is default)
Probably one of xml, html or text>]
 * }</pre>
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
            String inFileName = properties.getProperty("in.file");
            String inFileEncoding = properties.getProperty("in.file.encoding", Charset.defaultCharset().name());

            String outFileEncoding = properties.getProperty("out.file.encoding", Charset.defaultCharset().name());
            String outFileName = properties.getProperty("out.file", inFileName + ".out");

            Schema<?> inputSchema = Xml2SchemaBuilder.loadSchemaFromXmlFile(new File(properties.getProperty("in.schema")));

            final String outputSchemaPath = properties.getProperty("out.schema");
            final String outputXsltPath = properties.getProperty("xslt.file");
            CollectingConsumer<JSaParException> errors = new CollectingConsumer<>();
            if(outputSchemaPath != null) {
                convertText2Text(errors, inFileName, inFileEncoding, outFileEncoding, outFileName, inputSchema, outputSchemaPath);
            }
            else if(outputXsltPath != null) {
                transformText(errors, inFileName, inFileEncoding, outFileEncoding, outFileName, inputSchema, outputXsltPath, properties);
            }
            else{
                System.err.println("Missing property or argument!");
                System.err.println("One of 'out.schema' or 'xslt.file' needs to be specified.");
                printUsage(System.out);

            }
            List<JSaParException> parseErrors = errors.getCollected();
            if (parseErrors.size() > 0)
                System.out.println("===> Found errors while converting file " + inFileName + ": "
                        + System.getProperty("line.separator") + parseErrors);
            else
                System.out.println("Successfully converted file " + inFileName);

        } catch (Throwable t) {
            System.err.println("Failed to convert file.");
            t.printStackTrace(System.err);
        }
    }

    private void convertText2Text(CollectingConsumer<JSaParException> errorEventListener, String inFileName, String inFileEncoding, String outFileEncoding, String outFileName, Schema<?> inputSchema, String outputSchemaPath) throws IOException {
        Schema<?> outputSchema = Xml2SchemaBuilder.loadSchemaFromXmlFile(new File(outputSchemaPath));

        try (Reader inputFileReader = new InputStreamReader(
                new FileInputStream(inFileName), inFileEncoding );
             Writer writer = new OutputStreamWriter(
                     new FileOutputStream(outFileName), outFileEncoding )) {
            Text2TextConverter converter = makeConverter(inputSchema, outputSchema);
            converter.setErrorConsumer(errorEventListener);
            converter.convert(inputFileReader, writer);

        }
    }

    private void transformText(CollectingConsumer<JSaParException> errorConsumer, String inFileName, String inFileEncoding, String outFileEncoding, String outFileName, Schema<?> inputSchema, String outputXsltPath, Properties properties) throws IOException, TransformerConfigurationException {
        String xsltEncoding = properties.getProperty("xslt.encoding", Charset.defaultCharset().name());
        String xsltMethod = properties.getProperty("xslt.method", "xml");

        try (Reader inputFileReader = new InputStreamReader(new FileInputStream(inFileName), inFileEncoding);
             Writer writer = new OutputStreamWriter(new FileOutputStream(outFileName), outFileEncoding);
             Reader xsltReader = new InputStreamReader(new FileInputStream(outputXsltPath), xsltEncoding)) {

            Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xsltReader));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, xsltMethod);

            Text2XmlConverter converter = new Text2XmlConverter(inputSchema, transformer);
            converter.setErrorConsumer(errorConsumer);
            converter.convert(inputFileReader, writer);
        }
    }


    @SuppressWarnings("SameParameterValue")
    private void printUsage(Exception e, PrintStream out) {
        out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        printUsage(out);
    }

    private void printUsage(PrintStream out) {
        out.println();
        out.println("Usage:");
        out.println(" 1. Read all properties from a file:");
        out.println(getApplicationName() + " <property file name> ");
        out.println();
        out.println(" 2. Convert from one text file to another using different input and output");
        out.println("    schemas:");
        out.println(getApplicationName() + " -in.schema <input schema path>");
        out.println("           -out.schema <output schema path>");
        out.println("           -in.file <input file name>");
        out.println("           [-out.file <output file name>]");
        out.println("           [-in.file.encoding  <input file encoding (or system default is used)>]");
        out.println("           [-out.file.encoding <output file encoding (or system default is used)>]");
        out.println();
        out.println(" 3. Transform a text file into xml, html or any other format using XSLT:");
        out.println(getApplicationName() + " -in.schema <input schema path>");
        out.println("           -xslt.file <xslt file path>");
        out.println("           -in.file <input file name>");
        out.println("           [-out.file <output file name>]");
        out.println("           [-in.file.encoding  <input file encoding (or system default is used)>]");
        out.println("           [-out.file.encoding <output file encoding (or system default is used)>]");
        out.println("           [-xslt.encoding     <xslt file encoding (or system default is used)>]");
        out.println("           [-xslt.method       <xslt method to use. (xml is default)");
        out.println("                                Probably one of xml, html or text>]");
        out.println();
    }

    private Text2TextConverter makeConverter(Schema<?> inputSchema, Schema<?> outputSchema) {
        return new Text2TextConverter(inputSchema, outputSchema);
    }

    private void readArgs(Properties properties, String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.length() > 1 && arg.charAt(0) == '-') {
                if (args.length > i + 1) {
                    properties.setProperty(arg.substring(1), args[i + 1]);
                    i++; // Skip next.
                }
            }
        }
    }

    private void checkMandatory(Properties properties, String key) {
        if (null == properties.getProperty(key))
            throw new IllegalArgumentException("Mandatory argument -" + key + " is missing.");
    }

    private Properties readConfig(String[] args) throws IOException {
        Properties properties = new Properties();
        if (args.length == 1) {
            try (FileReader reader = new FileReader(args[0])) {
                properties.load(reader);
            }
        } else if (args.length > 1) {
            readArgs(properties, args);
        } else {
            throw new IllegalArgumentException("Too few arguments");
        }

        // Check mandatory arguments
        checkMandatory(properties, "in.schema");
        checkMandatory(properties, "in.file");
        return properties;
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

    /**
     * Starts stand-alone text to text converter.
     * @param args The main program arguments
     */
    public static void main(String[] args) {
        ConverterMain main = new ConverterMain();
        main.run(args);
    }

}
