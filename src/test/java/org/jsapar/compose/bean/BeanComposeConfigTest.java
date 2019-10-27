package org.jsapar.compose.bean;

import org.jsapar.error.ValidationAction;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for BeanComposeConfig
 */
public class BeanComposeConfigTest {

    @Test
    public void testGetSetOnUndefinedLineType() {
        BeanComposeConfig c = new BeanComposeConfig();
        c.setOnUndefinedLineType(ValidationAction.NONE);
        assertEquals(ValidationAction.NONE, c.getOnUndefinedLineType());
        c.setOnUndefinedLineType(ValidationAction.ERROR);
        assertEquals(ValidationAction.ERROR, c.getOnUndefinedLineType());
    }

}