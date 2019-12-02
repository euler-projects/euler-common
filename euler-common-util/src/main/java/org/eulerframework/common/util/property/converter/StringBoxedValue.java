package org.eulerframework.common.util.property.converter;

public class StringBoxedValue extends AbstractBoxedValue<String> {
    StringBoxedValue(Object value, String convertedValue) {
        super(value, convertedValue);
    }

    @Override
    public String asText() {
        return this.getConvertedValue();
    }
}
