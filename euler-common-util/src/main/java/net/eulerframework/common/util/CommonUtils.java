package net.eulerframework.common.util;

import java.util.Locale;

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
     * 统一路径为UNIX格式<br>
     * Examples:
     * <pre>
     * convertDirToUnixFormat("\", true) returns "/"
     * convertDirToUnixFormat("D:\floder\", true) returns "D:/floder/"
     * convertDirToUnixFormat("D:\floder\file", true) returns "D:/floder/file/"
     * convertDirToUnixFormat("\", false) returns ""
     * convertDirToUnixFormat("D:\floder\", false) returns "D:/floder"
     * convertDirToUnixFormat("D:\floder\file", false) returns "D:/floder/file"
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
    
    /**
     * 将表示语言的字符串转成Locale对象
     * @param localeString 表示语言的字符串
     * @return Locale对象
     */
    public static Locale parseLocale(String localeString) {
        Assert.hasText(localeString);
        
        localeString = localeString.toLowerCase();
        
        if(localeString.length() == 5) {
            return new Locale(localeString.substring(0, 2), localeString.substring(3, 5));
        } else if(localeString.length() == 2){
            return new Locale(localeString);
        } else {
            throw new IllegalArgumentException("地区字符串必须符合语言二码-国家/地区二码的格式, 两者之间必须要有一个分隔符,可是为任意ASCII打印字符, 国家/地区二码为可选部分");
        }
    }
    
    public static String formatLocal(Locale locale, char split) {
        Assert.notNull(locale);

        String language = locale.getLanguage();
        String country = locale.getCountry();
        
        if(StringUtils.hasText(country)) {
            return language.toLowerCase() + split + country.toLowerCase();
        } else {
            return language.toLowerCase();
        }
    }
}
