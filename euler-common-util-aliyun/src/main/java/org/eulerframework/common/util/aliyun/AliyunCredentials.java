package org.eulerframework.common.util.aliyun;

public class AliyunCredentials {
    private final String accessKeyId;
    private final String accessKeySecret;

    public AliyunCredentials(String accessKeyId, String accessKeySecret) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }
}
