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

import org.eulerframework.common.http.request.StringRequestBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.*;

public class JdkHttpClientTemplate implements HttpTemplate {
    public static final JdkHttpClientTemplate INSTANCE = new JdkHttpClientTemplate();

    @Override
    public HttpResponse execute(HttpRequest httpRequest) throws IOException {
        HttpMethod httpMethod = httpRequest.getHttpMethod();
        URI uri = httpRequest.getUri();
        List<Header> headers = httpRequest.getHeaders();
        RequestBody body = httpRequest.getBody();

        System.out.println(uri.getScheme());
        System.out.println(uri.getHost());
        System.out.println(uri.getPath());
        System.out.println(uri.getQuery());
        System.out.println(uri.getRawQuery());
        System.out.println(uri.getUserInfo());
        System.out.println(uri.getAuthority());
        System.out.println(uri);

        java.net.http.HttpRequest.Builder builder = java.net.http.HttpRequest.newBuilder().uri(uri);

        if (headers != null && !headers.isEmpty()) {
            for (Header header : headers) {
                builder.header(header.getName(), header.getValue());
            }
        }

        switch (httpMethod) {
            case GET:
                builder.GET();
                break;
            case POST:
                builder.POST(this.stringBodyPublisher(((StringRequestBody) body).getContent()));
                break;
            case PUT:
                builder.PUT(this.stringBodyPublisher(((StringRequestBody) body).getContent()));
                break;
            case DELETE:
                builder.DELETE();
                break;
            default:
                throw new IllegalArgumentException("Unsupported http method: " + httpMethod);
        }

        java.net.http.HttpRequest jdkHttpRequest = builder.build();
        HttpClient client = HttpClient.newHttpClient();
        java.net.http.HttpResponse<InputStream> response;
        try {
            response = client.send(jdkHttpRequest, BodyHandlers.ofInputStream());
        } catch (InterruptedException e) {
            throw ExceptionEraser.asRuntimeException(e);
        }

        return HttpResponse.newBuilder()
                .status(response.statusCode())
                .headers(response.headers().map())
                .content(response.body())
                .build();
    }

    private java.net.http.HttpRequest.BodyPublisher stringBodyPublisher(String str) {
        return Optional.ofNullable(str)
                .map(java.net.http.HttpRequest.BodyPublishers::ofString).
                orElse(java.net.http.HttpRequest.BodyPublishers.noBody());
    }
}
