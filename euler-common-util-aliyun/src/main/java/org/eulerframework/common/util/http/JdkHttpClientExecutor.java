package org.eulerframework.common.util.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class JdkHttpClientExecutor implements HttpExecutor {
    @Override
    public String execute(HttpMethod httpMethod, URI uri, Map<String, String> headers, String body) throws IOException {
        System.out.println(uri.getScheme());
        System.out.println(uri.getHost());
        System.out.println(uri.getPath());
        System.out.println(uri.getQuery());
        System.out.println(uri.getRawQuery());
        System.out.println(uri.getUserInfo());
        System.out.println(uri.getAuthority());
        System.out.println(uri);

        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(uri);
        Optional.ofNullable(headers).orElse(Collections.emptyMap()).forEach(builder::header);
        switch (httpMethod) {
            case GET:
                builder.GET();
                break;
            case POST:
                builder.POST(this.stringBodyPublisher(body));
            case PUT:
                builder.PUT(this.stringBodyPublisher(body));
            case DELETE:
                builder.DELETE();
                break;
            default:
                throw new IllegalArgumentException("Unsupported http method: " + httpMethod);
        }

        HttpRequest httpRequest = builder.build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response;
        try {
            response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response.body();
    }

    private HttpRequest.BodyPublisher stringBodyPublisher(String str) {
        return Optional.ofNullable(str)
                .map(HttpRequest.BodyPublishers::ofString).
                orElse(HttpRequest.BodyPublishers.noBody());
    }
}
