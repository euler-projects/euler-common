/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2013-2018 cFrost.sun(孙宾, SUN BIN) 
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
 * https://cfrost.net
 */
package net.eulerframework.common;

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
import net.eulerframework.common.util.JavaObjectUtils;

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
