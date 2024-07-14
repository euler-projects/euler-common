package org.eulerframework.common.http;

import org.eulerframework.common.http.message.BasicHeaderValueFormatter;
import org.eulerframework.common.http.util.CharArrayBuffer;

import java.nio.charset.Charset;

public class ContentType {
    private final String mimeType;
    private final Charset charset;
    private final Param[] params;

    /**
     * Creates a new instance of {@link ContentType}.
     *
     * @param mimeType MIME type. It may not be {@code null} or empty. It may not contain
     *                 characters {@code <">, <;>, <,>} reserved by the HTTP specification.
     * @param charset  charset. It may not contain characters {@code <">, <;>, <,>} reserved by the HTTP
     *                 specification. This parameter is optional.
     */
    public ContentType(final String mimeType, final Charset charset) {
        this.mimeType = mimeType;
        this.charset = charset;
        this.params = null;
    }

    public ContentType(String mimeType, Charset charset, Param[] params) {
        this.mimeType = mimeType;
        this.charset = charset;
        this.params = params;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public Charset getCharset() {
        return this.charset;
    }

    /**
     * Gets this Charset if it's non-null, otherwise, return the given {@code defaultCharset}.
     *
     * @param defaultCharset A default Charset.
     * @return this Charset if it's non-null, or the given {@code defaultCharset}.
     * @since 5.2
     */
    public Charset getCharset(final Charset defaultCharset) {
        return this.charset != null ? this.charset : defaultCharset;
    }

    /**
     * @since 4.3
     */
    public String getParameter(final String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Parameter name must not be empty");
        }
        if (this.params == null) {
            return null;
        }
        for (final Param param : this.params) {
            if (param.getName().equalsIgnoreCase(name)) {
                return param.getValue();
            }
        }
        return null;
    }

    /**
     * Generates textual representation of this content type which can be used as the value
     * of a {@code Content-Type} header.
     */
    @Override
    public String toString() {
        final CharArrayBuffer buf = new CharArrayBuffer(64);
        buf.append(this.mimeType);
        if (this.params != null) {
            buf.append("; ");
            BasicHeaderValueFormatter.INSTANCE.formatParameters(buf, this.params, false);
        } else if (this.charset != null) {
            buf.append("; charset=");
            buf.append(this.charset.name());
        }
        return buf.toString();
    }
}
