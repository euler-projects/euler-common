package org.eulerframework.proto.util.bytes;

public class ByteConvertorRegistryFactory {

    public static ByteConvertorRegistry defaultRegistry() {
        return DefaultByteConvertorRegistryHolder.DEFAULT;
    }

    private static class DefaultByteConvertorRegistryHolder {
        private static final ByteConvertorRegistry DEFAULT = new ByteConvertorRegistry();

        static {
            DEFAULT.addConvertor(Integer.class, new IntegerBytesConvertor())
                    .addConvertor(Short.class, new ShortBytesConvertor())
                    .addConvertor(Long.class, new LongBytesConvertor())
                    .addConvertor(String.class, new StringBytesConvertor())
                    .addConvertor(byte[].class, new BytesBytesConvertor());
        }
    }
}
