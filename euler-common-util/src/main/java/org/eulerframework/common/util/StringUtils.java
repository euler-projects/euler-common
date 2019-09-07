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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;


public abstract class StringUtils {

    private static final String REGEX_MULTISPACE = "[^\\S\\r\\n]+";// 除换行外的连续空白字符

    /**
     * 去除字符串头尾的空白,与{@code String.trim()}的区别在于可以处理空对象
     *
     * @param string 待处理的字符串
     * @return 处理后的字符串
     */
    public static String trim(String string) {
        if (hasLength(string))
            return string.trim();
        return string;
    }

    /**
     * Check whether the given {@code String} is empty or string "null".
     *
     * @param str the candidate String
     * @return {@code true} if the {@code str} is {@code null}, {@code ""} or {@code "null"}
     */
    public final static boolean isNull(String str) {
        return isEmpty(str) || str.toLowerCase().equals("null");
    }

    /**
     * Check whether the given {@code String} is empty.
     * <p>This method accepts any Object as an argument, comparing it to
     * {@code null} and the empty String. As a consequence, this method
     * will never return {@code true} for a non-null non-String object.
     * <p>The Object signature is useful for general attribute handling code
     * that commonly deals with Strings but generally has to iterate over
     * Objects since attributes may e.g. be primitive value objects as well.
     *
     * @param str the candidate String
     * @return {@code true} if the {@code str} is {@code null} or {@code ""}
     * @since 3.2.1
     */
    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    /**
     * Check that the given {@code CharSequence} is neither {@code null} nor
     * of length 0.
     * <p>Note: this method returns {@code true} for a {@code CharSequence}
     * that purely consists of whitespace.
     * <pre class="code">
     * StringUtils.hasLength(null) = false
     * StringUtils.hasLength("") = false
     * StringUtils.hasLength(" ") = true
     * StringUtils.hasLength("Hello") = true
     * </pre>
     *
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
     *
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
     * <pre class="code">
     * StringUtils.hasText(null) = false
     * StringUtils.hasText("") = false
     * StringUtils.hasText(" ") = false
     * StringUtils.hasText("12345") = true
     * StringUtils.hasText(" 12345 ") = true
     * </pre>
     *
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
     *
     * @param str the {@code String} to check (may be {@code null})
     * @return {@code true} if the {@code String} is not {@code null}, its
     * length is greater than 0, and it does not contain whitespace only
     * @see #hasText(CharSequence)
     */
    public static boolean hasText(String str) {
        return hasText((CharSequence) str);
    }

    /**
     * 按字节长度截取字符串
     *
     * @param str      要截取的字符串
     * @param subBytes 截取字节长度
     * @param suffix   如果发生截取,在结果后添加的后缀, 为{@code null}表示不添加, 后缀不包含在长度内, 即最终字符串长度等于subBytes+后缀长度
     * @return 截取后字符串
     */
    public static String subStringByBytes(String str, int subBytes, String suffix) {
        if (str == null)
            return null;

        byte[] stringBytes = str.getBytes();
        if (stringBytes.length <= subBytes)
            return str;

        byte[] subStringBytes = Arrays.copyOf(stringBytes, subBytes);
        String subString;
        subString = new String(subStringBytes, StandardCharsets.UTF_8);
        subString = subString.substring(0, subString.length() - 1);

        if (StringUtils.hasText(suffix)) {
            subString += suffix;
        }

        return subString;
    }

    /**
     * 按字符长度截取字符串
     *
     * @param str    要截取的字符串
     * @param length 截取字符长度
     * @param suffix 如果发生截取,在结果后添加的后缀, 为{@code null}表示不添加, 后缀不包含在长度内, 即最终字符串长度等于subBytes+后缀长度
     * @return 截取后字符串
     */
    public static String subStringByLength(String str, int length, String suffix) {
        if (str == null)
            return null;

        if (str.length() <= length)
            return str;

        String subString = str.substring(0, length);

        if (StringUtils.hasText(suffix)) {
            subString += suffix;
        }

        return subString;
    }

