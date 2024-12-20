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
package org.eulerframework.proto.serializer;

import org.eulerframework.proto.annotation.ByteArrayObject;
import org.eulerframework.proto.field.ByteArrayObjectField;
import org.eulerframework.proto.field.ObjectField;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

public class ObjectProtoFieldDeserializer implements Deserializer {
    private final SerializerRegistry serializerRegistry;

    public ObjectProtoFieldDeserializer(SerializerRegistry serializerRegistry) {
        this.serializerRegistry = serializerRegistry;
    }

    @Override
    public <T> T read(InputStream in, Class<T> clazz) throws IOException {
        ByteArrayObject byteArrayObject = clazz.getAnnotation(ByteArrayObject.class);
        if (byteArrayObject == null) {
            ObjectField<T> field = new ObjectField<>();
            field.setSerializerRegistry(this.serializerRegistry);
            field.read(this.newInstance(clazz));
            field.read(in);
            return field.value();
        } else {
            ByteArrayObjectField<T> field = new ByteArrayObjectField<>(byteArrayObject.length());
            field.read(this.newInstance(clazz));
            field.read(in);
            return field.value();
        }
    }

    @Override
    public <T> T read(InputStream in, int length, Class<T> clazz) throws IOException {
        throw new UnsupportedOperationException();
    }

    private <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw ExceptionUtils.asRuntimeException(e);
        }
    }
}
