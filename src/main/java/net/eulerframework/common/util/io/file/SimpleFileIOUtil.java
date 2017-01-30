/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015-2016 cFrost.sun(孙宾, SUN BIN) 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * For more information, please visit the following website
 * 
 * https://github.com/euler-form/web-form
 * http://eulerframework.net
 * http://cfrost.net
 */
package net.eulerframework.common.util.io.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.eulerframework.common.util.Assert;

/**
 * 基于内存的文件读写器,适用小文件的读写
 * @author cFrost
 *
 */
public abstract class SimpleFileIOUtil {
    private static final Logger logger = LogManager.getLogger();
    private static final int MAX_SIZE = 100 * 1024 * 1024;

    /**
     * 读取文件至内存,一次读取一个字节
     * @param file 被读取的文件
     * @return 文件的二进制内容
     * @throws FileNotFoundException 文件不存在
     * @throws FileReadException 其他读取异常
     */
    public static byte[] readFileByByte(File file) throws FileNotFoundException, FileReadException {
        if(file.length() > MAX_SIZE) {
            throw new FileReadException("文件过大");
        }
        
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            byte[] result = new byte[inputStream.available()];

            int count = 0;
            int tempInt;
            while ((tempInt = inputStream.read()) != -1) {
                result[count++] = (byte) tempInt;
            }

            return result;
        } catch (FileNotFoundException fileNotFoundException) {
            throw fileNotFoundException;
        } catch (IOException e) {
            throw new FileReadException(e);
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
    }

    /**
     * 读取文件至内存,一次读取多个字节
     * @param file 被读取的文件
     * @param number 一次读取的字节数
     * @return 文件二进制内容
     * @throws FileNotFoundException 文件不存在
     * @throws FileReadException 其他读取异常
     */
    public static byte[] readFileByMultiBytes(File file, int number) throws FileNotFoundException, FileReadException {
        if(file.length() > MAX_SIZE) {
            throw new FileReadException("文件过大");
        }
        
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            byte[] result = new byte[inputStream.available()];

            int count = 0;
            byte[] tempbytes = new byte[number];
            int readCount;
            while ((readCount = inputStream.read(tempbytes)) != -1) {
                for (int i = 0; i < readCount; i++) {
                    result[count++] = tempbytes[i];
                }
            }

            return result;
        } catch (FileNotFoundException fileNotFoundException) {
            throw fileNotFoundException;
        } catch (IOException e) {
            throw new FileReadException(e);
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
    }

    /**
     * 写字符串
     * 
     * @param filePath
     *            文件路径
     * @param data
     *            字符串内容
     * @param append
     *            追加模式
     * @throws IOException
     */
    public static void writeFile(String filePath, String data, boolean append) throws IOException {

        logger.info("Write File: " + filePath);

        File file = new File(filePath);
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter bufferWritter = null;

        try {
            createFileIfNotExist(file);
            
            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file, append), "UTF-8");
            bufferWritter = new BufferedWriter(outputStreamWriter);
            bufferWritter.write(data);
            bufferWritter.close();
        } catch (IOException e) {
            throw e;
        } finally {
            if (bufferWritter != null)
                bufferWritter.close();
            if (outputStreamWriter != null)
                outputStreamWriter.close();
        }
    }

    /**
     * 写二进制数据
     * 
     * @param filePath
     *            文件路径
     * @param data
     *            数据内容
     * @param append
     *            追加模式
     * @throws IOException
     */
    public static void writeFile(String filePath, byte[] data, boolean append) throws IOException {

        logger.info("Write File: " + filePath);

        File file = new File(filePath);
        FileOutputStream fileOutputStream = null;

        try {
            createFileIfNotExist(file);
            
            fileOutputStream = new FileOutputStream(file, append);
            fileOutputStream.write(data);
        } catch (IOException e) {
            throw e;
        } finally {
            if (fileOutputStream != null)
                fileOutputStream.close();
        }
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * 
     * @param dir
     *            将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful. If a
     *         deletion fails, the method stops attempting to delete and returns
     *         "false".
     */
    public static boolean deleteFile(String path) {
        File file = new File(path);
        return deleteFile(file);
    }

    public static boolean deleteFile(File file) {
        if (!file.exists())
            return true;

        if (file.isDirectory()) {
            String[] children = file.list();
            // 递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteFile(new File(file, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return delete(file);
    }

    private static boolean delete(File file) {
        logger.info("DELETE " + file.getPath());
        return file.delete();
    }

    public static void createFileIfNotExist(File file) throws IOException {
        Assert.isNotNull(file, "file is null");
        
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        
        if (!file.exists()) {
            file.createNewFile();
        }
    }
}
