package org.eulerframework.common.util.property.converter;

import org.eulerframework.common.util.CommonUtils;

import java.util.Locale;

public class LocaleTypeConverter implements TypeConverter<Locale> {
    @Override
    public BoxedValue<Locale> convert(Object value, Class<Locale> requiredType) {
        return new LocaleBoxedValue(value, CommonUtils.parseLocale(String.valueOf(value)));
    }
}
