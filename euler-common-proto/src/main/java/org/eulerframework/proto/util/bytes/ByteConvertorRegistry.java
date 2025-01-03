package org.eulerframework.proto.util.bytes;

import java.util.HashMap;
import java.util.Map;

public class ByteConvertorRegistry {
    private final Map<Class<?>, BytesConvertor<?>> convertors = new HashMap<>();

    ByteConvertorRegistry() {
        System.out.println("Create new byte convertor registry.");
    }

    public ByteConvertorRegistry addConvertor(Class<?> clazz, BytesConvertor<?> convertor) {
        this.convertors.put(clazz, convertor);
        return this;
    }

    public ByteConvertorRegistry addConvertor(ByteConvertorRegistry registry) {
        registry.convertors.forEach(this::addConvertor);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> BytesConvertor<T> getConvertor(Class<T> clazz) {
        return (BytesConvertor<T>) convertors.get(clazz);
    }
}
