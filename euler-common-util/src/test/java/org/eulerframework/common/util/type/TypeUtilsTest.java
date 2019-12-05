/*
 * Copyright 2013-2019 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.eulerframework.common.util.type;

import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.Date;

public class TypeUtilsTest {

    @Test
    public void convert() {
        Assert.assertNull(TypeUtils.convert(null, Date.class));
        java.sql.Date sqlDate = new java.sql.Date(123L);
        Assert.assertEquals(new Date(123L), TypeUtils.convert(sqlDate, Date.class));
        java.sql.Date[] sqlDateArray = new java.sql.Date[]{new java.sql.Date(1L), new java.sql.Date(2L)};
        Assert.assertArrayEquals(new Date[]{new Date(1L), new Date(2L)}, TypeUtils.convert(sqlDateArray, Date[].class));

        Assert.assertEquals(DurationStyle.SIMPLE, TypeUtils.convert("SiMPlE", DurationStyle.class));


        Assert.assertEquals(Integer[].class, TypeUtils.convert("1, 2, 3", Integer[].class).getClass());
        Assert.assertArrayEquals(new Integer[] {1, 2, 3}, TypeUtils.convert("1, 2, 3", Integer[].class));


        Assert.assertEquals(Integer.class, TypeUtils.convert("1", Integer.class).getClass());
        Assert.assertEquals(new Integer(1), TypeUtils.convert("1", Integer.class));


        Assert.assertArrayEquals(new String[] {"1", "2", "3", "4"}, TypeUtils.convert("1, 2, 3, 4", String[].class));
        Assert.assertArrayEquals(new String[] {"1", "2", "3"}, TypeUtils.convert(new String[] {"1", "2", "3"}, String[].class));

    }

    @Test
    public void convertToInt() {
        Assert.assertEquals(1, TypeUtils.convertToInt("1"));
        Assert.assertEquals(2, TypeUtils.convertToInt(2));
        Assert.assertEquals(-1, TypeUtils.convertToInt(Long.MAX_VALUE));
    }

    @Test
    public void convertToLong() {
        Assert.assertEquals(1L, TypeUtils.convertToLong("1"));
        Assert.assertEquals(2L, TypeUtils.convertToLong(2));
        Assert.assertEquals(Long.MAX_VALUE, TypeUtils.convertToLong(Long.MAX_VALUE));
    }

    @Test
    public void convertToByte() {
        byte b = 127;
        Assert.assertEquals(b, TypeUtils.convertToByte("127"));
    }

    @Test
    public void convertToShort() {
        Assert.assertEquals(1, TypeUtils.convertToShort("1"));
    }

    @Test
    public void convertToFloat() {
        Assert.assertEquals(1.1, TypeUtils.convertToFloat("1.1"), 0.01);
    }

    @Test
    public void convertToDouble() {
        Assert.assertEquals(1.1, TypeUtils.convertToDouble("1.1"), 0.01);
    }

    @Test
    public void convertToIntArray() {
        Assert.assertArrayEquals(new int[] {1, 2, 3}, TypeUtils.convertToIntArray(new int[] {1, 2, 3}));
        Assert.assertArrayEquals(new int[] {1, 2, 3}, TypeUtils.convertToIntArray(new Integer[] {1, 2, 3}));
        Assert.assertArrayEquals(new int[] {1, 2, 3}, TypeUtils.convertToIntArray(new String[] {"1", "2", "3"}));
        Assert.assertArrayEquals(new int[] {1, 2, 3}, TypeUtils.convertToIntArray("1, 2, 3"));
    }

    @Test
    public void convertToLongArray() {
        Assert.assertArrayEquals(new long[] {1, 2, 3}, TypeUtils.convertToLongArray(new long[] {1, 2, 3}));
        Assert.assertArrayEquals(new long[] {1, 2, 3}, TypeUtils.convertToLongArray(new Integer[] {1, 2, 3}));
        Assert.assertArrayEquals(new long[] {1, 2, 3}, TypeUtils.convertToLongArray(new String[] {"1", "2", "3"}));
        Assert.assertArrayEquals(new long[] {1, 2, 3}, TypeUtils.convertToLongArray("1, 2, 3"));
    }

    @Test
    public void asString() {
        Assert.assertEquals("PT1S", TypeUtils.asString(Duration.ofSeconds(1)));
    }
}