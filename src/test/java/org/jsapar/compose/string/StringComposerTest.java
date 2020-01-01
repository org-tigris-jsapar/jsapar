package org.jsapar.compose.string;

import org.jsapar.model.Document;
import org.jsapar.model.Line;
import org.jsapar.model.StringCell;
import org.jsapar.parse.CollectingConsumer;
import org.jsapar.schema.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class StringComposerTest {

    @Test
    public void compose() {
        StringSchema schema = StringSchema.builder()
                .withLine(StringSchemaLine.builder("person").withCells("FirstName", "LastName").build())
                .build();

        Document doc = new Document();

        Line line1 = new Line("person");
        line1.addCell(new StringCell("FirstName","Jonas"));
        line1.addCell(new StringCell("LastName","Stenberg"));
        doc.addLine(line1);

        Line line2 = new Line("person");
        line2.addCell(new StringCell("FirstName","Nils"));
        line2.addCell(new StringCell("LastName", "Nilsson"));
        doc.addLine(line2);

        CollectingStringConsumer listener = new CollectingStringConsumer();
        StringComposer instance = new StringComposer(schema, listener);
        instance.compose(doc);
        assertEquals(2, listener.size() );
        assertEquals("Jonas", listener.getCollected().get(0).get(0));
        assertEquals("Stenberg", listener.getCollected().get(0).get(1));
        assertEquals("Nils", listener.getCollected().get(1).get(0));
        assertEquals("Nilsson", listener.getCollected().get(1).get(1));
    }

}