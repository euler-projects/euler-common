package org.eulerframework.proto.serializer;

import org.eulerframework.proto.util.PropertyField;
import org.eulerframework.proto.util.ProtoContext;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class ByteArraySerializer extends AbstractSerializer implements Serializer {
    @Override
    public void writeTo(ProtoContext ctx, Object value, OutputStream outputStream) throws IOException {
        throw new UnsupportedEncodingException();
    }

    @Override
    public void writeTo(ProtoContext ctx, PropertyField valueField, Object value, OutputStream outputStream) throws IOException {
        super.writeTo(ctx, valueField, value, outputStream);
    }
}
