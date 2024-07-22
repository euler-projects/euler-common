package org.eulerframework.common.http.response;

import org.eulerframework.common.http.ContentType;
import org.eulerframework.common.http.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InputStreamResponseBody implements ResponseBody {
    private static final int OUTPUT_BUFFER_SIZE = 4096;

    private final ContentType contentType;
    private final int length;
    private final InputStream content;

    public InputStreamResponseBody(ContentType contentType, int length, InputStream content) {
        this.contentType = contentType;
        this.length = length;
        this.content = content;
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

    @Override
    public ContentType getContentType() {
        return contentType;
    }

    @Override
    public int getContentLength() {
        return length;
    }

    @Override
    public InputStream getContent() {
        return content;
    }

    @Override
    public void close() throws IOException {
        if (this.content != null) {
            this.content.close();
        }
    }

    public static final class InputStreamResponseBodyBuilder {
        private ContentType contentType;
        private int length;
        private InputStream content;

        public InputStreamResponseBodyBuilder contentType(ContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public InputStreamResponseBodyBuilder length(int length) {
            this.length = length;
            return this;
        }

        public InputStreamResponseBodyBuilder content(InputStream content) {
            this.content = content;
            return this;
        }

        public InputStreamResponseBody build() {
            return new InputStreamResponseBody(this.contentType, this.length, this.content);
        }
    }
}
