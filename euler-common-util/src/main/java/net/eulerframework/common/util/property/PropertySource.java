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
package net.eulerframework.common.util.property;

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

import net.eulerframework.common.base.log.LogSupport;
import net.eulerframework.common.util.ArrayUtils;
import net.eulerframework.common.util.CommonUtils;
import net.eulerframework.common.util.StringUtils;
import net.eulerframework.common.util.io.file.FileUtils;

/**
 * 配置文件数据源，支持properties文件和yaml文件，<br>
 * 
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
 *
 */
public class PropertySource extends LogSupport {
    private static final String[] PROPERTY_FILE_EXTENSIONS = { ".properties", ".property" };
    private static final String[] YAML_FILE_EXTENSIONS = { ".yml", ".yaml" };

    private Yaml yaml = new Yaml();
    private Properties props;

    /**
     * 新建空Properties文件数据源
     */
    public PropertySource() {
        this.props = new Properties();
    }

    /**
     * 新建Properties文件数据源, 并读取一个uri列表中的数据, 后读的会覆盖先读的
     * 
     * @param uri
     * @throws IOException
     * @throws URISyntaxException
     */
    public PropertySource(String... uri) throws IOException, URISyntaxException {
        this();
        this.loadProperties(uri);
    }

    /**
     * 读取多个Properties文件, 后读的会覆盖先读的
     * 
     * @param uri
     * @throws URISyntaxException
     * @throws IOException
     */
    public void loadProperties(String... uri) throws URISyntaxException, IOException {
        for (String each : uri) {
            this.loadProperties(each);
        }
    }

    public void loadProperties(String uri) throws URISyntaxException, IOException {
        try {
            this.logger.info("Load property file: " + uri);
            String extension = FileUtils.extractFileExtension(uri);
            if (ArrayUtils.contains(PROPERTY_FILE_EXTENSIONS, extension)) {
                this.logger.info("Is a property file, load it.");
                try (InputStream inputStream = FileUtils.getInputStreamFromUri(uri);
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                    this.props.load(bufferedReader);
                }
            } else if (ArrayUtils.contains(YAML_FILE_EXTENSIONS, extension)) {
                this.logger.info("Is a yaml file, load it.");
                this.loadYamlFileToProperties(this.props, uri);
            } else {
                this.logger.warn("Unsupported file, abord.");
            }
        } catch (FileNotFoundException e1) {
            this.logger.warn("Property file " + uri + " does not exist, abord.");
        }
    }

    private void loadYamlFileToProperties(Properties properties, String uri) throws IOException, URISyntaxException {
        try (InputStream inputStream = FileUtils.getInputStreamFromUri(uri)) {
            @SuppressWarnings("unchecked")
            LinkedHashMap<String, Object> yamlMap = yaml.loadAs(inputStream, LinkedHashMap.class);
            LinkedHashMap<String, Object> flatMap = this.flatYamlMap(yamlMap);

            if(!CommonUtils.isEmpty(flatMap)) {
                flatMap.forEach((key, value) -> {
                    if(value != null) {
                        this.props.put(key, flatMap.get(key));
                    } else {
                        this.logger.warn("Load config " + key + " is null, but property dose not support null value, ignore it.");
                    }
                });
            }
        }
    }
    
    private LinkedHashMap<String, Object> flatYamlMap(LinkedHashMap<String, Object> yamlMap) {

        if (CommonUtils.isEmpty(yamlMap)) {
            return null;
        }
        
        LinkedHashMap<String, Object> flatMap = new LinkedHashMap<>();
        
        yamlMap.forEach((key, obj) -> {
            if (obj instanceof LinkedHashMap<?, ?>) {
                @SuppressWarnings("unchecked")
                LinkedHashMap<String, Object> subFlatMap = this.flatYamlMap((LinkedHashMap<String, Object>) obj);

                if (!CommonUtils.isEmpty(subFlatMap)) {
                    for (String subFlatMapKey : subFlatMap.keySet()) {
                        Object value = subFlatMap.get(subFlatMapKey);
                        String fullKey = key + "." + subFlatMapKey;
                        flatMap.put(fullKey, value);
                    }
                }
            } else if (obj instanceof ArrayList<?>) {
                @SuppressWarnings("unchecked")
                ArrayList<Object> list = (ArrayList<Object>) obj;
                if(!CommonUtils.isEmpty(list)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    list.forEach(each -> stringBuilder.append(String.valueOf(each)).append(","));
                    flatMap.put(
                            StringUtils.toLowerCamelCase(key, "-"), 
                            stringBuilder.subSequence(0, stringBuilder.length() - 1).toString());
                }
            } else {
                flatMap.put(StringUtils.toLowerCamelCase(key, "-"), obj == null ? "" : String.valueOf(obj));
            }
        });

        return flatMap;
    }

    public Object getProperty(String key) throws PropertyNotFoundException {
        Object value = this.props.get(key);
        if (value == null) {
            throw new PropertyNotFoundException("Property not found: " + key);
        }
        return value;
    }

}
