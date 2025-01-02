package org.eulerframework.proto.util;

import org.eulerframework.proto.annotation.BitProperty;

import java.lang.reflect.Field;

public class BitPropertyField {
    private final Field field;
    private final BitProperty annotation;
    private final int offset;

    public BitPropertyField(Field field, BitProperty annotation) {
        this.field = field;
        this.annotation = annotation;
        this.offset = annotation.offset();
    }

    public Field getField() {
        return field;
    }

    public BitProperty getAnnotation() {
        return annotation;
    }

    public int getOffset() {
        return offset;
    }
}
