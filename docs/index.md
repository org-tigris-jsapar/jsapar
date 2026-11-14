---
layout: page
title: JSaPar Contents
---
**JSaPar** stands for  **J**ava **S**chem**a** based **Par**ser

JSaPar is a Java library providing a schema based parser and composer of almost all sorts of delimited and fixed 
width files.

It is an open source java library created with the purpose of
making it easy to process delimited and fixed width data sources.
By separating the description of the data format into a schema that can be loaded from XML it makes the code
easier to maintain and increases flexibility.

* TOC
{:toc}

## Mission
The goal of this project is a java library that removes the burden of parsing and composing flat files and csv files from the developer.

The library should
* Be easy to use for both simple and complex situations.
* Be possible to extend.
* Have a low memory impact and good performance.
* Be flexible to use in different situations.
* Be independent of other (third party) libraries.
* Use schemas in order to distinctly separate the description of the format of the data source from the code.
* Unburden the tremendous tasks for a developer dealing with fixed width and delimited data sources.

## Features
* Support for type conversion while parsing and composing.
* Uses a schema to express the source or target format.
* Support for different type of lines where line type is determined by the value of defined "condition cells". 
* The schema can be expressed with xml notation or created directly within the java code.
* Can handle internationalization of numbers and dates both while parsing and composing.
* The parser can either produce a Document class, representing the content of the file, or you can choose to provide 
a consumer for each line that is successfully parsed.
* Can handle huge files without loading everything into memory.
* The output Document class contains a list of lines which contains a list of cells.
* The input and outputs are given by java.io.Reader and java.io.Writer which means that it is not necessarily files
that are parsed or generated.
* The schema contains information about the format of each cell regarding data type and syntax.
* Errors can either be handled by exceptions thrown at first error or the errors can be collected during
parsing to be able to deal with them later.
* Can consume or produce an internal xml format which can be used to transform any of the supported formats 
into any markup language by the use of xslt.
### Bean mapping features
* Support converting Java objects to or from any of the other supported input or output formats.
* Supports natively all primitive types, `Number`, `String`, `BigDecimal`, `BigInteger`, `Enum`, `java.util.Date` and the `java.time` types.
* It is possible to programmatically make manipulations and transformations between parsing and the bean is created which makes it possible to 
join and split cells that do not fit a bean property one-to-one.
### CSV features
* Support for CSV and all other kinds delimited files such as TAB-separated or multi character separated.
* Configurable line separator character sequence.
* Handles quoted cells with configurable quote character.
* Can handle quotes both according to [RFC4180](https://tools.ietf.org/html/rfc4180) and naive quoting first and last.
* Handles multi line quoted CSV cells. Line breaks are allowed within quoted cells.
* Cell separator can be configured as one or multiple characters.
### Fixed width features
* Can both parse and compose flat files with fixed positions with or without line separator characters.
* Custom fill character
* Custom cell alignment
* Implied decimal

## Documentation
* [Release notes](release_notes)
* [Why JSaPar](why_jsapar) - What makes it different and when to use something else
* [Introduction](introduction)
* [Basics](basics)
* [Basics of Schema](basics_schema)
* [Bean mapping](bean_mapping)
* [Advanced](advanced)
* [Javadocs API documentation](https://javadoc.io/doc/org.tigris.jsapar/jsapar)
* [Upgrading from earlier versions of JSaPar](upgradingfrom)

## Resources
* [Source code and issues](https://github.com/org-tigris-jsapar/jsapar)
* [Examples](https://github.com/org-tigris-jsapar/jsapar-examples)
* [Maven coordinates and binaries download](https://central.sonatype.com/artifact/org.tigris.jsapar/jsapar)
* For questions about usage, use [Stack overflow with the jsapar tag](https://stackoverflow.com/questions/tagged/jsapar)

## Dependencies
This project has no dependencies to other external libraries in runtime. The only Java module that is required, apart from 
`java.base`, is `java.xml`. 

Required Java version in runtime:

|JSaPar version|Java version|
|---|---|
|2.5|21 or later|
|2.4|17 or later|
|2.3|11 or later|
|2.0 - 2.2|8 or later|
|1.5 - 1.8|7 or later|

The library is a named Java module. If your project uses modules, add this to your `module-info.java`:
```java
requires org.jsapar;
```

If you plan to build and test the source consider the following:

* To build the source you need [Maven](https://maven.apache.org/) and a JDK of at least the version in the table above. 
* The provided test classes require JUnit 6 (JUnit Jupiter) in order to run. It is only used while testing.

## Other sources of information
* [Links to other related projects](links)


