package org.eulerframework.proto.node;

public interface ProtoNode {
    static ObjectProtoNode newRootNode() {
        return new ObjectProtoNode(null);
    }

    static ValueProtoNode newValueNode(ProtoNode parent) {
        return new ValueProtoNode(parent);
    }

    static ObjectProtoNode newObjectNode(ProtoNode parent) {
        return new ObjectProtoNode(parent);
    }

    ProtoNode parent();
}
