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
package net.eulerframework.common;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.Assert;
import net.eulerframework.common.util.property.PropertyNotFoundException;
import net.eulerframework.common.util.property.PropertySource;

/**
 * @author cFrost
 *
 */
public class PropertySourceTest {
    
    public static PropertySource propertySource;

    @BeforeClass
    public static void init() throws IOException, URISyntaxException{
        propertySource = new PropertySource("/config-mime.properties", "/test.yml", "/a.yml", "/a.properties", "/log4j.xml");
    }
    
    @Test
    public void testPropertyRead() throws IOException, URISyntaxException, PropertyNotFoundException {
        Assert.assertEquals("application/vnd.lotus-1-2-3;attachment", propertySource.getProperty(".123"));
        Assert.assertEquals("value1", propertySource.getProperty("root.prop1"));
        Assert.assertEquals("value2", propertySource.getProperty("root.child.childProp2"));
        Assert.assertEquals("value3", propertySource.getProperty("root.child.childProp3"));
    }
    

    @Test(expected = PropertyNotFoundException.class)
    public void testPropertyNotFoundException() throws PropertyNotFoundException {
        propertySource.getProperty("root.child.empty");
    }
    
    @Test
    public void testCoverRead() throws IOException, URISyntaxException, PropertyNotFoundException {
        PropertySource propertySource = new PropertySource("/config-mime.properties");
        Assert.assertEquals("image/jpeg;inline", propertySource.getProperty(".jpg"));
        propertySource.loadProperties("/test.yml");
        Assert.assertEquals("covered", propertySource.getProperty(".jpg"));
    }
}
