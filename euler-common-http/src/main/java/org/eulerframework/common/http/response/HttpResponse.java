package org.eulerframework.common.http.response;

import org.eulerframework.common.http.ContentType;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface HttpResponse extends Closeable {
    int getStatus();

    ContentType getContentType();

    int getContentLength();

    Map<String, List<String>> getHeaders();

    InputStream getContent() throws IOException;

    void writeTo(OutputStream outputStream) throws IOException;
}
