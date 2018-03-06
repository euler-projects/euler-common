package net.eulerframework.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.eulerframework.common.util.io.file.SimpleFileIOUtils;

public abstract class JavaObjectUtils {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SimpleFileIOUtils.class);

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
     * 获取父类范型类型
     * 
     * @param clazz
     *            当前类
     * @param index
     *            第几个范型参数
     * @return 范型参数的<code>Class</code>
     */
    public static Class<?> findSuperClassGenricType(Class<?> clazz, int index) {
        if (clazz == null)
            return null;

        Type type = clazz.getGenericSuperclass();

        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Type[] argTypes = pType.getActualTypeArguments();

            if (index >= argTypes.length || index < 0) {
                throw new IndexOutOfBoundsException("Genric args types find error，Index out of bounds: total="
                        + argTypes.length + ", index=" + index);
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
     * 获取某个实现类所实现接口的范型类型
     * 
     * @param clazz
     *            实现类
     * @param interfaceIndex
     *            实现接口索引
     * @param index
     *            范型参数索引
     * @return 范型类型
     */
    public static Class<?> findSuperInterfaceGenricType(Class<?> clazz, int interfaceIndex, int index) {
        if (clazz == null)
            return null;

        Type type = clazz.getGenericInterfaces()[interfaceIndex];

        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Type[] argTypes = pType.getActualTypeArguments();

            if (index >= argTypes.length || index < 0) {
                throw new IndexOutOfBoundsException("Genric args types find error，Index out of bounds: total="
                        + argTypes.length + ", index=" + index);
            }

            Type argType = argTypes[index];

            if (argType instanceof Class) {
                return ((Class<?>) argType);
            }

            return null;
        }

        return findSuperClassGenricType(clazz.getSuperclass(), index);
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
                    if(LinkedHashMap.class.equals(value.getClass())) {
                        setFieldValue(obj, field, readMapAsObject((LinkedHashMap<String, Object>)value, field.getType()));
                    } else {
                        setFieldValue(obj, field, analyzeStringValueToObject(String.valueOf(value), field.getType()));
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException("Property '" + field.getName() + "' read error: " + e.getMessage(), e);
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

    public static Object analyzeStringValueToObject(String value, Class<?> clazz) {
        try {
            if (String.class.equals(clazz)) {
                return value;
            } else if (Integer.class.equals(clazz) || "int".equals(clazz.toString())) {
                return Integer.parseInt(value);
            } else if (Long.class.equals(clazz) || "long".equals(clazz.toString())) {
                return Long.parseLong(value);
            } else if (Short.class.equals(clazz) || "short".equals(clazz.toString())) {
                return Short.parseShort(value);
            } else if (Float.class.equals(clazz) || "float".equals(clazz.toString())) {
                return Float.parseFloat(value);
            } else if (Double.class.equals(clazz) || "double".equals(clazz.toString())) {
                return Double.parseDouble(value);
            } else if (Boolean.class.equals(clazz) || "boolean".equals(clazz.toString())) {
                return Boolean.parseBoolean(value);
            } else if (Character.class.equals(clazz) || "char".equals(clazz.toString())) {
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
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Number format error: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        
        throw new IllegalArgumentException("Unsupport query property type: " + clazz);
    }
}
