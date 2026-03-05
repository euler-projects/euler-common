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

package org.eulerframework.common.http.request;

import org.eulerframework.common.http.ContentType;
import org.eulerframework.common.http.message.BasicParam;
import org.eulerframework.common.util.http.ContentDisposition;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * A {@link org.eulerframework.common.http.RequestBody} implementation for {@code multipart/form-data} requests.
 * <p>
 * Supports multiple parts including text fields, file uploads via {@link File}, {@code byte[]},
 * and {@link InputStream}.
 *
 * <pre>{@code
 * MultipartFormDataRequestBody body = MultipartFormDataRequestBody.newBuilder()
 *     .addTextPart("field1", "value1")
 *     .addFilePart("avatar", avatarFile)
 *     .addByteArrayPart("data", "data.bin", byteArray, ContentType.APPLICATION_OCTET_STREAM)
 *     .addInputStreamPart("stream", "log.txt", inputStream, ContentType.TEXT_PLAIN)
 *     .build();
 * }</pre>
 */
public class MultipartFormDataRequestBody extends AbstractRequestBody {
    private final String boundary;
    private final List<Part> parts;

    private MultipartFormDataRequestBody(String boundary, List<Part> parts) {
        super(ContentType.MULTIPART_FORM_DATA.withParameters(new BasicParam("boundary", boundary)));
        this.boundary = boundary;
        this.parts = Collections.unmodifiableList(parts);
    }

    /**
     * Returns the boundary string used to separate parts in this multipart body.
     *
     * @return the boundary string
     */
    public String getBoundary() {
        return boundary;
    }

    /**
     * Returns the list of parts contained in this multipart body.
     *
     * @return an unmodifiable list of parts
     */
    public List<Part> getParts() {
        return parts;
    }

    /**
     * Returns an {@link InputStream} that produces the complete multipart/form-data encoded body.
     *
     * @return the multipart body as an {@link InputStream}
     */
    @Override
    public InputStream getContent() {
        List<InputStream> streams = new ArrayList<>();

        for (Part part : parts) {
            // Boundary delimiter line
            streams.add(toStream("--" + boundary + "\r\n"));

            // Part headers
            streams.add(toStream(part.getHeaders()));

            // Empty line separating headers from body
            streams.add(toStream("\r\n"));

            // Part body
            streams.add(part.getBodyStream());

            // CRLF after body
            streams.add(toStream("\r\n"));
        }

        // Closing boundary
        streams.add(toStream("--" + boundary + "--\r\n"));

        return new SequenceInputStream(Collections.enumeration(streams));
    }

    /**
     * Creates a new {@link Builder} for constructing a {@link MultipartFormDataRequestBody}.
     *
     * @return a new builder instance
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    private static InputStream toStream(String s) {
        return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Builds a {@code Content-Disposition: form-data} header value for a text field (without filename).
     *
     * @param name the field name
     * @return the formatted header value
     */
    private static String formatFormDataContentDisposition(String name) {
        return ContentDisposition.formData().name(name).build().toString();
    }

    /**
     * Builds a {@code Content-Disposition: form-data} header value with filename,
     * delegating to {@link ContentDisposition} for proper RFC 5987 / RFC 2047 encoding
     * of non-ASCII filenames.
     *
     * @param name     the field name
     * @param filename the filename
     * @return the formatted header value
     */
    private static String formatFormDataContentDisposition(String name, String filename) {
        return ContentDisposition.formData().name(name).filename(filename, StandardCharsets.UTF_8).build().toString();
    }

    // ---- Part definitions ----

    /**
     * Represents a single part in a multipart/form-data request body.
     */
    public interface Part {
        /**
         * Returns the form field name of this part.
         *
         * @return the field name
         */
        String getName();

        /**
         * Returns the formatted headers for this part, without the trailing blank line.
         *
         * @return the headers as a string
         */
        String getHeaders();

        /**
         * Returns an {@link InputStream} providing the body content of this part.
         *
         * @return the body content stream
         */
        InputStream getBodyStream();
    }

    /**
     * A text field part with a simple name-value pair.
     */
    public static class TextPart implements Part {
        private final String name;
        private final String value;

        TextPart(String name, String value) {
            this.name = Objects.requireNonNull(name, "name");
            this.value = Objects.requireNonNull(value, "value");
        }

        @Override
        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String getHeaders() {
            return "Content-Disposition: " + formatFormDataContentDisposition(name) + "\r\n";
        }

