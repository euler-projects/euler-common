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
package org.eulerframework.common.util.property;

import junit.framework.Assert;
import org.eulerframework.common.util.type.DurationStyle;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class PropertyReaderTest {

    private static PropertyReader propertyReader;

    static {
        try {
            propertyReader = new PropertyReader(new FilePropertySource("/config.properties"));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void get() throws PropertyNotFoundException {
        Assert.assertEquals(DurationStyle.SIMPLE, propertyReader.get("enum.durationStyle", DurationStyle.class));
        Assert.assertEquals(DurationStyle.SIMPLE, propertyReader.get("enum.notExists", DurationStyle.SIMPLE));
        Assert.assertEquals(DurationStyle.SIMPLE, propertyReader.get("enum.notExists", DurationStyle.class, DurationStyle.SIMPLE));
    }
}