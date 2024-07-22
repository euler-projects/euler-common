package org.eulerframework.common.http;

import org.eulerframework.common.http.request.StringRequestBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.*;

public class JdkHttpClientTemplate implements HttpTemplate {
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
