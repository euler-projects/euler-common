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

import org.eulerframework.proto.annotation.ProtoProperty;
import org.eulerframework.proto.serializer.*;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;

public class ObjectField<T> implements ProtoField<T> {
    private SerializerRegistry serializerRegistry;

    public void setSerializerRegistry(SerializerRegistry serializerRegistry) {
        this.serializerRegistry = serializerRegistry;
    }

    public static <T> ObjectField<T> valueOf(T value) {
        ObjectField<T> field = new ObjectField<>();
        field.read(value);
        return field;
    }

    private T data;

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
    }

    @Override
    public void read(InputStream in) throws IOException {
        List<Field> fields = FieldUtils.getFieldsListWithAnnotation(this.data.getClass(), ProtoProperty.class);
        fields.sort((o1, o2) -> {
            int o1Order = o1.getAnnotation(ProtoProperty.class).order();
            int o2Order = o2.getAnnotation(ProtoProperty.class).order();
            return Integer.compare(o1Order, o2Order);
        });
        for (Field field : fields) {
            ProtoProperty property = field.getAnnotation(ProtoProperty.class);
            String type = property.type();
            Deserializer deserializer = this.serializerRegistry.getDeserializer(type);
            Object value;
            if (property.length() < 0) {
                value = deserializer.read(in, field.getType());
            } else {
                value = deserializer.read(in, property.length(), field.getType());
            }
            try {
                FieldUtils.writeField(field, this.data, value, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public byte[] writeAsBytes() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            this.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(OutputStream out) throws IOException {
        List<Field> fields = FieldUtils.getFieldsListWithAnnotation(this.data.getClass(), ProtoProperty.class);
        fields.sort((o1, o2) -> {
            int o1Order = o1.getAnnotation(ProtoProperty.class).order();
            int o2Order = o2.getAnnotation(ProtoProperty.class).order();
            return Integer.compare(o1Order, o2Order);
        });
        for (Field field : fields) {
            ProtoProperty property = field.getAnnotation(ProtoProperty.class);
            String type = property.type();
            Object value = null;
            try {
                value = FieldUtils.readField(field, this.data, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            Serializer serializer = this.serializerRegistry.getSerializer(type);
            if (property.length() < 0) {
                serializer.writeTo(value, out);
            } else {
                serializer.writeTo(value, property.length(), out);
            }
        }
    }
}
