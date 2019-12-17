/*
 * Copyright 2013-2019 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.eulerframework.common.util.type;

import org.eulerframework.common.util.Assert;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TypeUtils {
    private final static Map<Class<?>, TypeConverter<?>> TYPE_CONVERTERS = new HashMap<>();

    static {
        StringTypeConverter stringTypeConverter = new StringTypeConverter();
        TYPE_CONVERTERS.put(String.class, stringTypeConverter);
        TYPE_CONVERTERS.put(String[].class, new ArrayTypeConverter<>(stringTypeConverter, String.class));

        LocaleTypeConverter localeTypeConverter = new LocaleTypeConverter();
        TYPE_CONVERTERS.put(Locale.class, localeTypeConverter);
        TYPE_CONVERTERS.put(Locale[].class, new ArrayTypeConverter<>(localeTypeConverter, Locale.class));

        DurationTypeConverter durationTypeConverter = new DurationTypeConverter();
        TYPE_CONVERTERS.put(Duration.class, durationTypeConverter);
        TYPE_CONVERTERS.put(Duration[].class, new ArrayTypeConverter<>(durationTypeConverter, Duration.class));

        IntegerTypeConverter integerTypeConverter = new IntegerTypeConverter();
        TYPE_CONVERTERS.put(Integer.class, integerTypeConverter);
        TYPE_CONVERTERS.put(Integer[].class, new ArrayTypeConverter<>(integerTypeConverter, Integer.class));

        LongTypeConverter longTypeConverter = new LongTypeConverter();
        TYPE_CONVERTERS.put(Long.class, longTypeConverter);
        TYPE_CONVERTERS.put(Long[].class, new ArrayTypeConverter<>(longTypeConverter, Long.class));
    }

    @SuppressWarnings("unchecked")
    public static <T> T convert(Object raw, Class<T> requireType) {
        Assert.notNull(requireType, "requireType is required");

        if(raw == null || requireType.isAssignableFrom(raw.getClass())) {
            return (T) raw;
        }

        TypeConverter<T> typeConverter = getTypeConverter(requireType);

        if(typeConverter == null) {
            if(requireType.isEnum()) {
                T[] enumConstants = requireType.getEnumConstants();
                String rawString = convert(raw, String.class);
                for(T enumConstant : enumConstants) {
                    if(enumConstant.toString().equalsIgnoreCase(rawString)) {
                        return enumConstant;
                    }
                }

                throw new IllegalArgumentException(raw + " can not convert to Enum " + requireType.getName() +  ", the valid values are " + Arrays.toString(enumConstants));
            } else {
                throw new IllegalArgumentException("No type converter for " + requireType.getName() + " was found.");
            }
        }

        return typeConverter.convert(raw);
    }

    public static int convertToInt(Object raw) {
        Assert.notNull(raw, "value is null");

        if(Number.class.isAssignableFrom(raw.getClass())) {
            return ((Number) raw).intValue();
        }

        return Integer.parseInt(convert(raw, String.class));
    }

    public static long convertToLong(Object raw) {
        Assert.notNull(raw, "value is null");

        if(Number.class.isAssignableFrom(raw.getClass())) {
            return ((Number) raw).longValue();
        }

        return Long.parseLong(convert(raw, String.class));
    }

    public static byte convertToByte(Object raw) {
        Assert.notNull(raw, "value is null");

        if(Number.class.isAssignableFrom(raw.getClass())) {
            return ((Number) raw).byteValue();
        }

        return Byte.parseByte(convert(raw, String.class));
    }

    public static short convertToShort(Object raw) {
        Assert.notNull(raw, "value is null");

        if(Number.class.isAssignableFrom(raw.getClass())) {
            return ((Number) raw).shortValue();
        }

        return Short.parseShort(convert(raw, String.class));
    }

    public static float convertToFloat(Object raw) {
        Assert.notNull(raw, "value is null");

        if(Number.class.isAssignableFrom(raw.getClass())) {
            return ((Number) raw).floatValue();
        }

        return Float.parseFloat(convert(raw, String.class));
    }

    public static double convertToDouble(Object raw) {
        Assert.notNull(raw, "value is null");

        if(Number.class.isAssignableFrom(raw.getClass())) {
            return ((Number) raw).doubleValue();
        }

        return Double.parseDouble(convert(raw, String.class));
    }

    public static boolean convertToBoolean(Object raw) {
        Assert.notNull(raw, "value is null");

        if(Boolean.class.isAssignableFrom(raw.getClass())) {
            return (Boolean) raw;
        }

        return Boolean.parseBoolean(convert(raw, String.class));
    }

    public static int[] convertToIntArray(Object raw) {
        Assert.notNull(raw, "value is null");

        if(int[].class.equals(raw.getClass())) {
            return (int[]) raw;
        }

        if(Number[].class.isAssignableFrom(raw.getClass())) {
            Number[] numbers = (Number[]) raw;
            int[] result = new int[numbers.length];
            for(int i = 0; i < numbers.length; i++) {
                result[i] = numbers[i].intValue();
            }
            return result;
        }

        Integer[] integerArray = convert(raw, Integer[].class);
        int[] result = new int[integerArray.length];
        for(int i = 0; i < integerArray.length; i++) {
            result[i] = integerArray[i];
        }
        return result;
    }

    public static long[] convertToLongArray(Object raw) {
        Assert.notNull(raw, "value is null");

        if(long[].class.equals(raw.getClass())) {
            return (long[]) raw;
        }

        if(Number[].class.isAssignableFrom(raw.getClass())) {
            Number[] numbers = (Number[]) raw;
            long[] result = new long[numbers.length];
            for(int i = 0; i < numbers.length; i++) {
                result[i] = numbers[i].longValue();
            }
            return result;
        }

        Long[] objArray = convert(raw, Long[].class);
        long[] result = new long[objArray.length];
        for(int i = 0; i < objArray.length; i++) {
            result[i] = objArray[i];
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> String asString(T value) {
        if(value == null) {
            return null;
        }

        TypeConverter<T> typeConverter = (TypeConverter<T>) getTypeConverter(value.getClass());
        return typeConverter == null ?  String.valueOf(value) : typeConverter.asString(value);
    }

    @SuppressWarnings("unchecked")
    private static <T> TypeConverter<T> getTypeConverter(Class<T> requireType) {
        return (TypeConverter<T>) TYPE_CONVERTERS.get(requireType);
    }

    public static boolean containsTypeConverter(Class<?> clazz) {
        return TYPE_CONVERTERS.containsKey(clazz);
    }

    public static void addTypeConverter(Class<?> clazz, TypeConverter<?> typeConverter) {
        TYPE_CONVERTERS.put(clazz, typeConverter);
    }
}
