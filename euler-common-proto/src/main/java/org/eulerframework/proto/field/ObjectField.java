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
import org.eulerframework.proto.annotation.ProtoProperty;
import org.eulerframework.proto.node.ObjectProtoNode;
import org.eulerframework.proto.serializer.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.eulerframework.proto.util.PropertyField;
import org.eulerframework.proto.util.ProtoContext;
import org.eulerframework.proto.util.ProtoUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.List;

public class ObjectField<T> implements ProtoField<T> {

    public static <T> ObjectField<T> newInstance(ProtoContext ctx, ObjectProtoNode objectNode) {
        return new ObjectField<>(ctx, objectNode);
    }

    public static <T> ObjectField<T> valueOf(ProtoContext ctx, T value) {
        ObjectField<T> field = new ObjectField<>(ctx, null);
        field.read(value);
        return field;
    }

    private SerializerRegistry serializerRegistry;

    public ObjectField(ProtoContext ctx, ObjectProtoNode objectNode) {
        this.ctx = ctx;
        this.objectNode = objectNode;
    }

    public void setSerializerRegistry(SerializerRegistry serializerRegistry) {
        this.serializerRegistry = serializerRegistry;
    }

    private T data;
    private final ProtoContext ctx;
    private final ObjectProtoNode objectNode;

    @Override
    public T value() {
        return this.data;
    }

    @Override
    public void read(T object) {
        if (object == null) {
            throw new NullPointerException("object is null");
        }
        this.data = object;

    }

    @Override
    public void read(byte[] bytes) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            this.read(in);
        } catch (IOException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    @Override
    public void read(InputStream in) throws IOException {
        List<PropertyField> propertyFields = ProtoUtils.getSortedPropertyFields(this.value().getClass(), ctx.getVersion());
        for (PropertyField propertyField : propertyFields) {
            if (in.available() == 0) {
                break;
            }
            Field field = propertyField.getField();
            ProtoProperty property = propertyField.getAnnotation();
            String type = property.type();
            Deserializer deserializer = this.serializerRegistry.getDeserializer(type);
            Object value;
            ObjectProtoNode.PropertyNodeFuture propertyNodeFuture = this.objectNode.addProperty(field.getName(), deserializer::newProtoNode);
            if (property.length() < 0) {
                value = deserializer.read(ctx, in, propertyField, propertyNodeFuture.get());
            } else {
                value = deserializer.read(ctx, in, property.length(), propertyField, propertyNodeFuture.get());
            }
            propertyNodeFuture.setValue(value);
            try {
                FieldUtils.writeField(field, this.data, value, true);
            } catch (IllegalAccessException e) {
                throw ExceptionUtils.asRuntimeException(e);
            }
        }
    }

    @Override
    public byte[] writeAsBytes() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            this.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }

    @Override
    public void write(OutputStream out) throws IOException {
        List<PropertyField> propertyFields = ProtoUtils.getSortedPropertyFields(this.value().getClass(), ctx.getVersion());
        for (PropertyField propertyField : propertyFields) {
            Field field = propertyField.getField();
            ProtoProperty property = propertyField.getAnnotation();
            String type = property.type();
            Object value;
            try {
                value = FieldUtils.readField(field, this.data, true);
            } catch (IllegalAccessException e) {
                throw ExceptionUtils.asRuntimeException(e);
            }
            Serializer serializer = this.serializerRegistry.getSerializer(type);

            if (property.length() < 0) {
                serializer.writeTo(ctx, propertyField, value, out);
            } else {
                serializer.writeTo(ctx, propertyField, value, property.length(), out);
            }
        }
    }
}
