package net.eulerframework.common.util;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sourceforge.pinyin4j.PinyinHelper;


public abstract class StringUtils {
    
    protected static final Logger logger = LogManager.getLogger();
    
    private static final String REGEX_MULTISPACE = "[^\\S\\r\\n]+";// 除换行外的连续空白字符
    
    /**
     * 判断字符串是否为<code>null<code>,<code>""</code>,<code>"null"</code>(忽略大小写)
     * @param string 待判断的字符串
     * @return 判断结果
     */
    public final static boolean isNull(String string) {
        return string == null || string.trim().equals("") || string.trim().toLowerCase().equals("null");
    }

    /**
     * Check that the given {@code CharSequence} is neither {@code null} nor
     * of length 0.
     * <p>Note: this method returns {@code true} for a {@code CharSequence}
     * that purely consists of whitespace.
     * <p><pre class="code">
     * StringUtils.hasLength(null) = false
     * StringUtils.hasLength("") = false
     * StringUtils.hasLength(" ") = true
     * StringUtils.hasLength("Hello") = true
     * </pre>
     * @param str the {@code CharSequence} to check (may be {@code null})
     * @return {@code true} if the {@code CharSequence} is not {@code null} and has length
     * @see #hasText(String)
     */
    public static boolean hasLength(CharSequence str) {
        return (str != null && str.length() > 0);
    }

    /**
     * Check that the given {@code String} is neither {@code null} nor of length 0.
     * <p>Note: this method returns {@code true} for a {@code String} that
     * purely consists of whitespace.
     * @param str the {@code String} to check (may be {@code null})
     * @return {@code true} if the {@code String} is not {@code null} and has length
     * @see #hasLength(CharSequence)
     * @see #hasText(String)
     */
    public static boolean hasLength(String str) {
        return hasLength((CharSequence) str);
    }
    
