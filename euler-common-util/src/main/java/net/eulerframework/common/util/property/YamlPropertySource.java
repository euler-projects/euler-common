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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

import net.eulerframework.common.util.StringUtils;
import net.eulerframework.common.util.io.file.FileUtils;

/**
 * @author cFrost
 *
 */
public class YamlPropertySource {
    
    private LinkedHashMap<String, Object> map;
    
    public YamlPropertySource(String... uri) throws IOException, URISyntaxException {
        this.loadProperties(uri);
    }
    
    public void loadProperties(String... uri) throws URISyntaxException, IOException {
        for(String each : uri) {
            this.loadProperties(each);
        }
    }

    protected void loadProperties(String uri) throws URISyntaxException, IOException {
        try(InputStream inputStream = FileUtils.getInputStreamFromUri(uri)) {
            //this.logger.info("Load property file: " + uri);
            Yaml yaml = new Yaml();
            LinkedHashMap<String, Object> map = yaml.loadAs(inputStream, LinkedHashMap.class);
            this.map = this.convertToLowerCamelCaseKeyLinkedHashMap(map);
        } catch (FileNotFoundException e1) {
            //this.logger.warn("Property file " + uri + " does not exist, abord.");
        }
    }
    
    @SuppressWarnings("unchecked")
    private LinkedHashMap<String, Object> convertToLowerCamelCaseKeyLinkedHashMap(LinkedHashMap<String, Object> map) {
        LinkedHashMap<String, Object> ret = new LinkedHashMap<>();
        Set<String> keySet = map.keySet();
        
        for(String key : keySet) {
            Object value = map.get(key);
            String lowerCamelCaseKey = StringUtils.toLowerCamelCase(key, "-");
            if(value instanceof LinkedHashMap<?, ?>) {
                value = convertToLowerCamelCaseKeyLinkedHashMap((LinkedHashMap<String, Object>)value);
            }
            ret.put(lowerCamelCaseKey, value);
        }
        
        return ret;
    }
    
    public Object getProperty(String key) throws PropertyNotFoundException {
        if(key.indexOf(".") > 0) {
            String rootkey = key.split("\\.")[0];
            String ckey = key.substring(key.indexOf(".") + 1);
            return this.getProperty((HashMap<String, Object>)map.get(rootkey), ckey);
        } else {
            return map.get(key);
        }
    }
    
    private Object getProperty(HashMap<String, Object> map, String key) {
        if(key.indexOf(".") > 0) {
            String rootkey = key.split("\\.")[0];
            String ckey = key.substring(key.indexOf(".") + 1);
            return this.getProperty((HashMap<String, Object>)map.get(rootkey), ckey);
        } else {
            return map.get(key);
        }
    }
}
