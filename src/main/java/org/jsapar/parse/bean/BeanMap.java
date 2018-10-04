package org.jsapar.parse.bean;

import org.jsapar.error.BeanException;
import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaLine;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A class that defines the mapping
 * <ol><li>Between the bean class and the line type</li>
 * <li>Between each bean property and the cell name</li></ol>
 */
public class BeanMap {

    private Map<Class, BeanPropertyMap> beanPropertyMap = new HashMap<>();
    private Map<String, BeanPropertyMap> beanPropertyMapByLineType = new HashMap<>();

    /**
     * @param aClass The class to get a {@link BeanPropertyMap} for.
     * @return An optional {@link BeanPropertyMap} that can be used for supplied class or any of its super classes. Empty if
     * no {@link BeanPropertyMap} was mapped that can be used for supplied class.
     */
    public Optional<BeanPropertyMap> getBeanPropertyMap(Class<?> aClass) {
        BeanPropertyMap beanPropertyMap = this.beanPropertyMap.get(aClass);
        if (beanPropertyMap != null)
            return Optional.of(beanPropertyMap);
        final Class superClass = aClass.getSuperclass();
        if (superClass == null || superClass == Object.class)
            return Optional.empty();
        return getBeanPropertyMap(superClass);
    }

    public BeanPropertyMap getBeanPropertyMap(String lineType){
        return beanPropertyMapByLineType.get(lineType);
    }

    public void putBean2Line(String className, BeanPropertyMap beanPropertyMap) throws ClassNotFoundException {
        putBean2Line(Class.forName(className), beanPropertyMap);
    }

    public void putBean2Line(Class<?> aClass, BeanPropertyMap beanPropertyMap) {
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
    public static BeanMap ofSchema(Schema schema) throws BeanException {
        try {
            BeanMap beanMap = new BeanMap();

            for (SchemaLine schemaLine : schema.getSchemaLines()) {
                beanMap.putBean2Line(schemaLine.getLineType(), BeanPropertyMap.ofSchemaLine(schemaLine));
            }
            return beanMap;
        } catch (ClassNotFoundException e) {
            throw new BeanException("Failed to build bean mapping based on schema", e);
        }
    }

    /**
     * Creates a BeanMap based on a supplied map defining which BeanPropertyMap to use for each full class name.
     * @param beanPropertyMaps A list of BeanPropertyMap to use for each class.
     * @return a BeanMap based on a supplied map defining which BeanPropertyMap to use for each full class name
     * @throws ClassNotFoundException If one of the full class names does not exist in the class path.
     */
    public static BeanMap ofBeanPropertyMaps(Collection<BeanPropertyMap> beanPropertyMaps) throws ClassNotFoundException {
        BeanMap beanMap = new BeanMap();
        for (BeanPropertyMap entry : beanPropertyMaps) {
            beanMap.putBean2Line(entry.getLineClass(), entry);
        }
        return beanMap;
    }

    public static BeanMap ofXml(Reader reader) throws IOException, ClassNotFoundException {
        Xml2BeanMapBuilder builder = new Xml2BeanMapBuilder();
        return builder.build(reader);
    }

}
