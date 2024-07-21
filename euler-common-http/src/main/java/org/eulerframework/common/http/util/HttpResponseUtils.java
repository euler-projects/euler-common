package org.eulerframework.common.http.util;

import org.eulerframework.common.http.response.HttpResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpResponseUtils {
    public static Optional<String> firstHeader(HttpResponse response, String name) {
        return header(response, name)
                .stream()
                .findFirst();
    }

    public static  List<String> header(HttpResponse response, String name) {
        Map<String, List<String>> headers = response.getHeaders();
        if (headers == null || headers.isEmpty()) {
            return List.of();
        }

        return headers.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(name))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(List.of());
    }
}
