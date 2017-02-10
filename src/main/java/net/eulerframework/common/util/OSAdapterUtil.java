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
     * @param path
     *            原始路径
     * @return unix路径
     */
    public static String convertDirToUnixFormat(String path) {
        if (path == null)
            return null;
    
        String unixPath = path.replace("\\", "/");
        if (unixPath.endsWith("/") && unixPath.length() > 1) {
            unixPath = unixPath.substring(0, unixPath.length() - 1);
        }
        return unixPath;
    }

}
