package org.eulerframework.proto.annotation;

public @interface StringProperty {
    LengthMode lengthMode() default LengthMode.FIXED;

    String lengthPrefixType() default "";

    byte[] separator() default {};
}
