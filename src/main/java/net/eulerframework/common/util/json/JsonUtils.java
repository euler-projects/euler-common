package net.eulerframework.common.util.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JsonUtils {
    
    private final static ObjectMapper OM;
    
    static {
        OM = new ObjectMapper();
        OM.setSerializationInclusion(Include.NON_NULL);
    }
    
    public static String toJsonStr(Object obj) throws JsonConvertException {
        try {
            return OM.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
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
    
    public static <K, V> LinkedHashMap<K, V> toLinkedHashMap(String jsonStr, Class<K> keyClass, Class<V> valueClass) throws JsonConvertException {
        try {
            JavaType t = OM.getTypeFactory().constructMapLikeType(LinkedHashMap.class, keyClass, valueClass);
            return OM.readValue(jsonStr, t);
        } catch (IOException e) {
            throw new JsonConvertException(e);
        }
    }
}
