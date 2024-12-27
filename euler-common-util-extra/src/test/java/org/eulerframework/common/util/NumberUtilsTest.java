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

import org.junit.Assert;
import org.junit.Test;

public class NumberUtilsTest {
    @Test
    public void toBoolean() {
        Assert.assertTrue(NumberUtils.toBoolean(true));
        Assert.assertTrue(NumberUtils.toBoolean((byte)0xFF));
        Assert.assertTrue(NumberUtils.toBoolean((short) -1));
        Assert.assertTrue(NumberUtils.toBoolean(-1));
        Assert.assertTrue(NumberUtils.toBoolean(-1L));
        Assert.assertTrue(NumberUtils.toBoolean(true));
        Assert.assertTrue(NumberUtils.toBoolean((byte)0x01));
        Assert.assertTrue(NumberUtils.toBoolean((short) 1));
        Assert.assertTrue(NumberUtils.toBoolean(1));
        Assert.assertTrue(NumberUtils.toBoolean(1L));
        Assert.assertFalse(NumberUtils.toBoolean(false));
        Assert.assertFalse(NumberUtils.toBoolean((byte)0x00));
        Assert.assertFalse(NumberUtils.toBoolean((short) 0));
        Assert.assertFalse(NumberUtils.toBoolean(0));
        Assert.assertFalse(NumberUtils.toBoolean(0L));
    }

    @Test
    public void toByte() {
        char c = '\uffff';
        System.out.printf("0x%x\n", NumberUtils.toByte(c));
        System.out.printf("0x%x\n", NumberUtils.toByte(Character.valueOf(c)));
        System.out.printf("0x%x\n", NumberUtils.toByte((byte) 0xFF));
        System.out.printf("0x%x\n", NumberUtils.toByte((short) -1));
        System.out.printf("0x%x\n", NumberUtils.toByte(-1));
        System.out.printf("0x%x\n", NumberUtils.toByte(-1L));

        Assert.assertEquals(0x01, 0xFFL & NumberUtils.toByte(true));
        Assert.assertEquals(0x00, 0xFFL & NumberUtils.toByte(false));
        Assert.assertEquals(0xFF, 0xFFL & NumberUtils.toByte(c));
        Assert.assertEquals(0xFF, 0xFFL & NumberUtils.toByte(Character.valueOf(c)));
        Assert.assertEquals(0xFF, 0xFFL & NumberUtils.toByte((byte) 0xFF));
        Assert.assertEquals(0xFF, 0xFFL & NumberUtils.toByte((short) -1));
        Assert.assertEquals(0xFF, 0xFFL & NumberUtils.toByte(-1));
        Assert.assertEquals(0xFF, 0xFFL & NumberUtils.toByte(-1L));
    }

    @Test
    public void toUnsignedShort() {
        short r = 0x00F9;
        char c = '\u00f9';
        Assert.assertEquals(1, NumberUtils.toUnsignedShort(true));
        Assert.assertEquals(0, NumberUtils.toUnsignedShort(false));
        Assert.assertEquals(r, NumberUtils.toUnsignedShort(c));
        Assert.assertEquals(r, NumberUtils.toUnsignedShort((byte) 0xF9));
        Assert.assertEquals(r, NumberUtils.toUnsignedShort(r));
        Assert.assertEquals(r, NumberUtils.toUnsignedShort(0xFFFF00F9));
        Assert.assertEquals(r, NumberUtils.toUnsignedShort(0xFFFFFFFFFFFF00F9L));


        r = 0x7FF9;
        c = '\u7Ff9';
        Assert.assertEquals(r, NumberUtils.toUnsignedShort(c));
        Assert.assertEquals(r, NumberUtils.toUnsignedShort(Character.valueOf(c)));
        //Assert.assertEquals(r, NumberUtils.toUnsignedShort((byte) 0xF9));
        Assert.assertEquals(r, NumberUtils.toUnsignedShort(r));
        Assert.assertEquals(r, NumberUtils.toUnsignedShort(0xFFFF7FF9));
        Assert.assertEquals(r, NumberUtils.toUnsignedShort(Integer.valueOf(0xFFFF7FF9)));
        Assert.assertEquals(r, NumberUtils.toUnsignedShort(0xFFFFFFFFFFFF7FF9L));
        Assert.assertEquals(r, NumberUtils.toUnsignedShort(Long.valueOf(0xFFFFFFFFFFFF7FF9L)));
        Assert.assertEquals(r, NumberUtils.toUnsignedShort(0x0FFF7FF9));
        Assert.assertEquals(r, NumberUtils.toUnsignedShort(0x0FFFFFFFFFFF7FF9L));
    }

