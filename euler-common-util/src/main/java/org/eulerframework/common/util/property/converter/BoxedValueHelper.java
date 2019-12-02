package org.eulerframework.common.util.property.converter;

import org.eulerframework.common.util.Assert;

import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BoxedValueHelper {
    private static final Map<Class<?>, TypeConverter> TYPE_CONVERTERS = new HashMap<>();

    static {
        TYPE_CONVERTERS.put(String.class, new StringTypeConverter());
        TYPE_CONVERTERS.put(Locale.class, new LocaleTypeConverter());
        TYPE_CONVERTERS.put(Duration.class, new DurationTypeConverter());
    }

    public static <T> BoxedValue<T> box(Object value, Class<T> requiredType) {
        Assert.notNull(requiredType, "requiredType is required");

        if (value == null || requiredType.isAssignableFrom(value.getClass())) {
            return new OriginBoxedValue<>(value);
        }

        TypeConverter typeConverter = TYPE_CONVERTERS.get(requiredType);

        Assert.notNull(typeConverter, () -> "not support type: " + requiredType.getName());

        return typeConverter.convert(value, requiredType);
    }
}
