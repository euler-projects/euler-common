package org.eulerframework.common.util.type;

import java.time.Duration;
import java.util.Date;

class DurationTypeConverter implements TypeConverter<Duration> {
    @Override
    public Duration convert(Object value) {
        return this.stringToDuration(String.valueOf(value));
    }

    private Duration stringToDuration(String string) {
        DurationStyle style = DurationStyle.detect(string);
        return style.parse(string, null);
    }

    @Override
    public String asString(Duration value) {
        return value == null ? null : value.toString();
    }
}
