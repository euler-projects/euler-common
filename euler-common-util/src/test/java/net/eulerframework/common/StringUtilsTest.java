package net.eulerframework.common;

import org.junit.Test;

import junit.framework.Assert;
import net.eulerframework.common.util.StringUtils;

public class StringUtilsTest {

    @Test
    public void test() {
        Assert.assertEquals("euler_framework", StringUtils.camelCaseToUnderLineCase("EulerFramework"));
        Assert.assertEquals("euler_framework", StringUtils.camelCaseToUnderLineCase("Euler_Framework"));
        Assert.assertEquals("euler_framework", StringUtils.camelCaseToUnderLineCase("Euler__Framework"));
        Assert.assertEquals("euler_framework", StringUtils.camelCaseToUnderLineCase("Euler_framework"));
        Assert.assertEquals("euler_framework", StringUtils.camelCaseToUnderLineCase("eulerFramework"));
    }
}
