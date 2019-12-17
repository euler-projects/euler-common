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

import org.eulerframework.common.base.log.LogSupport;
import org.eulerframework.common.util.Assert;
import org.eulerframework.common.util.type.TypeUtils;

public class PropertyReader extends LogSupport {

    private final PropertySource propertySource;
    
    /**
     * 初始化读取器，使用classpath根目录作为搜索位置，config.properties作为文件名
     */
    public PropertyReader(PropertySource propertySource) {
        this.propertySource = propertySource;
    }

    public Object get(String property) throws PropertyNotFoundException {
        Object value = propertySource.getProperty(property);
        if(logger.isDebugEnabled()) {
            logger.debug("Load config: '{}' = '{}'", property, TypeUtils.asString(value));
        }
        return value;
    }

    public <T> T get(String property, Class<T> requireType) throws PropertyNotFoundException {
        T value = propertySource.getProperty(property, requireType);
        if(logger.isDebugEnabled()) {
            logger.debug("Load config: '{}' = '{}'", property, TypeUtils.asString(value));
        }
        return value;
    }

    public <T> T get(String property, Class<T> requireType, T defaultValue) {
        try {
            return this.get(property, requireType);
        } catch (PropertyNotFoundException e) {
            if(logger.isWarnEnabled()) {
                logger.warn("Couldn't load '{}' , use '{}' for default.", property, TypeUtils.asString(defaultValue));
            }
            return defaultValue;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String property, T defaultValue) {
        Assert.notNull(defaultValue, "defaultValue can not be null");
        return this.get(property, (Class<T>) defaultValue.getClass(), defaultValue);
    }

    public String getString(String property) throws PropertyNotFoundException {
        return this.get(property, String.class);
    }
    
    public String getString(String property, String defaultValue) {
        return this.get(property, String.class, defaultValue);
    }

    public int getIntValue(String property, int defaultValue) {
        try {
            return TypeUtils.convertToInt(this.get(property));
        } catch (PropertyNotFoundException e) {
            logger.warn("Couldn't load '{}' , use '{}' for default.", property, defaultValue);
            return defaultValue;
        }
    }
    
    public long getLongValue(String property, long defaultValue) {
        try {
            return TypeUtils.convertToLong(this.get(property));
        } catch (PropertyNotFoundException e) {
            logger.warn("Couldn't load '{}' , use '{}' for default.", property, defaultValue);
            return defaultValue;
        }
    }
    

    public double getDoubleValue(String property, double defaultValue) {
        try {
            return TypeUtils.convertToDouble(this.get(property));
        } catch (PropertyNotFoundException e) {
            logger.warn("Couldn't load '{}' , use '{}' for default.", property, defaultValue);
            return defaultValue;
        }
    }

    public boolean getBooleanValue(String property, boolean defaultValue) {
        try {
            return TypeUtils.convertToBoolean(this.get(property));
        } catch (PropertyNotFoundException e) {
            logger.warn("Couldn't load '{}' , use '{}' for default.", property, defaultValue);
            return defaultValue;
        }
    }
}
