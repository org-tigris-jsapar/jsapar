package org.jsapar.parse.bean.reflect;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.Assert.*;

public class BeanInfoReflectionTest {

    public static class TestBean1{
        private String arg;
        private int    num;

        public String getArg() {
            return arg;
        }

        public void setArg(String arg) {
            this.arg = arg;
        }

        public int getNum() {
            return num;
        }

        public void setNum(short num) {
            this.num = (int) num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public void setNum(long num) {
            this.num = (int) num;
        }

        public String isSome(){
            return "some";
        }

        public boolean isThat(){
            return false;
        }

        private String getPrivate(){
            return "private";
        }

        public String getURL(){
            return "https://github.com/org-tigris-jsapar/jsapar";
        }

        public String getoutofhere(){
            return "now";
        }

        public String get(){
            return "that thing";
        }

        public void set(String s){
        }

        public String getWithArgs(String key){
            return "value";
        }

        public void getVoid(){}
    }

    public interface TestInterface{
        String getArg();
    }

    public static class TestBean2 extends TestBean1 implements TestInterface{
        private String arg;

        public String getArg() {
            return arg;
        }

        public void setArg(String arg) {
            this.arg = arg;
        }
    }

    @Test
    public void getPropertyDescriptorsByName() {
        BeanInfo beanInfo = new BeanInfoReflection(TestBean1.class);
        Map<String, PropertyDescriptor> properties = beanInfo.getPropertyDescriptorsByName();
        assertEquals("getArg", properties.get("arg").getReadMethod().getName());
        assertEquals("setArg", properties.get("arg").getWriteMethod().getName());

        assertEquals("getNum", properties.get("num").getReadMethod().getName());
        assertEquals("setNum", properties.get("num").getWriteMethod().getName());

        // Choose the setter with the same type as the getter if there is one.
        assertEquals(Integer.TYPE, properties.get("num").getWriteMethod().getParameterTypes()[0]);

        // Ignore is methods that does not return boolean
        assertFalse(properties.containsKey("some"));
        // Ignore private methods
        assertFalse(properties.containsKey("private"));
        // Ignore when first letter after get is not capital.
        assertFalse(properties.containsKey("outofhere"));
        // Ignore getters with arguments
        assertFalse(properties.containsKey("withArgs"));
        // Ignore getters with void as return type
        assertFalse(properties.containsKey("void"));

        assertEquals("isThat", properties.get("that").getReadMethod().getName());
        assertNull(properties.get("that").getWriteMethod());

        // Properties with two leading capital letters should not be decapitalized.
        assertEquals("getURL", properties.get("URL").getReadMethod().getName());

    }


    @Test
    public void getPropertyDescriptorsByName_override() {
        BeanInfo beanInfo = new BeanInfoReflection(TestBean2.class);
        Map<String, PropertyDescriptor> properties = beanInfo.getPropertyDescriptorsByName();
        assertEquals("getArg", properties.get("arg").getReadMethod().getName());
        assertEquals("setArg", properties.get("arg").getWriteMethod().getName());

        assertEquals("getNum", properties.get("num").getReadMethod().getName());
        assertEquals("setNum", properties.get("num").getWriteMethod().getName());
    }
}