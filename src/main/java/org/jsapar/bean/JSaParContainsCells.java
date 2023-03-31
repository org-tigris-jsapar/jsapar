package org.jsapar.bean;

import java.lang.annotation.*;

/**
 * Annotates a complex attribute to indicate that the class of the attribute may contain more cell mapping declarations.
 * Use with precaution so that you do not cause infinite loops. There can only be one path to each declared
 * {@link JSaParCell}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JSaParContainsCells {

    /**
     * @return Appends a level within the schema for this attribute. E.g. the cell name 'address.streetName' relies on the first
     * level 'address'.
     * If no name is provided, sub entries are flattened without dot-notation in the schema cell name.
     * @since 2.3
     */
    String name() default "";

}
