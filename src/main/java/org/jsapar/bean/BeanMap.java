package org.jsapar.bean;

import org.jsapar.error.BeanException;
import org.jsapar.parse.bean.BeanPropertyMap;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaLine;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.Function;

/**
 * A class that defines the mapping
 * <ol><li>Between the bean class and the line type</li>
 * <li>Between each bean property and the cell name</li></ol>
 * Instances can be created in several ways:
 * <ol>
 *     <li>By using a builder, see {@link #builder()}</li>
 *     <li>From a schema if schema already contains class names and correct property names, see {@link #ofSchema(Schema)}</li>
 *     <li>From a schema but with overridden values if any of the class names or properties differs from the schema, see {@link #ofSchema(Schema, BeanMap)}</li>
 *     <li>From an xml file, see {@link #ofXml(Reader)}</li>
 *     <li>From an annotated class, see {@link #ofClass(Class)}, and {@link #ofClasses(List)}</li>
 * </ol>
 * either from a schema, from an xml file or from one or several annotated classes.
 * @see JSaParLine
 * @see JSaParCell
 * @see JSaParContainsCells
 */
@SuppressWarnings("ClassEscapesDefinedScope")
public class BeanMap {

    private Map<Class<?>, BeanPropertyMap>  beanPropertyMap           = new HashMap<>();
    private Map<String, BeanPropertyMap> beanPropertyMapByLineType = new HashMap<>();

    private BeanMap() {
    }

    private BeanMap(Builder builder) {
        builder.beanPropertyMaps.forEach(m->putBean2Line(m.getLineClass(), m));
    }

    /**
     * @param aClass The class to get a {@link BeanPropertyMap} for.
     * @return An optional {@link BeanPropertyMap} that can be used for supplied class or any of its super classes. Empty if
     * no {@link BeanPropertyMap} was mapped that can be used for supplied class.
     */
    public Optional<BeanPropertyMap> getBeanPropertyMap(Class<?> aClass) {
        BeanPropertyMap beanPropertyMap = this.beanPropertyMap.get(aClass);
        if (beanPropertyMap != null)
            return Optional.of(beanPropertyMap);
        final Class<?> superClass = aClass.getSuperclass();
        if (superClass == null || superClass == Object.class)
            return Optional.empty();
        return getBeanPropertyMap(superClass);
    }

    public BeanPropertyMap getBeanPropertyMap(String lineType){
        return beanPropertyMapByLineType.get(lineType);
    }

    @SuppressWarnings("WeakerAccess")
    public void putBean2Line(String className, BeanPropertyMap beanPropertyMap) throws ClassNotFoundException {
        putBean2Line(Class.forName(className), beanPropertyMap);
    }

    private void putBean2Line(Class<?> aClass, BeanPropertyMap beanPropertyMap) {
        this.beanPropertyMap.put(aClass, beanPropertyMap);
        this.beanPropertyMapByLineType.put(beanPropertyMap.getLineType(), beanPropertyMap);
    }

    /**
     * Creates a BeanMap based on a schema where each schema line type is the full class name and where each cell name
     * is the bean property name.
     *
     * @param schema The schema to use to create a BeanMap.
     * @return A BeanMap instance.
     * @throws BeanException If one of the line types describes a class name that does exist in the class path or
     *                             if one of the cell names describes a bean property that does not exist for the class.
     */
    public static  <L extends SchemaLine<? extends SchemaCell>> BeanMap ofSchema(Schema<L> schema) throws BeanException {
        try {
            BeanMap beanMap = new BeanMap();

            for (L schemaLine : schema.getSchemaLines()) {
                beanMap.putBean2Line(schemaLine.getLineType(), BeanPropertyMap.ofSchemaLine(schemaLine));
            }
            return beanMap;
        } catch (ClassNotFoundException e) {
            throw new BeanException("Failed to build bean mapping based on schema", e);
        }
    }

    /**
     * Creates a BeanMap instance based on a list of annotated classes. All classes provided need to have the
     * annotation {@link JSaParLine} and only attributes annotated with {@link JSaParCell} will be mapped to the schema
     * values.
     * @param classes A list of annotated classes.
     * @return A newly created BeanMap instance.
     */
    public static <C> BeanMap ofClasses(List<Class<C> > classes) {
        BeanMap beanMap = new BeanMap();

        for (Class<?> c : classes) {
            if (!c.isAnnotationPresent(JSaParLine.class))
                throw new BeanException(
                        "The class " + c.getName() + " needs to have the annotation " + JSaParLine.class.getSimpleName()
                                + ". Unable to create bean map.");
            JSaParLine lineAnnotation = c.getAnnotation(JSaParLine.class);
            beanMap.putBean2Line(c, BeanPropertyMap.ofClass(c, lineAnnotation.lineType()));
        }
        return beanMap;
    }

    /**
     * Creates a BeanMap instance based on an annotated class. The provided class needs to have the
     * annotation {@link JSaParLine} and only properties annotated with {@link JSaParCell} will be mapped to the schema
     * values.
     * @param lineClass The annotated class.
     * @return A newly created BeanMap instance.
     */
    public static <C> BeanMap ofClass(Class<C> lineClass) {
        return ofClasses(Collections.singletonList(lineClass));
    }

