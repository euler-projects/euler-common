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
import org.eulerframework.common.util.type.TypeUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.Locale;

public class PropertyReader extends LogSupport {

    private final PropertySource propertySource;
    
    /**
     * 初始化读取器，使用classpath根目录作为搜索位置，config.properties作为文件名
     */
    public PropertyReader(PropertySource propertySource) {
        this.propertySource = propertySource;
    }

    public <T> T get(String property, Class<T> requireType) throws PropertyNotFoundException {
        T value = propertySource.getProperty(property, requireType);
        if(logger.isDebugEnabled()) {
            logger.debug("Load config: {} = {}", property, TypeUtils.asString(value));
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String property, T defaultValue) {
        try {
            return (T) this.get(property, defaultValue.getClass());
        } catch (PropertyNotFoundException e) {
            if(logger.isWarnEnabled()) {
                logger.warn("Couldn't load "+ property +" , use " + TypeUtils.asString(defaultValue) + " for default.");
            }
            return defaultValue;
        }
    }

    public String getString(String property) throws PropertyNotFoundException {
        return this.get(property, String.class);
    }
    
    public String getString(String property, String defaultValue) {
        try {
            return this.get(property, String.class);
        } catch (PropertyNotFoundException e) {
            logger.warn("Couldn't load "+ property +" , use " + defaultValue + " for default.");
            return defaultValue;
        }
    }

    public Locale getLocaleValue(String property, Locale defaultValue) {
        try {
            return this.get(property, Locale.class);
        } catch (PropertyNotFoundException e) {
            logger.warn("Couldn't load "+ property +" , use " + defaultValue + " for default.");
            return defaultValue;
        }
    }

    public Duration getDurationValue(String property, Duration defaultValue) {
        try {
            return this.get(property, Duration.class);
        } catch (PropertyNotFoundException e) {
            logger.warn("Couldn't load "+ property +" , use " + defaultValue + " for default.");
            return defaultValue;
        }
    }

    public Locale[] getLocaleArrayValue(String property, Locale[] defaultValue) {
        try {
            return this.get(property, Locale[].class);
        } catch (PropertyNotFoundException e) {
            logger.warn("Couldn't load "+ property +" , use " + Arrays.toString(defaultValue) + " for default.");
            return defaultValue;
        }
    }

    public int getIntValue(String property, int defaultValue) {
        try {
            return Integer.parseInt(getString(property));
        } catch (PropertyNotFoundException e) {
            logger.warn("Couldn't load "+ property +" , use " + defaultValue + " for default.");
            return defaultValue;
        }
    }
    
    public long getLongValue(String property, long defaultValue) {
        try {
            return Long.parseLong(getString(property));
        } catch (PropertyNotFoundException e) {
            logger.warn("Couldn't load "+ property +" , use " + defaultValue + " for default.");
            return defaultValue;
        }
    }
    

    public double getDoubleValue(String property, double defaultValue) {
        try {
            return Double.parseDouble(getString(property));
        } catch (PropertyNotFoundException e) {
            logger.warn("Couldn't load "+ property +" , use " + defaultValue + " for default.");
            return defaultValue;
        }
    }

    public boolean getBooleanValue(String property, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(getString(property));
        } catch (PropertyNotFoundException e) {
            logger.warn("Couldn't load "+ property +" , use " + defaultValue + " for default.");
            return defaultValue;
        }
    }
    
    /**
     * 读取枚举类型的配置
     * 
     * @param <T> 待读取的枚举类
     * @param property 参数名
     * @param defaultValue 默认值，在读不到的时候返回此值
     * @param toUpperCase 是否将读取到的字符串转为大写后再转为对应的Enum
     * @return 配置了正确的参数按配置返回，未配置或配置参数不正确返回默认值
     *
     * @deprecated use {@link PropertyReader#get(String, Object)} for instead
     */
    @Deprecated
    public <T extends Enum<T>> T getEnumValue(String property, T defaultValue, boolean toUpperCase) {
        try {
            String configValue = getString(property);
            
            if(toUpperCase)
                configValue = configValue.toUpperCase();
            
            return T.valueOf(defaultValue.getDeclaringClass(), configValue);
        } catch (PropertyNotFoundException e) {
            logger.warn("Couldn't load "+ property +" , use " + defaultValue + " for default.");
            return defaultValue;
        } catch (IllegalArgumentException e) {
            logger.error(property +" was configed as a wrong value.");
            throw new EnumPropertyReadException(e);
        }
    }
}
