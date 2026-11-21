---
layout: page
title: Bean mapping
---
<h1>Bean mapping</h1>
This article describes how to convert between text and your own Java classes, and the different ways there are to 
describe which class a line belongs to and which property a cell belongs to.

* TOC
{:toc}

# Overview
When converting text to Java beans, or the other way around, the library needs to know two things:
1. Which **class** corresponds to each **line type** in the schema.
2. Which **property** of that class corresponds to each **cell** in the schema.

This information is held by a `org.jsapar.bean.BeanMap`. There are several ways to create one:

|Alternative|Use when|
|---|---|
|[Names in the schema](#names-in-the-schema)|You control the schema and do not mind that it contains Java class and property names.|
|[Annotations](#annotations)|You control the bean classes and want the schema to be free from Java names.|
|[Bean map xml](#bean-map-xml)|You can change neither the schema nor the classes, or you want to keep the mapping outside the code.|
|[Bean map builder](#bean-map-builder)|As bean map xml, but you prefer to write the mapping in Java code.|
|[Schema with overrides](#combining-a-schema-with-overrides)|Most names in the schema already match, but some need to be mapped differently.|

All alternatives work in both directions. The same `BeanMap` can be used both with `Text2BeanConverter` to create beans 
from text and with `Bean2TextConverter` or `BeanCollection2TextConverter` to create text from beans.

## Requirements on the bean classes
The following requirements apply regardless of how the mapping is described:
* The class needs a public constructor without arguments.
* Each mapped property needs both a public getter and a public setter method that follows the Java bean naming 
convention, for example `getName()` and `setName()`. 
* The same applies to the classes of sub-properties, such as `Address` in the examples below. When a sub-property is 
`null` while assigning one of its values, an instance is created with the constructor without arguments and assigned 
with the setter.

This means that Java `record` classes and other immutable classes cannot be used as beans. If you need to create such 
objects, parse into [`Line` objects](basics#parsing) and create the objects from the lines in your own code, or 
provide [your own bean factory](#providing-your-own-bean-factory).

## Supported property types
The type of the cell, as described by the schema, does not need to be exactly the same as the type of the property. 
Supported property types are all primitive types and their wrapper classes, `String`, `BigDecimal`, `BigInteger`, 
enum classes, `java.util.Date` and the `java.time` types `LocalDate`, `LocalDateTime`, `LocalTime`, `ZonedDateTime` and 
`Instant`. A string cell can also be assigned to a property of type `java.nio.file.Path`.  

# The examples
All examples below use these two classes. The annotations are only needed for the [annotations](#annotations) 
alternative and can be left out otherwise.
```java
package com.example;

@JSaParLine(lineType = "Employee")
public class Employee {
    @JSaParCell(name = "Name")
    private String    name;
    @JSaParCell(name = "Employee number")
    private int       employeeNumber;
    @JSaParCell(name = "Birth date")
    private LocalDate birthDate;
    @JSaParContainsCells(name = "Home")
    private Address   address;
    private String    nickname;

    public Employee() {}
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    // ...getters and setters for the other properties in the same way.
}
```
```java
package com.example;

public class Address {
    @JSaParCell(name = "Street")
    private String street;
    @JSaParCell(name = "City")
    private String city;

    public Address() {}
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}
```
The text looks like this:
```csv
Famous Duck;17;1931-02-19;Duckstreet 13;Duckcity;Don
Mats Mouse;42;1930-09-15;Holestreet 3;Mousetown;Matte
```
...and this is the schema, where neither the line type nor the cell names, except for `nickname`, match the names in 
the Java classes:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://jsapar.tigris.org/JSaParSchema/2.0">
  <csvschema lineseparator="\n">
    <line occurs="*" linetype="Employee" cellseparator=";">
      <cell name="Name" />
      <cell name="Employee number"><format type="integer"/></cell>
      <cell name="Birth date"><format type="local_date" pattern="yyyy-MM-dd"/></cell>
      <cell name="Home.Street" />
      <cell name="Home.City" />
      <cell name="nickname" />
    </line>
  </csvschema>
</schema>
```

# Names in the schema
If no bean map is supplied, the names in the schema are used:
* The line type needs to be the full class name, for example `linetype="com.example.Employee"`. 
* The name of each cell needs to be the name of the property, for example `name="employeeNumber"`. 
* Use dot notation for properties of properties, for example `name="address.street"`. There is no limit to the number 
of levels.

Cells that do not match any property are ignored.

```java
Text2BeanConverter<Employee> converter = new Text2BeanConverter<>(schema);
```
There is a complete example of this alternative in the 
[basics](basics#converting-text-to-java-beans) article. This is the alternative that requires the least amount of code but it 
ties the schema to your Java classes, so renaming a class or a property requires that all schemas are changed as well.

The schema above cannot be used this way since `Employee` is not a full class name. Creating the converter fails with a 
`BeanException`.

# Annotations
With annotations, the mapping is described in the bean class itself. All annotations are in the package `org.jsapar.bean`. 

|Annotation|Applies to|Description|
|---|---|---|
|`@JSaParLine(lineType = "...")`|Class|Maps the class to a line type in the schema.|
|`@JSaParCell(name = "...")`|Field|Maps the property of the field to a cell with the supplied name in the schema.|
|`@JSaParContainsCells`|Field|Indicates that the class of the field contains fields that are annotated with `@JSaParCell`.|

The annotations are placed on the fields, but the values are still read and assigned with the getter and setter 
methods of the property with the same name as the field.

With `@JSaParContainsCells(name = "Home")`, the name is used as a prefix followed by a dot for all cells within that 
class. In the example above, the property `street` of the property `address` is thus mapped to the cell 
`Home.Street`. This makes it possible to use the same class for several properties, such as a home address 
and a work address. Without a name, no prefix is added and the cell is simply named `Street`.

Be careful not to create infinite loops with `@JSaParContainsCells`, for instance a class that contains a field of 
its own type.

To use an annotated class, supply it when creating the converter:
```java
Text2BeanConverter<Employee> converter = new Text2BeanConverter<>(schema, Employee.class);
converter.convertForEach(reader, employee -> {
    // Handle each employee here...
});
```
...and the same when composing:
```java
try (Bean2TextConverter<Employee> converter = new Bean2TextConverter<>(schema, Employee.class, writer)) {
    employees.forEach(converter::convert);
}
```
When the class is supplied like this, the annotations are used as [overrides of the schema](#combining-a-schema-with-overrides).
Cells that have no annotation are still mapped if the cell name matches a property name. In the example above, this 
is how `nickname` gets assigned even though the field has no annotation.

If you instead want only the annotated fields to be mapped, or if you have more than one annotated class, create 
the `BeanMap` from the classes:
```java
BeanMap beanMap = BeanMap.ofClasses(Employee.class, Department.class);
Text2BeanConverter<Object> converter = new Text2BeanConverter<>(schema, beanMap);
```

# Bean map xml
The mapping can also be described with a separate xml document. That makes it possible to map classes without changing 
neither the schema nor the classes.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beanmap xmlns="http://jsapar.tigris.org/BeanMapSchema/2.0">
  <bean name="com.example.Employee" linetype="Employee">
    <property name="name" cellname="Name"/>
    <property name="employeeNumber" cellname="Employee number"/>
    <property name="birthDate" cellname="Birth date"/>
    <property name="address.street" cellname="Home.Street"/>
    <property name="address.city" cellname="Home.City"/>
  </bean>
</beanmap>
```

|Element|Attribute|Description|
|---|---|---|
|`bean`|`name`|The full class name of the bean. If left out, lines of this type are regarded as not having any class, see [Lines without a class](#lines-without-a-class).|
|`bean`|`linetype`|The line type in the schema.|
|`property`|`name`|The name of the bean property. Use dot notation for properties of properties.|
|`property`|`cellname`|The name of the cell in the schema.|

The xml is loaded with `BeanMap.ofXml()`:
```java
try (Reader beanMapReader = new FileReader("employee-beanmap.xml")) {
    BeanMap beanMap = BeanMap.ofXml(beanMapReader);
    Text2BeanConverter<Employee> converter = new Text2BeanConverter<>(schema, beanMap);
    List<Employee> employees = converter.stream(reader).toList();
}
```
A bean map that is supplied to a converter is used as it is, so only the properties that are present in the bean map 
are mapped. With the bean map above, `nickname` is not assigned.

The xml needs to conform to the xml schema
[BeanMapSchema.xsd](https://raw.githubusercontent.com/org-tigris-jsapar/jsapar/master/src/main/resources/xml/schema/BeanMapSchema.xsd).
As with all other xml that is loaded by the library, a `DOCTYPE` declaration is not allowed.

# Bean map builder
The same mapping can be created in Java code. Note that the first argument of `withCell()` is the name of the cell 
and the second is the name of the property. If both are the same, only one argument is needed.
```java
BeanMap beanMap = BeanMap.builder()
        .withLine("Employee", Employee.class, line -> line
                .withCell("Name", "name")
                .withCell("Employee number", "employeeNumber")
                .withCell("Birth date", "birthDate")
                .withCell("Home.Street", "address.street")
                .withCell("Home.City", "address.city")
                .withCell("nickname"))
        .build();
```

# Combining a schema with overrides
Often most of the names in the schema already match the property names and only a few need to be mapped differently. 
In that case use `BeanMap.ofSchema(schema, overrideBeanMap)`. It creates a bean map where:
* Cells that are present in the override bean map are mapped as described there.
* All other cells in the schema are mapped to properties with the same name as the cell, if there is one.
* Lines that are not present in the override bean map are only mapped if the line type is a class name.

The override bean map can be created in any of the ways described above. A common case is that the only thing that 
differs is the class name. Then you only need to supply the line type and the class:
```java
BeanMap overrides = BeanMap.builder()
        .withLine("Employee", Employee.class)
        .build();
Text2BeanConverter<Employee> converter = 
        new Text2BeanConverter<>(schema, BeanMap.ofSchema(schema, overrides));
```
Supplying an annotated class directly to the constructor of a converter, as described under [Annotations](#annotations), 
is a shorter way to write `BeanMap.ofSchema(schema, BeanMap.ofClass(Employee.class))`.

# Several line types
A schema can contain several line types that each maps to a different class. Use a common base class as type argument 
of the converter, or `Object` if there is none.

This text contains one header, a department and its employees:
```csv
H;2026-11-14
D;Sales
E;Famous Duck;17
E;Mats Mouse;42
```
The schema uses [line conditions](basics_schema#line-condition) to determine the type of each line:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://jsapar.tigris.org/JSaParSchema/2.0">
  <csvschema lineseparator="\n">
    <line linetype="Header" cellseparator=";">
      <cell name="Type" default="H"><linecondition><match pattern="H"/></linecondition></cell>
      <cell name="Created" />
    </line>
    <line linetype="Department" cellseparator=";">
      <cell name="Type" default="D"><linecondition><match pattern="D"/></linecondition></cell>
      <cell name="Name" />
    </line>
    <line linetype="Employee" cellseparator=";">
      <cell name="Type" default="E"><linecondition><match pattern="E"/></linecondition></cell>
      <cell name="Name" />
      <cell name="Employee number"><format type="integer"/></cell>
    </line>
  </csvschema>
</schema>
```
With a class `Department` that is annotated with `@JSaParLine(lineType = "Department")`, the lines can be converted like this:
```java
Text2BeanConverter<Object> converter = 
        new Text2BeanConverter<>(schema, BeanMap.ofClasses(Employee.class, Department.class));
converter.getComposeConfig().setOnUndefinedLineType(ValidationAction.OMIT_LINE);

ByClassBeanConsumer<Object> consumer = new ByClassBeanConsumer<>();
consumer.put(Department.class, (bean, line) -> handleDepartment((Department) bean));
consumer.put(Employee.class, (bean, line) -> handleEmployee((Employee) bean));
converter.convertForEach(reader, consumer);
```
The `org.jsapar.compose.bean.ByClassBeanConsumer` calls a different consumer depending on the class of the bean. There is 
also a `ByLineTypeBeanConsumer` that does the same depending on the line type. You can of course also supply your own 
consumer. The consumer gets both the bean and the `Line` that it was created from, so you can for instance get the 
line number with `line.getLineNumber()`.

When composing, the class of each bean determines which line type to use, so you can feed beans of different classes 
to the same converter:
```java
try (Bean2TextConverter<Object> converter = new Bean2TextConverter<>(schema, 
        BeanMap.ofClasses(Employee.class, Department.class), writer)) {
    converter.convert(department);
    employees.forEach(converter::convert);
}
```
Since the cell `Type` has a default value in the schema, it gets the correct value in the output even though the beans do 
not have any such property:
```csv
D;Sales
E;Famous Duck;17
E;Mats Mouse;42
```

## Lines without a class
In the example above there is no class for the line type `Header`. By default, parsing a line where no class can be 
found for the line type is an error. Change that by setting the `onUndefinedLineType` of the `BeanComposeConfig`:
```java
converter.getComposeConfig().setOnUndefinedLineType(ValidationAction.OMIT_LINE);
```
When using [bean map xml](#bean-map-xml) as overrides of a schema, add a `bean` element without the `name` attribute to 
make it explicit that there should be no class for a line type, even if the line type happens to be a class name:
```xml
<bean linetype="Header"/>
```

# Detail lines
When the schema groups lines as [master and detail lines](advanced#master-and-detail-lines), the beans of the detail 
lines can be added to a collection or map property of the bean of the master line, using the annotation 
`@JSaParDetail`, `withDetail()` of the bean map builder or a `<detail>` element in the bean map xml. *New since version 2.5*

# Providing your own bean factory
If none of the alternatives above is sufficient, you can take full control of how beans are created and how values 
are assigned by implementing the interface `org.jsapar.compose.bean.BeanFactory` and assigning it to the converter with 
`Text2BeanConverter.setBeanFactory()`. This can for instance be used to fetch existing instances from somewhere 
else instead of creating new ones.

There are two implementations in the library:

|Class|Description|
|---|---|
|`BeanFactoryByMap`|The default for `Text2BeanConverter`. Creates beans and assigns values as described by a `BeanMap`.|
|`BeanFactoryDefault`|Uses the line type as class name and cell names as property names, without any `BeanMap`.|

A bean factory decides with `hasDetail()` and `assignDetailToBean()` if and how detail beans are added to the bean of
the master line. The default implementation of these methods ignores detail lines, and `BeanFactoryDefault` does not
support detail lines.

`BeanFactoryDefault` creates an instance of whatever class the line type denotes. Use it only when the line types 
originate from a trusted source such as your own schema, never when they originate from the parsed input, 
which is the case when parsing the [internal xml format](advanced#using-xml-as-input).

# Manipulating lines before the bean is created
Sometimes a bean property does not correspond to one cell. A name may for example be split into two cells in the text but 
be only one property in the bean. In that case you can add a [line manipulator or a transformer](basics#manipulating-or-transforming-lines-while-converting) to the 
converter that adds a cell with the joined value before the bean is created.
