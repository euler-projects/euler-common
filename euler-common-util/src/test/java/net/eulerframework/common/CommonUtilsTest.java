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
