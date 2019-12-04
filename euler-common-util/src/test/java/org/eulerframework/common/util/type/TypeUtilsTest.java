/*
 * Copyright 2013-2019 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.eulerframework.common.util.type;

import org.eulerframework.common.util.type.TypeUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class TypeUtilsTest {

    @Test
    public void convert() {
        Assert.assertNull(TypeUtils.convert(null, Date.class));
        java.sql.Date sqlDate = new java.sql.Date(123L);
        Assert.assertEquals(new Date(123L), TypeUtils.convert(sqlDate, Date.class));
        java.sql.Date[] sqlDateArray = new java.sql.Date[]{new java.sql.Date(1L), new java.sql.Date(2L)};
        Assert.assertArrayEquals(new Date[]{new Date(1L), new Date(2L)}, TypeUtils.convert(sqlDateArray, Date[].class));

        Assert.assertEquals(DurationStyle.SIMPLE, TypeUtils.convert("SiMPlE", DurationStyle.class));

        Assert.assertArrayEquals(new Integer[] {1, 2, 3}, TypeUtils.convert("1, 2, 3", Integer[].class));
        Assert.assertEquals(new Integer(1), TypeUtils.convert("1", Integer.class));
        int i = TypeUtils.convert("1", int.class);
        Assert.assertEquals(1, i);
    }

}