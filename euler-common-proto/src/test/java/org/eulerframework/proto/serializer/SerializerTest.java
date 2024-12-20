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
package org.eulerframework.proto.serializer;

import org.eulerframework.proto.field.ByteProtoField;
import org.eulerframework.proto.field.UnsignedIntegerProtoField;
import org.eulerframework.proto.field.UnsignedShortProtoField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SerializerTest {

    @Test
    void intValue() {
        ByteProtoField byteProtoField = new ByteProtoField();
        byteProtoField.read(0xF9);
        Assertions.assertEquals((byte) 0xF9, byteProtoField.byteValue());
        Assertions.assertEquals((short) 0x00F9, byteProtoField.shorValue());
        Assertions.assertEquals(0x00F9, byteProtoField.intValue());
        Assertions.assertEquals(0x00F9L, byteProtoField.longValue());

        UnsignedShortProtoField unsignedShortProtoField = new UnsignedShortProtoField();
        unsignedShortProtoField.read(0x800070F9);
        Assertions.assertEquals((byte) 0xF9, unsignedShortProtoField.byteValue());
        Assertions.assertEquals((short) 0x70F9, unsignedShortProtoField.shorValue());
        Assertions.assertEquals(0x70F9, unsignedShortProtoField.intValue());
        Assertions.assertEquals(0x70F9L, unsignedShortProtoField.longValue());

        UnsignedIntegerProtoField dwordField = new UnsignedIntegerProtoField();
        dwordField.read(0x800080F9);
        Assertions.assertEquals((byte) 0xF9, dwordField.byteValue());
        Assertions.assertEquals((short) 0x80F9, dwordField.shorValue());
        Assertions.assertEquals(0x800080F9, dwordField.intValue());
        Assertions.assertEquals(0x800080F9L, dwordField.longValue());

        dwordField.read(0x700070F9);
        Assertions.assertEquals((byte) 0xF9, dwordField.byteValue());
        Assertions.assertEquals((short) 0x70F9, dwordField.shorValue());
        Assertions.assertEquals(0x700070F9, dwordField.intValue());
        Assertions.assertEquals(0x700070F9L, dwordField.longValue());
    }

}