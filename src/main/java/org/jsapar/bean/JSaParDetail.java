package org.jsapar.bean;

import java.lang.annotation.*;

/**
 * Maps detail lines of a line type to a collection or map attribute of the bean of the master line. See
 * {@link org.jsapar.schema.SchemaLine.Builder#withParent(String)} about master and detail lines.
 * <p>
 * The attribute type can be a {@link java.util.Collection}, such as {@link java.util.List} or {@link java.util.Set},
 * or a {@link java.util.Map}. For a {@link java.util.Map}, {@link #keyCell()} must name the cell of the detail line
 * whose value is used as key. The key is the typed value of the cell, converted to the declared key type of the map:
 * a number is converted to another number type, such as {@link Integer}, if the value fits, and any value is
 * converted to its text for a {@link String} key. A key that cannot be converted is reported as an error.
 * <p>
 * If the attribute is null, a new {@link java.util.ArrayList}, {@link java.util.LinkedHashSet},
 * {@link java.util.TreeSet}, {@link java.util.ArrayDeque}, {@link java.util.LinkedHashMap}, {@link java.util.TreeMap},
 * {@link java.util.concurrent.ConcurrentHashMap} or {@link java.util.concurrent.ConcurrentSkipListMap}, the first that
 * fits the attribute type, is created and assigned with the setter, or an instance of the attribute type if it is a
 * concrete class with a public default constructor. For other attribute types, the bean needs to initialize the
 * attribute itself.
 * <p>
 * The class of the detail line needs to be mapped too, for instance by annotating it with {@link JSaParLine}. When
 * creating a {@link BeanMap} from annotated classes, the element classes of attributes annotated with this annotation
 * are added automatically if they are annotated with {@link JSaParLine}.
 * @since 2.5
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JSaParDetail {
    /**
     * @return The line type of the detail lines to add to this attribute.
     */
    String lineType();

    /**
     * @return The name of the cell in the detail line whose value is used as key when the attribute is a
     * {@link java.util.Map}. Leave empty for collections.
     */
    String keyCell() default "";
}
