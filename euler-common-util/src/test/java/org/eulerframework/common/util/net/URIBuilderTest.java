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