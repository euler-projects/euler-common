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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
    }

    public static ObjectMapper getDefaultObjectMapper() {
        return OM;
    }

    public static String toJsonStr(Object obj) throws JsonConvertException {
        try {
            return OM.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new JsonConvertException(e);
        }
    }

    public static <T> T readKeyValue(String jsonStr, String key, Class<T> keyValueClass) throws JsonConvertException {

        try {
            JsonNode root = OM.readTree(jsonStr);
            Iterator<Entry<String, JsonNode>> elements = root.fields();

            while (elements.hasNext()) {
                Entry<String, JsonNode> node = elements.next();

                if (node.getKey().equals(key))
                    return OM.readValue(node.getValue().toString(), keyValueClass);
            }

            return null;
        } catch (IOException e) {
            throw new JsonConvertException(e);
        }
    }

    public static <T> T toObject(String jsonStr, Class<T> valueClass) throws JsonConvertException {
        try {
            return OM.readValue(jsonStr, valueClass);
        } catch (IOException e) {
            throw new JsonConvertException(e);
        }
    }

    public static <T> ArrayList<T> toArrayList(String jsonStr, Class<T> valueClass) throws JsonConvertException {
        try {
            JavaType t = OM.getTypeFactory().constructCollectionType(ArrayList.class, valueClass);
            return OM.readValue(jsonStr, t);
        } catch (IOException e) {
            throw new JsonConvertException(e);
        }
    }

    public static <K, V> LinkedHashMap<K, V> toLinkedHashMap(String jsonStr, Class<K> keyClass, Class<V> valueClass)
            throws JsonConvertException {
        try {
            JavaType t = OM.getTypeFactory().constructMapLikeType(LinkedHashMap.class, keyClass, valueClass);
            return OM.readValue(jsonStr, t);
        } catch (IOException e) {
            throw new JsonConvertException(e);
        }
    }
}
