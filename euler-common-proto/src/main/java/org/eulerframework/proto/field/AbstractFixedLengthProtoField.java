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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractFixedLengthProtoField<T> implements FixedLengthProtoField<T> {
    private final int length;

    protected AbstractFixedLengthProtoField(int length) {
        this.length = length;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public void read(InputStream in) throws IOException {
        if (in.available() < this.length()) {
            throw new IllegalStateException("Not enough bytes to read, abort.");
        }

        byte[] bytes = new byte[this.length()];
        int r;
        if ((r = in.read(bytes)) < this.length()) {
            throw new IllegalStateException(
                    "Except " + this.length() + " bytes but only " + r + " bytes read.");
        }

        this.read(bytes);
    }

    @Override
    public void write(OutputStream out) throws IOException {
        out.write(this.writeAsBytes());
    }
}
