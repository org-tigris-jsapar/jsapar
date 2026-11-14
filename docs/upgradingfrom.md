---
layout: default
title: Upgrading from earlier versions of JSaPar
---
<h1>Upgrading from earlier versions of JSaPar</h1>
* TOC
{:toc}

## Upgrading from version 2.4
* Java 21 or later is required.
* `Cell`, `Line` and `Document` no longer implement `java.io.Serializable`. If you relied on Java serialization of these
classes you need to serialize the content in some other way, for instance by composing it as text.
* Generic type parameters have been added to `Cell`, `CellFactory`, `Format`, `SchemaCellFormat`, `Schema` and related 
classes. Code that uses these classes as raw types still compiles but gives compiler warnings. In most cases it is enough 
to add `<?>`, for example `Schema<?> schema = Schema.ofXml(reader);`
* Xml that contains a `DOCTYPE` declaration is no longer accepted, neither as schema or bean map xml nor as input to the 
`XmlParser` and `Xml2TextConverter`. Remove the `DOCTYPE` declaration from the xml.
* Composing a `DECIMAL` cell without a pattern no longer rounds the value to one decimal. Specify a pattern in the 
schema if you need a fixed number of decimals in the output.

## Upgrading from version 2.3
* Java 17 or later is required.

## Upgrading from version 2.2
* Java 11 or later is required.

## Upgrading from version 2.1

All the event based methods and classes have been replaced by corresponding Consumer based. The old methods and classes have been 
marked as deprecated so they still work but it is recommended to switch 
to the new methods. There should be a note in the javadoc for each deprecated method about how to switch to the new methods. 


## Upgrading from version 2.0
The following classes has moved to package `org.jsapar.bean` in order to make them visible externally when introducing modules: 
  * org.jsapar.parse.bean.BeanMap
  * org.jsapar.parse.bean.Xml2BeanMapBuilder

The following classes has moved to package `org.jsapar.text` in order to make them visible externally when introducing modules: 
  * org.jsapar.parse.text.TextParseConfig

## Upgrading from version 1.x
The 2.0 version of the library differs fundamentally from the 1.x versions so there is no easy way to upgrade. This
chapter will however try to help as much as possible.

Almost all package names has changed in 2.0.
## Converter
* The class `org.jsapar.io.Converter` is more or less replaced by `org.jsapar.Text2TextConverter`
* It is no longer possible to get a list of errors as return value from the converter, instead use `org.jsapar.parse.CollectingConsumer` or `org.jsapar.error.ThresholdCollectingErrorConsumer` as error consumer if you want to handle all errors when done parsing.

## Outputter
* The class `org.jsapar.output.Outputter` is more or less replaced by `org.jsapar.TextComposer`

## Line
* All the model classes have moved from `org.jsapar` package to `org.jsapar.model`
* The utility methods to get and set cell values of different types on the `Line` class have moved to the `LineUtils` class and are now static methods.