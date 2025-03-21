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

package org.eulerframework.common.http.message;

import org.eulerframework.common.http.HeaderElement;
import org.eulerframework.common.http.Param;
import org.eulerframework.common.http.util.Args;
import org.eulerframework.common.http.util.CharArrayBuffer;

/**
 * Default {@link HeaderValueFormatter} implementation.
 * <p>
 * CLAUSE: Most code of this class is copied from Apache HTTP.
 */
public class BasicHeaderValueFormatter implements HeaderValueFormatter {

    public final static BasicHeaderValueFormatter INSTANCE = new BasicHeaderValueFormatter();

    private final static String SEPARATORS = " ;,:@()<>\\\"/[]?={}\t";
    private final static String UNSAFE_CHARS = "\"\\";

    public BasicHeaderValueFormatter() {
        super();
    }

    @Override
    public void formatElements(
            final CharArrayBuffer buffer, final HeaderElement[] elems, final boolean quote) {
        Args.notNull(buffer, "Char array buffer");
        Args.notNull(elems, "Header element array");

        for (int i = 0; i < elems.length; i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            formatHeader(buffer, elems[i], quote);
        }
    }

    @Override
    public void formatHeader(
            final CharArrayBuffer buffer, final HeaderElement elem, final boolean quote) {
        Args.notNull(buffer, "Char array buffer");
        Args.notNull(elem, "Header element");

        buffer.append(elem.getName());
        final String value = elem.getValue();
        if (value != null) {
            buffer.append('=');
            formatValue(buffer, value, quote);
        }

        final int c = elem.getParameterCount();
        if (c > 0) {
            for (int i = 0; i < c; i++) {
                buffer.append("; ");
                formatParam(buffer, elem.getParameter(i), quote);
            }
        }
    }

    @Override
    public void formatParameters(
            final CharArrayBuffer buffer, final Param[] nvps, final boolean quote) {
        Args.notNull(buffer, "Char array buffer");
        Args.notNull(nvps, "Header parameter array");

        for (int i = 0; i < nvps.length; i++) {
            if (i > 0) {
                buffer.append("; ");
            }
            formatParam(buffer, nvps[i], quote);
        }
    }

    @Override
    public void formatParam(
            final CharArrayBuffer buffer, final Param nvp, final boolean quote) {
        Args.notNull(buffer, "Char array buffer");
        Args.notNull(nvp, "Name / value pair");

        buffer.append(nvp.getName());
        final String value = nvp.getValue();
        if (value != null) {
            buffer.append('=');
            formatValue(buffer, value, quote);
        }
    }

    void formatValue(final CharArrayBuffer buffer, final String value, final boolean quote) {

        boolean quoteFlag = quote;
        if (!quoteFlag) {
            for (int i = 0; (i < value.length()) && !quoteFlag; i++) {
                quoteFlag = isSeparator(value.charAt(i));
            }
        }

        if (quoteFlag) {
            buffer.append('"');
        }
        for (int i = 0; i < value.length(); i++) {
            final char ch = value.charAt(i);
            if (isUnsafe(ch)) {
                buffer.append('\\');
            }
            buffer.append(ch);
        }
        if (quoteFlag) {
            buffer.append('"');
        }
    }

    boolean isSeparator(final char ch) {
        return SEPARATORS.indexOf(ch) >= 0;
    }

    boolean isUnsafe(final char ch) {
        return UNSAFE_CHARS.indexOf(ch) >= 0;
    }

}
