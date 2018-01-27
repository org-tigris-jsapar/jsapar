package org.jsapar.parse.bean;

import org.jsapar.schema.Schema;
import org.jsapar.schema.SchemaLine;

import java.beans.IntrospectionException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BeanMap {

    private Map<Class, Bean2Line> bean2LineMap = new HashMap<>();

    public Optional<Bean2Line> getBean2Line(Class<?> aClass){
        return Optional.ofNullable(bean2LineMap.get(aClass));
    }

    public void putBean2Line(String className, Bean2Line bean2Line) throws ClassNotFoundException {
        putBean2Line(Class.forName(className), bean2Line);
    }

    public void putBean2Line(Class<?> aClass, Bean2Line bean2Line) {
        bean2LineMap.put(aClass, bean2Line);
    }

    public static BeanMap ofSchema(Schema schema) throws ClassNotFoundException, IntrospectionException {
        BeanMap beanMap = new BeanMap();

        for(SchemaLine schemaLine: schema.getSchemaLines()){
            beanMap.putBean2Line(schemaLine.getLineType(), Bean2Line.ofSchemaLine(schemaLine));
        }
        return beanMap;
    }
}
