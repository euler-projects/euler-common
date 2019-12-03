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
package org.eulerframework.common.util.property.converter;

import org.eulerframework.common.util.Assert;

public class RawTypeConverterUtils {

    @SuppressWarnings("unchecked")
    public static <T> T convert(Object raw, Class<T> requireType) {
        Assert.notNull(requireType, "requireType is required");

        if(raw == null || requireType.isAssignableFrom(raw.getClass())) {
            return (T) raw;
        }

        return null;
    }
}
