package org.eulerframework.common.util.property.converter;

public class StringTypeConverter implements TypeConverter<String> {
    @Override
    public BoxedValue<String> convert(Object value, Class<String> requiredType) {
        return new StringBoxedValue(value, String.valueOf(value));
    }
}
