---
layout: default
title: JSaPar Contents
---

JSaPar is a Java library providing a schema based parser and composer of CSV (Comma Separated Values) and flat files.

## Mission
The goal of this project is a java library that removes the burden of parsing and composing flat files and csv files from the developer.

The library should
* Be simple to use.
* Possible to extend.
* Separate the format of the parsed/composed file from the parsing/composing code by the use of schemas.

## Existing features
* Support for flat files with fixed positions.
* Support for CSV files.
* Support converting Java objects to or from any of the other supported input or output formats.
* The schema can be expressed with xml notation or created directly within the java code.
* The parser can either produce a Document class, representing the content of the file, or you can choose to receive
 events for each line that has been successfully parsed.
* Can handle huge files without loading everything into memory.
* The output Document class contains a list of lines which contains a list of cells.
* The Document class can be transformed into a Java object (via reflection) if the schema is carefully written or by
applying a mapping configuration.
* It is also possible to produce java objects directly by using a converter.
* It is possible convert a list of java objects into a file according to a schema if the schema is carefully written
or by applying a mapping configuration.
    <!--<li>The Document class can be transformed into a xml file (according to an internal xml schema).-->
* The Document class can be built from a xml file (according to an internal xml schema).
* The input and outputs are given by java.io.Reader and java.io.Writer which means that it is not necessarily files
that are parsed or generated.
* The file parsing schema contains information about how to parse each cell regarding data type and syntax.
* Parsing errors can either be handled by exceptions thrown at first error or the errors can be collected during
parsing to be able to deal with them later.
* JUnit tests for most classes within the library.
* Support for localisation.

## Documentation
* [Introduction](introduction)
* [Basics of Schema](schemaintroduction)
* [Javadocs API documentation](api)
* [Upgrading from JSaPar 1.x versions](upgradingfrom1)

## Resources
<a href="https://github.com/org-tigris-jsapar/jsapar">Source code</a><br/>
<a href="http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.tigris.jsapar%22%20AND%20a%3A%22jsapar%22">Maven coordinates and binaries download</a><br/>
<a href="http://jsapar.tigris.org/servlets/ProjectDocumentList">Download historical binaries</a><br/>
<a href="http://jsapar.tigris.org">The original project web page with historical archive.</a><br/>

## Dependencies
This project has no dependencies to other external libraries in runtime.

However if you plan to build and test the source consider the following:

* To build the source it is recommended to use <a href="https://maven.apache.org/">Maven</a>. A Maven pom.xml is provided. As of version 1.6, the library is published in the public <a href="http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.tigris.jsapar%22%20AND%20a%3A%22jsapar%22">Maven repository</a>.
* The provided test classes require JUnit4 in order to run.
* Since JSaPar 1.5.0 the library in the download packages are built with java 1.7. Earlier versions of the library in the download packages are built with java 1.6 but with target compatibility level 1.5. It is no guarantee that the library will work for earlier version of Java.
* JSaPar version 2.0 requires Java 1.8 or later.

## Other sources of information
<ul>
<li><a href="http://jsapar.blogspot.com/">The JSaPar developer blog</a>.</li>
<li><a href="http://ezroad.blogspot.com/2010/02/last-week-friend-of-mine-decided-to.html">Parsing Huge Text Files Using Java and JSaPar</a></li>
</ul>

