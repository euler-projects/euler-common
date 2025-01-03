package org.eulerframework.proto.util.bytes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class StringBytesConvertor implements CharSequenceBytesConvertor<String>, BytesConvertor<String> {
    @Override
    public String readFrom(InputStream in, Charset charset) throws IOException {
        ByteArrayBuffer dataBuffer = new ByteArrayBuffer(in.available());
        byte[] readBuffer = new byte[Math.min(in.available(), 1024)];
        int readBytes;
        while ((readBytes = in.read(readBuffer)) != -1) {
            dataBuffer.append(readBuffer, 0, readBytes);
        }
        return new String(dataBuffer.toByteArray(), charset);
    }

    @Override
    public String readFrom(byte[] data, Charset charset) {
        return new String(data, charset);
    }

    @Override
    public int writeTo(Object value, OutputStream out, Charset charset) throws IOException {
        String s = (String) value;
        byte[] data = s.getBytes(charset);
        out.write(data);
        return data.length;
    }
}
