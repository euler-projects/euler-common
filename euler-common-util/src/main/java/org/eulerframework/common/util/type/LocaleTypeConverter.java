package org.eulerframework.common.util.type;

import org.eulerframework.common.util.CommonUtils;

import java.util.Locale;

class LocaleTypeConverter implements TypeConverter<Locale> {
    @Override
    public Locale convert(Object value) {
        return CommonUtils.parseLocale(String.valueOf(value));
    }

    @Override
    public String asString(Locale value) {
        return value == null ? null : value.toString();
    }
}
