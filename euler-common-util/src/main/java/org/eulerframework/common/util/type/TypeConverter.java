package org.eulerframework.common.util.type;

interface TypeConverter<T> {
    T convert(Object value);

    String asString(T value);
}
