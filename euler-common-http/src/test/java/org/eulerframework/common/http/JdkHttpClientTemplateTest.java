package org.eulerframework.common.http;

import org.eulerframework.common.http.request.StringRequestBody;
import org.eulerframework.common.http.response.InputStreamResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

class JdkHttpClientTemplateTest {
    public static void main(String[] args) throws URISyntaxException, IOException {
        try (HttpResponse response = JdkHttpClientTemplate.INSTANCE.execute(HttpRequest.post("http://example.com/abc?a=b")
                .header("X-abc", "abc")
                .body(new StringRequestBody("abc", ContentType.TEXT_PLAIN)).build());
             InputStream in = ((InputStreamResponseBody) response.getBody()).getContent()) {
            byte[] bytes = in.readAllBytes();
            System.out.println(new String(bytes));
        }
    }
}