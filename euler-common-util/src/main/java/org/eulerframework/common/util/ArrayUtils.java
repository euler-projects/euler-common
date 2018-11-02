/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eulerframework.common.util;

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
    
    public static boolean contains(Object[] array, Object except) {
        Assert.notNull(array);
        
        for(Object obj : array) {
            if(obj == null) {
                if(except == null) {
                    return true;
                }
            } else {
                if(obj.equals(except)) {
                    return true;
                }
            }
        }
        
        return false;
    }
}
