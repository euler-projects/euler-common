package org.eulerframework.proto.util.bytes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public interface CharSequenceBytesConvertor<T extends CharSequence> extends BytesConvertor<T> {
    T readFrom(InputStream in, Charset charset) throws IOException;

    T readFrom(byte[] data, Charset charset);

    int writeTo(Object value, OutputStream out, Charset charset) throws IOException;

    @Override
    default T readFrom(InputStream in) throws IOException {
        return readFrom(in, Charset.defaultCharset());
    }

    @Override
    default T readFrom(byte[] data) {
        return readFrom(data, Charset.defaultCharset());
    }

    @Override
    default int writeTo(Object value, OutputStream out) throws IOException {
        return writeTo(value, out, Charset.defaultCharset());
    }
}
