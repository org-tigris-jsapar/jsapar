package org.jsapar.bean;

import org.jsapar.error.JSaParException;
import org.jsapar.parse.bean.BeanPropertyMap;
import org.jsapar.schema.SchemaException;
import org.jsapar.schema.Xml2SchemaBuilder;
import org.jsapar.utils.XmlTypes;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class for building {@link BeanMap} instances based on xml.
 */
public class Xml2BeanMapBuilder implements XmlTypes {
    private final static String NAMESPACE = "http://jsapar.tigris.org/BeanMapSchema/2.0";

    /**
     * Loads a {@link BeanMap} instance from xml that is read from the supplied reader.
     * @param reader The reader to read xml from.
     * @return A newly created {@link BeanMap} instance.
     * @throws IOException In case there was an io error while reading xml.
     */
    public BeanMap build(Reader reader) throws IOException {
        String schemaFileName = "/xml/schema/BeanMapSchema.xsd";

        try(InputStream schemaStream = Xml2SchemaBuilder.class.getResourceAsStream(schemaFileName)) {
            if (schemaStream == null)
                throw new FileNotFoundException("Could not find schema file: " + schemaFileName);
            Element xmlRoot = parseXmlDocument(reader, schemaStream);

            List<BeanPropertyMap> beanPropertyMaps = getChildrenStream(NAMESPACE, xmlRoot, "bean")
                    .map(this::buildPropertyMap)
                    .collect(Collectors.toList());

            return BeanMap.ofBeanPropertyMaps(beanPropertyMaps);
        } catch (ParserConfigurationException | SAXException e) {
            throw new JSaParException("Failed to load bean map from xml ", e);
        }

    }

    private BeanPropertyMap buildPropertyMap(Element xmlBean) {
        String className = xmlBean.getAttribute("name");
        String lineType = xmlBean.getAttribute("linetype");

        Map<String, String> cellNamesOfProperty = getChildrenStream(xmlBean, "property")
                .collect(Collectors.toMap(e -> e.getAttribute("name"), e -> e.getAttribute("cellname")));
        try {
            return BeanPropertyMap.ofPropertyNames(className, lineType, cellNamesOfProperty);
        } catch (ClassNotFoundException e) {
            throw new JSaParException("Failed to build property map", e);
        }
    }

    /**
     * Loads a bean map from specified resource using default character encoding.
     * Deprecated since 2.3. Use {@link #loadBeanMapFromXmlResource(Class, String, Charset)} instead.
     *
     * @param resourceBaseClass
     *            A class that specifies the base for the relative location of the resource. If this parameter is null,
     *            the resource name has to specify the resource with an absolute path.
     * @param resourceName
     *            The name of the resource to load.
     * @return A newly created bean map from the supplied xml resource.
     * @throws SchemaException  When there is an error in the bean map
     * @throws UncheckedIOException      When there is an error reading from input
     */
    @Deprecated
    public static <T> BeanMap loadBeanMapFromXmlResource(Class<T> resourceBaseClass, String resourceName)
            throws UncheckedIOException{
        return loadBeanMapFromXmlResource(resourceBaseClass, resourceName, Charset.defaultCharset().name());
    }

    /**
     * Loads a bean map from specified resource using supplied character encoding.
     * Deprecated since 2.3. Use {@link #loadBeanMapFromXmlResource(Class, String, Charset)} instead.
     * @param resourceBaseClass
     *            A class that specifies the base for the relative location of the resource. If this parameter is null,
     *            the resource name has to specify the resource with an absolute path.
     * @param resourceName
     *            The name of the resource to load.
     * @param encoding
     *            The character encoding to use while reading resource.
     * @return A newly created bean map from the supplied xml resource.
     * @throws UncheckedIOException      When there is an error reading from input
     */
    @Deprecated
    public static <T> BeanMap loadBeanMapFromXmlResource(Class<T> resourceBaseClass, String resourceName, String encoding)
            throws UncheckedIOException{
        if(resourceBaseClass == null)
            return loadBeanMapFromXmlResource(Xml2BeanMapBuilder.class, resourceName, Charset.forName(encoding));
        return  loadBeanMapFromXmlResource(resourceBaseClass, resourceName, Charset.forName(encoding));
    }

    /**
     * Loads a bean map from specified resource using supplied character encoding.
     *
     * @param resourceBaseClass
     *            A class that specifies the base for the relative location of the resource. Cannot be null.
     * @param resourceName
     *            The name of the resource to load.
     * @param encoding
     *            The character encoding to use while reading resource.
     * @return A newly created bean map from the supplied xml resource.
     * @throws UncheckedIOException      When there is an error reading from input
     * @since 2.3
     */
    public static <T> BeanMap loadBeanMapFromXmlResource(Class<T> resourceBaseClass, String resourceName, Charset encoding)
            throws UncheckedIOException{
        Objects.requireNonNull(resourceBaseClass, "The resource base class cannot be null.");
        Objects.requireNonNull(resourceName, "The resource name cannot be null when trying to load xml resource.");
        try (InputStream is = resourceBaseClass.getResourceAsStream(resourceName)) {
            if (is == null) {
                throw new IOException(
                        "Failed to load resource [" + resourceName + "] from class " + resourceBaseClass.getName());
            }
            Xml2BeanMapBuilder beanMapBuilder = new Xml2BeanMapBuilder();
            return beanMapBuilder.build(new InputStreamReader(is, encoding));
        } catch (IOException  e) {
            throw new UncheckedIOException("Failed to load bean map from xml resource", e);
        }
    }

}
