---
layout: page
title: Advanced
---
<h1>Advanced</h1>
* TOC
{:toc}
*This page is under construction*

Please see the [API javadocs](api) for more advanced documentation.

The schema XSD itself provides
a lot of documentation about the details of each allowed element and attribute within the schema xml. The latest version
of the schema is located here: 

[JSaParSchema.xsd](https://raw.githubusercontent.com/org-tigris-jsapar/jsapar/master/src/main/resources/xml/schema/JSaParSchema.xsd)

If you want to download the XSD as a file, you will probably need to right-click on the link above and choose *"Save link as..."* depending on your browser.

#Advanced parsing
## Using XML as input
It is possible to parse a xml document that conforms to the XMLDocumentFormat.xsd (http://jsapar.tigris.org/XMLDocumentFormat/2.0).
Use the class org.jsapar.XmlParser in order to parse a xml file and produce line parsed events.

#Advanced converting
## Text to markup (XML or HTML)
Use the class Text2XmlConverter in order to produce a xml output. You can register an XSLT together with this converter and in
that way you convert the text to any other text output format such as HTML.
