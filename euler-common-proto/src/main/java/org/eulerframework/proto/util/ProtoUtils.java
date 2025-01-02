package org.eulerframework.proto.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.eulerframework.proto.annotation.BitProperty;
import org.eulerframework.proto.annotation.ProtoProperty;

import java.lang.annotation.Annotation;
import java.util.Arrays;
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

                    @SuppressWarnings("unchecked")
                    AnnotationWithVersion<ProtoProperty>[] annotations = Arrays.stream(propertyAnnotations)
                            .map(a -> new AnnotationWithVersion<>(a, a.version()))
                            .toArray(AnnotationWithVersion[]::new);

                    ProtoProperty matchedProperty;
                    try {
                        matchedProperty = getMatchedAnnotation(annotations, version);
                    } catch (DuplicatedVersionException e) {
                        throw new IllegalArgumentException("The property " +
                                clazz.getName() + "#" + field.getName()
                                + " has more than one ProtoProperty annotations matched the version '"
                                + version + "'.");
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

                    @SuppressWarnings("unchecked")
                    AnnotationWithVersion<BitProperty>[] annotations = Arrays.stream(propertyAnnotations)
                            .map(a -> new AnnotationWithVersion<>(a, a.version()))
                            .toArray(AnnotationWithVersion[]::new);

                    BitProperty matchedProperty;
                    try {
                        matchedProperty = getMatchedAnnotation(annotations, version);
                    } catch (DuplicatedVersionException e) {
                        throw new IllegalArgumentException("The property " +
                                clazz.getName() + "#" + field.getName()
                                + " has more than one BitProperty annotations matched the version '"
                                + version + "'.");
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

    private static <A extends Annotation> A getMatchedAnnotation(AnnotationWithVersion<A>[] annotations, int exceptedVersion) throws DuplicatedVersionException {
        if (annotations.length == 0) {
            return null;
        }

        int matchedPropertyScore = -1;
        AnnotationWithVersion<A> matchedProperty = null;

        for (AnnotationWithVersion<A> propertyAnnotation : annotations) {
            int matchedScore = 0;
            if (propertyAnnotation.getVersion() == null || propertyAnnotation.getVersion().length == 0) {
                // 没有版本标记, 则跟所有版本都匹配, 但是匹配度最低
                if (matchedScore > matchedPropertyScore) {
                    matchedPropertyScore = matchedScore;
                    matchedProperty = propertyAnnotation;
                } else if (matchedScore == matchedPropertyScore) {
                    throw new DuplicatedVersionException();
                }
                continue;
            }

            if (propertyAnnotation.getVersion().length == 1 && propertyAnnotation.getVersion()[0] <= exceptedVersion) {
                // 有版本标记, 且只有一个, 则跟所有大于它的版本匹配,
                // 若版本刚好相等, 匹配度最高
                // 若版本标记小于期望版本, 则随着版本差距匹配度逐次降低
                matchedScore = Integer.MAX_VALUE - (exceptedVersion - propertyAnnotation.getVersion()[0]);
                if (matchedScore > matchedPropertyScore) {
                    matchedPropertyScore = matchedScore;
                    matchedProperty = propertyAnnotation;
                } else if (matchedScore == matchedPropertyScore) {
                    throw new DuplicatedVersionException();
                }
                continue;
            }

            // 若指定了多个版本标记, 若可以精确匹配, 则匹配度最高, 否则按较高的版本匹配
            if (ArrayUtils.contains(propertyAnnotation.getVersion(), exceptedVersion)) {
                matchedScore = Integer.MAX_VALUE;
                if (matchedScore > matchedPropertyScore) {
                    matchedPropertyScore = matchedScore;
                    matchedProperty = propertyAnnotation;
                } else {
                    throw new DuplicatedVersionException();
                }
            }

            int maxVersion = Arrays.stream(propertyAnnotation.getVersion()).max().getAsInt();
            if (exceptedVersion > maxVersion) {
                matchedScore = Integer.MAX_VALUE - (exceptedVersion - maxVersion);
                if (matchedScore > matchedPropertyScore) {
                    matchedPropertyScore = matchedScore;
                    matchedProperty = propertyAnnotation;
                } else if (matchedScore == matchedPropertyScore) {
                    throw new DuplicatedVersionException();
                }
            }
        }
        return matchedProperty == null ? null : matchedProperty.getAnnotation();
    }

    private static class AnnotationWithVersion<A extends Annotation> {
        private final A annotation;
        private final int[] version;

        public AnnotationWithVersion(A annotation, int[] version) {
            this.annotation = annotation;
            this.version = version;
        }

        public A getAnnotation() {
            return annotation;
        }

        public int[] getVersion() {
            return version;
        }
    }
}
