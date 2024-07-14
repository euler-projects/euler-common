package org.eulerframework.common.http.request;

import org.eulerframework.common.http.ContentType;

public interface RequestBody {
    ContentType getContentType();
    Object getContent();
}
