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

import org.eulerframework.common.util.NumberUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ByteProtoField extends AbstractFixedLengthProtoField<Byte>
        implements NumberProtoField<Byte>, FixedLengthProtoField<Byte> {
    private byte data;

    public ByteProtoField() {
        this((byte) 0);
    }

    public ByteProtoField(byte b) {
        super(1);
        this.data = b;
    }

    @Override
    public Byte value() {
        return this.data;
    }

    @Override
    public byte byteValue() {
        return this.data;
    }

    @Override
    public short shorValue() {
        return NumberUtils.toUnsignedShort(this.data);
    }

    @Override
    public int intValue() {
        return NumberUtils.toUnsignedInt(this.data);
    }

    @Override
    public long longValue() {
        return NumberUtils.toUnsignedLong(this.data);
    }

    @Override
    public void read(Byte object) {
        if (object != null) {
            this.data = object;
        }
    }

    @Override
    public void read(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("Bytes is null");
        }

        if (bytes.length != this.length()) {
            throw new IllegalArgumentException("Invalid bytes length " + bytes.length);
        }

        this.data = bytes[0];
    }

    @Override
    public void read(byte b) {
        this.data = b;
    }

    @Override
    public void read(short s) {
        this.data = NumberUtils.toByte(s);
    }

    @Override
    public void read(int i) {
        this.data = NumberUtils.toByte(i);
    }

    @Override
    public void read(long l) {
        this.data = NumberUtils.toByte(l);
    }

    @Override
    public void read(InputStream in) throws IOException {
        if (in.available() < this.length()) {
            throw new IllegalStateException("Not enough bytes to read, abort.");
        }

        int data;
        if ((data = in.read()) < 0) {
            throw new IllegalStateException("Can not read any data from the input stream.");
        }

        this.read(data);
    }

    @Override
    public byte[] writeAsBytes() {
        byte[] bytes = new byte[1];
        bytes[0] = this.data;
        return bytes;
    }

    @Override
    public void write(OutputStream out) throws IOException {
        out.write(this.data);
    }
}
