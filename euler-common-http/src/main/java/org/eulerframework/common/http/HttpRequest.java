/*
 * Copyright 2013-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eulerframework.common.http;

import org.eulerframework.common.http.message.BasicHeader;
import org.eulerframework.common.http.util.Args;
import org.eulerframework.common.util.net.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Consumer;

public interface HttpRequest {

    HttpMethod getHttpMethod();

    URI getUri();

    List<Header> getHeaders();

    RequestBody getBody() throws IOException;

    static Builder of(HttpMethod httpMethod, URI uri) {
        return new UriSupportBuilder(httpMethod, uri);
    }

    static Builder of(HttpMethod httpMethod, String url) {
        return new UriBuilderSupportBuilder(httpMethod, URIBuilder.of(url));
    }

    static Builder get(String url) {
        return of(HttpMethod.GET, url);
    }

    static Builder head(String url) {
        return of(HttpMethod.HEAD, url);
    }

    static Builder post(String url) {
        return of(HttpMethod.POST, url);
    }

    static Builder put(String url) {
        return of(HttpMethod.PUT, url);
    }

    static Builder patch(String url) {
        return of(HttpMethod.PATCH, url);
    }

    static Builder delete(String url) {
        return of(HttpMethod.DELETE, url);
    }

    static Builder options(String url) {
        return of(HttpMethod.OPTIONS, url);
    }

    static Builder trace(String url) {
        return of(HttpMethod.TRACE, url);
    }

    class UriBuilderSupportBuilder extends Builder {
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

    class UriSupportBuilder extends Builder {
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

    abstract class Builder {
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
            return new BasicHttpRequest(this.httpMethod, this.getUri(), this.headers, this.body);
        }
    }

    final class BasicHttpRequest implements HttpRequest {
        private final HttpMethod httpMethod;
        private final URI uri;
        private final List<Header> headers = new ArrayList<>();
        private final RequestBody body;

        private BasicHttpRequest(HttpMethod httpMethod, URI uri, List<Header> headers, RequestBody body) {
            this.httpMethod = httpMethod;
            this.uri = uri;
            this.headers.addAll(headers);
            this.body = body;

            // check content-type header is match with the given body's content-type,
            // or add a content-type header if the given headers does not contain one.
            if (this.httpMethod.supportBody() && this.body != null) {
                ContentType bodyContentType = this.body.getContentType();
                ContentType headerContentType = null;

                for (Header header : this.headers) {
                    if (header.getName().equalsIgnoreCase(ContentType.HEADER_CONTENT_TYPE)) {
                        headerContentType = ContentType.parse(header.getValue());
                        break;
                    }
                }

                if (headerContentType == null) {
                    this.headers.add(new BasicHeader(ContentType.HEADER_CONTENT_TYPE, bodyContentType.toString()));
                } else {
                    if (!headerContentType.isSameMimeTypeAndCharset(bodyContentType)) {
                        throw new IllegalArgumentException("Request body's content type '"
                                + bodyContentType
                                + "' does not match with the content-type header '"
                                + headerContentType
                                + "' ");
                    }
                }
            }
        }

        @Override
        public HttpMethod getHttpMethod() {
            return httpMethod;
        }

        @Override
        public URI getUri() {
            return uri;
        }

        @Override
        public List<Header> getHeaders() {
            return headers;
        }

        @Override
        public RequestBody getBody() {
            return body;
        }
    }
}
