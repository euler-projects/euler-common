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

package org.eulerframework.proto.node;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class ObjectProtoNode extends ValueProtoNode implements ProtoNode {

    private final Map<String /* fieldName */, ProtoNode> properties = new LinkedHashMap<>();

    ObjectProtoNode(ProtoNode parent) {
        super(parent);
    }

    public Map<String, ProtoNode> getProperties() {
        return properties;
    }

    public PropertyNodeFuture addProperty(String propertyName, Function<ProtoNode, ProtoNode> nodeSupplier) {
        ProtoNode node = nodeSupplier.apply(this);
        this.properties.put(propertyName, node);
        return new PropertyNodeFuture() {
            @Override
            public ProtoNode get() {
                return node;
            }

            @Override
            public void setValue(Object value) {
                if (ValueProtoNode.class.isAssignableFrom(node.getClass())) {
                    ((ValueProtoNode) node).setValue(value);
                } else {
                    throw new IllegalArgumentException("Only ValueProtoNode cloud set value.");
                }
            }
        };
    }

    public interface PropertyNodeFuture {
        ProtoNode get();

        void setValue(Object value);
    }
}
