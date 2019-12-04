package org.eulerframework.common.util.type;

class IntegerTypeConverter implements TypeConverter<Integer> {
    @Override
    public Integer convert(Object value) {
        return Integer.parseInt(String.valueOf(value));
    }

    @Override
    public String asString(Integer value) {
        return value == null ? null : String.valueOf(value);
    }
}