    /**
     * Creates a {@link BeanMap} based on a schema and an override bean map. This can for instance be used when the schema does
     * not contain the proper class names of the classes to instantiate. You can then provide a
     * {@link BeanMap} with only mapping between the line types and the class names.
     *
     * @param schema         The schema to use to create a BeanMap.
     * @param overrideValues A {@link BeanMap} that overrides configuration for each line/cell that it contains mapping
     *                       for. All other lines or cells are left unchanged.
     * @return A BeanMap instance.
     * @throws BeanException If one of the line types describes a class name that does exist in the class path or
     *                       if one of the cell names describes a bean property that does not exist for the class.
     */
    public static <L extends SchemaLine<? extends SchemaCell>> BeanMap ofSchema(Schema<L> schema, BeanMap overrideValues) throws BeanException {
        try {
            BeanMap beanMap = new BeanMap();

            for (L schemaLine : schema) {
                BeanPropertyMap overridePropertyMap = overrideValues.beanPropertyMapByLineType
                        .get(schemaLine.getLineType());
                if (overridePropertyMap == null) {
                    if(schemaLine.getLineType().contains(".") || classExists(schemaLine.getLineType()))
                        beanMap.putBean2Line(schemaLine.getLineType(), BeanPropertyMap.ofSchemaLine(schemaLine));
                } else {
                    final BeanPropertyMap beanPropertyMap = BeanPropertyMap
                            .ofSchemaLine(schemaLine, overridePropertyMap);
                    if (!beanPropertyMap.ignoreLine())
                        beanMap.putBean2Line(beanPropertyMap.getLineClass().getName(), beanPropertyMap);
                }
            }
            return beanMap;
        } catch (ClassNotFoundException e) {
            throw new BeanException("Failed to build bean mapping based on schema", e);
        }
    }

    /**
     * Please refactor if there is a better way of doing this.
     * @param className  The full name of the class
     * @return true if class exists, false otherwise.
     */
    private static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Creates a BeanMap based on a supplied map defining which BeanPropertyMap to use for each full class name.
     * @param beanPropertyMaps A list of BeanPropertyMap to use for each class.
     * @return a BeanMap based on a supplied map defining which BeanPropertyMap to use for each full class name
     */
    static BeanMap ofBeanPropertyMaps(Collection<BeanPropertyMap> beanPropertyMaps) {
        BeanMap beanMap = new BeanMap();
        for (BeanPropertyMap entry : beanPropertyMaps) {
            beanMap.putBean2Line(entry.getLineClass(), entry);
        }
        return beanMap;
    }

    /**
     * Creates an instance based on xml that is read from the supplied reader.
     * @param reader The reader to read xml from.
     * @return A newly created instance
     * @throws IOException In case of io error
     * @throws ClassNotFoundException If one of the classes specified in the xml does not exist in the classpath.
     */
    public static BeanMap ofXml(Reader reader) throws IOException, ClassNotFoundException {
        Xml2BeanMapBuilder builder = new Xml2BeanMapBuilder();
        return builder.build(reader);
    }

    /**
     * @return A newly created builder that builds a BeanMap instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class that builds BeanMap instances.
     */
    public static class Builder{
        List<BeanPropertyMap> beanPropertyMaps = new ArrayList<>();
        private Builder() {
        }

        /**
         * Maps a line type to a bean class but completely without cells. This is only useful if this BeanMap instance
         * is to be used as override values for another BeanMap instance by using {@link BeanMap#ofSchema(Schema, BeanMap)}.
         * @param lineType  The line type type map to.
         * @param beanClass The bean class to map to.
         * @return This builder instance.
         */
        public Builder withLine(String lineType, Class<?> beanClass){
            return withLine(lineType, beanClass, l->l);
        }

        /**
         * Maps a line type to a bean class.
         * @param lineType  The line type type map to.
         * @param beanClass The bean class to map to.
         * @param builderHandler A function that should prepare a {@link BeanPropertyMapBuilder} instance with cells.
         * @return This builder instance.
         */
        public Builder withLine(String lineType, Class<?> beanClass, Function<BeanPropertyMapBuilder, BeanPropertyMapBuilder> builderHandler){
            beanPropertyMaps.add(builderHandler.apply(new BeanPropertyMapBuilder(lineType, beanClass)).build());
            return this;
        }

        /**
         * Creates the actual BeanMap.
         * @return A newly created BeanMap by using this builder.
         */
        public BeanMap build(){
            return new BeanMap(this);
        }

    }

    /**
     * Builder class that associates cells with bean properties.
     */
    public static class BeanPropertyMapBuilder {
        private final String lineType;
        private final Class<?> beanClass;
        private final Map<String, String> cellNamesOfProperty = new HashMap<>();

        private BeanPropertyMapBuilder(String lineType, Class<?> beanClass) {
            this.lineType = lineType;
            this.beanClass = beanClass;
        }

        /**
         * Associates a cell with a bean property
         * @param cellName The name of cell.
         * @param propertyName The bean property. Use dot notation to access sub-properties. E.g. address.street will
         *                     refer to the property accessed with the getter chain getAddress().getStreet().
         * @return This builder instance.
         */
        public BeanPropertyMapBuilder withCell(String cellName, String propertyName){
            cellNamesOfProperty.put(propertyName, cellName);
            return this;
        }

        /**
         * Associates a cell with a bean property when the bean property name and the cell name are the same.
         * @param propertyName The bean property and also the cell name. Use dot notation to access sub-properties. E.g. address.street will
         *                     refer to the property accessed with the getter chain getAddress().getStreet().
         * @return This builder instance.
         */
        public BeanPropertyMapBuilder withCell(String propertyName){
            return withCell(propertyName, propertyName);
        }

        private BeanPropertyMap build(){
            return BeanPropertyMap.ofClass(beanClass, lineType, cellNamesOfProperty);
        }

    }

}
