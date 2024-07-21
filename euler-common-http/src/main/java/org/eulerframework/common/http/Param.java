package org.eulerframework.common.http;

public interface Param {
    /**
     * Gets the name of this param.
     *
     * @return the name of this param, never {@code null}.
     */
    String getName();

    /**
     * Gets the value of this param.
     *
     * @return the value of this param, may be {@code null}.
     */
    String getValue();
}
