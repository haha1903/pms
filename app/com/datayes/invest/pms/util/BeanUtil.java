package com.datayes.invest.pms.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BeanUtil {
    
    private static final ConcurrentMap<Class, BeanInfo> beanInfoMap = new ConcurrentHashMap<>();

    private BeanUtil() {
        // private constructor
    }
    
    public static <T> T clone(T object) {
        if (object == null) {
            return null;
        }
        checkClass(object.getClass());
        if (isJavaBaseClass(object.getClass())) {
            return object;
        }
        if (shouldPackageSkipped(object.getClass())) {
            return object;
        }
        BeanInfo beanInfo = getBeanInfo(object.getClass());
        Object newObj;
        try {
            newObj = beanInfo.constructor.newInstance();
            for (String attr : beanInfo.getAttributes()) {
                Object value = beanInfo.getGetter(attr).invoke(object);
                Object valueCloned = clone(value);
                beanInfo.getSetter(attr).invoke(newObj, valueCloned);
            }
        } catch (Exception e) {
            throw new BeanException("Error cloning object of class: " + object.getClass().getName(), e);
        }
        return (T) newObj;
    }
    
    public static boolean equals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (!o1.getClass().equals(o2.getClass())) {
            return false;
        }
        checkClass(o1.getClass());
        if (isJavaBaseClass(o1.getClass())) {
            return o1.equals(o2);
        }
        if (shouldPackageSkipped(o1.getClass())) {
            return o1.equals(o2);
        }
        boolean result = true;
        BeanInfo beanInfo = getBeanInfo(o1.getClass());
        try {
            for (String attr : beanInfo.getAttributes()) {
                Object v1 = beanInfo.getGetter(attr).invoke(o1);
                Object v2 = beanInfo.getGetter(attr).invoke(o2);
                if (v1 == v2) {
                    continue;
                }
                if (v1 == null || v2 == null) {
                    result = false;
                    break;
                }
                if (! equals(v1, v2)) {
                    result = false;
                    break;
                }
            }
            return result;
        } catch (Exception e) {
            throw new BeanException("Error comparing objects of class: " + o1.getClass().getName(), e);
        }
    }

    public static int hashCode(Object object) {
        if (object == null) {
            throw new NullPointerException("Cannot invoke hasCode() on null object");
        }
        checkClass(object.getClass());
        if (isJavaBaseClass(object.getClass())) {
            return object.hashCode();
        }
        if (shouldPackageSkipped(object.getClass())) {
            return object.hashCode();
        }

        BeanInfo beanInfo = getBeanInfo(object.getClass());
        int hashCode = 157;
        for (String attr : beanInfo.getAttributes()) {
            try {
                Object value = beanInfo.getGetter(attr).invoke(object);
                hashCode += 23 * hashCode(value);
            } catch (Exception e) {
                throw new BeanException("Error invoking hashCode() on object of class: " + object.getClass(), e);
            }
        }

        return hashCode;
    }
    
    public static String toString(Object object) {
        if (object == null) {
            return "null";
        }
        checkClass(object.getClass());
        if (isJavaBaseClass(object.getClass())) {
            return object.toString();
        }
        if (shouldPackageSkipped(object.getClass())) {
            return object.toString();
        }
        
        StringBuilder builder = new StringBuilder();
        builder.append(object.getClass().getSimpleName());
        builder.append(" [");
        boolean isFirst = true;
        
        BeanInfo beanInfo = getBeanInfo(object.getClass());
        for (String attr : beanInfo.getAttributes()) {
            try {
                Object value = beanInfo.getGetter(attr).invoke(object);
                String s = toString(value);
                if (isFirst) {
                    isFirst = false;
                } else {
                    builder.append(", ");
                }
                builder.append(attr);
                builder.append(" = ");
                builder.append(s);
            } catch (Exception e) {
                throw new BeanException("Error invoking toString() on object of class: " + object.getClass(), e);
            }
        }
        
        builder.append("]");
        
        return builder.toString();
    }
    
    // TODO More classes may need to be added
    private static final Class[] JAVA_BASE_CLASSES = new Class[] {
        Boolean.class,
        Character.class,
        Double.class,
        Integer.class,
        Long.class,
        String.class,
        BigDecimal.class
    };

    private static boolean isJavaBaseClass(Class clazz) {
        if (clazz.isPrimitive()) {
            return true;
        }
        for (Class c : JAVA_BASE_CLASSES) {
            if (c.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    private static final String[] SKIP_PACKAGES = new String[] {
        "org.joda.time"
    };
    
    private static boolean shouldPackageSkipped(Class clazz) {
        String pkgName = clazz.getPackage().getName();
        for (String name : SKIP_PACKAGES) {
            if (pkgName.startsWith(name)) {
                return true;
            }
        }
        return false;
    }

    // TODO More classes may need to be added
    private static final Class[] NOT_SUPPORTED_CLASSES = new Class[] {
            List.class,
            Map.class,
            Set.class
    };

    private static void checkClass(Class clazz) {
        for (Class c : NOT_SUPPORTED_CLASSES) {
            if (c.isAssignableFrom(clazz)) {
                throw new BeanException("Unable to handle class " + clazz.getName());
            }
        }
    }

    private static BeanInfo getBeanInfo(Class clazz) {
        BeanInfo info = beanInfoMap.get(clazz);
        if (info == null) {
            BeanInfo newInfo = new BeanInfo(clazz);
            info = beanInfoMap.putIfAbsent(clazz, newInfo);
            if (info == null) {
                info = newInfo;
            }
        }
        return info;
    }

    /**
     * BeanInfo
     *
     * The class that contains the information about the bean, including the default constructor,
     * attributes (sorted alphabetically), the getters and setters.
     */
    private static class BeanInfo {
        
        private final Class clazz;
        private final Constructor constructor;
        private final List<String> attributes;
        private final Map<String, Method> getterMap = new HashMap<>();
        private final Map<String, Method> setterMap = new HashMap<>();

        BeanInfo(Class clazz) {
            this.clazz = clazz;
            this.constructor = getConstructor();
            findGetters();
            findSetters();
            attributes = findAttributes();
        }
        
        List<String> getAttributes() {
            return attributes;
        }
        
        Method getGetter(String attr) {
            return getterMap.get(attr);
        }
        
        Method getSetter(String attr) {
            return setterMap.get(attr);
        }
        
        private Constructor getConstructor() {
            try {
                Constructor c = clazz.getDeclaredConstructor();
                c.setAccessible(true);
                return c;
            } catch (NoSuchMethodException e) {
                throw new BeanException("No default constructor found for class " + clazz.getName());
            }
        }
        
        private void findGetters() {
            Class c = clazz;
            while (c != Object.class) {
                Method[] methods = c.getDeclaredMethods();
                for (Method m : methods) {
                    String attr = getBeanAttributeName(m.getName());
                    if (attr == null || getterMap.containsKey(attr)) {
                        // when not a bean attribute or subclass overrides the method
                        continue;
                    }
                    m.setAccessible(true);
                    getterMap.put(attr, m);
                }
                
                c = c.getSuperclass();
            }
        }
        
        private void findSetters() {
            for (String attr : getterMap.keySet()) {
                Method getter = getterMap.get(attr);
                Class retType = getter.getReturnType();
                
                Class c = clazz;
                while (c != null) {
                    String setterMethodName = "set" + capitalize(attr);
                    Method m = null;
                    try {
                        m = c.getDeclaredMethod(setterMethodName, retType);
                    } catch (NoSuchMethodException e) {
                        // ignore and try superclass
                    }
                    if (m != null) {
                        m.setAccessible(true);
                        setterMap.put(attr, m);
                        break;
                    }
                    
                    c = c.getSuperclass();
                }
                if (c == null) {
                    throw new BeanException("No setter method found on class " + clazz.getName() + " for attribute: " + attr);
                }
            }
        }
        
        private List<String> findAttributes() {
            List<String> list = new ArrayList<>();
            Set<String> set = getterMap.keySet();
            list.addAll(set);
            Collections.sort(list);
            return Collections.unmodifiableList(list);
        }
        
        /**
         * Gets the bean attribute name from method name.
         * Strip off "get" or "is" if method name starts with either of them, and return the
         * decapitalized substring as the attribute name.
         * 
         * @param methodName
         * 
         * @return the bean attribute name, otherwise null
         */
        private static String getBeanAttributeName(String methodName) {
            if (methodName == null) {
                return null;
            }
            int prefixLength;
            if (methodName.startsWith("get")) {
                prefixLength = 3;
            } else if (methodName.startsWith("is")) {
                prefixLength = 2;
            } else {
                return null;
            }
            String postfix = methodName.substring(prefixLength);
            if (!postfix.isEmpty() && Character.isUpperCase(postfix.charAt(0))) {
                return Character.toLowerCase(postfix.charAt(0)) + postfix.substring(1);
            }
            return null;
        }
        
        private static String capitalize(String name) {
            if (name == null || name.isEmpty()) {
                return name;
            }
            return Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }
    }
}
