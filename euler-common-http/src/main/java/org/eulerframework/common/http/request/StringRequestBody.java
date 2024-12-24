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

import java.nio.charset.Charset;

public class StringRequestBody extends AbstractRequestBody {
    private final String content;

    public StringRequestBody(String content, ContentType contentType) {
        super(contentType);
        this.content = content;
    }

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
