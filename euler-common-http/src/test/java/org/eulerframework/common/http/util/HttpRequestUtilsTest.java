/*
 * Copyright 2013-2026 the original author or authors.
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

import org.eulerframework.common.http.HttpMethod;
import org.eulerframework.common.http.HttpRequest;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpRequestUtilsTest {

    private static HttpRequest buildRequest(String rawUri) throws URISyntaxException {
        return HttpRequest.of(HttpMethod.GET, new URI(rawUri))
                .header("Authorization", "Bearer secret-token-value")
                .header("cookie", "session=abc; theme=dark")
                .header("X-Api-Key", "k-12345")
                .header("Accept", "application/json")
                .build();
    }

    @Test
    void formatRfc7230Message_defaultOverload_doesNotMask() throws URISyntaxException {
        HttpRequest request = buildRequest(
                "https://api.example.com/v1/users?access_token=ATV&keep=ok&password=p%40ss");

        String message = HttpRequestUtils.formatRfc7230Message(request);

        assertTrue(message.contains("Bearer secret-token-value"),
                "default overload should keep Authorization value intact");
        assertTrue(message.contains("Cookie: session=abc; theme=dark"),
                "default overload should keep Cookie value intact");
        assertTrue(message.contains("X-Api-Key: k-12345"));
        assertTrue(message.contains("access_token=ATV"));
        assertTrue(message.contains("password=p%40ss"),
                "default overload should preserve raw-encoded query value");
        assertTrue(message.contains("keep=ok"));
    }

    @Test
    void formatRfc7230Message_maskSensitiveFalse_sameAsDefault() throws URISyntaxException {
        HttpRequest request = buildRequest(
                "https://api.example.com/v1/users?access_token=ATV&keep=ok");

        String defaultMessage = HttpRequestUtils.formatRfc7230Message(request);
        String explicitFalse = HttpRequestUtils.formatRfc7230Message(request, false);

        assertEquals(defaultMessage, explicitFalse);
    }

    @Test
    void formatRfc7230Message_maskSensitive_masksHeadersAndQueryValues() throws URISyntaxException {
        HttpRequest request = buildRequest(
                "https://api.example.com/v1/users?access_token=ATV&keep=ok&password=p%40ss");

        String message = HttpRequestUtils.formatRfc7230Message(request, true);

        // Sensitive headers are masked regardless of the original casing
        assertTrue(message.contains("Authorization: ***"), message);
        assertTrue(message.contains("Cookie: ***"), message);
        assertTrue(message.contains("X-Api-Key: ***"), message);

        // Non-sensitive headers remain untouched
        assertTrue(message.contains("Accept: application/json"), message);

        // Sensitive query values are masked, names and order preserved
        assertTrue(message.contains("access_token=***"), message);
        assertTrue(message.contains("password=***"), message);
        assertFalse(message.contains("ATV"), message);
        assertFalse(message.contains("p%40ss"), message);

        // Non-sensitive query parameters are preserved as-is
        assertTrue(message.contains("keep=ok"), message);
    }

    @Test
    void formatRfc7230Message_maskSensitive_preservesQueryStructure() throws URISyntaxException {
        // Parameters without a value, duplicate names, and order must be preserved
        HttpRequest request = HttpRequest.of(HttpMethod.GET,
                        new URI("https://api.example.com/v1/users?a=1&access_token&code=X&code=Y&b=2"))
                .build();

        String message = HttpRequestUtils.formatRfc7230Message(request, true);
        String requestLine = message.split("\r\n", 2)[0];

        // Parameter without value is still considered sensitive and gets a masked placeholder
        // Order and separators are fully preserved
        assertEquals("GET /v1/users?a=1&access_token=***&code=***&code=***&b=2 HTTP/1.1",
                requestLine, message);
    }

    @Test
    void formatRfc7230Message_maskSensitive_caseInsensitiveMatching() throws URISyntaxException {
        HttpRequest request = HttpRequest.of(HttpMethod.GET,
                        new URI("https://api.example.com/v1/users?Access_Token=ATV&API_KEY=KKK"))
                .header("AUTHORIZATION", "Bearer x")
                .header("Set-Cookie", "id=1")
                .build();

        String message = HttpRequestUtils.formatRfc7230Message(request, true);

        assertTrue(message.contains("AUTHORIZATION: ***"), message);
        assertTrue(message.contains("Set-Cookie: ***"), message);
        assertTrue(message.contains("Access_Token=***"), message);
        assertTrue(message.contains("API_KEY=***"), message);
        assertFalse(message.contains("ATV"), message);
        assertFalse(message.contains("KKK"), message);
    }

    @Test
    void formatRfc7230Message_maskSensitive_emptyPathRendersSlash() throws URISyntaxException {
        HttpRequest request = HttpRequest.of(HttpMethod.GET,
                        new URI("https://api.example.com?token=abc"))
                .build();

        String message = HttpRequestUtils.formatRfc7230Message(request, true);
        String requestLine = message.split("\r\n", 2)[0];

        assertEquals("GET /?token=*** HTTP/1.1", requestLine, message);
    }
}
