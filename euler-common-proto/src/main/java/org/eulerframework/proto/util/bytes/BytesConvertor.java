package org.eulerframework.proto.util.bytes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface BytesConvertor<T> {
    T readFrom(InputStream in) throws IOException;

    T readFrom(byte[] data);

    int writeTo(Object value, OutputStream out) throws IOException;
}
