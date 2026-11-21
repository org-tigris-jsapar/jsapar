---
layout: page
title: Recipes
---
<h1>Recipes</h1>
Short solutions to common tasks. Each recipe is self-contained, so pick the one you need. The 
[basics](basics) and the [basics of schemas](basics_schema) articles explain the concepts that are used here.

* TOC
{:toc}

# Setting up
## Add the library to your build
Maven:
```xml
<dependency>
    <groupId>org.tigris.jsapar</groupId>
    <artifactId>jsapar</artifactId>
    <version>2.4.0</version>
</dependency>
```
Gradle:
```groovy
implementation 'org.tigris.jsapar:jsapar:2.4.0'
```
If your project uses Java modules, also add this to your `module-info.java`:
```java
requires org.jsapar;
```

## Load a schema from the class path
Keeping the schema as a resource within your application makes sure that the schema and the code that uses it are 
always deployed together.
```java
Schema<?> schema;
try (Reader schemaReader = new InputStreamReader(
        MyClass.class.getResourceAsStream("/schemas/payments.xml"), StandardCharsets.UTF_8)) {
    schema = Schema.ofXml(schemaReader);
}
```
A schema is not changed by parsing or composing, so load it once and use the same instance every time.

## Read and write files with a specific character encoding
The library reads from a `java.io.Reader` and writes to a `java.io.Writer`, so the character encoding is decided where 
you create them. `FileReader` and `FileWriter` use the default encoding of the platform unless you specify something else, 
which makes your application behave differently on different computers. Specify the encoding explicitly instead:
```java
try (Reader reader = Files.newBufferedReader(Path.of("in.csv"), StandardCharsets.UTF_8);
     Writer writer = Files.newBufferedWriter(Path.of("out.txt"), StandardCharsets.ISO_8859_1)) {
    new Text2TextConverter(inputSchema, outputSchema).convert(reader, writer);
}
```
Using a buffered writer is also good for performance, since the composer writes each line to the writer as soon as it is 
composed.

## Skip the byte order mark of a UTF-8 file
Files that are saved as UTF-8 by some applications on Windows, for instance Excel, start with an invisible character called 
byte order mark (BOM). The library does not remove it, so it ends up as the first character of the first cell unless 
you skip it:
```java
try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
    reader.mark(1);
    if (reader.read() != '\uFEFF')
        reader.reset();
    parser.parseForEach(reader, line -> handle(line));
}
```

