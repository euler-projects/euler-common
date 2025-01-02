package org.eulerframework.proto.util;

import org.eulerframework.proto.annotation.ProtoProperty;

import java.lang.reflect.Field;

public class PropertyField {
    private final Field field;
    private final ProtoProperty annotation;
    private final int order;

    public PropertyField(Field field, ProtoProperty annotation) {
        this.field = field;
        this.annotation = annotation;
        this.order = annotation.order();
    }

    public Field getField() {
        return field;
    }

    public ProtoProperty getAnnotation() {
        return annotation;
    }

    public int getOrder() {
        return order;
    }
}
