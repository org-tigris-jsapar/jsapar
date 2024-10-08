---
layout: page
title: Release notes
---
# Release notes
* TOC
{:toc}
## 2.4
* Added method `Cell.cloneWithName(String)` to make it easier to create a new copy of a cell with a different name without
knowing it's type.
## 2.3
### 2.3.5
* Fixed problem with subclasses when creating bean map from schema with overrides.
* Added support for assigning java.nio.file.Path property in a class from a string cell value.

### 2.3.4
* Changed Class.forName to use current thread class loader instead of default class loader

### 2.3.3
* Fixed problem with generics creating a bean map of multiple classes of different types.

### 2.3.2
* Fixed problem with Integer/Short/Float/Double null values caused NPE instead of mapping to EmptyCell

### 2.3.1
* Support for parsing and composing java.time.Instant

### 2.3.0
1. Built with and for Java 11. Support for Java 8 and below is dropped. 
2. Added methods to `TextParser` to get stream of lines while parsing. This makes parsing lazy, i.e. lines are pulled from the source `Reader` upon requested.
3. Added methods to `Text2BeanConverter` to get stream of beans while parsing. This makes parsing lazy, i.e. lines are pulled from the source `Reader` upon requested.
4. The return value of method `Cell.getValue()` is now undefined if the cell is empty and may return null.
5. When header line is used as schema, the cell names in that header line are now trimmed from leading and trailing whitespaces. This eliminates the hard detected error of having trailing white space characters on the first line.
6. Made it possible to specify a level label in the `@JSaParContainsCells` annotation.

## 2.2
### 2.2.0
There are two main focus areas of this release:
1. Improve composing performance, both for delimited and fixed width target. Also parsing performance
benefits from some of these changes.
2. Make it easier to create Schema and BeanMap objects in Java code.

These are the specific changes:
* Java generics has been added to the Schema base classes Schema and SchemaLine.
This may break existing code if you iterate lines or cells in schema.
If you encounter problem during upgrade, in most cases it is enough to add wild card type <?> to the Schema. E.g. Schema<?>.
* The event idiom has been replaced by the more modern Consumer concept from java functional. The parsers accepts a Consumer instead 
of a native event listener. The difference is that it reduces the need to create an event for each line that is parsed, instead 
a consumer method is called. All event based methods and classes are still present in this version but have been marked as deprecated and may be removed in some future version.      
* The SchemaCell now accepts a native org.jsapar.text.Format instead of the java.text.Format. The native Format interface 
wraps the java.text.Format and makes it possible to implement different parsing 
and format that does not conform to the java.text.Format contract.
* Schema variants and BeanMap can now more easily be built in Java code by using builders.
* Cell value conditions in the xml schema can now be created with `equals` on top of the existing `matches`. Using `equals` simply checks if the value is exactly equal to supplied value with an option to ignore case.
* If you set any other locale than en-US upon a schema or a schema cell, formatting and parsing of numbers will then use locale specific symbols also when not specifying any explicit pattern.
* Convert classes such as Text2TextConverter now supports that you supply a transformer Function<Line, List<Line>> that 
allows one line from the parser to split into several lines before forwarding to the composer during converting.
* ConvertTask class has been re-written with a new interface, constructed by static constructors that provides Consumer with the behavior instead of LineManipulator. 

## 2.1
### 2.1.1
* Fixed problem that insufficient-error was not signaled correctly as en error when parsing an "ignoreread" cell.
* Fixed problem converting to/from bean when bean class was annotated using lombok @Getter/@Setter and at the same time 
implementing an interface that imposed a getter or a setter.

### 2.1.0
* The default quote character for delimited files is now the double quote character (") and quoting is enabled by default.
 If you don't want quoting support, you need to actively switch it off. See [Quoted values](basics_schema#quoted-values) in documentation.
* Support for quote syntax RFC4180 which makes both the CSV parser and composer compliant to the RFC 4180 standard regarding quoting.
* Support for enum cell type and mapping between enum values and text representation. See chapter about [enum format](basics_schema#enum-format) in the article [Basics of Schema](basics_schema).
* Support for implied decimal format. See chapter about [implied decimal](basics_schema#implied-decimal) in the article [Basics of Schema](basics_schema).
* The command line utility is now capable of also transforming the output by using a XSLT file.
* Added class **ByLineTypeLineEventListener**. A convenience class that acts as a subscription hub for line parsed events.
* When converting to and from java beans, it is now possible to map schema line types and 
 cell names by using annotations in the target class instead of providing a bean map as an xml. 
* This version of the library requires at least Java 11 to build but the official binaries are still built for Java release 8.
* The following classes has moved to package `org.jsapar.bean` in order to make them visible externally when introducing modules: 
  * org.jsapar.parse.bean.BeanMap
  * org.jsapar.parse.bean.Xml2BeanMapBuilder
* The following classes has moved to package `org.jsapar.text` in order to make them visible externally when introducing modules: 
  * org.jsapar.parse.text.TextParseConfig
* Removed dependency to `java.beans` package since that imposed dependency to `java.desktop` module.
* Introduced schema cell properties that controls whether pad characters and leading spaces should be trimmed or not while parsing.
* For cells where pad character is something other than space, the default behavior is now to trim leading spaces. The behavior from previous version can be 
obtained by setting the schema cell property to not trim leading spaces. See documentation page [Basics of Schema](basics_schema). 
* Added some checks for null values in constructors thus finding errors during initialization instead of while parsing/composing.
* The interfaces LineEventListener and ErrorEventListener are now marked with annotation @FunctionalInterface. 

## 2.0
### 2.0.1
1. Performance improvements while parsing delimited (CSV) sources and fixed width sources where lines are separated. 
Performance of the parsing part should be improved by at least 50% in a normal scenario. Both CPU and memory impact has been significantly improved.
1. Default cell cache size while parsing is now 1 (instead of 10) since this has the best characteristics in a normal scenario. A single item sized cache could be implemented in a lot easier way leading to better performance.  
1. Changed behaviour when parsing quoted delimited sources. When a start quote is found but no end quote within 8kB of data, the parser now
tries again to parse the source but considers that particular cell as not being quoted.
1. It is now possible to configure maximum line length while parsing. Default is 8kB.
1. When using first line as schema while parsing CSV and the header line 
contains an empty cell, the cells of the body at that position will now 
be ignored while reading. Previously this generated an exception and parsing was aborted.
