package org.jsapar.parse.bean;

import org.jsapar.error.JSaParException;
import org.jsapar.schema.Xml2SchemaBuilder;
import org.jsapar.utils.XmlTypes;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.beans.IntrospectionException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class Xml2BeanMapBuilder implements XmlTypes {
    private final String NAMESPACE = "http://jsapar.tigris.org/BeanMapSchema/2.0";

    public BeanMap build(Reader reader) throws ClassNotFoundException, IOException {
        String schemaFileName = "/xml/schema/BeanMapSchema.xsd";

        try(InputStream schemaStream = Xml2SchemaBuilder.class.getResourceAsStream(schemaFileName)) {
            if (schemaStream == null)
                throw new FileNotFoundException("Could not find schema file: " + schemaFileName);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            factory.setIgnoringComments(true);
            factory.setCoalescing(true);
            factory.setNamespaceAware(true);
            factory.setValidating(true);
            factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            factory.setAttribute(JAXP_SCHEMA_SOURCE, schemaStream);

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(makeDefaultErrorHandler());
            org.xml.sax.InputSource is = new org.xml.sax.InputSource(reader);
            org.w3c.dom.Document xmlDocument = builder.parse(is);

            Element xmlRoot = xmlDocument.getDocumentElement();

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
        } catch (ClassNotFoundException | IntrospectionException e) {
            throw new JSaParException("Failed to build property map", e);
        }
    }
}
