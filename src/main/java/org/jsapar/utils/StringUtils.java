package org.jsapar.utils;

import java.util.function.IntPredicate;

@SuppressWarnings("WeakerAccess")
public class StringUtils {


    /**
     * Removes all characters that are regarded as white-space according to
     * Character.isWhitespace(int codePoint) function.
     *
     * @param s The string to remove all white-spaces from
     * @return A string without any white-space characters.
     */
    public static CharSequence removeAllWhitespaces(CharSequence s) {
        return removeAll(s, Character::isWhitespace);
    }

    /**
     * Removes all characters that are regarded as space according to
     * Character.isSpaceChar(int codePoint) function.
     *
     * @param s The string to remove all spaces from
     * @return A string without any space characters.
     */
    public static CharSequence removeAllSpaces(CharSequence s) {
        return removeAll(s, Character::isSpaceChar);
    }


    /**
     * Removes all characters that matches
     * provided check
     *
     * @param s     String to remove from.
     * @param check Check lambda, if returns true, character will be removed.
     * @return A string without any characters matching the check.
     */
    private static CharSequence removeAll(CharSequence s, IntPredicate check) {
        for(int i=0; i<s.length(); i++){
            if(check.test(s.charAt(i))){
                return removeAll(s, check, i);
            }
        }
        return s;
    }

    /**
     * Removes all characters that matches
     * provided check where first confirmed match is found.
     * @param s        String to remove from.
     * @param check    Check lambda, if returns true, character will be removed.
     * @param firstFound The position of the first confirmed match.
     * @return A string without any characters matching the check.
     */
    private static CharSequence removeAll(CharSequence s, IntPredicate check, int firstFound) {
        StringBuilder sb = new StringBuilder(s.length()-1);
        sb.append(s, 0, firstFound);

        for(int i=firstFound+1; i<s.length(); i++){
            char character = s.charAt(i);
            if(!check.test(character)){
                sb.append(character);
            }
        }
        return sb;
    }

    /**
     * Replaces all occurrences of some common control characters into escaped string values.
     * @param sToReplace The string containing control characters
     * @return A string containing only escaped character values.
     */
    public static String replaceJava2Escapes(String sToReplace) {
        sToReplace = sToReplace.replace("\r", "\\r");
        sToReplace = sToReplace.replace("\n", "\\n");
        sToReplace = sToReplace.replace("\t", "\\t");
        sToReplace = sToReplace.replace("\f", "\\f");
        return sToReplace;
    }

    /**
     * Replaces escaped string value of \n, \r, \t and \f with their ascii control code values.
     * @param sToReplace The string to replace escaped strings within.
     * @return The string with all escaped values replaced with control code values.
     */
    public static String replaceEscapes2Java(String sToReplace) {
        //   Since it is a regex we need 4 \
        sToReplace = sToReplace.replace("\\r", "\r");
        sToReplace = sToReplace.replace("\\n", "\n");
        sToReplace = sToReplace.replace("\\t", "\t");
        sToReplace = sToReplace.replace("\\f", "\f");
        return sToReplace;
    }

}