    @Test
    public void toUnsignedInt() {
        char c = '\uffff';
        Assert.assertEquals(1, NumberUtils.toUnsignedInt(true));
        Assert.assertEquals(0, NumberUtils.toUnsignedInt(false));
        Assert.assertEquals(0x0000FFFF, NumberUtils.toUnsignedInt(c));
        Assert.assertEquals(0x0000FFFF, NumberUtils.toUnsignedInt(Character.valueOf(c)));
        Assert.assertEquals(0x000000FF, NumberUtils.toUnsignedInt((byte) 0xFF));
        Assert.assertEquals(0x000000FF, NumberUtils.toUnsignedInt(Byte.valueOf((byte) 0xFF)));
        short s = -1; // 0xFFFF
        Assert.assertEquals(0x0000FFFF, NumberUtils.toUnsignedInt(s));
        Assert.assertEquals(0x0000FFFF, NumberUtils.toUnsignedInt(Short.valueOf(s)));
        Assert.assertEquals(0x7FFFFFFF, NumberUtils.toUnsignedInt(0x7FFFFFFF));
        Assert.assertEquals(0x7FFFFFFF, NumberUtils.toUnsignedInt(Integer.valueOf(0x7FFFFFFF)));
        Assert.assertEquals(0x7FFFFFFF, NumberUtils.toUnsignedInt(0xFFFFFFFF7FFFFFFFL));
        Assert.assertEquals(0x7FFFFFFF, NumberUtils.toUnsignedInt(Long.valueOf(0xFFFFFFFF7FFFFFFFL)));
    }

    @Test
    public void toUnsignedLong() {
        char c = '\uffff';
        Assert.assertEquals(1, NumberUtils.toUnsignedLong(true));
        Assert.assertEquals(0, NumberUtils.toUnsignedLong(false));
        Assert.assertEquals(0x000000000000FFFF, NumberUtils.toUnsignedLong(c));
        Assert.assertEquals(0x000000000000FFFF, NumberUtils.toUnsignedLong(Character.valueOf(c)));
        Assert.assertEquals(0x00000000000000FF, NumberUtils.toUnsignedLong((byte) 0xFF));
        Assert.assertEquals(0x00000000000000FF, NumberUtils.toUnsignedLong(Byte.valueOf((byte) 0xFF)));
        short s = -1; // 0xFFFF
        Assert.assertEquals(0x000000000000FFFF, NumberUtils.toUnsignedLong(s));
        Assert.assertEquals(0x000000000000FFFF, NumberUtils.toUnsignedLong(Short.valueOf(s)));
        Assert.assertEquals(0x00000000FFFFFFFFL, NumberUtils.toUnsignedLong(-1));
        Assert.assertEquals(0x00000000FFFFFFFFL, NumberUtils.toUnsignedLong(Integer.valueOf(-1)));
        Assert.assertEquals(-1L, NumberUtils.toUnsignedLong(-1L));
        Assert.assertEquals(-1L, NumberUtils.toUnsignedLong(Long.valueOf(-1)));
        Assert.assertEquals(-1L, NumberUtils.toUnsignedLong(Long.valueOf(-1L)));
    }

    @Test
    public void toUnsignedShortLE() {
        Assert.assertEquals(0x0000FFFF, NumberUtils.toUnsignedShortLE((byte) 0xFF, (byte)0xFF));
        Assert.assertEquals(0x0000FEFF, NumberUtils.toUnsignedShortLE((byte) 0xFF, (byte)0xFE));
        Assert.assertEquals(0x00000001, NumberUtils.toUnsignedShortLE((byte) 0x01, (byte)0x00));
        Assert.assertEquals(0x0000FF01, NumberUtils.toUnsignedShortLE((byte) 0x01, (byte)0xFF));
        Assert.assertEquals(0x0000F001, NumberUtils.toUnsignedShortLE((byte) 0x01, (byte)0xF0));
    }

    @Test
    public void getBit() {
        Assert.assertTrue(NumberUtils.getBit(0x00001111, 0));
        Assert.assertFalse(NumberUtils.getBit(0x00001111, 1));
        Assert.assertFalse(NumberUtils.getBit(0x00001111, 2));
        Assert.assertFalse(NumberUtils.getBit(0x00001111, 3));
        Assert.assertTrue(NumberUtils.getBit(0x00001111, 4));
        Assert.assertFalse(NumberUtils.getBit(0x00001111, 5));
        Assert.assertFalse(NumberUtils.getBit(0x00001111, 6));
        Assert.assertFalse(NumberUtils.getBit(0x00001111, 7));
        Assert.assertTrue(NumberUtils.getBit(0x00001111, 8));
        Assert.assertFalse(NumberUtils.getBit(0x00001111, 9));
        Assert.assertFalse(NumberUtils.getBit(0x00001111, 10));
        Assert.assertFalse(NumberUtils.getBit(0x00001111, 11));
        Assert.assertTrue(NumberUtils.getBit(0x00001111, 12));
        Assert.assertFalse(NumberUtils.getBit(0x00001111, 13));
        Assert.assertFalse(NumberUtils.getBit(0x00001111, 14));
        Assert.assertFalse(NumberUtils.getBit(0x00001111, 15));
    }
}