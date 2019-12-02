package org.eulerframework.common.util.property.converter;

import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

public class TypeConverterTest {
    @Test
    public void convertLocale() {
        LocaleTypeConverter localeTypeConverter = new LocaleTypeConverter();
        BoxedValue<Locale> locale = localeTypeConverter.convert("zh_CN", Locale.class);
        Assert.assertEquals(Locale.CHINA, locale.getConvertedValue());
    }
}
