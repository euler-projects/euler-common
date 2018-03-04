package net.eulerframework.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class JavaObjectUtils {
    
    /**
     * 将对象中为<code>""</code>的属性置为<code>null</code>
     * @param obj 待处理的对象
     */
    public static void clearEmptyProperty(Object obj) {
        if (obj == null)
            return;
        try {
            for (Class<? extends Object> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
                Field[] fields = getBeanFields(clazz, true);
                for (Field field : fields)
                    if ((field.getType() == String.class) && (!(Modifier.isStatic(field.getModifiers())))) {
                        boolean accessible = field.isAccessible();
                        field.setAccessible(true);
                        String value = (String) field.get(obj);
                        if (value != null && "".equals(value.trim())) {
                            field.set(obj, null);
                        }
                        field.setAccessible(accessible);
                    }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取对象的属性
     * @param clazz 待获取的对象
     * @param includeParentClass 是否包含父类属性
     * @return 对象的属性数组
     */
    public static Field[] getBeanFields(Class<?> clazz, boolean includeParentClass) {
        Field[] fields = clazz.getDeclaredFields();
        if (includeParentClass) {
            Class<?> parentClazz = clazz.getSuperclass();
            if (parentClazz != Object.class) {
                Field[] parentClazzFields = getBeanFields(parentClazz, true);
                Field[] result = ArrayUtils.concatAll(fields, parentClazzFields);
                return result;
            }
        }
        return fields;
    }

    /**
     * 获取父类范型类型
     * @param clazz 当前类
     * @param index 第几个范型参数
     * @return 范型参数的<code>Class</code>
     */
    public static Class<?> findSuperClassGenricType(Class<?> clazz, int index){
        if(clazz == null)
            return null;
        
        Type type = clazz.getGenericSuperclass();
        
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;            
            Type[] argTypes = pType.getActualTypeArguments();
            
            if(index >= argTypes.length || index < 0){
                throw new IndexOutOfBoundsException("Genric args types find error，Index out of bounds: total="+argTypes.length+", index="+index);
            }
            
            Type argType = argTypes[index];
            
            if (argType instanceof Class) {
                return ((Class<?>) argType);
            }
            
            return null;
        }
    
        return findSuperClassGenricType(clazz.getSuperclass(), index);        
    }
    public static Class<?> findSuperInterfaceGenricType(Class<?> clazz, int interfaceIndex, int index){
        if(clazz == null)
            return null;
        
        Type type = clazz.getGenericInterfaces()[interfaceIndex];
        
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;            
            Type[] argTypes = pType.getActualTypeArguments();
            
            if(index >= argTypes.length || index < 0){
                throw new IndexOutOfBoundsException("Genric args types find error，Index out of bounds: total="+argTypes.length+", index="+index);
            }
            
            Type argType = argTypes[index];
            
            if (argType instanceof Class) {
                return ((Class<?>) argType);
            }
            
            return null;
        }
    
        return findSuperClassGenricType(clazz.getSuperclass(), index);        
    }
    
    /**
     * 将Map转换为对象, 目前只支持String类型的属性
     * @param map
     * @param objectClass
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <E> E readMapAsObject(Map<String, Object> map, Class<E> objectClass) throws InstantiationException, IllegalAccessException {
        E obj = objectClass.newInstance();
        Field[] fields = getBeanFields(objectClass, true);
        
        for (Field field : fields) {
            if(Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            
            Object value = map.get(field.getName());
            if(value != null) {
                setFieldValue(obj, field, analyzeValue(value, field.getType()));
            }
        }
        
        return obj;
    }
    
    private static Object analyzeValue(Object value, Class<?> clazz) {
        if(String.class.equals(clazz)) {
            return value;
        } else if(Integer.class.equals(clazz) || "int".equals(clazz.toString())) {
            return (Integer) value;
        } else if(Long.class.equals(clazz) || "long".equals(clazz.toString())) {
            return (Long) value;
        } else if(Short.class.equals(clazz) || "short".equals(clazz.toString())) {
            return (Short) value;
        } else if(Float.class.equals(clazz) || "float".equals(clazz.toString())) {
            return (Float) value;
        } else if(Double.class.equals(clazz) || "double".equals(clazz.toString())) {
            return (Double) value;
        } else if(Boolean.class.equals(clazz) || "boolean".equals(clazz.toString())) {
            return (Boolean) value;
        } else if(Date.class.equals(clazz)) {
            return new Date((long) value);
        } else if(BigDecimal.class.equals(clazz)) {
            return new BigDecimal((String) value);
        } else if(clazz.isEnum()) {
            return Enum.valueOf((Class<? extends Enum>) clazz, (String) value);
        }
        
        throw new IllegalArgumentException("Unsupport query property type: " + clazz);
    }
    
    public static Map<String, Object> writeObjectToMap(Object obj) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = getBeanFields(obj.getClass(), true);
        Map<String, Object> result = new HashMap<>();
        
        for (Field field : fields) {
            if(Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            
            result.put(field.getName(), getFieldValue(obj, field));
        }
        
        return result;
    }
    
    private static void setFieldValue(Object obj, Field field, Object value) throws IllegalArgumentException, IllegalAccessException {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        field.set(obj, value);
        field.setAccessible(accessible);
    }
    
    private static Object getFieldValue(Object obj, Field field) throws IllegalArgumentException, IllegalAccessException {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        Object value = field.get(obj);
        field.setAccessible(accessible);
        return value;
    }
}
