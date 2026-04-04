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

package org.eulerframework.common.util.net;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Black-box tests for {@link URIBuilder}.
 *
 * <p>The tests do not rely on any knowledge of URIBuilder's internal
 * implementation. They only assert behaviour that is required or clearly
 * permitted by RFC 3986 (Uniform Resource Identifier: Generic Syntax):
 *
 * <ul>
 *   <li>Unreserved characters (A-Z a-z 0-9 - . _ ~) must never be
 *       percent-encoded.</li>
 *   <li>Non-ASCII characters must be percent-encoded as UTF-8 byte
 *       sequences so that the produced URI is ASCII-only.</li>
 *   <li>Space must be encoded as {@code %20} and a literal '%' as
 *       {@code %25}.</li>
 *   <li>Characters that would otherwise be mistaken for component
 *       delimiters must be encoded inside that component: '#' and '?' in
 *       a path; '#', '&', '=' in a query value.</li>
 *   <li>The builder is invertible: the input values round-trip through
 *       {@link URI#getPath()} / {@link URI#getQuery()}.</li>
 *   <li>Insertion order and duplicate keys are preserved in the query.</li>
 * </ul>
 */
class URIBuilderTest {

    // ====================================================================
    // Scheme / authority / host / port
    // ====================================================================

    @Test
    @DisplayName("scheme + host builds an absolute URI")
    void buildSchemeAndHost() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().schema("https").host("example.com").build();
        assertEquals("https://example.com", uri.toString());
        assertEquals("https", uri.getScheme());
        assertEquals("example.com", uri.getHost());
    }

    @Test
    @DisplayName("scheme + host + port builds URI with port")
    void buildSchemeHostPort() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().schema("https").host("example.com").port(8443).build();
        assertEquals("https://example.com:8443", uri.toString());
        assertEquals(8443, uri.getPort());
    }

    @Test
    @DisplayName("port -1 means 'no port'")
    void buildNegativePortOmitted() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().schema("https").host("example.com").port(-1).build();
        assertEquals("https://example.com", uri.toString());
    }

    @Test
    @DisplayName("IPv4 literal host is preserved verbatim")
    void buildIpv4Host() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().schema("http").host("127.0.0.1").port(8080).build();
        assertEquals("http://127.0.0.1:8080", uri.toString());
    }

    @Test
    @DisplayName("IPv6 literal host is wrapped in brackets per RFC 3986")
    void buildIpv6HostAutoWrapped() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().schema("http").host("::1").port(8080).build();
        assertEquals("http://[::1]:8080", uri.toString());
    }

    @Test
    @DisplayName("IPv6 host that already has brackets is not wrapped twice")
    void buildIpv6HostAlreadyBracketed() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().schema("http").host("[fe80::1]").port(8080).build();
        assertEquals("http://[fe80::1]:8080", uri.toString());
    }

    // ====================================================================
    // Path encoding
    //
    // All path tests omit scheme/host so that the produced URI is
    // relative. This lets URI#getRawPath() return exactly what the
    // builder emitted, with no risk of path/authority ambiguity.
    // ====================================================================

    @Test
    @DisplayName("pure ASCII path round-trips unchanged")
    void pathAsciiRoundTrip() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().path("/api/v1/users").build();
        assertEquals("/api/v1/users", uri.getPath());
    }
    
    @Test
    @DisplayName("null and empty path segments are accepted as no-op")
    void pathNullOrEmptyAccepted() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().path(null).path("").path("/only").build();
        assertEquals("/only", uri.getPath());
    }
    
    @Test
    @DisplayName("unreserved characters (A-Z a-z 0-9 - . _ ~) are never encoded")
    void pathUnreservedNeverEncoded() throws URISyntaxException {
        // RFC 3986 §2.3: unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"
        String unreserved = "abcXYZ0189-._~";
        URI uri = URIBuilder.newBuilder().path("/" + unreserved).build();
        assertEquals("/" + unreserved, uri.getRawPath());
    }
    
    @Test
    @DisplayName("space is encoded as %20")
    void pathSpaceEncoded() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().path("/hello world").build();
        assertEquals("/hello%20world", uri.getRawPath());
        assertEquals("/hello world", uri.getPath());
    }
    
    @Test
    @DisplayName("a literal '%' is encoded as %25")
    void pathPercentEncoded() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().path("/50%off").build();
        assertEquals("/50%25off", uri.getRawPath());
        assertEquals("/50%off", uri.getPath());
    }
    
    @Test
    @DisplayName("'?' and '#' in a path are encoded so they do not start query/fragment")
    void pathHashAndQuestionEncoded() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().path("/a?b#c").build();
        // ? -> %3F, # -> %23
        assertEquals("/a%3Fb%23c", uri.getRawPath());
        assertNull(uri.getRawQuery());
        assertNull(uri.getRawFragment());
        assertEquals("/a?b#c", uri.getPath());
    }
    
    @Test
    @DisplayName("Chinese characters are encoded as UTF-8 byte sequences")
    void pathChinese() throws URISyntaxException {
        // 用 -> E7 94 A8, 户 -> E6 88 B7, 资 -> E8 B5 84, 料 -> E6 96 99
        URI uri = URIBuilder.newBuilder().path("/用户/资料").build();
        assertEquals("/%E7%94%A8%E6%88%B7/%E8%B5%84%E6%96%99", uri.getRawPath());
        assertEquals("/用户/资料", uri.getPath());
    }
    
    @Test
    @DisplayName("a single supplementary-plane emoji is encoded as its UTF-8 bytes")
    void pathEmojiSingleCodePoint() throws URISyntaxException {
        // 😀 U+1F600 -> F0 9F 98 80
        URI uri = URIBuilder.newBuilder().path("/😀").build();
        assertEquals("/%F0%9F%98%80", uri.getRawPath());
        assertEquals("/😀", uri.getPath());
    }
    
    @Test
    @DisplayName("emoji with skin-tone modifier is encoded as two separate code points")
    void pathEmojiSkinTone() throws URISyntaxException {
        // 👋 U+1F44B -> F0 9F 91 8B
        // 🏽 U+1F3FD -> F0 9F 8F BD  (medium skin tone)
        URI uri = URIBuilder.newBuilder().path("/👋🏽").build();
        assertEquals("/%F0%9F%91%8B%F0%9F%8F%BD", uri.getRawPath());
        assertEquals("/👋🏽", uri.getPath());
    }
    
    @Test
    @DisplayName("ZWJ emoji sequence round-trips through build and decode")
    void pathEmojiZwjSequence() throws URISyntaxException {
        // Family-of-four: 👨 ZWJ 👩 ZWJ 👧 ZWJ 👦
        // ZWJ is U+200D, an invisible joiner between each emoji below.
        String family = "👨‍👩‍👧‍👦";
        URI uri = URIBuilder.newBuilder().path("/" + family).build();
        assertEquals("/%F0%9F%91%A8%E2%80%8D%F0%9F%91%A9%E2%80%8D%F0%9F%91%A7%E2%80%8D%F0%9F%91%A6",
                uri.getRawPath());
        assertEquals("/" + family, uri.getPath());
    }
    
    @Test
    @DisplayName("world languages round-trip through path encode/decode")
    void pathWorldLanguagesRoundTrip() throws URISyntaxException {
        String[] languages = {
                "中文",          // Chinese (Simplified)
                "繁體中文",       // Chinese (Traditional)
                "日本語",         // Japanese
                "한국어",         // Korean
                "العربية",        // Arabic (RTL)
                "עברית",         // Hebrew (RTL)
                "Русский",       // Russian (Cyrillic)
                "Français",      // French (accented Latin)
                "Deutsch",       // German
                "Español",       // Spanish
                "Português",     // Portuguese
                "हिन्दी",           // Hindi (Devanagari with combining marks)
                "ไทย",           // Thai
                "Ελληνικά",      // Greek
                "Türkçe",        // Turkish
                "Tiếng",         // Vietnamese
                "Українська"     // Ukrainian
        };
        for (String lang : languages) {
            URI uri = URIBuilder.newBuilder().path("/" + lang).build();
            // Produced URI must be ASCII-only (RFC 3986 §2.1).
            String raw = uri.getRawPath();
            assertTrue(raw.chars().allMatch(c -> c < 0x80),
                    "raw path must be ASCII-only for language=" + lang + " but was " + raw);
            // And round-trip back to the original language text.
            assertEquals("/" + lang, uri.getPath(), "language=" + lang);
        }
    }

    // ====================================================================
    // Path templates
    // ====================================================================

    @Test
    @DisplayName("template variable injected via Consumer")
    void pathTemplateConsumerSingleVar() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder()
                .path("/users/{id}", t -> t.param("id", 42))
                .build();
        assertEquals("/users/42", uri.getPath());
    }

    @Test
    @DisplayName("template variables injected via Map")
    void pathTemplateMapMultipleVars() throws URISyntaxException {
        Map<String, String> vars = new HashMap<>();
        vars.put("group", "admin");
        vars.put("id", "99");
        URI uri = URIBuilder.newBuilder()
                .path("/groups/{group}/users/{id}", vars)
                .build();
        assertEquals("/groups/admin/users/99", uri.getPath());
    }

    @Test
    @DisplayName("Chinese variable values are encoded as UTF-8")
    void pathTemplateChineseVar() throws URISyntaxException {
        // 搜 -> E6 90 9C, 索 -> E7 B4 A2; 中 -> E4 B8 AD, 文 -> E6 96 87
        URI uri = URIBuilder.newBuilder()
                .path("/搜索/{keyword}", t -> t.param("keyword", "中文"))
                .build();
        assertEquals("/%E6%90%9C%E7%B4%A2/%E4%B8%AD%E6%96%87", uri.getRawPath());
        assertEquals("/搜索/中文", uri.getPath());
    }

    @Test
    @DisplayName("emoji variable values are encoded as UTF-8")
    void pathTemplateEmojiVar() throws URISyntaxException {
        // 🔥 U+1F525 -> F0 9F 94 A5
        URI uri = URIBuilder.newBuilder()
                .path("/reactions/{emoji}", t -> t.param("emoji", "🔥"))
                .build();
        assertEquals("/reactions/%F0%9F%94%A5", uri.getRawPath());
        assertEquals("/reactions/🔥", uri.getPath());
    }

    @Test
    @DisplayName("multi-language variable values round-trip")
    void pathTemplateMultiLanguageVars() throws URISyntaxException {
        Map<String, String> vars = new LinkedHashMap<>();
        vars.put("lang", "日本語");
        vars.put("user", "Привет");
        vars.put("tag", "🏷");        // 🏷 U+1F3F7 -> F0 9F 8F B7
        URI uri = URIBuilder.newBuilder()
                .path("/{lang}/{user}/{tag}", vars)
                .build();
        assertEquals("/日本語/Привет/🏷", uri.getPath());
    }

    @Test
    @DisplayName("malformed template throws SyntaxException")
    void pathTemplateDanglingRightBraceThrows() {
        assertThrows(SyntaxException.class,
                () -> URIBuilder.newBuilder().path("/bad}", t -> {
                }));
    }

    // ====================================================================
    // Query encoding
    // ====================================================================

    @Test
    @DisplayName("single key=value")
    void querySingle() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().query("name", "alice").build();
        assertEquals("name=alice", uri.getRawQuery());
        assertEquals("name=alice", uri.getQuery());
    }

    @Test
    @DisplayName("null value renders as 'key='")
    void queryNullValueRenderedAsEmpty() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().query("k", null).build();
        assertEquals("k=", uri.getRawQuery());
    }

    @Test
    @DisplayName("empty value renders as 'key='")
    void queryEmptyValueRenderedAsEmpty() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().query("k", "").build();
        assertEquals("k=", uri.getRawQuery());
    }

    @Test
    @DisplayName("null or blank key is rejected")
    void queryBlankKeyThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> URIBuilder.newBuilder().query((String) null, "v"));
        assertThrows(IllegalArgumentException.class,
                () -> URIBuilder.newBuilder().query("", "v"));
    }

    @Test
    @DisplayName("duplicate keys preserve insertion order")
    void queryDuplicateKeyPreservesOrder() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder()
                .query("tag", "a")
                .query("tag", "b")
                .query("tag", "c")
                .build();
        assertEquals("tag=a&tag=b&tag=c", uri.getRawQuery());
    }

    @Test
    @DisplayName("different keys preserve insertion order")
    void queryDifferentKeysPreserveInsertionOrder() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder()
                .query("z", "1")
                .query("a", "2")
                .query("m", "3")
                .build();
        assertEquals("z=1&a=2&m=3", uri.getRawQuery());
    }

    @Test
    @DisplayName("unreserved characters are never encoded in a query value")
    void queryUnreservedNotEncoded() throws URISyntaxException {
        String unreserved = "abcXYZ0189-._~";
        URI uri = URIBuilder.newBuilder().query("q", unreserved).build();
        assertEquals("q=" + unreserved, uri.getRawQuery());
    }

    @Test
    @DisplayName("space in query value is encoded as %20")
    void querySpaceEncoded() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().query("q", "hello world").build();
        assertEquals("q=hello%20world", uri.getRawQuery());
        assertEquals("q=hello world", uri.getQuery());
    }

    @Test
    @DisplayName("'&' inside a value must be encoded, otherwise it would split the pair")
    void queryAmpersandInValueEncoded() throws URISyntaxException {
        // & -> %26
        URI uri = URIBuilder.newBuilder().query("q", "a&b").build();
        assertEquals("q=a%26b", uri.getRawQuery());
        assertEquals("q=a&b", uri.getQuery());
    }

    @Test
    @DisplayName("'=' inside a value must be encoded, otherwise it could confuse parsers")
    void queryEqualsInValueEncoded() throws URISyntaxException {
        // = -> %3D
        URI uri = URIBuilder.newBuilder().query("q", "a=b").build();
        assertEquals("q=a%3Db", uri.getRawQuery());
        assertEquals("q=a=b", uri.getQuery());
    }

    @Test
    @DisplayName("'#' inside a value must be encoded, otherwise it would start a fragment")
    void queryHashInValueEncoded() throws URISyntaxException {
        // # -> %23
        URI uri = URIBuilder.newBuilder().query("q", "a#b").build();
        assertEquals("q=a%23b", uri.getRawQuery());
        assertNull(uri.getRawFragment());
        assertEquals("q=a#b", uri.getQuery());
    }

    @Test
    @DisplayName("a literal '%' in a value is encoded as %25")
    void queryPercentEncoded() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().query("q", "50%off").build();
        assertEquals("q=50%25off", uri.getRawQuery());
        assertEquals("q=50%off", uri.getQuery());
    }

    @Test
    @DisplayName("Chinese key and value are encoded as UTF-8 bytes")
    void queryChineseKeyAndValue() throws URISyntaxException {
        // 关 -> E5 85 B3, 键 -> E9 94 AE, 字 -> E5 AD 97, 值 -> E5 80 BC
        URI uri = URIBuilder.newBuilder().query("关键字", "值").build();
        assertEquals("%E5%85%B3%E9%94%AE%E5%AD%97=%E5%80%BC", uri.getRawQuery());
        assertEquals("关键字=值", uri.getQuery());
    }

    @Test
    @DisplayName("emoji value is encoded as UTF-8 bytes")
    void queryEmojiValue() throws URISyntaxException {
        // 😀 U+1F600 -> F0 9F 98 80
        // 😎 U+1F60E -> F0 9F 98 8E
        // 🔥 U+1F525 -> F0 9F 94 A5
        URI uri = URIBuilder.newBuilder().query("mood", "😀😎🔥").build();
        assertEquals("mood=%F0%9F%98%80%F0%9F%98%8E%F0%9F%94%A5", uri.getRawQuery());
        assertEquals("mood=😀😎🔥", uri.getQuery());
    }

    @Test
    @DisplayName("world languages round-trip through query encode/decode")
    void queryWorldLanguagesRoundTrip() throws URISyntaxException {
        Map<String, String> samples = new LinkedHashMap<>();
        samples.put("zh", "中文");
        samples.put("ja", "日本語");
        samples.put("ko", "한국어");
        samples.put("ar", "العربية");
        samples.put("he", "עברית");
        samples.put("ru", "Русский");
        samples.put("el", "Ελληνικά");
        samples.put("hi", "हिन्दी");
        samples.put("th", "ไทย");

        URIBuilder builder = URIBuilder.newBuilder();
        samples.forEach(builder::query);
        URI uri = builder.build();

        // Produced query must be ASCII-only.
        String raw = uri.getRawQuery();
        assertNotNull(raw);
        assertTrue(raw.chars().allMatch(c -> c < 0x80),
                "raw query must be ASCII-only but was " + raw);

        // And the decoded query must contain every input pair verbatim.
        String decoded = uri.getQuery();
        samples.forEach((k, v) ->
                assertTrue(decoded.contains(k + "=" + v),
                        "decoded query must contain '" + k + "=" + v + "' but was " + decoded));
    }

    // ====================================================================
    // Query string parsing
    // ====================================================================

    @Test
    @DisplayName("query string is parsed into key/value pairs")
    void queryParseBasic() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().query("a=1&b=2&c=3").build();
        assertEquals("a=1&b=2&c=3", uri.getRawQuery());
    }

    @Test
    @DisplayName("query string with duplicate keys is parsed order-preserving")
    void queryParseDuplicateKey() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().query("tag=a&tag=b&tag=c").build();
        assertEquals("tag=a&tag=b&tag=c", uri.getRawQuery());
    }

    @Test
    @DisplayName("'a=' is parsed as key with empty value")
    void queryParseKeyWithEmptyValue() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().query("a=").build();
        assertEquals("a=", uri.getRawQuery());
    }

    @Test
    @DisplayName("null or empty query string is ignored")
    void queryParseNullOrEmptyIgnored() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder()
                .query((String) null)
                .query("")
                .query("a", "1")
                .build();
        assertEquals("a=1", uri.getRawQuery());
    }

    // ====================================================================
    // URIBuilder.of(...)
    // ====================================================================

    @Test
    @DisplayName("of() parses scheme, host and port")
    void ofSchemeAndHost() throws URISyntaxException {
        URI uri = URIBuilder.of("https://example.com:8443").build();
        assertEquals("https://example.com:8443", uri.toString());
    }

    @Test
    @DisplayName("of() preserves the existing query")
    void ofPreservesQuery() throws URISyntaxException {
        URI uri = URIBuilder.of("http://example.com?a=1&b=2").build();
        assertEquals("a=1&b=2", uri.getRawQuery());
    }

    @Test
    @DisplayName("of() followed by query(...) appends new pairs at the end")
    void ofThenAppendQuery() throws URISyntaxException {
        URI uri = URIBuilder.of("http://example.com?a=1")
                .query("b", "2")
                .query("a", "3")
                .build();
        assertEquals("a=1&b=2&a=3", uri.getRawQuery());
    }

    // ====================================================================
    // Realistic combined scenarios
    // ====================================================================

    @Test
    @DisplayName("multi-language path variables + emoji query + duplicate keys together")
    void combinedPathAndQuery() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder()
                .path("/api/v1/用户/{id}", t -> t.param("id", "张三"))
                .query("keyword", "搜索 😀")
                .query("tag", "热门")
                .query("tag", "推荐")
                .query("page", "1")
                .build();

        // Path: 用户 -> %E7%94%A8%E6%88%B7; 张三 -> %E5%BC%A0%E4%B8%89
        assertEquals("/api/v1/%E7%94%A8%E6%88%B7/%E5%BC%A0%E4%B8%89", uri.getRawPath());
        assertEquals("/api/v1/用户/张三", uri.getPath());

        // Query: 搜索 -> %E6%90%9C%E7%B4%A2, space -> %20, 😀 -> %F0%9F%98%80
        //        热门 -> %E7%83%AD%E9%97%A8, 推荐 -> %E6%8E%A8%E8%8D%90
        String expectedQuery = "keyword=%E6%90%9C%E7%B4%A2%20%F0%9F%98%80"
                + "&tag=%E7%83%AD%E9%97%A8"
                + "&tag=%E6%8E%A8%E8%8D%90"
                + "&page=1";
        assertEquals(expectedQuery, uri.getRawQuery());
        assertEquals("keyword=搜索 😀&tag=热门&tag=推荐&page=1", uri.getQuery());
    }

    @Test
    @DisplayName("no path and no query: raw path is empty and raw query is null")
    void noPathNoQuery() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().schema("https").host("example.com").build();
        assertEquals("", uri.getRawPath());
        assertNull(uri.getRawQuery());
    }

    // ====================================================================
    // Regression tests
    //
    // The cases below target bugs that an earlier version of URIBuilder
    // exhibited. They are all plain RFC 3986 expectations.
    // ====================================================================

    @Test
    @DisplayName("scheme + host + absolute path builds https://host/path (no collapse)")
    void regressionSchemeHostAbsolutePath() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder()
                .schema("https").host("example.com").path("/api/v1/users").build();
        assertEquals("https://example.com/api/v1/users", uri.toString());
        assertEquals("/api/v1/users", uri.getRawPath());
    }

    @Test
    @DisplayName("leading slash in a relative path is preserved")
    void regressionLeadingSlashPreserved() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().path("/a/b").build();
        assertEquals("/a/b", uri.getRawPath());
    }

    @Test
    @DisplayName("trailing slash is preserved (REST treats /a/b/ and /a/b as different resources)")
    void regressionTrailingSlashPreserved() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().path("/a/b/").build();
        assertEquals("/a/b/", uri.getRawPath());
    }

    @Test
    @DisplayName("consecutive slashes are preserved (empty segments are legal in RFC 3986)")
    void regressionConsecutiveSlashesPreserved() throws URISyntaxException {
        URI uri = URIBuilder.newBuilder().path("/a//b").build();
        assertEquals("/a//b", uri.getRawPath());
    }

    @Test
    @DisplayName("multiple path() calls are joined with exactly one slash at the boundary")
    void regressionMultiplePathCallsJoinedCleanly() throws URISyntaxException {
        // Neither side has a boundary slash: one is inserted.
        assertEquals("/a/b",
                URIBuilder.newBuilder().path("/a").path("b").build().getRawPath());
        // Both sides have one: the extra is collapsed.
        assertEquals("/a/b",
                URIBuilder.newBuilder().path("/a/").path("/b").build().getRawPath());
        // Only the left has one: kept as-is.
        assertEquals("/a/b",
                URIBuilder.newBuilder().path("/a/").path("b").build().getRawPath());
        // Only the right has one: kept as-is.
        assertEquals("/a/b",
                URIBuilder.newBuilder().path("/a").path("/b").build().getRawPath());
    }

    @Test
    @DisplayName("'=' after the first in a query value is percent-encoded per RFC 3986 §2.2")
    void regressionQueryParseEqualsInValueEncoded() throws URISyntaxException {
        // RFC 3986 §2.2 recommends percent-encoding reserved characters that
        // carry delimiter semantics in the current context. Inside a
        // key=value pair the '=' is such a delimiter, so a Base64 padding
        // '=' sitting in the value must be emitted as %3D to avoid
        // ambiguity. '=' -> %3D.
        URI uri = URIBuilder.newBuilder().query("token=dXNlcjpwYXNz==&next=home").build();
        assertEquals("token=dXNlcjpwYXNz%3D%3D&next=home", uri.getRawQuery());
        // The decoded query round-trips back to the original form.
        assertEquals("token=dXNlcjpwYXNz==&next=home", uri.getQuery());
    }

    @Test
    @DisplayName("of() accepts a URL whose query has an empty key (RFC 3986 does not forbid it)")
    void regressionOfAcceptsEmptyKeyQueryPair() throws URISyntaxException {
        URI uri = URIBuilder.of("http://example.com/?=orphan").build();
        assertNotNull(uri);
        assertEquals("http", uri.getScheme());
        assertEquals("example.com", uri.getHost());
    }

    @Test
    @DisplayName("of() round-trips a URL that contains a path")
    void regressionOfRoundTripsUrlWithPath() throws URISyntaxException {
        URI uri = URIBuilder.of("https://example.com/a/b/c").build();
        assertEquals("https://example.com/a/b/c", uri.toString());
        assertEquals("/a/b/c", uri.getRawPath());
    }
}
