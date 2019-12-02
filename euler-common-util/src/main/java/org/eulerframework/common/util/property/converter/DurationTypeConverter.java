package org.eulerframework.common.util.property.converter;

import java.time.Duration;

public class DurationTypeConverter implements TypeConverter<Duration> {
    @Override
    public BoxedValue<Duration> convert(Object value, Class<Duration> requiredType) {
        String stringValue = String.valueOf(value);
        DurationStyle style = DurationStyle.detect(stringValue);
        Duration duration = style.parse(stringValue, null);
        return new DurationBoxedValue(value, duration);
    }
}
