package org.eulerframework.common.util.collections;

import java.util.Map;

public class MapUtils {
    public static boolean getBoolean(Map<String, Object> options, String key, boolean defaultValue) {
        Object value = options.get(key);

        if (value == null) {
            return defaultValue;
        }

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }

        if (value instanceof Number) {
            return ((Number) value).intValue() > 0;
        }

        throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
    }
}
