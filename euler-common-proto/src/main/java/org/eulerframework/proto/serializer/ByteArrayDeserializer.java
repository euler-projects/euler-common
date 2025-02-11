package org.eulerframework.proto.serializer;

import org.eulerframework.proto.annotation.ProtoProperty;
import org.eulerframework.proto.annotation.ProtoPropertyOption;
import org.eulerframework.proto.util.PropertyField;
import org.eulerframework.proto.util.ProtoContext;
import org.eulerframework.proto.util.bytes.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Optional;

public class ByteArrayDeserializer extends AbstractDeserializer implements Deserializer {
    @Override
    @SuppressWarnings("unchecked")
    public <T> T read(ProtoContext ctx, InputStream in, PropertyField propertyField) throws IOException {
        Field field = propertyField.getField();
        Class<?> fieldType = field.getType();
        ProtoProperty protoProperty = propertyField.getAnnotation();
        ProtoPropertyOption protoPropertyOption = protoProperty.option();
        String lengthMode = Optional.ofNullable(protoPropertyOption)
                .map(ProtoPropertyOption::lengthMode)
                .orElse(ProtoPropertyOption.LENGTH_MODE_FIXED);

        byte[] data;
        if (ProtoPropertyOption.LENGTH_MODE_FIXED.equals(lengthMode)) {
            int length = protoProperty.length();

            if (in.available() < length) {
                throw new IllegalArgumentException("Not enough bytes to read.");
            }

            data = new byte[length];
            int readBytes;
            if ((readBytes = in.read(data)) < data.length) {
                throw new IOException("This property need " + data.length + " bytes, but only " + readBytes + " bytes read.");
            }
        } else if (ProtoPropertyOption.LENGTH_MODE_ALL_BYTES.equals(lengthMode)) {
            data = in.readAllBytes();
        } else {
            throw new IllegalArgumentException("Unsupported property length mode: " + lengthMode);
        }

        BytesConvertor<?> convertor = ByteConvertorRegistryFactory.defaultRegistry().getConvertor(fieldType);

        if (convertor instanceof CharSequenceBytesConvertor) {
            CharSequenceBytesConvertor<?> charSequenceBytesConvertor = (CharSequenceBytesConvertor<?>) convertor;
            Charset charset = Charset.forName(protoProperty.charset());
            data = ByteArrayUtils.rightTrim(data);
            return (T) charSequenceBytesConvertor.readFrom(data, charset);
        }

        return (T) convertor.readFrom(data);
    }
}
