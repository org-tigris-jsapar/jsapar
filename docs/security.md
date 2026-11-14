---
layout: page
title: Security
---
<h1>Security</h1>
This article describes what you need to consider when you use the library with data that comes from a source that you 
do not fully trust, such as files uploaded by users or received from other organizations.

* TOC
{:toc}

# What to trust
The library distinguishes between two kinds of input:

|Input|Examples|Treated as|
|---|---|---|
|**Data**|The text that is parsed, the beans or lines that are composed.|Untrusted. The library is built to handle any content.|
|**Configuration**|Schemas, bean maps, xslt documents, the arguments to the command line converter.|Trusted. Should only come from you or someone you trust as much as your own code.|

The reason that configuration needs to be trusted is that it controls what the library does: 
* A schema or a bean map contains names of classes to create and of properties to assign. 
* A schema contains regular expressions. An expression can be written in such a way that it takes a very long time to 
evaluate for certain values.
* A xslt document is a program that is executed by the xslt transformer of the Java runtime.

**Do not let the users of your application upload or edit schemas, bean maps or xslt documents** unless they 
are as trusted as the developers of the application. If you need to let users describe a format, let them do it in a 
user interface of your own and build the schema [in code](basics_schema#the-schema) from the validated result.

The rest of this article is about untrusted data.

# Parsing untrusted text
## Memory usage
Parsing is done one line at a time, so the size of the input does not affect how much memory that is used by the 
library. There are however some things to consider:

* **The maximum length of a line is limited.** The default is 8192 characters and a line that is longer than that 
results in an error instead of the library trying to read it into memory. This also limits the damage of a start quote 
without matching end quote in CSV input. If you need to raise the limit with 
`TextParseConfig.setMaxLineLength()`, do not set it higher than you need.
* **Do not collect all lines in memory.** A `Document`, the `DocumentBuilderLineConsumer` and the `CollectingConsumer` 
store everything that is parsed. Handle each line when it is parsed instead, or limit the number of lines with 
`stream(reader).limit(n)`, if the size of the input is not under your control.
* **Limit the number of collected errors.** If you [collect errors](basics#format-errors) instead of failing at the first one, an 
input where every line is wrong produces as many errors as there are lines. Use the 
`ThresholdCollectingErrorConsumer` which aborts the parsing with an exception when there are too many errors:

```java
TextParser parser = new TextParser(schema);
ThresholdCollectingErrorConsumer errors = new ThresholdCollectingErrorConsumer(100);
parser.setErrorConsumer(errors);
parser.parseForEach(reader, line -> handle(line));
```
The library reads from the `java.io.Reader` that you supply. If you need to limit the total size of the input or the time 
it takes to receive it, do that where you create the reader.

## Validate the values
The schema describes the format, and you can make it strict by declaring cells as mandatory, by specifying 
[the type and the pattern](basics_schema#cell-formats) and by restricting values to a range. This will reject input that is not well formatted, but 
a value that has the correct format is not necessarily harmless. The parsed values are still untrusted data when 
you use them in your application, so the usual precautions apply. Use for instance parameters when using the values in 
SQL statements, and escape them when presenting them in HTML.

## Error messages contain the data
The message of a parsing error contains the value that could not be parsed, for example:
```text
Line=1 Cell='Age' Value='notanumber' Expected: CellType=INTEGER, Format={USIntegerFormat} - Failed to parse integer from value [notanumber]
```
That is convenient when finding the problem, but consider it before writing the messages to a log or showing them 
to a user if the input may contain personal or other sensitive information. Line number and cell name are available 
separately on the `CellParseException` if you want to create your own message.  

# Parsing untrusted xml
The [internal xml format](advanced#using-xml-as-input) can be parsed with `XmlParser` and `Xml2TextConverter`. As of 
version 2.5 the following applies to all xml that is loaded by the library, including schemas and bean maps:
* A xml document that contains a `DOCTYPE` declaration is rejected.
* External entities are never resolved and external DTDs are never loaded.

This protects against what is known as XML external entity (XXE) attacks, where a specially crafted xml document makes 
the parser read local files or make network requests. **Versions before 2.5 do not have this protection**, so upgrade 
if you parse xml that you do not fully trust.

In this xml format, the line type and the names of the cells are part of the data, in contrast to when parsing text 
where they come from the schema. Never rely on them without validation, and see the next section if you create Java 
beans from the lines.

# Creating Java beans from untrusted data
When [converting text to Java beans](bean_mapping) with `Text2BeanConverter`, the classes to create and the 
properties to assign are determined by the schema and the bean map. The parsed text can only affect the values. 

The exception is the class `BeanFactoryDefault`, which is used by default if you create a `BeanComposer` yourself 
without supplying a bean factory. It uses the line type as the name of the class to create and the cell names as the 
names of the properties to assign. That is safe when the lines are parsed from text with a schema that you trust, but 
**do not use `BeanFactoryDefault` when line types or cell names originate from the data**, as they do when 
parsing xml with `XmlParser`. Otherwise, the one who creates the xml decides which of the classes in your 
application that are instantiated and which setter methods that are called. Use `BeanFactoryByMap` with a `BeanMap` 
that contains exactly the classes and properties that are allowed, or your own implementation of `BeanFactory`.

# Composing text that is opened in a spreadsheet application
If you compose a CSV file from values that originate from users, and that file is later opened in a spreadsheet 
application such as Excel, a value that starts with one of the characters `=`, `+`, `-` or `@` may be interpreted as 
a formula. This is known as CSV injection or formula injection and can be used to make the spreadsheet application 
leak data or execute commands on the computer of the one who opens the file.

The library does not alter any values while composing since it cannot know how the output is going to be used. A leading 
`-` is for instance perfectly valid in most other situations. If the output is intended for a spreadsheet application, 
you need to neutralize such values yourself. A common way is to put a single quote character in front of the value: 

```java
static void neutralizeFormulas(Line line) {
    List<Cell<?>> risky = line.stream()
            .filter(cell -> cell.getCellType() == CellType.STRING && !cell.isEmpty())
            .filter(cell -> "=+-@\t\r".indexOf(cell.getStringValue().charAt(0)) >= 0)
            .toList();
    risky.forEach(cell -> line.putCell(new StringCell(cell.getName(), "'" + cell.getStringValue())));
}
```
Call the method for each line before it is composed, or add it as a 
[line manipulator](basics#linemanipulator) when converting:
```java
converter.addLineManipulator(line -> {
    neutralizeFormulas(line);
    return true;
});
```
Only string cells are altered in the example since a number cell that is composed with a leading minus sign should 
remain a number.

# The command line converter
The [command line converter](basics#running-text-to-text-conversion-from-command-line) reads and writes the files 
that are denoted by its arguments, with the permissions of the user that runs it. If you call it from a script or from 
another application, do not build the arguments from untrusted input.

# Dependencies
The library has no dependencies to other libraries in runtime, so there are no vulnerabilities to inherit from 
third party libraries. It does rely on the xml parser and the xslt transformer of the Java runtime, so keep your Java 
runtime updated.

# Reporting a vulnerability
If you find a security problem in the library, please report it as an 
[issue on Github](https://github.com/org-tigris-jsapar/jsapar/issues). If you consider the problem too sensitive to 
describe in public, create an issue that only asks for a way to get in contact, without any details.

Security fixes are made in the latest version. See the [release notes](release_notes) for what has been fixed in 
each version.
