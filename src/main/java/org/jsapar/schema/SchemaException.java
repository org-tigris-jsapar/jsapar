/**
 * Copyrigth: Jonas Stenberg
 */
package org.jsapar.schema;

/**
 * @author Jonas
 *
 */
public class SchemaException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 636516160510599949L;

    /**
     *
     */
    public SchemaException() {
    }

    /**
     * @param arg0
     */
    public SchemaException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public SchemaException(Throwable arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public SchemaException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
