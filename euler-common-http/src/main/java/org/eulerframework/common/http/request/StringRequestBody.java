package org.eulerframework.common.http.request;

import java.nio.charset.Charset;

public class StringRequestBody extends AbstractRequestBody {
    private final String content;

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
