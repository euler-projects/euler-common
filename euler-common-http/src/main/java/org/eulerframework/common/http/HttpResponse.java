/*
 * Copyright 2013-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eulerframework.common.http;

import org.eulerframework.common.http.message.BasicHeader;
import org.eulerframework.common.http.response.InputStreamResponseBody;
import org.eulerframework.common.http.util.HttpResponseUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface HttpResponse extends Closeable {
    int getStatus();

    List<Header> getHeaders();

    ResponseBody getBody() throws IOException;

    static Builder newBuilder() {
        return new Builder();
    }

    class Builder {
        private int status;
        private final List<Header> headers = new ArrayList<>();
        private final InputStreamResponseBody.InputStreamResponseBodyBuilder bodyBuilder = new InputStreamResponseBody.InputStreamResponseBodyBuilder();

        private Builder() {
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder header(String name, String value) {
            this.headers.add(new BasicHeader(name, value));
            return this;
        }

        public Builder headers(Map<String, List<String>> headers) {
            headers.forEach((name, values) -> values.forEach(value -> this.headers.add(new BasicHeader(name, value))));
            return this;
        }

        public Builder headers(List<Header> headers) {
            this.headers.addAll(headers);
            return this;
        }

        public Builder content(InputStream in) {
            this.bodyBuilder.content(in);
            return this;
        }

        public HttpResponse build() {
            return new BasicHttpResponse(this.status, this.headers, this.bodyBuilder);
        }
    }

    final class BasicHttpResponse implements HttpResponse {
        private final int status;
        private final List<Header> headers = new ArrayList<>();
        private final ResponseBody responseBody;

        public BasicHttpResponse(int status, List<Header> headers, InputStreamResponseBody.InputStreamResponseBodyBuilder bodyBuilder) {
            this.status = status;
            this.headers.addAll(headers);
            this.responseBody = bodyBuilder
                    .contentType(HttpResponseUtils.firstHeader(this, "content-type").map(ContentType::parse).orElse(null))
                    .length(HttpResponseUtils.firstHeader(this, "content-length").map(Integer::parseInt).orElse(-1))
                    .build();
        }

        @Override
        public int getStatus() {
            return status;
        }

        @Override
        public List<Header> getHeaders() {
            return headers;
        }

        @Override
        public ResponseBody getBody() throws IOException {
            return responseBody;
        }

        @Override
        public void close() throws IOException {
            if (this.responseBody != null) {
                this.responseBody.close();
            }
        }
    }
}
