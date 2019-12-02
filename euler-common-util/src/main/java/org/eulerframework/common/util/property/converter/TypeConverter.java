package org.eulerframework.common.util.property.converter;

public interface TypeConverter<T> {
    BoxedValue<T> convert(Object value, Class<T> requiredType);
}
