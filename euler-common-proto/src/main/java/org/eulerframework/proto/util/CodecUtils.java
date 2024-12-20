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
package org.eulerframework.proto.util;

import io.netty.util.internal.StringUtil;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CodecUtils {
    public static void encodeBcd(OutputStream out, CharSequence cs, int length) throws IOException {
        int charLength = cs.length();
        int minimumLength = (charLength >> 1) + (charLength & 0x01);
        if (length < minimumLength) {
            throw new IllegalArgumentException(
                    "A string of length " + charLength + " requires at least " + minimumLength + " bytes");
        }

        final CharSequence fcs = (charLength & 0x01) == 0 ? cs : "0" + cs;

        int paddingByteLength = length - (fcs.length() >> 1);
        for (int i = 0; i < paddingByteLength; i++) {
            out.write(0x00);
        }

        for (int i = 0; i < fcs.length(); i += 2) {
            out.write(StringUtil.decodeHexByte(fcs, i));
        }
    }

    public static byte[] encodeBcd(CharSequence cs, int length) {
        int numberLength = cs.length();
        int minimumBytes = (numberLength >> 1) + (numberLength & 0x01);
        if (length < minimumBytes) {
            throw new IllegalArgumentException(
                    "A string of length " + numberLength + " requires at least " + minimumBytes + " bytes");
        }

        byte[] bytes = new byte[length];
        int byteIndex = length - 1;
        for (int i = numberLength - 2; i >= 0; i -= 2) {
            bytes[byteIndex--] = StringUtil.decodeHexByte(cs, i);
        }
        if (byteIndex >= length - minimumBytes) {
            String firstNum = "0" + cs.charAt(0);
            bytes[byteIndex] = StringUtil.decodeHexByte(firstNum, 0);
        }
        return bytes;
    }

    public static String decodeBcd(InputStream in, int length) throws IOException {
        StringBuilder result = new StringBuilder();
        while (length-- > 0) {
            int b;
            if ((b = in.read()) < 0) {
                throw new EOFException();
            }
            StringUtil.byteToHexStringPadded(result, b);
        }
        return result.toString();
    }

    public static String decodeBcd(byte[] bcd) {
        return StringUtil.toHexStringPadded(bcd);
    }

    public static CharSequence leftStrip(CharSequence string, char c) {
        if (string == null || string.length() == 0 || c != string.charAt(0)) {
            return string;
        }

        int stripIndex = 0;
        while (string.charAt(++stripIndex) == c) ;
        return string.subSequence(stripIndex, string.length());
    }

    public static String toHexString(byte[] bytes) {
        return toHexString(bytes, "", " ");
    }

    public static String toHexString(byte[] bytes, CharSequence prefix, CharSequence delimiter) {
        if (null == bytes) {
            return null;
        }

        if ((prefix == null || prefix.length() == 0 ) && (delimiter == null || delimiter.length() == 0)) {
            return StringUtil.toHexString(bytes);
        }

        if ((delimiter == null || delimiter.length() == 0)) {
            StringBuilder builder = new StringBuilder();
            for (byte aByte : bytes) {
                builder.append(prefix).append(StringUtil.byteToHexStringPadded(aByte));
            }
            return builder.toString();
        }

        String[] hexNums = new String[bytes.length];

        if (prefix == null || prefix.length() == 0) {
            for (int i = 0; i < bytes.length; i++) {
                hexNums[i] = StringUtil.byteToHexStringPadded(bytes[i]);
            }
        } else {
            for (int i = 0; i < bytes.length; i++) {
                hexNums[i] = prefix + StringUtil.byteToHexStringPadded(bytes[i]);
            }
        }

        return String.join(delimiter, hexNums);
    }
}
