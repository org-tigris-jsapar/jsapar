package org.jsapar.parse.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps a bean attribute to a schema cell. If the bean contains complex attributes that by themselves can contain cell
 * declaration, these complex attributes needs to be annotated with {@link JSaParContainsCells}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JSaParCell {
    String name();
}
