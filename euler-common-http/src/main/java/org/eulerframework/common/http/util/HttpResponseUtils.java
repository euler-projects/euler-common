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

package org.eulerframework.common.http.util;

import org.eulerframework.common.http.Header;
import org.eulerframework.common.http.HttpResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpResponseUtils {
    public static Optional<String> firstHeader(HttpResponse response, String name) {
        return header(response, name)
                .stream()
                .findFirst();
    }

    public static List<String> header(HttpResponse response, String name) {
        List<Header> headers = response.getHeaders();
        if (headers == null || headers.isEmpty()) {
            return List.of();
        }

        return headers.stream()
                .filter(header -> header.getName().equalsIgnoreCase(name))
                .map(Header::getValue)
                .collect(Collectors.toList());
    }
}
