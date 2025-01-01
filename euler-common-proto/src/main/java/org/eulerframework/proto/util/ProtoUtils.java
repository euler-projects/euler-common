package org.eulerframework.proto.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.eulerframework.proto.annotation.BitProperty;
import org.eulerframework.proto.annotation.ProtoProperty;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProtoUtils {
    public static List<PropertyField> getSortedPropertyFields(Class<?> clazz, int version) {
        return FieldUtils.getAllFieldsList(clazz).stream()
                .map(field -> {
                    ProtoProperty[] propertyAnnotations = field.getAnnotationsByType(ProtoProperty.class);
                    if (propertyAnnotations.length == 0) {
                        return null;
                    }
                    ProtoProperty matchedProperty = null;

                    for (ProtoProperty propertyAnnotation : propertyAnnotations) {
                        boolean matched = false;
                        if (propertyAnnotation.versions() == null || propertyAnnotation.versions().length == 0) {
                            matched = true;
                        } else if (ArrayUtils.contains(propertyAnnotation.versions(), version)) {
                            matched = true;
                        }
                        if (matched) {
                            if (matchedProperty != null) {
                                throw new IllegalArgumentException("The property " +
                                        clazz.getName() + "#" + field.getName()
                                        + " has more than one ProtoProperty annotations matched the version '"
                                        + version + "'.");
                            }
                            matchedProperty = propertyAnnotation;
                        }
                    }

                    if (matchedProperty == null) {
                        return null;
                    } else {
                        return new PropertyField(field, matchedProperty);
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(PropertyField::getOrder))
                .collect(Collectors.toList());
    }


    public static List<BitPropertyField> getSortedBitPropertyFields(Class<?> clazz, int version) {
        return FieldUtils.getAllFieldsList(clazz).stream()
                .map(field -> {
                    BitProperty[] propertyAnnotations = field.getAnnotationsByType(BitProperty.class);
                    if (propertyAnnotations.length == 0) {
                        return null;
                    }
                    BitProperty matchedProperty = null;

                    for (BitProperty propertyAnnotation : propertyAnnotations) {
                        boolean matched = false;
                        if (propertyAnnotation.versions() == null || propertyAnnotation.versions().length == 0) {
                            matched = true;
                        } else if (ArrayUtils.contains(propertyAnnotation.versions(), version)) {
                            matched = true;
                        }
                        if (matched) {
                            if (matchedProperty != null) {
                                throw new IllegalArgumentException("The property " +
                                        clazz.getName() + "#" + field.getName()
                                        + " has more than one ProtoProperty annotations matched the version '"
                                        + version + "'.");
                            }
                            matchedProperty = propertyAnnotation;
                        }
                    }

                    if (matchedProperty == null) {
                        return null;
                    } else {
                        return new BitPropertyField(field, matchedProperty);
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(BitPropertyField::getOffset))
                .collect(Collectors.toList());
    }

    public static class PropertyField {
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

    public static class BitPropertyField {
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
}
