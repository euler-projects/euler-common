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
