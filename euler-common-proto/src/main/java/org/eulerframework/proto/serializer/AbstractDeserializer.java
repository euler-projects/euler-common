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

import org.eulerframework.proto.node.ProtoNode;
import org.eulerframework.proto.util.ProtoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public abstract class AbstractDeserializer implements Deserializer {
    @Override
    public <T> T read(InputStream in, Class<T> clazz) throws IOException {
        throw new UnsupportedEncodingException();
    }

    @Override
    public <T> T read(InputStream in, int length, Class<T> clazz) throws IOException {
        throw new UnsupportedEncodingException();
    }

    @Override
    public <T> T read(InputStream in, Class<T> clazz, ProtoNode propertyNode) throws IOException {
        return this.read(in, clazz);
    }

    @Override
    public <T> T read(InputStream in, int length, Class<T> clazz, ProtoNode propertyNode) throws IOException {
        return this.read(in, length, clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T read(InputStream in, ProtoUtils.PropertyField propertyField, ProtoNode propertyNode) throws IOException {
        return (T) this.read(in, propertyField.getField().getType(), propertyNode);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T read(InputStream in, int length, ProtoUtils.PropertyField propertyField, ProtoNode propertyNode) throws IOException {
        return (T) this.read(in, length, propertyField.getField().getType(), propertyNode);
    }

    @Override
    public ProtoNode newProtoNode(ProtoNode parent) {
        return ProtoNode.newValueNode(parent);
    }
}
