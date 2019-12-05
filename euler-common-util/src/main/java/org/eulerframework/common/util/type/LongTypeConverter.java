package org.eulerframework.common.util.type;

class LongTypeConverter implements TypeConverter<Long> {
    @Override
    public Long convert(Object value) {
        return Long.parseLong(String.valueOf(value));
    }

    @Override
    public String asString(Long value) {
        return value == null ? null : String.valueOf(value);
    }
}
