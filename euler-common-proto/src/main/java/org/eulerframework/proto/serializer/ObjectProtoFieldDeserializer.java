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

import org.eulerframework.common.util.JavaObjectUtils;
import org.eulerframework.proto.annotation.ByteArrayObject;
import org.eulerframework.proto.field.*;
import org.eulerframework.proto.node.ObjectProtoNode;
import org.eulerframework.proto.node.ProtoNode;
import org.eulerframework.proto.util.ProtoContext;

import java.io.IOException;
import java.io.InputStream;

public class ObjectProtoFieldDeserializer extends AbstractDeserializer implements Deserializer {
    private final SerializerRegistry serializerRegistry;

    public ObjectProtoFieldDeserializer(SerializerRegistry serializerRegistry) {
        this.serializerRegistry = serializerRegistry;
    }

    @Override
    public <T> T read(ProtoContext ctx, InputStream in, Class<T> clazz, ProtoNode propertyNode) throws IOException {
        ByteArrayObject byteArrayObject = clazz.getAnnotation(ByteArrayObject.class);
        if (byteArrayObject == null) {
            ObjectField<T> field = ObjectField.newInstance(ctx, (ObjectProtoNode) propertyNode);
            field.setSerializerRegistry(this.serializerRegistry);
            field.read(JavaObjectUtils.newInstance(clazz));
            field.read(in);
            return field.value();
        } else {
            ByteArrayObjectField<T> field = ByteArrayObjectField.newInstance(ctx, byteArrayObject.length(), (ObjectProtoNode) propertyNode);
            field.read(JavaObjectUtils.newInstance(clazz));
            field.read(in);
            return field.value();
        }
    }

    @Override
    public ProtoNode newProtoNode(ProtoNode parent) {
        return ProtoNode.newObjectNode(parent);
    }
}
