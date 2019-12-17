package org.eulerframework.common.util.type;

public interface TypeConverter<T> {
    T convert(Object value);

    String asString(T value);
}
