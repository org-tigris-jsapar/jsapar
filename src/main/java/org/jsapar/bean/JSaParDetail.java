package org.jsapar.bean;

import java.lang.annotation.*;

/**
 * Maps detail lines of a line type to a collection or map attribute of the bean of the master line. See
 * {@link org.jsapar.schema.SchemaLine.Builder#withParent(String)} about master and detail lines.
 * <p>
 * The attribute type can be a {@link java.util.Collection}, such as {@link java.util.List} or {@link java.util.Set},
 * or a {@link java.util.Map}. For a {@link java.util.Map}, {@link #keyCell()} must name the cell of the detail line
 * whose value is used as key. The key is the typed value of the cell, so for an integer cell the key type of the map
 * is {@link Long}. If the attribute is null, a new {@link java.util.ArrayList}, {@link java.util.LinkedHashSet} or
 * {@link java.util.LinkedHashMap} is created and assigned with the setter, or an instance of the attribute type if it
 * is a concrete class.
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
