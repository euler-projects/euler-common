package org.eulerframework.common.http.request;

import org.eulerframework.common.http.ContentType;
import org.eulerframework.common.http.Header;
import org.eulerframework.common.http.HttpMethod;
import org.eulerframework.common.http.ReX;
import org.eulerframework.common.http.message.BasicHeader;
import org.eulerframework.common.http.util.Args;
import org.eulerframework.common.util.net.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Consumer;

public final class HttpRequest implements ReX {
    private final HttpMethod httpMethod;
    private final URI uri;
    private final List<Header> headers = new ArrayList<>();
    private ContentType contentType;
    private final RequestBody body;

    private HttpRequest(HttpMethod httpMethod, URI uri, List<Header> headers, RequestBody body) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.headers.addAll(headers);
        this.body = body;

        // check content-type header is match with the given body's content-type,
        // or add a content-type header if the given headers does not contain one.
        if (this.httpMethod.supportBody() && this.body != null) {
            this.contentType = this.body.getContentType();
            ContentType headerContentType = null;

            for (Header header : this.headers) {
                if (header.getName().equalsIgnoreCase(ContentType.HEADER_CONTENT_TYPE)) {
                    headerContentType = ContentType.parse(header.getValue());
                    break;
                }
            }

            if (headerContentType == null) {
                this.headers.add(new BasicHeader(ContentType.HEADER_CONTENT_TYPE, this.contentType.toString()));
            } else {
                if (!headerContentType.isSameMimeTypeAndCharset(this.contentType)) {
                    throw new IllegalArgumentException("Request body's content type '"
                            + this.contentType
                            + "' does not match with the content-type header '"
                            + headerContentType
                            + "' ");
                }
            }
        }
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public URI getUri() {
        return uri;
    }

    @Override
    public List<Header> getHeaders() {
        return headers;
    }

    @Override
    public ContentType getContentType() {
        return this.contentType;
    }

    public RequestBody getBody() {
        return body;
    }


    public static Builder of(HttpMethod httpMethod, URI uri) {
        return new UriSupportBuilder(httpMethod, uri);
    }

    public static Builder of(HttpMethod httpMethod, String url) {
        return new UriBuilderSupportBuilder(httpMethod, URIBuilder.of(url));
    }

    public static Builder get(String url) {
        return of(HttpMethod.GET, url);
    }

    public static Builder head(String url) {
        return of(HttpMethod.HEAD, url);
    }

    public static Builder post(String url) {
        return of(HttpMethod.POST, url);
    }

    public static Builder put(String url) {
        return of(HttpMethod.PUT, url);
    }

    public static Builder patch(String url) {
        return of(HttpMethod.PATCH, url);
    }

    public static Builder delete(String url) {
        return of(HttpMethod.DELETE, url);
    }

    public static Builder options(String url) {
        return of(HttpMethod.OPTIONS, url);
    }

    public static Builder trace(String url) {
        return of(HttpMethod.TRACE, url);
    }

    public static class UriBuilderSupportBuilder extends Builder {
        private final URIBuilder uriBuilder;

        private UriBuilderSupportBuilder(HttpMethod httpMethod, URIBuilder uriBuilder) {
            super(httpMethod);
            Args.notNull(uriBuilder, "Uri builder");
            this.uriBuilder = uriBuilder;
        }

        public UriBuilderSupportBuilder schema(String scheme) {
            this.uriBuilder.schema(scheme);
            return this;
        }

        public UriBuilderSupportBuilder host(String host) {
            this.uriBuilder.host(host);
            return this;
        }

        public UriBuilderSupportBuilder port(int port) {
            this.uriBuilder.port(port);
            return this;
        }

        public UriBuilderSupportBuilder path(String path) {
            this.uriBuilder.path(path);
            return this;
        }

        public UriBuilderSupportBuilder path(String template, Consumer<URIBuilder.PathTemplate> pathTemplateConsumer) {
            this.uriBuilder.path(template, pathTemplateConsumer);
            return this;
        }

        public UriBuilderSupportBuilder path(String template, Map<String, String> vars) {
            this.uriBuilder.path(template, vars);
            return this;
        }

        public UriBuilderSupportBuilder query(String key, String value) {
            this.uriBuilder.query(key, value);
            return this;
        }

        public UriBuilderSupportBuilder query(String query) {
            this.uriBuilder.query(query);
            return this;
        }

        @Override
        protected URI getUri() throws URISyntaxException {
            return this.uriBuilder.build();
        }
    }

    public static class UriSupportBuilder extends Builder {
        private final URI uri;

        private UriSupportBuilder(HttpMethod httpMethod, URI uri) {
            super(httpMethod);
            Args.notNull(uri, "Uri");
            this.uri = uri;
        }

        @Override
        protected URI getUri() {
            return this.uri;
        }
    }

    public static abstract class Builder {
        private final HttpMethod httpMethod;
        private final List<Header> headers = new ArrayList<>();
        private RequestBody body;

        private Builder(HttpMethod httpMethod) {
            Args.notNull(httpMethod, "Http method");
            this.httpMethod = httpMethod;
        }

        protected abstract URI getUri() throws URISyntaxException;

        public Builder header(String name, String value) {
            this.headers.add(new BasicHeader(name, value));
            return this;
        }

        public Builder headers(Map<String, List<String>> headers) {
            headers.forEach((name, values) -> values.forEach(value -> this.headers.add(new BasicHeader(name, value))));
            return this;
        }

        public Builder headers(List<Header> headers) {
            this.headers.addAll(headers);
            return this;
        }

        public Builder body(RequestBody body) {
            this.body = body;
            return this;
        }

        public HttpRequest build() throws URISyntaxException {

            return new HttpRequest(this.httpMethod, this.getUri(), this.headers, this.body);
        }
    }
}
