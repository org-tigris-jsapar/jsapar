/**
 * The JSaPar package provides a parser for flat and CSV (Comma Separated Values) files.
 * <p>
 * The classes in this package provides the highest level of abstractions and are thus the easiest to use. If you want
 * to solve more complex scenarios you may choose to use the lower level {@link org.jsapar.parse.ParseTask} and
 * {@link org.jsapar.convert.ConvertTask}
 * implementations. For instance, the {@link org.jsapar.concurrent.ConcurrentText2TextConverter} is provided to be able
 * to convert text to text by using different threads for reading and writing, but there are no other
 * concurrent converter implementations. You may however implement your own by using the
 * {@link org.jsapar.concurrent.ConcurrentConvertTask} or {@link org.jsapar.concurrent.ConcurrentLineEventListener}.
 * <p>
 * The files for the examples below are provided in the <tt>samples</tt> folder of the project. The JUnit test
 * {@code org.jsapar.JSaParExamplesTest.java}
 * contains a more comprehensive set of examples of how to use the package.
 * <p>
 * Example of reading <b>CSV file</b> into a {@link org.jsapar.model.Document} object according to an xml-schema:
 * <p>
 * <pre>{@code
 * try (Reader schemaReader = new FileReader("exsamples/01_CsvSchema.xml");
 * Reader fileReader = new FileReader("exsamples/01_Names.csv")) {
 * Schema schema = Schema.fromXml(schemaReader);
 * TextParser parser = new TextParser(schema);
 * DocumentBuilderLineEventListener listener = new DocumentBuilderLineEventListener();
 * parser.parse(fileReader, listener);
 * Document document = listener.getDocument();
 * }
 * }</pre>
 * <p>
 * Example of converting a <b>CSV file</b> into a <b>Fixed width file</b> according to two xml-schemas:
 * <pre>{@code
 * File outFile = new File("exsamples/02_Names_out.txt");
 * try (Reader inSchemaReader = new FileReader("exsamples/01_CsvSchema.xml");
 * Reader outSchemaReader = new FileReader("exsamples/02_FixedWidthSchema.xml");
 * Reader inReader = new FileReader("exsamples/01_Names.csv");
 * Writer outWriter = new FileWriter(outFile)) {
 * Text2TextConverter converter = new Text2TextConverter(Schema.fromXml(inSchemaReader),
 * Schema.fromXml(outSchemaReader));
 * converter.convert(inReader, outWriter);
 * }
 * }</pre>
 * <p>
 * Example of converting a <b>CSV file</b> into a list of <b>Java objects</b> according to an xml-schema:
 * <pre>{@code
 * try (Reader schemaReader = new FileReader("exsamples/07_CsvSchemaToJava.xml");
 * Reader fileReader = new FileReader("exsamples/07_Names.csv")) {
 * Text2BeanConverter converter = new Text2BeanConverter(Schema.fromXml(schemaReader));
 * RecordingBeanEventListener<TstPerson> beanEventListener = new RecordingBeanEventListener<>();
 * converter.convert(fileReader, beanEventListener);
 * List<TstPerson> people = beanEventListener.getLines();
 * }
 * }</pre>
 */
package org.jsapar;