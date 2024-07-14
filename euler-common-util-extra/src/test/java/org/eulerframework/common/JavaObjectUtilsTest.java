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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import junit.framework.Assert;
import org.eulerframework.common.util.JavaObjectUtils;

/**
 * @author cFrost
 *
 */
public class JavaObjectUtilsTest {

    public static enum EnumTest {
        A, B;
    }

    public static class TestObj {
        private String string;
        private Date date;
        private Integer number;
        private Boolean bool;
        private TestObj obj;
        private EnumTest enumTest;

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }

        public Date getDate() {
            return date;
        }

        public EnumTest getEnumTest() {
            return enumTest;
        }

        public void setEnumTest(EnumTest enumTest) {
            this.enumTest = enumTest;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Boolean getBool() {
            return bool;
        }

        public void setBool(Boolean bool) {
            this.bool = bool;
        }

        public TestObj getObj() {
            return obj;
        }

        public void setObj(TestObj obj) {
            this.obj = obj;
        }
    }

    @Test
    public void test() throws JsonParseException, JsonMappingException, IOException, InstantiationException,
            IllegalAccessException {
        ObjectMapper om = new ObjectMapper();
        String json = "{" 
                    + "    \"string\":\"abc\"," 
                    + "    \"date\":\"123123123\"," 
                    + "    \"number\":\"123\","
                    + "    \"bool\":false," 
                    + "    \"enumTest\":\"A\"," 
                    + "    \"obj\":{"
                    + "        \"string\":\"abc\"" 
                    + "    }" 
                    + "}";
        Map<String, Object> map = om.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        
        LinkedHashMap<Date, Date> lm = new LinkedHashMap<>();
        lm.put(new Date(), new Date());
        
        //map.put("obj", lm);
        
        TestObj obj = JavaObjectUtils.readMapAsObject(map, TestObj.class);
        Assert.assertEquals("abc", obj.getString());
        Assert.assertEquals(EnumTest.A, obj.getEnumTest());
        Assert.assertEquals("abc", obj.getObj().getString());
        Assert.assertEquals(null, obj.getObj().getDate());
    }

    @Test
    public void testIsSupportObject() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {

        Assert.assertEquals(true, JavaObjectUtils.isSafeToString(String.class));
        Assert.assertEquals(true, JavaObjectUtils.isSafeToString(Integer.class));
        Assert.assertEquals(true, JavaObjectUtils.isSafeToString(Long.class));
        Assert.assertEquals(true, JavaObjectUtils.isSafeToString(Short.class));
        Assert.assertEquals(true, JavaObjectUtils.isSafeToString(Float.class));
        Assert.assertEquals(true, JavaObjectUtils.isSafeToString(Double.class));
        Assert.assertEquals(true, JavaObjectUtils.isSafeToString(Date.class));
        Assert.assertEquals(true, JavaObjectUtils.isSafeToString(Boolean.class));
        Assert.assertEquals(true, JavaObjectUtils.isSafeToString(Character.class));
        Assert.assertEquals(true, JavaObjectUtils.isSafeToString(BigDecimal.class));
        Assert.assertEquals(true, JavaObjectUtils.isSafeToString(int.class));
        Assert.assertEquals(true, JavaObjectUtils.isSafeToString(long.class));
        Assert.assertEquals(true, JavaObjectUtils.isSafeToString(short.class));
        Assert.assertEquals(true, JavaObjectUtils.isSafeToString(float.class));
        Assert.assertEquals(true, JavaObjectUtils.isSafeToString(double.class));
        Assert.assertEquals(true, JavaObjectUtils.isSafeToString(boolean.class));
        Assert.assertEquals(true, JavaObjectUtils.isSafeToString(char.class));
        Assert.assertEquals(true, JavaObjectUtils.isSafeToString(EnumTest.class));
        Assert.assertEquals(false, JavaObjectUtils.isSafeToString(JsonParseException.class));

        // Class<?>[] parameterTypes = { Class.class }; // 声明调用的方法只有一个参数,
        // 类型为String
        // Method method =
        // JavaObjectUtils.class.getDeclaredMethod("isSafeToString",
        // parameterTypes); // 声明调用哪个类的哪个方法
        // method.setAccessible(true);// 允许处理私有方法
        //
        // Assert.assertEquals(true, method.invoke(null, String.class));
        // Assert.assertEquals(true, method.invoke(null, Integer.class));
        // Assert.assertEquals(true, method.invoke(null, Long.class));
        // Assert.assertEquals(true, method.invoke(null, Short.class));
        // Assert.assertEquals(true, method.invoke(null, Float.class));
        // Assert.assertEquals(true, method.invoke(null, Double.class));
        // Assert.assertEquals(true, method.invoke(null, Date.class));
        // Assert.assertEquals(true, method.invoke(null, Boolean.class));
        // Assert.assertEquals(true, method.invoke(null, Character.class));
        // Assert.assertEquals(true, method.invoke(null, BigDecimal.class));
        // Assert.assertEquals(true, method.invoke(null, int.class));
        // Assert.assertEquals(true, method.invoke(null, long.class));
        // Assert.assertEquals(true, method.invoke(null, short.class));
        // Assert.assertEquals(true, method.invoke(null, float.class));
        // Assert.assertEquals(true, method.invoke(null, double.class));
        // Assert.assertEquals(true, method.invoke(null, boolean.class));
        // Assert.assertEquals(true, method.invoke(null, char.class));
        // Assert.assertEquals(false, method.invoke(null,
        // JsonParseException.class));
        //
        // method.setAccessible(false);
    }

}
