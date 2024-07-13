package org.eulerframework.common.util.http;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public interface HttpExecutor {
    String execute(HttpMethod httpMethod, URI uri, Map<String, String> headers, String body) throws IOException;
}
