package org.eulerframework.common.http;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public interface ResponseBody extends Closeable {
    ContentType getContentType();

    int getContentLength();

    Object getContent();

    void writeTo(OutputStream outputStream) throws IOException;
}
