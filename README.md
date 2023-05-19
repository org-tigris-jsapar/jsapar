[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.tigris.jsapar/jsapar/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.tigris.jsapar/jsapar)
[![Javadocs](https://javadoc.io/badge/org.tigris.jsapar/jsapar.svg)](https://javadoc.io/doc/org.tigris.jsapar/jsapar)

[![Java 11-ea](https://img.shields.io/badge/java-11-brightgreen.svg)](#java-11)
[![Java 12-ea](https://img.shields.io/badge/java-12-brightgreen.svg)](#java-12)
[![Java 13-ea](https://img.shields.io/badge/java-13-brightgreen.svg)](#java-13)
[![Java 14-ea](https://img.shields.io/badge/java-14-brightgreen.svg)](#java-14)
[![Java 15-ea](https://img.shields.io/badge/java-15-brightgreen.svg)](#java-15)
[![Java 16-ea](https://img.shields.io/badge/java-16-brightgreen.svg)](#java-16)
[![Java 17-ea](https://img.shields.io/badge/java-17-brightgreen.svg)](#java-17)
# jsapar
**JSaPar** stands for  **J**ava **S**chem**a** based **Par**ser

JSaPar is a Java library providing a schema based parser and composer of almost all sorts of delimited and fixed 
width files.

It is an open source java library created with the purpose of
making it easy to process delimited and fixed width data sources.
By separating the description of the data format into a schema that can be loaded from XML it makes the code
easier to maintain and increases flexibility.

* [Announcements](https://github.com/org-tigris-jsapar/jsapar/wiki/Announcements)
* [Documentation](https://org-tigris-jsapar.github.io/jsapar/)
* [Release notes](https://org-tigris-jsapar.github.io/jsapar/release_notes)
* [Javadocs API documentation](https://javadoc.io/doc/org.tigris.jsapar/jsapar)
* [Examples](https://github.com/org-tigris-jsapar/jsapar-examples)

## Mission
The goal of this project is a java library that removes the burden of parsing and composing flat files and CSV files from the developer.

The library should
* Be easy to use for both simple and complex situations.
* Be possible to extend.
* Have a low memory impact and good performance.
* Be flexible to use in different situations.
* Be independent of other (third party) libraries.
* Use schemas in order to distinctly separate the description of the format of the data source from the code.
* Unburden the tremendous tasks of a developer dealing with fixed width and delimited data sources.

## Features
* Support for type conversion while parsing and composing.
* Uses a schema to express the source or target format.
* Support for different type of lines where line type is determined by the value of defined "condition cells". 
* The schema can be expressed with xml notation or created directly within the java code.
* Can handle internationalization of numbers and dates both while parsing and composing.
* The parser can either produce a Document class, representing the content of the file, or you can choose to provide a consumer for each line that is successfully parsed.
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
* Supports natively all primitive types, Number types, String, BigDecimal, BigInteger, Enum, java.util.Date and the java.time types.
* It is possible to programmatically make manipulations and transformations between parsing and the bean is created which makes it possible to 
join and split cells that does not fit a bean property one-to-one.
### CSV features
* Support for CSV and all other delimited files such as TAB-separated or multi character separated.
* Configurable line separator character sequence.
* Handles quoted cells with configurable quote character.
* Can handle quotes both according to [RFC4180](https://tools.ietf.org/html/rfc4180) and naive quoting first and last.
* Handles multi line quoted CSV cells. Line breaks are allowed within quoted cells.
* Cell separator can be configured as one or multiple characters.
### Fixed width features
* Can both parse and compose flat files with fixed positions with or without line separator characters.
* Custom fill character
* Custom cell alignment
* [Implied decimal](https://www.ibm.com/support/knowledgecenter/en/SSLVMB_24.0.0/spss/base/syn_data_list_implied_decimal_positions.html)

## Quality goals
* All features fully documented, discussed and demonstrated.
* Unit tests for (almost) all classes within the library.
* Examples demonstrating all features.

We are not quite there yet, but we are working on it...
## Community
* Bugs and suggestions can be submitted [here on Github](https://github.com/org-tigris-jsapar/jsapar/issues). 
* For other type of questions, use the [`[jsapar]` tag in Stack exchange](https://stackoverflow.com/questions/tagged/jsapar). Remember to add the tag to new questions.
