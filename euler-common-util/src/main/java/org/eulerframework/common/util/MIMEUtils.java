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
package org.eulerframework.common.util;

import org.eulerframework.common.util.property.FilePropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eulerframework.common.util.property.InvalidPropertyValueException;
import org.eulerframework.common.util.property.PropertyReader;

import java.io.IOException;
import java.net.URISyntaxException;

public abstract class MIMEUtils {
    protected static final Logger LOGGER = LoggerFactory.getLogger(MIMEUtils.class);
    
    private static final String DEFAULT_CONFIG_VALUE = "application/octet-stream;attachment";
    private static final MIME DEFAULT_MIME = new MIME(DEFAULT_CONFIG_VALUE);

    private static final PropertyReader properties;

    static {
        try {
            properties = new PropertyReader(new FilePropertySource("/config-mime.properties"));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static MIME getDefaultMIME() {
        return DEFAULT_MIME;
    }

    public static MIME getMIME(String extension) {
        Assert.hasText(extension, "extension must not null");
        if(!extension.startsWith(".")) {
            extension = "." + extension;
        }
        return new MIME(properties.getString(extension.toLowerCase(), DEFAULT_CONFIG_VALUE));
    }
    
    public static class MIME {
        private final static String SPLIT_CHAR = ";";
        
        private String contentType;
        private String contentDisposition;
        
        public MIME() {}
        
        public MIME(String configValue) {
            if(StringUtils.isEmpty(configValue) || configValue.indexOf(SPLIT_CHAR) < 0) {
                throw new InvalidPropertyValueException(configValue);
            }
            
            String[] configArray = configValue.split(SPLIT_CHAR);
            
            if(configArray.length < 2) {
                throw new InvalidPropertyValueException(configValue);
            }
            
            this.contentType = configArray[0];
            this.contentDisposition = configArray[1];
        }
        
        public String getContentType() {
            return contentType;
        }
        public void setContentType(String contentType) {
            this.contentType = contentType;
        }
        public String getContentDisposition() {
            return contentDisposition;
        }
        public void setContentDisposition(String contentDisposition) {
            this.contentDisposition = contentDisposition;
        }   
    }
}
