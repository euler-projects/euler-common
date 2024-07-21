/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.eulerframework.common.http;


import org.eulerframework.common.http.message.BasicHeaderValueFormatter;
import org.eulerframework.common.http.message.BasicHeaderValueParser;
import org.eulerframework.common.http.message.BasicParam;
import org.eulerframework.common.http.message.ParserCursor;
import org.eulerframework.common.http.util.Args;
import org.eulerframework.common.http.util.CharArrayBuffer;
import org.eulerframework.common.http.util.TextUtils;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.*;

/**
 * Content type information consisting of a MIME type and an optional charset.
 * <p>
 * This class makes no attempts to verify validity of the MIME type.
 * The input parameters of the {@link #create(String, String)} method, however, may not
 * contain characters {@code <">, <;>, <,>} reserved by the HTTP specification.
 * <p>
 * CLAUSE: Most code of this class is copied from Apache HTTP.
 */
public final class ContentType implements Serializable {

    /**
     * Header name of {@code Content-Type}
     */
    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    /**
     * Param that represent {@code charset} constant.
     */
    private static final String CHARSET = "charset";

    /**
     * Public constant media type for {@code application/x-www-form-urlencoded}.
     */
    public static final ContentType APPLICATION_FORM_URLENCODED = create("application/x-www-form-urlencoded");

    /**
     * Public constant media type for {@code multipart/form-data}.
     */
    public static final ContentType MULTIPART_FORM_DATA = create("multipart/form-data");

    /**
     * Public constant media type for {@code application/json}.
     */
    public static final ContentType APPLICATION_JSON = create("application/json");

    /**
     * Public constant media type for {@code application/json; charset=UTF-8}.
     *
     * @deprecated since major browsers like Chrome
     * <a href="https://bugs.chromium.org/p/chromium/issues/detail?id=438464">
     * now comply with the specification</a> and interpret correctly UTF-8 special
     * characters without requiring a {@code charset=UTF-8} parameter.
     */
    @Deprecated
    public static final ContentType APPLICATION_JSON_UTF8 = APPLICATION_JSON.withCharset(StandardCharsets.UTF_8);

    /**
     * Public constant media type for {@code application/xml}.
     */
    public static final ContentType APPLICATION_XML = create("application/xml");

    /**
     * Public constant media type for {@code application/octet-stream}.
     */
    public static final ContentType APPLICATION_OCTET_STREAM = create("application/octet-stream");

    /**
     * Public constant media type for {@code text/html}.
     */
    public static final ContentType TEXT_HTML = create("text/html");

    /**
     * Public constant media type for {@code text/xml}.
     */
    public static final ContentType TEXT_XML = create("text/xml");

    /**
     * Public constant media type for {@code text/plain}.
     */
    public static final ContentType TEXT_PLAIN = create("text/plain");

    /**
     * An empty immutable {@code Param} array.
     */
    private static final Param[] EMPTY_NAME_VALUE_PAIR_ARRAY = {};

    // defaults
    public static final ContentType DEFAULT_TEXT = TEXT_PLAIN;
    public static final ContentType DEFAULT_BINARY = APPLICATION_OCTET_STREAM;

    private final String mimeType;
    private final Charset charset;
    private final Param[] params;

    ContentType(
            final String mimeType,
            final Charset charset) {
        this.mimeType = mimeType;
        this.charset = charset;
        this.params = null;
    }

