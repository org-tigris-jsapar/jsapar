package org.jsapar.parse.bean.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A simple implementation of BeanInfo interface that uses reflection instead of java.beans since java.beans package
 * belongs to java.desktop module and would impose dependency to all desktop packages.
 */
public final class BeanInfoReflection implements BeanInfo {
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

        Map<String, Method> getters = propertyMethodsStream(c, GET_PREFIX)
                .filter(m->m.getParameterCount()==0 && !m.getReturnType().equals(Void.TYPE))
                .collect(Collectors.toMap(m->propertyName(m.getName(), GET_PREFIX.length()), m->m,
                        BeanInfoReflection::mostExplicit));

        Map<String, Method> isers = propertyMethodsStream(c, IS_PREFIX)
                .filter(m->m.getParameterCount()==0
                        && (m.getReturnType().isAssignableFrom(Boolean.class)
                            || m.getReturnType().isAssignableFrom(Boolean.TYPE)) )
                .collect(Collectors.toMap(m->propertyName(m.getName(), IS_PREFIX.length()), m->m,
                        BeanInfoReflection::mostExplicit));

        Map<String, Method> setters = propertyMethodsStream(c, SET_PREFIX)
                .filter(m->m.getParameterCount()==1 )
                .collect(Collectors.toMap(m->propertyName(m.getName(), SET_PREFIX.length()), m->m, (l, r) -> chooseSetter(l, r, getters, isers)));

        isers.forEach((property, iser)->{
            // Prefer is over get in case there are both
            getters.remove(property);
            Method setter = setters.remove(property); // Might be null
            descriptors.put(property, new PropertyDescriptorSimple(property, iser, setter));
        });
        getters.forEach((property, getter)->{
            Method setter = setters.remove(property); // Might be null
            descriptors.put(property, new PropertyDescriptorSimple(property, getter, setter));
        });
        // Add all setters that does not have any getter
        setters.forEach((property, setter)->
                descriptors.put(property, new PropertyDescriptorSimple(property, null, setter)));
        return descriptors;
    }

    /**
     * In case there are multiple getters/setters with the same name, use the one from the most explicit class. This can
     * happen when getter is injected by for instance a lombok annotation and there already is a getter/setter with the same
     * name in one of the interfaces.
     * @param l Left method
     * @param r Right method
     * @return The most appropriate getter/setter or just the left if it is not possible to determine.
     */
    private static Method mostExplicit(Method l, Method r) {
        if(l.getDeclaringClass().isAssignableFrom(r.getDeclaringClass()))
            return r;
        return l;
    }

    /**
     * In case there are multiple setters with different types, find the setter with the same argument type as any
     * getter.
     * @param l Left setter
     * @param r Right setter
     * @param getters All getters
     * @param isers   All isers
     * @return The most appropriate setter or just the left if it is not possible to determine.
     */
    private static Method chooseSetter(Method l, Method r, Map<String, Method> getters, Map<String, Method> isers) {
        if(Arrays.equals(l.getParameterTypes(), r.getParameterTypes())){
            return mostExplicit(l, r);
        }
        final String propertyName = propertyName(l.getName(), SET_PREFIX.length());
        final Method is = isers.get(propertyName);
        if(is != null){
            if(is.getReturnType().equals(l.getParameterTypes()[0]))
                return l;
            if(is.getReturnType().equals(r.getParameterTypes()[0]))
                return r;
        }
        final Method get = getters.get(propertyName);
        if(get != null){
            if(get.getReturnType().equals(l.getParameterTypes()[0]))
                return l;
            if(get.getReturnType().equals(r.getParameterTypes()[0]))
                return r;
        }
        return l;
    }

    /**
     * @param c The class to scan
     * @param prefix The prefix to check.
     * @return A stream of all methods that could be a property
     */
    private static Stream<Method> propertyMethodsStream(Class c, String prefix){
        return Arrays.stream(c.getMethods())
                .filter(m->m.getName().startsWith(prefix)
                        && m.getName().length()>prefix.length()
                        && Character.isUpperCase(m.getName().charAt(prefix.length()))
                        && Modifier.isPublic(m.getModifiers()));
    }


    /**
     * @param methodName  The method name
     * @param prefixLength  The length of the prefix.
     * @return The property name of a getter or setter
     */
    private static String propertyName(String methodName, int prefixLength) {
        if (methodName.length() > prefixLength+1 && Character.isUpperCase(methodName.charAt(prefixLength+1)) &&
                Character.isUpperCase(methodName.charAt(prefixLength))){
            return methodName.substring(prefixLength);
        }
        char[] chars = methodName.toCharArray();
        chars[prefixLength] = Character.toLowerCase(chars[prefixLength]);
        return new String(chars, prefixLength, methodName.length()-prefixLength);
    }

}
