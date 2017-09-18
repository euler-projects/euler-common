package net.eulerframework.common;

import org.junit.Test;

import junit.framework.Assert;
import net.eulerframework.common.util.CommonUtils;

public class CommonUtilsTest {

    @Test
    public void test() {
        Assert.assertEquals("/app/dev/", CommonUtils.convertDirToUnixFormat("/app/dev"));
        Assert.assertEquals("/", CommonUtils.convertDirToUnixFormat("/////"));
        Assert.assertEquals("/", CommonUtils.convertDirToUnixFormat("/"));
        Assert.assertEquals("/", CommonUtils.convertDirToUnixFormat(""));
        Assert.assertEquals("/app/dev/", CommonUtils.convertDirToUnixFormat("/app/dev/"));
        Assert.assertEquals("D:/app/dev/", CommonUtils.convertDirToUnixFormat("D:\\app\\dev"));
        Assert.assertEquals("D:/app/dev/", CommonUtils.convertDirToUnixFormat("D:\\app\\dev\\"));
    }
}
