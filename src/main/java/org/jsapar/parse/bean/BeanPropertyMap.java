package org.jsapar.parse.bean;

import org.jsapar.bean.JSaParCell;
import org.jsapar.bean.JSaParContainsCells;
import org.jsapar.bean.JSaParDetail;
import org.jsapar.error.BeanException;
import org.jsapar.parse.bean.reflect.BeanInfo;
import org.jsapar.parse.bean.reflect.PropertyDescriptor;
import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaLine;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BeanPropertyMap {
    private final String lineType;

    private final Map<String, Bean2Cell> bean2CellByProperty = new HashMap<>();
    private final Map<String, Bean2Cell> bean2CellByCellName = new HashMap<>();
    /** In the order they were added, which is the order detail lines are composed in. */
    private final Map<String, Bean2Detail> bean2DetailByLineType = new LinkedHashMap<>();
    private BeanInfo               beanInfo;
    private Class<?>                  lineClass;

    private BeanPropertyMap(String lineType) {
        this.lineType = lineType;
    }

    private BeanPropertyMap(String lineType, Class<?> lineClass) {
        this.lineType = lineType;
        this.beanInfo = BeanInfo.ofClass(lineClass);
        this.lineClass = lineClass;
    }

    public boolean ignoreLine(){
        return lineClass == null;
    }

    public String getLineType() {
        return lineType;
    }

    Collection<Bean2Cell> getBean2Cells() {
        return bean2CellByProperty.values();
    }

    private Bean2Cell getBean2CellByProperty(String propertyName){
        return bean2CellByProperty.get(propertyName);
    }

    public Bean2Cell getBean2CellByName(String cellName){
        return bean2CellByCellName.get(cellName);
    }


    public Object createBean() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        return lineClass.getConstructor().newInstance();
    }

    public static BeanPropertyMap ofSchemaLine(SchemaLine<? extends SchemaCell> schemaLine, BeanPropertyMap overrideValues) {
        try {
            if(overrideValues.ignoreLine())
                return overrideValues;

            Map<String, String> cellNamesOfProperty = new HashMap<>(
                    schemaLine.stream().collect(Collectors.toMap(SchemaCell::getName, SchemaCell::getName)));
            putOverrideValues(cellNamesOfProperty, overrideValues, "");

            BeanPropertyMap beanPropertyMap = ofPropertyNames(overrideValues.getLineClass().getName(),
                    schemaLine.getLineType(),
                    cellNamesOfProperty);
            beanPropertyMap.bean2DetailByLineType.putAll(overrideValues.bean2DetailByLineType);
            return beanPropertyMap;
        } catch (ClassNotFoundException e) {
            throw new BeanException("Failed to create bean mapping based on schema", e);
        }
    }

    private static void putOverrideValues(Map<String, String> cellNamesOfProperty,
                                          BeanPropertyMap overrideValues,
                                          String propertyPrefix) {
        overrideValues.bean2CellByCellName
                .forEach((key, value) -> {
                    if(value.getChildren() != null){
                        putOverrideValues(cellNamesOfProperty, value.getChildren(), propertyPrefix + value.getPropertyDescriptor().getName() + ".");
                    }
                    else {
                        cellNamesOfProperty.put(propertyPrefix + value.getPropertyDescriptor().getName(), key);
                    }
                });
    }

    public static BeanPropertyMap ofSchemaLine(SchemaLine<? extends SchemaCell> schemaLine) throws BeanException {
        try {
            return ofPropertyNames(schemaLine.getLineType(),
                    schemaLine.getLineType(),
                    schemaLine.stream().collect(Collectors.toMap(SchemaCell::getName, SchemaCell::getName)));
        } catch (ClassNotFoundException  e) {
            throw new BeanException("Failed to create bean mapping based on schema", e);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static BeanPropertyMap ofPropertyNames(String className, String lineType, Map<String, String> cellNamesOfProperty) throws ClassNotFoundException{
        if(className==null || className.isEmpty()){
            return new BeanPropertyMap(lineType);
        }
        return ofClass(Class.forName(className, false, Thread.currentThread().getContextClassLoader()), lineType, cellNamesOfProperty);
    }

    public static BeanPropertyMap ofClass(Class<?> lineClass, String lineType)  {
        BeanPropertyMap beanPropertyMap = ofClass(lineClass,
                lineType,
                makeFieldEntryStream(lineClass, "", "")
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        for (java.lang.reflect.Field field : lineClass.getDeclaredFields()) {
            JSaParDetail detail = field.getAnnotation(JSaParDetail.class);
            if (detail != null)
                beanPropertyMap.withDetail(detail.lineType(), field.getName(), detail.keyCell());
        }
        return beanPropertyMap;
    }

    /**
     * Maps detail lines of a line type to a collection or map property of the bean.
     * @param detailLineType The line type of the detail lines.
     * @param propertyName   The name of the collection or map property.
     * @param keyCellName    The cell of the detail line to use as key for a map property. Null or empty for a
     *                       collection property.
     * @return This instance.
     * @throws BeanException If the property does not exist or does not fit.
     * @since 2.5
     */
    public BeanPropertyMap withDetail(String detailLineType, String propertyName, String keyCellName) {
        if (ignoreLine())
            throw new BeanException("Line type " + lineType + " has no class so it cannot have detail lines.");
        PropertyDescriptor propertyDescriptor = beanInfo.getPropertyDescriptorsByName().get(propertyName);
        if (propertyDescriptor == null)
            throw new BeanException("There is no property " + propertyName + " in class " + lineClass.getName()
                    + " for detail lines of type " + detailLineType + ".");
        bean2DetailByLineType.put(detailLineType, Bean2Detail.of(detailLineType, propertyDescriptor, keyCellName));
        return this;
    }

    /**
     * @param detailLineType The line type of the detail lines.
     * @return The mapping of detail lines of the given type, or null if there is none.
     * @since 2.5
     */
    public Bean2Detail getBean2Detail(String detailLineType) {
        return bean2DetailByLineType.get(detailLineType);
    }

    /**
     * @return All detail line mappings in the order they were added.
     * @since 2.5
     */
    public Collection<Bean2Detail> getBean2Details() {
        return bean2DetailByLineType.values();
    }

    private static Stream< Map.Entry<String, String> > makeFieldEntryStream(Class<?> c, String propertyPrefix,
                                                                            String cellNamePrefix) {
        return Arrays.stream(c.getDeclaredFields())
                .filter(f->f.isAnnotationPresent(JSaParCell.class) || (f.isAnnotationPresent(JSaParContainsCells.class) && !f.getType().isPrimitive()))
                .flatMap(f-> {
                    if (f.isAnnotationPresent(JSaParCell.class)) {
                        return Stream.of(new AbstractMap.SimpleEntry<>(propertyPrefix + f.getName(),
                                cellNamePrefix + f.getAnnotation(JSaParCell.class).name()));
                    }
                    else if (f.isAnnotationPresent(JSaParContainsCells.class) && !f.getAnnotation(JSaParContainsCells.class).name().isEmpty()){
                        return makeFieldEntryStream(f.getType(), propertyPrefix + f.getName() + '.', f.getAnnotation(JSaParContainsCells.class).name()+".");
                    }
                    return makeFieldEntryStream(f.getType(), propertyPrefix + f.getName() + '.', "");
                });
    }

    public static BeanPropertyMap ofClass(Class<?> lineClass, String lineType, Map<String, String> cellNamesOfProperty)  {
        BeanPropertyMap beanPropertyMap = new BeanPropertyMap(lineType, lineClass);

        Map<String, PropertyDescriptor> descriptors = beanPropertyMap.beanInfo.getPropertyDescriptorsByName();

        for(Map.Entry<String, String> propertyEntry : cellNamesOfProperty.entrySet()) {
            String propertyName = propertyEntry.getKey();
            String cellName = propertyEntry.getValue();
            if(cellName==null)
                cellName=propertyName;

            PropertyDescriptor propertyDescriptor = descriptors.get(propertyName);
            if(propertyDescriptor != null){
                beanPropertyMap.putBean2Cell(propertyName, Bean2Cell.ofCellName(cellName, propertyDescriptor));
            }
            else
                cellOfChildObject(cellName, propertyName, beanPropertyMap, descriptors);
        }
        return beanPropertyMap;
    }


    private static void cellOfChildObject(String cellName, String propertyName, BeanPropertyMap beanPropertyMap, Map<String, PropertyDescriptor> descriptors)  {
        if(propertyName.contains(".")){
            String[] propertyNames = propertyName.split("\\.", 2);
            PropertyDescriptor propertyDescriptor = descriptors.get(propertyNames[0]);
            if(propertyDescriptor != null) {
                beanPropertyMap.putBean2Cell(propertyNames[0], cellName,
                        cellOfChildObject(cellName,
                                propertyNames[1],
                                propertyDescriptor,
                                beanPropertyMap.getBean2CellByProperty(propertyNames[0])));
            }

        }
    }

    private static Bean2Cell cellOfChildObject(String cellName, String propertyName, PropertyDescriptor basePropertyDescriptor, Bean2Cell baseBean2Cell)  {
        if(baseBean2Cell == null) {
            Class<?> childClass = basePropertyDescriptor.getReadMethod().getReturnType();
            baseBean2Cell = Bean2Cell.ofBaseProperty(basePropertyDescriptor, new BeanPropertyMap(basePropertyDescriptor.getName(), childClass));
        }
        BeanPropertyMap beanPropertyMap = baseBean2Cell.getChildren();
        Map<String, PropertyDescriptor> descriptors = beanPropertyMap.beanInfo.getPropertyDescriptorsByName();

        PropertyDescriptor propertyDescriptor = descriptors.get(propertyName);
        if(propertyDescriptor != null){
            beanPropertyMap.putBean2Cell(propertyName, cellName, Bean2Cell.ofCellName(cellName, propertyDescriptor));
            return baseBean2Cell;
        }
        else
            cellOfChildObject(cellName, propertyName, beanPropertyMap, descriptors);
        return baseBean2Cell;
    }

    private void putBean2Cell(String propertyName, String cellName, Bean2Cell bean2Cell) {
        this.bean2CellByProperty.put(propertyName, bean2Cell);
        this.bean2CellByCellName.put(cellName, bean2Cell);
    }

    private void putBean2Cell(String propertyName, Bean2Cell bean2Cell) {
        putBean2Cell(propertyName, bean2Cell.getCellName(), bean2Cell);
    }

    public Class<?> getLineClass() {
        return lineClass;
    }

    public int size() {
        return this.bean2CellByCellName.size();
    }

}
