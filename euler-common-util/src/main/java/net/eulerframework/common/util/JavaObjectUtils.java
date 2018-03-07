package net.eulerframework.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JavaObjectUtils {
    protected static final Logger LOGGER = LoggerFactory.getLogger(JavaObjectUtils.class);

    /**
     * 获取对象的属性
     * 
     * @param clazz
     *            待获取的对象
     * @param includeParentClass
     *            是否包含父类属性
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
     * 将对象中为<code>""</code>的属性置为<code>null</code>
     * 
     * @param obj
     *            待处理的对象
     */
    public static void clearEmptyProperty(Object obj) {
        if (obj == null)
            return;
        try {
            for (Class<? extends Object> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
                Field[] fields = getBeanFields(clazz, true);
                for (Field field : fields)
                    if ((field.getType() == String.class) && (!(Modifier.isStatic(field.getModifiers())))) {
                        String value = (String) getFieldValue(obj, field);
                        if(!StringUtils.hasText(value)) {
                            setFieldValue(obj, field, null);
                        }
                    }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取父类范型类型
     * 
     * @param clazz
     *            当前类
     * @param typeArgIndex
     *            第几个范型参数
     * @return 范型参数的<code>Class</code>
     */
    public static Class<?> findSuperClassGenricType(Class<?> clazz, int typeArgIndex) {
        if (clazz == null)
            return null;

        Type type = clazz.getGenericSuperclass();

        if (type instanceof ParameterizedType) {
            return getActualTypeArgumentsOfParameterizedType((ParameterizedType) type, typeArgIndex);
        }

        return findSuperClassGenricType(clazz.getSuperclass(), typeArgIndex);
    }

    /**
     * 获取某个实现类所实现接口的范型类型
     * 
     * @param clazz
     *            实现类
     * @param interfaceIndex
     *            实现接口索引
     * @param typeArgIndex
     *            范型参数索引
     * @return 范型类型
     */
    public static Class<?> findSuperInterfaceGenricType(Class<?> clazz, int interfaceIndex, int typeArgIndex) {
        if (clazz == null)
            return null;

        Type type = clazz.getGenericInterfaces()[interfaceIndex];

        if (type instanceof ParameterizedType) {
            return getActualTypeArgumentsOfParameterizedType((ParameterizedType) type, typeArgIndex);
        }

        return null;
    }
    
    private static Class<?> getActualTypeArgumentsOfParameterizedType(ParameterizedType pType, int index) {
        Type argType = pType.getActualTypeArguments()[index];

        if (argType instanceof Class) {
            return ((Class<?>) argType);
        }

        return null;
        
    }

    public static void setFieldValue(Object obj, Field field, Object value)
            throws IllegalArgumentException, IllegalAccessException {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        field.set(obj, value);
        field.setAccessible(accessible);
    }

    public static Object getFieldValue(Object obj, Field field)
            throws IllegalArgumentException, IllegalAccessException {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        Object value = field.get(obj);
        field.setAccessible(accessible);
        return value;
    }

    /**
     * 将Map转换为对象
     * 
     * @param map
     * @param objectClass
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <E> E readMapAsObject(Map<String, Object> map, Class<E> objectClass)
            throws InstantiationException, IllegalAccessException {
        E obj = objectClass.newInstance();
        Field[] fields = getBeanFields(objectClass, true);

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            Object value = map.get(field.getName());
            if (value != null) {
                try {
                    if (field.getType().equals(value.getClass())) {
                        setFieldValue(obj, field, value);
                    } else if (LinkedHashMap.class.equals(value.getClass())) {
                        setFieldValue(obj, field,
                                readMapAsObject((LinkedHashMap<String, Object>) value, field.getType()));
                    } else if (isSafeToString(value.getClass())) {
                        setFieldValue(obj, field, analyzeStringValueToObject(String.valueOf(value), field.getType()));
                    } else {
                        throw new IllegalArgumentException("Unsupport raw type: " + value.getClass().getName());
                    }
                } catch (IllegalArgumentException e) {
                    throw new RawMapReaAsObjectException("Property '" + field.getName() + "' which type is '"
                            + field.getType().getName() + "' read error: " + e.getMessage(), e);
                }
            }
        }

        return obj;
    }

    public static Map<String, Object> writeObjectToMap(Object obj)
            throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = getBeanFields(obj.getClass(), true);
        Map<String, Object> result = new HashMap<>();

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            result.put(field.getName(), getFieldValue(obj, field));
        }

        return result;
    }

    /**
     * 判断一个类是否可以安全的调用{@link String#valueOf(Object)}方法,
     * 这里的安全调用指在转为String后可以通过{@link JavaObjectUtils#analyzeStringValueToObject(String, Class)}转换回原始类型
     * 
     * @param clazz
     *            被检查的类型
     * @return 可以安全转换返回{@code true}, 反之返回{@code false}
     */
    public static boolean isSafeToString(Class<?> clazz) {
        Set<Class<?>> supportClasses = new HashSet<Class<?>>() {
            {
                add(String.class);
                add(Integer.class);
                add(Long.class);
                add(Short.class);
                add(Float.class);
                add(Double.class);
                add(Boolean.class);
                add(Character.class);
                add(Date.class);
                add(BigDecimal.class);
                add(int.class);
                add(long.class);
                add(short.class);
                add(float.class);
                add(double.class);
                add(boolean.class);
                add(char.class);
            }
        };

        return supportClasses.contains(clazz) || clazz.isEnum();
    }

    public static Object analyzeStringValueToObject(String value, Class<?> clazz) {
        if(value == null)
            return null;
        
        if (String.class.equals(clazz)) {
            return value;
        } else if (Integer.class.equals(clazz) || int.class.equals(clazz)) {
            return Integer.parseInt(value);
        } else if (Long.class.equals(clazz) || long.class.equals(clazz)) {
            return Long.parseLong(value);
        } else if (Short.class.equals(clazz) || short.class.equals(clazz)) {
            return Short.parseShort(value);
        } else if (Float.class.equals(clazz) || float.class.equals(clazz)) {
            return Float.parseFloat(value);
        } else if (Double.class.equals(clazz) || double.class.equals(clazz)) {
            return Double.parseDouble(value);
        } else if (Boolean.class.equals(clazz) || boolean.class.equals(clazz)) {
            return Boolean.parseBoolean(value);
        } else if (Character.class.equals(clazz) || char.class.equals(clazz)) {
            if (value.length() > 0)
                LOGGER.warn("Query property type is Character, only use the first char of value");

            return value.toCharArray()[0];
        } else if (Date.class.equals(clazz)) {
            Date ret = null;
            try {
                ret = new Date(Long.parseLong(value));
            } catch (NumberFormatException e) {
                try {
                    ret = DateUtils.parseDate(value, "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                } catch (ParseException e1) {
                    throw new IllegalArgumentException("Date property value '" + value
                            + "' format doesn't match timesamp(3) or \"yyyy-MM-dd'T'HH:mm:ss.SSSZ\"");

                }
            }
            return ret;
        } else if (BigDecimal.class.equals(clazz)) {
            return new BigDecimal(value);
        } else if (clazz.isEnum()) {
            return Enum.valueOf((Class<? extends Enum>) clazz, value);
        }

        throw new IllegalArgumentException("Unsupport type: " + clazz);
    }
}
