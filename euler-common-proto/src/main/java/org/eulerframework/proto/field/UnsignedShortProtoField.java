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

public class UnsignedShortProtoField extends AbstractFixedLengthProtoField<Short>
        implements NumberProtoField<Short>, FixedLengthProtoField<Short> {
    private short data;

    public UnsignedShortProtoField() {
        this((short) 0);
    }

    public UnsignedShortProtoField(short s) {
        super(2);
        this.data = s;
    }

    @Override
    public Short value() {
        return this.data;
    }

    @Override
    public byte byteValue() {
        return NumberUtils.toByte(this.data);
    }

    @Override
    public short shorValue() {
        return this.data;
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
    public void read(Short object) {
        if (object != null) {
            this.data = object;
        }
    }

    @Override
    public void read(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("Bytes is null");
        }

        if (bytes.length != 2) {
            throw new IllegalArgumentException("Invalid bytes length " + bytes.length);
        }

        this.data = (short) ((bytes[0] & 0xFF) << 8 | bytes[1] & 0xFF);
    }

    @Override
    public void read(byte b) {
        this.data = NumberUtils.toUnsignedShort(b);
    }

    @Override
    public void read(short s) {
        this.data = s;
    }

    @Override
    public void read(int i) {
        this.data = NumberUtils.toUnsignedShort(i);
    }

    @Override
    public void read(long l) {
        this.data = NumberUtils.toUnsignedShort(l);
    }

    @Override
    public byte[] writeAsBytes() {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (this.data >>> 8);
        bytes[1] = (byte) this.data;
        return bytes;
    }
}
