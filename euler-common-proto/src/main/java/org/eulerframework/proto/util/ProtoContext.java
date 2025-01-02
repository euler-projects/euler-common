package org.eulerframework.proto.util;

import org.eulerframework.proto.node.ProtoNode;

public class ProtoContext {
    private int version;

    private ProtoNode propertyNode;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public ProtoNode getPropertyNode() {
        return propertyNode;
    }

    public void setPropertyNode(ProtoNode propertyNode) {
        this.propertyNode = propertyNode;
    }
}
