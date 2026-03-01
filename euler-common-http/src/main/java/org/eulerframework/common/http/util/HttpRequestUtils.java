package org.eulerframework.common.http.util;

import org.eulerframework.common.http.ContentType;
import org.eulerframework.common.http.HttpRequest;
import org.eulerframework.common.http.HttpVersion;
import org.eulerframework.common.http.RequestBody;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class HttpRequestUtils {
    /**
     * Constructs an HTTP request message compliant with RFC 7230.
     * <p>
     * Note that RFC 7230 is an HTTP/1.1 specification, so the HTTP-version field in the request line
     * will always be "HTTP/1.1", which may differ from the actual HTTP version used for transmission.
     */
    public static String formatRfc7230Message(HttpRequest httpRequest) {
        URI uri = httpRequest.getUri();
        String path = uri.getPath();
        if (path == null || path.isEmpty()) {
            path = "/";
        }
        StringBuilder reqBuilder = new StringBuilder();
        reqBuilder.append(httpRequest.getMethod());
        reqBuilder.append(" ");
        reqBuilder.append(path);
        String rawQuery;
        if ((rawQuery = uri.getQuery()) != null && !rawQuery.isEmpty()) {
            reqBuilder.append("?").append(rawQuery);
        }
        reqBuilder.append(" ");
        reqBuilder.append(HttpVersion.HTTP_1_1);
        reqBuilder.append("\r\n");
        reqBuilder.append("Host: ");
        reqBuilder.append(uri.getHost());
        reqBuilder.append("\r\n");

        Map<String, String> headers = new LinkedHashMap<>();
        httpRequest.getHeaders().forEach(header -> headers.put(HttpRequestUtils.canonicalizeHeaderName(header.getName()), header.getValue()));


        RequestBody body = httpRequest.getBody();
        if (body != null) {
            ContentType contentType;
            int contentLength;
            if ((contentType = body.getContentType()) != null) {
                headers.put("Content-Type", contentType.toString());
            }

            if ((contentLength = body.getContentLength()) > -1) {
                headers.put("Content-Length", String.valueOf(contentLength));
            }
        }

        headers.forEach((key, value) -> reqBuilder.append(key).append(": ").append(value).append("\r\n"));

        Object content = Optional.ofNullable(body).map(RequestBody::getContent).orElse(null);
        if (content != null) {
            reqBuilder.append("\r\n");
            reqBuilder.append(content);
        }

        return reqBuilder.toString();
    }

    /**
     * Canonicalizes an HTTP header name, converting it to the standard format where each word
     * separated by hyphens is capitalized (e.g., "content-type" becomes "Content-Type").
     *
     * @param headerName the raw HTTP header name; may be null or empty
     * @return the canonicalized HTTP header name, or the original value if null or empty
     */
    private static String canonicalizeHeaderName(String headerName) {
        if (headerName == null || headerName.isEmpty()) {
            return headerName;
        }

        boolean toUpperCase = true;
        StringBuilder result = new StringBuilder();
        for (char c : headerName.toCharArray()) {
            if (toUpperCase) {
                result.append(Character.toUpperCase(c));
                toUpperCase = false;
                continue;
            }

            result.append(c);
            if (c == '-') {
                toUpperCase = true;
            }
        }
        return result.toString();
    }
}
