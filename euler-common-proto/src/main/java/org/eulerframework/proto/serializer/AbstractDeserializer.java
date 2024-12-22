package org.eulerframework.proto.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

public abstract class AbstractDeserializer implements Deserializer {
    @Override
    @SuppressWarnings("unchecked")
    public <T> T read(InputStream in, Field propertyField) throws IOException {
        return (T) this.read(in, propertyField.getType());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T read(InputStream in, int length, Field propertyField) throws IOException {
        return (T) this.read(in, length, propertyField.getType());
    }
}
