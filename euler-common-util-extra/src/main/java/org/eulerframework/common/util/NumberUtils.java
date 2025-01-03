/*
 * Copyright 2013-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eulerframework.common.util;

import org.apache.commons.lang3.ArrayUtils;

public class NumberUtils {
    @SuppressWarnings("unchecked")
    public static <T> T toUnsignedValue(Object value, Class<T> clazz) {
        if (clazz == Integer.class || clazz == int.class) {
            return (T) Integer.valueOf(toUnsignedInt(value));
        }
        if (clazz == Boolean.class || clazz == boolean.class) {
            return (T) Boolean.valueOf(toBoolean(value));
        }
        if (clazz == Byte.class || clazz == byte.class) {
            return (T) Byte.valueOf(toByte(value));
        }
        if (clazz == Long.class || clazz == long.class) {
            return (T) Long.valueOf(toUnsignedLong(value));
        }
        if (clazz == Short.class || clazz == short.class) {
            return (T) Short.valueOf(toUnsignedShort(value));
        }
        if (clazz == Character.class || clazz == char.class) {
            return (T) Character.valueOf((char) toUnsignedShort(value));
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

    public static byte toByte(Byte[] bytes) {
        return toByte(ArrayUtils.toPrimitive(bytes));
    }

    public static byte toByte(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException();
        }

        if (bytes.length != 1) {
            throw new IllegalArgumentException("data must be 1 bytes in length");
        }

        return bytes[0];
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

        if (v instanceof byte[]) {
            return toUnsignedShort((byte[]) v);
        }

        if (v instanceof Byte[]) {
            return toUnsignedShort((Byte[]) v);
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

    public static short toUnsignedShort(Byte[] bytes) {
        return toUnsignedShort(ArrayUtils.toPrimitive(bytes));
    }

    public static short toUnsignedShort(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException();
        }

        if (bytes.length != 2) {
            throw new IllegalArgumentException("data must be 2 bytes in length");
        }

        int result = 0;
        for (byte b : bytes) {
            result = (result << 8) | (b & 0xFF);
        }
        return (short) result;
    }

    public static short toUnsignedShort(byte hi, byte lo) {
        return (short) (((hi & 0xFF) << 8) | lo & 0xFF);
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

        if (v instanceof byte[]) {
            return toUnsignedInt((byte[]) v);
        }

        if (v instanceof Byte[]) {
            return toUnsignedInt((Byte[]) v);
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

    public static int toUnsignedInt(Byte[] bytes) {
        return toUnsignedInt(ArrayUtils.toPrimitive(bytes));
    }

    public static int toUnsignedInt(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException();
        }

        if (bytes.length != 4) {
            throw new IllegalArgumentException("data must be 4 bytes in length");
        }

        int result = 0;
        for (byte b : bytes) {
            result = (result << 8) | (b & 0xFF);
        }
        return result;
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

        if (v instanceof byte[]) {
            return toUnsignedLong((byte[]) v);
        }

        if (v instanceof Byte[]) {
            return toUnsignedLong((Byte[]) v);
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

    public static long toUnsignedLong(Byte[] bytes) {
        return toUnsignedLong(ArrayUtils.toPrimitive(bytes));
    }

    public static long toUnsignedLong(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException();
        }

        if (bytes.length != 8) {
            throw new IllegalArgumentException("data must be 8 bytes in length");
        }

        long result = 0;
        for (byte b : bytes) {
            result = (result << 8) | (b & 0xFF);
        }
        return result;
    }

    public static byte[] toByteArray(Object v) {
        if (v == null) {
            return new byte[0];
        }

        if (v instanceof Number) {
            if (v instanceof Byte) {
                return toByteArray(((Byte) v).byteValue());
            }
            if (v instanceof Short) {
                return toByteArray(((Short) v).shortValue());
            }
            if (v instanceof Integer) {
                return toByteArray(((Integer) v).intValue());
            }
            if (v instanceof Long) {
                return toByteArray(((Long) v).longValue());
            }
        }

        if (v instanceof Boolean) {
            return toByteArray(((Boolean) v).booleanValue());
        }

        if (v instanceof Character) {
            return toByteArray(((Character) v).charValue());
        }

        if (v instanceof byte[]) {
            return toByteArray((byte[]) v);
        }

        if (v instanceof Byte[]) {
            return toByteArray((Byte[]) v);
        }

        throw new IllegalArgumentException("Unsupported type: " + v.getClass());
    }

    public static byte[] toByteArray(boolean b) {
        return b ? new byte[]{0x01} : new byte[]{0x00};
    }

    public static byte[] toByteArray(char c) {
        return toByteArray((short) c);
    }

    public static byte[] toByteArray(byte b) {
        return new byte[]{b};
    }

    public static byte[] toByteArray(short s) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (s >>> 8);
        bytes[1] = (byte) s;
        return bytes;
    }

    public static byte[] toByteArray(int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (i >>> 24);
        bytes[1] = (byte) (i >>> 16);
        bytes[2] = (byte) (i >>> 8);
        bytes[3] = (byte) i;
        return bytes;
    }

    public static byte[] toByteArray(long l) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (l >>> 56);
        bytes[1] = (byte) (l >>> 48);
        bytes[2] = (byte) (l >>> 40);
        bytes[3] = (byte) (l >>> 32);
        bytes[4] = (byte) (l >>> 24);
        bytes[5] = (byte) (l >>> 16);
        bytes[6] = (byte) (l >>> 8);
        bytes[7] = (byte) l;
        return bytes;
    }

    public static byte[] toByteArray(Byte[] bytes) {
        byte[] result = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = bytes[i];
        }
        return result;
    }

    public static byte[] toByteArray(byte[] bytes) {
        byte[] result = new byte[bytes.length];
        System.arraycopy(bytes, 0, result, 0, result.length);
        return result;
    }

    public static boolean getBit(byte b, int pos) {
        if (pos > 7) {
            throw new IndexOutOfBoundsException();
        }
        return (b >>> pos & 1) != 0;
    }

    public static boolean getBit(short s, int pos) {
        if (pos > 15) {
            throw new IndexOutOfBoundsException();
        }
        return (s >>> pos & 1) != 0;
    }

    public static boolean getBit(int i, int pos) {
        if (pos > 31) {
            throw new IndexOutOfBoundsException();
        }
        return (i >>> pos & 1) != 0;
    }

    public static boolean getBit(long l, int pos) {
        if (pos > 63) {
            throw new IndexOutOfBoundsException();
        }
        return (l >>> pos & 1) != 0;
    }
}
