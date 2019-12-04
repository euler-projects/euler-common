package org.eulerframework.common.util.type;

class StringTypeConverter implements TypeConverter<String> {
    @Override
    public String convert(Object value) {
        return String.valueOf(value);
    }

    @Override
    public String asString(String value) {
        return value;
    }
}
