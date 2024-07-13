package org.eulerframework.common.util.aliyun;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eulerframework.common.util.http.HttpExecutor;
import org.eulerframework.common.util.http.HttpMethod;
import org.eulerframework.common.util.http.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

public class AliyunOpenApiRequest {
    private final static Logger LOGGER = LoggerFactory.getLogger(AliyunOpenApiRequest.class);

    private static final String ALGORITHM = "ACS3-HMAC-SHA256";

    private final AliyunCredentials credentials;
    private final HttpMethod httpMethod;
    private final URI endpoint;
    private final String action;
    private final String version;
    private final Map<String, String> headers = new LinkedHashMap<>();
    //    private final Map<String, String> queryParams = new LinkedHashMap<>();
    private String body = null;

    public AliyunOpenApiRequest(
            HttpMethod httpMethod,
            String endpoint,
            String path,
            String action,
            String version,
            Map<String, String> headers,
            Map<String, String> queryParams,
            Map<String, String> bodyParams,
            AliyunCredentials credentials) throws URISyntaxException {
        this.httpMethod = httpMethod;
        this.action = action;
        this.version = version;

        Optional.ofNullable(headers).ifPresent(this.headers::putAll);

//        Optional.ofNullable(queryParams).ifPresent(this.queryParams::putAll);

        if (bodyParams != null && !bodyParams.isEmpty()) {
            if (HttpMethod.supportBody(this.httpMethod)) {
                this.headers.put("Content-Type", "application/x-www-form-urlencoded");

                this.body = bodyParams.entrySet()
                        .stream()
                        .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                        .collect(Collectors.joining("&"));
            } else {
                throw new IllegalArgumentException("Http method " + this.httpMethod + " doesn't support body");
            }
        }


        URIBuilder uriBuilder = URIBuilder.of(endpoint)
                .path(path);

        if (!queryParams.isEmpty()) {
            queryParams.forEach(uriBuilder::query);
        }

        this.endpoint = uriBuilder.build();

        this.credentials = credentials;

        this.initSystemHeader();
    }

    private void initSystemHeader() {
        this.headers.put("x-acs-action", this.action);
        this.headers.put("x-acs-version", this.version);
        this.headers.put("x-acs-date", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .format(LocalDateTime.now(ZoneId.of("GMT"))));
        this.headers.put("x-acs-signature-nonce", UUID.randomUUID().toString());
        this.headers.put("Authorization", this.buildAuthorizationHeader());
    }

    private String buildAuthorizationHeader() {
        // 步骤 1：拼接规范请求串
        String path = endpoint.getPath();
        String canonicalUri = (path == null || path.isEmpty()) ? "/" : path;
        // 请求参数，当请求的查询字符串为空时，使用空字符串作为规范化查询字符串
//        String canonicalQueryString = this.queryParams.entrySet().stream()
//                .map(entry -> percentCode(entry.getKey()) + "=" + percentCode(String.valueOf(entry.getValue())))
//                .collect(Collectors.joining("&"));
        String canonicalQueryString = Optional.ofNullable(this.endpoint.getRawQuery())
                .map(query -> query
                        .replace("+", "%20")
                        .replace("*", "%2A")
                        .replace("%7E", "~")
                )
                .orElse("");

        // 请求体，当请求正文为空时，比如GET请求，RequestPayload固定为空字符串
        String requestPayload = this.body != null ? this.body : "";

        // 计算请求体的哈希值
        String hashedRequestPayload = sha256Hex(requestPayload);
        this.headers.put("x-acs-content-sha256", hashedRequestPayload);
        // 构造请求头，多个规范化消息头，按照消息头名称（小写）的字符代码顺序以升序排列后拼接在一起
        StringBuilder canonicalHeaders = new StringBuilder();
        // 已签名消息头列表，多个请求头名称（小写）按首字母升序排列并以英文分号（;）分隔
        StringBuilder signedHeadersSb = new StringBuilder();

        final TreeMap<String, String> willSignHeaders = new TreeMap<>(Comparator.naturalOrder());
        this.headers.forEach((header, value) -> {
            if (header.toLowerCase().startsWith("x-acs-")
                    || header.equalsIgnoreCase("Content-Type")) {
                willSignHeaders.put(header.toLowerCase(), value.trim());
            }
        });

        String host = this.endpoint.getHost();
        willSignHeaders.put("host", host);
        willSignHeaders.forEach((header, value) -> {
            canonicalHeaders.append(header).append(":").append(value).append("\n");
            signedHeadersSb.append(header).append(";");
        });

        String signedHeaders = signedHeadersSb.substring(0, signedHeadersSb.length() - 1);
        String canonicalRequest = this.httpMethod + "\n" + canonicalUri + "\n" + canonicalQueryString + "\n" + canonicalHeaders + "\n" + signedHeaders + "\n" + hashedRequestPayload;
        // 步骤 2：拼接待签名字符串
        String hashedCanonicalRequest = sha256Hex(canonicalRequest); // 计算规范化请求的哈希值
        String stringToSign = ALGORITHM + "\n" + hashedCanonicalRequest;
        // 步骤 3：计算签名
        String signature = toHexString((hmac256(this.credentials.getAccessKeySecret().getBytes(StandardCharsets.UTF_8), stringToSign)));
        // 步骤 4：拼接 Authorization
        String authorization = ALGORITHM + " " + "Credential=" + this.credentials.getAccessKeyId() + ",SignedHeaders=" + signedHeaders + ",Signature=" + signature;
        LOGGER.trace("\n" +
                        "============================================================\n" +
                        "                   Aliyun signature info:                   \n" +
                        "============================================================\n" +
                        "canonicalRequest:\n" +
                        "------------------------------------------------------------\n" +
                        "{}\n" +
                        "------------------------------------------------------------\n" +
                        "stringToSign:\n" +
                        "------------------------------------------------------------\n" +
                        "{}\n" +
                        "------------------------------------------------------------\n" +
                        "signature:{}\n" +
                        "authorization: {}\n" +
                        "============================================================\n",
                canonicalRequest,
                stringToSign,
                signature,
                authorization);
        return authorization;
    }

