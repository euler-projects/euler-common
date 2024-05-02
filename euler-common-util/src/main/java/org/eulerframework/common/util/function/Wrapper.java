package org.eulerframework.common.util.function;

public class Wrapper<T> {
    private final T value;

    public Wrapper(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}
