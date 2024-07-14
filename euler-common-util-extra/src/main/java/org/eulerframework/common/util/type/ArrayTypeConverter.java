/*
 * Copyright 2013-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eulerframework.common.util.type;

import org.eulerframework.common.util.StringUtils;

import java.lang.reflect.Array;
import java.util.Arrays;

class ArrayTypeConverter<T> implements TypeConverter<T[]> {
    private final TypeConverter<T> itemTypeConverter;
    private final Class<T> itemType;
    private final TypeConverter<String> stringTypeConverter = new StringTypeConverter();
    private final String split;

    public ArrayTypeConverter(TypeConverter<T> itemTypeConverter, Class<T> itemType) {
        this(itemTypeConverter, itemType, ",");
    }

    public ArrayTypeConverter(TypeConverter<T> itemTypeConverter, Class<T> itemType, String split) {
        this.itemTypeConverter = itemTypeConverter;
        this.itemType = itemType;
        this.split = split;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T[] convert(Object value) {
        Object[] rawArray;
        if(value.getClass().isArray()) {
            rawArray = (Object[]) value;
        } else {
            String stringValue = this.stringTypeConverter.convert(value);

            if(StringUtils.isEmpty(stringValue)) {
                return null;
            }

            String[] stringArray = stringValue.split(this.split);

            for(int i = 0; i < stringArray.length; i++) {
                stringArray[i] = stringArray[i].trim();
            }

            rawArray = stringArray;
        }

        if(this.itemType.equals(rawArray.getClass().getComponentType())) {
            return (T[]) rawArray;
        }

        Object array = Array.newInstance(this.itemType, rawArray.length);

        for(int i = 0; i < rawArray.length; i++) {
            Array.set(array, i, this.itemTypeConverter.convert(rawArray[i]));
        }

        return (T[]) array;
    }

    @Override
    public String asString(T[] value) {
        if(value == null) {
            return null;
        }

        String[] stringArray = Arrays.stream(value).map(this.itemTypeConverter::asString).toArray(String[]::new);
        return Arrays.toString(stringArray);
    }
}