    /**
     * 将制表符和多个连续的空格用一个空格替代
     *
     * @param str 待处理的字符串
     * @return 处理后的字符串
     */
    public static String eraseMultiSpaces(String str) {
        if (str == null)
            return null;

        Pattern p_space = Pattern.compile(REGEX_MULTISPACE, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(str);
        str = m_space.replaceAll(" "); // 过滤空格制表符标签
        return str.trim(); // 返回文本字符串
    }

    /**
     * 将CRLF和CR换行符转换为LF换行符
     *
     * @param str 待处理的字符串
     * @return 处理后的字符串
     */
    public static String convertToLF(String str) {
        if (str == null)
            return null;

        return str.replace("\r\n", "\n").replace("\r", "\n");
    }

    /**
     * 将换行符替换为空格
     *
     * @param str 待处理的字符串
     * @return 处理后的字符串
     */
    public static String convertReturnToSpace(String str) {
        return eraseReturn(str, " ");
    }

    /**
     * 删除换行符
     *
     * @param str 待处理的字符串
     * @return 处理后的字符串
     */
    public static String eraseReturn(String str) {
        return eraseReturn(str, "");
    }

    /**
     * 将换行符转换为指定字符串
     *
     * @param str         待处理的字符串
     * @param replacement 代替空格的字符串
     * @return 处理后的字符串
     */
    public static String eraseReturn(String str, String replacement) {
        if (str == null)
            return null;

        return convertToLF(str).replace("\n", replacement);
    }

    /**
     * 首字母转小写
     *
     * @param str 待转换的字符串
     * @return 转换后的字符串
     */
    public static String toLowerCaseFirstChar(String str) {
        if (!hasLength(str))
            return str;
        //!string.matches("^[\\u0020-\\u007e]+$")
        if (Character.isLowerCase(str.charAt(0)))
            return str;

        if (str.length() == 1)
            return str.toLowerCase();
        else
            return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 首字母转大写
     *
     * @param str 待转换的字符串
     * @return 转换后的字符串
     */
    public static String toUpperCaseFirstChar(String str) {
        if (!hasLength(str))
            return str;

        if (Character.isUpperCase(str.charAt(0)))
            return str;

        if (str.length() == 1)
            return str.toUpperCase();
        else
            return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 转为小驼峰
     *
     * @param str   待处理字符串
     * @param split 单词分隔符
     * @return 转换后的字符串
     */
    public static String toLowerCaseCamelStyle(String str, String split) {
        if (!hasLength(str))
            return str;

        return toLowerCaseFirstChar(toCamelStyle(str, split));
    }

    /**
     * 转为大驼峰
     *
     * @param str   待处理字符串
     * @param split 单词分隔符
     * @return 转换后的字符串
     */
    public static String toUpperCaseCamelStyle(String str, String split) {
        if (!hasLength(str))
            return str;

        return toUpperCaseFirstChar(toCamelStyle(str, split));
    }

    /**
     * 转为小驼峰, 以空白字符作为单词分隔符, 多个连续的空白字符会被视为一个
     *
     * @param str 待处理字符串
     * @return 转换后的字符串
     */
    public static String toLowerCaseCamelStyle(String str) {
        str = StringUtils.eraseMultiSpaces(str);
        return toLowerCaseCamelStyle(str, " ");
    }

    /**
     * 转为大驼峰, 以空白字符作为单词分隔符, 多个连续的空白字符会被视为一个
     *
     * @param str 待处理字符串
     * @return 转换后的字符串
     */
    public static String toUpperCaseCamelStyle(String str) {
        str = StringUtils.eraseMultiSpaces(str);
        return toUpperCaseCamelStyle(str, " ");
    }

    private static String toCamelStyle(String str, String split) {
        if (!hasLength(str))
            return str;

        String[] words = str.split(split);

        if (words.length <= 1) {
            return str;
        }

        StringBuilder builder = new StringBuilder();

        for (String word : words) {
            builder.append(toUpperCaseFirstChar(word.toLowerCase()));
        }

        return builder.toString();
    }

    /**
     * 把驼峰风格的字符串转换为下划线风格，连续的下划线会被替换为一个。
     * <pre class="code">
     * StringUtils.camelStyleToUnderLineLowerCase("EulerFramework") = "euler_framework"
     * StringUtils.camelStyleToUnderLineLowerCase("Euler_Framework") = "euler_framework"
     * StringUtils.camelStyleToUnderLineLowerCase("Euler__Framework") = "euler_framework"
     * StringUtils.camelStyleToUnderLineLowerCase("Euler_framework") = "euler_framework"
     * StringUtils.camelStyleToUnderLineLowerCase("eulerFramework") = "euler_framework"
     * StringUtils.camelStyleToUnderLineLowerCase("EULERFRAMEWORK") = "e_u_l_e_r_f_r_a_m_e_w_o_r_k"
     * </pre>
     *
     * @param str 峰风格的字符串
     * @return 下划线风格字符串
     */
    public static String camelStyleToUnderLineLowerCase(String str) {
        str = camelStyleToWord(str, "_");

        if (!hasText(str)) {
            return str;
        }

        return str.toLowerCase().replaceAll("[_]+", "_");
    }

    /**
     * 把驼峰风格的字符串转换为中划线风格，连续的中划线会被替换为一个。
     * <pre class="code">
     * StringUtils.camelStyleToDashLowerCase("EulerFramework") = "euler-framework"
     * StringUtils.camelStyleToDashLowerCase("Euler-Framework") = "euler-framework"
     * StringUtils.camelStyleToDashLowerCase("Euler--Framework") = "euler-framework"
     * StringUtils.camelStyleToDashLowerCase("Euler-framework") = "euler-framework"
     * StringUtils.camelStyleToDashLowerCase("eulerFramework") = "euler-framework"
     * StringUtils.camelStyleToDashLowerCase("EULERFRAMEWORK") = "e-u-l-e-r-f-r-a-m-e-w-o-r-k"
     * </pre>
     *
     * @param str 峰风格的字符串
     * @return 中划线风格字符串
     */
    public static String camelStyleToDashLowerCase(String str) {
        str = camelStyleToWord(str, "-");

        if (!hasText(str)) {
            return str;
        }

        return str.toLowerCase().replaceAll("[-]+", "-");
    }

    /**
     * 把驼峰风格的字符串转为单词
     *
     * @param str   峰风格的字符串
     * @param split 单词分隔符
     * @return 单词字符串
     */
    public static String camelStyleToWord(String str, String split) {
        if (!hasText(str)) {
            return str;
        }

        char[] chars = str.toCharArray();
        StringBuilder ret = new StringBuilder();
        for (char each : chars) {
            if (Character.isUpperCase(each) && ret.length() > 0) {
                ret.append(split);
            }
            ret.append(each);
        }
        return ret.toString();
    }

    /**
     * 随机生成字符串,字符串可能的取值在ASCII 0x21-0x7e之间
     *
     * @param length 生成的字符串长度
     * @return 生成的字符串
     */
    public static String randomString(int length) {
        StringBuffer stringBuffer = new StringBuffer();

        Random random = new Random();
        for (; length > 0; length--) {
            stringBuffer.append((char) (random.nextInt(93) + 33));
        }
        return stringBuffer.toString();
    }

    /**
     * 将字符串转换为拼音
     *
     * @param str 原字符串
     * @return 拼音
     */
    public static String toPinYinString(String str) {
        if (!hasLength(str))
            return str;

        StringBuilder sb = new StringBuilder();
        String[] arr = null;

        for (int i = 0; i < str.length(); i++) {
            arr = PinyinHelper.toHanyuPinyinStringArray(str.charAt(i));
            if (arr != null && arr.length > 0) {
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
