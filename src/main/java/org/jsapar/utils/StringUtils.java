package org.jsapar.utils;

public class StringUtils {

    /**
     * Removes all characters of specified type from the string
     *
     * @param s          The string to remove from
     * @param chToRemove The character to remove
     * @return The new string where all character of type chToRemove has been
     * removed.
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
     * @param sb         The string to remove from
     * @param chToRemove The character to remove
     * @return The new string builder where all character of type chToRemove has
     * been removed.
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
     * @param s The string to remove all white-spaces from
     * @return A string without any white-space characters.
     */
    public static String removeAllWhitespaces(String s) {
        return removeAll(s, Character::isWhitespace);
    }

    /**
     * Removes all characters that are regarded as space according to
     * Character.isSpaceChar(int codePoint) function.
     *
     * @param s The string to remove all spaces from
     * @return A string without any space characters.
     */
    public static String removeAllSpaces(String s) {
        return removeAll(s, Character::isSpaceChar);
    }

    private interface CheckCharacterType{
        boolean check(int codePoint);
    }

    /**
     * Removes all characters that are regarded as space according to
     * provided check
     *
     * @param s        String to remove from.
     * @param check    Check lambda, if returns true, character will be removed.
     * @return A string without any space characters.
     */
    private static String removeAll(String s, CheckCharacterType check) {
        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < sb.length(); i++) {
            int codePoint = sb.codePointAt(i);
            if (check.check(codePoint)) {
                for (int k = 0; k < Character.charCount(codePoint); k++) {
                    sb.deleteCharAt(i);
                }
                i--;
            }
        }
        return sb.toString();
    }

}
