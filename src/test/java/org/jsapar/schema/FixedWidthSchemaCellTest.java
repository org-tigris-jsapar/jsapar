package org.jsapar.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Locale;

import org.jsapar.parse.LineEventListener;
import org.jsapar.model.BigDecimalCell;
import org.jsapar.model.BooleanCell;
import org.jsapar.model.Cell;
import org.jsapar.model.CellType;
import org.jsapar.model.DateCell;
import org.jsapar.JSaParException;
import org.jsapar.model.NumberCell;
import org.jsapar.model.StringCell;
import org.jsapar.parse.LineErrorEvent;
import org.jsapar.parse.LineParsedEvent;
import org.jsapar.parse.ParseException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FixedWidthSchemaCellTest {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }



    @Test
    public final void testClone() throws CloneNotSupportedException {
        FixedWidthSchemaCell schemaCell = new FixedWidthSchemaCell("First name", 11);
        schemaCell.setAlignment(FixedWidthSchemaCell.Alignment.RIGHT);

        FixedWidthSchemaCell clone = schemaCell.clone();

        assertEquals(schemaCell.getName(), clone.getName());
        assertEquals(schemaCell.getAlignment(), clone.getAlignment());

        // Does not clone strings values yet. Might do that in the future.
        assertTrue(schemaCell.getName() == clone.getName());
    }

}
