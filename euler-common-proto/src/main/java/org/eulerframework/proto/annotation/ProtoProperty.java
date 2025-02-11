/*
 * Copyright 2013-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eulerframework.proto.annotation;

import org.eulerframework.proto.util.ProtoCharset;

import java.lang.annotation.*;

@Target(value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(ProtoProperty.MultiVersion.class)
public @interface ProtoProperty {
    String type();

    int length() default -1;

    int order();

    int[] version() default {};

    /**
     * The charset for a {@link CharSequence} field.
     * <p>
     * If the field type is not a {@link CharSequence}, this attribute will be ignored.
     */
    String charset() default ProtoCharset.UTF_8;

    ProtoPropertyOption option() default @ProtoPropertyOption();

    @Target({ElementType.FIELD})
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @interface MultiVersion {
        ProtoProperty[] value();
    }

}
