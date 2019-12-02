package org.eulerframework.common.util.property.converter;

public class OriginBoxedValue<T> implements BoxedValue<T> {
    private final Object value;

    OriginBoxedValue(Object value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public String asText() {
        return this.value == null ? null : String.valueOf(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getConvertedValue() {
        return (T) this.value;
    }
}
