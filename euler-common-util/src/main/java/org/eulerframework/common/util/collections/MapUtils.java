package org.eulerframework.common.util.collections;

import java.util.Map;

/**
 * Utility methods for reading typed values from a {@code Map<String, Object>}
 * whose values may come from loosely-typed sources (YAML binding, JSON
 * deserialization, OIDC claim maps, etc.).
 *
 * <p>Each accessor performs null-safe type coercion following a consistent
 * contract: if the raw value is already the target type it is returned
 * directly; if it is a {@code String} it is parsed; otherwise an
 * {@link IllegalArgumentException} is thrown so misconfiguration surfaces
 * early.
 */
public class MapUtils {

    // ---- String ----

    /**
     * Return the value as a {@code String}, or {@code null} if absent or
     * empty. Non-string values are converted via {@link Object#toString()}.
     */
    public static String getString(Map<String, Object> map, String key) {
        if (map == null) {
            return null;
        }
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        String s = value.toString();
        return s.isEmpty() ? null : s;
    }

    /**
     * Return the value as a {@code String}, or {@code defaultValue} if
     * absent or empty.
     */
    public static String getString(Map<String, Object> map, String key, String defaultValue) {
        String value = getString(map, key);
        return value != null ? value : defaultValue;
    }

    // ---- Boolean ----

    /**
     * Return the value as a {@code Boolean}, or {@code null} if absent.
     * Accepts {@code Boolean}, {@code String} (parsed via
     * {@link Boolean#valueOf(String)}), and {@code Number} (positive =
     * {@code true}).
     */
    public static Boolean getBoolean(Map<String, Object> map, String key) {
        if (map == null) {
            return null;
        }
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean b) {
            return b;
        }
        if (value instanceof String s) {
            return Boolean.valueOf(s);
        }
        if (value instanceof Number n) {
            return n.intValue() > 0;
        }
        throw new IllegalArgumentException(
                "Cannot coerce value of type " + value.getClass().getName()
                        + " to Boolean for key '" + key + "'");
    }

    /**
     * Return the value as a primitive {@code boolean}, or
     * {@code defaultValue} if absent.
     */
    public static boolean getBoolean(Map<String, Object> map, String key, boolean defaultValue) {
        Boolean value = getBoolean(map, key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}
