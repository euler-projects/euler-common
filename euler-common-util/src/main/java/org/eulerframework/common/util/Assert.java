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

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

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

    /**
     * Assert a boolean expression, throwing an {@code IllegalStateException}
     * if the expression evaluates to {@code false}.
     * <p>Call {@link #isTrue} if you wish to throw an {@code IllegalArgumentException}
     * on an assertion failure.
     * <pre class="code">Assert.state(id == null, "The id property must not already be initialized");</pre>
     * @param expression a boolean expression
     * @param message the exception message to use if the assertion fails
     * @throws IllegalStateException if {@code expression} is {@code false}
     */
    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Assert a boolean expression, throwing an {@code IllegalStateException}
     * if the expression evaluates to {@code false}.
     * <p>Call {@link #isTrue} if you wish to throw an {@code IllegalArgumentException}
     * on an assertion failure.
     * <pre class="code">
     * Assert.state(id == null,
     *     () -&gt; "ID for " + entity.getName() + " must not already be initialized");
     * </pre>
     * @param expression a boolean expression
     * @param messageSupplier a supplier for the exception message to use if the
     * assertion fails
     * @throws IllegalStateException if {@code expression} is {@code false}
     * @since 5.0
     */
    public static void state(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new IllegalStateException(nullSafeGet(messageSupplier));
        }
    }

    /**
     * Assert a boolean expression, throwing an {@code IllegalStateException}
     * if the expression evaluates to {@code false}.
     * @deprecated as of 4.3.7, in favor of {@link #state(boolean, String)}
     */
    @Deprecated
    public static void state(boolean expression) {
        state(expression, "[Assertion failed] - this state invariant must be true");
    }

    private static String nullSafeGet(Supplier<String> messageSupplier) {
        return (messageSupplier != null ? messageSupplier.get() : null);
    }

    public static void isTrue(boolean expression) {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }
    
    public static void isTrue(boolean expression, String message) {
        Assert.isTrue(expression, IllegalArgumentException.class, message);
    }

    public static void isTrue(boolean expression, Supplier<String> messageSupplier) {
        Assert.isTrue(expression, IllegalArgumentException.class, messageSupplier);
    }

    public static <T extends RuntimeException> void isTrue(boolean expression, Class<T> exceptionClass, String message) {
        if (!expression) {
            Assert.throwException(exceptionClass, message);
        }
    }

    public static <T extends RuntimeException> void isTrue(boolean expression, Class<T> exceptionClass, Supplier<String> messageSupplier) {
        if (!expression) {
            Assert.throwException(exceptionClass, nullSafeGet(messageSupplier));
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

    public static void notNull(Object object, Supplier<String> messageSupplier) {
        Assert.notNull(object, IllegalArgumentException.class, messageSupplier);
    }

    public static <T extends RuntimeException> void notNull(Object object, Class<T> exceptionClass, String message) {
        if (object == null) {
            Assert.throwException(exceptionClass, message);
        }
    }

    public static <T extends RuntimeException> void notNull(Object object, Class<T> exceptionClass, Supplier<String> messageSupplier) {
        if (object == null) {
            Assert.throwException(exceptionClass, nullSafeGet(messageSupplier));
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
