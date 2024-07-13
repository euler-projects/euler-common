package org.eulerframework.common.util.http;

public enum HttpMethod {

    GET,

    HEAD,

    POST,

    PUT,

    PATCH,

    DELETE,

    OPTIONS,

    TRACE;

    public static boolean supportBody(HttpMethod httpMethod) {
        return POST.equals(httpMethod) || PUT.equals(httpMethod) || PATCH.equals(httpMethod);
    }
}
