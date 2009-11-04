/**
 * 
 */
package org.jsapar.input;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsapar.DateCell;
import org.jsapar.Document;
import org.jsapar.IntegerCell;
import org.jsapar.JSaParException;
import org.jsapar.Line;
import org.jsapar.TestPerson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author stejon0
 * 
 */
public class JavaBuilderTest {
    static final Date birthTime = new Date();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link org.jsapar.input.JavaBuilder#build(java.util.Collection)}.
     * 
     * @throws JSaParException
     */
    @Test
    public void testBuild() throws JSaParException {
        List<TestPerson> people = new ArrayList<TestPerson>(2);
        TestPerson person = new TestPerson();
        person.setFirstName("Jonas");
        people.add(person);

        person = new TestPerson();
        person.setFirstName("Test2");
        people.add(person);

        JavaBuilder builder = new JavaBuilder();
        Document doc = builder.build(people);

        assertEquals(2, doc.getNumberOfLines());
        Line line = doc.getLine(0);
        assertEquals("Jonas", line.getCell("firstName").getStringValue());

        line = doc.getLine(1);
        assertEquals("Test2", line.getCell("firstName").getStringValue());

    }

    /**
     * Test method for {@link org.jsapar.input.JavaBuilder#buildLine(java.lang.Object)}.
     * 
     * @throws JSaParException
     */
    @Test
    public void testBuildLine() throws JSaParException {
        TestPerson person = new TestPerson();
        person.setBirthTime(birthTime);
        person.setFirstName("Jonas");
        person.setLastName("Bergsten");
        person.setLuckyNumber(123456787901234567L);
        person.setShoeSize(42);

        JavaBuilder builder = new JavaBuilder();
        Line line = builder.buildLine(person);
        assertEquals("org.jsapar.TestPerson", line.getLineType());
        assertEquals(5, line.getNumberOfCells());
        assertEquals("Jonas", line.getCell("firstName").getStringValue());
        assertEquals(42, ((IntegerCell) line.getCell("shoeSize")).getNumberValue().intValue());
        assertEquals(birthTime, ((DateCell) line.getCell("birthTime")).getDateValue());
        assertEquals(123456787901234567L, ((IntegerCell) line.getCell("luckyNumber")).getNumberValue().longValue());
    }

}
