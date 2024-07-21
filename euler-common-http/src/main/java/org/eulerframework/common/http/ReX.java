package org.eulerframework.common.http;

import java.util.List;

public interface ReX {
    List<Header> getHeaders();
    ContentType getContentType();
}