    /**
     * Check whether the given {@code CharSequence} contains actual <em>text</em>.
     * <p>More specifically, this method returns {@code true} if the
     * {@code CharSequence} is not {@code null}, its length is greater than
     * 0, and it contains at least one non-whitespace character.
     * <p><pre class="code">
     * StringUtils.hasText(null) = false
     * StringUtils.hasText("") = false
     * StringUtils.hasText(" ") = false
     * StringUtils.hasText("12345") = true
     * StringUtils.hasText(" 12345 ") = true
     * </pre>
     * @param str the {@code CharSequence} to check (may be {@code null})
     * @return {@code true} if the {@code CharSequence} is not {@code null},
     * its length is greater than 0, and it does not contain whitespace only
     * @see Character#isWhitespace
     */
    public static boolean hasText(CharSequence str) {
        if (!hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether the given {@code String} contains actual <em>text</em>.
     * <p>More specifically, this method returns {@code true} if the
     * {@code String} is not {@code null}, its length is greater than 0,
     * and it contains at least one non-whitespace character.
     * @param str the {@code String} to check (may be {@code null})
     * @return {@code true} if the {@code String} is not {@code null}, its
     * length is greater than 0, and it does not contain whitespace only
     * @see #hasText(CharSequence)
     */
    public static boolean hasText(String str) {
        return hasText((CharSequence) str);
    }

    /**
     * 获取字符串的字节数
     * @param string 待判断的字符串
     * @return 字符串的字节数
     */
    public final static int getStringBytesLength(String string) {
        if (string == null)
            return 0;

        return string.getBytes().length;
    }

    /**
     * 按字节长度截取字符串
     *
     * @param string 要截取的字符串
     * @param subBytes 截取字节长度
     * @param suffix 如果发生截取,在结果后添加的后缀,为<code>null</code>表示不添加
     * @return 截取后字符串
     */
    public static String subStringByBytes(String string, int subBytes, String suffix) {
        byte[] stringBytes = string.getBytes();
        if (stringBytes.length <= subBytes)
            return string;

        byte[] subStringBytes = Arrays.copyOf(stringBytes, subBytes);
        String subString;
        try {
            subString = new String(subStringBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        subString = subString.substring(0, subString.length() - 1);

        if (!StringUtils.isNull(suffix)) {
            subString += suffix;
        }

        return subString;
    }

    /**
     * 将制表符和多个连续的空格用一个空格替代
     *
     * @param string 待处理的字符串
     * @return 处理后的字符串
     */
    public static String earseMultiSpcases(String string) {
        if (string == null)
            return string;

        Pattern p_space = Pattern.compile(REGEX_MULTISPACE, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(string);
        string = m_space.replaceAll(" "); // 过滤空格制表符标签
        return string.trim(); // 返回文本字符串
    }

    /**
     * 删除制表符和空格
     *
     * @param string 待处理的字符串
     * @return 处理后的字符串
     */
    public static String earseAllSpcases(String string) {
        if (string == null)
            return string;

        return earseMultiSpcases(string).replace(" ", "");
    }

    /**
     * 将CRLF和CR换行符转换为LF换行符
     *
     * @param string 待处理的字符串
     * @return 处理后的字符串
     */
    public static String convertToLF(String string) {
        if (string == null)
            return string;
        return string.replace("\r\n", "\n").replace("\r", "\n");
    }

    /**
     * 将换行符替换为空格
     *
     * @param string 待处理的字符串
     * @return 处理后的字符串
     */
    public static String convertReturnToSpace(String string) {
        if (string == null)
            return string;
        return convertToLF(string).replace("\n", " ");
    }

    /**
     * 删除换行符
     *
     * @param string 待处理的字符串
     * @return 处理后的字符串
     */
    public static String earseReturn(String string) {
        if (string == null)
            return string;
        return convertToLF(string).replace("\n", "");
    }
    
    /**
     * 将纯ASCII打印字符串(0x20-0x7e)转为下划线命名法，连续的空白字符会被转换为一个下划线表示，如果待转换的字符串含有非ASCII字符，则放弃转换，原样返回
     * @param string 待转换的字符串
     * @return 转换后的字符串
     */
    public static String toUnderlineNomenclature(String string) {
        if(string == null || string.length() == 0)
            return string;
        if (!string.matches("^[\\u0020-\\u007e]+$")) {
            logger.warn("只能转换ASCII打印字符串(0x20-0x7e),函数将返回未修改的字符串");
            return string;
        }
        
        string = StringUtils.earseMultiSpcases(string);
        
        string = string.replace(' ', '_');
        
        return string;
    }

    /**
     * 首字母转小写
     * @param string 待转换的字符串
     * @return 转换后的字符串
     */
    public static String toLowerCaseFirstChar(String string) {
        if(string == null || string.length() == 0)
            return string;

        if (Character.isLowerCase(string.charAt(0)))
            return string;

        if(string.length() == 1)
            return string.toLowerCase();
        else
            return (new StringBuilder()).append(Character.toLowerCase(string.charAt(0))).append(string.substring(1)).toString();
    }

    /**
     * 首字母转大写
     * @param string 待转换的字符串
     * @return 转换后的字符串
     */
    public static String toUpperCaseFirstChar(String string) {
        if(string == null || string.length() == 0)
            return string;

        if (Character.isUpperCase(string.charAt(0)))
            return string;

        if(string.length() == 1)
            return string.toUpperCase();
        else
            return (new StringBuilder()).append(Character.toUpperCase(string.charAt(0))).append(string.substring(1)).toString();
    }

    /**
     * 随机生成字符串,字符串可能的取值在ASCII 0x21-0x7e之间
     * @param length 生成的字符串长度
     * @return
     */
    public static String randomString(int length) {
        StringBuffer stringBuffer = new StringBuffer();
        
        Random random = new Random();
        for(; length > 0; length--) {
            stringBuffer.append((char)(random.nextInt(93)+33));
        }
        return stringBuffer.toString();
    }

    /**
     * 去除字符串头尾的空白,与<code>String.trim()</code>的区别在于可以处理空对象
     * @param string 待处理的字符串
     * @return 处理后的字符串
     */
    public static String trim(String string) {
        if(string == null || string.length() == 0)
            return string;
        return string.trim();
    }

    /**
     * 将字符串转换为拼音
     * @param str 原字符串
     * @return 拼音
     */
    public static String toPinYinString(String str){  
        
        StringBuilder sb=new StringBuilder();  
        String[] arr=null;  
          
        for(int i=0;i<str.length();i++){  
            arr=PinyinHelper.toHanyuPinyinStringArray(str.charAt(i));  
            if(arr!=null && arr.length>0){  
                for (String string : arr) {  
                    sb.append(string);  
                }  
            } else {
                sb.append(str.charAt(i));
            } 
        }
          
        return sb.toString().toLowerCase();  
    }
}
