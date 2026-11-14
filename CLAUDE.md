# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

JSaPar (**J**ava **S**chem**a** based **Par**ser) — an open-source (Apache 2.0) Java library for parsing and composing delimited (CSV) and fixed-width text files, driven by a schema that can be defined in XML or built in code. Published to Maven Central as `org.tigris.jsapar:jsapar`; hosted on GitHub (`org-tigris-jsapar/jsapar`).

This is an open source library project and **not** a microservice: none of the conventions from the workspace-level CLAUDE.md apply. A core design goal is **zero runtime dependencies** — only JDK (`java.xml`) is allowed; JUnit is test-only.

## Build & Test

Single-module Maven project, Java 21 (`java.release` property in `pom.xml`). Note that `gpg.skip` defaults to `false`, so any goal reaching `verify` (`install`, `deploy`) tries to GPG-sign — pass `-Dgpg.skip` locally.

```bash
mvn clean test                                   # compile + run tests
mvn clean install -Dgpg.skip                     # install locally without signing
mvn test -Dtest=TextParserTest                   # single test class
mvn test -Dtest=TextParserTest#testParse         # single test method
mvn javadoc:javadoc                              # doclint is enabled (accessibility,html,reference,syntax) — broken javadoc fails the release build
```

### Benchmarks

JMH benchmarks live in `src/jmh/java` (`ParseBenchmark`, `ComposeBenchmark`, parameterized by `data`: `CSV_` or `FIXED_WIDTH_` + `STRINGS`, `TYPED`, `WIDE` (32 cells), plus `CSV_QUOTED`; score = lines/s). They are only compiled with the `bench` profile, so JMH never becomes a dependency of the normal build:

```bash
mvn -Pbench test-compile exec:exec                                               # all benchmarks (~4 min)
mvn -Pbench test-compile exec:exec -Djmh.args="ParseBenchmark -p data=CSV_TYPED,CSV_WIDE -f 3"   # subset; -f 3 for before/after comparisons
```

Results are written to `target/jmh-result.json`. Single-fork compose results are noisy; use several forks when comparing.

Tests use **JUnit 6** Jupiter (`org.junit.jupiter.api.Test`, `Assertions`; use `assertThrows` for expected exceptions). Some tests (e.g. `JSaParExamplesTest`) read files from `examples/` via relative paths, so tests must run with the project root as working directory.

Releases: `mvn release:prepare release:perform` (tag format is the bare version, e.g. `2.5.0`). Branches: `master` = released, `develop` = next version; `X.Y` branches for older maintenance lines.

## Architecture

The core model is a three-level hierarchy mirrored across schema, data, and processing:

| Level | Schema (`org.jsapar.schema`) | Data (`org.jsapar.model`) |
|---|---|---|
| File | `Schema` → `CsvSchema`, `FixedWidthSchema`, `StringSchema` | `Document` |
| Line | `SchemaLine` → `CsvSchemaLine`, ... | `Line` |
| Cell | `SchemaCell` + `SchemaCellFormat` | `Cell` subclasses (`StringCell`, `IntegerCell`, `LocalDateCell`, ...) |

- **Schemas** are immutable-ish and built with generic self-typed builders (`Schema.Builder<L, S, B>`). XML schemas (`src/main/resources/xml/schema/JSaParSchema.xsd`) are loaded by `Xml2SchemaBuilder` and written back by `Schema2XmlExtractor` — keep the XSD, both classes, and `docs/` in sync when adding schema attributes.
- **Line type selection**: a schema can have multiple line types; which one applies to an input line is decided by *condition cells* (`CellValueCondition` / `MatchingCellValueCondition`) or by line order/occurrence. Matching lives in `parse/csv/CsvLineParserMatcher` and `parse/fixed/FWLineParserMatcher`.
- **Parsing** (`org.jsapar.parse`): `TextParser` (public façade) → `TextParseTask` → `TextSchemaParser.ofSchema(...)` picks `CsvParser` or `FixedWidthParser`, which produce `Line`s passed to a `Consumer<Line>`. Cell text → typed `Cell` conversion goes through `parse/cell/*CellFactory` using `org.jsapar.text.format` `Format` implementations. Parsing is streaming by design (huge files must not be loaded into memory); `Document` is only built if the caller collects it (`DocumentBuilderLineConsumer`).
- **Composing** (`org.jsapar.compose`): `Composer` implementations per format (`csv`, `fixed`, `string`, `bean`), with `line`/`cell` sub-composers. `TextComposer` is the public façade.
- **Errors**: either fail-fast via exceptions or collected through an error consumer (`org.jsapar.error`), configured on `AbstractParser`/`AbstractConverter`.
- **Converters** (root package, `org.jsapar.convert`): `Text2TextConverter`, `Text2BeanConverter`, `Bean2TextConverter`, `Text2XmlConverter`, `Xml2TextConverter`, etc. combine a parser and a composer via `ConvertTask`, with optional `LineManipulator`s in between. `ConverterMain` is the jar's CLI entry point.
- **Bean mapping**: `org.jsapar.bean.BeanMap` maps line types/cells to classes/properties, from annotations (`@JSaParLine`, `@JSaParCell`), XML (`BeanMapSchema.xsd` via `Xml2BeanMapBuilder`), or defaults. Reflection helpers are in `parse/bean/reflect`.
- **Internal XML format**: `XMLDocumentFormat.xsd` defines a generic XML representation of a `Document` (`parse/xml`, `compose` XML side), used for XSLT transforms.

### Module system

`src/main/java/module-info.java` declares module `org.jsapar` and explicitly lists exported packages. Packages like `parse.csv`, `parse.fixed`, `compose.csv`, `utils` are internal. When adding a new public-API package, export it there.

## Documentation

`docs/` is the GitHub Pages (Jekyll) site at https://org-tigris-jsapar.github.io/jsapar/. When changing behavior or public API, update `docs/release_notes.md` under the current development version heading (currently `## 2.5`) and the relevant guide page (`basics.md`, `basics_schema.md`, `advanced.md`). Example schemas/data in `examples/` double as test fixtures.
