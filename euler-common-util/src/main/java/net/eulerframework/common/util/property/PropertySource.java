/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2013-2018 Euler Project 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * For more information, please visit the following website
 * 
 * https://eulerproject.io
 */
package net.eulerframework.common.util.property;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Properties;

import net.eulerframework.common.base.log.LogSupport;
import net.eulerframework.common.util.io.file.FileUtils;

public class PropertySource extends LogSupport {

    private Properties props;
    
    /**
     * 新建空Properties文件数据源
     */
    protected PropertySource() {
        this.props = new Properties();
    }

    /**
     * 新建Properties文件数据源, 并读取一个uri列表中的数据, 后读的会覆盖先读的
     * @param uri
     * @throws IOException
     * @throws URISyntaxException
     */
    protected PropertySource(String... uri) throws IOException, URISyntaxException {
        this();
        this.loadProperties(uri);
    }
    
    protected void loadProperties(String... uri) throws URISyntaxException, IOException {
        for(String each : uri) {
            this.loadProperties(each);
        }
    }
    
    protected void loadProperties(String uri) throws URISyntaxException, IOException {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = FileUtils.getInputStreamFromUri(uri);
            this.logger.info("Load property file: " + uri);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            this.props.load(bufferedReader);
        } catch (FileNotFoundException e1) {
            this.logger.warn("Property file " + uri + " does not exist, abord.");
        } finally {
            if(inputStream != null)
                inputStream.close();
            if(bufferedReader != null)
                bufferedReader.close();
        }
    }

    protected Object getProperty(String key) throws PropertyNotFoundException {
        Object value = this.props.get(key);
        if (value == null) {
            throw new PropertyNotFoundException("Property not found: " + key);
        }
        return value;
    }

}
