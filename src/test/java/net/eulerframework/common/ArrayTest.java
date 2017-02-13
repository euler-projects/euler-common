package net.eulerframework.common;

import static org.junit.Assert.*;

import org.junit.Test;

import net.eulerframework.common.util.ArrayUtil;

public class ArrayTest {

    @Test
    public void voidArrayTest() {
        Character[] a = {'a', 'b', 'c'};
        Character[] b = {'d', 'e', 'f'};  
        Character[] ret = {'a', 'b', 'c', 'd', 'e', 'f'}; 
        Character[] ret2 = {'a', 'b', 'c', 'd', 'e', 'f', 'a', 'b', 'c', 'd', 'e', 'f'};    
        
        assertArrayEquals(ret, ArrayUtil.concat(a, b));
        assertArrayEquals(ret2, ArrayUtil.concatAll(a, b, ret));
    }
}
