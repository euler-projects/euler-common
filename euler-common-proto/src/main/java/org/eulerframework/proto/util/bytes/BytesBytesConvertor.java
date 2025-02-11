package org.eulerframework.proto.util.bytes;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BytesBytesConvertor implements BytesConvertor<byte[]> {

    @Override
    public byte[] readFrom(InputStream in) throws IOException {
        return in.readAllBytes();
    }

    @Override
    public byte[] readFrom(byte[] data) {
        return ArrayUtils.clone(data);
    }

    @Override
    public int writeTo(Object value, OutputStream out) throws IOException {
        byte[] bytes = (byte[]) value;
        out.write(bytes);
        return bytes.length;
    }
}
