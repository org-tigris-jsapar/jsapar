---
layout: page
title: Advanced
---
<h1>Advanced</h1>
* TOC
{:toc}
*This page is under construction*

Please see the [API javadocs](api) for more advanced documentation.

How to map lines and cells to your own Java classes is described in the article about [bean mapping](bean_mapping).

The schema XSD itself provides
a lot of documentation about the details of each allowed element and attribute within the schema xml. The latest version
of the schema is located here: 

[JSaParSchema.xsd](https://raw.githubusercontent.com/org-tigris-jsapar/jsapar/master/src/main/resources/xml/schema/JSaParSchema.xsd)

If you want to download the XSD as a file, you will probably need to right-click on the link above and choose *"Save link as..."* depending on your browser.

# Advanced parsing
## Using XML as input
It is possible to parse a xml document that conforms to the 
[XMLDocumentFormat.xsd](https://raw.githubusercontent.com/org-tigris-jsapar/jsapar/master/src/main/resources/xml/schema/XMLDocumentFormat.xsd) 
(name space `http://jsapar.tigris.org/XMLDocumentFormat/2.0`). This is the same format that is produced by the `Text2XmlConverter`.

Use the class `org.jsapar.Xml2TextConverter` in order to convert such xml into text according to a schema, or the class 
`org.jsapar.parse.xml.XmlParser` in order to parse the xml and get each line supplied to a line consumer.

For security reasons, a xml document that contains a `DOCTYPE` declaration is rejected.

# Advanced converting
## Text to markup (XML or HTML)
Use the class `org.jsapar.Text2XmlConverter` in order to produce a xml output. You can register an XSLT together with this converter and in
that way you convert the text to any other text output format such as HTML.
