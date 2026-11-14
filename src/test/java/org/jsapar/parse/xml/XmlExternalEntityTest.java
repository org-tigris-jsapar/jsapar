package org.jsapar.parse.xml;

import org.jsapar.bean.BeanMap;
import org.jsapar.error.JSaParException;
import org.jsapar.model.Line;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies that none of the xml parsers resolves external entities or loads external DTDs (XXE).
 */
public class XmlExternalEntityTest {
    private static final String SECRET = "TOP-SECRET-CONTENT";

    @TempDir
    Path tempDir;
    private String secretUri;
    private String missingDtdUri;

    @BeforeEach
    void setUp() throws IOException {
        Path secretFile = tempDir.resolve("secret.txt");
        Files.writeString(secretFile, SECRET);
        secretUri = secretFile.toUri().toString();
        missingDtdUri = tempDir.resolve("missing.dtd").toUri().toString();
    }

    private String documentWithExternalEntity() {
        return "<?xml version=\"1.0\"?>"
                + "<!DOCTYPE document [<!ENTITY x SYSTEM \"" + secretUri + "\">]>"
                + "<document xmlns=\"http://jsapar.tigris.org/XMLDocumentFormat/2.0\">"
                + "<line linetype=\"L\"><cell name=\"c\" type=\"string\">&x;</cell></line>"
                + "</document>";
    }

    private static void assertNoSecret(List<Line> lines) {
        for (Line line : lines)
            assertFalse(line.toString().contains(SECRET), "External entity was resolved: " + line);
    }

    @Test
    public void xmlParser_parseForEach_externalEntityIsNotResolved() {
        List<Line> lines = new ArrayList<>();
        assertThrows(JSaParException.class,
                () -> new XmlParser().parseForEach(new StringReader(documentWithExternalEntity()), lines::add));
        assertNoSecret(lines);
    }

    @Test
    public void xmlParser_stream_externalEntityIsNotResolved() {
        List<Line> lines = new ArrayList<>();
        assertThrows(JSaParException.class,
                () -> new XmlParser().stream(new StringReader(documentWithExternalEntity())).forEach(lines::add));
        assertNoSecret(lines);
    }

    @Test
    public void xmlParser_documentWithoutDoctypeIsStillParsed() throws IOException {
        String xml = "<?xml version=\"1.0\"?>"
                + "<document xmlns=\"http://jsapar.tigris.org/XMLDocumentFormat/2.0\">"
                + "<line linetype=\"L\"><cell name=\"c\" type=\"string\">&lt;value&gt;</cell></line>"
                + "</document>";
        List<Line> lines = new ArrayList<>();
        new XmlParser().parseForEach(new StringReader(xml), lines::add);
        assertEquals(1, lines.size());
        assertEquals("<value>", lines.get(0).getCell("c").orElseThrow().getStringValue());

        List<Line> streamed = new XmlParser().stream(new StringReader(xml)).toList();
        assertEquals(1, streamed.size());
        assertEquals("<value>", streamed.get(0).getCell("c").orElseThrow().getStringValue());
    }

    @Test
    public void schema_externalDtdIsNotLoaded() {
        String xml = "<?xml version=\"1.0\"?>"
                + "<!DOCTYPE schema SYSTEM \"" + missingDtdUri + "\">"
                + "<schema xmlns=\"http://jsapar.tigris.org/JSaParSchema/2.0\">"
                + "<csvschema lineseparator=\"\\n\"><line occurs=\"*\" linetype=\"L\" cellseparator=\";\">"
                + "<cell name=\"c\"/></line></csvschema></schema>";
        SchemaException e = assertThrows(SchemaException.class, () -> Schema.ofXml(new StringReader(xml)));
        assertDoctypeRejected(e);
    }

    @Test
    public void beanMap_externalDtdIsNotLoaded() {
        String xml = "<?xml version=\"1.0\"?>"
                + "<!DOCTYPE beanmap SYSTEM \"" + missingDtdUri + "\">"
                + "<beanmap xmlns=\"http://jsapar.tigris.org/BeanMapSchema/2.0\">"
                + "<bean name=\"org.jsapar.TstPerson\" linetype=\"Person\"/></beanmap>";
        Exception e = assertThrows(Exception.class, () -> BeanMap.ofXml(new StringReader(xml)));
        assertDoctypeRejected(e);
    }

    /**
     * The parser should refuse the DOCTYPE before even trying to load the DTD. If it had tried, the failure would
     * have been a missing file instead.
     */
    private void assertDoctypeRejected(Throwable e) {
        StringBuilder messages = new StringBuilder();
        for (Throwable t = e; t != null; t = t.getCause()) {
            assertFalse(t instanceof java.io.FileNotFoundException, "Parser tried to load the external DTD: " + t);
            messages.append(t.getMessage()).append('\n');
        }
        assertTrue(messages.toString().contains("DOCTYPE"), "Expected DOCTYPE to be rejected but got: " + messages);
    }
}
