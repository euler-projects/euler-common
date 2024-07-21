package org.eulerframework.common.http.message;


import org.eulerframework.common.http.HeaderElement;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class HeaderValueParserTest {
    @Test
    public void parseElement() {
        String s = "PSINO=6; domain=.baidu.com; path=/";
        //String s = "text/html;charset=utf-8";
        final ParserCursor cursor = new ParserCursor(0, s.length());
        HeaderElement headerElement = BasicHeaderValueParser.INSTANCE.parseHeader(s, cursor);

        System.out.println(headerElement.getName());
        System.out.println(headerElement.getValue());
        System.out.println(Arrays.toString(headerElement.getParameters()));
        System.out.println(headerElement);
    }

    @Test
    public void parseElements() {
        String s = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
        final ParserCursor cursor = new ParserCursor(0, s.length());
        HeaderElement[] headerElements = BasicHeaderValueParser.INSTANCE.parseElements(s, cursor);

        for (HeaderElement headerElement : headerElements) {
            System.out.println(headerElement.getName());
            System.out.println(headerElement.getValue());
            System.out.println(Arrays.toString(headerElement.getParameters()));
            System.out.println(headerElement);
        }
    }

}