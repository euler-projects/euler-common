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

/**
 * 配置数据源
 *
 * @author cFrost
 */
public interface PropertySource {

    /**
     * 获取配置项的值
     *
     * @param key 配置项
     * @return 配置项值
     * @throws PropertyNotFoundException 配置项不存在
     */
    Object getProperty(String key) throws PropertyNotFoundException;

    /**
     * 按指定类型获取配置项的值
     *
     * @param key         配置项
     * @param requireType 配置项的值类型
     * @param <T>         配置项的值类型
     * @return 配置项值
     * @throws PropertyNotFoundException 配置项不存在
     */
    <T> T getProperty(String key, Class<T> requireType) throws PropertyNotFoundException;
}
