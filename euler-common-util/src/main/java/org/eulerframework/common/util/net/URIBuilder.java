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


import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * A fluent builder that assembles RFC 3986 compliant {@link URI} values.
 *
 * <p>Unlike the multi-argument {@link URI#URI(String, String, String, int,
 * String, String, String)} constructor, which accepts only decoded input
 * and has limited control over percent-encoding, {@code URIBuilder} gives
 * each component its own encoding contract:
 * <ul>
 *   <li>{@link #path(String)} preserves RFC 3986 §3.3 structure verbatim
 *       (leading / trailing slashes and empty segments) and percent-encodes
 *       each segment against {@code unreserved}.</li>
 *   <li>{@link #query(String, String)} accepts <em>literal</em> name/value
 *       pairs (in the sense of RFC 6570 §1.2) and percent-encodes them on
 *       the caller's behalf.</li>
 *   <li>{@link #query(String)} parses an already percent-encoded query
 *       string, preserves well-formed {@code %XX} triplets and re-encodes
 *       raw reserved characters that carry delimiter semantics.</li>
 * </ul>
 *
 * <p>Typical usage:
 * <pre>{@code
 * URI uri = URIBuilder.newBuilder()
 *         .schema("https")
 *         .host("api.example.com")
 *         .path("/v1/users/{id}", t -> t.param("id", "42"))
 *         .query("lang", "zh-CN")
 *         .build();
 * // -> https://api.example.com/v1/users/42?lang=zh-CN
 * }</pre>
 *
 * <p>Instances are <strong>not thread-safe</strong>; a builder is intended
 * to be constructed, configured and consumed on a single thread. Each
 * mutating method returns {@code this} to support fluent chaining, and
 * {@link #build()} may be invoked any number of times to snapshot the
 * current state into an immutable {@link URI}.
 *
 * <p>The percent-encoding routines are adapted from {@code java.net.URI};
 * their structure and character-class masks are preserved verbatim so
 * readers already familiar with the JDK source can navigate the code
 * without surprise.
 *
 * @see URI
 * @see <a href="https://www.rfc-editor.org/rfc/rfc3986">RFC 3986 — URI Generic Syntax</a>
 */
public class URIBuilder {
    private String scheme;
    private String authority;
    private String userInfo;
    private String host;
    private int port = -1;
    private final StringBuilder rawPath = new StringBuilder();
    private final List<String[]> rawQueryParams = new ArrayList<>();

    // Use newBuilder() or of(String) to obtain a fresh instance.
    private URIBuilder() {
    }

    /**
     * Returns an empty builder with every component left unset.
     *
     * @return a new builder instance
     */
    public static URIBuilder newBuilder() {
        return new URIBuilder();
    }

    /**
     * Returns a builder seeded from the components of an existing URL.
     *
     * <p>The input is parsed by {@link URI#create(String)}; path and query
     * are copied in their raw (percent-encoded) form so that a subsequent
     * {@link #build()} round-trips to the original URL verbatim when no
     * further mutation takes place.
     *
     * @param url an RFC 3986 compliant URI string
     * @return a builder pre-populated from {@code url}
     * @throws IllegalArgumentException if {@code url} violates RFC 3986
     */
    public static URIBuilder of(String url) {
        URI uri = URI.create(url);
        return new URIBuilder()
                .schema(uri.getScheme())
                .authority(uri.getAuthority())
                .userInfo(uri.getUserInfo())
                .host(uri.getHost())
                .port(uri.getPort())
                .path(uri.getPath())
                .query(uri.getRawQuery());
    }

    /**
     * Sets the URI scheme (for example {@code https} or {@code file}).
     *
     * <p>The supplied value is stored verbatim and is expected to already
     * conform to RFC 3986 §3.1
     * ({@code ALPHA *( ALPHA / DIGIT / "+" / "-" / "." )}).
     *
     * @param scheme the scheme, or {@code null} to clear
     * @return this builder
     */
    public URIBuilder schema(String scheme) {
        this.scheme = scheme;
        return this;
    }

    // Copied verbatim from the source URL by of(); not exposed because
    // host(), userInfo() and port() already provide the preferred fluent
    // knobs for constructing an authority component piece by piece.
    private URIBuilder authority(String authority) {
        this.authority = authority;
        return this;
    }

    // See the note on authority(String).
    private URIBuilder userInfo(String userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    /**
     * Sets the URI host.
     *
     * <p>IPv6 literals may be provided with or without surrounding square
     * brackets; brackets are inserted automatically during {@link #build()}
     * when the host contains a colon.
     *
     * @param host the host, or {@code null} to clear
     * @return this builder
     */
    public URIBuilder host(String host) {
        this.host = host;
        return this;
    }

    /**
     * Sets the URI port. Pass {@code -1} to omit the port entirely.
     *
     * @param port the port number, or {@code -1} for none
     * @return this builder
     */
    public URIBuilder port(int port) {
        this.port = port;
        return this;
    }

    /**
     * Appends a path, preserving its RFC 3986 §3.3 structure.
     *
     * <p>Each segment (the text between two {@code '/'}) is percent-encoded
     * against {@code unreserved}; every {@code '/'} in the input &mdash;
     * including a leading slash, a trailing slash and any empty segments
     * produced by consecutive slashes &mdash; is kept verbatim. The input
     * is therefore treated as a path expression rather than a single
     * opaque segment.
     *
     * <p>Successive calls are joined with exactly one slash at the
     * boundary: an existing trailing slash and a new leading slash are
     * collapsed, and a slash is inserted when both sides lack one.
     *
     * @param path the path fragment to append; {@code null} or empty is a no-op
     * @return this builder
     */
    public URIBuilder path(String path) {
        if (path == null || path.isEmpty()) {
            return this;
        }

        // Quote each segment individually but keep every '/' in place, so
        // leading slash, trailing slash and empty segments (e.g. "/a//b")
        // are all preserved verbatim, as required by RFC 3986 §3.3.
        StringBuilder appended = new StringBuilder();
        StringBuilder segment = new StringBuilder();
        int n = path.length();
        for (int i = 0; i < n; i++) {
            char c = path.charAt(i);
            if (c == '/') {
                if (!segment.isEmpty()) {
                    appended.append(quote(segment.toString(), L_UNRESERVED, H_UNRESERVED));
                    segment.setLength(0);
                }
                appended.append('/');
            } else {
                segment.append(c);
            }
        }
        if (!segment.isEmpty()) {
            appended.append(quote(segment.toString(), L_UNRESERVED, H_UNRESERVED));
        }

        if (this.rawPath.isEmpty()) {
            this.rawPath.append(appended);
        } else {
            // When joining with a previous path() call, guarantee exactly one
            // slash at the boundary: collapse double, insert when missing.
            boolean endSlash = this.rawPath.charAt(this.rawPath.length() - 1) == '/';
            boolean startSlash = !appended.isEmpty() && appended.charAt(0) == '/';
            if (endSlash && startSlash) {
                this.rawPath.append(appended, 1, appended.length());
            } else if (!endSlash && !startSlash) {
                this.rawPath.append('/').append(appended);
            } else {
                this.rawPath.append(appended);
            }
        }
        return this;
    }

    /**
     * Appends a path expanded from a {@link PathTemplate}.
     *
     * <p>Variables are written in the template as {@code {name}} placeholders
     * and are bound through the supplied consumer. The expanded path is then
     * processed by {@link #path(String)}, so the same encoding and slash
     * normalisation rules apply.
     *
     * @param template             a path template containing {@code {name}}
     *                             placeholders; a preceding {@code '\\'}
     *                             escapes the next character
     * @param pathTemplateConsumer callback that binds values to the
     *                             template's variables
     * @return this builder
     */
    public URIBuilder path(String template, Consumer<PathTemplate> pathTemplateConsumer) {
        PathTemplate pathTemplate = new PathTemplate(template);
        pathTemplateConsumer.accept(pathTemplate);
        return this.path(pathTemplate.getPath());
    }

    /**
     * Appends a path expanded from a template whose variables are taken
     * from the given map. Short form of
     * {@link #path(String, Consumer)}.
     *
     * @param template a path template containing {@code {name}} placeholders
     * @param vars     values to bind to the template's variables
     * @return this builder
     */
    public URIBuilder path(String template, Map<String, String> vars) {
        return this.path(new PathTemplate(template).params(vars).getPath());
    }

    /**
     * Appends a single {@code key=value} pair to the query.
     *
     * <p>Both {@code key} and {@code value} are treated as <em>literal</em>
     * characters (RFC 6570 §1.2) and are percent-encoded against
     * {@code unreserved} before being emitted. A {@code null} value is
     * permitted and produces a bare {@code key=} in the resulting query.
     *
     * @param key   the query key; must be non-null and non-empty
     * @param value the query value, or {@code null} for an empty value
     * @return this builder
     * @throws IllegalArgumentException if {@code key} is {@code null} or empty
     */
    public URIBuilder query(String key, String value) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Query key must not be empty.");
        }
        addLiteralQueryPair(key, value);
        return this;
    }

    /**
     * Appends pairs parsed from a query string.
     *
     * <p>The first {@code '='} in each pair separates key from value; any
     * subsequent {@code '='} belongs to the value. {@code '&'} separates
     * pairs.
     *
     * <p>Well-formed {@code %XX} triplets in the input are preserved, so a
     * query returned by {@link URI#getRawQuery()} round-trips cleanly.
     * Raw reserved characters that have delimiter semantics in a
     * {@code key=value} context (in particular a {@code '='} inside a
     * value) are percent-encoded on output, as recommended by RFC 3986
     * §2.2, to avoid parser ambiguity.
     *
     * <p>Pairs with an empty key are accepted here (unlike
     * {@link #query(String, String)}) because RFC 3986 does not forbid them
     * and real-world URLs occasionally contain them.
     *
     * @param query a percent-encoded query string, without the leading
     *              {@code '?'}; {@code null} or empty is a no-op
     * @return this builder
     */
    public URIBuilder query(String query) {
        if (query == null || query.isEmpty()) {
            return this;
        }

        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        int tag = 0;
        for (char c : query.toCharArray()) {
            // Only the FIRST '=' in a pair separates key from value; any
            // subsequent '=' (e.g. Base64 padding) belongs to the value.
            if (c == '=' && tag == 0) {
                tag = 1;
                continue;
            }
            if (c == '&') {
                tag = 0;
                addEncodedQueryPair(key.toString(), value.toString());
                key = new StringBuilder();
                value = new StringBuilder();
                continue;
            }

            if (tag == 0) {
                key.append(c);
            } else {
                value.append(c);
            }
        }

        if (!key.isEmpty() || !value.isEmpty()) {
            addEncodedQueryPair(key.toString(), value.toString());
        }

        return this;
    }

    /**
     * Builds an immutable {@link URI} from the current builder state.
     *
     * <p>Components already in percent-encoded form (path and query) are
     * assembled directly; host and port are rendered following RFC 3986
     * §3.2 (IPv6 literals are bracketed when necessary). When a scheme
     * is set the path, if any, must be absolute, otherwise a
     * {@link URISyntaxException} is raised &mdash; matching the contract
     * of {@link URI}.
     *
     * <p>This method is idempotent: calling it repeatedly without further
     * mutations yields equal {@link URI} instances.
     *
     * @return a {@link URI} encoding the current state
     * @throws URISyntaxException if the components cannot be combined into
     *                            a valid URI
     */
    public URI build() throws URISyntaxException {
        String path = this.rawPath.isEmpty() ? null : this.rawPath.toString();

        String query = null;
        if (!this.rawQueryParams.isEmpty()) {
            StringJoiner joiner = new StringJoiner("&");
            this.rawQueryParams.forEach((nv) -> joiner.add(nv[0] + "=" + (nv[1] == null ? "" : nv[1])));
            query = joiner.toString();
        }
        String s = toUriString(scheme,
                this.authority, this.userInfo, host, port,
                path, query, null);
        checkPath(s, scheme, path);
        return new URI(s);
    }

    // Encodes a pair whose key and value are supplied as literal characters
    // (in the sense of RFC 6570 §1.2: "characters that appear verbatim"),
    // then stores it in {@link #rawQueryParams} as the percent-encoded form
    // required by RFC 3986 §3.4.
    private void addLiteralQueryPair(String literalKey, String literalValue) {
        this.rawQueryParams.add(new String[]{
                quote(literalKey, L_UNRESERVED, H_UNRESERVED),
                literalValue == null ? null : quote(literalValue, L_UNRESERVED, H_UNRESERVED)
        });
    }

    // Stores a pair whose key and value are already in percent-encoded form.
    // Existing %XX triplets are preserved verbatim (L_PCT_ENCODED bit); raw
    // characters that carry key/value delimiter semantics in the current
    // context (most notably '=') are still re-encoded per RFC 3986 §2.2,
    // so a Base64 padding '=' inside a value is emitted as %3D.
    private void addEncodedQueryPair(String encodedKey, String encodedValue) {
        this.rawQueryParams.add(new String[]{
                quote(encodedKey, L_COMPONENT, H_COMPONENT),
                encodedValue == null ? null : quote(encodedValue, L_COMPONENT, H_COMPONENT)
        });
    }

    // -- String construction --

    // If a scheme is given then the path, if given, must be absolute
    //
    private static void checkPath(String s, String scheme, String path)
            throws URISyntaxException {
        if (scheme != null) {
            if (path != null && !path.isEmpty() && path.charAt(0) != '/')
                throw new URISyntaxException(s, "Relative path in absolute URI");
        }
    }

    // Concatenates the pre-encoded components into a single URI string
    // following RFC 3986 §5.3. An IPv6 literal is wrapped in brackets when
    // the host itself is not already bracketed.
    private String toUriString(String scheme,
                               String authority,
                               String userInfo,
                               String host,
                               int port,
                               String path,
                               String query,
                               String fragment) {
        StringBuilder sb = new StringBuilder();
        if (scheme != null) {
            sb.append(scheme);
            sb.append(':');
        }

        if (host != null) {
            sb.append("//");
            if (userInfo != null) {
                sb.append(userInfo);
                sb.append('@');
            }
            boolean needBrackets = ((host.indexOf(':') >= 0)
                    && !host.startsWith("[")
                    && !host.endsWith("]"));
            if (needBrackets) sb.append('[');
            sb.append(host);
            if (needBrackets) sb.append(']');
            if (port != -1) {
                sb.append(':');
                sb.append(port);
            }
        } else if (authority != null) {
            sb.append("//");
            sb.append(authority);
        }
        if (path != null)
            sb.append(path);
        if (query != null) {
            sb.append('?');
            sb.append(query);
        }

        if (fragment != null) {
            sb.append('#');
            sb.append(fragment);
        }
        return sb.toString();
    }

    // -- Character classes for parsing --

    // RFC2396 precisely specifies which characters in the US-ASCII charset are
    // permissible in the various components of a URI reference.  We here
    // define a set of mask pairs to aid in enforcing these restrictions.  Each
    // mask pair consists of two longs, a low mask and a high mask.  Taken
    // together they represent a 128-bit mask, where bit i is set iff the
    // character with value i is permitted.
    //
    // This approach is more efficient than sequentially searching arrays of
    // permitted characters.  It could be made still more efficient by
    // pre-compiling the mask information so that a character's presence in a
    // given mask could be determined by a single table lookup.

    // To save startup time, we manually calculate the low-/hiMask constants.
    // For reference, the following methods were used to calculate the values:

    // Compute the low-order mask for the characters in the given string
    private static long loMask(String chars) {
        int n = chars.length();
        long m = 0;
        for (int i = 0; i < n; i++) {
            char c = chars.charAt(i);
            if (c < 64)
                m |= (1L << c);
        }
        return m;
    }

    // Compute the high-order mask for the characters in the given string
    private static long hiMask(String chars) {
        int n = chars.length();
        long m = 0;
        for (int i = 0; i < n; i++) {
            char c = chars.charAt(i);
            if ((c >= 64) && (c < 128))
                m |= (1L << (c - 64));
        }
        return m;
    }

    // Compute a low-order mask for the characters
    // between first and last, inclusive
    private static long loMask(char first, char last) {
        long m = 0;
        int f = Math.max(Math.min(first, 63), 0);
        int l = Math.max(Math.min(last, 63), 0);
        for (int i = f; i <= l; i++)
            m |= 1L << i;
        return m;
    }

    // Compute a high-order mask for the characters
    // between first and last, inclusive
    private static long hiMask(char first, char last) {
        long m = 0;
        int f = Math.max(Math.min(first, 127), 64) - 64;
        int l = Math.max(Math.min(last, 127), 64) - 64;
        for (int i = f; i <= l; i++)
            m |= 1L << i;
        return m;
    }

    // Tell whether the given character is permitted by the given mask pair
    private static boolean match(char c, long loMask, long hiMask) {
        if (c == 0) // 0 doesn't have a slot in the mask. So, it never matches.
            return false;
        if (c < 64)
            return ((1L << c) & loMask) != 0;
        if (c < 128)
            return ((1L << (c - 64)) & hiMask) != 0;
        return false;
    }

    // RFC3986 Collected ABNF for URI

    // digit    = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" |
    //            "8" | "9"
    private static final long L_DIGIT = 0x3FF000000000000L; // loMask('0', '9');
    private static final long H_DIGIT = 0L;

    // upalpha  = "A" | "B" | "C" | "D" | "E" | "F" | "G" | "H" | "I" |
    //            "J" | "K" | "L" | "M" | "N" | "O" | "P" | "Q" | "R" |
    //            "S" | "T" | "U" | "V" | "W" | "X" | "Y" | "Z"
    private static final long L_UPALPHA = 0L;
    private static final long H_UPALPHA = 0x7FFFFFEL; // hiMask('A', 'Z');

    // lowalpha = "a" | "b" | "c" | "d" | "e" | "f" | "g" | "h" | "i" |
    //            "j" | "k" | "l" | "m" | "n" | "o" | "p" | "q" | "r" |
    //            "s" | "t" | "u" | "v" | "w" | "x" | "y" | "z"
    private static final long L_LOWALPHA = 0L;
    private static final long H_LOWALPHA = 0x7FFFFFE00000000L; // hiMask('a', 'z');

    // alpha         = lowalpha | upalpha
    private static final long L_ALPHA = L_LOWALPHA | L_UPALPHA;
    private static final long H_ALPHA = H_LOWALPHA | H_UPALPHA;

    // alphanum      = alpha | digit
    private static final long L_ALPHANUM = L_DIGIT | L_ALPHA;
    private static final long H_ALPHANUM = H_DIGIT | H_ALPHA;

    // hex           = digit | "A" | "B" | "C" | "D" | "E" | "F" |
    //                         "a" | "b" | "c" | "d" | "e" | "f"
    private static final long L_HEX = L_DIGIT;
    private static final long H_HEX = 0x7E0000007EL; // hiMask('A', 'F') | hiMask('a', 'f');

    // pct-encoded   = "%" HEXDIG HEXDIG
    // The zeroth bit is used to indicate that pct-encoded pairs are allowed
    private static final long L_PCT_ENCODED = 1L;
    private static final long H_PCT_ENCODED = 0L;

    // sub-delims    = "!" / "$" / "&" / "'" / "(" / ")"
    //                 / "*" / "+" / "," / ";" / "="
    private static final long L_SUM_DELIMS = 0x28001FD200000000L; // loMask("!$&'()*+,;=");
    private static final long H_SUM_DELIMS = 0L;                  // hiMask("!$&'()*+,;=")

    // gen-delims    = ":" / "/" / "?" / "#" / "[" / "]" / "@"
    private static final long L_GEN_DELIMS = 0x8400800800000000L; // loMask(":/?#[]@");
    private static final long H_GEN_DELIMS = 0x28000001L;         // hiMask(":/?#[]@")

    // reserved      = gen-delims / sub-delims
    private static final long L_RESERVED = L_SUM_DELIMS | L_GEN_DELIMS;
    private static final long H_RESERVED = H_SUM_DELIMS | H_GEN_DELIMS;

    // unreserved    = ALPHA / DIGIT / "-" / "." / "_" / "~"
    private static final long L_UNRESERVED = L_ALPHANUM | 0x600000000000L;     // loMask("-._~");
    private static final long H_UNRESERVED = H_ALPHANUM | 0x4000000080000000L; // hiMask("-._~");

    // pchar         = unreserved / pct-encoded / sub-delims / ":" / "@"
    private static final long L_PCHAR = L_UNRESERVED | L_PCT_ENCODED | L_SUM_DELIMS | 0x400000000000000L; // loMask(":@");
    private static final long H_PCHAR = H_UNRESERVED | H_PCT_ENCODED | H_SUM_DELIMS | 1L;                 // hiMask(":@");

    // uric          = unreserved / pct-encoded / ";" / "?" / ":"
    //                 / "@" / "&" / "=" / "+" / "$" / "," / "/"
    private static final long L_URIC = L_UNRESERVED | L_PCT_ENCODED | 0xAC00985000000000L; // loMask(";?:@&=+$,/");
    private static final long H_URIC = H_UNRESERVED | H_PCT_ENCODED | 1L;                  // hiMask(";?:@&=+$,/");

    // component     = unreserved / pct-encoded
    // A URI component may contain only unreserved characters and already
    // percent-encoded octets.
    private static final long L_COMPONENT = L_UNRESERVED | L_PCT_ENCODED;
    private static final long H_COMPONENT = H_UNRESERVED | H_PCT_ENCODED;

    // -- Escaping and encoding --

    private static final char[] hexDigits = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    private static void appendEscape(StringBuilder sb, byte b) {
        sb.append('%');
        sb.append(hexDigits[(b >> 4) & 0x0f]);
        sb.append(hexDigits[(b >> 0) & 0x0f]);
    }

    private static void appendEncoded(CharsetEncoder encoder, StringBuilder sb, char c) {
        ByteBuffer bb = null;
        try {
            bb = encoder.encode(CharBuffer.wrap(new char[]{c}));
        } catch (CharacterCodingException x) {
            assert false;
        }
        while (bb.hasRemaining()) {
            int b = bb.get() & 0xff;
            if (b >= 0x80)
                appendEscape(sb, (byte) b);
            else
                sb.append((char) b);
        }
    }

    private static void appendEncoded(CharsetEncoder encoder, StringBuilder sb, int codePoint) {
        char[] chars = Character.toChars(codePoint);
        ByteBuffer bb = null;
        try {
            bb = encoder.encode(CharBuffer.wrap(chars));
        } catch (CharacterCodingException x) {
            assert false;
        }
        while (bb.hasRemaining()) {
            int b = bb.get() & 0xff;
            if (b >= 0x80)
                appendEscape(sb, (byte) b);
            else
                sb.append((char) b);
        }
    }

    /**
     * Percent-encodes characters in {@code s} that are not permitted by the
     * given ASCII mask pair, following standard URI encoding rules.
     *
     * <p>Encoding rules:
     * <ul>
     *   <li>Permitted ASCII characters are emitted verbatim; other ASCII
     *       characters are escaped as {@code %XX}.</li>
     *   <li>Non-ASCII characters, including supplementary code points such
     *       as emoji (reassembled from UTF-16 surrogate pairs), are always
     *       escaped as the UTF-8 byte sequence of their code point.</li>
     *   <li>When bit {@code 0} of {@code loMask} ({@link #L_PCT_ENCODED}) is
     *       set, any well-formed {@code %XX} triplet already present in
     *       {@code s} is copied verbatim and not double-escaped to
     *       {@code %25XX}; a standalone {@code '%'} not followed by two
     *       ASCII hex digits is still escaped to {@code %25}. When the bit
     *       is clear, every {@code '%'} is re-escaped to {@code %25} so
     *       that the output contains only characters explicitly allowed by
     *       the mask plus freshly produced {@code %XX} triplets.</li>
     * </ul>
     *
     * <p>The original {@code s} is returned unchanged when no character
     * needs encoding, avoiding unnecessary allocations.
     *
     * @param s      the input string to encode
     * @param loMask low-order bitmask for permitted ASCII characters
     *               (bits 0..63)
     * @param hiMask high-order bitmask for permitted ASCII characters
     *               (bits 64..127)
     * @return the encoded string, or {@code s} itself when nothing changed
     */
    private static String quote(String s, long loMask, long hiMask) {
        // Preserve well-formed %XX triplets only when the mask explicitly
        // permits pct-encoded octets (L_PCT_ENCODED bit set); otherwise
        // every '%' is treated as a disallowed character and escaped to %25.
        boolean preservePctEncoded = (loMask & L_PCT_ENCODED) != 0;
        StringBuilder sb = null;
        CharsetEncoder encoder = null;
        int n = s.length();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if (c < '\u0080') {
                // Preserve well-formed %XX triplets when pct-encoded is allowed.
                if (c == '%' && preservePctEncoded
                        && i + 2 < n
                        && match(s.charAt(i + 1), L_HEX, H_HEX)
                        && match(s.charAt(i + 2), L_HEX, H_HEX)) {
                    if (sb != null) {
                        sb.append(c).append(s.charAt(i + 1)).append(s.charAt(i + 2));
                    }
                    i += 2;
                    continue;
                }
                if (match(c, loMask, hiMask)) {
                    if (sb != null) {
                        sb.append(c);
                    }
                } else {
                    if (sb == null) {
                        sb = new StringBuilder();
                        sb.append(s, 0, i);
                    }
                    appendEscape(sb, (byte) c);
                }
            } else {
                // Non-ASCII (including emoji via surrogate pairs): always
                // percent-encode as UTF-8 bytes.
                if (sb == null) {
                    sb = new StringBuilder();
                    sb.append(s, 0, i);
                }
                if (encoder == null) {
                    encoder = StandardCharsets.UTF_8.newEncoder();
                }
                if (Character.isHighSurrogate(c)
                        && i + 1 < n
                        && Character.isLowSurrogate(s.charAt(i + 1))) {
                    int cp = Character.toCodePoint(c, s.charAt(++i));
                    appendEncoded(encoder, sb, cp);
                } else {
                    appendEncoded(encoder, sb, c);
                }
            }
        }
        return (sb == null) ? s : sb.toString();
    }

    /**
     * A minimal {@code {name}}-style template used by
     * {@link URIBuilder#path(String, Consumer)} and
     * {@link URIBuilder#path(String, Map)} to build paths with named
     * placeholders.
     *
     * <p>Grammar:
     * <ul>
     *   <li>{@code {name}} &mdash; expands to the value bound to
     *       {@code name}.</li>
     *   <li>{@code \\c} &mdash; escapes the next character so that a
     *       literal {@code '{'} or {@code '}'} may appear in the path.</li>
     *   <li>Any other character appears verbatim.</li>
     * </ul>
     *
     * <p>Instances are obtained through the overloads of
     * {@link URIBuilder#path(String, Consumer)} and are not intended for
     * direct construction by client code.
     */
    public static class PathTemplate {
        private final String template;
        private final Map<String, String> variables = new HashMap<>();

        private PathTemplate(String template) {
            this.template = template;
        }

        /**
         * Binds several variables at once.
         *
         * <p>Entries whose keys do not appear in the template are ignored;
         * existing bindings for the same key are overwritten.
         *
         * @param variables a map whose keys match {@code {name}}
         *                  placeholders in the template
         * @return this template
         */
        public PathTemplate params(Map<String, String> variables) {
            this.variables.putAll(variables);
            return this;
        }

        /**
         * Binds a single variable. The value's {@link Object#toString()} is
         * used as the replacement.
         *
         * @param name  the placeholder name
         * @param value the replacement value
         * @return this template
         */
        public PathTemplate param(String name, Object value) {
            this.variables.put(name, value.toString());
            return this;
        }

        // Expands the template into a concrete path by substituting every
        // {name} placeholder with its bound value. Unbalanced braces raise
        // a SyntaxException.
        private String getPath() {
            if (this.template == null || this.template.isEmpty()) {
                return this.template;
            }

            StringBuilder pathBuffer = new StringBuilder();
            StringBuilder paramBuffer = new StringBuilder();
            int tag = 0;

            char[] chars = this.template.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                if (ESCAPE == c) {
                    pathBuffer.append(chars[++i]);
                    continue;
                }

                if (L == c) {
                    tag = 1;
                    continue;
                }

                if (R == c) {
                    if (tag < 1) {
                        throw new SyntaxException("Invalid URI path char '" + c + "', index " + i);
                    }
                    tag = 0;
                    String param = paramBuffer.toString();
                    paramBuffer.delete(0, paramBuffer.length());
                    String value = this.variables.get(param);
                    pathBuffer.append(value);
                    continue;
                }

                if (tag == 0) {
                    pathBuffer.append(c);
                } else {
                    paramBuffer.append(c);
                }
            }

            return pathBuffer.toString();
        }

        private static final char ESCAPE = '\\';
        private static final char L = '{';
        private static final char R = '}';
    }
}
