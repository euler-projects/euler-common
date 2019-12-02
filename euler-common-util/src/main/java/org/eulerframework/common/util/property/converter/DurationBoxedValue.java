package org.eulerframework.common.util.property.converter;

import java.time.Duration;

public class DurationBoxedValue extends AbstractBoxedValue<Duration> {
    DurationBoxedValue(Object value, Duration convertedValue) {
        super(value, convertedValue);
    }

    @Override
    public String asText() {
        return this.getConvertedValue().toString();
    }
}
