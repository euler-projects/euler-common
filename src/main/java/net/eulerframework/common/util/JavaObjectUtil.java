package net.eulerframework.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class JavaObjectUtil {
    
    /**
     * 将对象中为<code>""</code>的属性置为<code>null</code>
     * @param obj 待处理的对象
     */
    public static void clearEmptyProperty(Object obj) {
        if (obj == null)
            return;
        try {
            for (Class<? extends Object> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
                Field[] fields = clazz.getDeclaredFields();
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
                Field[] result = ArrayUtil.concatAll(fields, parentClazzFields);
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
}
