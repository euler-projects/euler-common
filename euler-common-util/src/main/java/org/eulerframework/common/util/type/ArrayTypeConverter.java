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
        String stringValue = this.stringTypeConverter.convert(value);

        if(StringUtils.isEmpty(stringValue)) {
            return null;
        }

        String[] stringArray = stringValue.split(this.split);

        Object array = Array.newInstance(this.itemType, stringArray.length);

        for(int i = 0; i < stringArray.length; i++) {
            Array.set(array, i, this.itemTypeConverter.convert(stringArray[i].trim()));
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
