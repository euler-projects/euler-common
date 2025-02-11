package org.eulerframework.proto.serializer;

import org.eulerframework.proto.annotation.ProtoProperty;
import org.eulerframework.proto.annotation.ProtoPropertyOption;
import org.eulerframework.proto.util.PropertyField;
import org.eulerframework.proto.util.ProtoContext;
import org.eulerframework.proto.util.bytes.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Optional;

public class ByteArraySerializer extends AbstractSerializer implements Serializer {
    @Override
    public void writeTo(ProtoContext ctx, Object value, OutputStream outputStream) throws IOException {
        throw new UnsupportedEncodingException();
    }

    @Override
    public void writeTo(ProtoContext ctx, PropertyField valueField, Object value, OutputStream outputStream) throws IOException {
        Field field = valueField.getField();
        Class<?> fieldType = field.getType();
        ProtoProperty protoProperty = valueField.getAnnotation();
        ProtoPropertyOption protoPropertyOption = protoProperty.option();
        String lengthMode = Optional.ofNullable(protoPropertyOption)
                .map(ProtoPropertyOption::lengthMode)
                .orElse(ProtoPropertyOption.LENGTH_MODE_FIXED);

        int length = protoProperty.length();
        int count;
        if (value == null) {
            count = 0;
        } else {
            if (CharSequence.class.isAssignableFrom(fieldType)) {
                CharSequenceBytesConvertor<?> convertor = (CharSequenceBytesConvertor<?>) ByteConvertorRegistryFactory.defaultRegistry()
                        .getConvertor(fieldType);
                Charset charset = Charset.forName(protoProperty.charset());
                count = convertor.writeTo(value, outputStream, charset);
            } else {
                BytesConvertor<?> convertor = ByteConvertorRegistryFactory.defaultRegistry()
                        .getConvertor(fieldType);

                if (FixedLengthBytesConvertor.class.isAssignableFrom(convertor.getClass())) {
                    int actualLength = ((FixedLengthBytesConvertor<?>) convertor).length();
                    if (actualLength > length) {
                        throw new IndexOutOfBoundsException("The bytes length of this property is " + length +
                                ", but " + actualLength + " bytes will be written.");
                    }
                }

                count = convertor.writeTo(value, outputStream);
            }
        }

        if (ProtoPropertyOption.LENGTH_MODE_FIXED.equals(lengthMode)) {
            if (count > length) {
                throw new IndexOutOfBoundsException("The bytes length of this property is " + length +
                        ", but " + count + " bytes was written.");
            }

            if (count < length) {
                for (int i = count; i < length; i++) {
                    outputStream.write((byte) 0);
                }
            }
        }
    }
}
