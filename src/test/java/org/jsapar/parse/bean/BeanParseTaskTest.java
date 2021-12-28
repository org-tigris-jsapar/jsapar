package org.jsapar.parse.bean;

import org.jsapar.TstPerson;
import org.jsapar.bean.BeanMap;
import org.jsapar.model.Line;
import org.jsapar.parse.CollectingConsumer;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BeanParseTaskTest {

    private BeanMap makeBeanMap() {
        return BeanMarshallerTest.makeBeanMap();
    }

    @Test
    public void testBuild() throws IOException {
        List<TstPerson> people = new ArrayList<>(2);
        TstPerson person = new TstPerson();
        person.setFirstName("Jonas");
        people.add(person);

        person = new TstPerson();
        person.setFirstName("Test2");
        people.add(person);

        BeanParseTask<TstPerson> parser = new BeanParseTask<>(people.stream(), makeBeanMap());
        CollectingConsumer<Line> listener = new CollectingConsumer<>();
        parser.setLineConsumer(listener);
        parser.execute();
        List<Line> lines = listener.getCollected();

        assertEquals(2, lines.size());
        Line line = lines.get(0);
        assertEquals("Jonas",
                line.getCell("firstName").orElseThrow(() -> new AssertionError("Should be set")).getStringValue());

        line = lines.get(1);
        assertEquals("Test2", line.getCell("firstName").orElseThrow(() -> new AssertionError("Should be set")).getStringValue());
        parser.close();
    }




    
}
