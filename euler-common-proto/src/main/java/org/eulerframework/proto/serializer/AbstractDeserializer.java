package org.eulerframework.proto.serializer;

import org.eulerframework.proto.node.ProtoNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

public abstract class AbstractDeserializer implements Deserializer {
    @Override
    public <T> T read(InputStream in, Class<T> clazz) throws IOException {
        throw new UnsupportedEncodingException();
    }

    @Override
    public <T> T read(InputStream in, int length, Class<T> clazz) throws IOException {
        throw new UnsupportedEncodingException();
    }

    @Override
    public <T> T read(InputStream in, Class<T> clazz, ProtoNode propertyNode) throws IOException {
        return this.read(in, clazz);
    }

    @Override
    public <T> T read(InputStream in, int length, Class<T> clazz, ProtoNode propertyNode) throws IOException {
        return this.read(in, length, clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T read(InputStream in, Field propertyField, ProtoNode propertyNode) throws IOException {
        return (T) this.read(in, propertyField.getType(), propertyNode);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T read(InputStream in, int length, Field propertyField, ProtoNode propertyNode) throws IOException {
        return (T) this.read(in, length, propertyField.getType(), propertyNode);
    }

    @Override
    public ProtoNode newProtoNode(ProtoNode parent) {
        return ProtoNode.newValueNode(parent);
    }
}
