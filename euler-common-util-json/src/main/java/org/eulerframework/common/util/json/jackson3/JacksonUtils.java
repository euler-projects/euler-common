/*
 * Copyright 2013-2026 the original author or authors.
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
package org.eulerframework.common.util.json.jackson3;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

public abstract class JacksonUtils {

    private final static JsonMapper OM;

    static {
        OM = JsonMapper.builder()
                .changeDefaultPropertyInclusion(value -> value
                        .withValueInclusion(JsonInclude.Include.NON_NULL)
                        .withContentInclusion(JsonInclude.Include.NON_NULL))
                .enable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                //.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                //.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(
                        DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS,
                        DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS
                )
                .addModule(new JsSafeModule())
                .build();
    }

    public static JsonMapper getDefaultObjectMapper() {
        return OM;
    }

    public static <T> T readValue(String value, Class<T> valueClass) {
        return OM.readValue(value, valueClass);
    }

    public static <T> T readValue(byte[] value, Class<T> valueClass) {
        return OM.readValue(value, valueClass);
    }

    public static String writeValueAsString(Object object) {
        return OM.writeValueAsString(object);
    }

    public static byte[] writeValueAsBytes(Object object) {
        return OM.writeValueAsBytes(object);
    }
}