# Parsing
## Parse a CSV file with a header line
When the first line contains the names of the columns, set `firstlineasschema="true"` on the line. The columns can then 
come in any order and you only need to describe the cells that are not plain strings: 
```xml
<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://jsapar.tigris.org/JSaParSchema/2.0">
  <csvschema lineseparator="\n">
    <line occurs="*" linetype="Person" cellseparator=";" firstlineasschema="true">
      <cell name="Has dog"><format type="boolean" pattern="yes;no"/></cell>
    </line>
  </csvschema>
</schema>
```
See [The first line describes cell layout](basics_schema#the-first-line-describes-cell-layout) for details.

## Parse a tab separated file
Use `\t` as cell separator. Tab separated files normally do not use quotes, so turn that off to make sure that a quote 
character within a value is treated as any other character:
```xml
<line occurs="*" linetype="Person" cellseparator="\t" quotechar="NONE">
```

## Parse a file with header, detail and trailer lines
Describe each type of line in the schema with a [line condition](basics_schema#line-condition) that tells them apart. 
In this example the first cell contains the type of the line:
```csv
H;2026-11-14
P;1234;100.50
P;5678;20.25
T;2;120.75
```
```xml
<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://jsapar.tigris.org/JSaParSchema/2.0">
  <csvschema lineseparator="\n">
    <line linetype="Header" occurs="1" cellseparator=";">
      <cell name="Type" default="H"><linecondition><match pattern="H"/></linecondition></cell>
      <cell name="Created"><format type="local_date" pattern="yyyy-MM-dd"/></cell>
    </line>
    <line linetype="Payment" cellseparator=";">
      <cell name="Type" default="P"><linecondition><match pattern="P"/></linecondition></cell>
      <cell name="Account" mandatory="true"/>
      <cell name="Amount" mandatory="true"><format type="decimal" pattern="0.00"/></cell>
    </line>
    <line linetype="Trailer" cellseparator=";">
      <cell name="Type" default="T"><linecondition><match pattern="T"/></linecondition></cell>
      <cell name="Count"><format type="integer"/></cell>
      <cell name="Total"><format type="decimal" pattern="0.00"/></cell>
    </line>
  </csvschema>
</schema>
```
The `ByLineTypeLineConsumer` lets you register one consumer for each type of line. Here it is used to verify that the 
count and the total in the trailer match the content of the file:
```java
TextParser parser = new TextParser(schema);
AtomicLong count = new AtomicLong();
BigDecimal[] total = {BigDecimal.ZERO};

ByLineTypeLineConsumer consumer = new ByLineTypeLineConsumer();
consumer.put("Header", line -> 
        System.out.println("Created " + LineUtils.getLocalDateCellValue(line, "Created").orElseThrow()));
consumer.put("Payment", line -> {
    count.incrementAndGet();
    total[0] = total[0].add(LineUtils.getDecimalCellValue(line, "Amount").orElseThrow());
});
consumer.put("Trailer", line -> {
    if (LineUtils.getLongCellValue(line, "Count").orElseThrow() != count.get()
            || LineUtils.getDecimalCellValue(line, "Total").orElseThrow().compareTo(total[0]) != 0)
        throw new IllegalStateException("The trailer does not match the content of the file");
});
parser.parseForEach(reader, consumer);
```
The same schema is used in the recipe [Compose a file with header and trailer lines](#compose-a-file-with-header-and-trailer-lines) below.

## Ignore lines that do not match any line type
By default, it is an error if a line does not fulfill the line condition of any of the line types in the schema. If 
the file contains lines that you are not interested in, for instance comments, you can tell the parser to skip them: 
```java
TextParser parser = new TextParser(schema);
parser.getParseConfig().setOnUndefinedLineType(ValidationAction.OMIT_LINE);
```

## Parse a fixed width file without line separators
Some systems produce files where the lines follow directly after each other without any line separator, and where a line 
is only determined by its length. Specify an empty line separator in the schema:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://jsapar.tigris.org/JSaParSchema/2.0">
  <fixedwidthschema lineseparator="">
    <line linetype="Person" occurs="*">
      <cell name="First name" length="8"/>
      <cell name="Last name" length="9"/>
      <cell name="Age" length="3"><format type="integer"/></cell>
    </line>
  </fixedwidthschema>
</schema>
```
This schema parses the text `Erik    Svensson  37Fredrik Larsson   42` into two lines.

## Report all errors in a file with line numbers
By default, the parsing stops with an exception at the first error. To instead get a report of all errors, register 
an error consumer that collects them. The `ThresholdCollectingErrorConsumer` gives up with an exception when there are 
more errors than the supplied maximum, which protects you from running out of memory if a large file is all wrong.
```java
TextParser parser = new TextParser(schema);
ThresholdCollectingErrorConsumer errors = new ThresholdCollectingErrorConsumer(1000);
parser.setErrorConsumer(errors);

List<Line> accepted = new ArrayList<>();
parser.parseForEach(reader, line -> {
    if (!line.hasCellErrors())
        accepted.add(line);
});

for (JSaParException error : errors.getCollected()) {
    if (error instanceof CellParseException cellError)
        System.out.printf("Line %d, cell %s: value '%s' is not valid%n", 
                cellError.getLineNumber(), cellError.getCellName(), cellError.getCellValue());
    else
        System.out.println(error.getMessage());
}
```
With the schema from the header, detail and trailer recipe above, and a file where one amount is not a number, one 
account is missing and the count in the trailer is not a number, the output is:
```text
Line 2, cell Amount: value 'abc' is not valid
Line 3, cell Account: value '' is not valid
Line 5, cell Count: value 'x' is not valid
```
Note that a line with errors is still supplied to the line consumer. A cell whose value could not be parsed is left out, while an empty 
mandatory cell is kept as an empty cell. Use 
`line.hasCellErrors()` as in the example if you only want the lines that are correct.

## Read only the beginning of a file
Use the `stream()` method. Nothing more than what is needed is read from the reader, so this is fast also for a huge file:
```java
List<Line> firstLines = parser.stream(reader).limit(10).toList();
```

# Composing
## Compose a file with header and trailer lines
Feed the composer with lines of different types in the order that they should appear in the output. The line type of 
each line decides which line in the schema that is used to format it. This example uses the same schema as the recipe 
[Parse a file with header, detail and trailer lines](#parse-a-file-with-header-detail-and-trailer-lines) above:
```java
try (TextComposer composer = new TextComposer(schema, writer)) {
    Line header = new Line("Header");
    LineUtils.setLocalDateCellValue(header, "Created", LocalDate.now());
    composer.composeLine(header);

    long count = 0;
    BigDecimal total = BigDecimal.ZERO;
    for (Payment payment : payments) {
        Line line = new Line("Payment");
        LineUtils.setStringCellValue(line, "Account", payment.getAccount());
        LineUtils.setDecimalCellValue(line, "Amount", payment.getAmount());
        composer.composeLine(line);
        count++;
        total = total.add(payment.getAmount());
    }

    Line trailer = new Line("Trailer");
    LineUtils.setLongCellValue(trailer, "Count", count);
    LineUtils.setDecimalCellValue(trailer, "Total", total);
    composer.composeLine(trailer);
}
```
The cell `Type` is never assigned in the code. It gets its value from the `default` attribute in the schema.

## Compose a CSV file with a header line
Set `firstlineasschema="true"` on the line in the schema, and a first line with the names of the cells is written 
automatically before the first line. See the recipe below for an example.

## Compose a CSV file that can be opened in Excel
What Excel expects depends on the regional settings of the computer. This is what usually works:
* Use `\r\n` as line separator.
* Use quote syntax `RFC4180`, which means that a quote character within a quoted value is written as two quote characters.
* Use the cell separator and the decimal separator of the region. In large parts of Europe that is semicolon and 
decimal comma. Specifying a [locale](basics_schema#internationalization) in the schema takes care of the decimal separator. 
* Write the file as UTF-8 and start it with a byte order mark. Otherwise, Excel assumes a different character encoding and 
characters outside ASCII get distorted.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://jsapar.tigris.org/JSaParSchema/2.0">
  <csvschema lineseparator="\r\n" quotesyntax="RFC4180">
    <locale language="sv" country="SE"/>
    <line occurs="*" linetype="Payment" cellseparator=";" firstlineasschema="true">
      <cell name="Account" />
      <cell name="Note" />
      <cell name="Amount"><format type="decimal" pattern="0.00"/></cell>
    </line>
  </csvschema>
</schema>
```
```java
try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
     TextComposer composer = new TextComposer(schema, writer)) {
    writer.write('\uFEFF'); // Byte order mark
    Line line = new Line("Payment");
    LineUtils.setStringCellValue(line, "Account", "1234");
    LineUtils.setStringCellValue(line, "Note", "He said \"hi\"; twice");
    LineUtils.setDecimalCellValue(line, "Amount", new BigDecimal("1234.5"));
    composer.composeLine(line);
}
```
This produces:
```csv
Account;Note;Amount
1234;"He said ""hi""; twice";1234,50
```
If any of the values originate from users, also read about 
[formula injection](security#composing-text-that-is-opened-in-a-spreadsheet-application) in the security article.

## Export the result of a database query
Create one line for each row of the result set and compose it immediately. Nothing but the current row is kept in memory, 
so the size of the result does not matter.
```java
try (PreparedStatement statement = connection.prepareStatement("select account, amount, booked from payment");
     ResultSet rs = statement.executeQuery();
     TextComposer composer = new TextComposer(schema, writer)) {
    while (rs.next()) {
        Line line = new Line("Payment");
        LineUtils.setStringCellValue(line, "Account", rs.getString("account"));
        LineUtils.setDecimalCellValue(line, "Amount", rs.getBigDecimal("amount"));
        LineUtils.setLocalDateCellValue(line, "Booked", rs.getObject("booked", LocalDate.class));
        composer.composeLine(line);
    }
}
```
If you already have the rows as Java objects, see [bean mapping](bean_mapping) for how to compose text directly from them.

# Converting
## Convert between CSV and fixed width
Use one schema for the input and another for the output, where the line types and the cell names are the same in both:
```java
new Text2TextConverter(csvSchema, fixedWidthSchema).convert(reader, writer);
```
This can also be done [from the command line](basics#running-text-to-text-conversion-from-command-line) without any code. See 
[Converting text to text](basics#converting-text-to-text) for details.

## Join two cells into one and filter lines while converting
Add a line manipulator to the converter. It is called for each line and can add, change and remove cells. Return 
`false` to omit the line from the output. This example joins the first and the last name into a new cell and omits all 
lines that have no last name: 
```java
Text2TextConverter converter = new Text2TextConverter(inputSchema, outputSchema);
converter.addLineManipulator(line -> {
    if (LineUtils.getStringCellValue(line, "Last name").isEmpty())
        return false;
    LineUtils.setStringCellValue(line, "Name",
            LineUtils.getStringCellValue(line, "First name") + " " + LineUtils.getStringCellValue(line, "Last name"));
    return true;
});
converter.convert(reader, writer);
```
The output schema needs to contain a cell with the name `Name`. Cells that are not present in the output schema are not written. 

## Split one line into several while converting
Use a transformer, which returns a list of lines for each line. In this example the input has a cell `Items` with a 
comma separated list and the output should have one line for each item:
```java
Text2TextConverter converter = new Text2TextConverter(inputSchema, outputSchema);
converter.setTransformer(line -> Arrays.stream(LineUtils.getStringCellValue(line, "Items").split(","))
        .map(item -> {
            Line itemLine = line.clone();
            LineUtils.setStringCellValue(itemLine, "Item", item);
            return itemLine;
        })
        .toList());
converter.convert(reader, writer);
```
The input
```csv
17;apple,pear
18;plum
```
...becomes
```csv
17;apple
17;pear
18;plum
```
Remember to clone the line as in the example, otherwise the same instance is altered and returned several times. 
See [Manipulating or transforming lines while converting](basics#manipulating-or-transforming-lines-while-converting) for details.
