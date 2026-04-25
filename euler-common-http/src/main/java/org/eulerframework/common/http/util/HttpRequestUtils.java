package org.eulerframework.common.http.util;

import org.eulerframework.common.http.ContentType;
import org.eulerframework.common.http.HttpRequest;
import org.eulerframework.common.http.HttpVersion;
import org.eulerframework.common.http.RequestBody;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class HttpRequestUtils {
    private static final String MASKED_VALUE = "***";

    /**
     * Header names that are commonly considered sensitive and should be masked
     * when a redacted view of the request is required (e.g. for logging). The
     * comparison is performed in a case-insensitive manner, so the entries here
     * are normalized to lower case.
     */
    private static final Set<String> SENSITIVE_HEADER_NAMES = Set.of(
            "authorization",
            "proxy-authorization",
            "cookie",
            "set-cookie",
            "x-api-key",
            "x-auth-token",
            "x-csrf-token",
            "x-access-token"
    );

    /**
     * Query parameter names that are commonly considered sensitive and should
     * be masked when a redacted view of the request is required. The comparison
     * is performed in a case-insensitive manner.
     */
    private static final Set<String> SENSITIVE_QUERY_PARAM_NAMES = Set.of(
            "access_token",
            "refresh_token",
            "id_token",
            "code",
            "client_secret",
            "password",
            "token",
            "api_key",
            "apikey"
    );

    /**
     * Constructs an HTTP request message compliant with RFC 7230, without masking
     * any sensitive data.
     * <p>
     * Equivalent to {@link #formatRfc7230Message(HttpRequest, boolean)} with
     * {@code maskSensitive = false}.
     */
    public static String formatRfc7230Message(HttpRequest httpRequest) {
        return formatRfc7230Message(httpRequest, false);
    }

    /**
     * Constructs an HTTP request message compliant with RFC 7230.
     * <p>
     * Note that RFC 7230 is an HTTP/1.1 specification, so the HTTP-version field in the request line
     * will always be "HTTP/1.1", which may differ from the actual HTTP version used for transmission.
     *
     * @param httpRequest    the request to format; must not be {@code null}
     * @param maskSensitive  if {@code true}, values of commonly sensitive headers
     *                       (e.g. {@code Authorization}, {@code Cookie}) and
     *                       query parameters (e.g. {@code access_token},
     *                       {@code password}) will be replaced with
     *                       {@value #MASKED_VALUE} to prevent leaking secrets
     *                       through logs or other sinks
     */
    public static String formatRfc7230Message(HttpRequest httpRequest, boolean maskSensitive) {
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
        if ((rawQuery = uri.getRawQuery()) != null && !rawQuery.isEmpty()) {
            reqBuilder.append("?").append(maskSensitive ? maskQueryString(rawQuery) : rawQuery);
        }
        reqBuilder.append(" ");
        reqBuilder.append(HttpVersion.HTTP_1_1);
        reqBuilder.append("\r\n");
        reqBuilder.append("Host: ");
        reqBuilder.append(uri.getHost());
        reqBuilder.append("\r\n");

        Map<String, String> headers = new LinkedHashMap<>();
        httpRequest.getHeaders().forEach(header -> {
            String canonicalName = HttpRequestUtils.canonicalizeHeaderName(header.getName());
            String value = maskSensitive && isSensitiveHeader(canonicalName) ? MASKED_VALUE : header.getValue();
            headers.put(canonicalName, value);
        });


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
     * Checks whether the given canonicalized header name is considered sensitive.
     */
    private static boolean isSensitiveHeader(String canonicalHeaderName) {
        if (canonicalHeaderName == null || canonicalHeaderName.isEmpty()) {
            return false;
        }
        return SENSITIVE_HEADER_NAMES.contains(canonicalHeaderName.toLowerCase(Locale.ROOT));
    }

    /**
     * Masks values of sensitive parameters inside a raw URI query string. The
     * structure of the query (parameter order, separators, parameters without a
     * value) is preserved; only values of sensitive parameters are replaced.
     */
    private static String maskQueryString(String rawQuery) {
        StringBuilder sb = new StringBuilder(rawQuery.length());
        int start = 0;
        int length = rawQuery.length();
        for (int i = 0; i <= length; i++) {
            if (i == length || rawQuery.charAt(i) == '&') {
                appendMaskedQueryPair(sb, rawQuery, start, i);
                if (i < length) {
                    sb.append('&');
                }
                start = i + 1;
            }
        }
        return sb.toString();
    }

    private static void appendMaskedQueryPair(StringBuilder sb, String rawQuery, int start, int end) {
        if (start >= end) {
            return;
        }
        int eq = -1;
        for (int j = start; j < end; j++) {
            if (rawQuery.charAt(j) == '=') {
                eq = j;
                break;
            }
        }
        String name = eq < 0 ? rawQuery.substring(start, end) : rawQuery.substring(start, eq);
        if (SENSITIVE_QUERY_PARAM_NAMES.contains(name.toLowerCase(Locale.ROOT))) {
            sb.append(name).append('=').append(MASKED_VALUE);
        } else {
            sb.append(rawQuery, start, end);
        }
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
