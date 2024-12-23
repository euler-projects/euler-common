package org.eulerframework.proto.node;

public class AbstractProtoNode implements ProtoNode {
    private final ProtoNode parent;

    public AbstractProtoNode(ProtoNode parent) {
        this.parent = parent;
    }

    @Override
    public ProtoNode parent() {
        return this.parent;
    }
}