        @Override
        public InputStream getBodyStream() {
            return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * A file part that reads content from a {@link Path}.
     */
    public static class FilePart implements Part {
        private final String name;
        private final Path filePath;
        private final String filename;
        private final ContentType contentType;

        FilePart(String name, Path filePath, String filename, ContentType contentType) {
            this.name = Objects.requireNonNull(name, "name");
            this.filePath = Objects.requireNonNull(filePath, "filePath");
            this.filename = Objects.requireNonNull(filename, "filename");
            this.contentType = Objects.requireNonNull(contentType, "contentType");
        }

        @Override
        public String getName() {
            return name;
        }

        public Path getFilePath() {
            return filePath;
        }

        public String getFilename() {
            return filename;
        }

        public ContentType getPartContentType() {
            return contentType;
        }

        @Override
        public String getHeaders() {
            return "Content-Disposition: "
                    + formatFormDataContentDisposition(name, filename) + "\r\n"
                    + "Content-Type: " + contentType + "\r\n";
        }

        @Override
        public InputStream getBodyStream() {
            try {
                return Files.newInputStream(filePath);
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to open file: " + filePath, e);
            }
        }
    }

    /**
     * A part that reads content from a {@code byte[]}.
     */
    public static class ByteArrayPart implements Part {
        private final String name;
        private final String filename;
        private final byte[] data;
        private final ContentType contentType;

        ByteArrayPart(String name, String filename, byte[] data, ContentType contentType) {
            this.name = Objects.requireNonNull(name, "name");
            this.filename = Objects.requireNonNull(filename, "filename");
            this.data = Objects.requireNonNull(data, "data");
            this.contentType = Objects.requireNonNull(contentType, "contentType");
        }

        @Override
        public String getName() {
            return name;
        }

        public String getFilename() {
            return filename;
        }

        public byte[] getData() {
            return data;
        }

        public ContentType getPartContentType() {
            return contentType;
        }

        @Override
        public String getHeaders() {
            return "Content-Disposition: "
                    + formatFormDataContentDisposition(name, filename) + "\r\n"
                    + "Content-Type: " + contentType + "\r\n";
        }

        @Override
        public InputStream getBodyStream() {
            return new ByteArrayInputStream(data);
        }
    }

    /**
     * A part that reads content from an {@link InputStream}, enabling streaming uploads.
     */
    public static class InputStreamPart implements Part {
        private final String name;
        private final String filename;
        private final InputStream inputStream;
        private final ContentType contentType;

        InputStreamPart(String name, String filename, InputStream inputStream, ContentType contentType) {
            this.name = Objects.requireNonNull(name, "name");
            this.filename = Objects.requireNonNull(filename, "filename");
            this.inputStream = Objects.requireNonNull(inputStream, "inputStream");
            this.contentType = Objects.requireNonNull(contentType, "contentType");
        }

        @Override
        public String getName() {
            return name;
        }

        public String getFilename() {
            return filename;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public ContentType getPartContentType() {
            return contentType;
        }

        @Override
        public String getHeaders() {
            return "Content-Disposition: "
                    + formatFormDataContentDisposition(name, filename) + "\r\n"
                    + "Content-Type: " + contentType + "\r\n";
        }

        @Override
        public InputStream getBodyStream() {
            return inputStream;
        }
    }

    // ---- Builder ----

    /**
     * A builder for constructing {@link MultipartFormDataRequestBody} instances.
     */
    public static class Builder {
        private final List<Part> parts = new ArrayList<>();

        private Builder() {
        }

        /**
         * Adds a text field part.
         *
         * @param name  the field name
         * @param value the field value
         * @return this builder
         */
        public Builder addTextPart(String name, String value) {
            parts.add(new TextPart(name, value));
            return this;
        }

        /**
         * Adds a file part. The content type is probed from the file name; falls back to
         * {@code application/octet-stream} if the type cannot be determined.
         *
         * @param name the field name
         * @param file the file to upload
         * @return this builder
         */
        public Builder addFilePart(String name, File file) {
            String mimeType;
            try {
                mimeType = Files.probeContentType(file.toPath());
            } catch (IOException e) {
                mimeType = null;
            }
            ContentType ct = mimeType != null ? ContentType.create(mimeType) : ContentType.APPLICATION_OCTET_STREAM;
            return addFilePart(name, file, ct);
        }

        /**
         * Adds a file part with an explicit content type.
         *
         * @param name        the field name
         * @param file        the file to upload
         * @param contentType the content type of the file
         * @return this builder
         */
        public Builder addFilePart(String name, File file, ContentType contentType) {
            parts.add(new FilePart(name, file.toPath(), file.getName(), contentType));
            return this;
        }

        /**
         * Adds a part from a {@code byte[]}.
         *
         * @param name        the field name
         * @param filename    the filename to report to the server
         * @param data        the binary content
         * @param contentType the content type of the data
         * @return this builder
         */
        public Builder addByteArrayPart(String name, String filename, byte[] data, ContentType contentType) {
            parts.add(new ByteArrayPart(name, filename, data, contentType));
            return this;
        }

        /**
         * Adds a part from an {@link InputStream}, enabling streaming uploads without buffering
         * the entire content in memory.
         *
         * @param name        the field name
         * @param filename    the filename to report to the server
         * @param inputStream the input stream providing the content
         * @param contentType the content type of the stream content
         * @return this builder
         */
        public Builder addInputStreamPart(String name, String filename, InputStream inputStream,
                                          ContentType contentType) {
            parts.add(new InputStreamPart(name, filename, inputStream, contentType));
            return this;
        }

        /**
         * Builds the {@link MultipartFormDataRequestBody}.
         *
         * @return a new multipart/form-data request body
         * @throws IllegalStateException if no parts have been added
         */
        public MultipartFormDataRequestBody build() {
            if (parts.isEmpty()) {
                throw new IllegalStateException("At least one part is required");
            }
            String boundary = generateBoundary();
            return new MultipartFormDataRequestBody(boundary, new ArrayList<>(parts));
        }

        private static String generateBoundary() {
            return "----EulerFormBoundary" + UUID.randomUUID().toString().replace("-", "");
        }
    }
}
