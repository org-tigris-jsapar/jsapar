package org.jsapar.parse.bean.reflect;

import java.lang.reflect.Method;
import java.util.*;

public class BeanInfoReflection implements BeanInfo {
    private static final String GET_PREFIX = "get";
    private static final String SET_PREFIX = "set";
    private static final String IS_PREFIX = "is";

    private final Map<String, PropertyDescriptor> propertyDescriptorsByName;

    public BeanInfoReflection(Class c) {
        propertyDescriptorsByName = makePropertyDescriptors(c);
    }

    @Override
    public Map<String, PropertyDescriptor> getPropertyDescriptorsByName() {
        return propertyDescriptorsByName;
    }

    private static Map<String, PropertyDescriptor> makePropertyDescriptors(Class c) {
        Map<String, PropertyDescriptor> descriptors = new HashMap<>();
        List<Method> methods = new LinkedList<>(Arrays.asList(c.getMethods()));
        while(!methods.isEmpty()){
            Method method = methods.remove(0);
            if(method.getName().startsWith(GET_PREFIX)
                    && method.getName().length()>GET_PREFIX.length()
                    && !method.getName().equals("getClass")){
                String suffix = method.getName().substring(GET_PREFIX.length());
                if(method.getReturnType().isAssignableFrom(Boolean.class)){
                    methods.stream().filter(m->m.getName().equals(IS_PREFIX + suffix)).findFirst().ifPresent(is->{
//                        method =
                    });
                }
            }
                continue;

        }
        return descriptors;
    }


    public static String decapitalize(String name) {
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) &&
                Character.isUpperCase(name.charAt(0))){
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

}