    ContentType(
            final String mimeType,
            final Charset charset,
            final Param[] params) {
        this.mimeType = mimeType;
        this.charset = charset;
        this.params = params;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public String getParameter(final String name) {
        Args.notEmpty(name, "Parameter name");
        if (this.params == null) {
            return null;
        }
        for (final Param param : this.params) {
            if (param.getName().equalsIgnoreCase(name)) {
                return param.getValue();
            }
        }
        return null;
    }

    /**
     * Generates textual representation of this content type which can be used as the value
     * of a {@code Content-Type} header.
     */
    @Override
    public String toString() {
        final CharArrayBuffer buf = new CharArrayBuffer(64);
        buf.append(this.mimeType);
        if (this.params != null) {
            buf.append("; ");
            BasicHeaderValueFormatter.INSTANCE.formatParameters(buf, this.params, false);
        } else if (this.charset != null) {
            buf.append("; charset=");
            buf.append(this.charset.name());
        }
        return buf.toString();
    }

    private static boolean valid(final String s) {
        for (int i = 0; i < s.length(); i++) {
            final char ch = s.charAt(i);
            if (ch == '"' || ch == ',' || ch == ';') {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new instance of {@link ContentType}.
     *
     * @param mimeType MIME type. It may not be {@code null} or empty. It may not contain
     *                 characters {@code <">, <;>, <,>} reserved by the HTTP specification.
     * @param charset  charset.
     * @return content type
     */
    public static ContentType create(final String mimeType, final Charset charset) {
        final String normalizedMimeType = TextUtils.toLowerCase(Args.notBlank(mimeType, "MIME type"));
        Args.check(valid(normalizedMimeType), "MIME type may not contain reserved characters");
        return new ContentType(normalizedMimeType, charset);
    }

    /**
     * Creates a new instance of {@link ContentType} without a charset.
     *
     * @param mimeType MIME type. It may not be {@code null} or empty. It may not contain
     *                 characters {@code <">, <;>, <,>} reserved by the HTTP specification.
     * @return content type
     */
    public static ContentType create(final String mimeType) {
        return create(mimeType, (Charset) null);
    }

    /**
     * Creates a new instance of {@link ContentType}.
     *
     * @param mimeType MIME type. It may not be {@code null} or empty. It may not contain
     *                 characters {@code <">, <;>, <,>} reserved by the HTTP specification.
     * @param charset  charset. It may not contain characters {@code <">, <;>, <,>} reserved by the HTTP
     *                 specification. This parameter is optional.
     * @return content type
     * @throws UnsupportedCharsetException Thrown when the named charset is not available in
     *                                     this instance of the Java virtual machine
     */
    public static ContentType create(
            final String mimeType, final String charset) throws UnsupportedCharsetException {
        return create(mimeType, !TextUtils.isBlank(charset) ? Charset.forName(charset) : null);
    }

    private static ContentType create(final HeaderElement helem, final boolean strict) {
        final String mimeType = helem.getName();
        if (TextUtils.isBlank(mimeType)) {
            return null;
        }
        return create(helem.getName(), helem.getParameters(), strict);
    }

    private static ContentType create(final String mimeType, final Param[] params, final boolean strict) {
        Charset charset = null;
        if (params != null) {
            for (final Param param : params) {
                if (param.getName().equalsIgnoreCase(CHARSET)) {
                    final String s = param.getValue();
                    if (!TextUtils.isBlank(s)) {
                        try {
                            charset = Charset.forName(s);
                        } catch (final UnsupportedCharsetException ex) {
                            if (strict) {
                                throw ex;
                            }
                        }
                    }
                    break;
                }
            }
        }
        return new ContentType(mimeType, charset, params != null && params.length > 0 ? params : null);
    }

    /**
     * Creates a new instance of {@link ContentType} with the given parameters.
     *
     * @param mimeType MIME type. It may not be {@code null} or empty. It may not contain
     *                 characters {@code <">, <;>, <,>} reserved by the HTTP specification.
     * @param params   parameters.
     * @return content type
     */
    public static ContentType create(
            final String mimeType, final Param... params) throws UnsupportedCharsetException {
        final String type = TextUtils.toLowerCase(Args.notBlank(mimeType, "MIME type"));
        Args.check(valid(type), "MIME type may not contain reserved characters");
        return create(mimeType, params != null ? params.clone() : null, true);
    }

    /**
     * Parses textual representation of {@code Content-Type} value.
     *
     * @param s text
     * @return content type {@code Content-Type} value or null.
     * @throws UnsupportedCharsetException Thrown when the named charset is not available in
     *                                     this instance of the Java virtual machine
     */
    public static ContentType parse(final CharSequence s) throws UnsupportedCharsetException {
        return parse(s, true);
    }

    /**
     * Parses textual representation of {@code Content-Type} value ignoring invalid charsets.
     *
     * @param s text
     * @return content type {@code Content-Type} value or null.
     * @throws UnsupportedCharsetException Thrown when the named charset is not available in
     *                                     this instance of the Java virtual machine
     */
    public static ContentType parseLenient(final CharSequence s) throws UnsupportedCharsetException {
        return parse(s, false);
    }

    private static ContentType parse(final CharSequence s, final boolean strict) throws UnsupportedCharsetException {
        if (TextUtils.isBlank(s)) {
            return null;
        }
        final ParserCursor cursor = new ParserCursor(0, s.length());
        final HeaderElement[] elements = BasicHeaderValueParser.INSTANCE.parseElements(s, cursor);
        if (elements.length > 0) {
            return create(elements[0], strict);
        }
        return null;
    }

    /**
     * Creates a new instance with this MIME type and the given Charset.
     *
     * @param charset charset
     * @return a new instance with this MIME type and the given Charset.
     */
    public ContentType withCharset(final Charset charset) {
        return create(this.getMimeType(), charset);
    }

    /**
     * Creates a new instance with this MIME type and the given Charset name.
     *
     * @param charset name
     * @return a new instance with this MIME type and the given Charset name.
     * @throws UnsupportedCharsetException Thrown when the named charset is not available in
     *                                     this instance of the Java virtual machine
     */
    public ContentType withCharset(final String charset) {
        return create(this.getMimeType(), charset);
    }

    /**
     * Creates a new instance with this MIME type and the given parameters.
     *
     * @param params parameters.
     * @return a new instance with this MIME type and the given parameters.
     */
    public ContentType withParameters(
            final Param... params) throws UnsupportedCharsetException {
        if (params.length == 0) {
            return this;
        }
        final Map<String, String> paramMap = new LinkedHashMap<>();
        if (this.params != null) {
            for (final Param param : this.params) {
                paramMap.put(param.getName(), param.getValue());
            }
        }
        for (final Param param : params) {
            paramMap.put(param.getName(), param.getValue());
        }
        final List<Param> newParams = new ArrayList<>(paramMap.size() + 1);
        if (this.charset != null && !paramMap.containsKey(CHARSET)) {
            newParams.add(new BasicParam(CHARSET, this.charset.name()));
        }
        for (final Map.Entry<String, String> entry : paramMap.entrySet()) {
            newParams.add(new BasicParam(entry.getKey(), entry.getValue()));
        }
        return create(this.getMimeType(), newParams.toArray(EMPTY_NAME_VALUE_PAIR_ARRAY), true);
    }

    public boolean isSameMimeType(final ContentType contentType) {
        return contentType != null && mimeType.equalsIgnoreCase(contentType.getMimeType());
    }

    public boolean isSameMimeTypeAndCharset(final ContentType contentType) {
        return this.isSameMimeType(contentType) && Objects.equals(charset, contentType.getCharset());
    }
}
