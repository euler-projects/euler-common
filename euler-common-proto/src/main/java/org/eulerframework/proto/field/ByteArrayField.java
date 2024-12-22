package org.eulerframework.proto.field;

public class ByteArrayField extends AbstractFixedLengthProtoField<byte[]>
        implements FixedLengthProtoField<byte[]> {
    private byte[] data;

    public ByteArrayField(int length) {
        super(length);
    }

    @Override
    public byte[] value() {
        return this.writeAsBytes();
    }

    @Override
    public void read(byte[] value) {
        byte[] tmp = new byte[this.length()];
        System.arraycopy(value, 0, tmp, 0, Math.min(this.length(), value.length));
        this.data = tmp;
    }

    @Override
    public byte[] writeAsBytes() {
        byte[] tmp = new byte[this.length()];
        System.arraycopy(this.data, 0, tmp, 0, Math.min(this.length(), this.data.length));
        return tmp;
    }
}
