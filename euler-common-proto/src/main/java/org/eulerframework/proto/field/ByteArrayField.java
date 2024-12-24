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

public class ByteArrayField extends AbstractFixedLengthProtoField<byte[]>
        implements FixedLengthProtoField<byte[]> {
    private byte[] data;

    public ByteArrayField(int length) {
        super(length);
    }

    @Override
    public byte[] value() {
        return this.writeAsBytes();
    }

    @Override
    public void read(byte[] value) {
        byte[] tmp = new byte[this.length()];
        System.arraycopy(value, 0, tmp, 0, Math.min(this.length(), value.length));
        this.data = tmp;
    }

    @Override
    public byte[] writeAsBytes() {
        byte[] tmp = new byte[this.length()];
        System.arraycopy(this.data, 0, tmp, 0, Math.min(this.length(), this.data.length));
        return tmp;
    }
}
