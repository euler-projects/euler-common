package org.eulerframework.common.http;

public enum HttpMethod {

    GET,

    HEAD,

    POST,

    PUT,

    PATCH,

    DELETE,

    OPTIONS,

    TRACE;

    public boolean supportBody() {
        return supportBody(this);
    }

    public static boolean supportBody(HttpMethod httpMethod) {
        return POST.equals(httpMethod) || PUT.equals(httpMethod) || PATCH.equals(httpMethod);
    }
}
