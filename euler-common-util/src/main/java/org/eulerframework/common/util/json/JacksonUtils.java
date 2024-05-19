/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eulerframework.common.util.json;

import java.util.Iterator;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.exception.ExceptionUtils;

public abstract class JacksonUtils {

    private final static ObjectMapper OM;

    static {
        OM = new ObjectMapper();
        OM.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        OM.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OM.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, true);
        OM.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);

        OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OM.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);

        OM.registerModule(new JavaTimeModule());
        OM.registerModule(new JsSafeModule());
    }

    public static ObjectMapper getDefaultObjectMapper() {
        return OM;
    }

    public static <T> T readValue(String jsonStr, Class<T> valueClass) {
        try {
            return OM.readValue(jsonStr, valueClass);
        } catch (JsonProcessingException e) {
            throw ExceptionUtils.<RuntimeException>rethrow(e);
        }
    }

    public static String writeValueAsString(Object object) {
        try {
            return OM.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw ExceptionUtils.<RuntimeException>rethrow(e);
        }
    }

    public static <T> T readKeyValue(String jsonStr, String key, Class<T> keyValueClass) {

        try {
            JsonNode root = OM.readTree(jsonStr);
            Iterator<Entry<String, JsonNode>> elements = root.fields();

            while (elements.hasNext()) {
                Entry<String, JsonNode> node = elements.next();

                if (node.getKey().equals(key))
                    return OM.readValue(node.getValue().toString(), keyValueClass);
            }

            return null;
        } catch (JsonProcessingException e) {
            throw ExceptionUtils.<RuntimeException>rethrow(e);
        }
    }
}
