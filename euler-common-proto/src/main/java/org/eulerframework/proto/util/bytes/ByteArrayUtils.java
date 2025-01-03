package org.eulerframework.proto.util.bytes;

import org.apache.commons.lang3.ArrayUtils;

public class ByteArrayUtils {
    public static byte[] rightTrim(byte[] data) {
        return rightTrim(data, (byte) 0);
    }

    public static byte[] rightTrim(byte[] data, int b) {
        return rightTrim(data, (byte) (b & 0xFF));
    }

    public static byte[] rightTrim(byte[] data, byte b) {
        if (data == null || data.length == 0) {
            return data;
        }

        int trimIndex = -1;
        for (int i = data.length - 1; i >= 0; i--) {
            if (data[i] == b) {
                trimIndex = i;
                continue;
            }
            break;
        }
        if (trimIndex > -1) {
            return ArrayUtils.subarray(data, 0, trimIndex);
        } else {
            return data;
        }
    }
}
