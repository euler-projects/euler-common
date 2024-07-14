package org.eulerframework.common.http;

public interface Param {
    /**
     * Gets the name of this pair.
     *
     * @return the name of this pair, never {@code null}.
     */
    String getName();

    /**
     * Gets the value of this pair.
     *
     * @return the value of this pair, may be {@code null}.
     */
    String getValue();
}
