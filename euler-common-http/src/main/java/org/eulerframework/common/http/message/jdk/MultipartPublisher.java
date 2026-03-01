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

package org.eulerframework.common.http.message.jdk;

import java.io.*;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

/**
 * multipart/form-data 工具类，适配 java.net.http.HttpClient
 */
public class MultipartPublisher {

    private static final String CRLF = "\r\n";
    private final String boundary;
    private final ByteArrayOutputStream preamble = new ByteArrayOutputStream();
    private final List<Supplier<InputStream>> contentStreams = new ArrayList<>();

    public MultipartPublisher() {
        this.boundary = "----HttpClientBoundary" + UUID.randomUUID().toString().replace("-", "");
    }

    // ➕ 添加文本字段
    public MultipartPublisher addField(String name, String value) {
        try {
            preamble.write(("--" + boundary + CRLF).getBytes(StandardCharsets.UTF_8));
            preamble.write(("Content-Disposition: form-data; name=\"" + name + "\"" + CRLF)
                    .getBytes(StandardCharsets.UTF_8));
            preamble.write(CRLF.getBytes(StandardCharsets.UTF_8));
            preamble.write(value.getBytes(StandardCharsets.UTF_8));
            preamble.write(CRLF.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return this;
    }

    // ➕ 添加文件字段（流式）
    public MultipartPublisher addFile(String fieldName, Path filePath, String contentType) {
        try {
            preamble.write(("--" + boundary + CRLF).getBytes(StandardCharsets.UTF_8));
            preamble.write(("Content-Disposition: form-data; name=\"" + fieldName +
                    "\"; filename=\"" + filePath.getFileName() + "\"" + CRLF)
                    .getBytes(StandardCharsets.UTF_8));
            preamble.write(("Content-Type: " + contentType + CRLF).getBytes(StandardCharsets.UTF_8));
            preamble.write(CRLF.getBytes(StandardCharsets.UTF_8));

            // 文件内容流式添加
            contentStreams.add(() -> {
                try {
                    return Files.newInputStream(filePath);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return this;
    }

    // ➕ 添加文件字段（带自定义文件名）
    public MultipartPublisher addFile(String fieldName, Path filePath, String fileName, String contentType) {
        try {
            preamble.write(("--" + boundary + CRLF).getBytes(StandardCharsets.UTF_8));
            preamble.write(("Content-Disposition: form-data; name=\"" + fieldName +
                    "\"; filename=\"" + fileName + "\"" + CRLF)
                    .getBytes(StandardCharsets.UTF_8));
            preamble.write(("Content-Type: " + contentType + CRLF).getBytes(StandardCharsets.UTF_8));
            preamble.write(CRLF.getBytes(StandardCharsets.UTF_8));

            contentStreams.add(() -> {
                try {
                    return Files.newInputStream(filePath);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return this;
    }

    // 🚀 构建 BodyPublisher
    public HttpRequest.BodyPublisher build() {
        try {
            // 写入结束 boundary
            preamble.write(("--" + boundary + "--" + CRLF).getBytes(StandardCharsets.UTF_8));
            byte[] preambleBytes = preamble.toByteArray();

            // 组合流：preamble + 所有文件流
            return HttpRequest.BodyPublishers.ofInputStream(() -> {
                List<InputStream> streams = new ArrayList<>();
                streams.add(new ByteArrayInputStream(preambleBytes));
                for (Supplier<InputStream> supplier : contentStreams) {
                    streams.add(supplier.get());
                }
                return new SequenceInputStream(Collections.enumeration(streams));
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String getContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    public static MultipartPublisher create() {
        return new MultipartPublisher();
    }
}
