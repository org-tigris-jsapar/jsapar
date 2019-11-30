package org.jsapar.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class StringUtilsTest {
    @Test
    public void countMatches() {
        assertEquals(0, StringUtils.countMatches(null, ""));
        assertEquals(0, StringUtils.countMatches("", ""));
        assertEquals(2, StringUtils.countMatches("abABabc", "ab"));
        assertEquals(2, StringUtils.countMatches("ababaABabac", "aba"));
    }

	@Test
	public final void testRemoveAllWhitespace() {
		String sOriginal = "This text has\nwhitespaces";
		String sResult = StringUtils.removeAllWhitespaces(sOriginal);
		assertEquals("Thistexthaswhitespaces", sResult);
	}

	@Test
	public final void testRemoveAllWhitespace_nothing_to_remove() {
		String sOriginal = "Thistextdoesnot";
		String sResult = StringUtils.removeAllWhitespaces(sOriginal);
		assertSame(sOriginal, sResult);
	}

	
	@Test
	public final void testRemoveAllSpace() {
		String sOriginal = "This text has\u00A0spaces";
		String sResult = StringUtils.removeAllSpaces(sOriginal);
		assertEquals("Thistexthasspaces", sResult);
	}
	
}
