package org.eulerframework.common.http.request;

import org.eulerframework.common.http.ContentType;

import java.nio.charset.Charset;

public class StringRequestBody extends AbstractRequestBody {
    private final String content;

    public StringRequestBody(String content, ContentType contentType) {
        super(contentType);
        this.content = content;
    }

    public StringRequestBody(String content, String mimeType) {
        super(mimeType);
        this.content = content;
    }

    public StringRequestBody(String content, String mimeType, Charset charset) {
        super(mimeType, charset);
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }
}
