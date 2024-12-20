package org.eulerframework.common.util;

import org.junit.Assert;
import org.junit.Test;

public class NumberUtilsTest {

    @Test
    public void cast() {
        byte v = NumberUtils.toUnsignedValue(0xFF, Byte.class);
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
}