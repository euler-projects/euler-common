package org.eulerframework.proto.annotation;

public @interface ProtoPropertyOption {

    String lengthMode() default LENGTH_MODE_FIXED;

    String lengthPrefixType() default "";

    byte[] separator() default {};

    String LENGTH_MODE_FIXED = "FIXED";
    String LENGTH_MODE_PREFIX = "PREFIX";
    String LENGTH_MODE_SEPARATOR = "SEPARATOR";
    String LENGTH_MODE_ALL_BYTES = "ALL_BYTES";
}
