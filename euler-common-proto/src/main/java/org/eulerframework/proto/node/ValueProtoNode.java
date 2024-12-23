package org.eulerframework.proto.node;

public class ValueProtoNode extends AbstractProtoNode implements ProtoNode {
    private Object value;

    ValueProtoNode(ProtoNode parent) {
        super(parent);
    }

    public Object value() {
        return value;
    }

    protected void setValue(Object value) {
        this.value = value;
    }
}
