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
package org.eulerframework.common;

import org.junit.Test;

import junit.framework.Assert;
import org.eulerframework.common.util.StringUtils;

public class StringUtilsTest {

    @Test
    public void camelStyleToUnderLineLowerCase() {
        Assert.assertEquals("euler_framework", StringUtils.camelStyleToUnderLineLowerCase("EulerFramework"));
        Assert.assertEquals("euler_framework", StringUtils.camelStyleToUnderLineLowerCase("Euler_Framework"));
        Assert.assertEquals("euler_framework", StringUtils.camelStyleToUnderLineLowerCase("Euler__Framework"));
        Assert.assertEquals("euler_framework", StringUtils.camelStyleToUnderLineLowerCase("Euler_framework"));
        Assert.assertEquals("euler_framework", StringUtils.camelStyleToUnderLineLowerCase("eulerFramework"));
        Assert.assertEquals("e_u_l_e_r_f_r_a_m_e_w_o_r_k", StringUtils.camelStyleToUnderLineLowerCase("EULERFRAMEWORK"));
    }

    @Test
    public void camelStyleToDashLowerCase() {
        Assert.assertEquals("euler-framework", StringUtils.camelStyleToDashLowerCase("EulerFramework"));
        Assert.assertEquals("euler-framework", StringUtils.camelStyleToDashLowerCase("Euler-Framework"));
        Assert.assertEquals("euler-framework", StringUtils.camelStyleToDashLowerCase("Euler--Framework"));
        Assert.assertEquals("euler-framework", StringUtils.camelStyleToDashLowerCase("Euler-framework"));
        Assert.assertEquals("euler-framework", StringUtils.camelStyleToDashLowerCase("eulerFramework"));
        Assert.assertEquals("e-u-l-e-r-f-r-a-m-e-w-o-r-k", StringUtils.camelStyleToDashLowerCase("EULERFRAMEWORK"));
    }
}
