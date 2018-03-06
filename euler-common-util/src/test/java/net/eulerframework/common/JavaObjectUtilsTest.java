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
import java.util.Date;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.eulerframework.common.util.JavaObjectUtils;

/**
 * @author cFrost
 *
 */
public class JavaObjectUtilsTest {
    
    public static class TestObj {
        private String string;
        private Date date;
        private Integer number;
        private Boolean bool;
        private TestObj obj;
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
    public void test() throws JsonParseException, JsonMappingException, IOException, InstantiationException, IllegalAccessException {
        ObjectMapper om = new ObjectMapper();
        String json = "{\n" + 
                "    \"string\": \"abc\",\n" + 
                "    \"date\": \"123123123\",\n" + 
                "    \"number\": \"123\",\n" + 
                "    \"bool\": \"false\",\n" + 
                "    \"obj\": {\n" + 
                "        \"string\": \"abc\"\n" + 
                "    }\n" + 
                "}";
        Map<String, Object> map = om.readValue(json, new TypeReference<Map<String, Object>>(){});
        
        TestObj obj = JavaObjectUtils.readMapAsObject(map, TestObj.class);
        
        System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(obj));
    }

}
