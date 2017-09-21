package net.eulerframework.common;

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
    }
}
