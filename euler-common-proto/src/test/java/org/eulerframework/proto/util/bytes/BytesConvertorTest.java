package org.eulerframework.proto.util.bytes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

class BytesConvertorTest {
    @Test
    void readFrom() throws IOException {
        {
            byte[] data = new byte[]{(byte) 0xFF, (byte) 0xEE};
            try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
                Short value = ByteConvertorRegistryFactory.defaultRegistry().getConvertor(Short.class).readFrom(in);
                Assertions.assertEquals((short)0x0000FFEE, value);
            }
        }

        {
            byte[] data = new byte[]{(byte) 0xFF, (byte) 0xEE, (byte) 0xDD, 0x11};
            try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
                Integer value = ByteConvertorRegistryFactory.defaultRegistry().getConvertor(Integer.class).readFrom(in);
                Assertions.assertEquals(0xFFEEDD11, value);
            }
        }

        {
            byte[] data = new byte[]{(byte) 0xFF, (byte) 0xEE, (byte) 0xDD, 0x11, (byte) 0xAA, (byte) 0xEE, (byte) 0xDD, 0x11};
            try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
                Long value = ByteConvertorRegistryFactory.defaultRegistry().getConvertor(Long.class).readFrom(in);
                Assertions.assertEquals(0xFFEEDD11AAEEDD11L, value);
            }
        }

        {
            Charset gbk = Charset.forName("GBK");
            String str = "我是一个中文字符串";
            byte[] data = str.getBytes(gbk);
            try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
                String value = (String) ((CharSequenceBytesConvertor<?>) ByteConvertorRegistryFactory.defaultRegistry()
                        .getConvertor(String.class)).readFrom(in, gbk);
                Assertions.assertEquals(str, value);
            }
        }

        {
            String str = "我是一个带Emoji😂😂的中文字符串";
            byte[] data = str.getBytes(StandardCharsets.UTF_8);
            try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
                String value = (String) ((CharSequenceBytesConvertor<?>) ByteConvertorRegistryFactory.defaultRegistry()
                        .getConvertor(String.class)).readFrom(in, StandardCharsets.UTF_8);
                Assertions.assertEquals(str, value);
            }
        }
    }

    @Test
    void writeTo() throws IOException {
        {
            byte[] data = new byte[]{(byte) 0xFF, (byte) 0xEE};
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                int count = ByteConvertorRegistryFactory.defaultRegistry().getConvertor(Short.class).writeTo((short)0x0000FFEE, out);
                Assertions.assertEquals(2, count);
                Assertions.assertArrayEquals(data, out.toByteArray());
            }
        }

        {
            byte[] data = new byte[]{(byte) 0xFF, (byte) 0xEE, (byte) 0xDD, 0x11};
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                int count = ByteConvertorRegistryFactory.defaultRegistry().getConvertor(Integer.class).writeTo(0xFFEEDD11, out);
                Assertions.assertEquals(4, count);
                Assertions.assertArrayEquals(data, out.toByteArray());
            }
        }

        {
            byte[] data = new byte[]{(byte) 0xFF, (byte) 0xEE, (byte) 0xDD, 0x11, (byte) 0xDD, (byte) 0xEE, (byte) 0xDD, 0x11};
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                int count = ByteConvertorRegistryFactory.defaultRegistry().getConvertor(Long.class).writeTo(0xFFEEDD11DDEEDD11L, out);
                Assertions.assertEquals(8, count);
                Assertions.assertArrayEquals(data, out.toByteArray());
            }
        }

        {
            Charset gbk = Charset.forName("GBK");
            String str = "我是一个中文字符串";
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                int count = ((CharSequenceBytesConvertor<?>) ByteConvertorRegistryFactory.defaultRegistry()
                        .getConvertor(String.class)).writeTo(str, out, gbk);
                byte[] data = str.getBytes(gbk);
                Assertions.assertEquals(data.length, count);
                Assertions.assertArrayEquals(data, out.toByteArray());
            }
        }

        {
            String str = "我是一个带Emoji😂😂的中文字符串";
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                int count = ((CharSequenceBytesConvertor<?>) ByteConvertorRegistryFactory.defaultRegistry()
                        .getConvertor(String.class)).writeTo(str, out, StandardCharsets.UTF_8);
                byte[] data = str.getBytes(StandardCharsets.UTF_8);
                Assertions.assertEquals(data.length, count);
                Assertions.assertArrayEquals(data, out.toByteArray());
            }
        }
    }
}