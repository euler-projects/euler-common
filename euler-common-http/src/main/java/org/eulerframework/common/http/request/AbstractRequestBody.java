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

package org.eulerframework.common.http.request;

import org.eulerframework.common.http.ContentType;
import org.eulerframework.common.http.HttpRequest;
import org.eulerframework.common.http.RequestBody;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class AbstractRequestBody implements RequestBody {
    protected final static Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final ContentType contentType;

    /**
     * Creates a new instance of {@link HttpRequest} with default charset UTF-8.
     *
     * @param mimeType MIME type. It may not be {@code null} or empty. It may not contain
     *                 characters {@code <">, <;>, <,>} reserved by the HTTP specification.
     */
    public AbstractRequestBody(String mimeType) {
        this(mimeType, DEFAULT_CHARSET);
    }

    /**
     * Creates a new instance of {@link HttpRequest}.
     *
     * @param mimeType MIME type. It may not be {@code null} or empty. It may not contain
     *                 characters {@code <">, <;>, <,>} reserved by the HTTP specification.
     * @param charset  charset. It may not contain characters {@code <">, <;>, <,>} reserved by the HTTP
     *                 specification. This parameter is optional.
     */
    public AbstractRequestBody(String mimeType, Charset charset) {
        this(mimeType == null ? null : ContentType.create(mimeType, charset));
    }

    public AbstractRequestBody(ContentType contentType) {
        this.contentType = contentType;
    }

    @Override
    public ContentType getContentType() {
        return contentType;
    }

    @Override
    public int getContentLength() {
        return -1;
    }
}
