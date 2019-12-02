package org.eulerframework.common.util.property.converter;

public abstract class AbstractBoxedValue<T> implements BoxedValue<T> {
    private final Object value;
    private final T convertedValue;

    AbstractBoxedValue(Object value, T convertedValue) {
        this.value = value;
        this.convertedValue = convertedValue;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public T getConvertedValue() {
        return convertedValue;
    }
}
