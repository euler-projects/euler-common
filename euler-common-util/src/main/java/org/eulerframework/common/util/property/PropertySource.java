/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eulerframework.common.util.property;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Properties;

import org.yaml.snakeyaml.Yaml;

import org.eulerframework.common.base.log.LogSupport;
import org.eulerframework.common.util.ArrayUtils;
import org.eulerframework.common.util.CommonUtils;
import org.eulerframework.common.util.StringUtils;
import org.eulerframework.common.util.io.file.FileUtils;

/**
 * 配置文件数据源，支持properties文件和yaml文件，<br>
 * <p>
 * properties文件的key按原始格式读取<br>
 * <br>
 * yaml文件会按层级关系转换为平铺的key-value结构，层级之间的key用<code>.</code>分隔，
 * key会统一转换成小驼峰结构，<code>-</code>会作为单词分隔符对待，
 * 例如<code>exam-key<code>会转换为</code>examKey</code><br>
 * <br>
 * 为了与properties文件解析逻辑一致：
 * yaml文件的数组类型数据会被转为用<code>,</code>分隔的字符串；
 * yaml文件如果value为<code>null</code>，将被存储为空字符串。
 *
 * @author cFrost
 */
public interface PropertySource {
    Object getProperty(String key) throws PropertyNotFoundException;
    <T> T getProperty(String key, Class<T> requireType) throws PropertyNotFoundException;
}
