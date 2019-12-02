package org.eulerframework.common.util.property.converter;

public interface BoxedValue<T> {
    Object getValue();

    String asText();

    T getConvertedValue();
}
