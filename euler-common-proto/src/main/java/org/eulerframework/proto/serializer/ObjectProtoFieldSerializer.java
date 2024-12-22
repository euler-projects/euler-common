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

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class ObjectProtoFieldSerializer extends AbstractSerializer implements Serializer {
    private final SerializerRegistry serializerRegistry;

    public ObjectProtoFieldSerializer(SerializerRegistry serializerRegistry) {
        this.serializerRegistry = serializerRegistry;
    }

    @Override
    public void writeTo(Object value, OutputStream outputStream) throws IOException {
        ByteArrayObject byteArrayObject = value.getClass().getAnnotation(ByteArrayObject.class);
        if (byteArrayObject == null) {
            ObjectField<?> objectField = ObjectField.valueOf(value);
            objectField.setSerializerRegistry(this.serializerRegistry);
            objectField.write(outputStream);
        } else {
            ByteArrayObjectField<?> objectField = ByteArrayObjectField.valueOf(value, byteArrayObject.length());
            objectField.write(outputStream);
        }
    }

    @Override
    public void writeTo(Object value, int length, OutputStream outputStream) throws IOException {
        throw new UnsupportedEncodingException();
    }
}
