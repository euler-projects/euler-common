package org.eulerframework.proto.annotation;

public @interface StringProperty {
    String lengthMode() default LengthMode.FIXED;

    String lengthPrefixType() default "";

    byte[] separator() default {};
}
