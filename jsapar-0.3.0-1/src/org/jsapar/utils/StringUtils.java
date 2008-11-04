package org.jsapar.utils;

public class StringUtils {

	/**
	 * Removes all characters of specified type from the string
	 * 
	 * @param s
	 *            The string to remove from
	 * @param chToRemove
	 *            The character to remove
	 * @return The new string where all character of type chToRemove has been
	 *         removed.
	 */
	public static String removeAll(String s, char chToRemove) {
		if (s.indexOf(chToRemove) >= 0)
			return removeAll(new StringBuilder(s), chToRemove).toString();
		else
			return s;
	}

	/**
	 * Removes all characters of specified type from the string builder
	 * 
	 * @param sb
	 *            The string to remove from
	 * @param chToRemove
	 *            The character to remove
	 * @return The new string builder where all character of type chToRemove has
	 *         been removed.
	 */
	public static StringBuilder removeAll(StringBuilder sb, char chToRemove) {
		final String sToRemove = Character.toString(chToRemove);
		int nIndex = sb.indexOf(sToRemove);

		while (nIndex >= 0) {
			sb.deleteCharAt(nIndex);
			nIndex = sb.indexOf(sToRemove, nIndex);
		}
		return sb;
	}

	/**
	 * Removes all characters that are regarded as white-space according to
	 * Character.isWhitespace(int codePoint) function.
	 * 
	 * @param s
	 * @param chToRemove
	 * @return
	 */
	public static String removeAllWhitespaces(String s) {
		StringBuilder sb = new StringBuilder(s);
		for (int i = 0; i < sb.length(); i++) {
			int codePoint = sb.codePointAt(i);
			if (Character.isWhitespace(codePoint)) {
				for (int k = 0; k < Character.charCount(codePoint); k++) {
					sb.deleteCharAt(i);
				}
				i--;
			}
		}
		return sb.toString();
	}
	
	/**
	 * Removes all characters that are regarded as space according to
	 * Character.isSpaceChar(int codePoint) function.
	 * 
	 * @param s
	 * @param chToRemove
	 * @return
	 */
	public static String removeAllSpaces(String s) {
		StringBuilder sb = new StringBuilder(s);
		for (int i = 0; i < sb.length(); i++) {
			int codePoint = sb.codePointAt(i);
			if (Character.isSpaceChar(codePoint)) {
				for (int k = 0; k < Character.charCount(codePoint); k++) {
					sb.deleteCharAt(i);
				}
				i--;
			}
		}
		return sb.toString();
	}
	
}
