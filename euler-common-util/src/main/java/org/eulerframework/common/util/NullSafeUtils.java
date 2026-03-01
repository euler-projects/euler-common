/*
 * Copyright 2013-2026 the original author or authors.
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

import jakarta.annotation.Nullable;

import java.util.Arrays;

public class NullSafeUtils {
    public static boolean nullSafeEquals(@Nullable Object o1, @Nullable Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (o1.equals(o2)) {
            return true;
        }
        if (o1.getClass().isArray() && o2.getClass().isArray()) {
            return arrayEquals(o1, o2);
        }
        return false;
    }

    public static int nullSafeHash(@Nullable Object... elements) {
        if (elements == null) {
            return 0;
        }
        int result = 1;
        for (Object element : elements) {
            result = 31 * result + nullSafeHashCode(element);
        }
        return result;
    }

    public static int nullSafeHashCode(@Nullable Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj.getClass().isArray()) {
            if (obj instanceof Object[] objects) {
                return Arrays.hashCode(objects);
            }
            if (obj instanceof boolean[] booleans) {
                return Arrays.hashCode(booleans);
            }
            if (obj instanceof byte[] bytes) {
                return Arrays.hashCode(bytes);
            }
            if (obj instanceof char[] chars) {
                return Arrays.hashCode(chars);
            }
            if (obj instanceof double[] doubles) {
                return Arrays.hashCode(doubles);
            }
            if (obj instanceof float[] floats) {
                return Arrays.hashCode(floats);
            }
            if (obj instanceof int[] ints) {
                return Arrays.hashCode(ints);
            }
            if (obj instanceof long[] longs) {
                return Arrays.hashCode(longs);
            }
            if (obj instanceof short[] shorts) {
                return Arrays.hashCode(shorts);
            }
        }
        return obj.hashCode();
    }

    private static boolean arrayEquals(Object o1, Object o2) {
        if (o1 instanceof Object[] objects1 && o2 instanceof Object[] objects2) {
            return Arrays.equals(objects1, objects2);
        }
        Class<?> type1 = o1.getClass();
        Class<?> type2 = o2.getClass();
        if (type1 == boolean[].class && type2 == boolean[].class) {
            return Arrays.equals((boolean[]) o1, (boolean[]) o2);
        }
        if (type1 == byte[].class && type2 == byte[].class) {
            return Arrays.equals((byte[]) o1, (byte[]) o2);
        }
        if (type1 == char[].class && type2 == char[].class) {
            return Arrays.equals((char[]) o1, (char[]) o2);
        }
        if (type1 == double[].class && type2 == double[].class) {
            return Arrays.equals((double[]) o1, (double[]) o2);
        }
        if (type1 == float[].class && type2 == float[].class) {
            return Arrays.equals((float[]) o1, (float[]) o2);
        }
        if (type1 == int[].class && type2 == int[].class) {
            return Arrays.equals((int[]) o1, (int[]) o2);
        }
        if (type1 == long[].class && type2 == long[].class) {
            return Arrays.equals((long[]) o1, (long[]) o2);
        }
        if (type1 == short[].class && type2 == short[].class) {
            return Arrays.equals((short[]) o1, (short[]) o2);
        }
        return false;
    }
}
