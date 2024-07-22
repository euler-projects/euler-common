package org.eulerframework.common.http;

public interface RequestBody {
    ContentType getContentType();

    int getContentLength();

    Object getContent();
}
