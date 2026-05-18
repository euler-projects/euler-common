/*
 * Copyright 2013-present the original author or authors.
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

package org.eulerframework.common.http.request;

import org.eulerframework.common.http.ContentType;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * {@link org.eulerframework.common.http.RequestBody RequestBody} implementation for the
 * {@code application/x-www-form-urlencoded} media type.
 * <p>
 * Form parameters are percent-encoded according to the
 * {@code application/x-www-form-urlencoded} rules
 * (i.e. {@link URLEncoder#encode(String, Charset)} semantics) and joined with
 * {@code '&'}. Parameter insertion order is preserved and duplicate names are
 * allowed.
 * <p>
 * Extends {@link StringRequestBody} so existing transports such as
 * {@code JdkHttpClientTemplate} can serialize it without any additional
 * adaptation.
 */
public class UrlEncodedRequestBody extends StringRequestBody {

    private UrlEncodedRequestBody(String content, Charset charset) {
        super(content, ContentType.APPLICATION_FORM_URLENCODED.withCharset(charset));
    }

    /**
     * Build a body from the given parameter map using UTF-8.
     * <p>
     * The map's iteration order determines the output order; pass a
     * {@link java.util.LinkedHashMap LinkedHashMap} when ordering matters.
     */
    public static UrlEncodedRequestBody of(Map<String, String> params) {
        return newBuilder().addAll(params).build();
    }

    /**
     * Create a new builder using UTF-8 as the form encoding charset.
     */
    public static Builder newBuilder() {
        return new Builder(StandardCharsets.UTF_8);
    }

    /**
     * Create a new builder using the given charset as the form encoding charset.
     */
    public static Builder newBuilder(Charset charset) {
        return new Builder(charset);
    }

    public static final class Builder {
        private final Charset charset;
        private final List<String[]> params = new ArrayList<>();

        private Builder(Charset charset) {
            this.charset = charset == null ? StandardCharsets.UTF_8 : charset;
        }

        /**
         * Append a single form parameter. {@code null} values are encoded as the
         * empty string ({@code key=}).
         */
        public Builder add(String name, String value) {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Form parameter name must not be empty");
            }
            this.params.add(new String[]{name, value == null ? "" : value});
            return this;
        }

        /**
         * Append every entry from the given map, preserving its iteration order.
         */
        public Builder addAll(Map<String, String> params) {
            if (params != null) {
                params.forEach(this::add);
            }
            return this;
        }

        public UrlEncodedRequestBody build() {
            StringJoiner joiner = new StringJoiner("&");
            for (String[] kv : this.params) {
                joiner.add(URLEncoder.encode(kv[0], this.charset)
                        + "="
                        + URLEncoder.encode(kv[1], this.charset));
            }
            return new UrlEncodedRequestBody(joiner.toString(), this.charset);
        }
    }
}
