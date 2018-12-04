/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eulerframework.common.util;

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
     * 通过反射为属性赋值, 无论该属性是否是public的
     * @param obj 目标实例
     * @param field 目标属性
     * @param value 要赋的值
     * @throws IllegalArgumentException 反射相关错误
     * @throws IllegalAccessException 反射相关错误
     */
    public static void setFieldValue(Object obj, Field field, Object value)
            throws IllegalArgumentException, IllegalAccessException {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        field.set(obj, value);
        field.setAccessible(accessible);
    }

    /**
     * 通过反射取属性的值, 无论该属性是否是public的
     * @param obj 目标实例
     * @param field 目标属性
     * @return 取出的值
     * @throws IllegalArgumentException 反射相关错误
     * @throws IllegalAccessException 反射相关错误
     */
    public static Object getFieldValue(Object obj, Field field)
            throws IllegalArgumentException, IllegalAccessException {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        Object value = field.get(obj);
        field.setAccessible(accessible);
        return value;
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

    /**
     * 将一个Raw Map转换为指定对象
     * 
     * @param rawMap 格式未定的Map对象
     * @param objectClass 目标类型
     * @return 转换结果
     * @throws InstantiationException 反射相关错误
     * @throws IllegalAccessException 反射相关错误
     * @throws RawMapReadException 当传入了格式不正确的value时，会抛出此异常
     */
    public static <E> E readMapAsObject(Map<String, Object> rawMap, Class<E> objectClass)
            throws InstantiationException, IllegalAccessException {
        E obj = objectClass.newInstance();
        Field[] fields = getBeanFields(objectClass, true);

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            Object value = rawMap.get(field.getName());
            if (value != null) {
                try {
                    if (field.getType().equals(value.getClass())) {
                        setFieldValue(obj, field, value);
                    } else if (LinkedHashMap.class.equals(value.getClass())) {
                        @SuppressWarnings("unchecked")
                        LinkedHashMap<String, Object> childRawMap = (LinkedHashMap<String, Object>) value;
                        setFieldValue(obj, field, readMapAsObject(childRawMap, field.getType()));
                    } else if (isSafeToString(value.getClass())) {
                        setFieldValue(obj, field, analyzeStringValueToObject(String.valueOf(value), field.getType()));
                    } else {
                        throw new IllegalArgumentException("Unsupport raw type: " + value.getClass().getName());
                    }
                } catch (IllegalArgumentException e) {
                    throw new RawMapReadException("Property '" + field.getName() + "' which type is '"
                            + field.getType().getName() + "' read error: " + e.getMessage(), e);
                }
            }
        }

        return obj;
    }

    /**
     * 将一个对象转换为Map, 只对第一层做转换， 如果对象含有非基本类型对象属性，则会把此属性直接放入map的value中
     * @param obj 待转换对象
     * @return 生成的Map
     * @throws IllegalArgumentException 反射相关错误
     * @throws IllegalAccessException 反射相关错误
     */
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

    /**
     * 将一个字符串解析为指定的类型
     * @param str 待解析字符串
     * @param clazz 指定类型
     * @return 解析生成的对象实例
     */
    public static Object analyzeStringValueToObject(String str, Class<?> clazz) {
        if(str == null)
            return null;
        
        if (String.class.equals(clazz)) {
            return str;
        } else if (Integer.class.equals(clazz) || int.class.equals(clazz)) {
            return Integer.parseInt(str);
        } else if (Long.class.equals(clazz) || long.class.equals(clazz)) {
            return Long.parseLong(str);
        } else if (Short.class.equals(clazz) || short.class.equals(clazz)) {
            return Short.parseShort(str);
        } else if (Float.class.equals(clazz) || float.class.equals(clazz)) {
            return Float.parseFloat(str);
        } else if (Double.class.equals(clazz) || double.class.equals(clazz)) {
            return Double.parseDouble(str);
        } else if (Boolean.class.equals(clazz) || boolean.class.equals(clazz)) {
            if(!("true".equalsIgnoreCase(str) || "false".equalsIgnoreCase(str))) {
                throw new IllegalArgumentException("invalid boolean string: " + str);
            }
            return Boolean.parseBoolean(str);
        } else if (Character.class.equals(clazz) || char.class.equals(clazz)) {
            if (str.length() > 0)
                LOGGER.warn("Object type is Character, only use the first char of the string");

            return str.toCharArray()[0];
        } else if (Date.class.equals(clazz)) {
            Date ret = null;
            try {
                ret = new Date(Long.parseLong(str));
            } catch (NumberFormatException e) {
                try {
                    ret = DateUtils.parseDate(str, "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                } catch (ParseException e1) {
                    throw new IllegalArgumentException("Date property value '" + str
                            + "' format doesn't match timesamp(3) or \"yyyy-MM-dd'T'HH:mm:ss.SSSZ\"");

                }
            }
            return ret;
        } else if (BigDecimal.class.equals(clazz)) {
            return new BigDecimal(str);
        } else if (clazz.isEnum()) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Object ret = Enum.valueOf((Class<? extends Enum>) clazz, str);
            return ret;
        }

        throw new IllegalArgumentException("Unsupport type: " + clazz);
    }
}
