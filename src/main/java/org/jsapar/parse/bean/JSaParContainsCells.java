package org.jsapar.parse.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a complex attribute to indicate that the class of the attribute may contain more cell mapping declarations.
 * Use with precaution so that you do not cause infinite loops. There can only be one path to each declared
 * {@link JSaParCell}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JSaParContainsCells {
}
