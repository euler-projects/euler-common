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

import java.util.Locale;

import org.junit.Test;

import junit.framework.Assert;
import net.eulerframework.common.util.CommonUtils;

public class CommonUtilsTest {

    @Test
    public void test() {
        Assert.assertEquals("/app/dev/", CommonUtils.convertDirToUnixFormat("/app/dev", true));
        Assert.assertEquals("/", CommonUtils.convertDirToUnixFormat("/////", true));
        Assert.assertEquals("/", CommonUtils.convertDirToUnixFormat("/", true));
        Assert.assertEquals("/", CommonUtils.convertDirToUnixFormat("", true));
        Assert.assertEquals("/app/dev/", CommonUtils.convertDirToUnixFormat("/app/dev/", true));
        Assert.assertEquals("D:/app/dev/", CommonUtils.convertDirToUnixFormat("D:\\app\\dev", true));
        Assert.assertEquals("D:/app/dev/", CommonUtils.convertDirToUnixFormat("D:\\app\\dev\\", true));
        Assert.assertEquals("/app/dev", CommonUtils.convertDirToUnixFormat("/app/dev", false));
        Assert.assertEquals("", CommonUtils.convertDirToUnixFormat("/////", false));
        Assert.assertEquals("", CommonUtils.convertDirToUnixFormat("/", false));
        Assert.assertEquals("", CommonUtils.convertDirToUnixFormat("", false));
        Assert.assertEquals("/app/dev", CommonUtils.convertDirToUnixFormat("/app/dev/", false));
        Assert.assertEquals("D:/app/dev", CommonUtils.convertDirToUnixFormat("D:\\app\\dev", false));
        Assert.assertEquals("D:/app/dev", CommonUtils.convertDirToUnixFormat("D:\\app\\dev\\", false));
        

        Assert.assertEquals(Locale.CHINA, CommonUtils.parseLocale("zhdcn"));
        Assert.assertEquals(Locale.CHINA, CommonUtils.parseLocale("zh-cn"));
        Assert.assertEquals(Locale.CHINA, CommonUtils.parseLocale("zh_cn"));
        Assert.assertEquals(Locale.CHINA, CommonUtils.parseLocale("zh-CN"));
        Assert.assertEquals(Locale.CHINA, CommonUtils.parseLocale("zh_CN"));
        Assert.assertEquals(Locale.CHINESE, CommonUtils.parseLocale("zh"));
        Assert.assertEquals("zh-cn", CommonUtils.formatLocal(Locale.CHINA, '-'));
        Assert.assertEquals("zh_cn", CommonUtils.formatLocal(Locale.CHINA, '_'));
        Assert.assertEquals("zh", CommonUtils.formatLocal(Locale.CHINESE, '_'));
        Assert.assertEquals("_cn", CommonUtils.formatLocal(new Locale("", "cn"), '_'));
        Assert.assertEquals("zh", CommonUtils.formatLocal(new Locale("zh", ""), '_'));
    }
}
