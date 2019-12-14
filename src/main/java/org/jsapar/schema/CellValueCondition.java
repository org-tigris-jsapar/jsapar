package org.jsapar.schema;

/**
 * Interface for cell validation.
 *
 * Deprecated since 2.2. Use Predicate<String>
 */
@FunctionalInterface
@Deprecated
public interface CellValueCondition {

    /**
     * @param value The string value to test.
     * @return True if the given string value satisfies the condition on this cell. False otherwise.
     */
    boolean satisfies(String value);

}
