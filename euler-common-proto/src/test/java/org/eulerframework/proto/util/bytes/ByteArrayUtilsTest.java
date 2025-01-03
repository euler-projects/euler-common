package org.eulerframework.proto.util.bytes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ByteArrayUtilsTest {

    @Test
    void rightTrim() {
        Assertions.assertArrayEquals(new byte[]{(byte) 0xFF, (byte) 0xEE, (byte) 0xDD, 0x11},
                ByteArrayUtils.rightTrim(new byte[]{(byte) 0xFF, (byte) 0xEE, (byte) 0xDD, 0x11, 0, 0, 0}));
        Assertions.assertArrayEquals(new byte[]{(byte) 0xFF, (byte) 0xEE, (byte) 0xDD, 0x11},
                ByteArrayUtils.rightTrim(new byte[]{(byte) 0xFF, (byte) 0xEE, (byte) 0xDD, 0x11}));
        Assertions.assertArrayEquals(new byte[]{(byte) 0xFF, (byte) 0xEE, (byte) 0xDD, 0x11},
                ByteArrayUtils.rightTrim(new byte[]{(byte) 0xFF, (byte) 0xEE, (byte) 0xDD, 0x11, 0}));
        Assertions.assertArrayEquals(new byte[0],
                ByteArrayUtils.rightTrim(new byte[]{0, 0, 0}));
        Assertions.assertArrayEquals(new byte[0],
                ByteArrayUtils.rightTrim(new byte[0]));
    }
}