package org.eulerframework.common.util.net;


import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Consumer;

public class URIBuilder {
    private String scheme;
    private String authority;
    private String userInfo;
    private String host;
    private int port = -1;
    private String path;
    private final LinkedHashMap<String, String> query = new LinkedHashMap<>();
    private String fragment;

    public static URIBuilder newBuilder() {
        return new URIBuilder();
    }

    public static URIBuilder of(String url) {
        URI uri = URI.create(url);
        return new URIBuilder()
                .schema(uri.getScheme())
                .authority(uri.getAuthority())
                .userInfo(uri.getUserInfo())
                .host(uri.getHost())
                .port(uri.getPort())
                .path(uri.getPath())
                .query(uri.getQuery());
    }

    public URIBuilder schema(String scheme) {
        this.scheme = scheme;
        return this;
    }

    private URIBuilder authority(String authority) {
        this.authority = authority;
        return this;
    }

    private URIBuilder userInfo(String userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    public URIBuilder host(String host) {
        this.host = host;
        return this;
    }

    public URIBuilder port(int port) {
        this.port = port;
        return this;
    }

    public URIBuilder path(String path) {
        this.path = path;
        return this;
    }

    public URIBuilder path(String template, Consumer<PathTemplate> pathTemplateConsumer) {
        PathTemplate pathTemplate = new PathTemplate(template);
        pathTemplateConsumer.accept(pathTemplate);
        return this.path(pathTemplate.getPath());
    }

    public URIBuilder path(String template, Map<String, String> vars) {
        return this.path(new PathTemplate(template).params(vars).getPath());
    }

    public URIBuilder query(String key, String value) {
        this.query.put(key, value);
        return this;
    }

    public URIBuilder query(String query) {
        if (query == null || query.isEmpty()) {
            return this;
        }

        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        int tag = 0;
        for (char c : query.toCharArray()) {
            if (c == '=') {
                tag = 1;
                continue;
            } else if (c == '&') {
                tag = 0;
                this.query(key.toString(), value.toString());
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

        if (key.length() > 0 || value.length() > 0) {
            this.query(key.toString(), value.toString());
        }

        return this;
    }

    private URIBuilder fragment(String fragment) {
        this.fragment = fragment;
        return this;
    }

    public URI build() throws URISyntaxException {
        String query = null;
        if (!this.query.isEmpty()) {
            StringJoiner joiner = new StringJoiner("&");
            this.query.forEach((key, value) -> {
                joiner.add(key + "=" + value);
            });
            query = joiner.toString();
        }
        String s = toString(scheme, null,
                this.authority, this.userInfo, host, port,
                path, query, this.fragment);
        checkPath(s, scheme, path);
        return new URI(s);
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

    private void appendAuthority(StringBuilder sb,
                                 String authority,
                                 String userInfo,
                                 String host,
                                 int port) {
        if (host != null) {
            sb.append("//");
            if (userInfo != null) {
                sb.append(quote(userInfo, L_USERINFO, H_USERINFO));
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
            if (authority.startsWith("[")) {
                // authority should (but may not) contain an embedded IPv6 address
                int end = authority.indexOf(']');
                String doquote = authority, dontquote = "";
                if (end != -1 && authority.indexOf(':') != -1) {
                    // the authority contains an IPv6 address
                    if (end == authority.length()) {
                        dontquote = authority;
                        doquote = "";
                    } else {
                        dontquote = authority.substring(0, end + 1);
                        doquote = authority.substring(end + 1);
                    }
                }
                sb.append(dontquote);
                sb.append(quote(doquote,
                        L_REG_NAME | L_SERVER,
                        H_REG_NAME | H_SERVER));
            } else {
                sb.append(quote(authority,
                        L_REG_NAME | L_SERVER,
                        H_REG_NAME | H_SERVER));
            }
        }
    }

    private void appendSchemeSpecificPart(StringBuilder sb,
                                          String opaquePart,
                                          String authority,
                                          String userInfo,
                                          String host,
                                          int port,
                                          String path,
                                          String query) {
        if (opaquePart != null) {
            /* check if SSP begins with an IPv6 address
             * because we must not quote a literal IPv6 address
             */
            if (opaquePart.startsWith("//[")) {
                int end = opaquePart.indexOf(']');
                if (end != -1 && opaquePart.indexOf(':') != -1) {
                    String doquote, dontquote;
                    if (end == opaquePart.length()) {
                        dontquote = opaquePart;
                        doquote = "";
                    } else {
                        dontquote = opaquePart.substring(0, end + 1);
                        doquote = opaquePart.substring(end + 1);
                    }
                    sb.append(dontquote);
                    sb.append(quote(doquote, L_URIC, H_URIC));
                }
            } else {
                sb.append(quote(opaquePart, L_URIC, H_URIC));
            }
        } else {
            appendAuthority(sb, authority, userInfo, host, port);
            if (path != null)
                sb.append(quote(path, L_PATH, H_PATH));
            if (query != null) {
                sb.append('?');
                sb.append(quote(query, L_URIC, H_URIC));
            }
        }
    }

    private void appendFragment(StringBuilder sb, String fragment) {
        if (fragment != null) {
            sb.append('#');
            sb.append(quote(fragment, L_URIC, H_URIC));
        }
    }

    private String toString(String scheme,
                            String opaquePart,
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
        appendSchemeSpecificPart(sb, opaquePart,
                authority, userInfo, host, port,
                path, query);
        appendFragment(sb, fragment);
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
    // precompiling the mask information so that a character's presence in a
    // given mask could be determined by a single table lookup.

    // To save startup time, we manually calculate the low-/highMask constants.
    // For reference, the following methods were used to calculate the values:

    // Compute the low-order mask for the characters in the given string
    //     private static long lowMask(String chars) {
    //        int n = chars.length();
    //        long m = 0;
    //        for (int i = 0; i < n; i++) {
    //            char c = chars.charAt(i);
    //            if (c < 64)
    //                m |= (1L << c);
    //        }
    //        return m;
    //    }

    // Compute the high-order mask for the characters in the given string
    //    private static long highMask(String chars) {
    //        int n = chars.length();
    //        long m = 0;
    //        for (int i = 0; i < n; i++) {
    //            char c = chars.charAt(i);
    //            if ((c >= 64) && (c < 128))
    //                m |= (1L << (c - 64));
    //        }
    //        return m;
    //    }

    // Compute a low-order mask for the characters
    // between first and last, inclusive
    //    private static long lowMask(char first, char last) {
    //        long m = 0;
    //        int f = Math.max(Math.min(first, 63), 0);
    //        int l = Math.max(Math.min(last, 63), 0);
    //        for (int i = f; i <= l; i++)
    //            m |= 1L << i;
    //        return m;
    //    }

    // Compute a high-order mask for the characters
    // between first and last, inclusive
    //    private static long highMask(char first, char last) {
    //        long m = 0;
    //        int f = Math.max(Math.min(first, 127), 64) - 64;
    //        int l = Math.max(Math.min(last, 127), 64) - 64;
    //        for (int i = f; i <= l; i++)
    //            m |= 1L << i;
    //        return m;
    //    }

    // Tell whether the given character is permitted by the given mask pair
    private static boolean match(char c, long lowMask, long highMask) {
        if (c == 0) // 0 doesn't have a slot in the mask. So, it never matches.
            return false;
        if (c < 64)
            return ((1L << c) & lowMask) != 0;
        if (c < 128)
            return ((1L << (c - 64)) & highMask) != 0;
        return false;
    }

    // Character-class masks, in reverse order from RFC2396 because
    // initializers for static fields cannot make forward references.

    // digit    = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" |
    //            "8" | "9"
    private static final long L_DIGIT = 0x3FF000000000000L; // lowMask('0', '9');
    private static final long H_DIGIT = 0L;

    // upalpha  = "A" | "B" | "C" | "D" | "E" | "F" | "G" | "H" | "I" |
    //            "J" | "K" | "L" | "M" | "N" | "O" | "P" | "Q" | "R" |
    //            "S" | "T" | "U" | "V" | "W" | "X" | "Y" | "Z"
    private static final long L_UPALPHA = 0L;
    private static final long H_UPALPHA = 0x7FFFFFEL; // highMask('A', 'Z');

    // lowalpha = "a" | "b" | "c" | "d" | "e" | "f" | "g" | "h" | "i" |
    //            "j" | "k" | "l" | "m" | "n" | "o" | "p" | "q" | "r" |
    //            "s" | "t" | "u" | "v" | "w" | "x" | "y" | "z"
    private static final long L_LOWALPHA = 0L;
    private static final long H_LOWALPHA = 0x7FFFFFE00000000L; // highMask('a', 'z');

    // alpha         = lowalpha | upalpha
    private static final long L_ALPHA = L_LOWALPHA | L_UPALPHA;
    private static final long H_ALPHA = H_LOWALPHA | H_UPALPHA;

    // alphanum      = alpha | digit
    private static final long L_ALPHANUM = L_DIGIT | L_ALPHA;
    private static final long H_ALPHANUM = H_DIGIT | H_ALPHA;

    // hex           = digit | "A" | "B" | "C" | "D" | "E" | "F" |
    //                         "a" | "b" | "c" | "d" | "e" | "f"
    private static final long L_HEX = L_DIGIT;
    private static final long H_HEX = 0x7E0000007EL; // highMask('A', 'F') | highMask('a', 'f');

    // mark          = "-" | "_" | "." | "!" | "~" | "*" | "'" |
    //                 "(" | ")"
    private static final long L_MARK = 0x678200000000L; // lowMask("-_.!~*'()");
    private static final long H_MARK = 0x4000000080000000L; // highMask("-_.!~*'()");

    // unreserved    = alphanum | mark
    private static final long L_UNRESERVED = L_ALPHANUM | L_MARK;
    private static final long H_UNRESERVED = H_ALPHANUM | H_MARK;

    // reserved      = ";" | "/" | "?" | ":" | "@" | "&" | "=" | "+" |
    //                 "$" | "," | "[" | "]"
    // Added per RFC2732: "[", "]"
    private static final long L_RESERVED = 0xAC00985000000000L; // lowMask(";/?:@&=+$,[]");
    private static final long H_RESERVED = 0x28000001L; // highMask(";/?:@&=+$,[]");

    // The zero'th bit is used to indicate that escape pairs and non-US-ASCII
    // characters are allowed; this is handled by the scanEscape method below.
    private static final long L_ESCAPED = 1L;
    private static final long H_ESCAPED = 0L;

    // uric          = reserved | unreserved | escaped
    private static final long L_URIC = L_RESERVED | L_UNRESERVED | L_ESCAPED;
    private static final long H_URIC = H_RESERVED | H_UNRESERVED | H_ESCAPED;

    // pchar         = unreserved | escaped |
    //                 ":" | "@" | "&" | "=" | "+" | "$" | ","
    private static final long L_PCHAR
            = L_UNRESERVED | L_ESCAPED | 0x2400185000000000L; // lowMask(":@&=+$,");
    private static final long H_PCHAR
            = H_UNRESERVED | H_ESCAPED | 0x1L; // highMask(":@&=+$,");

    // All valid path characters
    private static final long L_PATH = L_PCHAR | 0x800800000000000L; // lowMask(";/");
    private static final long H_PATH = H_PCHAR; // highMask(";/") == 0x0L;

    // Dash, for use in domainlabel and toplabel
    private static final long L_DASH = 0x200000000000L; // lowMask("-");
    private static final long H_DASH = 0x0L; // highMask("-");

    // Dot, for use in hostnames
    private static final long L_DOT = 0x400000000000L; // lowMask(".");
    private static final long H_DOT = 0x0L; // highMask(".");

    // userinfo      = *( unreserved | escaped |
    //                    ";" | ":" | "&" | "=" | "+" | "$" | "," )
    private static final long L_USERINFO
            = L_UNRESERVED | L_ESCAPED | 0x2C00185000000000L; // lowMask(";:&=+$,");
    private static final long H_USERINFO
            = H_UNRESERVED | H_ESCAPED; // | highMask(";:&=+$,") == 0L;

    // reg_name      = 1*( unreserved | escaped | "$" | "," |
    //                     ";" | ":" | "@" | "&" | "=" | "+" )
    private static final long L_REG_NAME
            = L_UNRESERVED | L_ESCAPED | 0x2C00185000000000L; // lowMask("$,;:@&=+");
    private static final long H_REG_NAME
            = H_UNRESERVED | H_ESCAPED | 0x1L; // highMask("$,;:@&=+");

    // All valid characters for server-based authorities
    private static final long L_SERVER
            = L_USERINFO | L_ALPHANUM | L_DASH | 0x400400000000000L; // lowMask(".:@[]");
    private static final long H_SERVER
            = H_USERINFO | H_ALPHANUM | H_DASH | 0x28000001L; // highMask(".:@[]");

    // Special case of server authority that represents an IPv6 address
    // In this case, a % does not signify an escape sequence
    private static final long L_SERVER_PERCENT
            = L_SERVER | 0x2000000000L; // lowMask("%");
    private static final long H_SERVER_PERCENT
            = H_SERVER; // | highMask("%") == 0L;

    // scheme        = alpha *( alpha | digit | "+" | "-" | "." )
    private static final long L_SCHEME = L_ALPHA | L_DIGIT | 0x680000000000L; // lowMask("+-.");
    private static final long H_SCHEME = H_ALPHA | H_DIGIT; // | highMask("+-.") == 0L

    // scope_id = alpha | digit | "_" | "."
    private static final long L_SCOPE_ID
            = L_ALPHANUM | 0x400000000000L; // lowMask("_.");
    private static final long H_SCOPE_ID
            = H_ALPHANUM | 0x80000000L; // highMask("_.");

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
            bb = encoder.encode(CharBuffer.wrap("" + c));
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

    // Quote any characters in s that are not permitted
    // by the given mask pair
    //
    private static String quote(String s, long lowMask, long highMask) {
        StringBuilder sb = null;
        CharsetEncoder encoder = null;
        boolean allowNonASCII = ((lowMask & L_ESCAPED) != 0);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '\u0080') {
                if (!match(c, lowMask, highMask)) {
                    if (sb == null) {
                        sb = new StringBuilder();
                        sb.append(s, 0, i);
                    }
                    appendEscape(sb, (byte) c);
                } else {
                    if (sb != null)
                        sb.append(c);
                }
            } else if (allowNonASCII
                    /*&& (Character.isSpaceChar(c)
                    || Character.isISOControl(c))*/) {
                if (encoder == null)
                    encoder = StandardCharsets.UTF_8.newEncoder();
                if (sb == null) {
                    sb = new StringBuilder();
                    sb.append(s, 0, i);
                }
                appendEncoded(encoder, sb, c);
            } else {
                if (sb != null)
                    sb.append(c);
            }
        }
        return (sb == null) ? s : sb.toString();
    }

    // Encodes all characters >= \u0080 into escaped, normalized UTF-8 octets,
    // assuming that s is otherwise legal
    //
    private static String encode(String s) {
        int n = s.length();
        if (n == 0)
            return s;

        // First check whether we actually need to encode
        for (int i = 0; ; ) {
            if (s.charAt(i) >= '\u0080')
                break;
            if (++i >= n)
                return s;
        }

        String ns = Normalizer.normalize(s, Normalizer.Form.NFC);
        ByteBuffer bb = null;
        try {
            bb = StandardCharsets.UTF_8.newEncoder()
                    .encode(CharBuffer.wrap(ns));

        } catch (CharacterCodingException x) {
            assert false;
        }

        StringBuilder sb = new StringBuilder();
        while (bb.hasRemaining()) {
            int b = bb.get() & 0xff;
            if (b >= 0x80)
                appendEscape(sb, (byte) b);
            else
                sb.append((char) b);
        }
        return sb.toString();
    }

    private static int decode(char c) {
        if ((c >= '0') && (c <= '9'))
            return c - '0';
        if ((c >= 'a') && (c <= 'f'))
            return c - 'a' + 10;
        if ((c >= 'A') && (c <= 'F'))
            return c - 'A' + 10;
        assert false;
        return -1;
    }

    private static byte decode(char c1, char c2) {
        return (byte) (((decode(c1) & 0xf) << 4)
                | ((decode(c2) & 0xf) << 0));
    }

    // Evaluates all escapes in s, applying UTF-8 decoding if needed.  Assumes
    // that escapes are well-formed syntactically, i.e., of the form %XX.  If a
    // sequence of escaped octets is not valid UTF-8 then the erroneous octets
    // are replaced with '\uFFFD'.
    // Exception: any "%" found between "[]" is left alone. It is an IPv6 literal
    //            with a scope_id
    //
    private static String decode(String s) {
        return decode(s, true);
    }

    // This method was introduced as a generalization of URI.decode method
    // to provide a fix for JDK-8037396
    private static String decode(String s, boolean ignorePercentInBrackets) {
        if (s == null)
            return s;
        int n = s.length();
        if (n == 0)
            return s;
        if (s.indexOf('%') < 0)
            return s;

        StringBuilder sb = new StringBuilder(n);
        ByteBuffer bb = ByteBuffer.allocate(n);
        CharBuffer cb = CharBuffer.allocate(n);
        CharsetDecoder dec = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);

        // This is not horribly efficient, but it will do for now
        char c = s.charAt(0);
        boolean betweenBrackets = false;

        for (int i = 0; i < n; ) {
            assert c == s.charAt(i);    // Loop invariant
            if (c == '[') {
                betweenBrackets = true;
            } else if (betweenBrackets && c == ']') {
                betweenBrackets = false;
            }
            if (c != '%' || (betweenBrackets && ignorePercentInBrackets)) {
                sb.append(c);
                if (++i >= n)
                    break;
                c = s.charAt(i);
                continue;
            }
            bb.clear();
            int ui = i;
            for (; ; ) {
                assert (n - i >= 2);
                bb.put(decode(s.charAt(++i), s.charAt(++i)));
                if (++i >= n)
                    break;
                c = s.charAt(i);
                if (c != '%')
                    break;
            }
            bb.flip();
            cb.clear();
            dec.reset();
            CoderResult cr = dec.decode(bb, cb, true);
            assert cr.isUnderflow();
            cr = dec.flush(cb);
            assert cr.isUnderflow();
            sb.append(cb.flip().toString());
        }

        return sb.toString();
    }

    public static class PathTemplate {
        private final String template;
        private final Map<String, String> variables = new HashMap<>();

        private PathTemplate(String template) {
            this.template = template;
        }

        public PathTemplate params(Map<String, String> variables) {
            this.variables.putAll(variables);
            return this;
        }

        public PathTemplate param(String name, Object value) {
            this.variables.put(name, value.toString());
            return this;
        }

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
