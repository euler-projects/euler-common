/*
 * Copyright 2013-2026 the original author or authors.
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

package org.eulerframework.common.http;

import java.io.IOException;
import java.net.URISyntaxException;

class JdkHttpClientTemplateTest {
    public static void main(String[] args) throws URISyntaxException, IOException {
//        Path filePath = Path.of("");
//        String token = "";
        try (
//                FileInputStream fileInputStream = new FileInputStream(filePath.toFile());
                HttpResponse response = JdkHttpClientTemplate.INSTANCE.execute(HttpRequest.get("https://eulerproject.io")
//                        .header("Authorization", "bearer " + token)
//                        .body(MultipartFormDataRequestBody.newBuilder()
//                                .addInputStreamPart("file",
//                                        "test.png",
//                                        fileInputStream,
//                                        ContentType.create(Files.probeContentType(filePath)))
//                                .build())
                        .build());
        ) {
            System.out.println(response.getVersion());
            System.out.println(response.getStatus());
            System.out.println(response.getBody().asText());
        }
    }
}