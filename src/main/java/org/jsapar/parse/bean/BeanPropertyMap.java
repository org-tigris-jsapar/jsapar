package org.jsapar.parse.bean;

import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaLine;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class BeanPropertyMap {
    private String lineType;

    private Map<String, Bean2Cell> bean2CellByProperty = new HashMap<>();
    private BeanInfo beanInfo;
    private Class lineClass;

    private BeanPropertyMap(String lineType, Class lineClass) throws IntrospectionException {
        this.lineType = lineType;
        this.beanInfo = Introspector.getBeanInfo(lineClass);
        this.lineClass = lineClass;
    }

    public String getLineType() {
        return lineType;
    }

    public Collection<Bean2Cell> getBean2Cells() {
        return bean2CellByProperty.values();
    }

    public Bean2Cell getBean2CellByProperty(String propertyName){
        return bean2CellByProperty.get(propertyName);
    }

    public static BeanPropertyMap ofSchemaLine(SchemaLine schemaLine) throws ClassNotFoundException, IntrospectionException{
        return ofPropertyNames(schemaLine.getLineType(), schemaLine.getLineType(), schemaLine.stream().collect(Collectors.toMap(SchemaCell::getName, SchemaCell::getName)));
    }

    public static BeanPropertyMap ofPropertyNames(String className, String lineType, Map<String, String> cellNamesOfProperty) throws ClassNotFoundException, IntrospectionException{
        Class lineClass = Class.forName(className);
        BeanPropertyMap beanPropertyMap = new BeanPropertyMap(lineType, lineClass);

        Map<String, PropertyDescriptor> descriptors = Arrays.stream(beanPropertyMap.beanInfo.getPropertyDescriptors())
                .collect(Collectors.toMap(PropertyDescriptor::getName, pd->pd));

        for(String propertyName : cellNamesOfProperty.keySet()) {
            String cellName = cellNamesOfProperty.get(propertyName);
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

    private static void cellOfChildObject(String cellName, String propertyName, BeanPropertyMap beanPropertyMap, Map<String, PropertyDescriptor> descriptors) throws IntrospectionException {
        if(propertyName.contains(".")){
            String[] propertyNames = propertyName.split("\\.", 2);
            PropertyDescriptor propertyDescriptor = descriptors.get(propertyNames[0]);
            if(propertyDescriptor != null) {
                beanPropertyMap.putBean2Cell(propertyNames[0],
                        cellOfChildObject(cellName,
                                propertyNames[1],
                                propertyDescriptor,
                                beanPropertyMap.getBean2CellByProperty(propertyNames[0])));
            }
        }
    }

    private static Bean2Cell cellOfChildObject(String cellName, String propertyName, PropertyDescriptor basePropertyDescriptor, Bean2Cell baseBean2Cell) throws IntrospectionException {
        if(baseBean2Cell == null) {
            Class childClass = basePropertyDescriptor.getReadMethod().getReturnType();
            baseBean2Cell = Bean2Cell.ofBaseProperty(basePropertyDescriptor, new BeanPropertyMap(basePropertyDescriptor.getName(), childClass));
        }
        BeanPropertyMap beanPropertyMap = baseBean2Cell.getChildren();
        Map<String, PropertyDescriptor> descriptors = Arrays.stream(beanPropertyMap.beanInfo.getPropertyDescriptors())
                .collect(Collectors.toMap(PropertyDescriptor::getName, pd->pd));

        PropertyDescriptor propertyDescriptor = descriptors.get(propertyName);
        if(propertyDescriptor != null){
            beanPropertyMap.putBean2Cell(propertyName, Bean2Cell.ofCellName(cellName, propertyDescriptor));
            return baseBean2Cell;
        }
        else
            cellOfChildObject(cellName, propertyName, beanPropertyMap, descriptors);
        return baseBean2Cell;
    }

    private void putBean2Cell(String propertyName, Bean2Cell bean2Cell) {
        this.bean2CellByProperty.put(propertyName, bean2Cell);
    }

    public Class getLineClass() {
        return lineClass;
    }
}
