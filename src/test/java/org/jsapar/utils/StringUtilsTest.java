package org.jsapar.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilsTest {

	@Test
	public final void testRemoveAll() {
		String sOriginal = ".This.text.has.lots.of.dots.";
		String sResult = StringUtils.removeAll(sOriginal, '.');
		assertEquals("Thistexthaslotsofdots", sResult);
	}

	@Test
	public final void testRemoveAll_nothing() {
		String sOriginal = "This text has no dots";
		String sResult = StringUtils.removeAll(sOriginal, '.');
		assertEquals("This text has no dots", sResult);
	}

	@Test
	public final void testRemoveAll_sb() {
		String sOriginal = ".This.text.has.lots.of.dots.";
		String sResult = StringUtils.removeAll(new StringBuilder(sOriginal), '.').toString();
		assertEquals("Thistexthaslotsofdots", sResult);
	}

	@Test
	public final void testRemoveAll_sb_nothing() {
		String sOriginal = "This text has no dots";
		String sResult = StringUtils.removeAll(new StringBuilder(sOriginal), '.').toString();
		assertEquals("This text has no dots", sResult);
	}

	@Test
	public final void testRemoveAllWhitespace() {
		String sOriginal = "This text has\nwhitespaces";
		String sResult = StringUtils.removeAllWhitespaces(sOriginal);
		assertEquals("Thistexthaswhitespaces", sResult);
	}

	
	@Test
	public final void testRemoveAllSpace() {
		String sOriginal = "This text has\u00A0spaces";
		String sResult = StringUtils.removeAllSpaces(sOriginal);
		assertEquals("Thistexthasspaces", sResult);
	}
	
}
