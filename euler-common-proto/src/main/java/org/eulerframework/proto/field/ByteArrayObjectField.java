/*
 * Copyright 2013-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eulerframework.proto.field;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.eulerframework.common.util.NumberUtils;
import org.eulerframework.proto.annotation.BitProperty;

import java.lang.reflect.Field;
import java.util.List;

public class ByteArrayObjectField<T> extends AbstractFixedLengthProtoField<T>
        implements FixedLengthProtoField<T> {
    private final static int MAX_FIELD_BITS = Integer.bitCount(-1);

    public static <T> ByteArrayObjectField<T> valueOf(T value, int length) {
        ByteArrayObjectField<T> field = new ByteArrayObjectField<>(length);
        field.read(value);
        return field;
    }

    private T data;

    public ByteArrayObjectField(int length) {
        super(length);
    }

    @Override
    public T value() {
        return this.data;
    }

    @Override
    public void read(T object) {
        this.data = object;
    }

    @Override
    public void read(byte[] bytes) {
        List<Field> fields = getBitPropertyFields();

        for (Field field : fields) {
            BitProperty property = field.getAnnotation(BitProperty.class);

            int offset = property.offset();
            int length = property.length();

            if (length > MAX_FIELD_BITS) {
                throw new IllegalArgumentException("The max bit length of a field is " + MAX_FIELD_BITS + ", but " + length);
            }

            int value = 0;
            for (int bit = 0; bit < length; bit++) {
                int bitIndex = offset + bit;
                value = value | (bytes[bitIndex / 8] >> bitIndex % 8 & 0x01) << bit;
            }

            try {
                FieldUtils.writeField(field, this.data, value, true);
            } catch (IllegalAccessException e) {
                throw ExceptionUtils.asRuntimeException(e);
            }
        }
    }

    @Override
    public byte[] writeAsBytes() {
        List<Field> fields = getBitPropertyFields();

        byte[] result = new byte[this.length()];

        for (Field field : fields) {
            BitProperty property = field.getAnnotation(BitProperty.class);
            int value;
            try {
                value = NumberUtils.toUnsignedInt(FieldUtils.readField(field, this.data, true));
            } catch (IllegalAccessException e) {
                throw ExceptionUtils.asRuntimeException(e);
            }

            if (value == 0) {
                continue;
            }

            int offset = property.offset();
            int length = property.length();

            if (length > MAX_FIELD_BITS) {
                throw new IllegalArgumentException("The max bit length of a field is " + MAX_FIELD_BITS + ", but " + length);
            }

            if (value >> length != 0) {
                throw new IllegalArgumentException(String.format("value 0x%04x(%d) is too large for a %d bit value", value, value, length));
            }

            for (int bit = 0; bit < length; bit++) {
                int bitIndex = offset + bit;
                result[bitIndex / 8] = (byte) (result[bitIndex / 8] |
                        (value >> bit & 0x01) << bitIndex % 8);
            }
        }

        return result;
    }

    private List<Field> getBitPropertyFields() {
        List<Field> fields = FieldUtils.getFieldsListWithAnnotation(this.data.getClass(), BitProperty.class);
        fields.sort((o1, o2) -> {
            int o1Order = o1.getAnnotation(BitProperty.class).offset();
            int o2Order = o2.getAnnotation(BitProperty.class).offset();
            return Integer.compare(o1Order, o2Order);
        });
        return fields;
    }
}
