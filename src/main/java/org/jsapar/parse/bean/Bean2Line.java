package org.jsapar.parse.bean;

import org.jsapar.schema.SchemaCell;
import org.jsapar.schema.SchemaLine;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class Bean2Line {
    private String lineType;

    private Map<String, Bean2Cell> bean2CellByProperty = new HashMap<>();
    private BeanInfo beanInfo;

    private Bean2Line(String lineType, Class lineClass) throws IntrospectionException {
        this.lineType = lineType;
        this.beanInfo = Introspector.getBeanInfo(lineClass);
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

    public static Bean2Line ofSchemaLine(SchemaLine schemaLine) throws ClassNotFoundException, IntrospectionException{
        return ofSchemaLine(schemaLine, schemaLine.stream().collect(Collectors.toMap(SchemaCell::getName, SchemaCell::getName)));
    }

    public static Bean2Line ofSchemaLine(SchemaLine schemaLine, Map<String, String> cellNamesOfProperty) throws ClassNotFoundException, IntrospectionException{
        Class lineClass = Class.forName(schemaLine.getLineType());
        Bean2Line bean2Line = new Bean2Line(schemaLine.getLineType(), lineClass);

        Map<String, PropertyDescriptor> descriptors = Arrays.stream(bean2Line.beanInfo.getPropertyDescriptors())
                .collect(Collectors.toMap(PropertyDescriptor::getName, pd->pd));

        for(String propertyName : cellNamesOfProperty.keySet()) {
            String cellName = cellNamesOfProperty.get(propertyName);
            if(cellName==null)
                cellName=propertyName;
            SchemaCell schemaCell = schemaLine.getSchemaCell(cellName);
            if(schemaCell == null)
                continue;

            PropertyDescriptor propertyDescriptor = descriptors.get(propertyName);
            if(propertyDescriptor != null){
                bean2Line.putBean2Cell(propertyName, Bean2Cell.ofSchemaCell(schemaCell, propertyDescriptor));
            }
            else
                cellOfChildObject(schemaCell, propertyName, bean2Line, descriptors);
        }
        return bean2Line;
    }

    private static void cellOfChildObject(SchemaCell schemaCell, String propertyName, Bean2Line bean2Line, Map<String, PropertyDescriptor> descriptors) throws IntrospectionException {
        if(propertyName.contains(".")){
            String[] propertyNames = propertyName.split("\\.", 2);
            PropertyDescriptor propertyDescriptor = descriptors.get(propertyNames[0]);
            if(propertyDescriptor != null) {
                bean2Line.putBean2Cell(propertyNames[0],
                        cellOfChildObject(schemaCell,
                                propertyNames[1],
                                propertyDescriptor,
                                bean2Line.getBean2CellByProperty(propertyNames[0])));
            }
        }
    }

    private static Bean2Cell cellOfChildObject(SchemaCell schemaCell, String propertyName, PropertyDescriptor basePropertyDescriptor, Bean2Cell baseBean2Cell) throws IntrospectionException {
        if(baseBean2Cell == null) {
            Class childClass = basePropertyDescriptor.getReadMethod().getReturnType();
            baseBean2Cell = Bean2Cell.ofBaseProperty(basePropertyDescriptor, new Bean2Line(basePropertyDescriptor.getName(), childClass));
        }
        Bean2Line bean2Line = baseBean2Cell.getChildren();
        Map<String, PropertyDescriptor> descriptors = Arrays.stream(bean2Line.beanInfo.getPropertyDescriptors())
                .collect(Collectors.toMap(PropertyDescriptor::getName, pd->pd));

        PropertyDescriptor propertyDescriptor = descriptors.get(propertyName);
        if(propertyDescriptor != null){
            bean2Line.putBean2Cell(propertyName, Bean2Cell.ofSchemaCell(schemaCell, propertyDescriptor));
            return baseBean2Cell;
        }
        else
            cellOfChildObject(schemaCell, propertyName, bean2Line, descriptors);
        return baseBean2Cell;
    }

    private void putBean2Cell(String propertyName, Bean2Cell bean2Cell) {
        this.bean2CellByProperty.put(propertyName, bean2Cell);
    }

}
