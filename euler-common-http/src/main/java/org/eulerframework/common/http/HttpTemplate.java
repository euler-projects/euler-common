package org.eulerframework.common.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public interface HttpTemplate {
    default HttpResponse execute(HttpMethod httpMethod, URI uri, List<Header> headers, RequestBody body) throws IOException {
        try {
            return this.execute(HttpRequest.of(httpMethod, uri)
                    .headers(headers)
                    .body(body)
                    .build());
        } catch (URISyntaxException e) {
            throw ExceptionEraser.asRuntimeException(e);
        }
    }

    HttpResponse execute(HttpRequest httpRequest) throws IOException;
}
