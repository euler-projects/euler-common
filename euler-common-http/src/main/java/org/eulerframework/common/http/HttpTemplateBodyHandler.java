package org.eulerframework.common.http;

import java.io.InputStream;
import java.net.http.HttpResponse;

public class HttpTemplateBodyHandler implements java.net.http.HttpResponse.BodyHandler<InputStream> {

    private java.net.http.HttpResponse.ResponseInfo responseInfo;

    public HttpResponse.ResponseInfo getResponseInfo() {
        return responseInfo;
    }

    @Override
    public java.net.http.HttpResponse.BodySubscriber<InputStream> apply(java.net.http.HttpResponse.ResponseInfo responseInfo) {
        this.responseInfo = responseInfo;
        return java.net.http.HttpResponse.BodySubscribers.ofInputStream();
    }
}
