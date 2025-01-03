package org.eulerframework.proto.util.bytes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IntegerBytesConvertor implements NumberBytesConvertor<Integer> {
    @Override
    public Integer readFrom(InputStream in) throws IOException {
        int length = this.length();
        if (in.available() < length) {
            throw new IOException("Not enough bytes to read integer");
        }
        byte[] data = new byte[length];
        int readBytes;
        if ((readBytes = in.read(data)) < data.length) {
            throw new IOException("An integer need " + data.length + " bytes, but only " + readBytes + " bytes read.");
        }

        return this.readFrom(data);
    }

    @Override
    public Integer readFrom(byte[] data) {
        int value = 0;
        int length = Math.min(data.length, this.length());
        for (int i = 0; i < length; i++) {
            value = (value << 8) | (data[i] & 0xff);
        }
        return value;
    }

    @Override
    public int writeTo(Object value, OutputStream out) throws IOException {
        int i = ((Number) value).intValue();
        int length = this.length();
        for (int j = length - 1; j >= 0; j--) {
            out.write(i >>> (j << 3) & 0xFF);
        }
        return length;
    }

    @Override
    public int length() {
        return 4;
    }
}
