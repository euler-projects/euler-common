package org.eulerframework.common.http.util;

import org.eulerframework.common.http.Header;
import org.eulerframework.common.http.HttpResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpResponseUtils {
    public static Optional<String> firstHeader(HttpResponse response, String name) {
        return header(response, name)
                .stream()
                .findFirst();
    }

    public static List<String> header(HttpResponse response, String name) {
        List<Header> headers = response.getHeaders();
        if (headers == null || headers.isEmpty()) {
            return List.of();
        }

        return headers.stream()
                .filter(header -> header.getName().equalsIgnoreCase(name))
                .map(Header::getValue)
                .collect(Collectors.toList());
    }
}
