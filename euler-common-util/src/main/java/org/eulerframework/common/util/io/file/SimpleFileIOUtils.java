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
package org.eulerframework.common.util.io.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eulerframework.common.util.Assert;

/**
 * 基于内存的文件读写器,适用小文件的读写
 * @author cFrost
 *
 */
public abstract class SimpleFileIOUtils {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SimpleFileIOUtils.class);
    private static int supportMaxFileBytes = 100 * 1024 * 1024;
    
    /**
     * 改变SimpleFileIOUtils中将文件读取到内存类方法的可读取文件的最大字节数
     * 
     * @param bytes 最大文件大小
     */
    public static  void setSupportMaxFileBytes(int bytes) {
        Assert.isTrue(bytes < 1024 * 1024 * 1024 *2, "最大支持2GB文件的读取");
        supportMaxFileBytes = bytes;
    }

    /**
     * 读取文件至内存,一次读取一个字节
     * @param file 被读取的文件
     * @return 文件的二进制内容
     * @throws FileNotFoundException 文件不存在
     * @throws FileReadException 其他读取异常
     */
    public static byte[] readFileByByte(File file) throws FileNotFoundException, FileReadException {
        Assert.notNull(file, "file is null");

        LOGGER.info("Load file: " + file.getPath() + " Size: " + file.length());
        
        if(file.length() > supportMaxFileBytes) {
            throw new FileReadException("file too large, max file size is " + supportMaxFileBytes + " bytes.");
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
        Assert.notNull(file, "file is null");

        LOGGER.info("Load file: " + file.getPath() + " Size: " + file.length());
        
        if(file.length() > supportMaxFileBytes) {
            throw new FileReadException("file too large, max file size is " + supportMaxFileBytes + " bytes.");
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
     * @param filePath 文件路径
     * @param str 字符串内容
     * @param append 追加模式
     * @throws IOException IO异常
     */
    public static void writeFile(String filePath, String str, boolean append) throws IOException {
        Assert.notNull(filePath, "filePath is null");

        LOGGER.info("Write File: " + filePath);

        File file = new File(filePath);
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter bufferWritter = null;

        try {
            createFileIfNotExist(file);
            
            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file, append), "UTF-8");
            bufferWritter = new BufferedWriter(outputStreamWriter);
            bufferWritter.write(str);
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
     * @param filePath 文件路径
     * @param data 二进制内容
     * @param append 追加模式
     * @throws IOException IO异常
     */
    public static void writeFile(String filePath, byte[] data, boolean append) throws IOException {
        Assert.notNull(filePath, "filePath is null");

        LOGGER.info("Write File: " + filePath);

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
     * 将文件写入输出流
     * @param file 待写文件
     * @param outputStream 目标输出流
     * @param cacheBytes 读取缓存大小
     * @throws IOException 读写异常
     */
    public static void readFileToOutputStream(File file, OutputStream outputStream, int cacheBytes) throws IOException {
        ByteBuffer buff = ByteBuffer.allocate(cacheBytes);
        FileChannel fileInChannel = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            fileInChannel = fileInputStream.getChannel();
            int length = 0;
            while((length = fileInChannel.read(buff)) != -1) {
                buff.flip();
                outputStream.write(buff.array(), 0, length);
                buff.clear();
            }          
        } finally {
            if(fileInputStream != null) {
                fileInputStream.close();          
            }
            if(fileInChannel != null) {
                fileInChannel.close();          
            }
        }
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * 
     * @param path 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful or file not exists. If a
     *         deletion fails, the method stops attempting to delete and returns
     *         "false".
     */
    public static boolean deleteFile(String path) {
        File file = new File(path);
        return deleteFile(file);
    }
    
    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * 
     * @param file 将要删除的文件
     * @return boolean Returns "true" if all deletions were successful or file not exists. If a
     *         deletion fails, the method stops attempting to delete and returns
     *         "false".
     */
    public static boolean deleteFile(File file) {
        Assert.notNull(file, "file is null");
        
        if (!file.exists())
            return true;

        if (file.isDirectory()) {
            String[] children = file.list();
            // 递归删除目录中的子目录
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteFile(new File(file, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        
        //此时目录为空，可以删除
        LOGGER.info("Delete file: " + file.getPath());
        return file.delete();
    }

    /**
     * 在文件系统创建不存在的文件以及文件所在的目录
     * @param file 要创建的文件
     * @throws IOException IO异常
     */
    public static void createFileIfNotExist(File file) throws IOException {
        Assert.notNull(file, "file is null");
        
        if (!file.getParentFile().exists()) {
            LOGGER.info("Create dir: " + file.getParentFile().getPath());
            file.getParentFile().mkdirs();
        } else {
            LOGGER.info("File path exists: " + file.getParentFile().getPath());
        }
        
        if (!file.exists()) {
            file.createNewFile();
            LOGGER.info("Create new file: " + file.getPath());
        } else {
            LOGGER.info("File exists: " + file.getPath());
        }
    }
}
