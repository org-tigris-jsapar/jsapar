package org.jsapar.bean;

import java.lang.annotation.*;

/**
 * Maps a bean attribute to a schema cell. If the bean contains complex attributes that by themselves can contain cell
 * declaration, these complex attributes needs to be annotated with {@link JSaParContainsCells}
 * @see JSaParContainsCells
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JSaParCell {
    /**
     * @return The name of the cell as described in the schema.
     */
    String name();
}
