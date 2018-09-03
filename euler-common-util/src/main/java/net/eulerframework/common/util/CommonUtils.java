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
package net.eulerframework.common.util;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

public abstract class CommonUtils {

    /**
     * 程序暂停
     * @param seconds 暂停秒数
     */
    public static void sleep(int seconds) {
        int i = 0;
        while(i++ < seconds) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Sleep... "+ i +"s");
        }
    }

    /**
     * 统一路径为UNIX格式<br>
     * Examples:
     * <pre>
     * convertDirToUnixFormat("\", true) returns "/"
     * convertDirToUnixFormat("D:\floder\", true) returns "D:/floder/"
     * convertDirToUnixFormat("D:\floder\file", true) returns "D:/floder/file/"
     * convertDirToUnixFormat("\", false) returns ""
     * convertDirToUnixFormat("D:\floder\", false) returns "D:/floder"
     * convertDirToUnixFormat("D:\floder\file", false) returns "D:/floder/file"
     * </pre>
     * 
     * @param dir 原始路径
     * @param endWithSlash 结尾是否追加斜杠
     * @return unix路径
     */
    public static String convertDirToUnixFormat(String dir, boolean endWithSlash) {
        if (dir == null)
            return dir;
    
        String unixDir = dir.replace("\\", "/");
        while(unixDir.endsWith("/")) {
            unixDir = unixDir.substring(0, unixDir.length() - 1);
        }
        return endWithSlash ? unixDir + "/" : unixDir;
    }
    
    /**
     * 将表示语言的字符串转成Locale对象
     * @param localeString 表示语言的字符串
     * @return Locale对象
     */
    public static Locale parseLocale(String localeString) {
        Assert.hasText(localeString);
        
        localeString = localeString.toLowerCase();
        
        if(localeString.length() == 5) {
            return new Locale(localeString.substring(0, 2), localeString.substring(3, 5));
        } else if(localeString.length() == 2){
            return new Locale(localeString);
        } else {
            throw new IllegalArgumentException("地区字符串必须符合语言二码-国家/地区二码的格式, 两者之间必须要有一个分隔符,可是为任意ASCII打印字符, 国家/地区二码为可选部分");
        }
    }
    
    public static String formatLocal(Locale locale, char split) {
        Assert.notNull(locale);

        String language = locale.getLanguage();
        String country = locale.getCountry();
        
        if(StringUtils.hasText(country)) {
            return language.toLowerCase() + split + country.toLowerCase();
        } else {
            return language.toLowerCase();
        }
    }
    
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }
    
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }
}
