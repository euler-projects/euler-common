package org.eulerframework.common.util;

public class NumberUtils {
    @SuppressWarnings("unchecked")
    public static <T> T toUnsignedValue(Object value, Class<T> clazz) {
        if (clazz == Integer.class || clazz == int.class) {
            return (T) Integer.valueOf(toUnsignedInt(value));
        }
        if (clazz == Boolean.class || clazz == boolean.class) {
            return (T) Boolean.valueOf(toBoolean(value));
        }
        if (clazz == Long.class || clazz == long.class) {
            return (T) Long.valueOf(toUnsignedLong(value));
        }
        if (clazz == Byte.class || clazz == byte.class) {
            return (T) Byte.valueOf(toByte(value));
        }
        if (clazz == Short.class || clazz == short.class) {
            return (T) Short.valueOf(toUnsignedShort(value));
        }
        throw new IllegalArgumentException("Unsupported type: " + clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T toUnsignedValue(byte b, Class<T> clazz) {
        if (clazz == Integer.class || clazz == int.class) {
            return (T) Integer.valueOf(toUnsignedInt(b));
        }
        if (clazz == Boolean.class || clazz == boolean.class) {
            return (T) Boolean.valueOf(toBoolean(b));
        }
        if (clazz == Long.class || clazz == long.class) {
            return (T) Long.valueOf(toUnsignedLong(b));
        }
        if (clazz == Byte.class || clazz == byte.class) {
            return (T) Byte.valueOf(toByte(b));
        }
        if (clazz == Short.class || clazz == short.class) {
            return (T) Short.valueOf(toUnsignedShort(b));
        }
        throw new IllegalArgumentException("Unsupported type: " + clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T toUnsignedValue(short s, Class<T> clazz) {
        if (clazz == Integer.class || clazz == int.class) {
            return (T) Integer.valueOf(toUnsignedInt(s));
        }
        if (clazz == Boolean.class || clazz == boolean.class) {
            return (T) Boolean.valueOf(toBoolean(s));
        }
        if (clazz == Long.class || clazz == long.class) {
            return (T) Long.valueOf(toUnsignedLong(s));
        }
        if (clazz == Byte.class || clazz == byte.class) {
            return (T) Byte.valueOf(toByte(s));
        }
        if (clazz == Short.class || clazz == short.class) {
            return (T) Short.valueOf(toUnsignedShort(s));
        }
        throw new IllegalArgumentException("Unsupported type: " + clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T toUnsignedValue(int i, Class<T> clazz) {
        if (clazz == Integer.class || clazz == int.class) {
            return (T) Integer.valueOf(toUnsignedInt(i));
        }
        if (clazz == Boolean.class || clazz == boolean.class) {
            return (T) Boolean.valueOf(toBoolean(i));
        }
        if (clazz == Long.class || clazz == long.class) {
            return (T) Long.valueOf(toUnsignedLong(i));
        }
        if (clazz == Byte.class || clazz == byte.class) {
            return (T) Byte.valueOf(toByte(i));
        }
        if (clazz == Short.class || clazz == short.class) {
            return (T) Short.valueOf(toUnsignedShort(i));
        }
        throw new IllegalArgumentException("Unsupported type: " + clazz);
    }

    public static boolean toBoolean(Object v) {
        if (v == null) {
            return false;
        }

        if (v instanceof Number) {
            if (v instanceof Byte) {
                return toBoolean(((Byte) v).byteValue());
            }
            if (v instanceof Short) {
                return toBoolean(((Short) v).shortValue());
            }
            if (v instanceof Integer) {
                return toBoolean(((Integer) v).intValue());
            }
            if (v instanceof Long) {
                return toBoolean(((Long) v).longValue());
            }
        }

        if (v instanceof Boolean) {
            return toBoolean(((Boolean) v).booleanValue());
        }

        if (v instanceof Character) {
            return toBoolean(((Character) v).charValue());
        }

        throw new IllegalArgumentException("Unsupported type: " + v.getClass());
    }

    public static boolean toBoolean(boolean b) {
        return b;
    }

    public static boolean toBoolean(char c) {
        return toBoolean((short) c);
    }

    public static boolean toBoolean(byte b) {
        return b != 0;
    }

    public static boolean toBoolean(short s) {
        return s != 0;
    }

    public static boolean toBoolean(int i) {
        return i != 0;
    }

    public static boolean toBoolean(long l) {
        return l != 0;
    }

    public static byte toByte(Object v) {
        if (v == null) {
            return 0;
        }

        if (v instanceof Number) {
            if (v instanceof Byte) {
                return toByte(((Byte) v).byteValue());
            }
            if (v instanceof Short) {
                return toByte(((Short) v).shortValue());
            }
            if (v instanceof Integer) {
                return toByte(((Integer) v).intValue());
            }
            if (v instanceof Long) {
                return toByte(((Long) v).longValue());
            }
        }

        if (v instanceof Boolean) {
            return toByte(((Boolean) v).booleanValue());
        }

        if (v instanceof Character) {
            return toByte(((Character) v).charValue());
        }

        throw new IllegalArgumentException("Unsupported type: " + v.getClass());
    }

    public static byte toByte(boolean b) {
        return toByte(b ? 1 : 0);
    }

    public static byte toByte(char c) {
        return toByte((short) c);
    }

    public static byte toByte(byte b) {
        return b;
    }

    public static byte toByte(short s) {
        return (byte) s;
    }

    public static byte toByte(int i) {
        return (byte) i;
    }

    public static byte toByte(long l) {
        return (byte) l;
    }

    public static short toUnsignedShort(Object v) {
        if (v == null) {
            return 0;
        }

        if (v instanceof Number) {
            if (v instanceof Byte) {
                return toUnsignedShort(((Byte) v).byteValue());
            }
            if (v instanceof Short) {
                return toUnsignedShort(((Short) v).shortValue());
            }
            if (v instanceof Integer) {
                return toUnsignedShort(((Integer) v).intValue());
            }
            if (v instanceof Long) {
                return toUnsignedShort(((Long) v).longValue());
            }
        }

        if (v instanceof Boolean) {
            return toUnsignedShort(((Boolean) v).booleanValue());
        }

        if (v instanceof Character) {
            return toUnsignedShort(((Character) v).charValue());
        }

        throw new IllegalArgumentException("Unsupported type: " + v.getClass());
    }

    public static short toUnsignedShort(boolean b) {
        return (short) (b ? 1 : 0);
    }

    public static short toUnsignedShort(char c) {
        return (short) c;
    }

    public static short toUnsignedShort(byte b) {
        return (short) (b & 0xFF);
    }

    public static short toUnsignedShort(short s) {
        return s;
    }

    public static short toUnsignedShort(int i) {
        return (short) i;
    }

    public static short toUnsignedShort(long l) {
        return (short) l;
    }

    public static int toUnsignedInt(Object v) {
        if (v == null) {
            return 0;
        }

        if (v instanceof Number) {
            if (v instanceof Byte) {
                return toUnsignedInt(((Byte) v).byteValue());
            }
            if (v instanceof Short) {
                return toUnsignedInt(((Short) v).shortValue());
            }
            if (v instanceof Integer) {
                return toUnsignedInt(((Integer) v).intValue());
            }
            if (v instanceof Long) {
                return toUnsignedInt(((Long) v).longValue());
            }
        }

        if (v instanceof Boolean) {
            return toUnsignedInt(((Boolean) v).booleanValue());
        }

        if (v instanceof Character) {
            return toUnsignedInt(((Character) v).charValue());
        }

        throw new IllegalArgumentException("Unsupported type: " + v.getClass());
    }

    public static int toUnsignedInt(boolean b) {
        return b ? 1 : 0;
    }

    public static int toUnsignedInt(char c) {
        return toUnsignedInt((short) c);
    }

    public static int toUnsignedInt(byte b) {
        return b & 0xFF;
    }

    public static int toUnsignedInt(short s) {
        return s & 0xFFFF;
    }

    public static int toUnsignedInt(int i) {
        return i;
    }

    public static int toUnsignedInt(long l) {
        return (int) l;
    }

    public static long toUnsignedLong(Object v) {
        if (v == null) {
            return 0;
        }

        if (v instanceof Number) {
            if (v instanceof Byte) {
                return toUnsignedLong(((Byte) v).byteValue());
            }
            if (v instanceof Short) {
                return toUnsignedLong(((Short) v).shortValue());
            }
            if (v instanceof Integer) {
                return toUnsignedLong(((Integer) v).intValue());
            }
            if (v instanceof Long) {
                return toUnsignedLong(((Long) v).longValue());
            }
        }

        if (v instanceof Boolean) {
            return toUnsignedLong(((Boolean) v).booleanValue());
        }

        if (v instanceof Character) {
            return toUnsignedLong(((Character) v).charValue());
        }

        throw new IllegalArgumentException("Unsupported type: " + v.getClass());
    }

    public static long toUnsignedLong(boolean b) {
        return b ? 1L : 0L;
    }

    public static long toUnsignedLong(char c) {
        return toUnsignedLong((short) c);
    }

    public static long toUnsignedLong(byte b) {
        return b & 0xFFL;
    }

    public static long toUnsignedLong(short s) {
        return s & 0xFFFFL;
    }

    public static long toUnsignedLong(int i) {
        return i & 0xFFFFFFFFL;
    }

    public static long toUnsignedLong(long l) {
        return l;
    }
}
