package org.eulerframework.common.util.property.converter;

import java.util.Locale;

public class LocaleBoxedValue extends AbstractBoxedValue<Locale> {
    LocaleBoxedValue(Object value, Locale convertedValue) {
        super(value, convertedValue);
    }

    @Override
    public String asText() {
        return this.getConvertedValue().toString();
    }
}
