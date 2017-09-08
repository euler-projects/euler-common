package net.eulerframework.common;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;

import net.eulerframework.common.util.io.file.FileReadException;
import net.eulerframework.common.util.io.file.SimpleFileIOUtils;

public class ArrayTest {

    @Test
    public void voidArrayTest() throws FileNotFoundException, FileReadException {
        File file = new File("D:\\Users\\cFrost\\Downloads\\a.torrent");
        for(int i = 0; i < 10000; i++) {
            System.out.println(SimpleFileIOUtils.readFileByByte(file).length);            
        }
    }
}
