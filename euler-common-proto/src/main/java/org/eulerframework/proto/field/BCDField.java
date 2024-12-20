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
package org.eulerframework.proto.field;


import org.eulerframework.proto.util.CodecUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BCDField extends AbstractFixedLengthProtoField<CharSequence>
        implements StringProtoField, FixedLengthProtoField<CharSequence> {

    public static BCDField valueOf(Object value, int length) {
        if (value instanceof CharSequence) {
            return BCDField.valueOf((CharSequence) value, length);
        }
        if (value instanceof Number) {
            return BCDField.valueOf((Number) value, length);
        }
        throw new IllegalArgumentException("Unsupported type: " + value.getClass());
    }

    public static BCDField valueOf(CharSequence cs, int length) {
        BCDField field = new BCDField(length);
        field.read(cs);
        return field;
    }

    public static BCDField valueOf(Number num, int length) {
        BCDField field = new BCDField(length);
        field.read(num.toString());
        return field;
    }

    private CharSequence data;

    public BCDField(int length) {
        super(length);
    }

    @Override
    public CharSequence value() {
        return this.data;
    }

    @Override
    public void read(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("Bytes is null");
        }

        if (bytes.length != this.length()) {
            throw new IllegalArgumentException("Invalid bytes length " + bytes.length);
        }

        this.data = CodecUtils.leftStrip(CodecUtils.decodeBcd(bytes), '0');
    }

    @Override
    public void read(CharSequence cs) {
        if (cs == null || cs.length() == 0) {
            this.data = "0";
            return;
        }

        int stringLength = cs.length();
        int minimumBytes = (stringLength >> 1) + (stringLength & 0x01);
        if (this.length() < minimumBytes) {
            throw new IllegalArgumentException(
                    "A string of length " + stringLength + " requires at least " + minimumBytes + " bytes");
        }

        for (int i = 0; i < cs.length(); i++) {
            char c = cs.charAt(i);
            if ((c >= '0' && c <= '9')
                    || (c >= 'a' && c <= 'f')
                    || (c >= 'A' && c <= 'F')) {
                continue;
            }
            throw new IllegalArgumentException("Unsupported character: " + c);
        }

        this.data = CodecUtils.leftStrip(cs, '0');
    }

    @Override
    public void read(InputStream in) throws IOException {
        if (in.available() < this.length()) {
            throw new IllegalStateException("Not enough bytes to read, abort.");
        }

        this.data = CodecUtils.leftStrip(CodecUtils.decodeBcd(in, this.length()), '0');
    }

    @Override
    public byte[] writeAsBytes() {
        return CodecUtils.encodeBcd(this.data, this.length());
    }

    @Override
    public void write(OutputStream out) throws IOException {
        CodecUtils.encodeBcd(out, this.data, this.length());
    }

    @Override
    public String toString() {
        return this.data == null ? null : this.data.toString();
    }
}
