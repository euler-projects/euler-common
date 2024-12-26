package org.eulerframework.common.util.pipeline;

public interface Pipeline<T> {
    boolean submit(T data);
}
