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
        TYPE_CONVERTERS.put(int.class, integerTypeConverter);
        TYPE_CONVERTERS.put(int[].class, new ArrayTypeConverter<>(integerTypeConverter, int.class));
    }

    @SuppressWarnings("unchecked")
    public static <T> T convert(Object raw, Class<T> requireType) {
        Assert.notNull(requireType, "requireType is required");

        if(raw == null || requireType.isAssignableFrom(raw.getClass())) {
            return (T) raw;
        }

        if(requireType.isEnum()) {
            T[] enumConstants = requireType.getEnumConstants();
            String rawString = String.valueOf(raw);
            for(T enumConstant : enumConstants) {
                if(enumConstant.toString().equalsIgnoreCase(rawString)) {
                    return enumConstant;
                }
            }

            throw new IllegalArgumentException(raw + " can not convert to Enum " + requireType.getName() +  ", the valid values are " + Arrays.toString(enumConstants));
        }

        TypeConverter<T> typeConverter = getTypeConverter(requireType);

        Assert.notNull(typeConverter, () -> "No type converter for " + requireType.getName() + "was found.");

        return typeConverter.convert(raw);
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
}
