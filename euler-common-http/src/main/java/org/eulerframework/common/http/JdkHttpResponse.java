package org.eulerframework.common.http;

import org.eulerframework.common.http.response.HttpResponse;
import org.eulerframework.common.http.util.HttpResponseUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class JdkHttpResponse implements HttpResponse {
    private static final int OUTPUT_BUFFER_SIZE = 4096;

    private final Map<String,List<String>> headers;
    //private final ContentType contentType;
    private final long length;
    private final java.net.http.HttpResponse<InputStream> jdkHttpResponse;
    private final InputStream content;

    public JdkHttpResponse(java.net.http.HttpResponse<InputStream> jdkHttpResponse) {
        this.jdkHttpResponse = jdkHttpResponse;
        this.content = jdkHttpResponse.body();
        this.headers = jdkHttpResponse.headers().map();
        String contentType = HttpResponseUtils.firstHeader(this, "content-type").orElse(null);
        //this.contentType  = new ContentType()

        this.length = -1;
    }

    @Override
    public void close() throws IOException {
        if (this.content != null) {
            this.content.close();
        }
    }

    @Override
    public int getStatus() {
        return this.jdkHttpResponse.statusCode();
    }

    @Override
    public ContentType getContentType() {
        return null;
    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }

    @Override
    public InputStream getContent() {
        return content;
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        if (outputStream == null) {
            throw new IllegalArgumentException("Output stream must not be null.");
        }
        try (final InputStream in = this.content) {
            final byte[] buffer = new byte[OUTPUT_BUFFER_SIZE];
            int l;
            if (this.length < 0) {
                // consume until EOF
                while ((l = in.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, l);
                }
            } else {
                // consume no more than length
                long remaining = this.length;
                while (remaining > 0) {
                    l = in.read(buffer, 0, (int) Math.min(OUTPUT_BUFFER_SIZE, remaining));
                    if (l == -1) {
                        break;
                    }
                    outputStream.write(buffer, 0, l);
                    remaining -= l;
                }
            }
        }
    }
}
