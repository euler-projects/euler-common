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

import org.eulerframework.proto.annotation.BitProperty;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;

public class ByteObjectField<T> extends AbstractFixedLengthProtoField<T>
        implements FixedLengthProtoField<T> {

    public static <T> ByteObjectField<T> valueOf(T value, int length) {
        ByteObjectField<T> field = new ByteObjectField<>(length);
        field.read(value);
        return field;
    }

    private T data;

    public ByteObjectField(int length) {
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
        int loadData = 0;
        for (int i = 0; i < bytes.length; i++) {
            loadData = loadData | ((bytes[i] & 0xff) << (i << 3));
        }

        List<Field> fields = FieldUtils.getFieldsListWithAnnotation(this.data.getClass(), BitProperty.class);
        fields.sort((o1, o2) -> {
            int o1Order = o1.getAnnotation(BitProperty.class).offset();
            int o2Order = o2.getAnnotation(BitProperty.class).offset();
            return Integer.compare(o1Order, o2Order);
        });

        for (Field field : fields) {
            BitProperty property = field.getAnnotation(BitProperty.class);

            int offset = property.offset();
            int length = property.length();

            if (offset > 32) {
                throw new IllegalArgumentException("the max position is 32");
            }

            if (offset + length > 32) {
                throw new IndexOutOfBoundsException("the data length is out of range");
            }

            int mask = 0x00;
            for (int i = 0; i < length; i++) {
                mask = mask << 1 | 0x01;
            }

            int d = (loadData >> offset) & mask;
            try {
                FieldUtils.writeField(field, this.data, d, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public byte[] writeAsBytes() {
        List<Field> fields = FieldUtils.getFieldsListWithAnnotation(this.data.getClass(), BitProperty.class);
        fields.sort((o1, o2) -> {
            int o1Order = o1.getAnnotation(BitProperty.class).offset();
            int o2Order = o2.getAnnotation(BitProperty.class).offset();
            return Integer.compare(o1Order, o2Order);
        });

        int result = 0x00;

        for (Field field : fields) {
            BitProperty property = field.getAnnotation(BitProperty.class);
            Object value;
            try {
                value = FieldUtils.readField(field, this.data, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (value == null) {
                continue;
            }

            int pos = property.offset();
            int length = property.length();

            if (pos > 32) {
                throw new IllegalArgumentException("the max position is 32");
            }

            if (pos + length > 32) {
                throw new IndexOutOfBoundsException("the data length is out of range");
            }

            int mask = 0x00;
            for (int i = 0; i < length; i++) {
                mask = mask << 1 | 0x01;
            }

            if (value instanceof Number) {
                result = result | ((((Number) value).intValue() & mask) << pos);
            }
        }

        byte[] bytes = new byte[this.length()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((result >> (i << 3)) & 0xff);
        }
        return bytes;
    }
}
