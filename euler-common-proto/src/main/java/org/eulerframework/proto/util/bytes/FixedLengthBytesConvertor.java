package org.eulerframework.proto.util.bytes;

public interface FixedLengthBytesConvertor<T> extends BytesConvertor<T> {
    int length();
}
