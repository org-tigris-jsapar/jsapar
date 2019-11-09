package org.jsapar.schema;

/**
 * Specifies the syntax while parsing and composing of quoted cells.
 */
public enum QuoteSyntax {
    /**
     * Quoted cells are considered quoted if and only if it begins and ends with a
     * quote character and all the intermediate characters are treated as is.
     * <p>
     * This is the most common scenario in delimited files since most sources that generates delimited files only adds
     * quotes first and last of the cell without inspecting the content.
     * <p>
     * "aaa","b""bb","ccc" will be treated as three cells with the values `aaa`, `b""bb` and `ccc`.
     * No characters will be replaced or removed between the enclosing quotes. Be aware that this mode will treat the input
     * "aaa","b"","ccc" as three cells with the values `aaa`, `b"` and `ccc`.
     * <p>
     * While composing, the content of a cell is always written as is and enclosing quotes are just added.
     */
    FIRST_LAST,

    /**
     * Parsing and composing will consider the <a href="https://tools.ietf.org/html/rfc4180">RFC 4180</a> regarding quotes.
     * Any double occurrences of quote characters will be treated as if one quote character is an escape character and
     * the other will become part of the cell value.
     * <p>
     * For instance "aaa","b""bb","ccc" will still be treated as three cells but with the values `aaa`, `b"bb` and `ccc`.
     * This mode will treat the input
     * "aaa","b"",bbb" as two cells with the values `aaa` and , `b",bbb`.
     * <p>
     * When composing quoted cells, all quotes within cell value will be escaped with an additional quote character in
     * order to make the output compliant.
     * <p>
     * According to RFC 4180, single quotes may not occur inside a quoted cell. This parser will however allow it and
     * treat it as part of the cell value as long as it is not followed by a cell or line separator.
     */
    RFC4180
}
