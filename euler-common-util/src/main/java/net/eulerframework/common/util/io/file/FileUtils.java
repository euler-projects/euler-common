/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2013-2017 cFrost.sun(孙宾, SUN BIN) 
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
package net.eulerframework.common.util.io.file;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import net.eulerframework.common.util.Assert;
import net.eulerframework.common.util.StringUtils;

/**
 * @author cFrost
 *
 */
public abstract class FileUtils {

    /**
     * 获取文件的扩展名
     * @param fileName 带有扩展名的文件名
     * @return 文件扩展名，如果传入的文件名没有扩展名，则返回为{@code null}
     */
    public static String extractFileExtension(String fileName) {
        String extension = null;
        
        if(StringUtils.isEmpty(fileName))
            return extension;
        
        int dot = fileName.lastIndexOf('.');
        if(dot > -1) {
            extension = fileName.substring(dot);
        }
        return extension == null ? null : extension;
    }

    /**
     * 获取不带扩展名的文件名
     * @param fileName 带有扩展名的文件名
     * @return 不带扩展名的文件名
     */
    public static String extractFileNameWithoutExtension(String fileName) {
        String extension = extractFileExtension(fileName);
        if(extension == null) {
            return fileName;
        } else {
            return fileName.substring(0, fileName.lastIndexOf(extension));
        }
    }
    
    private final static String PATH_PREFIX_FILE = "file:";
    private final static String PATH_PREFIX_CLASS_PATH = "classpath:";
    private final static String PATH_PREFIX_ROOT = "/";
    
    public static InputStream getInputStreamFromUri(String uri) throws URISyntaxException, IOException {
        Assert.hasText(uri);
        
        if(uri.startsWith(PATH_PREFIX_FILE)) {
            return new URI(uri).toURL().openStream();
        } else if(uri.startsWith(PATH_PREFIX_CLASS_PATH)) {
            URL url = FileUtils.class.getResource(uri.substring(PATH_PREFIX_CLASS_PATH.length()));
            if(url == null) {
                throw new RuntimeException(uri + " not exists");
            }
            return url.openStream();
        } else if(uri.startsWith(PATH_PREFIX_ROOT)) {
            URL url = FileUtils.class.getResource(uri);
            if(url == null) {
                throw new RuntimeException(uri + " not exists");
            }
            return url.openStream();
        } else {
            throw new RuntimeException(uri + " not supported");
        }
    }

}
