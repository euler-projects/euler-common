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
import org.eulerframework.proto.node.ObjectProtoNode;
import org.eulerframework.proto.node.ProtoNode;
import org.eulerframework.proto.util.ProtoUtils;

import java.lang.reflect.Field;
import java.util.List;

public class ByteArrayObjectField<T> extends AbstractFixedLengthProtoField<T>
        implements FixedLengthProtoField<T> {
    private final static int MAX_FIELD_BITS = Integer.bitCount(-1);

    public static <T> ByteArrayObjectField<T> newInstance(int length, ObjectProtoNode objectNode) {
        return new ByteArrayObjectField<>(length, objectNode);
    }

    public static <T> ByteArrayObjectField<T> valueOf(T value, int length) {
        ByteArrayObjectField<T> field = new ByteArrayObjectField<>(length, null);
        field.read(value);
        return field;
    }

    private T data;
    private final ObjectProtoNode objectNode;

    public ByteArrayObjectField(int length, ObjectProtoNode objectNode) {
        super(length);
        this.objectNode = objectNode;
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
        List<ProtoUtils.BitPropertyField> bitPropertyFields = ProtoUtils.getSortedBitPropertyFields(this.data.getClass(), 1);

        for (ProtoUtils.BitPropertyField bitPropertyField : bitPropertyFields) {
            Field field = bitPropertyField.getField();
            BitProperty property = bitPropertyField.getAnnotation();

            int offset = property.offset();
            int length = property.length();

            if (length > MAX_FIELD_BITS) {
                throw new IllegalArgumentException("The max bit length of a field is " + MAX_FIELD_BITS + ", but " + length);
            }

            int value = 0;
            for (int bit = 0; bit < length; bit++) {
                int bitIndex = offset + bit;
                // Big-Endian
                int byteIndex = bytes.length - bitIndex / 8 - 1;
                value = value | (bytes[byteIndex] >>> bitIndex % 8 & 0x01) << bit;
            }

            Object typedValue = NumberUtils.toUnsignedValue(value, field.getType());
            this.objectNode.addProperty(field.getName(), ProtoNode::newValueNode).setValue(typedValue);
            try {
                FieldUtils.writeField(field, this.data, typedValue, true);
            } catch (IllegalAccessException e) {
                throw ExceptionUtils.asRuntimeException(e);
            }
        }
    }

    @Override
    public byte[] writeAsBytes() {
        byte[] result = new byte[this.length()];

        List<ProtoUtils.BitPropertyField> bitPropertyFields = ProtoUtils.getSortedBitPropertyFields(this.data.getClass(), 1);

        for (ProtoUtils.BitPropertyField bitPropertyField : bitPropertyFields) {
            Field field = bitPropertyField.getField();
            BitProperty property = bitPropertyField.getAnnotation();
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

            if (value >>> length != 0) {
                throw new IllegalArgumentException(String.format("value 0x%04x(%d) is too large for a %d bit value", value, value, length));
            }

            for (int bit = 0; bit < length; bit++) {
                int bitIndex = offset + bit;
                // Big-Endian
                int byteIndex = result.length - bitIndex / 8 - 1;
                result[byteIndex] = (byte) (result[byteIndex] |
                        (value >>> bit & 0x01) << bitIndex % 8);
            }
        }

        return result;
    }
}
