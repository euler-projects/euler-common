package org.eulerframework.common.http;

import org.eulerframework.common.http.request.RequestBody;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public interface HttpTemplate {
    String execute(HttpMethod httpMethod, URI uri, Map<String, String> headers, RequestBody body) throws IOException;;
}
