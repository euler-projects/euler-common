package org.eulerframework.common.util.net;


import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class URIBuilderTest {
    public static void main(String[] args) throws URISyntaxException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("var", "中文");
        params.put("p2", "English");
        System.out.println(URIBuilder.of("https://example.com")
                .schema("http")
                //.host("::")
                .port(80)
                .path("/query/q\\{quer\\}y/{var}/abc/{p2}/{p3}",
                        pathTemplate -> pathTemplate
                                .params(params)
                                .param("p3", "v4")
                )
                .query("q1", "v1")
                .query("q2", "v2中文")
                .build());
    }

}