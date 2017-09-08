package net.eulerframework.common.util;

import java.lang.reflect.Array;

public abstract class ArrayUtils {
    
    public static <T> T[] concat(T[] array1, T[] array2) {
        int len1 = array1.length;
        int len2 = array2.length;
        
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(array1.getClass().getComponentType(), len1 + len2);
        System.arraycopy(array1, 0, result, 0, len1);  
        System.arraycopy(array2, 0, result, len1, len2);  
        return result;
    }

    @SafeVarargs
    public static <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(first.getClass().getComponentType(), totalLength);

        System.arraycopy(first, 0, result, 0, first.length);
        
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

}
