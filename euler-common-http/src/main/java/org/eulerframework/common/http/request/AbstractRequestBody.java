package org.eulerframework.common.http.request;

import org.eulerframework.common.http.ContentType;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class AbstractRequestBody implements RequestBody {
    protected final static Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final ContentType contentType;

    /**
     * Creates a new instance of {@link RequestBody} with default charset UTF-8.
     *
     * @param mimeType MIME type. It may not be {@code null} or empty. It may not contain
     *                 characters {@code <">, <;>, <,>} reserved by the HTTP specification.
     */
    public AbstractRequestBody(String mimeType) {
        this(mimeType, DEFAULT_CHARSET);
    }

    /**
     * Creates a new instance of {@link RequestBody}.
     *
     * @param mimeType MIME type. It may not be {@code null} or empty. It may not contain
     *                 characters {@code <">, <;>, <,>} reserved by the HTTP specification.
     * @param charset  charset. It may not contain characters {@code <">, <;>, <,>} reserved by the HTTP
     *                 specification. This parameter is optional.
     */
    public AbstractRequestBody(String mimeType, Charset charset) {
        if (mimeType != null) {
            this.contentType = new ContentType(mimeType, charset);
        } else {
            this.contentType = null;
        }
    }

    @Override
    public ContentType getContentType() {
        return contentType;
    }
}
