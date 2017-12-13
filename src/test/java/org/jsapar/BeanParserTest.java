package org.jsapar;

import org.jsapar.model.Document;
import org.jsapar.model.Line;
import org.jsapar.model.LineUtils;
import org.jsapar.parse.DocumentBuilderLineEventListener;
import org.jsapar.parse.bean.BeanParseConfig;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 */
public class BeanParserTest {
    @Test
    public void getSetParseConfig() throws Exception {
        BeanParser<TstPerson> parser = new BeanParser<>();
        BeanParseConfig parseConfig = new BeanParseConfig();
        parser.setParseConfig(parseConfig);
        assertSame(parseConfig, parser.getParseConfig());
    }


    @Test
    public void testParse() throws IOException {
        List<TstPerson> people = new ArrayList<>(2);
        TstPerson person = new TstPerson();
        person.setFirstName("Jonas");
        people.add(person);

        person = new TstPerson();
        person.setFirstName("Test2");
        people.add(person);

        BeanParser<TstPerson> parser = new BeanParser<>();
        DocumentBuilderLineEventListener listener = new DocumentBuilderLineEventListener();
        parser.parse(people.iterator(), listener);
        Document doc = listener.getDocument();

        assertEquals(2, doc.size());
        Line line = doc.getLine(0);
        assertEquals("Jonas", LineUtils.getStringCellValue(line, "firstName"));

        line = doc.getLine(1);
        assertEquals("Test2", LineUtils.getStringCellValue(line, "firstName"));

    }


}