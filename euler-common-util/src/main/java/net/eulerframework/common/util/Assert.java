package net.eulerframework.common.util;

import java.lang.reflect.Constructor;

/**
 * Assertion utility class.
 * 
 * <p>Some code was copied from {@code org.springframework.util.Assert} copyright belongs to the original author.
 * 
 * @author cFrost
 */
public abstract class Assert {
    
    private static <T extends RuntimeException> void throwException(Class<T> exceptionClass, String message) {
        try {
            Constructor<T> constructor = exceptionClass.getDeclaredConstructor(String.class);
            throw constructor.newInstance(message);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void isTrue(boolean expression) {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }
    
    public static void isTrue(boolean expression, String message) {
        Assert.isTrue(expression, IllegalArgumentException.class, message);
    }

    public static <T extends RuntimeException> void isTrue(boolean expression, Class<T> exceptionClass, String message) {
        if (!expression) {
            Assert.throwException(exceptionClass, message);
        }
    }
    
    public static void isFalse(boolean expression) {
        isFalse(expression, "[Assertion failed] - this expression must be false");
    }
    
    public static void isFalse(boolean expression, String message) {
        Assert.isFalse(expression, IllegalArgumentException.class, message);
    }

    public static <T extends RuntimeException> void isFalse(boolean expression, Class<T> exceptionClass, String message) {
        if (expression) {
            Assert.throwException(exceptionClass, message);
        }
    }
    
    public static void notNull(Object object) {
        notNull(object, "[Assertion failed] - this object must not be null");
    }
    
    public static void notNull(Object object, String message) {
        Assert.notNull(object, IllegalArgumentException.class, message);
    }

    public static <T extends RuntimeException> void notNull(Object object, Class<T> exceptionClass, String message) {
        if (object == null) {
            Assert.throwException(exceptionClass, message);
        }
    }
    
    public static void isNull(Object object) {
        isNull(object, "[Assertion failed] - this object must be null");
    }
    
    public static void isNull(Object object, String message) {
        Assert.isNull(object, IllegalArgumentException.class, message);
    }

    public static <T extends RuntimeException> void isNull(Object object, Class<T> exceptionClass, String message) {
        if (object != null) {
            Assert.throwException(exceptionClass, message);
        }
    }

    /**
     * Assert that the given String has valid text content; that is, it must not
     * be {@code null} and must contain at least one non-whitespace character.
     * <pre class="code">Assert.hasText(name);</pre>
     * @param text the String to check
     * @see StringUtils#hasText
     * @throws IllegalArgumentException if the text does not contain valid text content
     */
    public static void hasText(String text) {
        hasText(text,
                "[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
    }

    /**
     * Assert that the given String has valid text content; that is, it must not
     * be {@code null} and must contain at least one non-whitespace character.
     * <pre class="code">Assert.hasText(name, "'name' must not be empty");</pre>
     * @param text the String to check
     * @param message the exception message to use if the assertion fails
     * @see StringUtils#hasText
     * @throws IllegalArgumentException if the text does not contain valid text content
     */
    public static void hasText(String text, String message) {
        Assert.hasText(text, IllegalArgumentException.class, message);
    }
    
    public static <T extends RuntimeException> void hasText(String text, Class<T> exceptionClass, String message) {
        if (!StringUtils.hasText(text)) {
            Assert.throwException(exceptionClass, message);
        }
    }
}
