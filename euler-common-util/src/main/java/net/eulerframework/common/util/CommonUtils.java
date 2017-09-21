package net.eulerframework.common.util;

public abstract class CommonUtils {

    /**
     * 程序暂停
     * @param seconds 暂停秒数
     */
    public static void sleep(int seconds) {
        int i = 0;
        while(i++ < seconds) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Sleep... "+ i +"s");
        }
    }

    /**
     * 统一路径为UNIX格式,以"/"结尾<br>
     * Examples:
     * <pre>
     * convertDirToUnixFormat("\") returns "/"
     * convertDirToUnixFormat("D:\floder\") returns "D:/floder/"
     * convertDirToUnixFormat("D:\floder\file") returns "D:/floder/file/"
     * </pre>
     * 
     * @param dir 原始路径
     * @param endWithSlash 结尾是否追加斜杠
     * @return unix路径
     */
    public static String convertDirToUnixFormat(String dir, boolean endWithSlash) {
        if (dir == null)
            return dir;
    
        String unixDir = dir.replace("\\", "/");
        while(unixDir.endsWith("/")) {
            unixDir = unixDir.substring(0, unixDir.length() - 1);
        }
        return endWithSlash ? unixDir + "/" : unixDir;
    }
}
