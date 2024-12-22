package org.eulerframework.proto.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;

public abstract class AbstractSerializer implements Serializer {
    @Override
    public void writeTo(Field valueField, Object value, OutputStream outputStream) throws IOException {
        this.writeTo(value, outputStream);
    }

    @Override
    public void writeTo(Field valueField, Object value, int length, OutputStream outputStream) throws IOException {
        this.writeTo(value, length, outputStream);
    }
}