    /**
     * 使用HmacSHA256算法生成消息认证码（MAC）。
     *
     * @param key 密钥，用于生成MAC的密钥，必须保密。
     * @param str 需要进行MAC认证的消息。
     * @return 返回使用HmacSHA256算法计算出的消息认证码。
     * @throws Exception 如果初始化MAC或计算MAC过程中遇到错误，则抛出异常。
     */
    private static byte[] hmac256(byte[] key, String str) {
        // 实例化HmacSHA256消息认证码生成器
        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionUtils.<RuntimeException>rethrow(e);
        }
        // 创建密钥规范，用于初始化MAC生成器
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, mac.getAlgorithm());
        // 初始化MAC生成器
        try {
            mac.init(secretKeySpec);
        } catch (InvalidKeyException e) {
            throw ExceptionUtils.<RuntimeException>rethrow(e);
        }
        // 计算消息认证码并返回
        return mac.doFinal(str.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 使用SHA-256算法计算字符串的哈希值并以十六进制字符串形式返回。
     *
     * @param str 需要进行SHA-256哈希计算的字符串。
     * @return 计算结果为小写十六进制字符串。
     * @throws Exception 如果在获取SHA-256消息摘要实例时发生错误。
     */
    private static String sha256Hex(String str) {
        // 获取SHA-256消息摘要实例
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionUtils.<RuntimeException>rethrow(e);
        }
        // 计算字符串s的SHA-256哈希值
        byte[] d = md.digest(str.getBytes(StandardCharsets.UTF_8));
        // 将哈希值转换为小写十六进制字符串并返回
        return toHexString(d);
    }

    /**
     * 对指定的字符串进行URL编码。
     * 使用UTF-8编码字符集对字符串进行编码，并对特定的字符进行替换，以符合URL编码规范。
     *
     * @param str 需要进行URL编码的字符串。
     * @return 编码后的字符串。其中，加号"+"被替换为"%20"，星号"*"被替换为"%2A"，波浪号"%7E"被替换为"~"。
     */
    private static String percentCode(String str) {
        if (str == null) {
            throw new IllegalArgumentException("str is null");
        }
        return URLEncoder.encode(str, StandardCharsets.UTF_8)
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~");
    }

    /**
     * Table for byte to hex string translation.
     */
    private static final char[] hex = "0123456789abcdef".toCharArray();

    private static String toHexString(byte[] bytes) {
        if (null == bytes) {
            return null;
        }

        StringBuilder sb = new StringBuilder(bytes.length << 1);

        for (byte aByte : bytes) {
            sb.append(hex[(aByte & 0xf0) >> 4]).append(hex[(aByte & 0x0f)]);
        }

        return sb.toString();
    }

    public AliyunCredentials getCredentials() {
        return credentials;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public URI getEndpoint() {
        return endpoint;
    }

    public String getAction() {
        return action;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public static Builder GET() {
        return new Builder().get();
    }

    public static Builder POST() {
        return new Builder().post();
    }

    public static class Builder {
        private HttpMethod httpMethod;
        private String endpoint;
        private String path;
        private String action;
        private String version;
        private final Map<String, String> headers = new LinkedHashMap<>();
        private final Map<String, String> queryParams = new LinkedHashMap<>();
        private final Map<String, String> bodyParams = new LinkedHashMap<>();
        private String accessKeyId;
        private String accessKeySecret;

        private Builder() {
        }

        private Builder get() {
            return this.httpMethod(HttpMethod.GET);
        }

        private Builder post() {
            return this.httpMethod(HttpMethod.POST);
        }

        private Builder httpMethod(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder header(String header, String value) {
            this.headers.put(header, value);
            return this;
        }

        public Builder queryParam(String param, String value) {
            this.queryParams.put(param, value);
            return this;
        }

        public Builder bodyParam(String param, String value) {
            this.bodyParams.put(param, value);
            return this;
        }

        public Builder accessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
            return this;
        }

        public Builder accessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
            return this;
        }

        private AliyunOpenApiRequest build() {
            try {
                return new AliyunOpenApiRequest(
                        this.httpMethod,
                        this.endpoint,
                        this.path,
                        this.action,
                        this.version,
                        this.headers,
                        this.queryParams,
                        this.bodyParams,
                        new AliyunCredentials(accessKeyId, accessKeySecret)
                );
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        public String execute(HttpExecutor httpExecutor) throws IOException {
            AliyunOpenApiRequest request = this.build();
            return httpExecutor.execute(request.getHttpMethod(), request.getEndpoint(), request.getHeaders(), request.getBody());
        }
    }
}
