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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public interface HttpTemplate {
    default HttpResponse execute(HttpMethod httpMethod, URI uri, List<Header> headers, RequestBody body) throws IOException {
        try {
            return this.execute(HttpRequest.of(httpMethod, uri)
                    .headers(headers)
                    .body(body)
                    .build());
        } catch (URISyntaxException e) {
            throw ExceptionEraser.asRuntimeException(e);
        }
    }

    HttpResponse execute(HttpRequest httpRequest) throws IOException;
}
