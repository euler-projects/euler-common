package net.eulerframework.common.util;

public class OSAdapterUtil {

    /**
     * 统一路径为UNIX格式,结尾的"/"会去掉<br>
     * 如果只有一个"/"，则会保留<br>
     * <p>
     * Examples: <blockquote>
     * 
     * <pre>
     * convertDirToUnixFormat("\") returns "/"
     * convertDirToUnixFormat("D:\floder\") returns "D:/floder"
     * convertDirToUnixFormat("D:\floder\file") returns "D:/floder/file"
     * </pre>
     * 
     * </blockquote>
     * 
     * @param dir
     *            原始路径
     * @return unix路径
     */
    public static String convertDirToUnixFormat(String dir) {
        if (dir == null)
            return dir;
    
        String unixDir = dir.replace("\\", "/");
        if (unixDir.endsWith("/") && unixDir.length() > 1) {
            unixDir = unixDir.substring(0, unixDir.length() - 1);
        }
        return unixDir;
    }

}
